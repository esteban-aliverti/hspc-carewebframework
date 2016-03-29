/*
 * #%L
 * Message Viewer Plugin
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
package org.hspconsortium.cwf.ui.messagebox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.cogmedsys.hsp.api.ucs.MessageUtils;

import org.carewebframework.api.context.UserContext;
import org.carewebframework.api.event.IGenericEvent;
import org.carewebframework.common.NumUtil;
import org.carewebframework.common.StrUtil;
import org.carewebframework.ui.sharedforms.CaptionedForm;
import org.carewebframework.ui.zk.MessageWindow;
import org.carewebframework.ui.zk.MessageWindow.MessageInfo;
import org.carewebframework.ui.zk.PromptDialog;
import org.carewebframework.ui.zk.RowComparator;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Image;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;

import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.api.patient.PatientContext.IPatientContextEvent;
import org.hspconsortium.cwf.api.ucs.MessageService;
import org.hspconsortium.cwf.api.ucs.MessageWrapper;
import org.hspconsortium.cwf.api.ucs.Urgency;
import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.socraticgrid.hl7.services.uc.model.Message;
import org.socraticgrid.hl7.services.uc.model.UserContactInfo;

import ca.uhn.fhir.model.dstu2.resource.Patient;

/**
 * Controller for main message display.
 */
public class MainController extends CaptionedForm implements IPatientContextEvent {
    
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Response types for information-only message processing.
     */
    private enum Response {
        YES, NO, ALL, CANCEL;
        
        @Override
        public String toString() {
            return Labels.getLabel("cwfmessages.response.label." + name());
        }
    }
    
    /**
     * Recognized message actions.
     */
    private enum Action {
        CHECK, ADD, SCHEDULE, INFO, REFRESH, DELETE, MONITOR;
    }
    
    // This is the listener for notification action messages.
    private final IGenericEvent<String> actionListener = new IGenericEvent<String>() {
        
        
        @Override
        public void eventCallback(String eventName, String messageId) {
            Action action = Action.valueOf(StrUtil.piece(eventName, ".", 2));
            
            switch (action) {
                case ADD:
                    addMessage(service.getMessageWithId(messageId));
                    break;
                
                case INFO:
                    MessageWrapper message = findMessage(messageId);
                    
                    if (message != null) {
                        highlightMessage(message);
                        getContainer().bringToFront();
                    }
                    
                    break;
                
                case REFRESH:
                    refresh();
                    break;
                
                case DELETE:
                    removeMessage(messageId);
                    break;
            }
        }
        
    };
    
    private Listbox lstMessages;
    
    private Radiogroup rgFilter;
    
    private Radio radAll;
    
    private Radio radPatient;
    
    private Button btnAll;
    
    private Button btnSelected;
    
    private Button btnInfoAll;
    
    private Button btnForward;
    
    private Button btnDelete;
    
    private Image imgIndicator;
    
    private MessageService service;
    
    private ProcessingController processingController;
    
    private final ListModelList<MessageWrapper> model = new ListModelList<>();
    
    private boolean showAll = true;
    
    private Urgency alertThreshold = Urgency.HIGH;
    
    private int alertDuration = 30;
    
    private boolean isProcessing;
    
    private Patient patient;
    
    /**
     * Expose icon urls for auto-wiring.
     */
    @Override
    public void doBeforeComposeChildren(Component comp) throws Exception {
        super.doBeforeComposeChildren(comp);
        comp.setAttribute("iconInfoOnly", Constants.ICON_INFO);
        comp.setAttribute("iconActionable", Constants.ICON_ACTIONABLE);
        comp.setAttribute("iconUrgency", Constants.ICON_PRIORITY);
        comp.setAttribute("iconType", Constants.ICON_TYPE);
        comp.setAttribute("iconIndicator", Constants.ICON_INDICATOR);
        comp.setAttribute("iconUrgencyHigh", Urgency.HIGH.getIcon());
        comp.setAttribute("iconUrgencyMedium", Urgency.MEDIUM.getIcon());
        comp.setAttribute("iconUrgencyLow", Urgency.LOW.getIcon());
    }
    
