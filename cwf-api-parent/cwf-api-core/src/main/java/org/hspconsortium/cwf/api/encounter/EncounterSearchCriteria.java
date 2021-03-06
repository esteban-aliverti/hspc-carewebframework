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
package org.hspconsortium.cwf.api.encounter;

import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hspconsortium.cwf.api.SearchCriteria;

/**
 * Represents search criteria supported by FHIR.
 */
public class EncounterSearchCriteria extends SearchCriteria {
    
    
    private Patient patient;
    
    private String type;
    
    private Period period;
    
    public EncounterSearchCriteria() {
        super("Insufficent search parameters.");
    }
    
    /**
     * Returns the patient criterion.
     * 
     * @return Patient criterion.
     */
    public Patient getPatient() {
        return patient;
    }
    
    /**
     * Sets the patient criterion.
     * 
     * @param patient Patient.
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * Returns true if the current criteria settings meet the minimum requirements for a search.
     * 
     * @return True if minimum search requirements have been met.
     */
    @Override
    public boolean isValid() {
        return super.isValid() || patient != null;
    }
    
    /**
     * Returns true if no criteria have been set.
     * 
     * @return True if no criteria have been set.
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty() && patient == null && type == null;
    }
    
    /**
     * Returns the time window within which to search.
     * 
     * @return Search time window.
     */
    public Period getPeriod() {
        return period;
    }
    
    /**
     * Sets the time window within which to search.
     * 
     * @param period Search time window.
     */
    public void setPeriod(Period period) {
        this.period = period;
    }
    
}
