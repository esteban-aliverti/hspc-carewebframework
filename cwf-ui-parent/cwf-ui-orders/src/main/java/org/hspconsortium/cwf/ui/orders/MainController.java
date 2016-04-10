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

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.hspconsortium.cwf.ui.reporting.controller.ResourceListView;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.DeviceUseRequest;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticOrder;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticOrder.Item;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder.DosageInstruction;
import ca.uhn.fhir.model.dstu2.resource.NutritionOrder;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.primitive.BooleanDt;

/**
 * Controller for patient orders display.
 */
public class MainController extends ResourceListView<IResource, IBaseResource> {
    
    
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
        setup(IResource.class, "Orders", "Order Detail", QUERY, 1, "Type^^min", "Date^^min", "Order^^1", "Notes^^1");
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
        columns.add(order.getMedication());
        
        StringBuilder sb = new StringBuilder();
        
        for (DosageInstruction di : order.getDosageInstruction()) {
            append(sb, di.getDose(), " ");
            append(sb, di.getRate(), " ");
            append(sb, di.getSite(), " ");
            append(sb, di.getMethod(), " ");
            append(sb, di.getRoute(), " ");
            append(sb, di.getTiming(), " ");
            append(sb, di.getText(), " ");
            
            IDatatype prn = di.getAsNeeded();
            
            if (prn instanceof BooleanDt && ((BooleanDt) prn).getValue()) {
                append(sb, "PRN", " ");
            } else {
                append(sb, prn, " ");
            }
        }
        
        columns.add(sb);
    }
    
    private void render(DiagnosticOrder order, List<Object> columns) {
        columns.add("Diagnostic");
        columns.add(order.getEventFirstRep().getDateTime());
        StringBuilder sb = new StringBuilder();
        
        for (Item item : order.getItem()) {
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
    
    private void append(StringBuilder sb, IDatatype value, String delimiter) {
        append(sb, FhirUtil.getDisplayValueForType(value), delimiter);
    }
    
    private void append(StringBuilder sb, String value, String delimiter) {
        StrUtil.strAppend(sb, value, delimiter);
    }
    
    @Override
    protected void initModel(List<IResource> orders) {
        if (!orders.isEmpty() && orders.get(0) instanceof Patient) {
            orders.remove(0);
        }
        
        model.addAll(orders);
    }
    
}
