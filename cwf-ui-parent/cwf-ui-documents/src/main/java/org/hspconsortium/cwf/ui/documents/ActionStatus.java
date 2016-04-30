/*
 * #%L
 * cwf-ui-documents
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
package org.hspconsortium.cwf.ui.documents;

import org.carewebframework.shell.plugins.PluginStatus;

import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.api.patient.PatientContext.IPatientContextEvent;

/**
 * Updates the enabled status of the plugin.
 *
 * @author dmartin
 */
public class ActionStatus extends PluginStatus implements IPatientContextEvent {
    
    
    /**
     * Returns true if there is no current patient or the current patient has no documents.
     */
    @Override
    public boolean checkDisabled() {
        return PatientContext.getActivePatient() == null;
    }
    
    /**
     * @see org.carewebframework.api.context.IContextEvent#canceled()
     */
    @Override
    public void canceled() {
    }
    
    /**
     * Update the plugin enabled status when the patient selection changes.
     */
    @Override
    public void committed() {
        updateDisabled();
    }
    
    /**
     * @see org.carewebframework.api.context.IContextEvent#pending(boolean)
     */
    @Override
    public String pending(boolean silent) {
        return null;
    }
    
}
