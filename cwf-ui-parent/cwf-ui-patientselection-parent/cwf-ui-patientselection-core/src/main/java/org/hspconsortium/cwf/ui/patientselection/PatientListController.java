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

import java.util.Collection;

import org.apache.commons.lang.math.NumberUtils;

import org.carewebframework.api.spring.SpringUtil;
import org.hspconsortium.cwf.api.patientlist.IPatientList;
import org.hspconsortium.cwf.api.patientlist.PatientListItem;
import org.carewebframework.ui.FrameworkController;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

/**
 * Controller for patient list display. Recognizes the following dynamic properties: patientList =
 * The patient list to display (bean id or an instance of IPatientList). eventListener = The event
 * listener to handle selection from the list. maxRows = The maximum number of rows to display.
 * Defaults to 8.
 */
public class PatientListController extends FrameworkController {
    
    private static final long serialVersionUID = 1L;
    
    public static final String ATTR_PATIENT_LIST = "patientList";
    
    public static final String ATTR_EVENT_LISTENER = "eventListener";
    
    public static final String ATTR_MAX_ROWS = "maxRows";
    
    private Listbox patientList;
    
    private EventListener<Event> selectListener;
    
    /**
     * Set up the list box based on dynamic properties passed via the execution.
     * 
     * @param comp The top level component.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        IPatientList plist = getPatientList();
        patientList.setItemRenderer(PatientListItemRenderer.getInstance());
        Collection<PatientListItem> items = plist.getListItems();
        patientList.setModel(new ListModelList<PatientListItem>(items));
        int maxRows = getMaxRows();
        int count = items.size();
        patientList.setRows(count > maxRows ? maxRows : count);
        selectListener = (EventListener<Event>) execution.getAttribute(ATTR_EVENT_LISTENER);
    }
    
    /**
     * Returns the maximum rows from the "maxRows" dynamic property. If none specified, defaults to
     * 8.
     * 
     * @return Maximum rows.
     */
    private int getMaxRows() {
        Object maxRows = execution.getAttribute(ATTR_MAX_ROWS);
        return maxRows == null ? 8 : NumberUtils.toInt(maxRows.toString(), 8);
    }
    
    /**
     * Returns the patient list from the "patientList" dynamic property.
     * 
     * @return The patient list.
     */
    private IPatientList getPatientList() {
        Object plist = execution.getAttribute(ATTR_PATIENT_LIST);
        
        if (plist instanceof String) {
            return SpringUtil.getBean((String) plist, IPatientList.class);
        } else if (plist instanceof IPatientList) {
            return (IPatientList) plist;
        } else {
            return null;
        }
    }
    
    /**
     * Pass selection event to external listener, if any.
     * 
     * @param event The onSelect event.
     * @throws Exception Unspecified exception.
     */
    public void onSelect$patientList(Event event) throws Exception {
        if (selectListener != null) {
            selectListener.onEvent(event);
        }
    }
    
}
