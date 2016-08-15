/*
 * #%L
 * cwf-ui-orders
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
package org.hspconsortium.cwf.ui.orders;

import java.util.Collections;
import java.util.List;

import org.carewebframework.common.StrUtil;

import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.DeviceUseRequest;
import org.hl7.fhir.dstu3.model.DiagnosticRequest;
import org.hl7.fhir.dstu3.model.Medication;
import org.hl7.fhir.dstu3.model.MedicationOrder;
import org.hl7.fhir.dstu3.model.MedicationOrder.MedicationOrderDosageInstructionComponent;
import org.hl7.fhir.dstu3.model.NutritionRequest;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Type;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.hspconsortium.cwf.ui.reporting.controller.ResourceListView;

/**
 * Controller for patient orders display.
 */
public class MainController extends ResourceListView<IBaseResource, IBaseResource> {
    
    
    private static final long serialVersionUID = 1L;
    
    // @formatter:off
    private static final String QUERY = "Patient?_id=#"
            + "&_revinclude=MedicationOrder:patient"
            + "&_revinclude=ProcedureRequest:subject"
            + "&_revinclude=NutritionRequest:patient"
            + "&_revinclude=DiagnosticRequest:subject"
            + "&_revinclude=DeviceUseRequest:subject";
    // @formatter:on
    
    @Override
    protected void init() {
        setup(IBaseResource.class, "Orders", "Order Detail", QUERY, 1, "Type^^min", "Date^^min", "Order^^1", "Notes^^1");
        super.init();
    }
    
    @Override
    protected List<IBaseResource> processBundle(Bundle bundle) {
        return FhirUtil.getEntries(bundle, null, Collections.singletonList(Patient.class));
    }
    
    @Override
    protected void render(IBaseResource order, List<Object> columns) {
        if (order instanceof ProcedureRequest) {
            render((ProcedureRequest) order, columns);
        } else if (order instanceof NutritionRequest) {
            render((NutritionRequest) order, columns);
        } else if (order instanceof MedicationOrder) {
            render((MedicationOrder) order, columns);
        } else if (order instanceof DiagnosticRequest) {
            render((DiagnosticRequest) order, columns);
        } else if (order instanceof DeviceUseRequest) {
            render((DeviceUseRequest) order, columns);
        }
    }
    
    private void render(ProcedureRequest order, List<Object> columns) {
        columns.add("Procedure");
        columns.add(order.getOrderedOn());
        columns.add(order.getCode());
        columns.add(order.getNotes());
    }
    
    private void render(NutritionRequest order, List<Object> columns) {
        columns.add("Nutrition");
        columns.add(order.getDateTime());
        columns.add("");
        columns.add("");
    }
    
    private void render(MedicationOrder order, List<Object> columns) {
        columns.add("Medication");
        columns.add(order.getDateWritten());
        
        if (order.hasMedicationReference()) {
            Medication medication = getFhirService().getResource((Reference) order.getMedication(), Medication.class);
            columns.add(medication.getCode());
        } else {
            columns.add(order.getMedication());
        }
        
        StringBuilder sb = new StringBuilder();
        
        for (MedicationOrderDosageInstructionComponent di : order.getDosageInstruction()) {
            append(sb, di.getDose(), " ");
            append(sb, di.getRate(), " ");
            append(sb, di.getSite(), " ");
            append(sb, di.getMethod(), " ");
            append(sb, di.getRoute(), " ");
            append(sb, di.getTiming(), " ");
            append(sb, di.getText(), " ");
            
            Type prn = di.getAsNeeded();
            
            if (prn instanceof BooleanType && ((BooleanType) prn).getValue()) {
                append(sb, "PRN", " ");
            } else {
                append(sb, prn, " ");
            }
        }
        
        columns.add(sb);
    }
    
    private void render(DiagnosticRequest order, List<Object> columns) {
        columns.add("Diagnostic");
        columns.add(order.hasAuthored()
            ? order.getAuthored() : null);
        StringBuilder sb = new StringBuilder();
        
        for (CodeableConcept item : order.getReason()) {
            append(sb, item.getCodingFirstRep().getCode(), ", ");
        }
        
        columns.add(sb);
        columns.add(order.getNote());
    }
    
    private void render(DeviceUseRequest order, List<Object> columns) {
        columns.add("Device Use");
        columns.add(order.getAuthored());
        columns.add(order.getDevice().getId());
        columns.add(order.getNote());
    }
    
    private void append(StringBuilder sb, IBaseDatatype value, String delimiter) {
        append(sb, FhirUtil.getDisplayValueForType(value), delimiter);
    }
    
    private void append(StringBuilder sb, String value, String delimiter) {
        StrUtil.strAppend(sb, value, delimiter);
    }
    
    @Override
    protected void initModel(List<IBaseResource> orders) {
        model.addAll(orders);
    }
    
}
