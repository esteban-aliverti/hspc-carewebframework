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
package org.hspconsortium.cwf.ui.reporting.controller;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.api.property.PropertyUtil;
import org.carewebframework.api.query.DateQueryFilter;
import org.carewebframework.api.query.DateQueryFilter.DateType;
import org.carewebframework.api.query.DateQueryFilter.IDateTypeExtractor;
import org.carewebframework.api.query.IQueryService;
import org.hspconsortium.cwf.ui.reporting.Constants;
import org.hspconsortium.cwf.ui.reporting.Util;
import org.carewebframework.common.DateRange;
import org.carewebframework.ui.zk.DateRangePicker;
import org.carewebframework.ui.zk.HybridModel;
import org.carewebframework.ui.zk.HybridModel.IGrouper;
import org.carewebframework.ui.zk.ListUtil;
import org.carewebframework.ui.zk.ZKUtil;

import org.springframework.beans.BeanUtils;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.GroupsModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.impl.MeshElement;

/**
 * This is a stateful controller that supports plugins that use a list or grid model and background
 * thread for data retrieval. It supports paging vs ROD-based views.
 * 
 * @param <T> Query result class
 * @param <M> Model result class
 */
public abstract class AbstractMeshController<T, M> extends AbstractServiceController<T, M>implements IDateTypeExtractor<M> {
    
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(AbstractMeshController.class);
    
    private static final String ATTR_ROD_SIZE = "org.zkoss.zul.%.initRodSize";
    
    private static final String ATTR_ROD = "org.zkoss.zul.%.rod";
    
    // These components are auto-wired by the controller.
    
    private DateRangePicker dateRangePicker;
    
    private Combobox dateTypePicker;
    
    private Component printRoot;
    
    private Button btnPagingToggle;
    
    // --- End of auto-wired section
    
    private HybridModel<M, Object> listModel;
    
    private MeshElement meshElement;
    
    private String rodType;
    
    private int rodInitSize = 5;
    
    private boolean isPaging = true;
    
    private DateQueryFilter<M> dateFilter;
    
    // Maximum number of rows for paging view.
    private int pageSize = 30;
    
    private final boolean autowireColumns;
    
    private final String propertyPrefix;
    
    private final String printStyleSheet;
    
    private final IGrouper<M, ?> grouper;
    
    /**
     * Create the controller.
     *
     * @param service The is the data query service.
     * @param labelPrefix Prefix used to resolve label id's with placeholders.
     * @param propertyPrefix Prefix for property names.
     * @param printStyleSheet Optional style sheet to apply when printing.
     */
    public AbstractMeshController(IQueryService<T> service, String labelPrefix, String propertyPrefix,
        String printStyleSheet) {
        this(service, labelPrefix, propertyPrefix, printStyleSheet, true, false, null);
    }
    
    /**
     * Create the controller.
     *
     * @param service The is the data query service.
     * @param labelPrefix Prefix used to resolve label id's with placeholders.
     * @param propertyPrefix Prefix for property names.
     * @param printStyleSheet Optional style sheet to apply when printing.
     * @param patientAware If true, uses patient context.
     */
    public AbstractMeshController(IQueryService<T> service, String labelPrefix, String propertyPrefix,
        String printStyleSheet, boolean patientAware) {
        this(service, labelPrefix, propertyPrefix, printStyleSheet, patientAware, false, null);
    }
    
    /**
     * Create the controller.
     *
     * @param service The is the data query service.
     * @param labelPrefix Prefix used to resolve label id's with placeholders.
     * @param propertyPrefix Prefix for property names.
     * @param printStyleSheet Optional style sheet to apply when printing.
     * @param patientAware If true, uses patient context.
     * @param autowireColumns If true, columns are auto-wired with sort comparators.
     */
    public AbstractMeshController(IQueryService<T> service, String labelPrefix, String propertyPrefix,
        String printStyleSheet, boolean patientAware, boolean autowireColumns) {
        this(service, labelPrefix, propertyPrefix, printStyleSheet, patientAware, autowireColumns, null);
    }
    
    /**
     * Create the controller.
     *
     * @param service The is the data query service.
     * @param labelPrefix Prefix used to resolve label id's with placeholders.
     * @param propertyPrefix Prefix for property names.
     * @param printStyleSheet Optional style sheet to apply when printing.
     * @param patientAware If true, uses patient context.
     * @param autowireColumns If true, columns are auto-wired with sort comparators.
     * @param grouper The grouper implementation, or null if the data is not grouped.
     */
    @SuppressWarnings("unchecked")
    public AbstractMeshController(IQueryService<T> service, String labelPrefix, String propertyPrefix,
        String printStyleSheet, boolean patientAware, boolean autowireColumns, IGrouper<M, ?> grouper) {
        super(service, patientAware, labelPrefix);
        this.propertyPrefix = propertyPrefix;
        this.autowireColumns = autowireColumns;
        
        if (printStyleSheet != null && !printStyleSheet.startsWith("~./")) {
            printStyleSheet = ZKUtil.getResourcePath(getClass()) + printStyleSheet;
        }
        
        this.printStyleSheet = printStyleSheet;
        this.grouper = grouper;
        this.listModel = new HybridModel<M, Object>((IGrouper<M, Object>) grouper);
    }
    
