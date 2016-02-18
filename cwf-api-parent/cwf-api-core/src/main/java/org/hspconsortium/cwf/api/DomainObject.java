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
package org.hspconsortium.cwf.api;

import ca.uhn.fhir.model.primitive.IdDt;

import org.hspconsortium.cwf.fhir.common.IReferenceable;

/**
 * Abstract base class for domain objects.
 */
public abstract class DomainObject implements IReferenceable {
    
    private final IdDt id = new IdDt();
    
    public DomainObject() {
        super();
    }
    
    public DomainObject(String id) {
        super();
        setId(id);
    }
    
    @Override
    public IdDt getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id.setValue(id);
    }
}
