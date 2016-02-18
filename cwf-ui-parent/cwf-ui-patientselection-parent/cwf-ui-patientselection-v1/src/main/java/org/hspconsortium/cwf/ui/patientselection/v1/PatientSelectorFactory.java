/*
 * #%L
 * cwf-ui-patientselection-v1
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
package org.hspconsortium.cwf.ui.patientselection.v1;

import ca.uhn.fhir.model.dstu2.resource.Patient;

import org.hspconsortium.cwf.ui.patientselection.Constants;
import org.hspconsortium.cwf.ui.patientselection.IPatientSelector;
import org.hspconsortium.cwf.ui.patientselection.PatientSelectorFactoryBase;
import org.carewebframework.ui.zk.PopupDialog;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Window;

/**
 * This is the patient selection factory.
 */
public class PatientSelectorFactory extends PatientSelectorFactoryBase {
    
    public static class PatientSelector implements IPatientSelector {
        
        private final Window dlg = PopupDialog.popup(ZKUtil.getResourcePath(PatientSelectorFactory.class)
                + "patientSelection.zul", false, true, false);
        
        @Override
        public Patient select() {
            Events.sendEvent("onShow", dlg, null);
            return (Patient) dlg.getAttribute(Constants.SELECTED_PATIENT_ATTRIB);
        }
    };
    
    protected PatientSelectorFactory() {
        super("New patient selector", PatientSelector.class);
    }
}
