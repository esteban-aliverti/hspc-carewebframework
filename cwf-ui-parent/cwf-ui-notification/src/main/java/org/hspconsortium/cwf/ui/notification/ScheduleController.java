/*
 * #%L
 * Notifications Plugin
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
/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.hspconsortium.cwf.ui.notification;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.carewebframework.api.context.UserContext;
import org.carewebframework.common.StrUtil;
import org.carewebframework.ui.FrameworkController;
import org.carewebframework.ui.zk.DateTimebox;
import org.carewebframework.ui.zk.ListUtil;
import org.carewebframework.ui.zk.PopupDialog;
import org.carewebframework.ui.zk.PromptDialog;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import org.hspconsortium.cwf.api.notification.AbstractNotification.Priority;
import org.hspconsortium.cwf.api.notification.NotificationService;
import org.hspconsortium.cwf.api.notification.Recipient;
import org.hspconsortium.cwf.api.notification.ScheduledNotification;
import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.fhir.common.FhirUtil;

import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;

/**
 * Controller for creating or editing a scheduled notification.
 */
public class ScheduleController extends FrameworkController {
    
    private static final long serialVersionUID = 1L;
    
    private static final String DIALOG = ZKUtil.getResourcePath(ScheduleController.class) + "schedule.zul";
    
    private NotificationService service;
    
    private ScheduledNotification notification;
    
    private final List<Recipient> recipients = new ArrayList<Recipient>();
    
    private DateTimebox dtbDelivery;
    
    private Combobox cboPriority;
    
    private Textbox txtSubject;
    
    private Textbox txtMessage;
    
    private Textbox txtRecipients;
    
    private Checkbox chkAssociate;
    
    private Label lblPatient;
    
    private Component pnlAssociate;
    
    /**
     * Display the scheduled notification dialog modally.
     *
     * @param notification The scheduled notification to be modified. Specify null to create a new
     *            scheduled notification.
     * @return The modified or new scheduled notification, or null if the dialog was cancelled.
     */
    public static ScheduledNotification show(ScheduledNotification notification) {
        Map<Object, Object> args = new HashMap<Object, Object>();
        args.put("notification", notification);
        Window dlg = PopupDialog.popup(DIALOG, args, true, false, true);
        return (ScheduledNotification) dlg.getAttribute("notification");
    }
    
    /**
     * Initialize the dialog.
     */
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        notification = (ScheduledNotification) arg.get("notification");
        
        for (Priority priority : Priority.values()) {
            Comboitem item = new Comboitem(PriorityRenderer.getDisplayName(priority), PriorityRenderer.getImage(priority));
            item.setValue(priority);
            cboPriority.appendChild(item);
        }
        
        if (notification == null) {
            notification = new ScheduledNotification();
            notification.setPriority(Priority.LOW);
            notification.setDeliveryDate(new Date());
            recipients.add(new Recipient(UserContext.getActiveUser()));
        } else {
            service.getScheduledNotificationRecipients(notification, recipients);
        }
        
        populateForm();
    }
    
    /**
     * Populate the dialog based on values from the scheduled notification.
     */
    private void populateForm() {
        dtbDelivery.setDate(notification.getDeliveryDate());
        dtbDelivery.setConstraint("no past");
        ListUtil.selectComboboxData(cboPriority, notification.getPriority());
        
        txtSubject.setValue(notification.getSubject());
        txtMessage.setValue(StrUtil.fromList(service.getScheduledNotificationMessage(notification)));
        
        if (notification.hasPatient()) {
            lblPatient.setValue(notification.getPatientName());
            chkAssociate.setChecked(true);
            chkAssociate.setValue(notification.getPatientId());
        } else {
            Patient patient = PatientContext.getActivePatient();
            
            if (patient == null) {
                pnlAssociate.setVisible(false);
            } else {
                String name = FhirUtil.formatName(patient.getName());
                IdentifierDt mrn = FhirUtil.getMRN(patient);
                lblPatient.setValue(name + " (" + (mrn == null ? "" : mrn.getValue()) + ")");
                chkAssociate.setValue(patient.getId().getIdPart());
            }
        }
        
        updateRecipients();
    }
    
    /**
     * Update the recipient text box based on the current recipient list.
     */
    private void updateRecipients() {
        StringBuilder sb = new StringBuilder();
        
        for (Recipient recipient : recipients) {
            if (sb.length() > 0) {
                sb.append("; ");
            }
            
            sb.append(recipient.getName());
        }
        
        txtRecipients.setText(sb.toString());
    }
    
    /**
     * Validate entries.
     *
     * @return True if all entries successfully validated. False otherwise.
     */
    private boolean validate() {
        if (StringUtils.trimToEmpty(txtSubject.getText()).isEmpty()) {
            wrongValue(txtSubject, "cwfnotification.schedule.validate.nosubject");
        } else if (dtbDelivery.getDate() == null) {
            wrongValue(dtbDelivery, "cwfnotification.schedule.validate.nodate");
        } else if (recipients.isEmpty()) {
            wrongValue(txtRecipients, "cwfnotification.schedule.validate.norecipients");
        } else {
            return true;
        }
        
        return false;
    }
    
    /**
     * Displays a validation error next to the specified component.
     *
     * @param comp The component that failed validation.
     * @param key The key of the label to display.
     */
    private void wrongValue(Component comp, String key) {
        Clients.wrongValue(comp, Labels.getLabel(key));
    }
    
    /**
     * Allows IOC container to inject notification service.
     *
     * @param service Notification service.
     */
    public void setNotificationService(NotificationService service) {
        this.service = service;
    }
    
    /**
     * Update the scheduled notification with new input values and send to the server, then close
     * the dialog if successful.
     */
    public void onClick$btnOK() {
        if (validate()) {
            notification.setDeliveryDate(new Date(dtbDelivery.getDate().getTime()));
            notification.setPatientId(chkAssociate.isChecked() ? (String) chkAssociate.getValue() : null);
            notification.setPatientName(chkAssociate.isChecked() ? lblPatient.getValue() : null);
            notification.setSubject(txtSubject.getValue());
            notification.setPriority((Priority) cboPriority.getSelectedItem().getValue());
            List<String> message = StrUtil.toList(txtMessage.getText());
            
            if (service.scheduleNotification(notification, message, recipients)) {
                root.setAttribute("notification", notification);
                root.detach();
            } else {
                PromptDialog.showError("@cwfnotification.schedule.save.failure");
            }
        }
    }
    
    /**
     * Show the recipients dialog.
     */
    public void onClick$btnRecipients() {
        if (RecipientsController.show(recipients)) {
            updateRecipients();
        }
    }
}
