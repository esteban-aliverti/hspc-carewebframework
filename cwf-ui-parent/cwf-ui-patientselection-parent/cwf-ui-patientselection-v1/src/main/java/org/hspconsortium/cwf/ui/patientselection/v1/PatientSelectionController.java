/*
 * #%L
 * cwf-ui-patientselection-v1
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
package org.hspconsortium.cwf.ui.patientselection.v1;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ca.uhn.fhir.model.dstu2.resource.Patient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.api.patientlist.AbstractPatientListFilter;
import org.hspconsortium.cwf.api.patientlist.FavoritePatientList;
import org.hspconsortium.cwf.api.patientlist.IPatientList;
import org.hspconsortium.cwf.api.patientlist.IPatientListFilterManager;
import org.hspconsortium.cwf.api.patientlist.IPatientListFilterManager.FilterCapability;
import org.hspconsortium.cwf.api.patientlist.IPatientListItemManager;
import org.hspconsortium.cwf.api.patientlist.IPatientListRegistry;
import org.hspconsortium.cwf.api.patientlist.PatientListException;
import org.hspconsortium.cwf.api.patientlist.PatientListItem;
import org.hspconsortium.cwf.ui.patientselection.Constants;
import org.hspconsortium.cwf.ui.patientselection.IPatientDetailRenderer;
import org.hspconsortium.cwf.ui.patientselection.PatientDetailRenderer;
import org.hspconsortium.cwf.ui.patientselection.PatientListFilterRenderer;
import org.hspconsortium.cwf.ui.patientselection.PatientListItemRenderer;
import org.hspconsortium.cwf.ui.patientselection.PatientSearchUtil;
import org.carewebframework.common.DateRange;
import org.carewebframework.shell.CareWebUtil;
import org.carewebframework.ui.FrameworkController;
import org.carewebframework.ui.zk.DateRangePicker;
import org.carewebframework.ui.zk.PromptDialog;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.LayoutRegion;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Window;

/**
 * Controller for patient selection dialog.
 */
public class PatientSelectionController extends FrameworkController {
    
    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(PatientSelectionController.class);
    
    private static final String ATTR_PATIENT_LIST = "list";
    
    private static final String FILTER_DROP_ID = "patientselection.filter.drop.id";
    
    private final String[] dateRanges = Labels.getLabel(Constants.LBL_DATE_RANGE_VALUES).split("\n");
    
    private final String txtDateRange = Labels.getLabel(Constants.LBL_DATE_RANGE_LABEL);
    
    private final String txtNoFilters = Labels.getLabel(Constants.LBL_WARN_NO_FILTERS);
    
    private final String txtNoPatients = Labels.getLabel(Constants.LBL_WARN_NO_PATIENTS);
    
    private final String txtNoList = Labels.getLabel(Constants.LBL_WARN_NO_LIST_SELECTED);
    
    private final String txtDemoTitle = Labels.getLabel(Constants.LBL_DEMOGRAPHIC_TITLE);
    
    private final String txtManageTitle = Labels.getLabel(Constants.LBL_MANAGE_TITLE);
    
    private final String txtRenameFilterTitle = Labels.getLabel(Constants.LBL_FILTER_RENAME_TITLE);
    
    private final String txtNewFilterTitle = Labels.getLabel(Constants.LBL_FILTER_NEW_TITLE);
    
    private final String txtFilterNamePrompt = Labels.getLabel(Constants.LBL_FILTER_NAME_PROMPT);
    
    private final String txtDeleteFilterTitle = Labels.getLabel(Constants.LBL_FILTER_DELETE_TITLE);
    
    private final String txtDeleteFilterPrompt = Labels.getLabel(Constants.LBL_FILTER_DELETE_PROMPT);
    
    private final String txtSearchMessage = Labels.getLabel(Constants.LBL_SEARCH_MESSAGE);
    
    private final String txtWaitMessage = Labels.getLabel(Constants.LBL_LIST_WAIT_MESSAGE);
    
    private Radiogroup rgrpLists;
    
    private Window root;
    
    private Listbox lstFilter;
    
