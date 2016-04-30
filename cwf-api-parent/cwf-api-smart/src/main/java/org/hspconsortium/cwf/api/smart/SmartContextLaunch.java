/*
 * #%L
 * cwf-api-smart
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
package org.hspconsortium.cwf.api.smart;

import org.springframework.beans.factory.annotation.Value;

import org.hspconsortium.cwf.api.smart.SmartContextBase.ContextMap;

import ca.uhn.fhir.rest.client.IGenericClient;

/**
 * Default implementation for binding SMART context to launch id.
 */
public class SmartContextLaunch implements ISmartContextLaunch {
    
    
    private final IGenericClient fhirClient;
    
    @Value("${smart.service.launch.binder.url:}")
    private String smartLaunchBinder;
    
    public SmartContextLaunch(IGenericClient fhirClient) {
        this.fhirClient = fhirClient;
    }
    
    /**
     * Binds the context to a unique launch identifier.
     * 
     * @param contextMap The context map.
     * @return A unique launch identifier.
     */
    @Override
    public String bindContext(ContextMap contextMap) {
        return null;
    }
    
}
