/*
 * #%L
 * cwf-api-core
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
package org.hspconsortium.cwf.api.practitioner;

import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hspconsortium.cwf.api.query.BaseResourceQuery;
import org.hspconsortium.cwf.fhir.common.FhirUtil;

import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;

/**
 * Practitioner search implementation using FHIR.
 */
public class PractitionerSearch extends BaseResourceQuery<Practitioner, PractitionerSearchCriteria> {
    
    
    public PractitionerSearch(IGenericClient fhirClient) {
        super(Practitioner.class, fhirClient);
    }
    
    @Override
    public void buildQuery(PractitionerSearchCriteria criteria, IQuery<?> query) {
        super.buildQuery(criteria, query);
        Identifier id = criteria.getDEA();
        
        if (id != null) {
            query.where(Practitioner.IDENTIFIER.exactly().systemAndIdentifier(id.getSystem(), id.getValue()));
        }
        
        id = criteria.getSSN();
        
        if (id != null) {
            query.where(Practitioner.IDENTIFIER.exactly().systemAndIdentifier(id.getSystem(), id.getValue()));
        }
        
        if (criteria.getGender() != null) {
            query.where(Practitioner.GENDER.exactly().code(criteria.getGender()));
        }
        
        if (criteria.getName() != null) {
            HumanName name = criteria.getName();
            
            if (!name.getFamily().isEmpty()) {
                query.where(Practitioner.FAMILY.matches().values(FhirUtil.toStringList(name.getFamily())));
            }
            
            if (!name.getGiven().isEmpty()) {
                query.where(Practitioner.GIVEN.matches().values(FhirUtil.toStringList(name.getGiven())));
            }
            
        }
    }
    
}
