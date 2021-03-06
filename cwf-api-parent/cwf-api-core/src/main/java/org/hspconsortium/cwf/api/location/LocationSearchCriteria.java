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
package org.hspconsortium.cwf.api.location;

import org.hl7.fhir.dstu3.model.Location.LocationStatus;
import org.hspconsortium.cwf.api.SearchCriteria;

/**
 * Represents search criteria supported by FHIR.
 */
public class LocationSearchCriteria extends SearchCriteria {
    
    
    private String type;
    
    private LocationStatus status;
    
    private String name;
    
    public LocationSearchCriteria() {
        super("Insufficent search parameters.");
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public LocationStatus getStatus() {
        return status;
    }
    
    public void setStatus(LocationStatus status) {
        this.status = status;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Returns true if the current criteria settings meet the minimum requirements for a search.
     * 
     * @return True if minimum search requirements have been met.
     */
    @Override
    public boolean isValid() {
        return true;
    }
    
    /**
     * Returns true if no criteria have been set.
     * 
     * @return True if no criteria have been set.
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty() && status == null && type == null;
    }
    
}
