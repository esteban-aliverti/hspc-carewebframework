/*
 * #%L
 * cwf-ui-documents
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
package org.hspconsortium.cwf.ui.documents;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.carewebframework.api.query.AbstractQueryFilter;
import org.carewebframework.api.query.DateQueryFilter.DateType;
import org.carewebframework.api.query.IQueryContext;
import org.hspconsortium.cwf.api.documents.Document;
import org.hspconsortium.cwf.api.documents.DocumentListDataService;
import org.hspconsortium.cwf.api.documents.DocumentService;
import org.hspconsortium.cwf.ui.reporting.controller.AbstractListController;
import org.carewebframework.ui.zk.ListUtil;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listitem;

/**
 * Controller for the list-based display of clinical documents.
 */
public class DocumentListController extends AbstractListController<Document, Document> {
    
    /**
     * Handles filtering by document type.
     */
    private class DocumentTypeFilter extends AbstractQueryFilter<Document> {
        
        @Override
        public boolean include(Document document) {
            String filter = getCurrentFilter();
            return filter == null || document.hasType(filter);
        }
        
        @Override
        public boolean updateContext(IQueryContext context) {
            context.setParam("type", getCurrentFilter());
            return true;
        }
        
    }
    
    private static final long serialVersionUID = 1L;
    
    private Button btnClear;
    
    private Button btnView;
    
    private String viewText; //default view selected documents
    
    private final String lblBtnViewSelectAll = Labels.getLabel("caldocuments.plugin.btn.view.selectall.label");
    
    private Combobox cboFilter;
    
    private Comboitem cbiSeparator;
    
    private Label lblFilter;
    
    private Label lblInfo;
    
    private String fixedFilter;
    
    private final Collection<String> allTypes;
    
    public DocumentListController(DocumentService service) {
        super(new DocumentListDataService(service), "caldocuments", "TIU", "documentsPrint.css");
        setPaging(false);
        registerQueryFilter(new DocumentTypeFilter());
        allTypes = service.getTypes();
    }
    
    @Override
    public void initializeController() {
        super.initializeController();
        viewText = btnView.getLabel();
        getContainer().registerProperties(this, "fixedFilter");
        addFilters(allTypes, null, null);
        updateSelectCount();
    }
    
    /**
     * This is a good place to update the filter list.
     */
    @Override
    protected List<Document> toModel(List<Document> queryResult) {
        if (queryResult != null) {
            updateListFilter(queryResult);
        }
        
        return queryResult;
    }
    
    /**
     * Presents a quick pick list limited to types present in the unfiltered document list.
     *
     * @param documents The unfiltered document list.
     */
    private void updateListFilter(List<Document> documents) {
        if (fixedFilter != null) {
            return;
        }
        
        List<Comboitem> items = cboFilter.getItems();
        Set<String> types = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        String currentFilter = getCurrentFilter();
        
        while (items.get(1) != cbiSeparator) {
            items.remove(1);
        }
        
        cboFilter.setSelectedIndex(0);
        
        if (documents != null) {
            for (Document doc : documents) {
                types.addAll(doc.getTypes());
            }
        }
        
        addFilters(types, cbiSeparator, currentFilter);
        
        if (currentFilter != null && cboFilter.getSelectedIndex() < 1) {
            ListUtil.selectComboboxItem(cboFilter, currentFilter);
        }
    }
    
    private void addFilters(Collection<String> types, Component ref, String selected) {
        for (String type : types) {
            Comboitem item = new Comboitem(type);
            item.setValue(type);
            
            cboFilter.insertBefore(item, ref);
            
            if (type.equals(selected)) {
                cboFilter.setSelectedItem(item);
            }
        }
    }
    
    /**
     * Returns the currently active type filter.
     *
     * @return The currently active type filter.
     */
    private String getCurrentFilter() {
        return fixedFilter != null ? fixedFilter
                : cboFilter.getSelectedIndex() > 0 ? (String) cboFilter.getSelectedItem().getValue() : null;
    }
    
    /**
     * Handle change in type filter selection.
     */
    public void onSelect$cboFilter() {
        applyFilters();
    }
    
    /**
     * Update the display count of selected documents.
     */
    protected void updateSelectCount() {
        int selCount = listBox.getSelectedCount();
        
        if (selCount == 0) {
            btnView.setLabel(lblBtnViewSelectAll);
            btnClear.setDisabled(true);
        } else {
            btnView.setLabel(viewText + " (" + selCount + ")");
            btnClear.setDisabled(false);
        }
        
        btnView.setDisabled(listBox.getItemCount() == 0);
    }
    
    /**
     * Update selection count.
     */
    public void onSelect$listBox() {
        updateSelectCount();
    }
    
    /**
     * Double-clicking enters document view mode.
     *
     * @param event The double click event.
     */
    public void onDoubleClick$listBox(Event event) {
        Component cmpt = ZKUtil.getEventOrigin(event).getTarget();
        
        if (cmpt instanceof Listitem) {
            Events.postEvent("onDeferredOpen", listBox, cmpt);
        }
    }
    
    /**
     * Opening the display view after a double-click is deferred to avoid anomalies with selection
     * of the associated list item.
     * 
     * @param event The deferred open event.
     */
    public void onDeferredOpen$listBox(Event event) {
        Listitem item = (Listitem) ZKUtil.getEventOrigin(event).getData();
        item.setSelected(true);
        updateSelectCount();
        onClick$btnView();
    }
    
    /**
     * Clear selected items
     */
    @Override
    public void clearSelection() {
        super.clearSelection();
        updateSelectCount();
    }
    
    /**
     * Triggers document view mode.
     */
    public void onClick$btnView() {
        Events.postEvent("onViewOpen", root, true);
    }
    
    /**
     * Returns a list of currently selected documents, or if no documents are selected, of all
     * documents.
     *
     * @return The currently selected documents.
     */
    protected List<Document> getSelectedDocuments() {
        return getObjects(listBox.getSelectedCount() > 0);
    }
    
    /**
     * Returns the fixed filter, if any.
     *
     * @return The fixed filter.
     */
    public String getFixedFilter() {
        return fixedFilter;
    }
    
    /**
     * Sets the fixed filter.
     *
     * @param name The fixed filter.
     */
    public void setFixedFilter(String name) {
        fixedFilter = name;
        cboFilter.setVisible(fixedFilter == null);
        lblFilter.setVisible(fixedFilter != null);
        lblFilter.setValue(fixedFilter);
        refresh();
    }
    
    @Override
    protected void setListModel(ListModel<Document> model) {
        super.setListModel(model);
        int docCount = model == null ? 0 : model.getSize();
        lblInfo.setValue(docCount + " document(s)");
        btnView.setDisabled(docCount == 0);
        updateSelectCount();
    }
    
    @Override
    public Date getDateByType(Document result, DateType dateMode) {
        return result.getDateTime();
    }
    
}