    private Label lblDateRange;
    
    private DateRangePicker drpDateRange;
    
    private Button btnManageList;
    
    private Button btnFavorite;
    
    private Textbox edtSearch;
    
    private Listbox lstSearch;
    
    private Label lblPatientList;
    
    private Listbox lstPatientList;
    
    private Component pnlDemographics;
    
    private Component pnlDemoRoot;
    
    private Button btnDemoDetail;
    
    private Timer timer;
    
    private Component pnlManagedList;
    
    private Component pnlManagedListFilters;
    
    private Listbox lstManagedListFilter;
    
    private Button btnManagedListFilterNew;
    
    private Button btnManagedListFilterRename;
    
    private Button btnManagedListFilterDelete;
    
    private Component pnlManagedListItems;
    
    private Label lblManagedList;
    
    private Button btnManagedListAdd;
    
    private Button btnManagedListImport;
    
    private Button btnManagedListAddCurrent;
    
    private Button btnManagedListRemove;
    
    private Button btnManagedListRemoveAll;
    
    private Listbox lstManagedList;
    
    private Button btnOK;
    
    private LayoutRegion rgnEast;
    
    private IPatientListRegistry registry;
    
    private IPatientList activeList;
    
    private IPatientList managedList;
    
    private IPatientList originalList;
    
    private IPatientListItemManager itemManager;
    
    private IPatientListFilterManager filterManager;
    
    private AbstractPatientListFilter activeFilter;
    
    private FavoritePatientList favorites;
    
    private Patient activePatient;
    
    private boolean manageListMode;
    
    private DateRange defaultDateRange;
    
    private final List<PatientListItem> pendingListItem = new ArrayList<>();
    
    private IPatientDetailRenderer patientDetailRenderer = new PatientDetailRenderer();
    
    /**
     * Handles drag/drop events for filters in filter management mode.
     */
    private final EventListener<Event> filterDropListener = new EventListener<Event>() {
        
        @Override
        public void onEvent(Event event) throws Exception {
            DropEvent dropEvent = (DropEvent) ZKUtil.getEventOrigin(event);
            Listitem dragged = (Listitem) dropEvent.getDragged();
            Listitem target = (Listitem) dropEvent.getTarget();
            filterManager.moveFilter((AbstractPatientListFilter) dragged.getValue(), target.getIndex());
            dragged.getListbox().insertBefore(dragged, target);
        }
    };
    
    /**
     * Initial setup.
     * 
     * @throws Exception Unspecified exception.
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        root = (Window) comp;
        initDateRanges();
        initRegisteredLists();
        initRenderers();
        CareWebUtil.associateCSH(root, "patientSelectionV1Help", null, null);
    }
    
    /**
     * Initialize the date ranges to be used for filtering lists.
     */
    private void initDateRanges() {
        drpDateRange.loadChoices(dateRanges);
        defaultDateRange = drpDateRange.getSelectedRange();
    }
    
    /**
     * Loads the registered lists into the radio group.
     */
    private void initRegisteredLists() {
        for (IPatientList list : registry) {
            if (!list.isDisabled()) {
                Radio radio = new Radio(list.getName());
                radio.setAttribute(ATTR_PATIENT_LIST, list);
                rgrpLists.appendChild(radio);
                
                if (list == favorites) {
                    radio.setId("radFavorites");
                }
            }
        }
        
        rgrpLists.setSelectedIndex(0);
        pendingListItem.add(new PatientListItem(null, txtWaitMessage));
    }
    
    /**
     * Initializes renderers for list boxes.
     */
    private void initRenderers() {
        lstPatientList.setItemRenderer(PatientListItemRenderer.getInstance());
        lstSearch.setItemRenderer(PatientListItemRenderer.getInstance());
        lstManagedList.setItemRenderer(PatientListItemRenderer.getInstance());
        lstFilter.setItemRenderer(PatientListFilterRenderer.getInstance());
        lstManagedListFilter.setItemRenderer(PatientListFilterRenderer.getInstance());
    }
    
