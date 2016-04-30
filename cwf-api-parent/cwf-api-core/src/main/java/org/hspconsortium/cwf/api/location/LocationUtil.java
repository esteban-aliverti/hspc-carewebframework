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

import java.util.List;

import org.carewebframework.api.spring.SpringUtil;

import org.hl7.fhir.dstu3.model.Location;
import org.hspconsortium.cwf.api.query.IResourceQueryEx;

/**
 * Location-related utility functions.
 */
public class LocationUtil {
    
    
    /**
     * Returns a reference to the location search engine.
     * 
     * @return Location search engine.
     */
    @SuppressWarnings("unchecked")
    public static IResourceQueryEx<Location, LocationSearchCriteria> getSearchEngine() {
        return SpringUtil.getBean("locationSearchEngine", IResourceQueryEx.class);
    }
    
    /**
     * Perform a search based on given criteria.
     * 
     * @param criteria Search criteria.
     * @return Resources matching the search criteria.
     */
    public static List<Location> search(LocationSearchCriteria criteria) {
        return getSearchEngine().search(criteria);
    }
    
    /**
     * Enforce static class.
     */
    private LocationUtil() {
    }
}