    /**
     * Subclass should implement to set list model into appropriate component.
     *
     * @param model The list model.
     */
    protected abstract void setListModel(ListModel<M> model);
    
    /**
     * Subclass should implement to set groups model into appropriate component.
     *
     * @param model The groups model.
     */
    protected abstract void setGroupsModel(GroupsModel<M, ?, ?> model);
    
    /**
     * Returns the date for the given result for filtering purposes.
     *
     * @param result Result from which to extract a date.
     * @param dateType The date type.
     * @return The extracted date.
     */
    @Override
    public abstract Date getDateByType(M result, DateType dateType);
    
    /**
     * Sets the mesh element that consumes the list model (a grid or listbox).
     *
     * @param meshElement The mesh element.
     * @param rodType The ROD setting.
     */
    /*package*/void setMeshElement(MeshElement meshElement, String rodType) {
        this.meshElement = meshElement;
        this.rodType = rodType;
        setHideOnShowMessage(meshElement);
        
        if (autowireColumns) {
            autowireColumns();
        }
    }
    
    /**
     * Implement to auto-wire column comparators.
     */
    protected abstract void autowireColumns();
    
    protected IGrouper<M, ?> getGrouper() {
        return grouper;
    }
    
    /**
     * The list model has been updated. If the model is empty or null, displays the no data message.
     * This ultimately calls the abstract setModel method to allow the subclass to handle the
     * updated model.
     *
     * @param model The hybrid model.
     */
    private void updateModel(HybridModel<M, ?> model) {
        if (model == null || model.isEmpty()) {
            String msg = getLabel(Constants.LABEL_ID_NO_DATA);
            log.trace(msg);
            showMessage(msg);
        } else {
            showMessage(null);
        }
        
        if (model.isGrouped()) {
            setGroupsModel(model);
        } else {
            setListModel(model);
        }
        
        if (isPaging) {
            meshElement.setActivePage(0);
        }
    }
    
    /**
     * Returns the current list model.
     *
     * @return The list model.
     */
    protected ListModel<M> getListModel() {
        return listModel;
    }
    
    /**
     * Retrieves a property value of the specified data type. It examines the property value for a
     * type-compatible value. Failing that, it returns the specified default value.
     *
     * @param propName Name of property from which to retrieve the value.
     * @param clazz Expected data type of the property value.
     * @param dflt Default value to use if a suitable one cannot be found.
     * @return The value.
     */
    @SuppressWarnings("unchecked")
    protected <V> V getPropertyValue(String propName, Class<V> clazz, V dflt) {
        V value = null;
        
        if (propName != null) {
            propName = propName.replace("%", propertyPrefix == null ? "" : propertyPrefix);
            String val = StringUtils.trimToNull(PropertyUtil.getValue(propName));
            
            if (log.isDebugEnabled()) {
                log.debug("Property " + propName + " value: " + val);
            }
            
            if (clazz == String.class) {
                value = (V) (val);
            } else {
                Method method = BeanUtils.findMethod(clazz, "valueOf", String.class);
                
                if (method != null && method.getReturnType() == clazz) {
                    value = (V) parseString(method, val, null);
                }
            }
        }
        
        return value == null ? dflt : value;
    }
    
    /**
     * Uses the valueOf method in the target type class to convert one of two candidate values to
     * the target type. Failing that, it returns null.
     *
     * @param method The valueOf method in the target class.
     * @param value1 The first candidate value to try.
     * @param value2 The second candidate value to try.
     * @return The converted value if successful; null if not.
     */
    private Object parseString(Method method, String value1, String value2) {
        try {
            return method.invoke(null, value1);
        } catch (Exception e) {
            return value2 == null ? null : parseString(method, value2, null);
        }
    }
    
    /**
     * Initializes Controller.
     */
    @Override
    protected void initializeController() {
        super.initializeController();
        rodInitSize = getPropertyValue(Constants.PROPERTY_ID_ROD_SIZE, Integer.class, rodInitSize);
        pageSize = getPropertyValue(Constants.PROPERTY_ID_MAX_ROWS, Integer.class, pageSize);
        onUpdatePaging();
        
        if (dateRangePicker != null) {
            String deflt = getPropertyValue(Constants.PROPERTY_ID_DATE_RANGE, String.class, "Last Two Years");
            dateRangePicker.setSelectedItem(dateRangePicker.findMatchingItem(deflt));
            initDateFilter().setDateRange(dateRangePicker.getSelectedRange());
        }
        
        if (dateTypePicker != null) {
            for (DateType dt : DateType.values()) {
                String lbl = getLabel(Constants.LABEL_ID_SORT_MODE.replace("$", dt.name().toLowerCase()));
                Comboitem item = new Comboitem(lbl);
                item.setValue(dt);
                dateTypePicker.appendChild(item);
            }
            DateType sortModePref = getPropertyValue(Constants.PROPERTY_ID_SORT_MODE, DateType.class, DateType.MEASURED);
            int idx = ListUtil.findComboboxData(dateTypePicker, sortModePref);
            dateTypePicker.setSelectedIndex(idx == -1 ? 0 : idx);
            dateTypePicker.setReadonly(true);
            initDateFilter().setDateType(sortModePref);
        }
        
        if (dateFilter != null) {
            registerQueryFilter(dateFilter);
        }
        
    }
    
