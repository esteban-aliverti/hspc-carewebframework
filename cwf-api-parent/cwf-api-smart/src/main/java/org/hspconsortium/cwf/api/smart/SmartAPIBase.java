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

import java.util.Map;

import ca.uhn.fhir.model.dstu2.resource.Patient;

import org.hspconsortium.cwf.api.patient.PatientContext;

/**
 * Adapter for SMART classic.
 */
public abstract class SmartAPIBase extends org.carewebframework.smart.SmartAPIBase {
    
    /**
     * API entry point. If a record id is specified, verifies that it is the same as the currently
     * selected patient.
     *
     * @param params The API parameters.
     * @return True if the params are valid.
     */
    public static boolean isValid(Map<String, String> params) {
        String patientId = params.get("record_id");
        
        if (patientId != null) {
            Patient patient = PatientContext.getActivePatient();
            
            if (!patientId.equals(patient.getId().getIdPart())) {
                return false;
            }
        }
        
        return true;
    }
    
    public SmartAPIBase(String pattern, String capability) {
        super(pattern, ContentType.RDF, capability);
    }
    
    /**
     * Validate the request.
     *
     * @param params The associated request parameters.
     * @return True if the request is valid.
     */
    @Override
    public boolean validateRequest(Map<String, String> params) {
        return isValid(params);
    }
    
}
