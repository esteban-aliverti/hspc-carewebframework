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

import org.carewebframework.shell.layout.UIElementBase;
import org.carewebframework.shell.layout.UIElementPlugin;
import org.carewebframework.shell.plugins.PluginDefinition;
import org.carewebframework.ui.zk.ZKUtil;

import org.hspconsortium.cwf.api.smart.SmartManifest;

/**
 * This class is used for all SMART plugins to wrap a SMART container as a framework UI element.
 */
public class SmartPlugin extends UIElementPlugin {
    
    
    static {
        registerAllowedParentClass(SmartPlugin.class, UIElementBase.class);
    }
    
    private final SmartContainer smartContainer = new SmartContainer();
    
    /**
     * Sets the container as the wrapped component and registers itself to receive action
     * notifications from the container.
     */
    public SmartPlugin() {
        super();
        ZKUtil.updateStyle(getContainer(), "overflow", "hidden");
    }
    
    /**
     * Also passes the associated SMART manifest to the container.
     * 
     * @see org.carewebframework.shell.layout.UIElementBase#setDefinition(org.carewebframework.shell.plugins.PluginDefinition)
     */
    @Override
    public void setDefinition(PluginDefinition definition) {
        super.setDefinition(definition);
        SmartManifest manifest = definition.getResources(SmartResource.class).get(0).getManifest();
        fullSize(smartContainer);
        smartContainer.setParent(getContainer());
        smartContainer.setManifest(manifest);
    }
    
    /**
     * Returns the SMART container wrapped by this UI element.
     * 
     * @return The SMART container.
     */
    public SmartContainer getSmartContainer() {
        return smartContainer;
    }
    
    /**
     * Passes the activation request to the container.
     * 
     * @see org.carewebframework.shell.layout.UIElementBase#activateChildren(boolean)
     */
    @Override
    public void activateChildren(boolean active) {
        super.activateChildren(active);
        smartContainer.setActive(active);
    }
    
    /**
     * Passes the destroy event to the container.
     * 
     * @see org.carewebframework.shell.layout.UIElementBase#destroy()
     */
    @Override
    public void destroy() {
        smartContainer.destroy();
        super.destroy();
    }
    
}
