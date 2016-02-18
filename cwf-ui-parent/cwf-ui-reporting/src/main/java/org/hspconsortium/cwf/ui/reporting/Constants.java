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
package org.hspconsortium.cwf.ui.reporting;

import org.carewebframework.ui.zk.ZKUtil;

/**
 * Package-specific constants.
 */
public class Constants {
    
    public static final String RESOURCE_PREFIX = ZKUtil.getResourcePath(Constants.class);
    
    public static final String SCLASS_ABNORMAL_RESULT = "cwf-reporting-abnormalResult";
    
    public static final String SCLASS_NORMAL_RANGE = "cwf-reporting-normalRange";
    
    public static final String SCLASS_DRILLDOWN_LINK = "cwf-reporting-drillDown-link";
    
    public static final String SCLASS_DRILLDOWN_GRID = "cwf-reporting-drillDown-grid";
    
    public static final String SCLASS_DRILLDOWN_DOCUMENT_TITLE = "cwf-reporting-drillDown-document-title";
    
    public static final String SCLASS_REPORT_ALL = "cwf-reporting-report-all";
    
    public static final String SCLASS_REPORT_HEADER = "cwf-reporting-header";
    
    public static final String SCLASS_REPORT_TITLE = "cwf-reporting-header-title";
    
    public static final String SCLASS_TEXT_REPORT_TITLE = "cwf-reporting-textReport-title";
    
    public static final String SCLASS_TEXT_REPORT_BODY = "cwf-reporting-textReport-body";
    
    public static final String SCLASS_TEXT_REPORT_HEADER = "cwf-reporting-textReport-header";
    
    public static final String PROPERTY_ID_DRILLDOWN = "CAREWEB.ENABLE.DRILLDOWN";
    
    public static final String PROPERTY_ID_DATE_RANGE = "%.DATERANGE";
    
    public static final String PROPERTY_ID_EXPAND_DETAIL = "%.EXPAND.DETAIL";
    
    public static final String PROPERTY_ID_MAX_ROWS = "%.MAX.ROWS";
    
    public static final String PROPERTY_ID_SORT_MODE = "%.SORT";
    
    public static final String PROPERTY_ID_ROD_SIZE = "%.ROD.SIZE";
    
    public static final String LABEL_ID_SORT_MODE = "%.plugin.cmbx.sort.mode.item.$.label";
    
    public static final String LABEL_ID_TITLE = "%.plugin.print.title";
    
    public static final String LABEL_ID_PAGE_ON = "%.plugin.btn.paging.label.on";
    
    public static final String LABEL_ID_PAGE_OFF = "%.plugin.btn.paging.label.off";
    
    public static final String LABEL_ID_NO_PATIENT = "%.plugin.patient.selection.required";
    
    public static final String LABEL_ID_MISSING_PARAMETER = "%.plugin.missing.parameter";
    
    public static final String LABEL_ID_NO_DATA = "%.plugin.no.data.found";
    
    public static final String LABEL_ID_FETCHING = "%.plugin.status.fetching";
    
    public static final String LABEL_ID_FILTERING = "%.plugin.status.filtering";
    
    public static final String LABEL_ID_WAITING = "%.plugin.status.waiting";
    
    /**
     * Enforce static class.
     */
    private Constants() {
    };
}
