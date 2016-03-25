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

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import org.carewebframework.api.domain.IUser;
import org.carewebframework.api.security.SecurityUtil;
import org.carewebframework.common.MiscUtil;

/**
 * HTTP interceptor supporting Basic authentication.
 */
public class BasicAuthInterceptor extends AbstractAuthInterceptor {
    
    
    private final String credentials;
    
    public BasicAuthInterceptor(String id, String username, String password) {
        super(id, "Basic");
        username = StringUtils.trimToNull(username);
        password = StringUtils.trimToEmpty(password);
        this.credentials = username == null ? null : encode(username, password);
    }
    
    @Override
    public String getCredentials() {
        if (credentials != null) {
            return credentials;
        }
        
        IUser user = SecurityUtil.getAuthenticatedUser();
        return user == null ? null : encode(user.getLoginName(), user.getPassword());
    }
    
    private String encode(String username, String password) {
        try {
            String credentials = username + ":" + password;
            return Base64.encodeBase64String(credentials.getBytes("ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            throw MiscUtil.toUnchecked(e);
        }
    }
}
