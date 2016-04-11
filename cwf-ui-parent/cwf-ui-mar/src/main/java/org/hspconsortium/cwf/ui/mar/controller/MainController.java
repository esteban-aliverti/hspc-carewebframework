/*
 * #%L
 * Medication Administration Record
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
package org.hspconsortium.cwf.ui.mar.controller;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.shell.plugins.PluginContainer;
import org.carewebframework.shell.plugins.PluginController;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Grid;

import org.hspconsortium.cwf.api.eps.EPSService;
import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.fhir.medication.MedicationService;
import org.hspconsortium.cwf.ui.mar.MedicationActionUtil;
import org.hspconsortium.cwf.ui.mar.model.MarModel;
import org.hspconsortium.cwf.ui.mar.render.MarRenderer;
import org.socraticgrid.fhir.generated.IQICoreMedicationAdministration;
import org.socraticgrid.fhir.generated.IQICoreMedicationOrder;
import org.socraticgrid.fhir.generated.IQICorePatient;
import org.socraticgrid.fhir.generated.QICorePatientAdapter;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;

/**
 * The MAR plugin controller supports functionality for Medication Administration Records (a.k.a.,
 * MARs) TODO: Add paging support. Add windows for limit displays.
 */
public class MainController extends PluginController implements PatientContext.IPatientContextEvent {
    
    
    private static final long serialVersionUID = 1214364879321478753L;
    
    /**
     * Topic where new medication administrations will be published.
     */
    public static final String MED_TOPIC = "/Meds";
    
    /**
     * Topic where new Condition resources will be published
     */
    private static final String PATIENT_TOPIC = "/Patient";
    
    private static final Log log = LogFactory.getLog(MainController.class);
    
    /**
     * Service used to create, update, and/or delete medication-related resources from a FHIR server
     * to support MAR operations.
     */
    private final MedicationService medicationService;
    
    /**
     * Event Publish Subscribe service for the publication of notifications against publication
     * topics.
     */
    private final EPSService epsService;
    
    /**
     * Grid widget for representing medication administrations along a timeline.
     */
    private Grid marGrid;
    
    /**
     * A dynamic MAR grid row renderer associated with this MAR
     */
    private final MarRenderer marRowRenderer = new MarRenderer(this, MarModel.checkboxPlaceholder, "checkmark.gif");
    
    /**
     * Widget human-friendly title
     */
    private String title = "Medication Administration Record";
    
    /**
     * Convenience identifier to group medication administrations created using the MAR. This
     * identifier can be useful for bulk updates and deletes.
     */
    private final IdentifierDt generatedMedAdminsIdentifier = new IdentifierDt()
            .setSystem("urn:cogmedsys:hsp:model:medicationadministration").setValue("gen");
    
    public MainController(EPSService epsService, MedicationService medicationService) {
        this.epsService = epsService;
        this.medicationService = medicationService;
    }
    
    /**
     * Returns the human-friendly name for this MAR plugin
     * 
     * @return The title.
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the human-friendly name for this MAR plugin
     * 
     * @param title The title.
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Fetches medication administrations for this patient and then initializes and refreshes the
     * MAR Grid widget. TODO: May need to add a time range or other criteria for the medication
     * administration search.
     */
    public void initializeMar() {
        Patient patient = PatientContext.getActivePatient();
        if (patient != null) {
            IQICorePatient adaptedPatient = new QICorePatientAdapter(patient);
            List<IQICoreMedicationAdministration> medAdmins = medicationService
                    .searchMedicationAdministrationsForPatient(adaptedPatient);
            List<IQICoreMedicationOrder> medOrders = medicationService.searchMedicationOrdersForPatient(adaptedPatient);
            MarModel model = new MarModel(medOrders, medAdmins);
            MarRenderer.populateGrid(marGrid, model, marRowRenderer);
        } else {
            log.info("No patient context defined at this time. Grid not initialized");
        }
    }
    
    /**
     * Method marshals a Medication Administration resource to JSON and publishes it against an EPS
     * topic.
     * 
     * @param medAdmin
     * @param epsTopic
     */
    public void publishJsonPayloadToEpsTopic(IQICoreMedicationAdministration medAdmin, String epsTopic) {
        epsService.publishResourceToTopic(epsTopic, medAdmin.getAdaptee(), "New Medication Administration",
            "New Medication Administration");
    }
    
    /******************************************************************
     * Overridden Supertype Methods
     ******************************************************************/
    
    /**
     * @see org.carewebframework.ui.FrameworkController#doAfterCompose(org.zkoss.zk.ui.Component)
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        initializeMar();
        log.trace("Controller composed");
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
        //lblDate.setValue(new Date().toString());
        initializeMar();
    }
    
    /**
     * @see org.carewebframework.shell.plugins.IPluginEvent#onInactivate()
     */
    @Override
    public void onInactivate() {
        super.onInactivate();
    }
    
    /**
     * Fetches the latest patient medication administration records and refresh the view.
     */
    public void onClick$btnMarRefresh() {
        initializeMar();
    }
    
    /**
     * Clears the patient's medication administration records. Obviously for a demo on should not
     * remain if this demo plugin is adapted for production use cases.
     */
    public void onClick$btnMarClear() {
        medicationService.deleteMedicationAdministrationsByIdentifier(generatedMedAdminsIdentifier);
        initializeMar();
    }
    
    /**
     * Button handler for the addition of verbal orders. Each added verbal order will be displayed
     * as a row on the MAR.
     */
    public void onClick$btnVerbalOrder() {
        MedicationActionUtil.show(true);
        initializeMar();
    }
    
    @Override
    public String pending(boolean silent) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void committed() {
        initializeMar();
        
    }
    
    @Override
    public void canceled() {
        // TODO Auto-generated method stub
        
    }
    
}