    /**
     * Set up display.
     */
    @Override
    public void init() {
        super.init();
        service.addAlertListener(new AlertListener(this));
        service.addMessageListener(new MessageListener(this));
        getContainer().registerProperties(this, "showAll", "alertDuration", "alertThreshold");
        rgFilter.setSelectedItem(showAll ? radAll : radPatient);
        processingController = ProcessingController.create(this);
        lstMessages.setItemRenderer(new MessageRenderer());
        RowComparator.autowireColumnComparators(lstMessages.getListhead().getChildren());
        model.setMultiple(true);
        updatePatient(true);
        subscribe(true);
    }
    
    @Override
    public void cleanup() {
        subscribe(false);
    }
    
    /**
     * Refresh the display.
     */
    @Override
    public void refresh() {
        lstMessages.setModel((ListModel<?>) null);
        loadMessages(!radAll.isChecked());
        lstMessages.setModel(model);
        Clients.resize(lstMessages);
        updateControls(false);
    }
    
    private void loadMessages(boolean currentPatientOnly) {
        List<Message> messages = service.getAllMessages();
        String userId = UserContext.getActiveUser().getLogicalId();
        String patientId = currentPatientOnly && patient != null ? patient.getId().getIdPart() : null;
        messages = MessageUtils.filterMessagesByRecipient(messages, userId);
        model.clear();
        
        for (Message message : messages) {
            MessageWrapper mm = new MessageWrapper(message);
            
            if (currentPatientOnly) {
                String assocPatientId = mm.getPatientId();
                
                if (assocPatientId != null && assocPatientId != patientId) {
                    continue;
                }
            }
            
            model.add(mm);
        }
    }
    
    /**
     * Update controls to reflect the current selection state.
     *
     * @param processingUpdate If true, a processing status update has occurred.
     */
    private void updateControls(boolean processingUpdate) {
        btnAll.setDisabled(isProcessing || model.isEmpty());
        btnDelete.setDisabled(isProcessing || !canDeleteSelected());
        btnInfoAll.setDisabled(isProcessing || !hasInfoOnly());
        btnSelected.setDisabled(isProcessing || model.getSelection().isEmpty());
        btnForward.setDisabled(isProcessing || btnSelected.isDisabled());
        radAll.setStyle(radAll.isChecked() ? Constants.BOLD : Constants.NO_BOLD);
        radPatient.setStyle(radPatient.isChecked() ? Constants.BOLD : Constants.NO_BOLD);
        
        if (processingUpdate) {
            lstMessages.setDisabled(isProcessing);
            ZKUtil.disableChildren(lstMessages, isProcessing);
        }
    }
    
