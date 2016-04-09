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

import java.util.ArrayList;
import java.util.List;

import org.carewebframework.api.query.IQueryService;
import org.carewebframework.common.MiscUtil;
import org.carewebframework.ui.zk.HybridModel.IGrouper;
import org.carewebframework.ui.zk.ListUtil;
import org.carewebframework.ui.zk.RowComparator;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.GroupsModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * This is a stateful controller that supports list-based displays.
 *
 * @param <T> Query result class
 * @param <M> Model result class
 */
public abstract class AbstractListController<T, M> extends AbstractMeshController<T, M> {
    
    private static final long serialVersionUID = 1L;
    
    private ListitemRenderer<M> itemRenderer;
    
    // Auto-wired variables.
    
    protected Listbox listBox;
    
    public AbstractListController(IQueryService<T> service, String labelPrefix, String propertyPrefix,
        String printStyleSheet) {
        super(service, labelPrefix, propertyPrefix, printStyleSheet);
    }
    
    public AbstractListController(IQueryService<T> service, String labelPrefix, String propertyPrefix,
        String printStyleSheet, boolean patientAware, boolean autowireColumns, IGrouper<M, ?> grouper) {
        super(service, labelPrefix, propertyPrefix, printStyleSheet, patientAware, autowireColumns, grouper);
    }
    
    /**
     * Initializes the controller. Loads user preferences and properties.
     */
    @Override
    protected void initializeController() {
        setMeshElement(listBox, "list");
        super.initializeController();
        listBox.setItemRenderer(itemRenderer);
        setMultiple(listBox.isMultiple());
        
        if (listBox.getListhead() != null) {
            RowComparator.autowireColumnComparators(listBox.getListhead().getChildren());
        }
    }
    
    @Override
    protected void autowireColumns() {
        RowComparator.autowireColumnComparators(listBox);
    }
    
    /**
     * Re-renders a previously rendered list item.
     * 
     * @param item List item to re-render.
     */
    protected void rerender(Listitem item) {
        try {
            ZKUtil.detachChildren(item);
            listBox.getItemRenderer().render(item, item.getValue(), item.getIndex());
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
        int index = ListUtil.findListboxData(listBox, object);
        
        if (index >= 0) {
            rerender(listBox.getItems().get(index));
        }
    }
    
    @Override
    protected void print(Component root) {
        listBox.renderAll();
        super.print(root);
    }
    
    @Override
    protected void setListModel(ListModel<M> model) {
        this.listBox.setModel(model);
        Clients.resize(listBox);
    }
    
    @Override
    protected void setGroupsModel(GroupsModel<M, ?, ?> model) {
        this.listBox.setModel(model);
        Clients.resize(listBox);
    }
    
    /**
     * Sets the list item renderer.
     *
     * @param itemRenderer The item renderer.
     */
    public void setItemRenderer(ListitemRenderer<M> itemRenderer) {
        this.itemRenderer = itemRenderer;
        
        if (listBox != null) {
            listBox.setItemRenderer(itemRenderer);
        }
    }
    
    /**
     * Clear the current selection, if any.
     */
    protected void clearSelection() {
        listBox.clearSelection();
    }
    
    /**
     * Returns a list of listbox items.
     *
     * @param selectedOnly If true, only selected items are returned.
     * @return List of list items.
     */
    protected List<Listitem> getItems(boolean selectedOnly) {
        return selectedOnly ? ListUtil.getSelectedItems(listBox) : listBox.getItems();
    }
    
    /**
     * Returns a list of DTO objects.
     *
     * @param selectedOnly If true, only selected objects are returned.
     * @return List of DTO objects.
     */
    protected List<M> getObjects(boolean selectedOnly) {
        List<Listitem> items = getItems(selectedOnly);
        List<M> objects = new ArrayList<>(items.size());
        
        for (Listitem item : items) {
            M value = item.getValue();
            
            if (value == null) {
                listBox.renderItem(item);
                value = item.getValue();
            }
            
            objects.add(value);
        }
        
        return objects;
    }
    
    /**
     * Clear selected items
     */
    public void onClick$btnClear() {
        clearSelection();
    }
    
    public Listbox getListbox() {
        return listBox;
    }
}
