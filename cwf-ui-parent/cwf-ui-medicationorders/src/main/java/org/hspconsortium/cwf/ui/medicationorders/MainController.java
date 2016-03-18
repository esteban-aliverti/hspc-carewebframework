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

import org.apache.commons.lang3.StringUtils;

import org.carewebframework.common.StrUtil;

import org.hspconsortium.cwf.api.ClientUtil;
import org.hspconsortium.cwf.ui.reporting.controller.ResourceListView;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Medication;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder.DosageInstruction;

/**
 * Controller for patient conditions display.
 */
public class MainController extends ResourceListView<MedicationOrder, MedicationOrder> {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void init() {
        setup(MedicationOrder.class, "Medications", "Medication Detail", "MedicationOrder?patient=#", 1, "Medication",
            "Date", "Status", "Sig");
        super.init();
    }
    
    @Override
    protected void render(MedicationOrder script, List<Object> columns) {
        String med = null;
        IDatatype medicationDt = script.getMedication();
        
        if (medicationDt != null && medicationDt instanceof CodeableConceptDt) {
            CodeableConceptDt medCode = (CodeableConceptDt) medicationDt;
            med = medCode.getCodingFirstRep().getDisplay();//Assuming there is only one code. If not, we need to get the preferred one.
        } else if (medicationDt != null && medicationDt instanceof Medication) {
            Medication medObject = (Medication) medicationDt;
            med = medObject.getCode().getCodingFirstRep().getDisplay();//Not sure about this one
        }
        
        if (StringUtils.isEmpty(med)) {
            Medication medication = ClientUtil.getResource((ResourceReferenceDt) script.getMedication(), Medication.class);
            med = medication.getCode().getCodingFirstRep().getDisplay();
        }
        
        columns.add(med);
        columns.add(script.getDateWritten());
        columns.add(script.getStatus());
        columns.add(getSig(script.getDosageInstruction()));
    }
    
    private String getSig(List<DosageInstruction> dosageInstruction) {
        StringBuilder sb = new StringBuilder();
        
        for (DosageInstruction sig : dosageInstruction) {
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
