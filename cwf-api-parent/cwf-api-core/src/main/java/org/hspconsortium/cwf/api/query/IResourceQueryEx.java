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
package org.hspconsortium.cwf.api.query;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;

import ca.uhn.fhir.rest.gclient.IQuery;

/**
 * Extended resource query interface.
 * 
 * @param <R> The resource class
 * @param <C> The criteria class.
 */
public interface IResourceQueryEx<R extends IBaseResource, C> extends IResourceQuery<R, C> {
    
    
    /**
     * Alternative method for performing a search that allows for external configuration of the
     * query object.
     * 
     * @param query The query object.
     * @return List of matching resources. May return null to indicate no matches.
     */
    List<R> search(IQuery<?> query);
    
    /**
     * Creates an empty query object for this resource class.
     * 
     * @return The newly created query object.
     */
    IQuery<?> createQuery();
}
