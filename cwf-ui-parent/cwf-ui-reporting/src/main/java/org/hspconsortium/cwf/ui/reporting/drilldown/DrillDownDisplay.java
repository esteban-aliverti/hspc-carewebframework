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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.api.security.SecurityUtil;
import org.carewebframework.common.DateUtil;
import org.carewebframework.common.StrUtil;
import org.carewebframework.ui.zk.PopupDialog;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;

import org.hl7.fhir.dstu3.model.Identifier;
import org.hspconsortium.cwf.ui.reporting.Constants;

/**
 * Class which extends Popup. Subclasses should provide implementation of method addRows(). This is
 * the popup dialog that displays the detail information for a data object in a grid view. If
 * {@link SecurityUtil#hasDebugRole()}, the dataObject is interrogated and the classes get/bean
 * methods are invoked and displayed on the display as well.
 */
public class DrillDownDisplay extends PopupDialog {
    
    
    private class DebugLink extends DrillDownIconBase<Object> implements EventListener<Event> {
        
        
        private static final long serialVersionUID = 1L;
        
        private final String title;
        
        public DebugLink(Object dataObject, String title) {
            super(dataObject, DrillDownDisplay.class);
            this.title = title;
        }
        
        @Override
        public void onEvent(Event event) throws Exception {
            DrillDownDisplay ddd = new DrillDownDisplay(DrillDownDisplay.this, dataObject, title);
            ddd.setDebug(debug);
            ddd.owner = DrillDownDisplay.this;
            ddd.btnCloseAll.setVisible(true);
            ddd.show();
        }
        
        @Override
        protected void attachEventListener() {
            addEventListener(Events.ON_CLICK, this);
        }
    }
    
    private static final Log log = LogFactory.getLog(DrillDownDisplay.class);
    
    private static final long serialVersionUID = 1L;
    
    private Grid grid;
    
    private Column colLabel;
    
    private Column colValue;
    
    private Button btnCloseAll;
    
    private Object dataObject;
    
    private boolean resizing = false;
    
    private DrillDownDisplay owner = null;
    
    private boolean debug = SecurityUtil.hasDebugRole();
    
    /**
     * Subclasses a zul listbox for displaying multiple values in a single grid cell. Prevents
     * selection of entries in the list by resetting the state to no selection.
     *
     * @author dmartin
     */
    private class MultiListbox extends org.zkoss.zul.Listbox implements EventListener<Event> {
        
        
        private static final long serialVersionUID = 1L;
        
        private String defaultValue = null;
        
        /**
         * Creates the list box to fill the parent cell. Capture select and click events to undo any
         * selection that might occur.
         *
         * @param defaultValue The default value.
         */
        public MultiListbox(String defaultValue) {
            super();
            this.defaultValue = defaultValue;
            setWidth("100%");
            setHeight("100%");
            setStyle("background:white;border:none");
            setDisabled(true);
            addEventListener(Events.ON_SELECT, this);
            addEventListener(Events.ON_CLICK, this);
        }
        
        /**
         * Reset the list box state to no selection.
         */
        @Override
        public void onEvent(Event event) {
            if (getSelectedIndex() != -1) {
                setSelectedIndex(-1);
            }
            
            return;
        }
        
        /**
         * Add an item to the list box.
         *
         * @param value = Value of item to add.
         */
        public void addItem(String value) {
            Listitem item = new Listitem();
            item.setLabel(value);
            appendChild(item);
            
            if (value.equalsIgnoreCase(defaultValue)) {
                item.setStyle("font-style:italic");
            }
        }
    }
    
    /**
     * Create the dialog.
     *
     * @param parent The parent component.
     * @param dataObject The data object.
     * @param title The dialog title.
     */
    public DrillDownDisplay(Component parent, Object dataObject, String title) {
        super(parent, title);
        setDataObject(dataObject);
        setWidth("600px");
        
        try {
            Executions.createComponents(ZKUtil.loadCachedPageDefinition(Constants.RESOURCE_PREFIX + "drillDownDisplay.zul"),
                this, null);
            ZKUtil.wireController(this);
            adjustGrid();
        } catch (Exception e) {
            log.error("Error creating drilldown display dialog.", e);
        }
    }
    
    /**
     * Subclasses need implement the following method to add rows to the display.
     */
    public void addRows() {
    }
    
    /**
     * Add a link for drilldown of objects.
     *
     * @param dataObject Object for drilldown.
     * @param title Title for dialog.
     */
    private void addLink(Object dataObject, String title) {
        if (debugObject(dataObject, true)) {
            Component cell = getLastRow().getFirstChild();
            cell.appendChild(new DebugLink(dataObject, title));
        }
    }
    
    /**
     * Returns the last row added.
     *
     * @return The last row added.
     */
    private Row getLastRow() {
        return (Row) grid.getRows().getLastChild();
    }
    