    /**
     * Returns the renderer for the patient detail view.
     * 
     * @return Patient detail renderer.
     */
    public IPatientDetailRenderer getPatientDetailRenderer() {
        return patientDetailRenderer;
    }
    
    /**
     * Sets the renderer for the patient detail view.
     * 
     * @param patientDetailRenderer The patient detail renderer.
     */
    public void setPatientDetailRenderer(IPatientDetailRenderer patientDetailRenderer) {
        this.patientDetailRenderer = patientDetailRenderer;
    }
    
    /**
     * Sets the specified list as active.
     * 
     * @param list The patient list to make active.
     */
    private void setActiveList(IPatientList list) {
        activeList = list;
        activeFilter = null;
        btnFavorite.setDisabled(list == this.favorites);
        boolean hasDateRange = (list != null && list.isDateRangeRequired());
        lblDateRange.setVisible(hasDateRange);
        drpDateRange.setVisible(hasDateRange);
        
        if (hasDateRange) {
            DateRange range = list.getDateRange();
            
            if (range == null) {
                range = defaultDateRange;
                list.setDateRange(range);
            }
            
            Comboitem item = drpDateRange.findMatchingItem(range);
            item = item == null ? drpDateRange.addChoice(range, true) : item;
            drpDateRange.setSelectedItem(item);
            lblDateRange.setValue(MessageFormat.format(txtDateRange, list.getEntityName()));
        }
        
        refreshFilterList();
        refreshPatientList();
        updateControls();
    }
    
    private void refreshFilterList() {
        boolean hasFilter = activeList != null && activeList.isFiltered();
        lstFilter.setVisible(hasFilter);
        
        if (hasFilter) {
            activeFilter = activeList.getActiveFilter();
            Collection<AbstractPatientListFilter> filters = activeList.getFilters();
            
            if (filters == null || filters.isEmpty()) {
                lstFilter.setModel((ListModelList<?>) null);
                lstFilter.getItems().clear();
                lstFilter.appendItem(txtNoFilters, null);
            } else {
                lstFilter.setModel(new ListModelList<AbstractPatientListFilter>(filters));
                
                if (activeFilter == null) {
                    activeFilter = filters.iterator().next();
                    activeList.setActiveFilter(activeFilter);
                }
            }
            
            selectFilter(lstFilter, activeFilter);
        }
    }
    
