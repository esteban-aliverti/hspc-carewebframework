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

import java.util.Date;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;

import org.apache.commons.lang.StringUtils;

import org.hspconsortium.cwf.api.patientlist.PatientListItem;
import org.carewebframework.common.DateUtil;
import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.carewebframework.ui.zk.AbstractListitemRenderer;

import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listitem;

/**
 * Renderer for patient list items.
 */
public class PatientListItemRenderer extends AbstractListitemRenderer<Object, Object> {
    
    private static final PatientListItemRenderer instance = new PatientListItemRenderer();
    
    /**
     * Return singleton instance.
     * 
     * @return Patient list item renderer.
     */
    public static PatientListItemRenderer getInstance() {
        return instance;
    }
    
    /**
     * Force singleton usage.
     */
    private PatientListItemRenderer() {
        super("", null);
    }
    
    /**
     * Render a list item.
     * 
     * @param item The list item to render.
     * @param object The associated PatientListItem or Patient object.
     */
    @Override
    public void renderItem(Listitem item, Object object) {
        PatientListItem patientListItem;
        
        if (object instanceof PatientListItem) {
            patientListItem = (PatientListItem) object;
        } else if (object instanceof Patient) {
            patientListItem = new PatientListItem((Patient) object, null);
        } else {
            throw new RuntimeException("Invalid object type: " + object);
        }
        
        item.setValue(patientListItem);
        Patient patient = patientListItem.getPatient();
        // If list headers are defined, limit rendering to that number of cells.
        Listhead head = item.getListbox().getListhead();
        int max = head == null ? 0 : head.getChildren().size();
        String info = patientListItem.getInfo();
        
        if (patient != null) {
            HumanNameDt name = FhirUtil.getName(patient.getName(), NameUseEnum.USUAL, null);
            
            if (name == null) {
                name = FhirUtil.parseName(Labels.getLabel("patientselection.warn.unknown.patient"));
            }
            
            addCell(item, StringUtils.join(name.getFamily(), " "), max);
            addCell(item, StringUtils.join(name.getGiven(), " "), max);
            addCell(item, FhirUtil.getMRNString(patient), max);
            
            if (StringUtils.isEmpty(info)) {
                Date dob = patient.getBirthDate();
                info = dob == null ? "" : DateUtil.formatDate(dob);
            }
        }
        
        addCell(item, info, max);
    }
    
    /**
     * Add a cell to the list item.
     * 
     * @param item List item to receive the cell.
     * @param label Text label for the cell.
     * @param max Maximum # of allowable cells.
     */
    private boolean addCell(Listitem item, String label, int max) {
        if (max == 0 || item.getChildren().size() < max) {
            createCell(item, label);
            return true;
        }
        
        return false;
    }
}
