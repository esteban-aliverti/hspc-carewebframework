/*
 * #%L
 * cwf-api-patientlist
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
package org.hspconsortium.cwf.api.patientlist;

import org.hl7.fhir.dstu3.model.Patient;
import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.fhir.common.FhirUtil;

/**
 * A list item that is associates a patient object with some additional arbitrary displayable
 * information.
 */
public class PatientListItem implements Comparable<PatientListItem> {
    
    
    private final Patient patient;
    
    private final String info;
    
    /**
     * Creates a patient list item with no additional information.
     * 
     * @param patient A patient object.
     */
    public PatientListItem(Patient patient) {
        this(patient, null);
    }
    
    /**
     * Creates a patient list item with the specified displayable information.
     * 
     * @param patient A patient object.
     * @param info Displayable information to be associated with the patient.
     */
    public PatientListItem(Patient patient, String info) {
        this.patient = patient;
        this.info = info;
    }
    
    /**
     * Returns the patient associated with this item.
     * 
     * @return The associated patient.
     */
    public Patient getPatient() {
        return patient;
    }
    
    /**
     * Returns the displayable information associated with this item. May be null.
     * 
     * @return Displayable information.
     */
    public String getInfo() {
        return info;
    }
    
    /**
     * Selects the associated patient into the shared context.
     */
    public void select() {
        PatientContext.changePatient(patient);
    }
    
    /**
     * Two list items are considered equal if their associated patients are equal.
     */
    @Override
    public boolean equals(Object object) {
        return patient == null || !(object instanceof PatientListItem) ? false
                : FhirUtil.areEqual(patient, ((PatientListItem) object).patient);
    }
    
    /**
     * Used to sort patient list items alphabetically by patient name.
     */
    @Override
    public int compareTo(PatientListItem item) {
        String name1 = FhirUtil.formatName(patient.getName());
        String name2 = FhirUtil.formatName(item.patient.getName());
        return name1.compareToIgnoreCase(name2);
    }
}
