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

import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.hspconsortium.cwf.ui.patientselection.PatientSelection;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.Patient.Communication;
import ca.uhn.fhir.model.primitive.BooleanDt;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.StringDt;

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
        
        for (HumanNameDt name : patient.getName()) {
            
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
        
        for (IdentifierDt id : patient.getIdentifier()) {
            if (needsHeader) {
                needsHeader = false;
                addHeader("Identifiers");
            }
            
            String use = id.getUse();
            String system = id.getSystem();
            String value = id.getValue();
            
            if (!StringUtils.isEmpty(system)) {
                value += " (" + system + ")";
            }
            
            addDetail(value, use);
        }
        
        // Communication
        
        needsHeader = true;
        
        for (Communication comm : patient.getCommunication()) {
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
        
        List<ContactPointDt> telecoms = new ArrayList<>(patient.getTelecom());
        Collections.sort(telecoms, new Comparator<ContactPointDt>() {
            
            
            @Override
            public int compare(ContactPointDt cp1, ContactPointDt cp2) {
                int rank1 = cp1.getRank() == null ? 0 : cp1.getRank();
                int rank2 = cp2.getRank() == null ? 0 : cp2.getRank();
                return rank1 - rank2;
            }
            
        });
        
        for (ContactPointDt telecom : telecoms) {
            if (needsHeader) {
                needsHeader = false;
                addHeader("Contact Details");
            }
            
            String type = telecom.getSystem();
            String use = telecom.getUse();
            
            if (!StringUtils.isEmpty(use)) {
                type += " (" + use + ")";
            }
            
            addDetail(telecom.getValue(), type);
        }
        
        // Address(es)
        needsHeader = true;
        
        for (AddressDt address : patient.getAddress()) {
            if (needsHeader) {
                needsHeader = false;
                addHeader("Addresses");
            }
            
            String type = address.getType();
            String use = address.getUse();
            
            if (!StringUtils.isEmpty(type)) {
                use += " (" + type + ")";
            }
            
            addDetail(" ", use);
            
            for (StringDt line : address.getLine()) {
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
