/*
 * #%L
 * cwf-ui-mockuments
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
package org.hspconsortium.cwf.ui.mockuments;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.carewebframework.api.query.AbstractQueryFilter;
import org.carewebframework.api.query.DateQueryFilter.DateType;
import org.carewebframework.api.query.IQueryContext;
import org.carewebframework.ui.zk.ListUtil;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;

import org.hspconsortium.cwf.fhir.document.Document;
import org.hspconsortium.cwf.fhir.document.DocumentListDataService;
import org.hspconsortium.cwf.fhir.document.DocumentService;
import org.hspconsortium.cwf.ui.reporting.controller.AbstractListController;

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
    
    private Combobox cboFilter;
    
    private Comboitem cbiSeparator;
    
    private Label lblFilter;
    
    private Label lblInfo;
    
    private String fixedFilter;
    
    private final Collection<String> allTypes;
    
    public DocumentListController(DocumentService service) {
        super(new DocumentListDataService(service), "cwfdocuments", "DOCUMENT", "documentsPrint.css");
        setPaging(false);
        registerQueryFilter(new DocumentTypeFilter());
        allTypes = service.getTypes();
    }
    
    @Override
    public void initializeController() {
        super.initializeController();
        getContainer().registerProperties(this, "fixedFilter");
        addFilters(allTypes, null, null);
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
     * Selecting document displays view.
     */
    public void onSelect$listBox() {
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
    }
    
    @Override
    public Date getDateByType(Document result, DateType dateMode) {
        return result.getDateTime();
    }
    
}
