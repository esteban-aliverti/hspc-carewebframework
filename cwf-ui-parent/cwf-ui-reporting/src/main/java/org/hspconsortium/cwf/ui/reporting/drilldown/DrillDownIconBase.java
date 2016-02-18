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

import org.hspconsortium.cwf.ui.reporting.Constants;

import org.zkoss.util.resource.Labels;

/**
 * A clickable icon for invoking a drill down dialog on a single entity. All entity types are
 * supported by specifying a drillDownDisplayClass.
 *
 * @param <T> Class of drill down data object.
 */
public abstract class DrillDownIconBase<T> extends org.zkoss.zul.Image {
    
    private static final String TOOLTIP = "resultsdisplay.drilldownimage.tooltip";
    
    private static final long serialVersionUID = 1L;
    
    private static final String[] stockIcons = new String[] { "drilldown", "textreport", "image" };
    
    protected final T dataObject;
    
    protected final Class<?> drillDownDisplayClass;
    
    public enum StockIcons {
        standard, report, image
    };
    
    /**
     * Constructor for DrillDownLink
     *
     * @param dataObject Domain data object to interrogate
     * @param drillDownDisplayClass Declaration of the class to use to interrogate the dataObject
     */
    public DrillDownIconBase(T dataObject, Class<?> drillDownDisplayClass) {
        this(dataObject, drillDownDisplayClass, StockIcons.standard, null);
    }
    
    /**
     * @param dataObject Data Object
     * @param drillDownDisplayClass Drill down display class
     * @param stockIcon StockIcons
     * @param tooltip Tooltip text
     */
    public DrillDownIconBase(T dataObject, Class<?> drillDownDisplayClass, StockIcons stockIcon,
        String tooltip) {
        this(dataObject, drillDownDisplayClass, Constants.RESOURCE_PREFIX + stockIcons[stockIcon.ordinal()] + ".png",
                Constants.RESOURCE_PREFIX + stockIcons[stockIcon.ordinal()] + "2.png", tooltip);
    }
    
    /**
     * @param dataObject Data Object
     * @param drillDownDisplayClass Drill down display class
     * @param glyph1 Icon mouseout
     * @param glyph2 Icon mouseover
     * @param tooltip tooltip text
     */
    public DrillDownIconBase(T dataObject, Class<?> drillDownDisplayClass, String glyph1,
        String glyph2, String tooltip) {
        this.dataObject = dataObject;
        this.drillDownDisplayClass = drillDownDisplayClass;
        setSrc(glyph1);
        setHover(glyph2 == null ? glyph1 : glyph2);
        setTooltiptext(tooltip != null ? tooltip : Labels.getLabel(TOOLTIP));
        setSclass(Constants.SCLASS_DRILLDOWN_LINK);
        attachEventListener();
    }
    
    protected abstract void attachEventListener();
}
