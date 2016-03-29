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

import java.util.Date;

import com.cogmedsys.hsp.api.ucs.MessageProperty;

import org.socraticgrid.hl7.services.uc.model.Message;
import org.socraticgrid.hl7.services.uc.model.MessageBody;

public class MessageWrapper implements IMessageWrapper<Message> {
    
    
    private final Message message;
    
    public MessageWrapper(Message message) {
        this.message = message;
    }
    
    @Override
    public boolean hasPatient() {
        return getPatientId() != null;
    }
    
    @Override
    public String getPatientName() {
        // TODO How to get name?
        return getPatientId();
    }
    
    @Override
    public String getPatientId() {
        return getParam(MessageProperty.MESSAGE_ABOUT.name());
    }
    
    @Override
    public String getSubject() {
        return message.getHeader().getSubject();
    }
    
    @Override
    public Date getDeliveryDate() {
        return message.getHeader().getLastModified();
    }
    
    @Override
    public String getDisplayText() {
        return message.getHeader().getSubject();
    }
    
    @Override
    public boolean isActionable() {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public Urgency getUrgency() {
        String urgency = getParam("urgency");
        return urgency != null ? Urgency.fromString(urgency) : Urgency.LOW;
    }
    
    @Override
    public boolean canDelete() {
        return false;
    }
    
    @Override
    public Message getMessage() {
        return message;
    }
    
    @Override
    public String getAlertId() {
        return null;
    }
    
    @Override
    public String getParam(String param) {
        return message.getHeader().getProperties().getProperty(param);
    }
    
    @Override
    public String getType() {
        return null;
    }
    
    @Override
    public String getBody() {
        StringBuilder sb = new StringBuilder();
        
        for (MessageBody body : message.getParts()) {
            sb.append(body.getContent());
        }
        
        return sb.toString();
    }
    
    @Override
    public String getId() {
        return message.getHeader().getMessageId();
    }
    
}
