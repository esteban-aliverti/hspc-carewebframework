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

import org.springframework.beans.factory.annotation.Value;

public class MessageServiceConfigurator {
    
    
    @Value("${org.socraticgrid.hl7.ucs.nifiHost}")
    private String nifiHost;
    
    @Value("${org.socraticgrid.hl7.ucs.clientHost}")
    private String clientHost;
    
    @Value("${org.socraticgrid.hl7.ucs.nifiClientCommandPort}")
    private int nifiClientCommandPort;
    
    @Value("${org.socraticgrid.hl7.ucs.nifiAlertingCommandPort}")
    private int nifiAlertingCommandPort;
    
    @Value("${org.socraticgrid.hl7.ucs.clientPort}")
    private int clientPort;
    
    @Value("${org.socraticgrid.hl7.ucs.alertingPort}")
    private int alertingPort;
    
    @Value("${org.socraticgrid.hl7.ucs.managementPort}")
    private int managementPort;
    
    @Value("${org.socraticgrid.hl7.ucs.conversationPort}")
    private int conversationPort;
    
    public MessageServiceConfigurator() {
    }
    
    public String getNifiHost() {
        return nifiHost;
    }
    
    public int getNifiClientCommandPort() {
        return nifiClientCommandPort;
    }
    
    public int getNifiAlertingCommandPort() {
        return nifiAlertingCommandPort;
    }
    
    public String getClientHost() {
        return clientHost;
    }
    
    public int getClientPort() {
        return clientPort;
    }
    
    public int getAlertingPort() {
        return alertingPort;
    }
    
    public int getManagementPort() {
        return managementPort;
    }
    
    public int getConversationPort() {
        return conversationPort;
    }
    
}
