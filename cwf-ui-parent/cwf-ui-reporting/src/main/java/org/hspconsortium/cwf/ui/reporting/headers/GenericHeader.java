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
package org.hspconsortium.cwf.ui.reporting.headers;

import java.util.Date;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.DateDt;

import org.apache.commons.lang3.StringUtils;

import org.carewebframework.api.context.UserContext;
import org.carewebframework.api.domain.IUser;
import org.carewebframework.api.event.IGenericEvent;
import org.hspconsortium.cwf.api.patient.PatientContext;
import org.carewebframework.common.DateUtil;
import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.carewebframework.ui.FrameworkController;

import org.zkoss.zk.ui.Component;
import org.zkoss.zkplus.databind.AnnotateDataBinder;

/**
 * This is the generic controller for the stock report headers.
 */
public class GenericHeader extends FrameworkController {
    
    private static final long serialVersionUID = 1L;
    
    private AnnotateDataBinder binder;
    
    private final String contextEvent;
    
    private final IGenericEvent<Object> eventListener = new IGenericEvent<Object>() {
        
        @Override
        public void eventCallback(String eventName, Object eventData) {
            refresh();
        }
        
    };
    
    public GenericHeader() {
        this(null);
    }
    
    public GenericHeader(String contextEvent) {
        super();
        this.contextEvent = contextEvent;
    }
    
    /**
     * Creates an annotation binder for the controller.
     *
     * @param comp The component.
     * @throws Exception Unspecified exception.
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        binder = new AnnotateDataBinder(comp);
        
        if (contextEvent != null) {
            subscribe(contextEvent, true);
        }
        refresh();
    }
    
    /**
     * Retrieves a formatted header for the current patient.
     *
     * @return Formatted header.
     */
    public String getPatientInfo() {
        Patient patient = PatientContext.getActivePatient();
        String text;
        
        if (patient == null) {
            text = "No Patient Selected";
        } else {
            IdentifierDt mrn = FhirUtil.getMRN(patient); // May be null!
            text = FhirUtil.formatName(patient.getName());
            
            if (mrn != null) {
                text += "  #" + mrn.getValue();
            }
            
            String gender = patient.getGender();
            
            if (!StringUtils.isEmpty(gender)) {
                text += "   (" + gender + ")";
            }
            
            Date deceased = patient.getDeceased() instanceof DateDt ? ((DateDt) patient.getDeceased()).getValue() : null;
            String age = DateUtil.formatAge(patient.getBirthDate(), true, deceased);
            text += "  Age: " + age;
            
            if (deceased != null) {
                text += "  Died: " + DateUtil.formatDate(deceased);
            }
        }
        
        return text;
    }
    
    /**
     * Retrieves a formatted header for the current user.
     *
     * @return Formatted header.
     */
    public String getUserInfo() {
        IUser user = UserContext.getActiveUser();
        return user == null ? "No User Selected" : user.getFullName();
    }
    
    /**
     * Returns the current date in standard format.
     *
     * @return Timestamp for current date.
     */
    public String getTimestamp() {
        return DateUtil.formatDate(DateUtil.stripTime(new Date()));
    }
    
    /**
     * Rebind form data when context changes.
     */
    @Override
    public void refresh() {
        binder.loadAll();
    }
    
    /**
     * Subscribe or unsubscribe from context change event.
     *
     * @param eventName The event name.
     * @param subscribe If true, subscribe; if false, unsubscribe.
     */
    private void subscribe(String eventName, boolean subscribe) {
        if (subscribe) {
            getEventManager().subscribe(eventName, eventListener);
        } else {
            getEventManager().unsubscribe(eventName, eventListener);
        }
    }
    
}
