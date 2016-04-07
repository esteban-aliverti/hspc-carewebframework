/*
 * #%L
 * cwf-ui-smart
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
package org.hspconsortium.cwf.ui.smart;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import org.carewebframework.api.spring.SpringUtil;
import org.carewebframework.common.QueryStringBuilder;

import org.zkoss.zul.Iframe;

import org.hspconsortium.cwf.api.smart.ISmartContextSubscriber;
import org.hspconsortium.cwf.api.smart.SmartContextBase.ContextMap;
import org.hspconsortium.cwf.api.smart.SmartManifest;
import org.hspconsortium.cwf.api.smart.SmartUtil;

/**
 * SMART Container Implementation
 */
public class SmartContainer extends Iframe implements ISmartContextSubscriber {
    
    
    private static final long serialVersionUID = 1L;
    
    protected final SmartManifest _manifest = new SmartManifest();
    
    protected final Map<String, ContextMap> _context = new HashMap<>();
    
    private boolean _active;
    
    private final String rootUrl;
    
    private final String launchId = UUID.randomUUID().toString();
    
    public SmartContainer() {
        String url = SpringUtil.getProperty("smart.service.root.url");
        
        if (url == null) {
            url = SpringUtil.getProperty("fhir.service.root.url");
        }
        
        if (url == null) {
            throw new IllegalArgumentException("No service root url defined for SMART.");
        }
        
        rootUrl = StringUtils.chomp(url, "/");
    }
    
    /**
     * Sets the manifest.
     * 
     * @param manifest The SMART manifest.
     */
    public void setManifest(SmartManifest manifest) {
        _manifest.init(manifest);
        registerScope(_manifest);
    }
    
    /**
     * Registers this container as a subscriber to the context scope declared in the manifest.
     * 
     * @param manifest The SMART manifest.
     */
    private void registerScope(SmartManifest manifest) {
        String scope = manifest == null ? null : manifest.getValue("scope");
        
        if (scope != null) {
            SmartUtil.subscribe(scope, this);
        }
    }
    
    /**
     * Sets the context.
     * 
     * @param contextScope The name of the SMART context scope.
     * @param context The updated SMART context.
     */
    private void setContext(String contextScope, ContextMap context) {
        _context.remove(contextScope);
        
        if (context != null && !context.isEmpty()) {
            _context.put(contextScope, context);
        }
    }
    
    /**
     * Returns the container's activation state.
     * 
     * @return True if the container is active.
     */
    public boolean isActive() {
        return _active;
    }
    
    /**
     * Sets the container's activation state. The updated state is passed to the client.
     * 
     * @param active The activation state.
     */
    public void setActive(boolean active) {
        _active = active;
    }
    
    /**
     * ISmartContextSubscriber.updateContext is called by the associated SMART context to notify
     * this container of a change to the context.
     */
    @Override
    public void updateContext(String contextType, ContextMap context) {
        setContext(contextType, context);
        refresh();
    }
    
    /**
     * Override to implement custom logic on container destruction.
     */
    public void destroy() {
        
    }
    
    public void refresh() {
        invalidate();
        setSrc(getUrl());
    }
    
    /**
     * Returns the url for the SMART plugin.
     * 
     * @return SMART plugin url.
     */
    private String getUrl() {
        String qs = getQueryString();
        return qs.isEmpty() ? null : _manifest.getValue("launch_uri") + "?" + qs;
    }
    
    /**
     * Return query string for the SMART plugin.
     * 
     * @return The query string.
     */
    private String getQueryString() {
        QueryStringBuilder qs = new QueryStringBuilder();
        
        for (ContextMap context : _context.values()) {
            for (Entry<String, Object> entry : context.entrySet()) {
                qs.append(entry.getKey(), entry.getValue());
            }
        }
        
        if (qs.length() > 0) {
            qs.append("fhirServiceUrl", rootUrl);
            //qs.append("launch", launchId);
        }
        
        return qs.toString();
    }
}
