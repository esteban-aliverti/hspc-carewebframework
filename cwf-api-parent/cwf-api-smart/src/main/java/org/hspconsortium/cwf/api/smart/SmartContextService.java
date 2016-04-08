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

import java.util.Collection;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import org.carewebframework.common.QueryStringBuilder;

import org.springframework.beans.factory.annotation.Value;

import org.hspconsortium.cwf.api.smart.SmartContextBase.ContextMap;

/**
 * Provides full launch url for SMART apps.
 */
public class SmartContextService {
    
    
    private static final SmartContextService instance = new SmartContextService();
    
    @Value("${smart.service.root.url:}")
    private String smartServiceRoot;
    
    @Value("${fhir.service.root.url:}")
    private String fhirServiceRoot;
    
    private String serviceRoot;
    
    public static SmartContextService getInstance() {
        return instance;
    }
    
    private SmartContextService() {
    }
    
    public String getUrl(SmartManifest manifest, Collection<ContextMap> contexts) {
        if (contexts == null || contexts.isEmpty()) {
            return null;
        }
        
        String qs = getQueryString(contexts);
        return qs.isEmpty() ? null : manifest.getValue("launch_uri") + "?" + qs;
    }
    
    /**
     * Return query string for the SMART plugin.
     * 
     * @return The query string.
     */
    private String getQueryString(Collection<ContextMap> contexts) {
        QueryStringBuilder qs = new QueryStringBuilder();
        
        for (ContextMap context : contexts) {
            for (Entry<String, String> entry : context.entrySet()) {
                qs.append(entry.getKey(), entry.getValue());
            }
        }
        
        if (qs.length() > 0) {
            qs.append("fhirServiceUrl", getServiceRoot());
        }
        
        return qs.toString();
    }
    
    public void init() {
        getServiceRoot();
    }
    
    private String getServiceRoot() {
        if (serviceRoot == null) {
            serviceRoot = StringUtils.isEmpty(smartServiceRoot) ? fhirServiceRoot : smartServiceRoot;
            serviceRoot = StringUtils.chomp(serviceRoot, "/");
            
            if (StringUtils.isEmpty(serviceRoot)) {
                throw new IllegalArgumentException("No service root url defined for SMART.");
            }
        }
        
        return serviceRoot;
    }
    
}
