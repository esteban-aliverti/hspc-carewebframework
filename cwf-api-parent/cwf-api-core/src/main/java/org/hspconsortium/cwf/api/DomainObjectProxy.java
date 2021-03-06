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

/**
 * Abstract base class for proxied domain objects.
 * 
 * @param <T> Type of proxied object.
 */
public abstract class DomainObjectProxy<T> extends DomainObject {
    
    private final T proxiedObject;
    
    public DomainObjectProxy(String logicalId, T proxiedObject) {
        super(logicalId);
        this.proxiedObject = proxiedObject;
    }
    
    public DomainObjectProxy(DomainObjectProxy<T> src) {
        this(src.getId().getIdPart(), src.getProxiedObject());
    }
    
    public T getProxiedObject() {
        return proxiedObject;
    }
    
}