    private DateQueryFilter<M> initDateFilter() {
        if (dateFilter == null) {
            dateFilter = new DateQueryFilter<M>(this);
        }
        
        return dateFilter;
    }
    
    /**
     * Updates component states according to paging mode.
     */
    public void onUpdatePaging() {
        showBusy(null);
        
        if (meshElement != null) {
            if (isPaging) {
                meshElement.setMold("paging");
                meshElement.setPageSize(pageSize);
                activateROD(false);
            } else {
                meshElement.setMold(null);
                activateROD(true);
            }
        }
        
        if (btnPagingToggle != null) {
            btnPagingToggle.setLabel(getLabel(isPaging ? Constants.LABEL_ID_PAGE_OFF : Constants.LABEL_ID_PAGE_ON));
        }
    }
    
    /**
     * Activates/deactivates ROD support. Note that setting the rodInitSize to zero or less will
     * always inhibit ROD support.
     *
     * @param activate True activates ROD; false deactivates it.
     */
    private void activateROD(boolean activate) {
        meshElement.setAttribute(ATTR_ROD.replace("%", rodType), activate && rodInitSize > 0);
        meshElement.setAttribute(ATTR_ROD_SIZE.replace("%", rodType), rodInitSize);
    }
    
    /**
     * Submits a data fetch request in the background.
     */
    @Override
    protected void fetchData() {
        listModel.clear();
        super.fetchData();
    }
    
    /**
     * Event handler to handle changes in the DateType of a query
     */
    public void onSelect$dateTypePicker() {
        DateType dateType = getDateType();
        
        log.trace("Handling onSelect of dateTypePicker Combobox");
        
        if (log.isDebugEnabled()) {
            log.debug("dateTypePicker value: " + dateType);
        }
        
        dateFilter.setDateType(dateType);
    }
    
    /**
     * The event handler for DatePicker events. Compares DatePicker range against cached date range.
     * If out of range, {@link #fetchData()} is called and cache is refreshed
     */
    public void onSelectRange$dateRangePicker() {
        DateRange dateRange = getDateRange();
        
        if (log.isTraceEnabled()) {
            log.trace("DatePicker range: " + dateRange);
        }
        
        dateFilter.setDateRange(dateRange);
    }
    
    @Override
    protected void modelChanged(List<M> filteredModel) {
        listModel.clear();
        listModel.addAll(filteredModel);
        updateModel(listModel);
    }
    
    /**
     * Re-renders the current model.
     */
    protected void rerender() {
        applyFilters();
    }
    
    /**
     * Returns date range from picker.
     *
     * @return The date range.
     */
    protected DateRange getDateRange() {
        return dateRangePicker == null ? null : dateRangePicker.getSelectedRange();
    }
    
    /**
     * Returns date type from picker.
     *
     * @return The date type.
     */
    protected DateType getDateType() {
        Comboitem item = dateTypePicker == null ? null : dateTypePicker.getSelectedItem();
        return item == null ? dateFilter.getDateType() : (DateType) item.getValue();
    }
    
    /**
     * Invoke refresh upon refresh button click.
     */
    public void onClick$btnRefresh() {
        refresh();
    }
    
    /**
     * Paging Toggle
     */
    public void onClick$btnPagingToggle() {
        log.trace("Paging Toggle Button");
        isPaging = !isPaging;
        showBusy(getLabel(Constants.LABEL_ID_WAITING));
        Events.echoEvent("onUpdatePaging", root, isPaging);
    }
    
    protected void print(Component root) {
        String printTitle = getLabel(Constants.LABEL_ID_TITLE);
        Util.print(root == null ? meshElement.getParent() : root, printTitle, patientAware ? "patient" : "user",
            printStyleSheet, false);
    };
    
    public void onClick$btnPrint() {
        print(printRoot);
    }
    
    public boolean isPaging() {
        return isPaging;
    }
    
    public void setPaging(boolean value) {
        if (isPaging != value) {
            isPaging = value;
            
            if (meshElement != null) {
                onUpdatePaging();
            }
        }
    }
    
    public boolean isMultiple() {
        return listModel.isMultiple();
    }
    
    public void setMultiple(boolean multiple) {
        listModel.setMultiple(multiple);
    }
    
}
