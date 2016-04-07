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

import org.carewebframework.shell.CareWebShell;
import org.carewebframework.shell.layout.UIElementBase;
import org.carewebframework.shell.plugins.IPluginResource;

import org.hspconsortium.cwf.api.smart.SmartManifest;

/**
 * A plugin resource that encapsulates the SMART manifest. This allows the SMART manifest to be
 * associated with a plugin definition.
 */
public class SmartResource implements IPluginResource {
    
    
    private final SmartManifest manifest;
    
    protected SmartResource(SmartManifest manifest) {
        super();
        this.manifest = manifest;
    }
    
    public SmartManifest getManifest() {
        return manifest;
    }
    
    /**
     * Registers/unregisters a SMART manifest resource.
     * 
     * @param shell The running shell.
     * @param owner Owner of the resource.
     * @param register If true, register the resource. If false, unregister it.
     */
    @Override
    public void register(CareWebShell shell, UIElementBase owner, boolean register) {
    }
    
}
