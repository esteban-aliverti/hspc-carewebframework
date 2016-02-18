/*
 * #%L
 * cwf-ui-familyhistory
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
package org.hspconsortium.cwf.ui.familyhistory;

import java.util.List;

import org.hspconsortium.cwf.ui.reporting.controller.ResourceListView;
import org.hspconsortium.cwf.fhir.common.FhirUtil;

import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;

import ca.uhn.fhir.model.dstu2.resource.FamilyMemberHistory;
import ca.uhn.fhir.model.dstu2.resource.FamilyMemberHistory.Condition;

/**
 * Controller for family history display.
 */
public class MainController extends ResourceListView<FamilyMemberHistory, FamilyMemberHistory> {
    
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void init() {
        setup(FamilyMemberHistory.class, "Family History", "Family History Detail", "FamilyMemberHistory?patient=#", 1,
            "Relation", "Condition", "Outcome", "Notes");
        super.init();
    }
    
    @Override
    protected void render(FamilyMemberHistory relation, List<Object> columns) {
        String value = FhirUtil.getDisplayValue(relation.getRelationship());
        columns.add(value);
        
        for (int i = 0; i < 3; i++) {
            Vlayout cmp = new Vlayout();
            columns.add(cmp);
            
            for (Condition condition : relation.getCondition()) {
                value = null;
                
                switch (i) {
                    case 0:
                        value = FhirUtil.getDisplayValue(condition.getCode());
                        break;
                        
                    case 1:
                        value = FhirUtil.getDisplayValue(condition.getOutcome());
                        break;
                        
                    case 2:
                        value = condition.getNote().getText();
                        break;
                }
                
                cmp.appendChild(new Label(value));
            }
            
        }
    }
    
    @Override
    protected void initModel(List<FamilyMemberHistory> entries) {
        model.addAll(entries);
    }
    
}