    /**
     * Selects the list box item corresponding to the specified filter.
     * 
     * @param lb List box to search.
     * @param filter The filter whose associated list item is to be selected.
     * @return True if the item was successfully selected.
     */
    private boolean selectFilter(Listbox lb, AbstractPatientListFilter filter) {
        if (filter != null) {
            for (Object object : lb.getItems()) {
                Listitem item = (Listitem) object;
                lb.renderItem(item);
                AbstractPatientListFilter flt = (AbstractPatientListFilter) item.getValue();
                
                if (flt != null && filter.equals(flt)) {
                    lb.setSelectedItem(item);
                    Clients.scrollIntoView(item);
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private void refreshPatientList() {
        timer.stop();
        
        if (activeList != null) {
            Collection<PatientListItem> items;
            
            if (activeList.isPending()) {
                items = pendingListItem;
                timer.start();
            } else {
                items = activeList.getListItems();
            }
            
            ListModelList<PatientListItem> model = items == null ? new ListModelList<PatientListItem>()
                    : new ListModelList<PatientListItem>(items);
            
            if (model.isEmpty()) {
                model.add(new PatientListItem(null, txtNoPatients));
            }
            
            lstPatientList.setModel(model);
            lblPatientList.setValue(activeList.getDisplayName());
        } else {
            lstPatientList.setModel((ListModel<?>) null);
            lblPatientList.setValue(txtNoList);
        }
        
        setActivePatient((Patient) null);
    }
    
    private void setActiveFilter(AbstractPatientListFilter filter) {
        activeFilter = filter;
        activeList.setActiveFilter(filter);
        
        if (drpDateRange.isVisible()) {
            setActiveDateRange(drpDateRange.getSelectedRange());
        } else {
            refreshPatientList();
        }
    }
    
    private void setActiveDateRange(DateRange range) {
        if (range != null) {
            activeList.setDateRange(range);
            refreshFilterList();
            refreshPatientList();
        }
    }
    
    /**
     * Sets the active patient based on an event.
     * 
     * @param event An event.
     */
    public void setActivePatient(Event event) {
        PatientListItem pli = getItem(event);
        setActivePatient(pli == null ? null : pli.getPatient());
    }
    
    private void setActivePatient(Patient patient) {
        // Build the demographic display here
        activePatient = patient;
        root.setAttribute(Constants.SELECTED_PATIENT_ATTRIB, activePatient);
        ZKUtil.detachChildren(pnlDemoRoot);
        
        if (patient != null && patientDetailRenderer != null) {
            patientDetailRenderer.render(pnlDemoRoot, patient, this);
        }
        
        btnDemoDetail.setDisabled(activePatient == null);
        updateControls();
    }
    
    /**
     * Called by Spring to finish initialization.
     */
    public void init() {
    }
    
    /**
     * Search for matching patients based on user input.
     */
    private void doSearch() {
        log.trace("Start doSearch()");
        Clients.clearBusy();
        displaySearchMessage(null);
        
        try {
            lstSearch.clearSelection();
            List<Patient> matches = PatientSearchUtil.execute(edtSearch.getValue(), 100);
            
            if (matches != null) {
                lstSearch.setModel(new ListModelList<Patient>(matches));
                
                if (matches.size() == 1) {
                    lstSearch.setSelectedIndex(0);
                }
            }
        } catch (Exception e) {
            displaySearchMessage(e.getMessage());
        }
        
        edtSearch.setFocus(true);
        edtSearch.select();
        Events.postEvent(Events.ON_SELECT, lstSearch, null);
    }
    
    private void displaySearchMessage(String message) {
        lstSearch.clearSelection();
        lstSearch.setModel((ListModelList<?>) null);
        lstSearch.getItems().clear();
        
        if (message != null) {
            lstSearch.appendItem(message, null).setTooltiptext(message);
        }
        
        Clients.scrollIntoView(lstSearch.getFirstChild());
    }
    
    /**
     * Set the patient list registry (injected by Spring).
     * 
     * @param registry The patient list registry.
     */
    public void setPatientListRegistry(IPatientListRegistry registry) {
        this.registry = registry;
    }
    
    /**
     * Set a reference to the favorites list (injected by Spring).
     * 
     * @param list The favorite patient list.
     */
    public void setFavoritesList(FavoritePatientList list) {
        this.favorites = list;
    }
    
    /**
     * Sets list management mode.
     * 
     * @param value If true, the dialog enters list management mode. If false, the dialog reverts to
     *            patient selection mode.
     */
    private void setManageListMode(boolean value) {
        manageListMode = value;
        pnlManagedList.setVisible(value);
        pnlDemographics.setVisible(!value);
        rgnEast.setTitle(MessageFormat.format(value ? txtManageTitle : txtDemoTitle, activeList.getName()));
        
        if (originalList != null) {
            originalList.refresh();
        }
        
        if (manageListMode) {
            originalList = activeList;
            managedList = activeList.copy();
            itemManager = managedList.getItemManager();
            filterManager = managedList.getFilterManager();
            pnlManagedListFilters.setVisible(filterManager != null);
            btnManagedListFilterNew.setVisible(filterManager != null && filterManager.hasCapability(FilterCapability.ADD));
            btnManagedListFilterDelete.setVisible(filterManager != null
                    && filterManager.hasCapability(FilterCapability.REMOVE));
            btnManagedListFilterRename.setVisible(filterManager != null
                    && filterManager.hasCapability(FilterCapability.RENAME));
            
            if (filterManager != null) {
                lstManagedListFilter.setModel(new ListModelList<AbstractPatientListFilter>(managedList.getFilters()));
                
                if (filterManager.hasCapability(FilterCapability.MOVE)) {
                    addDragDropSupport(lstManagedListFilter, FILTER_DROP_ID, filterDropListener);
                }
            }
            
            pnlManagedListItems.setVisible(itemManager != null);
            lblManagedList.setVisible(itemManager != null);
            lstManagedList.setModel((ListModelList<?>) null);
            
            if (selectFilter(lstManagedListFilter, managedList.getActiveFilter())) {
                managedListFilterChanged();
            }
            
            pnlManagedList.invalidate();
        } else {
            originalList = null;
            managedList = null;
            itemManager = null;
            filterManager = null;
            setActiveList(activeList);
        }
        
        updateControls();
        Clients.resize(rgnEast);
    }
    
    /**
     * Changes the active filter for the currently managed list.
     * 
     * @param filter The patient list filter to make active.
     */
    private void setManagedListFilter(AbstractPatientListFilter filter) {
        if (itemManager != null) {
            itemManager.save();
        }
        
        managedList.setActiveFilter(filter);
        managedListFilterChanged();
        
    }
    
    /**
     * Adds drag/drop support to the items belonging to the specified list box.
     * 
     * @param lb The list box.
     * @param dropId The drop id to be used.
     * @param eventListener The event listener to handle the drag/drop operations.
     */
    private void addDragDropSupport(Listbox lb, String dropId, EventListener<?> eventListener) {
        for (Object object : lb.getItems()) {
            Listitem item = (Listitem) object;
            item.setDraggable(dropId);
            item.setDroppable(dropId);
            item.addEventListener(Events.ON_DROP, eventListener);
        }
    }
    
    /**
     * Update control states.
     */
    private void updateControls() {
        if (manageListMode) {
            boolean filterSelected = lstManagedListFilter.getSelectedItem() != null;
            boolean patientSelected = lstManagedList.getSelectedItem() != null;
            btnManagedListFilterRename.setDisabled(!filterSelected);
            btnManagedListFilterDelete.setDisabled(!filterSelected);
            btnManagedListAddCurrent.setDisabled(!filterSelected || PatientContext.getActivePatient() == null);
            btnManagedListAdd.setDisabled(!filterSelected || activePatient == null);
            btnManagedListImport.setDisabled(!filterSelected || lstPatientList.getModel() == null);
            btnManagedListRemove.setDisabled(!patientSelected);
            btnManagedListRemoveAll.setDisabled(lstManagedList.getItemCount() == 0);
            btnOK.setDisabled(false);
            btnManageList.setDisabled(true);
        } else {
            btnManageList.setDisabled(activeList == null
                    || (activeList.getItemManager() == null && activeList.getFilterManager() == null));
            btnOK.setDisabled(activePatient == null);
        }
    }
    
    /**
     * Adds the specified patient to the currently selected managed list.
     * 
     * @param patient The patient to add.
     * @param refresh If true, refresh the display.
     */
    private void managedListAdd(Patient patient, boolean refresh) {
        if (patient != null) {
            managedListAdd(new PatientListItem(patient, null), refresh);
        }
    }
    
    private void managedListAdd(PatientListItem item, boolean refresh) {
        if (item != null && item.getPatient() != null) {
            itemManager.addItem(item);
            
            if (refresh) {
                managedListRefresh();
            }
        }
    }
    
    private void managedListRemove(PatientListItem item, boolean refresh) {
        if (item != null) {
            itemManager.removeItem(item);
            
            if (refresh) {
                managedListRefresh();
            }
        }
    }
    
    private void managedListRefresh() {
        lstManagedList.setModel(new ListModelList<PatientListItem>(managedList.getListItems()));
    }
    
    private void managedListFilterChanged() {
        if (itemManager != null) {
            itemManager.save();
            lstManagedList.setModel(new ListModelList<PatientListItem>(managedList.getListItems()));
            AbstractPatientListFilter filter = managedList.getActiveFilter();
            lblManagedList.setValue(managedList.getEntityName() + (filter == null ? "" : ": " + filter.getName()));
        }
        updateControls();
    }
    
    private AbstractPatientListFilter getFilter(Event event) {
        return getFilter((Listbox) ZKUtil.getEventOrigin(event).getTarget());
    }
    
    private AbstractPatientListFilter getFilter(Listbox lb) {
        return getFilter(lb.getSelectedItem());
    }
    
    private AbstractPatientListFilter getFilter(Listitem item) {
        return item == null ? null : (AbstractPatientListFilter) item.getValue();
    }
    
    private PatientListItem getItem(Event event) {
        return getItem((Listbox) ZKUtil.getEventOrigin(event).getTarget());
    }
    
    private PatientListItem getItem(Listbox lb) {
        return getItem(lb.getSelectedItem());
    }
    
    private PatientListItem getItem(Listitem item) {
        return item == null ? null : (PatientListItem) item.getValue();
    }
    
    /**
     * Adds or renames a filter.
     * 
     * @param filter If not null, assumes we are renaming an existing filter. If null, assumes we
     *            are adding a new filter.
     */
    private void addOrRenameFilter(AbstractPatientListFilter filter) {
        String errorMessage = "";
        boolean newFilter = filter == null;
        String oldName = newFilter ? null : filter.getName();
        
        while (true) {
            try {
                String name = PromptDialog.input(errorMessage + txtFilterNamePrompt, newFilter ? txtNewFilterTitle
                        : txtRenameFilterTitle, oldName);
                
                if (!StringUtils.isEmpty(name)) {
                    if (newFilter) {
                        filter = filterManager.addFilter(name);
                    } else {
                        filterManager.renameFilter(filter, name);
                    }
                    
                    lstManagedListFilter.setModel(new ListModelList<AbstractPatientListFilter>(managedList.getFilters()));
                    selectFilter(lstManagedListFilter, filter);
                    setManagedListFilter(filter);
                }
                break;
                
            } catch (PatientListException e) {
                errorMessage = e.getMessage() + "\n";
            }
        }
    }
    
    private void doClose() {
        if (manageListMode) {
            if (itemManager != null) {
                itemManager.save();
            }
            
            setManageListMode(false);
            return;
        }
        
        if (activePatient == null) {
            doCancel();
            return;
        }
        
        root.setVisible(false);
    }
    
    private void doCancel() {
        if (manageListMode) {
            setManageListMode(false);
        } else {
            root.removeAttribute(Constants.SELECTED_PATIENT_ATTRIB);
            root.setVisible(false);
        }
    }
    
    /* ================== Event Handlers ================== */
    
    /* ----------------- Dialog Control ------------------- */
    
    /**
     * If in list management mode, clicking the OK button will save pending changes to the managed
     * list and revert to patient selection mode. If in patient selection mode, clicking the OK
     * button will select the current patient into the shared context and close the dialog.
     */
    public void onClick$btnOK() {
        doClose();
    }
    
    /**
     * If in list management mode, clicking the cancel button will cancel pending changes to the
     * managed list and revert to patient selection mode. If in patient selection mode, clicking the
     * cancel button will close the dialog without further action.
     */
    public void onClick$btnCancel() {
        doCancel();
    }
    
    /**
     * Handles a deferred request to show the dialog.
     * 
     * @param event The onShow event.
     * @throws Exception Unspecified exception.
     */
    public void onShow(Event event) throws Exception {
        root.removeAttribute(Constants.SELECTED_PATIENT_ATTRIB);
        lstSearch.clearSelection();
        onCheck$rgrpLists();
        Events.echoEvent(Events.ON_FOCUS, root, null);
        
        if (!root.inModal()) {
            root.doModal();
        }
    }
    
    /**
     * Handles a deferred request to set the focus to the search text box.
     */
    public void onFocus() {
        edtSearch.setFocus(true);
        edtSearch.select();
    }
    
    /* ------------------ List Control -------------------- */
    
    /**
     * When a radio button is selected, its associated patient list is activated.
     */
    public void onCheck$rgrpLists() {
        Radio radio = rgrpLists.getSelectedItem();
        
        if (radio == null) {
            radio = rgrpLists.getItemAtIndex(0);
            rgrpLists.setSelectedItem(radio);
        }
        
        IPatientList list = (IPatientList) radio.getAttribute(ATTR_PATIENT_LIST);
        setActiveList(list);
    }
    
    public void onTimer$timer() {
        if (activeList == null || !activeList.isPending()) {
            timer.stop();
            refreshPatientList();
        }
    }
    
    /**
     * When a filter is selected, make it the active filter for the active patient list.
     * 
     * @param event The onSelect event.
     */
    public void onSelect$lstFilter(Event event) {
        setActiveFilter(getFilter(event));
    }
    
    /**
     * When the date range changes, make it the current date range for the active patient list.
     */
    public void onSelectRange$drpDateRange() {
        setActiveDateRange(drpDateRange.getSelectedRange());
    }
    
    /**
     * Enter list management mode when the manage button is clicked.
     */
    public void onClick$btnManageList() {
        setManageListMode(true);
    }
    
    /**
     * Add the active list to the favorites.
     */
    public void onClick$btnFavorite() {
        favorites.addFavorite(activeList);
    }
    
    /* ---------------- Patient Selection ------------------ */
    
    /**
     * Set the active patient when selected from the list.
     * 
     * @param event The onSelect event.
     */
    public void onSelect$lstPatientList(Event event) {
        lstSearch.clearSelection();
        setActivePatient(event);
    }
    
    /**
     * Double-clicking a patient list item is the same as selecting it and then clicking the OK
     * button.
     * 
     * @param event The onDoubleClick event.
     */
    public void onDoubleClick$lstPatientList(Event event) {
        setActivePatient(event);
        
        if (activePatient != null) {
            if (!manageListMode) {
                doClose();
            } else if (itemManager != null && !btnManagedListAdd.isDisabled()) {
                managedListAdd(activePatient, true);
            }
        }
    }
    
    public void onSelect$lstSearch(Event event) {
        lstPatientList.clearSelection();
        setActivePatient(event);
    }
    
    public void onDoubleClick$lstSearch(Event event) {
        onDoubleClick$lstPatientList(event);
    }
    
    /* ----------------- Patient Search ------------------- */
    
    public void onClick$btnSearch() {
        Clients.showBusy(txtSearchMessage);
        displaySearchMessage(txtSearchMessage);
        Events.echoEvent("onSearch", root, null);
    }
    
    public void onOK$edtSearch() {
        onClick$btnSearch();
    }
    
    public void onSearch() {
        Clients.clearBusy();
        doSearch();
        edtSearch.setFocus(true);
    }
    
    /* ----------------- List Management ------------------ */
    
    public void onSelect$lstManagedListFilter(Event event) {
        setManagedListFilter(getFilter(event));
    }
    
    public void onSelect$lstManagedList() {
        updateControls();
    }
    
    /**
     * Create a new filter, prompting for a name.
     */
    public void onClick$btnManagedListFilterNew() {
        addOrRenameFilter(null);
    }
    
    /**
     * Rename an existing filter, prompting for a new name.
     */
    public void onClick$btnManagedListFilterRename() {
        addOrRenameFilter(managedList.getActiveFilter());
    }
    
    public void onClick$btnManagedListFilterDelete() {
        AbstractPatientListFilter filter = managedList.getActiveFilter();
        
        if (filter != null
                && PromptDialog.confirm(txtDeleteFilterPrompt, MessageFormat.format(txtDeleteFilterTitle, filter.getName()))) {
            filterManager.removeFilter(filter);
            lstManagedListFilter.getSelectedItem().detach();
            setManagedListFilter(null);
        }
    }
    
    public void onClick$btnManagedListAddCurrent() {
        managedListAdd(PatientContext.getActivePatient(), true);
    }
    
    public void onClick$btnManagedListAdd() {
        managedListAdd(activePatient, true);
    }
    
    public void onClick$btnManagedListImport() {
        for (Object item : (ListModelList<?>) lstPatientList.getModel()) {
            managedListAdd((PatientListItem) item, false);
        }
        
        managedListRefresh();
    }
    
    public void onClick$btnManagedListRemove() {
        managedListRemove(getItem(lstManagedList), true);
    }
    
    public void onClick$btnManagedListRemoveAll() {
        for (PatientListItem item : new ArrayList<>(managedList.getListItems())) {
            managedListRemove(item, false);
        }
        
        managedListRefresh();
    }
    
}
