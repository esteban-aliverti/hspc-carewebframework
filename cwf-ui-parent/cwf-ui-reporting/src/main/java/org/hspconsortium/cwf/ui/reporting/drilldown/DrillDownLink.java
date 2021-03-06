/*
 * #%L
 * cwf-ui-reporting
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
package org.hspconsortium.cwf.ui.reporting.drilldown;

/**
 * A clickable icon for invoking a drill down dialog on a single entity. All entity types are
 * supported by specifying a drillDownDisplayClass.
 *
 * @param <T> Class of drill down data object.
 */
public class DrillDownLink<T> extends DrillDownIconBase<T> {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructor for DrillDownLink
     *
     * @param dataObject Domain data object to interrogate
     * @param drillDownDisplayClass Declaration of the class to use to interrogate the dataObject
     */
    public DrillDownLink(T dataObject, Class<?> drillDownDisplayClass) {
        super(dataObject, drillDownDisplayClass, StockIcons.standard, null);
    }
    
    /**
     * @param dataObject Data Object
     * @param drillDownDisplayClass Drill down display class
     * @param stockIcon StockIcons
     * @param tooltip Tooltip text
     */
    public DrillDownLink(T dataObject, Class<?> drillDownDisplayClass, StockIcons stockIcon,
        String tooltip) {
        super(dataObject, drillDownDisplayClass, stockIcon, tooltip);
    }
    
    /**
     * @param dataObject Data Object
     * @param drillDownDisplayClass Drill down display class
     * @param glyph1 Icon mouseout
     * @param glyph2 Icon mouseover
     * @param tooltip tooltip text
     */
    public DrillDownLink(T dataObject, Class<?> drillDownDisplayClass, String glyph1, String glyph2,
        String tooltip) {
        super(dataObject, drillDownDisplayClass, glyph1, glyph2, tooltip);
    }
    
    @Override
    protected void attachEventListener() {
        new DrillDownListener<T>(this, dataObject, drillDownDisplayClass);
    }
    
}
