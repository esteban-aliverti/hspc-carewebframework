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
import java.util.List;

import ca.uhn.fhir.model.api.BasePrimitive;
import ca.uhn.fhir.model.dstu2.composite.AddressDt;
import ca.uhn.fhir.model.dstu2.composite.ContactPointDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AddressUseEnum;
import ca.uhn.fhir.model.primitive.StringDt;

import org.apache.commons.lang.StringUtils;

import org.hspconsortium.cwf.ui.util.Util;
import org.carewebframework.common.DateUtil;
import org.hspconsortium.cwf.fhir.common.FhirUtil;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Separator;

/**
 * Default class for rendering detail view of patient in patient selection dialog. This class may be
 * overridden to provide an alternate detail view.
 */
public class PatientDetailRenderer implements IPatientDetailRenderer {
    
    /**
     * Render detail view for the specified patient.
     * 
     * @param patient Patient whose detail view is to be rendered.
     * @param root Root component under which detail should be constructed.
     */
    @Override
    public Component render(Component root, Patient patient, Object... supportData) {
        if (confirmAccess(patient, root)) {
            renderDemographics(patient, root);
        }
        
        return null;
    }
    
    protected void renderDemographics(Patient patient, Component root) {
        root.appendChild(new Separator());
        Image photo = new Image();
        photo.setStyle("max-height:300px;max-width:300px");
        photo.setContent(Util.getImage(patient.getPhoto(), Util.SILHOUETTE_IMAGE));
        root.appendChild(photo);
        addDemographic(root, null, FhirUtil.formatName(patient.getName()), "font-weight: bold");
        addDemographic(root, "mrn", FhirUtil.getMRNString(patient));
        addDemographic(root, "gender", patient.getGender());
        //addDemographic(root, "race", org.springframework.util.StringUtils.collectionToCommaDelimitedString(patient.getRace()));
        addDemographic(root, "age", DateUtil.formatAge(patient.getBirthDate()));
        addDemographic(root, "dob", patient.getBirthDate());
        addDemographic(root, "dod", patient.getDeceased());
        //addDemographic(root, "mother", patient.getMothersFirstName());
        addDemographic(root, "language", patient.getLanguage());
        addContact(root, patient.getTelecom(), "home:phone", null);
        addContact(root, patient.getTelecom(), "home:email", null);
        addContact(root, patient.getTelecom(), "home:fax", "home fax");
        addContact(root, patient.getTelecom(), "work:phone", null);
        addContact(root, patient.getTelecom(), "work:email", null);
        addContact(root, patient.getTelecom(), "work:fax", "work fax");
        
        AddressDt address = FhirUtil.getAddress(patient.getAddress(), AddressUseEnum.HOME);
        
        if (address != null) {
            root.appendChild(new Separator());
            
            for (StringDt line : address.getLine()) {
                addDemographic(root, null, line.getValue());
            }
            
            String city = StringUtils.defaultString(address.getCity());
            String state = StringUtils.defaultString(address.getState());
            String zip = StringUtils.defaultString(address.getPostalCode());
            String sep = city.isEmpty() || state.isEmpty() ? "" : ", ";
            addDemographic(root, null, city + sep + state + "  " + zip);
        }
        
    }
    
    /**
     * Confirm access to patient.
     * 
     * @param patient The patient to check.
     * @param root The root component.
     * @return True if access confirmed.
     */
    private boolean confirmAccess(Patient patient, Component root) {
        boolean allowed = confirmAccess(patient);
        
        if (!allowed) {
            addDemographic(root, null, getDemographicLabel("restricted"), "font-weight: bold");
        }
        
        return allowed;
    }
    
    /**
     * Override to restrict access to certain patients.
     * 
     * @param patient The patient to check.
     * @return True if access confirmed.
     */
    protected boolean confirmAccess(Patient patient) {
        return true; //!patient.isRestricted();
    }
    
    /**
     * Adds a contact element to the demographic panel. Uses default styling.
     * 
     * @param root Root component.
     * @param contacts List of contacts from which to select.
     * @param type Type of contact desired (e.g., "home:phone").
     * @param labelId The id of the label to use.
     */
    protected void addContact(Component root, List<ContactPointDt> contacts, String type, String labelId) {
        ContactPointDt contact = FhirUtil.getContact(contacts, type);
        
        if (contact != null) {
            addDemographic(root, labelId == null ? contact.getUse() : labelId, contact.getValue(), null);
        }
    }
    
    /**
     * Adds a demographic element to the demographic panel. Uses default styling.
     * 
     * @param root Root component.
     * @param labelId The id of the label to use.
     * @param object The element to be added.
     */
    protected void addDemographic(Component root, String labelId, Object object) {
        addDemographic(root, labelId, object, null);
    }
    
    /**
     * Adds a demographic element to the demographic panel.
     * 
     * @param root Root component.
     * @param labelId The id of the label to use.
     * @param object The element to be added.
     * @param style CSS styling to apply to element (may be null).
     */
    protected void addDemographic(Component root, String labelId, Object object, String style) {
        object = object instanceof BasePrimitive ? ((BasePrimitive<?>) object).getValue() : object;
        String value = object == null ? null : object instanceof Date ? DateUtil.formatDate((Date) object) : object
                .toString().trim();
        
        if (!StringUtils.isEmpty(value)) {
            Label lbl = new Label((labelId == null ? "" : getDemographicLabel(labelId) + ": ") + value);
            root.appendChild(lbl);
            
            if (style != null) {
                lbl.setStyle(style);
            }
        }
        
    }
    
    /**
     * Returns the text for the specified label id.
     * 
     * @param labelId The id of the label value to locate. If no prefix is present, the id is
     *            prefixed with "patient.selection.demographic.label." to find the associated value.
     * @return Label text.
     */
    protected String getDemographicLabel(String labelId) {
        return Labels.getLabel(labelId.contains(".") ? labelId : "patientselection.demographic.label." + labelId);
    }
    
}