    /**
     * When debug is true, dataObject is interrogated and the classes get/bean methods are invoked
     * and displayed on the display as well.
     *
     * @param dataObject Object to interrogate.
     * @param checkOnly If true, only checks to see if the object has additional debug info.
     * @return True if the object is a type for which additional debug info is available..
     */
    private boolean debugObject(Object dataObject, boolean checkOnly) {
        if (dataObject != null) {
            Row row;
            Class<?> clazz = dataObject.getClass();
            
            if (!checkOnly) {
                log.debug("Adding Verbose DrillDown Object Debug Information");
                addRow("-------DEBUG--------", clazz.getName());
                row = getLastRow();
                row.appendChild(new Label());
                row.setSclass(Constants.SCLASS_DRILLDOWN_GRID);
            }
            
            try {
                Object[] params = null;
                //Method[] methods = clazz.getDeclaredMethods();
                Method[] methods = clazz.getMethods();
                
                if (!(dataObject instanceof String)) {
                    for (Method method : methods) {
                        if (Modifier.PUBLIC == method.getModifiers()) {
                            // Assumes getter methods
                            if (method.getName().startsWith("get") && method.getGenericParameterTypes().length == 0) {
                                if (checkOnly) {
                                    return true;
                                }
                                
                                Object invokedObject = method.invoke(getDataObject(), params);
                                String methodName = method.getName();
                                addRowViaObject(methodName, invokedObject);
                                addLink(invokedObject, clazz.getName() + "." + methodName);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        
        return false;
    }
    
    /**
     * Show the popup dialog, populating it with detail information for the specified data object.
     */
    @Override
    public void show() {
        addRows();
        
        if (debug) {
            debugObject(dataObject, false);
        }
        
        if (grid.getRows().getChildren().size() > 20) {
            grid.setHeight("600px");
        }
        
        super.show();
    }
    
    /**
     * Adds a detail row for a generic object.
     *
     * @param header The row header.
     * @param value The object to add.
     */
    protected void addRowViaObject(String header, Object value) {
        if (value instanceof String) {
            addRow(header, (String) value);
        } else if (value instanceof Date) {
            addRow(header, (Date) value);
        } else if (value instanceof Identifier) {
            addRow(header, ((Identifier) value).getValue());
        } else {
            addRow(header, value == null ? "" : String.valueOf(value));
        }
    }
    
    /**
     * Add a row containing the specified header (left column) and value (right column). If
     * log.isDebugEnabled() is false then don't add row for empty or null values
     *
     * @param header Text for header column
     * @param value Text for value column
     */
    protected void addRow(String header, String value) {
        if ((value == null || value.length() == 0) && !debug) {
            return;
        }
        
        Label lbl = new Label();
        lbl.setValue(value);
        lbl.setTooltiptext(value);
        addRow(header, lbl);
    }
    
    /**
     * Add a row containing the specified header (left column) and value (right column).
     *
     * @param header Text for header column
     * @param value Text for value column
     */
    protected void addRow(String header, Integer value) {
        addRow(header, value == null ? "" : Integer.toString(value));
    }
    
    /**
     * Add a row containing the specified header (left column) and value (right column).
     *
     * @param header Text for header column
     * @param value Text for value column
     */
    protected void addRow(String header, Long value) {
        addRow(header, value == null ? "" : Long.toString(value));
    }
    
    /**
     * Add a row containing the specified header (left column) and value (right column).
     *
     * @param header Text for header column
     * @param value Date object
     */
    protected void addRow(String header, Date value) {
        try {
            addRow(header, DateUtil.formatDate(value));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            addRow(header, e.getMessage());
        }
    }
    
    /**
     * Add a row containing the specified header (left column) and value (right column).
     *
     * @param header Text for header column
     * @param value Concept object
     */
    protected void addRow(String header, Identifier value) {
        addRow(header, value == null ? "" : value.getValue().toString());
    }
    
    /**
     * Add a row containing a multi-valued list.
     *
     * @param header Text for header column
     * @param list Iterable object of string values
     * @param dflt Value to mark as default
     */
    protected void addRow(String header, Iterable<String> list, String dflt) {
        if (list == null) {
            return;
        }
        
        MultiListbox container = new MultiListbox(dflt);
        
        for (String val : list) {
            container.addItem(val);
        }
        
        if (container.getItems().size() > 0 || debug) {
            addRow(header, container);
        }
    }
    
    /**
     * Add a row containing the specified header (left column) and value container (right column).
     *
     * @param header Text for header column
     * @param container Object containing text value(s)
     */
    protected void addRow(String header, Component container) {
        Row row = new Row();
        grid.getRows().appendChild(row);
        Div div = new Div();
        Label label = new Label(header + ":");
        label.setMultiline(true);
        label.setMaxlength(40);
        label.setStyle("font-weight:bold;word-wrap:word-break");
        row.appendChild(div);
        row.appendChild(label);
        row.appendChild(container);
    }
    
    public void onClick$btnClose() {
        detach();
    }
    
    public void onClick$btnCloseAll() {
        detach();
        
        if (owner != null) {
            owner.onClick$btnCloseAll();
        }
    }
    
    @Override
    public void onResize(String newHeight, String newWidth) {
        if (!resizing) {
            try {
                resizing = true;
                adjustGrid();
            } finally {
                resizing = false;
            }
        }
    }
    
    private void adjustGrid() {
        int w = (StrUtil.extractInt(getWidth()) - 40) / 3;
        colLabel.setWidth(w + "px");
        colValue.setWidth((w * 2) + "px");
    }
    
    public Grid getGrid() {
        return grid;
    }
    
    public Object getDataObject() {
        return dataObject;
    }
    
    public void setDataObject(Object dataObject) {
        this.dataObject = dataObject;
    }
    
    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    public boolean isDebug() {
        return debug;
    }
    
}
