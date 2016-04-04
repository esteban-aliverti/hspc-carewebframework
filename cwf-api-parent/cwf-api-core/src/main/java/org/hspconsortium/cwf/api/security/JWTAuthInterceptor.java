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
import java.util.Arrays;
import java.util.UUID;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;

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
    
    
    private AccessToken accessToken;
    
    private final JWTAuthConfigurator config;
    
    private final JWTCredentials jwtCredentials;
    
    private final Scopes requestedScopes;
    
    private final AccessTokenProvider<?> tokenProvider;
    
    public JWTAuthInterceptor(String id, FhirContext fhirContext, JWTAuthConfigurator config) throws Exception {
        super(id, "Bearer");
        this.config = config;
        
        tokenProvider = new JsonAccessTokenProvider(fhirContext);
        requestedScopes = new Scopes();
        
        for (String scope : Arrays.asList(config.getRequestedScopes().split("\\,"))) {
            scope = scope.trim();
            
            if (!scope.isEmpty()) {
                requestedScopes.add(new SimpleScope(scope));
            }
        }
        
        // RSA signatures require a public and private RSA key pair, the public key
        // must be made known to the JWS recipient in order to verify the signatures
        URL url = getClass().getResource(config.getWebKey());
        JWKSet jwks = JWKSet.load(url);
        
        RSAKey rsaKey = (RSAKey) jwks.getKeys().get(0);
        jwtCredentials = new JWTCredentials(rsaKey.toRSAPrivateKey());
        jwtCredentials.setIssuer(config.getIssuer());
        jwtCredentials.setSubject(config.getSubject());
        jwtCredentials.setAudience(config.getAudience().isEmpty() ? config.getTokenProviderUrl() : config.getAudience());
        jwtCredentials.setDuration(config.getDuration());
    }
    
    @Override
    public synchronized String getCredentials() {
        if (accessToken == null || accessToken.isExpired()) {
            accessToken = getAccessToken();
        }
        
        return accessToken.getValue();
    }
    
    private AccessToken getAccessToken() {
        jwtCredentials.setTokenReference(UUID.randomUUID().toString());
        ClientCredentialsAccessTokenRequest<JWTCredentials> tokenRequest = new ClientCredentialsAccessTokenRequest<>(
                config.getIssuer(), jwtCredentials, requestedScopes);
        
        return tokenProvider.getAccessToken(config.getTokenProviderUrl(), tokenRequest);
    }
    
}
