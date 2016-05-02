/*
 * #%L
 * Demo Configuration Plugin
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
/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * 
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.hspconsortium.cwf.ui.democonfig.controller;

import org.carewebframework.common.StrUtil;
import org.carewebframework.shell.plugins.PluginController;

import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;

import org.hl7.fhir.dstu3.model.Patient;
import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.ui.democonfig.util.Bootstrapper;
import org.hspconsortium.cwf.ui.patientselection.PatientSelection;

/**
 * This controller is only intended to be used for demo purposes in order to stage and unstage data.
 * At this time, it is fairly simple in its function. At a later time, it can be enhanced as needed
 * for demo or connectathon use cases.
 */
public class DemoConfigController extends PluginController {
    
    
    private static final long serialVersionUID = 1L;
    
    private static final String ADDED = "%s added: %d";
    
    private static final String DELETED = "%s deleted: %d";
    
    private static final String NOPATIENT = "A patient must be selected to add %s.";
    
    private static final String DELETEALL = "Resources deleted: %2$d";
    
    private final Bootstrapper bootstrapper;
    
    private Tabbox tabbox;
    
    public DemoConfigController(Bootstrapper bootstrapper) {
        super();
        this.bootstrapper = bootstrapper;
    }
    
    private void showAdded(int count) {
        showMessage(ADDED, count);
    }
    
    private void showDeleted(int count) {
        showMessage(DELETED, count);
    }
    
    private void showNoPatient() {
        showMessage(NOPATIENT, null);
    }
    
    private void showMessage(String msg, Object arg) {
        Tab tab = tabbox.getSelectedTab();
        Label lbl = (Label) tab.getFellow("lbl" + tab.getId().substring(3));
        msg = StrUtil.formatMessage(msg, tab == null ? "" : tab.getLabel(), arg);
        lbl.setValue(msg);
    }
    
    private Patient getPatient() {
        return PatientContext.getActivePatient();
    }
    
    /*************************************************************************
     * Event Listeners
     *************************************************************************/
    
    public void onSelect$tabbox() {
        showMessage("", null);
    }
    
    /**
     * Select a patient.
     */
    public void onClick$btnSelectPatient() {
        PatientSelection.show();
    }
    
    /**
     * Adds demo patients to the FHIR server.
     */
    public void onClick$btnAddPatients() {
        showAdded(bootstrapper.addPatients().size());
    }
    
    /**
     * Deletes demo patients from the FHIR server.
     */
    public void onClick$btnDelPatients() {
        showDeleted(bootstrapper.deletePatients());
    }
    
    /**
     * Adds demo practitioners to the FHIR server.
     */
    public void onClick$btnAddPractitioners() {
        showAdded(bootstrapper.addPractitioners().size());
    }
    
    /**
     * Deletes demo practitioners from the FHIR server.
     */
    public void onClick$btnDelPractitioners() {
        showDeleted(bootstrapper.deletePractitioners());
    }
    
    /**
     * Adds demo medication administrations to the FHIR server.
     */
    public void onClick$btnAddMedAdmins() {
        Patient patient = getPatient();
        
        if (patient != null) {
            showAdded(bootstrapper.addMedicationAdministrations(patient).size());
        } else {
            showNoPatient();
        }
    }
    
    /**
     * Deletes demo medication administrations from the FHIR server.
     */
    public void onClick$btnDelMedAdmins() {
        showDeleted(bootstrapper.deleteMedicationAdministrations());
    }
    
    /**
     * Adds demo medication orders to the FHIR server.
     */
    public void onClick$btnAddMedOrders() {
        Patient patient = getPatient();
        
        if (patient != null) {
            showAdded(bootstrapper.addMedicationOrders(patient).size());
        } else {
            showNoPatient();
        }
    }
    
    /**
     * Deletes demo medication orders from the FHIR server.
     */
    public void onClick$btnDelMedOrders() {
        showDeleted(bootstrapper.deleteMedicationOrders());
    }
    
    /**
     * Adds demo conditions to the FHIR server.
     */
    public void onClick$btnAddConditions() {
        Patient patient = getPatient();
        
        if (patient != null) {
            showAdded(bootstrapper.addConditions(patient).size());
        } else {
            showNoPatient();
        }
    }
    
    /**
     * Deletes demo conditions from the FHIR server.
     */
    public void onClick$btnDelConditions() {
        showDeleted(bootstrapper.deleteConditions());
    }
    
    /**
     * Adds demo documents to the FHIR server.
     */
    public void onClick$btnAddDocuments() {
        Patient patient = getPatient();
        
        if (patient != null) {
            showAdded(bootstrapper.addDocuments(patient).size());
        } else {
            showNoPatient();
        }
    }
    
    /**
     * Deletes demo documents from the FHIR server.
     */
    public void onClick$btnDelDocuments() {
        showDeleted(bootstrapper.deleteDocuments());
    }
    
    /**
     * Deletes all demo resources from the FHIR server.
     */
    public void onClick$btnDeleteAll() {
        showMessage(DELETEALL, bootstrapper.deleteAll());
    }
    
}
