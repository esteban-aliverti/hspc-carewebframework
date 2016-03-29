/*
 * #%L
 * UCS Messaging API
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
package org.hspconsortium.cwf.api.ucs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.socraticgrid.hl7.services.uc.interfaces.AlertingIntf;
import org.socraticgrid.hl7.services.uc.interfaces.UCSAlertingIntf;
import org.socraticgrid.hl7.services.uc.interfaces.UCSClientIntf;
import org.socraticgrid.hl7.services.uc.model.AlertMessage;
import org.socraticgrid.hl7.services.uc.model.AlertStatus;
import org.socraticgrid.hl7.services.uc.model.Message;
import org.socraticgrid.hl7.services.uc.model.MessageModel;
import org.socraticgrid.hl7.services.uc.model.Recipient;
import org.socraticgrid.hl7.services.uc.model.UserContactInfo;
import org.socraticgrid.hl7.ucs.nifi.api.ClientImpl;
import org.socraticgrid.hl7.ucs.nifi.api.UCSNiFiSession;
import org.socraticgrid.hl7.ucs.nifi.common.model.MessageWrapper;
import org.socraticgrid.hl7.ucs.nifi.common.util.AlertMessageBuilder;

/**
 * Wrapper for UCS message services.
 */
public class MessageService {
    
    
    private static final Log log = LogFactory.getLog(MessageService.class);
    
    /**
     * The UCS session associated with the service. This UCS session will be shared by all servlet
     * sessions.
     */
    private UCSNiFiSession session;
    
    /**
     * The UCS client associated with this session. This property MUST ALWAYS be accessed via it
     * getter.
     */
    private volatile ClientImpl client;
    
    /**
     * Alert message listeners registered against the UCS session.
     */
    private final AlertingBroadcaster alertBroadcaster;
    
    /**
     * Message listeners registered against the UCS session
     */
    private final MessageBroadcaster messageBroadcaster;
    
    /**
     * Reference to configuration class for UCS session configuration.
     */
    private final MessageServiceConfigurator config;
    
    public MessageService(MessageServiceConfigurator config) {
        this.config = config;
        alertBroadcaster = new AlertingBroadcaster();
        messageBroadcaster = new MessageBroadcaster();
    }
    
    /**
     * Adds a new alert Listener to this UCS session
     * 
     * @param alertListener Alert listener to add.
     */
    public void addAlertListener(UCSAlertingIntf alertListener) {
        this.alertBroadcaster.registerListener(alertListener);
    }
    
    /**
     * Removes an alert Listener from this UCS session
     * 
     * @param alertListener Alert listener to remove.
     */
    public void removeAlertListener(UCSAlertingIntf alertListener) {
        this.alertBroadcaster.unregisterListener(alertListener);
    }
    
    /**
     * Adds a new message listener to this UCS session.
     * 
     * @param messageListener Message listener to add.
     */
    public void addMessageListener(UCSClientIntf messageListener) {
        this.messageBroadcaster.registerListener(messageListener);
    }
    
    /**
     * Removes a message listener from this UCS session.
     * 
     * @param messageListener Message listener to remove.
     */
    public void removeMessageListener(UCSClientIntf messageListener) {
        this.messageBroadcaster.unregisterListener(messageListener);
    }
    
    /**
     * Returns the UCS session associated with this service. Should not be called outside the
     * service framework.
     * 
     * @return The UCS session.
     */
    protected UCSNiFiSession getSession() {
        return session;
    }
    
    /**
     * Returns the UCS client associated with the UCS session or creates a new one if there is none.
     * Should not be called outside the service framework. The idea behind this getter is to delay
     * the connection to UCS until is required.
     * 
     * @return The client.
     */
    protected ClientImpl getClient() {
        return client == null ? initClient() : client;
    }
    
    /**
     * Initialize the client in a thread-safe way.
     * 
     * @return The newly initialized client.
     */
    private synchronized ClientImpl initClient() {
        if (client == null) {
            client = (ClientImpl) session.getNewClient();
        }
        
        return client;
    }
    
