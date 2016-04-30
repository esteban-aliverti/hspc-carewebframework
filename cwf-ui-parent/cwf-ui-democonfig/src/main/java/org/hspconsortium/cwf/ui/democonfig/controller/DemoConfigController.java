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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.shell.plugins.PluginContainer;
import org.carewebframework.shell.plugins.PluginController;
import org.carewebframework.ui.zk.PromptDialog;

import org.zkoss.zk.ui.Component;

import org.hl7.fhir.dstu3.model.Patient;
import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.ui.democonfig.util.Bootstrapper;

/**
 * This controller is only intended to be used for demo purposes in order to stage and unstage data.
 * At this time, it is fairly simple in its function. At a later time, it can be enhanced as needed
 * for demo or connectathon use cases.
 */
public class DemoConfigController extends PluginController {
    
    
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(DemoConfigController.class);
    
    private final Bootstrapper bootstrapper;
    
    public DemoConfigController(Bootstrapper bootstrapper) {
        super();
        this.bootstrapper = bootstrapper;
    }
    
    /**
     * @see org.carewebframework.ui.FrameworkController#doAfterCompose(org.zkoss.zk.ui.Component)
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        log.trace("DemoConfigController composed");
    }
    
    /**
     * @see org.carewebframework.shell.plugins.IPluginEvent#onLoad(org.carewebframework.shell.plugins.PluginContainer)
     */
    @Override
    public void onLoad(PluginContainer container) {
        super.onLoad(container);
    }
    
    /**
     * @see org.carewebframework.shell.plugins.IPluginEvent#onUnload()
     */
    @Override
    public void onUnload() {
        super.onUnload();
    }
    
    /**
     * @see org.carewebframework.shell.plugins.IPluginEvent#onActivate()
     */
    @Override
    public void onActivate() {
        super.onActivate();
    }
    
    /**
     * @see org.carewebframework.shell.plugins.IPluginEvent#onInactivate()
     */
    @Override
    public void onInactivate() {
        super.onInactivate();
    }
    
    /*************************************************************************
     * Event Listeners
     *************************************************************************/
    
    /**
     * Event handler for btnAddPatient on-click events. Adds a new patient to the FHIR server if one
     * does not already exist.
     */
    public void onClick$btnAddPatient() {
        bootstrapper.addDemoPatients();
    }
    
    /**
     * Event handler for btnDelPatient on-click events. Deletes the patient from the FHIR server.
     */
    public void onClick$btnDelPatient() {
        bootstrapper.deleteDemoPatient();
    }
    
    /**
     * Event handler for the btnAddMedAdmins on-click events. Adds new medication administrations to
     * the FHIR server if they do not already exist. Note that this method will not replace an
     * existing instance.
     */
    public void onClick$btnAddMedAdmins() {
        Patient patient = PatientContext.getActivePatient();
        if (patient != null) {
            bootstrapper.addSampleMedicationAdmins(patient);
        } else {
            log.error("No patient selected in patient context. Request ignored.");
            PromptDialog.showWarning("You must first select a patient before administering a medication",
                "Please first select a patient");
        }
    }
    
    /**
     * Event handler for the btnDelMedAdmins on-click events. Delete all medication in the demo set.
     */
    public void onClick$btnDelMedAdmins() {
        bootstrapper.clearMedicationAdministrations();
    }
    
    /**
     * Event handler for the btnAddMedAdmins on-click events. Adds new medication administrations to
     * the FHIR server if they do not already exist. Note that this method will not replace an
     * existing instance.
     */
    public void onClick$btnAddMedOrders() {
        Patient patient = PatientContext.getActivePatient();
        if (patient == null) {
            log.error("No patient selected in patient context. Request ignored.");
            PromptDialog.showWarning("You must first select a patient before ordering a medication",
                "Please first select a patient");
        }
    }
    
    /**
     * Event handler for the btnDelMedAdmins on-click events. Delete all medication in the demo set.
     */
    public void onClick$btnDelMedOrders() {
        bootstrapper.clearMedicationOrders();
    }
    
    /**
     * Event handler for the btnAddMedAdmins on-click events. Adds new medication administrations to
     * the FHIR server if they do not already exist. Note that this method will not replace an
     * existing instance.
     */
    public void onClick$btnAddConditions() {
        Patient patient = PatientContext.getActivePatient();
        if (patient == null) {
            log.error("No patient selected in patient context. Request ignored.");
            PromptDialog.showWarning("You must first select a patient before ordering a medication",
                "Please first select a patient");
        }
    }
    
    /**
     * Event handler for the btnDelConditions on-click events. Delete all conditions in the demo
     * set.
     */
    public void onClick$btnDelConditions() {
        bootstrapper.clearConditions();
    }
    
}
