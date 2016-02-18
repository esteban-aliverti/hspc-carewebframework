/*
 * #%L
 * cwf-ui-patientselection-core
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
package org.hspconsortium.cwf.ui.patientselection;

import org.hspconsortium.cwf.api.patient.PatientContext;
import org.carewebframework.shell.CareWebUtil;
import org.carewebframework.shell.ICareWebStartup;
import org.carewebframework.ui.FrameworkWebSupport;
import org.carewebframework.ui.action.ActionRegistry;
import org.carewebframework.ui.action.IAction;
import org.carewebframework.ui.command.CommandUtil;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;

/**
 * Patient selection initializers.
 */
public class Init implements ICareWebStartup {
    
    private static final String ACTION_ID = "patientselection.select";
    
    private static final String ACTION_NAME = "@patientselection.action.select.label";
    
    private static final IAction PATIENT_SELECT = ActionRegistry.register(true, ACTION_ID, ACTION_NAME,
        "zscript:org.hspconsortium.cwf.ui.patientselection.PatientSelection.show();");
    
    private static final EventListener<Event> forceSelectionListener = new EventListener<Event>() {
        
        @Override
        public void onEvent(Event event) throws Exception {
            PatientSelection.show();
        }
        
    };
    
    @Override
    public boolean execute() {
        CommandUtil.associateCommand("PATIENT_SELECT", CareWebUtil.getShell(), PATIENT_SELECT);
        
        // call the patient selection routine at login, if the user preference is set
        if (PatientContext.getActivePatient() == null && PatientSelection.forcePatientSelection()) {
            Executions.schedule(FrameworkWebSupport.getDesktop(), forceSelectionListener, null);
        }
        
        return true;
    }
    
}
