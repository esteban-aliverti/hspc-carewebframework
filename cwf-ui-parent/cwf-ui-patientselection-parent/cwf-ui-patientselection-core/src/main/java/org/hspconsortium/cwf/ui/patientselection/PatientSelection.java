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

import java.util.Iterator;
import java.util.List;

import org.carewebframework.api.FrameworkUtil;
import org.carewebframework.api.property.PropertyUtil;
import org.carewebframework.api.security.SecurityUtil;
import org.carewebframework.common.StrUtil;
import org.carewebframework.ui.zk.PopupDialog;
import org.carewebframework.ui.zk.PromptDialog;

import org.zkoss.util.resource.Labels;

import org.hl7.fhir.dstu3.model.Patient;
import org.hspconsortium.cwf.api.patient.PatientContext;

/**
 * This is the convenience class for accessing patient selectors.
 */
public class PatientSelection {
    
    
    /**
     * Returns the current patient selector. If one has not already been created, it is created from
     * the factory.
     * 
     * @return The patient selector.
     */
    private static IPatientSelector getSelector() {
        IPatientSelector selector = (IPatientSelector) FrameworkUtil.getAttribute(Constants.SELECTOR_ATTRIB);
        
        if (selector == null) {
            IPatientSelectorFactory factory = getFactory();
            selector = factory == null ? null : factory.create();
            FrameworkUtil.setAttribute(Constants.SELECTOR_ATTRIB, selector);
        }
        
        return selector;
    }
    
    /**
     * Returns the patient selector factory based on the PATIENT.SELECTION.SELECTOR property. If
     * this property is not set, the first registered factory is returned.
     * 
     * @return The patient selector factory.
     */
    private static IPatientSelectorFactory getFactory() {
        String factoryBeanId;
        IPatientSelectorFactory factory;
        PatientSelectorRegistry registry = PatientSelectorRegistry.getInstance();
        
        try {
            factoryBeanId = PropertyUtil.getValue("PATIENT.SELECTION.SELECTOR");
        } catch (Exception e) {
            factoryBeanId = null;
        }
        
        factory = factoryBeanId == null ? null : registry.get(factoryBeanId);
        
        if (factory == null) {
            Iterator<IPatientSelectorFactory> iterator = registry.iterator();
            
            if (iterator.hasNext()) {
                factory = iterator.next();
            } else {
                PromptDialog.showError("@patientselection.error.no.selectors");
            }
        }
        
        return factory;
    }
    
    /**
     * Displays the new patient selection dialog.
     * 
     * @return The selected patient at the time the dialog was closed. This may be different from
     *         the patient in the shared context if <b>noContextChange</b> was true or the requested
     *         context change was rejected. It will be null if no patient was selected when the
     *         dialog was closed or if the selection was canceled by the user.
     */
    public static Patient show() {
        return show(false);
    }
    
    /**
     * Displays the new patient selection dialog.
     * 
     * @param noContextChange If true, no patient context change will be requested.
     * @return The selected patient at the time the dialog was closed. This may be different from
     *         the patient in the shared context if <b>noContextChange</b> was true or the requested
     *         context change was rejected. It will be null if no patient was selected when the
     *         dialog was closed or if the selection was canceled by the user.
     */
    public static Patient show(boolean noContextChange) {
        if (canSelect(true)) {
            IPatientSelector selector = getSelector();
            Patient patient = selector == null ? null : selector.select();
            
            if (patient == null || noContextChange) {
                return patient;
            }
            
            PatientContext.changePatient(patient);
            return PatientContext.getActivePatient();
        }
        return null;
    }
    
    /**
     * Invokes the patient match dialog, displaying the specified list of patients.
     * 
     * @param patientList List of patients from which to select.
     * @return The patient selected by the user or null if the operation was canceled.
     */
    public static Patient selectFromList(List<Patient> patientList) {
        FrameworkUtil.setAttribute(Constants.RESULT_ATTRIB, patientList);
        PopupDialog.popup(Constants.RESOURCE_PREFIX + "patientMatches.zul");
        Object result = FrameworkUtil.getAttribute(Constants.RESULT_ATTRIB);
        FrameworkUtil.setAttribute(Constants.RESULT_ATTRIB, null);
        return result instanceof Patient ? (Patient) result : null;
    }
    
    /**
     * Returns true if this user has patient selection privilege.
     * 
     * @param showMessage If true and the user does not have the required privilege, displays an
     *            error dialog.
     * @return True if user can select patients.
     */
    public static boolean canSelect(boolean showMessage) {
        boolean result = SecurityUtil.isGranted("PRIV_PATIENT_SELECT");
        
        if (!result && showMessage) {
            PromptDialog.showError(Labels.getLabel(Constants.LBL_CANNOT_SELECT_MESSAGE),
                Labels.getLabel(Constants.LBL_CANNOT_SELECT_TITLE));
        }
        
        return result;
    }
    
    /**
     * Returns true if a patient selection should be forced upon login.
     * 
     * @return True if patient selection should be forced.
     */
    public static boolean forcePatientSelection() {
        
        try {
            return canSelect(false) && StrUtil.toBoolean(PropertyUtil.getValue("CAREWEB.PATIENT.FORCE.SELECT", null));
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Enforce static class.
     */
    private PatientSelection() {
    }
    
    /**
     * Move to Patient Context Requests a context change for the specified patient and any
     * accessible alternate registrations. If the REGSELECT feature is enabled, the user is given
     * the opportunity to choose which registrations to select.
     * 
     * @param patient Patient to be selected into the context.
     * @return True if a context change was requested. public static boolean changePatient(Patient
     *         patient) { List<Patient> patients = PatientContext.getRegistrations(patient); if
     *         (patients != null && patients.size() > 1 &&
     *         Features.getInstance().isEnabled("REGSELECT", false)) { patients =
     *         PatientRegistrations.execute(patients, true); if (patients == null) { return false; }
     *         } PatientContext.changePatients(patients); return true; }
     */
}