    /**
     * Creates a new session after disposing of an existing session.
     */
    public void init() {
        try {
            destroy();
            
            String prefixedNifiHost = "http://" + config.getNifiHost();
            
            String alertingCommandURL = prefixedNifiHost + ":" + config.getNifiAlertingCommandPort() + "/contentListener";
            String clientCommandURL = prefixedNifiHost + ":" + config.getNifiClientCommandPort() + "/contentListener";
            
            // @formatter:off
            session = new UCSNiFiSession.UCSNiFiSessionBuilder()
                    .withAlertingCommandURL(alertingCommandURL)
                    .withClientCommandURL(clientCommandURL)
                    .withUCSClientHost(config.getClientHost())
                    .withUCSClientPort(config.getClientPort())
                    .withUCSAlertingHost(config.getClientHost())
                    .withUCSAlertingPort(config.getAlertingPort())
                    .withManagementHost(config.getClientHost())
                    .withManagementPort(config.getManagementPort())
                    .withConversationHost(config.getClientHost())
                    .withConversationPort(config.getConversationPort())
                    .withUCSClientListener(messageBroadcaster)
                    .withUCSAlertingListener(alertBroadcaster)
                    .build();//TODO Create singleton session with broadcast
            // @formatter:on
        } catch (Exception e) {
            log.error("Error initializing UCS Session", e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * If a session is currently active, method disposes of session.
     */
    public void destroy() {
        try {
            if (session != null) {
                session.dispose();
            }
        } catch (Exception e) {
            log.error("Error disposing of UCS Session", e);
        } finally {
            session = null;
        }
    }
    
    public void sendMessage(Message message) {
        try {
            getClient().sendMessage(new MessageModel<Message>(message));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Retracts multiple messages.
     * 
     * @param messagesToRetract List of message to retract.
     */
    public void retractMessages(List<Message> messagesToRetract) {
        if (messagesToRetract != null) {
            for (Message message : messagesToRetract) {
                retractMessage(message);
            }
        }
    }
    
    /**
     * Retracts a single message
     * 
     * @param messageToRetract Message to retract.
     */
    public void retractMessage(Message messageToRetract) {
        if (messageToRetract != null) {
            try {
                getClient().cancelMessage(messageToRetract.getHeader().getMessageId(), true);
            } catch (Exception e) {
                log.error("An error occurred retracting message", e);
            }
        }
    }
    
    /**
     * Returns all messages in the UCS broker. Use this call with caution. Use filtered variant
     * instead. TODO Add common filtered variant invocations.
     * 
     * @return Return all messages from from server.
     */
    public List<Message> getAllMessages() {
        try {
            return getClient().listMessages();
        } catch (Exception e) {
            log.error("Error retrieving messages", e);
            throw new RuntimeException("Error retrieving messages", e);
        }
    }
    
    public List<Message> getMessagesByRecipient(String recipientId) {
        List<Message> result = new ArrayList<>();
        
        for (Message message : getAllMessages()) {
            Set<Recipient> recipients = message.getHeader().getRecipientsList();
            
            for (Recipient myRecipient : recipients) {
                String addressee = myRecipient.getDeliveryAddress().getPhysicalAddress().getAddress();//TODO Does not seem right to me.
                
                if (addressee.equals(recipientId)) {
                    result.add(message);
                }
            }
        }
        return result;
    }
    
    public List<Message> getMessagesByPatient(String about) {
        List<Message> result = new ArrayList<>();
        
        for (Message message : getAllMessages()) {
            Properties props = message.getHeader().getProperties();
            
            String messageAbout = props.getProperty(MessageProperty.MESSAGE_ABOUT.toString());
            
            if (messageAbout != null && messageAbout.equals(about)) {
                result.add(message);
            }
        }
        return result;
    }
    
    public Message getMessageWithId(String messageId) {
        try {
            List<Message> messages = getAllMessages();
            
            for (Message message : messages) {
                String msgId = message.getHeader().getMessageId();
                
                if (msgId.equals(messageId)) {
                    return message;
                }
            }
            
            return null;
        } catch (Exception e) {
            log.error("Error retrieving message with ID " + messageId);
            throw new RuntimeException("Error retrieving message with ID " + messageId, e);
        }
    }
    
    /**
     * Acknowledges a list of messages.
     * 
     * @param messageIds Unique ids of messages to acknowledge.
     */
    public void acknowledgeMessages(List<String> messageIds) {
        AlertingIntf alertingIntf = session.getNewAlerting();
        
        for (String messageId : messageIds) {
            acknowledgeMessage(messageId, alertingIntf);
        }
    }
    
    /**
     * Acknowledges a single message
     * 
     * @param messageId The unique id of the message to acknowledge.
     */
    public void acknowledgeMessage(String messageId) {
        acknowledgeMessage(messageId, null);
    }
    
    /**
     * Acknowledges a message.
     * 
     * @param messageId The unique id of the message to acknowledge.
     * @param alertingIntf The alert handler.
     */
    protected void acknowledgeMessage(String messageId, AlertingIntf alertingIntf) {
        try {
            if (alertingIntf == null) {
                alertingIntf = session.getNewAlerting();
            }
            
            AlertStatus status = AlertStatus.Acknowledged;
            
            //Use AlertMessageBuilder from org.socraticgrid.hl7:ucs-nifi-common to create a new message
            MessageWrapper messageWrapper = new AlertMessageBuilder().withStatus(status).withMessageId(messageId)
                    .buildMessageWrapper();
            
            //invoke updateAlert()
            alertingIntf.updateAlert((AlertMessage) messageWrapper.getMessage());
        } catch (Exception e) {
            log.error("Unable to acknowledge message with message id: " + messageId, e);
        }
    }
    
    public void forwardMessage(Message message, Set<UserContactInfo> recipients, String comment) {
        // TODO Auto-generated method stub
        
    }
    
    public List<UserContactInfo> queryUsers(String query) {
        ClientImpl client = getClient();
        List<UserContactInfo> result = new ArrayList<>();
        
        try {
            for (String id : client.queryUsers(query)) {
                try {
                    result.add(client.retrieveUser(id));
                } catch (Exception e) {
                    log.error("Error retrieving contact info for id: " + id, e);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return result;
    }
    
    public Collection<ScheduledMessage> getScheduledMessages() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void deleteScheduledMessage(ScheduledMessage selected) {
        // TODO Auto-generated method stub
        
    }
    
    public Iterable<ScheduledMessage> getScheduledNotificationMessage(ScheduledMessage message) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public List<UserContactInfo> getScheduledNotificationRecipients(ScheduledMessage message) {
        // TODO Auto-generated method stub
        return null;
        
    }
    
    public boolean scheduleNotification(ScheduledMessage message, List<String> body, List<UserContactInfo> recipients) {
        // TODO Auto-generated method stub
        return false;
    }
}
