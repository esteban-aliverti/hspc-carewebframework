/*
 * #%L
 * cwf-ui-patientselection-core
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
package org.hspconsortium.cwf.ui.patientselection;

import org.hl7.fhir.dstu3.model.Patient;

/**
 * This interface must be implemented by any patient selector.
 */
public interface IPatientSelector {
    
    
    /**
     * Displays the patient selection dialog.
     * 
     * @return The selected patient at the time the dialog was closed. It will be null if no patient
     *         was selected when the dialog was closed or if the selection was canceled by the user.
     */
    Patient select();
}
