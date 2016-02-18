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

import org.apache.http.impl.client.CloseableHttpClient;

import org.carewebframework.api.spring.SpringUtil;
import org.hspconsortium.cwf.fhir.client.FhirContext;
import org.hspconsortium.cwf.fhir.client.HttpClientProxy;

import org.hl7.fhir.instance.model.api.IBaseResource;

import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.UriDt;
import ca.uhn.fhir.rest.client.GenericClient;
import ca.uhn.fhir.rest.client.IGenericClient;

/**
 * FHIR client utility methods.
 */
public class ClientUtil {
    
    public static FhirContext getFhirContext() {
        return SpringUtil.getAppContext().getBean("fhirContext", FhirContext.class);
    }
    
    public static void registerHttpClient(String pattern, CloseableHttpClient client) {
        ((HttpClientProxy) getFhirContext().getRestfulClientFactory().getHttpClient()).registerHttpClient(pattern, client);
    }
    
    public static IGenericClient getFhirClient() {
        return SpringUtil.getAppContext().getBean("fhirClient", IGenericClient.class);
    }
    
    /**
     * Returns the default FHIR service root url.
     * 
     * @return Default FHIR service root url.
     */
    public static String getServiceRoot() {
        return ((GenericClient) getFhirClient()).getUrlBase();
    }
    
    /**
     * For urls without a service root, prepends the default service root.
     * 
     * @param url URL to expand.
     * @return URL with a service root prepended.
     */
    public static String expandURL(String url) {
        return url.matches("^.+:/") ? url : concatPath(getServiceRoot(), url);
    }
    
    /**
     * Concatenates a path fragment to a root path. Ensures that a single "/" character separates
     * the two parts.
     * 
     * @param root The root path.
     * @param fragment The path fragment.
     * @return The concatenated result.
     */
    public static String concatPath(String root, String fragment) {
        while (root.endsWith("/")) {
            root = root.substring(0, root.length() - 1);
        }
        
        while (fragment.startsWith("/")) {
            fragment = fragment.substring(1);
        }
        
        return root + "/" + fragment;
    }
    
    /**
     * Returns a resource of the specified type given a resource reference. If the resource has not
     * been previously fetched, it will be fetched from the server. If the referenced resource is
     * not of the specified type, null is returned.
     * 
     * @param reference A resource reference.
     * @param clazz The desired resource class.
     * @return The corresponding resource.
     */
    @SuppressWarnings("unchecked")
    public static <T extends IBaseResource> T getResource(ResourceReferenceDt reference, Class<T> clazz) {
        IBaseResource resource = getResource(reference);
        return clazz.isInstance(resource) ? (T) resource : null;
    }
    
    /**
     * Returns a resource given a resource reference. If the resource has not been previously
     * fetched, it will be fetched from the server.
     * 
     * @param reference A resource reference.
     * @return The corresponding resource.
     */
    public static IBaseResource getResource(ResourceReferenceDt reference) {
        if (reference.isEmpty()) {
            return null;
        }
        
        if (reference.getResource() != null) {
            return reference.getResource();
        }
        
        IdDt resourceId = reference.getReference();
        
        if (resourceId == null) {
            throw new IllegalStateException("Reference has no resource ID defined");
        }
        
        String resourceUrl = expandURL(resourceId.getValue());
        IBaseResource resource = getFhirClient().read(new UriDt(resourceUrl));
        reference.setResource(resource);
        return resource;
    }
    
    /**
     * Enforce static class.
     */
    private ClientUtil() {
    };
}
