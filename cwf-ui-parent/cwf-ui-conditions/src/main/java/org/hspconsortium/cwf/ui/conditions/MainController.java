/*
 * #%L
 * cwf-ui-conditions
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
package org.hspconsortium.cwf.ui.conditions;

import java.util.List;

import org.hl7.fhir.dstu3.model.Condition;
import org.hspconsortium.cwf.ui.reporting.controller.ResourceListView;

/**
 * Controller for patient conditions display.
 */
public class MainController extends ResourceListView<Condition, Condition> {
    
    
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void init() {
        setup(Condition.class, "Conditions", "Condition Detail", "Condition?patient=#", 1, "Condition", "Date", "Status",
            "Notes");
        super.init();
    }
    
    @Override
    protected void render(Condition condition, List<Object> columns) {
        columns.add(condition.getCode());
        columns.add(condition.getDateRecorded());
        columns.add(condition.getClinicalStatus());
        columns.add(condition.getNotes());
    }
    
    @Override
    protected void initModel(List<Condition> entries) {
        model.addAll(entries);
    }
    
}
