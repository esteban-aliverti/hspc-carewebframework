/*
 * #%L
 * EPS API
 * %%
 * Copyright (C) 2014 - 2016 Healthcare Services Platform Consortium
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.hspconsortium.cwf.api.eps;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.api.event.AbstractGlobalEventDispatcher;
import org.carewebframework.common.JSONUtil;
import org.carewebframework.common.MiscUtil;
import org.carewebframework.common.StrUtil;

import org.socraticgrid.hl7.services.eps.model.AccessModel;
import org.socraticgrid.hl7.services.eps.model.Durability;
import org.socraticgrid.hl7.services.eps.model.Message;
import org.socraticgrid.hl7.services.eps.model.Options;
import org.socraticgrid.hl7.services.eps.model.PullRange;
import org.socraticgrid.hl7.services.eps.model.SubscriptionType;

import ca.uhn.fhir.model.api.IResource;

public class GlobalEventDispatcher extends AbstractGlobalEventDispatcher {
    
    
    private class EventPoller extends Thread {
        
        
        private boolean terminate;
        
        /**
         * Wakes up the background thread.
         *
         * @return True if request was successful.
         */
        public synchronized boolean wakeup() {
            try {
                synchronized (monitor) {
                    monitor.notify();
                }
                return true;
            } catch (Throwable t) {
                return false;
            }
        }
        
        public void terminate() {
            terminate = true;
            wakeup();
        }
        
        @Override
        public void run() {
            synchronized (monitor) {
                while (!terminate) {
                    try {
                        pullEvents();
                        monitor.wait(pollingInterval);
                    } catch (InterruptedException e) {}
                }
            }
            
            log.debug("Event poller has exited.");
        }
        
    }
    
    private static final Log log = LogFactory.getLog(GlobalEventDispatcher.class);
    
    private final EPSService epsService;
    
    private final String subscriberId = UUID.randomUUID().toString();
    
    private final Map<String, String> subscriptions = new HashMap<>();
    
    private final int pollingInterval = 5000;
    
    private Date lastPoll = new Date();
    
    private final Object monitor = new Object();
    
    private final EventPoller eventPoller = new EventPoller();
    
    public GlobalEventDispatcher(EPSService epsService) {
        this.epsService = epsService;
    }
    
    @Override
    public void init() {
        super.init();
        eventPoller.start();
    }
    
    @Override
    public void destroy() {
        eventPoller.terminate();
        super.destroy();
    }
    
    @Override
    protected String getNodeId() {
        return subscriberId;
    }
    
    @Override
    public void subscribeRemoteEvent(String eventName, boolean subscribe) {
        String topic = StrUtil.piece(eventName, ".");
        
        if (topic.isEmpty() || subscriptions.containsKey(topic) == subscribe) {
            return;
        }
        
        List<String> topics = Collections.singletonList(topic);
        
        try {
            if (subscribe) {
                Options options = new Options();
                options.setAccess(AccessModel.Open);
                options.setDurability(Durability.Transient);
                String subscriptionId = epsService.getSubscriberPort().subscribe(topics, SubscriptionType.Pull, options,
                    null);
                subscriptions.put(topic, subscriptionId);
            } else {
                String subscriptionId = subscriptions.remove(topic);
                epsService.getSubscriberPort().unsubscribe(topics, subscriberId, subscriptionId);
            }
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }
    
    @Override
    public void fireRemoteEvent(String eventName, Serializable eventData, String recipients) {
        String topic = StrUtil.piece(eventName, ".");
        
        if (eventData instanceof IResource) {
            IResource resource = (IResource) eventData;
            epsService.publishResourceToTopic(topic, resource, eventName, "FHIR");
            return;
        }
        
        String contentType;
        String data;
        String encoding;
        
        if (eventData == null || eventData instanceof String) {
            encoding = "NONE";
            contentType = "text/plain";
            data = (String) eventData;
        } else {
            encoding = "JSON";
            contentType = "application/json";
            data = JSONUtil.serialize(eventData);
        }
        
        epsService.publishEvent(topic, data, contentType, eventName, encoding);
    }
    
    public void pullEvents() {
        PullRange pullRange = PullRange.Specific;
        Date start = lastPoll;
        Date end = new Date();
        lastPoll = end;
        
        for (String topic : subscriptions.keySet()) {
            try {
                List<Message> events = epsService.getSubscriberPort().retrieveEvents(topic, pullRange, start, end,
                    Collections.<String> emptyList());
                
                for (Message event : events) {
                    processMessage(event);
                }
            } catch (Exception e) {
                log.error("Exception while polling for events.", e);
            }
        }
    }
    
    /**
     * Process a dequeued message by forwarding it to the local event manager for local delivery. If
     * the message is a ping request, send the response.
     * 
     * @param message Message to process.
     */
    protected void processMessage(Message message) {
        try {
            String eventName = message.getHeader().getSubject();
            String encoding = message.getTitle();
            String body = message.getMessageBodies().get(0).getBody();
            Object eventData;
            
            if ("FHIR".equals(encoding)) {
                eventData = epsService.getFhirContext().newJsonParser().parseResource(body);
            } else if ("JSON".equals(encoding)) {
                eventData = JSONUtil.deserialize(body);
            } else {
                eventData = body;
            }
            
            localEventDelivery(eventName, eventData);
        } catch (Exception e) {
            log.error("Error during local dispatch of global event.", e);
        }
    }
    
}
