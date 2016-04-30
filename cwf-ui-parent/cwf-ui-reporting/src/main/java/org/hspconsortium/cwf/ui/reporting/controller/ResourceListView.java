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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import org.carewebframework.ui.sharedforms.ListViewForm;
import org.carewebframework.ui.thread.ZKThread;
import org.carewebframework.ui.thread.ZKThread.ZKRunnable;
import org.carewebframework.ui.zk.PromptDialog;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.zul.Html;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Window;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.utilities.xhtml.XhtmlNode;
import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.fhir.common.BaseService;
import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.hspconsortium.cwf.ui.reporting.Constants;

/**
 * Controller for cover sheet components.
 *
 * @param <R> Type of resource object.
 * @param <M> Type of model object.
 */
public abstract class ResourceListView<R extends IBaseResource, M> extends ListViewForm<M> implements PatientContext.IPatientContextEvent {
    
    
    private static final long serialVersionUID = 1L;
    
    protected Html detailView;
    
    protected Patient patient;
    
    protected int asyncHandle;
    
    private String detailTitle;
    
    private BaseService fhirService;
    
    private String resourcePath;
    
    private Class<R> resourceClass;
    
    protected void setup(Class<R> resourceClass, String title, String detailTitle, String resourcePath, int sortBy,
                         String... headers) {
        this.detailTitle = detailTitle;
        this.resourcePath = resourcePath;
        this.resourceClass = resourceClass;
        super.setup(title, sortBy, headers);
    }
    
    @Override
    public String pending(boolean silent) {
        return null;
    }
    
    @Override
    public void committed() {
        patient = PatientContext.getActivePatient();
        refresh();
    }
    
    @Override
    public void canceled() {
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected Object transformData(Object data) {
        if (data instanceof IBaseDatatype) {
            return FhirUtil.getDisplayValueForType((IBaseDatatype) data);
        }
        
        if (data instanceof List) {
            List<?> c = (List<?>) data;
            
            if (!c.isEmpty() && c.get(0) instanceof IBaseDatatype) {
                return FhirUtil.getDisplayValueForTypes((List<IBaseDatatype>) c, ", ");
            }
        }
        
        return data;
    }
    
    /**
     * Override load list to clear display if no patient in context.
     */
    @Override
    protected void loadData() {
        if (patient == null) {
            asyncAbort();
            reset();
            status("No patient selected.");
        } else {
            super.loadData();
        }
        
        detailView.setContent(null);
    }
    
    @Override
    protected void requestData() {
        final String url = resourcePath.replace("#", patient.getIdElement().getIdPart());
        
        startBackgroundThread(new ZKRunnable() {
            
            
            @Override
            public void run(ZKThread thread) throws Exception {
                Bundle bundle = fhirService.getClient().search().byUrl(url).returnBundle(Bundle.class).execute();
                thread.setAttribute("bundle", bundle);
            }
            
            @Override
            public void abort() {
            }
            
        });
    }
    
    @Override
    protected void threadFinished(ZKThread thread) {
        try {
            thread.rethrow();
        } catch (Throwable e) {
            status("An unexpected error was encountered:  " + ZKUtil.formatExceptionForDisplay(e));
            return;
        }
        
        model.clear();
        initModel(FhirUtil.getEntries((Bundle) thread.getAttribute("bundle"), resourceClass));
        renderData();
    }
    
    protected abstract void initModel(List<R> entries);
    
    @Override
    protected void asyncAbort() {
        abortBackgroundThreads();
    }
    
    /**
     * Show detail for specified list item.
     *
     * @param li The list item.
     */
    protected void showDetail(Listitem li) {
        @SuppressWarnings("unchecked")
        M modelObject = li == null ? null : (M) li.getValue();
        String detail = modelObject == null ? null : getDetail(modelObject);
        
        if (!StringUtils.isEmpty(detail)) {
            if (getShowDetailPane()) {
                detailView.setContent(detail);
            } else {
                Map<Object, Object> map = new HashMap<>();
                map.put("title", detailTitle);
                map.put("content", detail);
                map.put("allowPrint", getAllowPrint());
                try {
                    ((Window) ZKUtil.loadZulPage(Constants.RESOURCE_PREFIX + "resourceListDetail.zul", null, map)).doModal();
                } catch (Exception e) {
                    PromptDialog.showError(e);
                }
            }
        }
    }
    
    protected String getDetail(M modelObject) {
        if (modelObject instanceof DomainResource) {
            XhtmlNode detail = ((DomainResource) modelObject).getText().getDiv();
            return detail == null ? null : detail.getValueAsString();
        }
        
        return null;
    }
    
    /**
     * Display detail when item is selected.
     */
    @Override
    protected void itemSelected(Listitem li) {
        showDetail(li);
    }
    
    @Override
    protected void init() {
        super.init();
        committed();
    }
    
    public BaseService getFhirService() {
        return fhirService;
    }
    
    public void setFhirService(BaseService fhirService) {
        this.fhirService = fhirService;
    }
    
}
