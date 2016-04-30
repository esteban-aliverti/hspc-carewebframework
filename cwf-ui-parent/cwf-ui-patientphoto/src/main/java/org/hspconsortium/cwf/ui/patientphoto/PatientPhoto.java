/*
 * #%L
 * cwf-ui-patientphoto
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
package org.hspconsortium.cwf.ui.patientphoto;

import org.carewebframework.ui.FrameworkController;

import org.zkoss.image.AImage;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Popup;

import org.hl7.fhir.dstu3.model.HumanName.NameUse;
import org.hl7.fhir.dstu3.model.Patient;
import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.hspconsortium.cwf.ui.util.Util;

/**
 * Controller for patient photo plugin.
 */
public class PatientPhoto extends FrameworkController implements PatientContext.IPatientContextEvent {
    
    
    private static final long serialVersionUID = 1L;
    
    private Image imgPhoto;
    
    private Image imgFullPhoto;
    
    private Popup popup;
    
    private Label lblCaption;
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        committed();
    }
    
    @Override
    public void canceled() {
    }
    
    @Override
    public void committed() {
        Patient patient = PatientContext.getActivePatient();
        AImage image = patient == null ? null : Util.getImage(patient.getPhoto());
        
        if (patient == null) {
            imgPhoto.setSrc(Util.NOPATIENT_IMAGE);
            imgPhoto.setTooltip((Popup) null);
            imgPhoto.setTooltiptext(Labels.getLabel("patientphoto.no.patient"));
        } else if (image == null) {
            imgPhoto.setSrc(Util.SILHOUETTE_IMAGE);
            imgPhoto.setTooltip((Popup) null);
            imgPhoto.setTooltiptext(Labels.getLabel("patientphoto.no.photo"));
        } else {
            imgPhoto.setContent(image);
            imgPhoto.setTooltiptext(null);
            imgPhoto.setTooltip(popup);
            imgFullPhoto.setContent(image);
            lblCaption.setValue(
                patient == null ? "" : FhirUtil.formatName(patient.getName(), NameUse.USUAL, NameUse.OFFICIAL, null));
        }
        
    }
    
    @Override
    public String pending(boolean silent) {
        return null;
    }
    
}
