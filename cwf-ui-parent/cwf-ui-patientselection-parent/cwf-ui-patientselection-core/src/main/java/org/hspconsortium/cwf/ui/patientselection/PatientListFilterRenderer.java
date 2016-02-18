/*
 * #%L
 * cwf-ui-patientselection-core
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
package org.hspconsortium.cwf.ui.patientselection;

import org.hspconsortium.cwf.api.patientlist.AbstractPatientListFilter;
import org.carewebframework.ui.zk.AbstractListitemRenderer;

import org.zkoss.zul.Listitem;

/**
 * Renderer for filters.
 */
public class PatientListFilterRenderer extends AbstractListitemRenderer<AbstractPatientListFilter, Object> {
    
    private static final PatientListFilterRenderer instance = new PatientListFilterRenderer();
    
    /**
     * Return singleton instance.
     * 
     * @return Patient list filter renderer.
     */
    public static PatientListFilterRenderer getInstance() {
        return instance;
    }
    
    /**
     * Force singleton usage.
     */
    private PatientListFilterRenderer() {
        super("", null);
    }
    
    /**
     * Render a list item.
     * 
     * @param item The list item to render.
     * @param filter The associated patient list filter.
     */
    @Override
    public void renderItem(Listitem item, AbstractPatientListFilter filter) {
        createCell(item, filter.getName());
    }
    
}
