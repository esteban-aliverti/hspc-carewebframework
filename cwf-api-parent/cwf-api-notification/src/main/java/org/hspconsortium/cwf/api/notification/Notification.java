/*
 * #%L
 * Notifications Support
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
package org.hspconsortium.cwf.api.notification;

import static org.carewebframework.common.StrUtil.U;

import java.util.Arrays;

import org.carewebframework.common.StrUtil;

import ca.uhn.fhir.model.dstu2.resource.Communication;

/**
 * A delivered notification.
 */
public class Notification extends AbstractNotification {
    
    
    private static final long serialVersionUID = 1L;
    
    private boolean actionable;
    
    private String patientId;
    
    private String patientLocation;
    
    private String type;
    
    private String alertId;
    
    private boolean canDelete;
    
    private Priority priority;
    
    public Notification() {
        
    }
    
    /**
     * Create a notification from server data.
     * 
     * @param data Format is <code>
     *    1        2           3               4             5            6              7      8       9        10         11         12
     * Priority^Info Only^Patient Name^ Patient Location ^Subject^Date Delivered^Sender Name^DFN^Alert Type^Alert ID^Can Delete^Extra Info
     * </code>
     */
    public Notification(Communication communication) {
        String data = null;
        String[] pcs = StrUtil.split(data, U, 12);
        setPriority(Priority.fromString(pcs[0]));
        setActionable(!StrUtil.toBoolean(pcs[1]));
        setPatientName(pcs[2]);
        setPatientLocation(pcs[3]);
        setSubject(pcs[4]);
        //setDeliveryDate(DateUtils.parseDate(pcs[5], null));
        setSenderName(pcs[6]);
        setPatientId(pcs[7]);
        setType(pcs[8]);
        setAlertId(pcs[9]);
        setCanDelete(StrUtil.toBoolean(pcs[10]));
        setExtraInfo(Arrays.copyOfRange(pcs, 11, pcs.length));
    }
    
    /**
     * True if the notification has an associated action.
     */
    @Override
    public boolean isActionable() {
        return actionable;
    }
    
    /**
     * Sets the actionable state of the notification.
     * 
     * @param actionable True if the notification has an associated action.
     */
    protected void setActionable(boolean actionable) {
        this.actionable = actionable;
    }
    
    /**
     * Returns the priority of the notification.
     */
    @Override
    public Priority getPriority() {
        return priority;
    }
    
    /**
     * Sets the priority of the notification.
     */
    @Override
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    /**
     * Returns the logical id of the associated patient, if any.
     */
    @Override
    public String getPatientId() {
        return patientId;
    }
    
    /**
     * Sets the logical id of the associated patient, if any.
     */
    @Override
    public void setPatientId(String id) {
        this.patientId = id;
    }
    
    /**
     * Returns the patient's current location.
     * 
     * @return Patient's location.
     */
    public String getPatientLocation() {
        return patientLocation;
    }
    
    /**
     * Sets the patient's current location.
     * 
     * @param patientLocation Patient's location.
     */
    protected void setPatientLocation(String patientLocation) {
        this.patientLocation = patientLocation;
    }
    
    /**
     * Returns the notification type.
     * 
     * @return The notification type.
     */
    public String getType() {
        return type;
    }
    
    /**
     * Sets the notification type.
     * 
     * @param type The notification type.
     */
    protected void setType(String type) {
        this.type = type;
    }
    
    /**
     * Returns the unique alert id.
     * 
     * @return Unique alert id.
     */
    public String getAlertId() {
        return alertId;
    }
    
    /**
     * Sets the unique alert id.
     * 
     * @param alertId Unique alert id.
     */
    protected void setAlertId(String alertId) {
        this.alertId = alertId;
    }
    
    /**
     * Returns true if the notification can be deleted.
     */
    @Override
    public boolean canDelete() {
        return canDelete;
    }
    
    /**
     * Allow or disallow manual notification deletion.
     * 
     * @param canDelete If true, the user may delete the notification manually.
     */
    protected void setCanDelete(boolean canDelete) {
        this.canDelete = canDelete;
    }
    
    @Override
    /**
     * Two notifications are the same if their unique id's are the same.
     */
    public boolean equals(Object object) {
        return object instanceof Notification && ((Notification) object).alertId.equals(alertId);
    }
    
}
