/*
 * #%L
 * cwf-ui-observations
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
package org.hspconsortium.cwf.ui.observations;

import java.util.List;

import org.hspconsortium.cwf.ui.reporting.controller.ResourceListView;

import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;

/**
 * Controller for patient observations display.
 */
public class MainController extends ResourceListView<Observation, Observation> {
    
    
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void init() {
        setup(Observation.class, "Observations", "Observation Detail", "Observation?patient=#", 1, "Observation", "Date",
            "Status", "Result", "Ref Range");
        super.init();
    }
    
    @Override
    protected void render(Observation observation, List<Object> columns) {
        CodingDt coding = observation.getCode().getCodingFirstRep();
        String name = "";
        
        if (coding != null) {
            name = coding.getDisplay();
            name = name == null ? coding.getCode() : name;
        }
        
        Object eff = observation.getEffective();
        
        if (eff instanceof DateTimeDt) {
            eff = ((DateTimeDt) eff).getValue();
        }
        
        Object value = observation.getValue();
        
        if (value instanceof QuantityDt) {
            QuantityDt val = (QuantityDt) value;
            String units = val.getUnit();
            value = val.getValue().toString() + (units == null ? "" : " " + units);
        }
        
        columns.add(name);
        columns.add(eff);
        columns.add(observation.getStatus());
        columns.add(value);
        columns.add(observation.getReferenceRangeFirstRep().getText());
    }
    
    @Override
    protected void initModel(List<Observation> entries) {
        model.addAll(entries);
    }
    
}