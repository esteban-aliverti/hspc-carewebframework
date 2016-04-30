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

import org.hl7.fhir.dstu3.model.Encounter;
import org.hspconsortium.cwf.api.DomainObject;

/**
 * Abstract base class for encounter-associated domain objects.
 */
public abstract class EncounterRelated extends DomainObject {
    
    
    private Encounter encounter;
    
    public EncounterRelated() {
        super();
    }
    
    public EncounterRelated(Encounter encounter) {
        super();
        this.encounter = encounter;
    }
    
    public Encounter getEncounter() {
        return encounter;
    }
    
    public void setEncounter(Encounter encounter) {
        this.encounter = encounter;
    }
    
}
