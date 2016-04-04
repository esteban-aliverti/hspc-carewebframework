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

import java.net.URL;
import java.util.UUID;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

import org.apache.commons.lang3.StringUtils;

import org.carewebframework.common.MiscUtil;

import org.hspconsortium.client.auth.Scopes;
import org.hspconsortium.client.auth.SimpleScope;
import org.hspconsortium.client.auth.access.AccessToken;
import org.hspconsortium.client.auth.access.AccessTokenProvider;
import org.hspconsortium.client.auth.access.JsonAccessTokenProvider;
import org.hspconsortium.client.auth.credentials.JWTCredentials;
import org.hspconsortium.client.session.clientcredentials.ClientCredentialsAccessTokenRequest;

import ca.uhn.fhir.context.FhirContext;

/**
 * Authentication interceptor supporting JWT authentication.
 */
public class JWTAuthInterceptor extends AbstractAuthInterceptor {
    
    
    private volatile AccessToken accessToken;
    
    private final FhirContext fhirContext;
    
    private final JWTAuthConfigurator config;
    
    public JWTAuthInterceptor(String id, FhirContext fhirContext, JWTAuthConfigurator config) {
        super(id, "Bearer");
        this.fhirContext = fhirContext;
        this.config = config;
    }
    
    @Override
    public String getCredentials() {
        if (accessToken == null || accessToken.isExpired()) {
            accessToken = getAccessToken();
        }
        
        return accessToken.getValue();
    }
    
    private AccessToken getAccessToken() {
        try {
            Scopes requestedScopes = new Scopes();
            requestedScopes.add(new SimpleScope("launch")).add(new SimpleScope("patient/*.read"));
            
            // RSA signatures require a public and private RSA key pair, the public key
            // must be made known to the JWS recipient in order to verify the signatures
            URL url = getClass().getResource(config.getWebKey());
            JWKSet jwks = JWKSet.load(url);
            
            RSAKey rsaKey = (RSAKey) jwks.getKeys().get(0);
            JWTCredentials jwtCredentials = new JWTCredentials(rsaKey.toRSAPrivateKey());
            jwtCredentials.setIssuer(config.getIssuer());
            jwtCredentials.setSubject(config.getSubject());
            jwtCredentials.setAudience(
                StringUtils.isEmpty(config.getAudience()) ? config.getTokenProviderUrl() : config.getAudience());
            jwtCredentials.setTokenReference(UUID.randomUUID().toString());
            jwtCredentials.setDuration(config.getDuration());
            
            ClientCredentialsAccessTokenRequest<JWTCredentials> tokenRequest = new ClientCredentialsAccessTokenRequest<>(
                    config.getIssuer(), jwtCredentials, requestedScopes);
            
            AccessTokenProvider<?> tokenProvider = new JsonAccessTokenProvider(fhirContext);
            return tokenProvider.getAccessToken(config.getTokenProviderUrl(), tokenRequest);
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }
    
}
