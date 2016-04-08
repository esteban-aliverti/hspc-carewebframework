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

import org.carewebframework.api.spring.SpringUtil;

import org.zkoss.zul.Iframe;

import org.hspconsortium.cwf.api.smart.ISmartContextSubscriber;
import org.hspconsortium.cwf.api.smart.SmartContextBase.ContextMap;
import org.hspconsortium.cwf.api.smart.SmartContextRegistry;
import org.hspconsortium.cwf.api.smart.SmartContextService;
import org.hspconsortium.cwf.api.smart.SmartManifest;

/**
 * SMART Container Implementation
 */
public class SmartContainer extends Iframe implements ISmartContextSubscriber {
    
    
    private static final long serialVersionUID = 1L;
    
    protected final SmartManifest _manifest = new SmartManifest();
    
    protected final Map<String, ContextMap> _context = new HashMap<>();
    
    private final SmartContextRegistry contextRegistry;
    
    private boolean _active;
    
    public SmartContainer() {
        contextRegistry = SpringUtil.getBean("smartContextRegistry", SmartContextRegistry.class);
    }
    
    /**
     * Sets the manifest.
     * 
     * @param manifest The SMART manifest.
     */
    public void setManifest(SmartManifest manifest) {
        _manifest.init(manifest);
        registerScope(_manifest);
        subscribe("user");
    }
    
    /**
     * Registers this container as a subscriber to the context scope declared in the manifest.
     * 
     * @param manifest The SMART manifest.
     */
    private void registerScope(SmartManifest manifest) {
        String scope = manifest == null ? null : manifest.getValue("scope");
        
        if (scope != null) {
            subscribe(scope);
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
        return SmartContextService.getInstance().getUrl(_manifest, _context.values());
    }
    
    /**
     * Attaches this subscriber to a SMART context scope.
     * 
     * @param contextScope The name of the SMART context scope.
     */
    public void subscribe(String contextScope) {
        contextRegistry.get(contextScope).subscribe(this);
    }
    
    /**
     * Detaches this subscriber from a SMART context scope.
     * 
     * @param contextScope The name of the SMART context scope.
     */
    public void unsubscribe(String contextScope) {
        contextRegistry.get(contextScope).unsubscribe(this);
    }
    
}
