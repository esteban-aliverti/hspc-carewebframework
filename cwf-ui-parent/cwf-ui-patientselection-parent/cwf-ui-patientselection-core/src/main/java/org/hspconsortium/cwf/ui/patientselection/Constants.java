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

import org.carewebframework.ui.zk.ZKUtil;

/**
 * Constants for patient selection.
 */
public class Constants {
    
    public static final String RESOURCE_PREFIX = ZKUtil.getResourcePath(Constants.class);
    
    public static final String PROP_PREFIX = Constants.class.getName() + ".";
    
    public static final String RESULT_ATTRIB = Constants.PROP_PREFIX + "result";
    
    public static final String SELECTOR_ATTRIB = PROP_PREFIX + "selector";
    
    public static final String SELECTED_PATIENT_ATTRIB = PROP_PREFIX + "patient";
    
    public static final String LBL_CANNOT_SELECT_TITLE = "patientselection.error.noselect.title";
    
    public static final String LBL_CANNOT_SELECT_MESSAGE = "patientselection.error.noselect.message";
    
    public static final String LBL_LIST_WAIT_MESSAGE = "patientselection.list.wait.message";
    
    public static final String LBL_DATE_RANGE_LABEL = "patientselection.daterange.label";
    
    public static final String LBL_DATE_RANGE_VALUES = "patientselection.daterange.values";
    
    public static final String LBL_WARN_NO_FILTERS = "patientselection.warn.no.filters";
    
    public static final String LBL_WARN_NO_PATIENTS = "patientselection.warn.no.patients";
    
    public static final String LBL_WARN_NO_LIST_SELECTED = "patientselection.warn.no.list.selected";
    
    public static final String LBL_DEMOGRAPHIC_TITLE = "patientselection.right.pane.title.demo";
    
    public static final String LBL_MANAGE_TITLE = "patientselection.right.pane.title.manage";
    
    public static final String LBL_FILTER_RENAME_TITLE = "patientselection.filter.rename.title";
    
    public static final String LBL_FILTER_NEW_TITLE = "patientselection.filter.new.title";
    
    public static final String LBL_FILTER_NAME_PROMPT = "patientselection.filter.name.prompt";
    
    public static final String LBL_FILTER_DELETE_TITLE = "patientselection.filter.deletion.confirm.title";
    
    public static final String LBL_FILTER_DELETE_PROMPT = "patientselection.filter.deletion.confirm.prompt";
    
    public static final String LBL_SEARCH_MESSAGE = "patientselection.search.message";
    
    /**
     * Enforce static class.
     */
    private Constants() {
    };
}
