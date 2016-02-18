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

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * Renderer for filters.
 */
public class FilterRenderer implements ListitemRenderer<AbstractPatientListFilter> {
    
    private static final FilterRenderer instance = new FilterRenderer();
    
    /**
     * Return singleton instance.
     * 
     * @return The filter renderer.
     */
    public static FilterRenderer getInstance() {
        return instance;
    }
    
    /**
     * Force singleton usage.
     */
    private FilterRenderer() {
        super();
    }
    
    /**
     * Render a list item.
     * 
     * @param item The list item to render.
     * @param filter The associated PatientListFilter object.
     * @param index The item index.
     */
    @Override
    public void render(Listitem item, AbstractPatientListFilter filter, int index) throws Exception {
        item.setValue(filter);
        addCell(item, filter.getName());
    }
    
    /**
     * Add a cell to the list item.
     * 
     * @param item List item to receive the cell.
     * @param label Text label for the cell.
     */
    private void addCell(Listitem item, String label) {
        Listcell cell = new Listcell(label);
        cell.setTooltiptext(label);
        item.appendChild(cell);
    }
}
