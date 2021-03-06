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

import org.hl7.fhir.dstu3.model.BaseResource;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hspconsortium.cwf.api.SearchCriteria;
import org.hspconsortium.cwf.fhir.common.FhirUtil;

import ca.uhn.fhir.rest.client.IGenericClient;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.StringClientParam;

/**
 * Base class for encapsulating the HAPI-FHIR implementation of a resource query.
 * <p>
 * There are two ways to use this class. The first is to create and populate a <b>SearchCriteria</b>
 * instance for the given resource type and pass this to the <b>search</b> method to perform the
 * query. The second is to call the <b>createQuery</b> method to get a query object, populate the
 * query object directly with your search criteria, and pass this to the <b>search</b> method to
 * perform the query.
 * 
 * @param <R> The resource class
 * @param <C> The criteria class.
 */
public class BaseResourceQuery<R extends IBaseResource, C extends SearchCriteria> implements IResourceQueryEx<R, C> {
    
    
    private final IGenericClient fhirClient;
    
    private final Class<R> resourceClass;
    
    public BaseResourceQuery(Class<R> resourceClass, IGenericClient fhirClient) {
        this.resourceClass = resourceClass;
        this.fhirClient = fhirClient;
    }
    
    /**
     * Validates the criteria settings and then transfers them to the query object. This method
     * transfers the base criteria only. Override to transfer additional criteria that are specific
     * to the resource type.
     * 
     * @param criteria Research search criteria.
     * @param query The query object.
     */
    protected void buildQuery(C criteria, IQuery<?> query) {
        criteria.validate();
        
        if (criteria.getMaximum() > 0) {
            query.count(criteria.getMaximum());
        }
        
        if (criteria.getId() != null) {
            query.where(new StringClientParam(BaseResource.SP_RES_ID).matches().value(criteria.getId()));
        }
    }
    
    /**
     * Creates an empty query object for this resource class.
     * 
     * @return The newly created query object.
     */
    @Override
    public IQuery<?> createQuery() {
        return fhirClient.search().forResource(resourceClass);
    }
    
    /**
     * Search for matching resources.
     * 
     * @param criteria Resource search criteria.
     * @return List of matching resources. May return null to indicate no matches.
     */
    @Override
    public List<R> search(C criteria) {
        IQuery<?> query = createQuery();
        buildQuery(criteria, query);
        return search(query);
    }
    
    /**
     * Alternative method for performing a search that allows for external configuration of the
     * query object.
     * 
     * @param query The query object.
     * @return List of matching resources. May return null to indicate no matches.
     */
    @Override
    public List<R> search(IQuery<?> query) {
        return FhirUtil.getEntries(query.returnBundle(Bundle.class).execute(), resourceClass);
    }
}
