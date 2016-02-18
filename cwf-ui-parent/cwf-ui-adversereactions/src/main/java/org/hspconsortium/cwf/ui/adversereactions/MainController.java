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

import org.apache.commons.lang3.StringUtils;

import org.hspconsortium.cwf.ui.reporting.controller.ResourceListView;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance;
import ca.uhn.fhir.model.dstu2.resource.AllergyIntolerance.Reaction;
import ca.uhn.fhir.model.dstu2.valueset.AllergyIntoleranceStatusEnum;

/**
 * Controller for patient adverse reaction display.
 */
public class MainController extends ResourceListView<AllergyIntolerance, Reaction> {
    
    private static final long serialVersionUID = 1L;
    
    private static final Set<AllergyIntoleranceStatusEnum> exclusions = new HashSet<>();
    
    static {
        exclusions.add(AllergyIntoleranceStatusEnum.ENTERED_IN_ERROR);
        exclusions.add(AllergyIntoleranceStatusEnum.REFUTED);
        exclusions.add(AllergyIntoleranceStatusEnum.RESOLVED);
    }
    
    @Override
    protected void init() {
        setup(AllergyIntolerance.class, "Adverse Reactions", "Adverse Reaction Detail", "AllergyIntolerance?patient=#", 1,
            "Date", "Agent", "Manifestation");
        super.init();
    }
    
    @Override
    protected void render(Reaction adr, List<Object> columns) {
        columns.add(adr.getOnset());
        columns.add(adr.getSubstance().getCodingFirstRep().getDisplay());
        columns.add(getManifestations(adr.getManifestation()));
    }
    
    private String getManifestations(List<CodeableConceptDt> symptoms) {
        StringBuilder sb = new StringBuilder();
        
        for (CodeableConceptDt symptom : symptoms) {
            String sx = symptom.getText();
            
            if (StringUtils.isEmpty(sx)) {
                sx = symptom.getCodingFirstRep().getDisplay();
            }
            
            add(sx, sb);
        }
        
        return sb.toString();
    }
    
    private void add(String value, StringBuilder sb) {
        if (!StringUtils.isEmpty(value)) {
            sb.append(sb.length() == 0 ? "" : ", ").append(value);
        }
    }
    
    @Override
    protected void initModel(List<AllergyIntolerance> entries) {
        for (AllergyIntolerance adr : entries) {
            if (!exclusions.contains(adr.getStatusElement())) {
                for (Reaction reaction : adr.getReaction()) {
                    model.add(reaction);
                }
            }
        }
    }
    
}
