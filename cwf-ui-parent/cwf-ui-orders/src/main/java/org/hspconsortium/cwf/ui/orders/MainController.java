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

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.hspconsortium.cwf.ui.reporting.controller.ResourceListView;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.DeviceUseRequest;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticOrder;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticOrder.Item;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.resource.NutritionOrder;
import ca.uhn.fhir.model.dstu2.resource.Order;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;

/**
 * Controller for patient orders display.
 */
public class MainController extends ResourceListView<Order, IBaseResource> {
    
    
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void init() {
        setup(Order.class, "Orders", "Order Detail", "Order?patient=#", 1, "Order", "Date", "Notes");
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
        columns.add(order.getCode());
        columns.add(order.getOrderedOn());
        columns.add(order.getNotes());
    }
    
    private void render(NutritionOrder order, List<Object> columns) {
        columns.add("Dietary");
        columns.add(order.getDateTime());
        columns.add("");
    }
    
    private void render(MedicationOrder order, List<Object> columns) {
        columns.add(order.getMedication());
        columns.add(order.getDateWritten());
        columns.add(order.getNote());
    }
    
    private void render(DiagnosticOrder order, List<Object> columns) {
        StringBuilder sb = new StringBuilder();
        
        for (Item item : order.getItem()) {
            sb.append(sb.length() == 0 ? "" : ", ").append(FhirUtil.getDisplayValueForType(item.getCode()));
        }
        
        columns.add(sb);
        columns.add(order.getEventFirstRep().getDateTime());
        columns.add(order.getNote());
    }
    
    private void render(DeviceUseRequest order, List<Object> columns) {
        columns.add(order.getDevice().getDisplay());
        columns.add(order.getOrderedOn());
        columns.add(order.getNotes());
    }
    
    @Override
    protected void initModel(List<Order> orders) {
        for (Order order : orders) {
            for (ResourceReferenceDt detail : order.getDetail()) {
                model.add(getFhirService().getResource(detail));
            }
        }
    }
    
}
