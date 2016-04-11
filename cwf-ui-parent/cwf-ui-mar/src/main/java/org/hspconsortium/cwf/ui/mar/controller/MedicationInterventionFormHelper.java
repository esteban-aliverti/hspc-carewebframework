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
package org.hspconsortium.cwf.ui.mar.controller;

import java.math.BigDecimal;

import org.zkoss.zul.Combobox;

import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.fhir.common.FhirTerminology;
import org.hspconsortium.cwf.fhir.common.FhirUtil;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.SimpleQuantityDt;
import ca.uhn.fhir.model.dstu2.composite.TimingDt.Repeat;
import ca.uhn.fhir.model.dstu2.resource.Patient;

public class MedicationInterventionFormHelper {
    
    
    private final SimpleMedicationAdministrationController controller;
    
    private CodeableConceptDt selectedMedication;
    
    private CodeableConceptDt selectedUnits;
    
    private CodeableConceptDt selectedFrequency;
    
    private CodeableConceptDt selectedRoute;
    
    private CodeableConceptDt selectedTimeUnit;
    
    private CodeableConceptDt selectedPrnReason;
    
    private BigDecimal doseAmount;
    
    private BigDecimal durationTime;
    
    private Boolean isPRNMed;
    
    public MedicationInterventionFormHelper(SimpleMedicationAdministrationController controller) {
        this.controller = controller;
    }
    
    public void initialize() {
        
        selectedMedication = getSelectedCode(controller.getMedSelector());
        selectedUnits = getSelectedCode(controller.getDoseUnitSelector());
        selectedRoute = getSelectedCode(controller.getRouteOfAdminSelector());
        selectedFrequency = getSelectedCode(controller.getFrequencySelector());
        selectedTimeUnit = getSelectedCode(controller.getUnitOfTimeSelector());
        selectedPrnReason = getSelectedCode(controller.getPrnReasonSelector());
        
        doseAmount = getDoseQuantityValue();
        durationTime = getDurationTime();
        
        isPRNMed = getIsPrnValue();
    }
    
    /**
     * Returns true if the necessary form state meets the minimum requirements for placing an
     * medication order.
     * 
     * @return
     */
    public boolean meetsOrderRequirements() {
        Patient patient = PatientContext.getActivePatient();
        return (patient != null && selectedMedication != null && selectedUnits != null && doseAmount != null);
    }
    
    /**
     * Returns true if the necessary form state meets the minimum requirements for administering a
     * medication.
     * 
     * @return
     */
    public boolean meetsMedicationAdministrationRequirements() {
        Patient patient = PatientContext.getActivePatient();
        return (patient != null && selectedMedication != null && selectedUnits != null && doseAmount != null);
    }
    
    public CodeableConceptDt getSelectedMedication() {
        return selectedMedication;
    }
    
    public void setSelectedMedication(CodeableConceptDt selectedMedication) {
        this.selectedMedication = selectedMedication;
    }
    
    public CodeableConceptDt getSelectedUnits() {
        return selectedUnits;
    }
    
    public void setSelectedUnits(CodeableConceptDt selectedUnits) {
        this.selectedUnits = selectedUnits;
    }
    
    public CodeableConceptDt getSelectedFrequency() {
        return selectedFrequency;
    }
    
    public void setSelectedFrequency(CodeableConceptDt selectedFrequency) {
        this.selectedFrequency = selectedFrequency;
    }
    
    public CodeableConceptDt getSelectedRoute() {
        return selectedRoute;
    }
    
    public void setSelectedRoute(CodeableConceptDt selectedRoute) {
        this.selectedRoute = selectedRoute;
    }
    
    public CodeableConceptDt getSelectedTimeUnit() {
        return selectedTimeUnit;
    }
    
    public void setSelectedTimeUnit(CodeableConceptDt selectedTimeUnit) {
        this.selectedTimeUnit = selectedTimeUnit;
    }
    
    public CodeableConceptDt getSelectedPrnReason() {
        return selectedPrnReason;
    }
    
    public void setSelectedPrnReason(CodeableConceptDt selectedPrnReason) {
        this.selectedPrnReason = selectedPrnReason;
    }
    
    public BigDecimal getDoseAmount() {
        return doseAmount;
    }
    
    public void setDoseAmount(BigDecimal doseAmount) {
        this.doseAmount = doseAmount;
    }
    
    public BigDecimal getDurationTime() {
        return durationTime;
    }
    
    public void setDurationTime(BigDecimal durationTime) {
        this.durationTime = durationTime;
    }
    
    public Boolean getIsPRNMed() {
        return isPRNMed;
    }
    
    public void setIsPRNMed(Boolean isPRNMed) {
        this.isPRNMed = isPRNMed;
    }
    
    public BigDecimal getDoseQuantityValue() {
        BigDecimal doseQuantity = null;
        if (controller.getDoseQuantity() != null) {
            doseQuantity = controller.getDoseQuantity().getValue();
        }
        return doseQuantity;
    }
    
    public BigDecimal getDurationTimeValue() {
        BigDecimal durationTime = null;
        if (controller.getDuration() != null) {
            durationTime = controller.getDuration().getValue();
        }
        return durationTime;
    }
    
    public Boolean getIsPrnValue() {
        Boolean isPRN = null;
        if (controller.getIsPRN() != null) {
            isPRN = controller.getIsPRN().isChecked();
        }
        return isPRN;
    }
    
    public SimpleQuantityDt getDoseQuantity() {
        SimpleQuantityDt simpleQuantity = null;
        if (doseAmount != null && selectedUnits != null) {
            simpleQuantity = new SimpleQuantityDt();// TODO Support specifying
                                                    // in UI? Leave blank? Ask
                                                    // Emory.
            simpleQuantity.setValue(doseAmount);
            simpleQuantity.setUnit(selectedUnits.getCodingFirstRep().getCode());
        }
        return simpleQuantity;
    }
    
    public Repeat getTimingRepeat() {
        Repeat repeat = null;
        if (selectedFrequency != null && selectedFrequency.getCodingFirstRep().getDisplay() != null) {
            repeat = FhirUtil.getRepeatFromFrequencyCode(selectedFrequency.getCodingFirstRep().getDisplay());
            if (durationTime != null) {
                repeat.setDuration(durationTime);
                repeat.setDurationUnits(FhirUtil.convertTimeUnitToEnum(selectedTimeUnit.getCodingFirstRep().getCode()));
            }
        }
        return repeat;
    }
    
    public CodeableConceptDt getSelectedCode(Combobox dropdown) {
        CodeableConceptDt selection = null;
        if (dropdown != null && dropdown.getSelectedItem() != null) {
            String label = dropdown.getSelectedItem().getLabel();
            String value = dropdown.getSelectedItem().getValue();
            selection = FhirUtil.createCodeableConcept(FhirTerminology.RXNORM, value, label);
        }
        return selection;
    }
    
    // public Repeat convertToTiming(CodeableConceptDt frequency) {
    // Repeat repeat = new Repeat();
    // if(frequency.getCodingFirstRep().getCode().equals("1")) {
    // repeat.setFrequency(1);
    // repeat.setPeriod(24);
    // repeat.setPeriodUnits(UnitsOfTimeEnum.H);
    // } else if(frequency.getCodingFirstRep().getCode().equals("2")) {
    // repeat.setFrequency(1);
    // repeat.setPeriod(8);
    // repeat.setPeriodUnits(UnitsOfTimeEnum.H);
    // } else {
    // throw new RuntimeException("Unknown timing " +
    // frequency.getCodingFirstRep().getDisplay());
    // }
    // return repeat;
    // }
}
