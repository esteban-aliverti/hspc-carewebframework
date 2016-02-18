/*
 * #%L
 * cwf-api-smart
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
package org.hspconsortium.cwf.api.smart;

import ca.uhn.fhir.model.dstu2.resource.Patient;

import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.carewebframework.smart.SmartContextBase;

/**
 * Implements SMART context scope "record".
 */
public class SmartContextRecord extends SmartContextBase {
    
    /**
     * Binds patient context changes to the SMART record context scope.
     */
    public SmartContextRecord() {
        super("record", "CONTEXT.CHANGED.Patient");
    }
    
    /**
     * Populate context map with information about currently selected patient.
     * 
     * @param context Context map to be populated.
     */
    @Override
    protected void updateContext(ContextMap context) {
        Patient patient = PatientContext.getActivePatient();
        
        if (patient != null) {
            context.put("full_name", FhirUtil.formatName(patient.getName()));
            context.put("id", patient.getId().getIdPart());
            context.put("param", "patientId");
        }
    }
    
}
