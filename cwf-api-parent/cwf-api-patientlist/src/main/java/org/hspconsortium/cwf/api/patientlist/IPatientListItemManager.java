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

/**
 * Defines methods for managing items within a user-manageable list.
 * 
 */
public interface IPatientListItemManager {
    
    /**
     * Adds a patient list item to the list.
     * 
     * @param item Patient list item to add.
     */
    void addItem(PatientListItem item);
    
    /**
     * Removes a patient list item from the list.
     * 
     * @param item Patient list item to remove.
     */
    void removeItem(PatientListItem item);
    
    /**
     * Saves any modifications to the list of patient items.
     */
    void save();
}
