/*
 * #%L
 * cwf-api-core
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
package org.hspconsortium.cwf.api.security;

import java.io.IOException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import org.hspconsortium.cwf.fhir.client.FhirContext;
import org.hspconsortium.cwf.fhir.client.IAuthInterceptor;

import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import ca.uhn.fhir.rest.server.Constants;

/**
 * Abstract base class for implementing authentication interceptors. The BeanFactoryPostProcessor
 * interface ensures that the interceptors get registered early.
 */
public abstract class AbstractAuthInterceptor implements IAuthInterceptor, BeanFactoryPostProcessor {
    
    
    private final String authType;
    
    /**
     * Create the interceptor with the specified authorization type.
     * 
     * @param id The name to be used as an identifier.
     * @param authType The authorization type.
     */
    protected AbstractAuthInterceptor(String id, String authType) {
        this.authType = authType.trim();
        FhirContext.registerAuthInterceptor(id, this);
    }
    
    /**
     * Intercepts the request, adding the appropriate authorization header.
     */
    @Override
    public void interceptRequest(IHttpRequest theRequest) {
        String credentials = getCredentials();
        
        if (credentials != null && !credentials.isEmpty()) {
            theRequest.addHeader(Constants.HEADER_AUTHORIZATION, authType + " " + credentials);
        }
    }
    
    @Override
    public void interceptResponse(IHttpResponse theResponse) throws IOException {
        // nothing
    }
    
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // nothing
    }
    
}
