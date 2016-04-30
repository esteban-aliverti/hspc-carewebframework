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

import java.util.List;

import org.carewebframework.common.StrUtil;

import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.DeviceUseRequest;
import org.hl7.fhir.dstu3.model.DiagnosticOrder;
import org.hl7.fhir.dstu3.model.DiagnosticOrder.DiagnosticOrderItemComponent;
import org.hl7.fhir.dstu3.model.Medication;
import org.hl7.fhir.dstu3.model.MedicationOrder;
import org.hl7.fhir.dstu3.model.MedicationOrder.MedicationOrderDosageInstructionComponent;
import org.hl7.fhir.dstu3.model.NutritionOrder;
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
            + "&_revinclude=NutritionOrder:patient"
            + "&_revinclude=DiagnosticOrder:subject"
            + "&_revinclude=DeviceUseRequest:subject";
    // @formatter:on
    
    @Override
    protected void init() {
        setup(IBaseResource.class, "Orders", "Order Detail", QUERY, 1, "Type^^min", "Date^^min", "Order^^1", "Notes^^1");
        super.init();
    }
    
    @Override
    protected void render(IBaseResource order, List<Object> columns) {
        if (order instanceof ProcedureRequest) {
            render((ProcedureRequest) order, columns);
        } else if (order instanceof NutritionOrder) {
            render((NutritionOrder) order, columns);
        } else if (order instanceof MedicationOrder) {
            render((MedicationOrder) order, columns);
        } else if (order instanceof DiagnosticOrder) {
            render((DiagnosticOrder) order, columns);
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
    
    private void render(NutritionOrder order, List<Object> columns) {
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
    
    private void render(DiagnosticOrder order, List<Object> columns) {
        columns.add("Diagnostic");
        columns.add(order.hasEvent() ? order.getEvent().get(0).getDateTime() : null);
        StringBuilder sb = new StringBuilder();
        
        for (DiagnosticOrderItemComponent item : order.getItem()) {
            append(sb, item.getCode(), ", ");
        }
        
        columns.add(sb);
        columns.add(order.getNote());
    }
    
    private void render(DeviceUseRequest order, List<Object> columns) {
        columns.add("Device Use");
        columns.add(order.getOrderedOn());
        columns.add(order.getDevice().getDisplay());
        columns.add(order.getNotes());
    }
    
    private void append(StringBuilder sb, IBaseDatatype value, String delimiter) {
        append(sb, FhirUtil.getDisplayValueForType(value), delimiter);
    }
    
    private void append(StringBuilder sb, String value, String delimiter) {
        StrUtil.strAppend(sb, value, delimiter);
    }
    
    @Override
    protected void initModel(List<IBaseResource> orders) {
        if (!orders.isEmpty() && orders.get(0) instanceof Patient) {
            orders.remove(0);
        }
        
        model.addAll(orders);
    }
    
}
