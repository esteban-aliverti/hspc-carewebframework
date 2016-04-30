/*
 * #%L
 * cwf-ui-medications
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
package org.hspconsortium.cwf.ui.medicationorders;

import java.util.List;

import org.carewebframework.common.StrUtil;

import org.hl7.fhir.dstu3.exceptions.FHIRException;
import org.hl7.fhir.dstu3.model.Medication;
import org.hl7.fhir.dstu3.model.MedicationOrder;
import org.hl7.fhir.dstu3.model.MedicationOrder.MedicationOrderDosageInstructionComponent;
import org.hl7.fhir.dstu3.model.Type;
import org.hspconsortium.cwf.fhir.medication.MedicationService;
import org.hspconsortium.cwf.ui.reporting.controller.ResourceListView;

/**
 * Controller for patient conditions display.
 */
public class MainController extends ResourceListView<MedicationOrder, MedicationOrder> {
    
    
    private static final long serialVersionUID = 1L;
    
    private final MedicationService service;
    
    public MainController(MedicationService service) {
        this.service = service;
    }
    
    @Override
    protected void init() {
        setup(MedicationOrder.class, "Medication Orders", "Order Detail", "MedicationOrder?patient=#", 1, "Medication",
            "Date", "Status", "Sig");
        super.init();
    }
    
    @Override
    protected void render(MedicationOrder script, List<Object> columns) {
        Object med = null;
        Type medicationDt = script.getMedication();
        
        if (script.hasMedicationCodeableConcept()) {
            try {
                med = script.getMedicationCodeableConcept();
            } catch (FHIRException e) {
                
            }
        } else if (script.hasMedicationReference()) {
            Medication medObject;
            try {
                medObject = (Medication) script.getMedicationReference().getResource();
                med = medObject.getCode();
            } catch (FHIRException e) {}
        }
        
        columns.add(med);
        columns.add(script.getDateWritten());
        columns.add(script.getStatus());
        columns.add(getSig(script.getDosageInstruction()));
    }
    
    private String getSig(List<MedicationOrderDosageInstructionComponent> dosageInstruction) {
        StringBuilder sb = new StringBuilder();
        
        for (MedicationOrderDosageInstructionComponent sig : dosageInstruction) {
            if (sb.length() > 0) {
                sb.append(StrUtil.CRLF);
            }
            
            if (sig.getText() != null) {
                sb.append(sig.getText());
            }
        }
        return sb.toString();
    }
    
    @Override
    protected void initModel(List<MedicationOrder> entries) {
        model.addAll(entries);
    }
    
}
