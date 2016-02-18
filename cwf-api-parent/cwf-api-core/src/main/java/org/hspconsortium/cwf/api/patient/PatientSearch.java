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
package org.hspconsortium.cwf.api.patient;

import org.hspconsortium.cwf.api.query.BaseResourceQuery;
import org.hspconsortium.cwf.fhir.common.FhirUtil;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;

/**
 * Patient search implementation using FHIR.
 */
public class PatientSearch extends BaseResourceQuery<Patient, PatientSearchCriteria> {
    
    public PatientSearch(IGenericClient fhirClient) {
        super(Patient.class, fhirClient);
    }
    
    @Override
    public void buildQuery(PatientSearchCriteria criteria, IQuery<Bundle> query) {
        super.buildQuery(criteria, query);
        
        if (criteria.getMRN() != null) {
            query.where(Patient.IDENTIFIER.exactly().identifier(criteria.getMRN()));
        }
        
        if (criteria.getSSN() != null) {
            query.where(Patient.IDENTIFIER.exactly().identifier(criteria.getSSN()));
        }
        
        if (criteria.getBirth() != null) {
            query.where(Patient.BIRTHDATE.exactly().day(criteria.getBirth()));
        }
        
        if (criteria.getGender() != null) {
            query.where(Patient.GENDER.exactly().code(criteria.getGender()));
        }
        
        if (criteria.getName() != null) {
            HumanNameDt name = criteria.getName();
            
            if (!name.getFamily().isEmpty()) {
                query.where(Patient.FAMILY.matches().values(FhirUtil.toStringList(name.getFamily())));
            }
            
            if (!name.getGiven().isEmpty()) {
                query.where(Patient.GIVEN.matches().values(FhirUtil.toStringList(name.getGiven())));
            }
            
        }
    }
    
}
