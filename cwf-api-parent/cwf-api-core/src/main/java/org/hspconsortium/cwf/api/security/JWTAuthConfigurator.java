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

import org.springframework.beans.factory.annotation.Value;

/**
 * JWT authentication configurator.
 */
public class JWTAuthConfigurator {
    
    
    @Value("${fhir.service.authentication.key.location:}")
    private String webKey;
    
    @Value("${fhir.service.authentication.key.issuer:careweb}")
    private String issuer;
    
    @Value("${fhir.service.authentication.subject:careweb}")
    private String subject;
    
    @Value("${fhir.service.authentication.token.duration:300}")
    private long duration;
    
    @Value("${fhir.service.authentication.token.provider:}")
    private String tokenProviderUrl;
    
    @Value("${fhir.service.authentication.audience:}")
    private String audience;
    
    public JWTAuthConfigurator() {
        super();
    }
    
    public String getWebKey() {
        return webKey;
    }
    
    public String getIssuer() {
        return issuer;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public String getTokenProviderUrl() {
        return tokenProviderUrl;
    }
    
    public String getAudience() {
        return audience;
    }
    
}
