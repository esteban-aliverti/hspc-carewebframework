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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.api.domain.IUser;
import org.carewebframework.api.security.SecurityUtil;
import org.carewebframework.common.DateUtil;
import org.carewebframework.ui.FrameworkController;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobutton;
import org.zkoss.zul.Label;

import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.hspconsortium.cwf.ui.patientselection.PatientSelection;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.BooleanDt;
import ca.uhn.fhir.model.primitive.DateDt;

/**
 * Controller for patient header plugin.
 */
public class PatientHeader extends FrameworkController implements PatientContext.IPatientContextEvent {
    
    
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(PatientHeader.class);
    
    private Combobutton btnDetail;
    
    private Component pnlDetail;
    
    private Label lblName;
    
    private Label lblMRN;
    
    private Label lblGender;
    
    private Label lblDOBLabel;
    
    private Label lblDOB;
    
    private Label lblDODLabel;
    
    private Label lblDOD;
    
    private Component[] labels;
    
    private Label lblUser;
    
    private String noSelection;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        noSelection = lblName.getValue();
        labels = new Component[] { btnDetail, lblMRN, lblGender, lblDOBLabel, lblDOB, lblDODLabel, lblDOD };
        IUser user = SecurityUtil.getAuthenticatedUser();
        setLabel(lblUser, user.getFullName() + " @ " + user.getSecurityDomain().getName(), null);
        committed();
    }
    
    public void onClick$lnkSelect() {
        PatientSelection.show();
    }
    
    public void onOpen$btnDetail(OpenEvent event) {
        if (event.isOpen()) {
            buildDetail();
        }
    }
    
    @Override
    public void canceled() {
    }
    
    @Override
    public void committed() {
        hideLabels();
        Patient patient = PatientContext.getActivePatient();
        //ZKUtil.detachChildren(pnlDetail);
        
        if (log.isDebugEnabled()) {
            log.debug("patient: " + patient);
        }
        
        if (patient == null) {
            lblName.setValue(noSelection);
            return;
        }
        
        setLabel(lblName, FhirUtil.formatName(patient.getName()), null);
        String mrn = FhirUtil.getMRNString(patient);
        setLabel(lblMRN, mrn.isEmpty() ? null : "(" + mrn + ")", null);
        setLabel(lblDOB, formatDateAndAge(patient.getBirthDate()), lblDOBLabel);
        setLabel(lblDOD, formatDOD(patient.getDeceased()), lblDODLabel);
        setLabel(lblGender, patient.getGender(), null);
        Clients.resize(root);
    }
    
    @Override
    public String pending(boolean silent) {
        return null;
    }
    
    private String formatDOD(IDatatype value) {
        if (value == null) {
            return null;
        }
        
        DateDt dod = FhirUtil.getTyped(value, DateDt.class);
        
        if (dod != null) {
            return formatDateAndAge(dod.getValue());
        }
        
        BooleanDt isDead = FhirUtil.getTyped(value, BooleanDt.class);
        
        if (isDead != null && isDead.getValue()) {
            return "unknown";
        }
        
        return null;
    }
    
    private String formatDateAndAge(Date date) {
        return date == null ? null : DateUtil.formatDate(date) + " (" + DateUtil.formatAge(date) + ")";
    }
    
    private void setLabel(Label label, String value, Component associatedComponent) {
        label.setValue(value);
        label.setVisible(value != null && !value.isEmpty());
        
        if (associatedComponent != null) {
            associatedComponent.setVisible(label.isVisible());
        }
    }
    
    private void hideLabels() {
        for (Component label : labels) {
            label.setVisible(false);
        }
    }
    
    private void buildDetail() {
        if (pnlDetail.getFirstChild() != null) {
            return;
        }
        
        System.out.println("Detail requested");
    }
}