    /**
     * Returns true if any selected message may be deleted.
     *
     * @return True if any selected message may be deleted.
     */
    private boolean canDeleteSelected() {
        for (MessageWrapper message : model.getSelection()) {
            if (message.canDelete()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Returns true if any selected message is information only.
     *
     * @return True if any selected message is information only.
     */
    private boolean hasInfoOnly() {
        for (MessageWrapper message : model) {
            if (!message.isActionable()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Prompts user for input, returning the response.
     *
     * @param prompt Text prompt.
     * @param responses Valid responses.
     * @return Selected response.
     */
    private Response getResponse(String prompt, Response... responses) {
        return PromptDialog.show(prompt, null, responses);
    }
    
    /**
     * Adds a message to the model unless filtered. Will generate a slide-down message alert if its
     * urgency exceeds the set threshold.
     *
     * @param msg Message.
     */
    protected void addMessage(Message msg) {
        MessageWrapper message = new MessageWrapper(msg);
        
        if (radAll.isChecked()
                || (message.hasPatient() && patient != null && message.getPatientId().equals(patient.getId().getIdPart()))) {
            model.add(message);
        }
        
        if (alertThreshold != null && message.getUrgency().ordinal() <= alertThreshold.ordinal()) {
            MessageInfo mi = new MessageInfo(message.getDisplayText(), "New Message", message.getUrgency().getColor(),
                    alertDuration * 1000, null, "cwf.fireLocalEvent('ALERT.INFO', '" + message.getAlertId() + "');");
            getEventManager().fireLocalEvent(MessageWindow.EVENT_SHOW, mi);
        }
    }
    
    /**
     * Removes a message from this list, if it exists.
     * 
     * @param id The message id.
     */
    protected void removeMessage(String id) {
        MessageWrapper message = findMessage(id);
        
        if (message != null) {
            model.remove(message);
        }
        
    }
    
    /**
     * Locates and returns a message based on its unique id.
     *
     * @param id Message id.
     * @return Message with a matching id, or null if not found in the current model.
     */
    private MessageWrapper findMessage(String id) {
        for (MessageWrapper message : model) {
            if (message.getId().equals(id)) {
                return message;
            }
        }
        
        return null;
    }
    
    /**
     * Places the highlight indicator next to the specified message. If the message is not found or
     * is null, the indicator is hidden.
     *
     * @param message Message to highlight.
     */
    protected void highlightMessage(MessageWrapper message) {
        int i = message == null ? -1 : model.indexOf(message);
        
        if (i >= 0) {
            Listitem item = lstMessages.getItemAtIndex(i);
            imgIndicator.setParent(item.getFirstChild());
            imgIndicator.setVisible(true);
            Clients.scrollIntoView(item);
        } else {
            imgIndicator.setVisible(false);
        }
    }
    
    /**
     * Clears all selections.
     */
    private void clearSelection() {
        lstMessages.clearSelection();
        updateControls(false);
    }
    
    /**
     * Update controls when the selection changes.
     */
    public void onSelect$lstMessage() {
        updateControls(false);
    }
    
    /**
     * Refresh the display.
     */
    public void onClick$btnRefresh() {
        refresh();
    }
    
    /**
     * Delete selected messages.
     */
    public void onClick$btnDelete() {
        boolean silent = false;
        
        LOOP: for (MessageWrapper message : model.getSelection()) {
            String s = message.getDisplayText();
            
            if (message.canDelete()) {
                if (!silent) {
                    String msg = StrUtil.getLabel("cwfmessages.main.delete.confirm.prompt", s);
                    
                    switch (getResponse(msg, Response.YES, Response.NO, Response.ALL, Response.CANCEL)) {
                        case NO:
                            continue;
                        
                        case ALL:
                            silent = true;
                            break;
                        
                        case CANCEL:
                            break LOOP;
                    }
                }
                service.acknowledgeMessage(message.getId());
            } else {
                String msg = StrUtil.getLabel("cwfmessages.main.delete.unable.prompt", s);
                
                if (getResponse(msg, Response.YES, Response.CANCEL) != Response.YES) {
                    break;
                }
            }
        }
    }
    
    /**
     * Refresh the display when the filter changes.
     */
    public void onCheck$radAll() {
        refresh();
    }
    
    /**
     * Refresh the display when the filter changes.
     */
    public void onCheck$radPatient() {
        refresh();
    }
    
    /**
     * Invoke the scheduled message management dialog.
     */
    public void onClick$btnSchedule() {
        SchedulingController.show();
    }
    
    /**
     * Process all messages.
     */
    public void onClick$btnAll() {
        processingController.process(model);
    }
    
    /**
     * Process all information-only messages.
     */
    public void onClick$btnInfoAll() {
        processingController.process(getMessagesToProcess(true));
    }
    
    /**
     * Process selected messages.
     */
    public void onClick$btnSelected() {
        processingController.process(getMessagesToProcess(false));
    }
    
    /**
     * Process a double-clicked message.
     *
     * @param event The process item event.
     */
    public void onProcessItem$lstMessage(Event event) {
        event = ZKUtil.getEventOrigin(event);
        Listitem item = (Listitem) event.getTarget();
        MessageWrapper message = (MessageWrapper) item.getValue();
        processingController.process(Collections.singleton(message));
    }
    
    /**
     * Forward selected messages.
     */
    public void onClick$btnForward() {
        Set<UserContactInfo> recipients = new HashSet<>();
        String comment = RecipientsController.showWithComment(recipients);
        
        if (comment != null && !recipients.isEmpty()) {
            List<MessageWrapper> messages = getMessagesToProcess(false);
            clearSelection();
            
            for (MessageWrapper message : messages) {
                service.forwardMessage(message.getMessage(), recipients, comment);
            }
        }
    }
    
    /**
     * Return messages to be processed.
     *
     * @param infoOnly If true, return all information-only messages. If false, return only selected
     *            messages.
     * @return List of messages to be processed.
     */
    private List<MessageWrapper> getMessagesToProcess(boolean infoOnly) {
        List<MessageWrapper> list = new ArrayList<>();
        
        for (MessageWrapper message : model) {
            if (!infoOnly && model.isSelected(message)) {
                list.add(message);
            } else if (infoOnly && !message.isActionable()) {
                list.add(message);
            }
        }
        
        return list;
    }
    
    /**
     * Conditionally suppress patient context changes.
     */
    @Override
    public String pending(boolean silent) {
        return null;
    }
    
    /**
     * Update display when patient context changes.
     */
    @Override
    public void committed() {
        updatePatient(radPatient.isChecked());
    }
    
    /**
     * Update display for currently selected patient.
     *
     * @param refresh If true, force a refresh before returning.
     */
    private void updatePatient(boolean refresh) {
        patient = PatientContext.getActivePatient();
        
        radPatient.setLabel(patient == null ? Labels.getLabel("cwfmessages.main.patient.not.selected")
                : FhirUtil.formatName(patient.getName()));
        
        if (refresh) {
            refresh();
        }
    }
    
    @Override
    public void canceled() {
    }
    
    /**
     * Subscribe to/unsubscribe from selected events.
     *
     * @param doSubscribe If true, subscribe. If false, unsubscribe.
     */
    private void subscribe(boolean doSubscribe) {
        for (Action action : Action.values()) {
            String eventName = "ALERT." + action.name();
            
            if (doSubscribe) {
                getEventManager().subscribe(eventName, actionListener);
            } else {
                getEventManager().unsubscribe(eventName, actionListener);
            }
        }
    }
    
    /**
     * Allows IOC container to inject UCS.
     *
     * @param service Message service.
     */
    public void setUcsMessageService(MessageService service) {
        this.service = service;
    }
    
    /**
     * Returns show all setting. If true, all messages are displayed, regardless of any patient
     * association. If false, only messages associated with the selected patient are displayed.
     *
     * @return The show all setting.
     */
    public boolean getShowAll() {
        return showAll;
    }
    
    /**
     * Sets the show all setting. If true, all messages are displayed, regardless of any patient
     * association. If false, only messages associated with the selected patient are displayed.
     *
     * @param value The show all setting.
     */
    public void setShowAll(boolean value) {
        showAll = value;
        
        if (rgFilter != null) {
            rgFilter.setSelectedItem(showAll ? radAll : radPatient);
            refresh();
        }
    }
    
    /**
     * Returns the alert threshold as a string value. This threshold determines which newly arriving
     * messages cause a slide-down message alert to be displayed.
     *
     * @return The alert threshold.
     */
    public Urgency getAlertThreshold() {
        return alertThreshold;
    }
    
    /**
     * Sets the alert threshold as a string value. This threshold determines which newly arriving
     * messages cause a slide-down message alert to be displayed.
     *
     * @param value The alert threshold.
     */
    public void setAlertThreshold(Urgency value) {
        this.alertThreshold = value;
    }
    
    /**
     * Returns the duration, in seconds, of any slide-down message alert.
     *
     * @return Alert duration in seconds.
     */
    public int getAlertDuration() {
        return alertDuration;
    }
    
    /**
     * Sets the duration, in seconds, of any slide-down message alert.
     *
     * @param alertDuration Alert duration in seconds.
     */
    public void setAlertDuration(int alertDuration) {
        this.alertDuration = NumUtil.enforceRange(alertDuration, 1, 999999);
    }
    
    public void setProcessing(boolean isProcessing) {
        this.isProcessing = isProcessing;
        updateControls(true);
    }
}
