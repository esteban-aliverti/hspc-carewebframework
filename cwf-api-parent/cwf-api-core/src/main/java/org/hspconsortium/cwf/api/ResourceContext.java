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

import org.carewebframework.api.context.IContextEvent;
import org.carewebframework.api.context.ManagedContext;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hspconsortium.cwf.fhir.common.BaseService;

/**
 * Abstract base class for implementing managed contexts for FHIR-based resources.
 *
 * @param <DomainClass> The FHIR resource class managed by this context.
 */
public abstract class ResourceContext<DomainClass extends IBaseResource> extends ManagedContext<DomainClass> {
    
    
    private BaseService fhirService;
    
    private final Class<DomainClass> domainClass;
    
    /**
     * Create a shared context with a specified initial state.
     * 
     * @param contextName Unique name for this context.
     * @param eventInterface The context change interface supported by this managed context.
     * @param initialContext The initial context state. May be null.
     */
    protected ResourceContext(String contextName, Class<DomainClass> domainClass,
        Class<? extends IContextEvent> eventInterface, DomainClass initialContext) {
        super(contextName, eventInterface, initialContext);
        this.domainClass = domainClass;
    }
    
    /**
     * Retrieve a resource by logical id.
     * 
     * @param logicalId The logical id.
     * @return The retrieved resource.
     */
    protected DomainClass getResource(String logicalId) {
        if (!logicalId.contains("/")) {
            logicalId = domainClass.getSimpleName() + "/" + logicalId;
        }
        
        return fhirService.getClient().fetchResourceFromUrl(domainClass, logicalId);
    }
    
    /**
     * Request a context change given the resources logical id.
     * 
     * @param logicalId Logical id of the resource.
     */
    protected void requestContextChange(String logicalId) {
        requestContextChange(logicalId == null ? null : getResource(logicalId));
    }
    
    /**
     * Returns the FHIR service.
     * 
     * @return The FHIR service.
     */
    public BaseService getFhirService() {
        return fhirService;
    }
    
    /**
     * Sets the FHIR service.
     * 
     * @param fhirService The FHIR service.
     */
    public void setFhirService(BaseService fhirService) {
        this.fhirService = fhirService;
    }
    
}
