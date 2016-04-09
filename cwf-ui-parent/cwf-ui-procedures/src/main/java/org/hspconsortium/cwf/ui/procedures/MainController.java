/*
 * #%L
 * cwf-ui-procedures
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
package org.hspconsortium.cwf.ui.procedures;

import java.util.List;

import org.hspconsortium.cwf.ui.reporting.controller.ResourceListView;

import ca.uhn.fhir.model.dstu2.resource.Procedure;

/**
 * Controller for patient procedures display.
 */
public class MainController extends ResourceListView<Procedure, Procedure> {
    
    
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void init() {
        setup(Procedure.class, "Procedures", "Procedure Detail", "Procedure?patient=#", 1, "Procedure", "Date", "Status",
            "Notes");
        super.init();
    }
    
    @Override
    protected void render(Procedure procedure, List<Object> columns) {
        columns.add(procedure.getCode());
        columns.add(procedure.getPerformed());
        columns.add(procedure.getStatus());
        columns.add(procedure.getNotes());
    }
    
    @Override
    protected void initModel(List<Procedure> entries) {
        model.addAll(entries);
    }
    
}
