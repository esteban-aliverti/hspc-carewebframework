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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.api.domain.IUser;
import org.carewebframework.api.security.SecurityUtil;
import org.carewebframework.common.DateUtil;
import org.carewebframework.common.StrUtil;
import org.carewebframework.ui.FrameworkController;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.zhtml.Div;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbar;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.DateType;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Patient.PatientCommunicationComponent;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.Type;
import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.hspconsortium.cwf.ui.patientselection.PatientSelection;

/**
 * Controller for patient header plugin.
 */
public class PatientHeader extends FrameworkController implements PatientContext.IPatientContextEvent {
    
    
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(PatientHeader.class);
    
    private Toolbar tbPatient;
    
    private Button btnDetail;
    
    private Component pnlDetail;
    
    private Label lblName;
    
    private Label lblGender;
    
    private Label lblDOBLabel;
    
    private Label lblDOB;
    
    private Label lblDODLabel;
    
    private Label lblDOD;
    
    private Label lblUser;
    
    private String noSelection;
    
    private Patient patient;
    
    private String patientName;
    
    private boolean needsDetail = true;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        noSelection = lblName.getValue();
        IUser user = SecurityUtil.getAuthenticatedUser();
        setLabel(lblUser, user.getFullName() + " @ " + user.getSecurityDomain().getName(), null);
        committed();
    }
    
    public void onClick$lnkSelect() {
        PatientSelection.show();
    }
    
    public void onClick$btnDetail(Event event) {
        buildDetail();
    }
    
    @Override
    public void canceled() {
    }
    
    @Override
    public void committed() {
        hideLabels();
        patient = PatientContext.getActivePatient();
        needsDetail = true;
        ZKUtil.detachChildren(pnlDetail);
        
        if (log.isDebugEnabled()) {
            log.debug("patient: " + patient);
        }
        
        if (patient == null) {
            lblName.setValue(noSelection);
            lblName.setSclass("z-bandbox-disabled");
            btnDetail.setDisabled(true);
            return;
        }
        
        btnDetail.setDisabled(false);
        patientName = FhirUtil.formatName(patient.getName());
        String mrn = FhirUtil.getMRNString(patient);
        lblName.setValue(patientName + (mrn.isEmpty() ? "" : "  (" + mrn + ")"));
        lblName.setSclass(null);
        setLabel(lblDOB, formatDateAndAge(patient.getBirthDate()), lblDOBLabel);
        setLabel(lblDOD, formatDOD(patient.getDeceased()), lblDODLabel);
        setLabel(lblGender, patient.hasGender() ? patient.getGender().getDisplay() : null, null);
        Clients.resize(root);
    }
    
    @Override
    public String pending(boolean silent) {
        return null;
    }
    
    private String formatDOD(Type value) {
        if (value == null) {
            return null;
        }
        
        DateType dod = FhirUtil.getTyped(value, DateType.class);
        
        if (dod != null) {
            return formatDateAndAge(dod.getValue());
        }
        
        BooleanType isDead = FhirUtil.getTyped(value, BooleanType.class);
        
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
        for (Component child : tbPatient.getChildren()) {
            if (child instanceof Label && child != lblName) {
                child.setVisible(false);
            }
        }
    }
    
    private boolean buildDetail() {
        if (!needsDetail) {
            return false;
        }
        
        needsDetail = false;
        boolean needsHeader;
        
        // Names
        
        needsHeader = true;
        
        for (HumanName name : patient.getName()) {
            
            String nm = FhirUtil.formatName(name);
            
            if (patientName.equals(nm)) {
                continue;
            }
            
            if (needsHeader) {
                needsHeader = false;
                addHeader("Other Names");
            }
            
            addDetail(nm, null);
        }
        
        // Identifiers
        
        needsHeader = true;
        
        for (Identifier id : patient.getIdentifier()) {
            if (needsHeader) {
                needsHeader = false;
                addHeader("Identifiers");
            }
            
            String use = id.hasUse() ? id.getUse().getDisplay() : "";
            String system = id.hasSystem() ? id.getSystem() : "";
            String value = id.hasValue() ? id.getValue() : "";
            
            if (!StringUtils.isEmpty(system)) {
                value += " (" + system + ")";
            }
            
            addDetail(value, use);
        }
        
        // Communication
        
        needsHeader = true;
        
        for (PatientCommunicationComponent comm : patient.getCommunication()) {
            if (needsHeader) {
                needsHeader = false;
                addHeader("Communication");
            }
            
            String language = FhirUtil.getDisplayValueForType(comm.getLanguage());
            
            if (comm.getPreferred()) {
                language += " (preferred)";
            }
            
            addDetail(language, null);
        }
        // Telecom info
        
        needsHeader = true;
        
        List<ContactPoint> telecoms = new ArrayList<>(patient.getTelecom());
        Collections.sort(telecoms, new Comparator<ContactPoint>() {
            
            
            @Override
            public int compare(ContactPoint cp1, ContactPoint cp2) {
                return cp1.getRank() - cp2.getRank();
            }
            
        });
        
        for (ContactPoint telecom : telecoms) {
            if (needsHeader) {
                needsHeader = false;
                addHeader("Contact Details");
            }
            
            String type = telecom.hasSystem() ? telecom.getSystem().getDisplay() : "";
            String use = telecom.hasUse() ? telecom.getUse().getDisplay() : "";
            
            if (!StringUtils.isEmpty(use)) {
                type += " (" + use + ")";
            }
            
            addDetail(telecom.getValue(), type);
        }
        
        // Address(es)
        needsHeader = true;
        
        for (Address address : patient.getAddress()) {
            if (needsHeader) {
                needsHeader = false;
                addHeader("Addresses");
            }
            
            String type = address.hasType() ? address.getType().getDisplay() : "";
            String use = address.hasUse() ? address.getUse().getDisplay() : "";
            
            if (!StringUtils.isEmpty(type)) {
                use += " (" + type + ")";
            }
            
            addDetail(" ", use);
            
            for (StringType line : address.getLine()) {
                addDetail(line.getValue(), null);
            }
            
            StringBuilder line = new StringBuilder();
            line.append(address.getCity()).append(", ");
            line.append(address.getState()).append("  ");
            line.append(address.getPostalCode());
            addDetail(line.toString(), null);
        }
        
        if (pnlDetail.getFirstChild() == null) {
            addDetail(StrUtil.getLabel("cwfpatientheader.nodetail.label"), null);
        }
        
        return true;
    }
    
    private void addHeader(String text) {
        Label header = new Label(text);
        header.setSclass("cwf-patientheader-header");
        pnlDetail.appendChild(header);
    }
    
    private void addDetail(String text, String label) {
        if (StringUtils.isEmpty(text)) {
            return;
        }
        
        Div div = new Div();
        pnlDetail.appendChild(div);
        
        if (label != null) {
            Label lbl = new Label(label);
            lbl.setSclass("cwf-patientheader-label");
            div.appendChild(lbl);
        }
        
        div.appendChild(new Label(text));
    }
}
