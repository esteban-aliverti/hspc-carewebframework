/*
 * #%L
 * cwf-ui-patientheader
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
package org.hspconsortium.cwf.ui.patientheader;

import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.BooleanDt;
import ca.uhn.fhir.model.primitive.DateDt;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.ui.patientselection.PatientSelection;
import org.carewebframework.common.DateUtil;
import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.carewebframework.ui.FrameworkController;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;

/**
 * Controller for patient header plugin.
 */
public class PatientHeader extends FrameworkController implements PatientContext.IPatientContextEvent {
    
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(PatientHeader.class);
    
    private Label patientHeader;
    
    private String noSelectionMessage;
    
    private Component root;
    
    public void onClick$select() {
        PatientSelection.show();
    }
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        root = comp;
        noSelectionMessage = patientHeader.getValue();
        committed();
    }
    
    @Override
    public void canceled() {
    }
    
    @Override
    public void committed() {
        Patient patient = PatientContext.getActivePatient();
        
        if (log.isDebugEnabled()) {
            log.debug("patient: " + patient);
        }
        
        String text = "";
        
        if (patient == null) {
            text = noSelectionMessage;
        } else {
            StringBuilder sb = new StringBuilder(FhirUtil.formatName(patient.getName()));
            String mrn = FhirUtil.getMRNString(patient);
            sb.append("  #").append(StringUtils.isEmpty(mrn) ? "Unknown" : mrn);
            
            if (!patient.getManagingOrganization().getDisplay().isEmpty()) {
                sb.append("@").append(patient.getManagingOrganization().getDisplay());
            }
            
            if (!patient.getGender().isEmpty()) {
                sb.append("   (").append(patient.getGender()).append(")");
            }
            
            sb.append("  Age: ").append(DateUtil.formatAge(patient.getBirthDate()));
            
            if (patient.getDeceased() != null) {
                DateDt dod = FhirUtil.getTyped(patient.getDeceased(), DateDt.class);
                
                if (dod != null) {
                    sb.append("  Died: ").append(DateUtil.formatDate(dod.getValue()));
                } else {
                    BooleanDt isDead = FhirUtil.getTyped(patient.getDeceased(), BooleanDt.class);
                    
                    if (isDead != null && isDead.getValue()) {
                        sb.append("  (deceased)");
                    }
                }
            }
            
            text = sb.toString();
        }
        
        patientHeader.setValue(text);
        Clients.resize(root);
    }
    
    @Override
    public String pending(boolean silent) {
        return null;
    }
    
}
