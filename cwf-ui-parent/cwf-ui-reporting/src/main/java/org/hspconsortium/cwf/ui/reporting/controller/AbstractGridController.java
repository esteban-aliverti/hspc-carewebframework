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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.api.query.IQueryService;
import org.hspconsortium.cwf.ui.reporting.Constants;
import org.carewebframework.common.MiscUtil;
import org.carewebframework.ui.zk.AbstractRowRenderer;
import org.carewebframework.ui.zk.HybridModel.IGrouper;
import org.carewebframework.ui.zk.RowComparator;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.GroupsModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

/**
 * This is a stateful controller that supports grid-based displays.
 *
 * @param <T> Query result class
 * @param <M> Model result class
 */
public abstract class AbstractGridController<T, M> extends AbstractMeshController<T, M> {
    
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(AbstractGridController.class);
    
    private RowRenderer<M> rowRenderer;
    
    // Autowired variables
    
    private Checkbox chkExpandAll;
    
    private Grid grid;
    
    public AbstractGridController(IQueryService<T> service, String labelPrefix, String propertyPrefix,
        String printStyleSheet) {
        super(service, labelPrefix, propertyPrefix, printStyleSheet);
    }
    
    public AbstractGridController(IQueryService<T> service, String labelPrefix, String propertyPrefix,
        String printStyleSheet, boolean patientAware, boolean autowireColumns, IGrouper<M, ?> grouper) {
        super(service, labelPrefix, propertyPrefix, printStyleSheet, patientAware, autowireColumns, grouper);
    }
    
    /**
     * Initializes the controller. Loads user preferences and properties.
     */
    @Override
    protected void initializeController() {
        setMeshElement(grid, "grid");
        super.initializeController();
        grid.setRowRenderer(rowRenderer);
        
        boolean expandAll = getPropertyValue(Constants.PROPERTY_ID_EXPAND_DETAIL, Boolean.class,
            chkExpandAll != null && chkExpandAll.isChecked());
            
        if (this.chkExpandAll != null) {
            this.chkExpandAll.setChecked(expandAll);
        }
        
        AbstractRowRenderer.setExpandDetail(grid, expandAll);
        
        if (grid.getColumns() != null) {
            RowComparator.autowireColumnComparators(grid.getColumns().getChildren());
        }
        
        grid.setVflex("1");
    }
    
    @Override
    protected void autowireColumns() {
        RowComparator.autowireColumnComparators(grid);
    }
    
    /**
     * Re-renders a previously rendered row.
     * 
     * @param row Row to re-render.
     */
    protected void rerender(Row row) {
        try {
            ZKUtil.detachChildren(row);
            grid.getRowRenderer().render(row, row.getValue(), row.getIndex());
        } catch (Exception e) {
            throw MiscUtil.toUnchecked(e);
        }
    }
    
    /**
     * Re-renders a model object.
     * 
     * @param object Model object
     */
    protected void rerender(M object) {
        for (Row row : grid.getRows().<Row> getChildren()) {
            if (object.equals(row.getValue())) {
                rerender(row);
                break;
            }
        }
    }
    
    @Override
    protected void print(Component root) {
        if (isPaging()) {
            grid.renderAll(); //TODO: this still doesn't properly render everything.
        }
        
        super.print(root);
    }
    
    /**
     * The event handler for checkbox events. Expand/close all the nodes in pharmacy orders when
     * check/uncheck checkbox.
     */
    public void onCheck$chkExpandAll() {
        if (log.isTraceEnabled()) {
            log.trace("onCheck : expand detail event fired");
        }
        
        boolean expandAll = this.chkExpandAll.isChecked();
        
        if (log.isDebugEnabled()) {
            log.debug("Expand Detail: " + expandAll);
        }
        
        AbstractRowRenderer.setExpandDetail(grid, expandAll);
    }
    
    /**
     * Set RowRenderer implementation
     *
     * @param rowRenderer RowRenderer
     */
    public void setRowRenderer(RowRenderer<M> rowRenderer) {
        this.rowRenderer = rowRenderer;
        
        if (grid != null) {
            grid.setRowRenderer(rowRenderer);
        }
    }
    
    @Override
    protected void setListModel(ListModel<M> model) {
        this.grid.setModel(model);
    }
    
    @Override
    protected void setGroupsModel(GroupsModel<M, ?, ?> model) {
        this.grid.setModel(model);
    }
    
    protected Grid getGrid() {
        return grid;
    }
}
