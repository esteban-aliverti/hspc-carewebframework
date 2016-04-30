/*
 * #%L
 * cwf-ui-adversereactions
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
package org.hspconsortium.cwf.ui.adversereactions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.carewebframework.common.StrUtil;

import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.hl7.fhir.dstu3.model.AllergyIntolerance.AllergyIntoleranceReactionComponent;
import org.hl7.fhir.dstu3.model.AllergyIntolerance.AllergyIntoleranceStatus;
import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.hspconsortium.cwf.ui.reporting.controller.ResourceListView;

/**
 * Controller for patient adverse reaction display.
 */
public class MainController extends ResourceListView<AllergyIntolerance, AllergyIntolerance> {
    
    
    private static final long serialVersionUID = 1L;
    
    private static final Set<AllergyIntoleranceStatus> exclusions = new HashSet<>();
    
    static {
        exclusions.add(AllergyIntoleranceStatus.ENTEREDINERROR);
        exclusions.add(AllergyIntoleranceStatus.REFUTED);
        exclusions.add(AllergyIntoleranceStatus.RESOLVED);
    }
    
    @Override
    protected void init() {
        setup(AllergyIntolerance.class, "Adverse Reactions", "Adverse Reaction Detail", "AllergyIntolerance?patient=#", 1,
            "Date^^min", "Agent", "Severity^^min", "Reaction");
        super.init();
    }
    
    @Override
    protected void render(AllergyIntolerance ai, List<Object> columns) {
        columns.add(ai.getRecordedDate());
        columns.add(ai.getSubstance());
        columns.add(ai.getCriticality());
        columns.add(getReactions(ai.getReaction()));
    }
    
    private String getReactions(List<AllergyIntoleranceReactionComponent> reactions) {
        StringBuilder sb = new StringBuilder();
        
        for (AllergyIntoleranceReactionComponent reaction : reactions) {
            StrUtil.strAppend(sb, FhirUtil.getDisplayValueForTypes(reaction.getManifestation(), ", "));
        }
        
        return sb.toString();
    }
    
    @Override
    protected void initModel(List<AllergyIntolerance> entries) {
        model.addAll(entries);
    }
    
}
