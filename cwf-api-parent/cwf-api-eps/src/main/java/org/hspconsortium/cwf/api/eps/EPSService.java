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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.ws.BindingProvider;

import org.carewebframework.common.MiscUtil;

import org.socraticgrid.hl7.services.eps.accessclients.broker.BrokerServiceSE;
import org.socraticgrid.hl7.services.eps.accessclients.publication.PublicationServiceSE;
import org.socraticgrid.hl7.services.eps.accessclients.subscription.SubscriptionServiceSE;
import org.socraticgrid.hl7.services.eps.interfaces.BrokerIFace;
import org.socraticgrid.hl7.services.eps.interfaces.PublicationIFace;
import org.socraticgrid.hl7.services.eps.interfaces.SubscriptionIFace;
import org.socraticgrid.hl7.services.eps.model.Message;
import org.socraticgrid.hl7.services.eps.model.MessageBody;
import org.socraticgrid.hl7.services.eps.model.MessageHeader;
import org.socraticgrid.hl7.services.eps.model.User;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IResource;

/**
 *
 */
public class EPSService {
    
    
    private final FhirContext fhirContext;
    
    private String serviceEndpoint;
    
    private User publisher;
    
    private PublicationIFace publisherPort;
    
    private SubscriptionIFace subscriberPort;
    
    private BrokerIFace brokerPort;
    
    public EPSService(FhirContext fhirContext, String serviceEndpoint) {
        this.fhirContext = fhirContext;
        this.serviceEndpoint = serviceEndpoint;
    }
    
    public void init() {
        if (!serviceEndpoint.endsWith("/")) {
            serviceEndpoint += "/";
        }
        
        PublicationServiceSE ps = new PublicationServiceSE();
        publisherPort = ps.getPublicationPort();
        ((BindingProvider) publisherPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
            serviceEndpoint + "publication");
        
        SubscriptionServiceSE ss = new SubscriptionServiceSE();
        subscriberPort = ss.getSubscriptionPort();
        ((BindingProvider) subscriberPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
            serviceEndpoint + "subscription");
        
        BrokerServiceSE bs = new BrokerServiceSE();
        brokerPort = bs.getBrokerPort();
        ((BindingProvider) brokerPort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
            serviceEndpoint + "broker");
    }
    
    public void destroy() {
        
    }
    
    public FhirContext getFhirContext() {
        return fhirContext;
    }
    
    public PublicationIFace getPublisherPort() {
        return publisherPort;
    }
    
    public SubscriptionIFace getSubscriberPort() {
        return subscriberPort;
    }
    
    public BrokerIFace getBrokerPort() {
        return brokerPort;
    }
    
    public User getPublisher() {
        return publisher;
    }
    
    /**
     * Set a publisher to use for all publications from this instance, Should not be changed when
     * the instance is used in a multi-threaded manner.
     * 
     * @param publisher Publisher of the event.
     */
    public void setPublisher(User publisher) {
        this.publisher = publisher;
    }
    
    /**
     * Raw event publication
     * 
     * @param topic Topic for publication.
     * @param event The event to be published.
     * @return The event id.
     */
    public String publishEvent(String topic, Message event) {
        if (event.getTopics().indexOf(topic) == -1) {
            event.getTopics().add(topic);
        }
        
        try {
            return publisherPort.publishEvent(topic, event);
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }
    
    /**
     * Raw event publication driven by topics declared in the message
     * 
     * @param event The event to be published.
     * @return A map of event id's keyed by topic.
     */
    public Map<String, String> publishEvent(Message event) {
        Map<String, String> msgIds = new HashMap<>();
        
        for (String topic : event.getTopics()) {
            msgIds.put(topic, publishEvent(topic, event));
        }
        
        return msgIds;
    }
    
    /**
     * Compose and publish an event on a topic
     * 
     * @param topic Topic for publication.
     * @param data Data to be published.
     * @param contentType The content type of the data.
     * @param subject Event subject.
     * @param title Event title.
     * @return The event id.
     */
    public String publishEvent(String topic, String data, String contentType, String subject, String title) {
        Date now = new Date();
        
        Message event = new Message();
        
        MessageHeader header = event.getHeader();
        header.setMessageId(UUID.randomUUID().toString()); // Not sure we need to generate an Id
        header.setSubject(subject);
        header.setMessageCreatedTime(now);
        header.setMessagePublicationTime(now);
        header.setPublisher(publisher);
        event.setTitle(title);
        
        MessageBody body = new MessageBody();
        body.setType(contentType);
        body.setBody(data);
        event.getMessageBodies().add(body);
        
        return publishEvent(topic, event);
    }
    
    /**
     * Publish a FHIR resource to a topic (as JSON) using a default subject & title
     * 
     * @param topic Topic for publication.
     * @param resource The FHIR resource to be published.
     * @return The event id.
     */
    public String publishResourceToTopic(String topic, IResource resource) {
        return publishResourceToTopic(topic, resource, "FHIR Resource", resource.getResourceName());
    }
    
    /**
     * Publish a FHIR resource to a topic, proving a title and subject
     * 
     * @param topic Topic for publication.
     * @param resource The FHIR resource to be published.
     * @param subject Event subject.
     * @param title Event title.
     * @return The event id.
     */
    public String publishResourceToTopic(String topic, IResource resource, String subject, String title) {
        String data = fhirContext.newJsonParser().encodeResourceToString(resource);
        return publishEvent(topic, data, "application/json", subject, title);
    }
    
}
