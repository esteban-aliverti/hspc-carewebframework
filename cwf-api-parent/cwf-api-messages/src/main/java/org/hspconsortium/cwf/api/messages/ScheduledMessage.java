/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is also subject to the terms of the Health-Related Additional
 * Disclaimer of Warranty and Limitation of Liability available at
 * http://www.carewebframework.org/licensing/disclaimer.
 */
package org.hspconsortium.cwf.api.messages;

import java.util.Date;
import java.util.Properties;

/**
 * A scheduled message.
 */
public class ScheduledMessage implements IMessageWrapper<ScheduledMessage> {
    
    
    private String id;
    
    private Date deliveryDate;
    
    private String subject;
    
    private final Properties extraInfo = new Properties();
    
    public ScheduledMessage() {
        
    }
    
    public ScheduledMessage(String id, Date deliveryDate, String subject, String... extraInfo) {
        this.id = id;
        this.deliveryDate = deliveryDate;
        this.subject = subject;
        
        for (String info : extraInfo) {
            String[] pcs = info.split("\\=", 2);
            this.extraInfo.setProperty(pcs[0], pcs.length == 1 ? "" : pcs[1]);
        }
    }
    
    /**
     * Returns the id of the scheduled message.
     * 
     * @return The id. Will be null if this is a new message.
     */
    @Override
    public String getId() {
        return id;
    }
    
    /**
     * Sets the id of the message.
     * 
     * @param id The id. Will be null if this is a new message.
     */
    protected void setId(String id) {
        this.id = id;
    }
    
    /**
     * Returns the urgency of this message.
     * 
     * @return Urgency of the message.
     */
    @Override
    public Urgency getUrgency() {
        return Urgency.fromString(getParam("PRI"));
    }
    
    /**
     * Sets the urgency of this message.
     * 
     * @param urgency Urgency of the message.
     */
    public void setUrgency(Urgency urgency) {
        setParam("PRI", urgency.ordinal() + 1);
    }
    
    /**
     * Returns the logical id of the associated patient, or null if no associated patient.
     * 
     * @return Id of the associated patient, if any.
     */
    @Override
    public String getPatientId() {
        return getParam("patientId");
    }
    
    /**
     * Sets the logical id of the associated patient. Use null if no associated patient.
     * 
     * @param patientId Logical id of the associated patient.
     */
    public void setPatientId(String patientId) {
        setParam("patientId", patientId);
    }
    
    @Override
    public Date getDeliveryDate() {
        return deliveryDate;
    }
    
    @Override
    public String getSubject() {
        return subject;
    }
    
    /**
     * Extract a parameter from name/value pairs in extra info.
     * 
     * @param param Parameter name.
     * @return Parameter value, or null if not found.
     */
    @Override
    public String getParam(String param) {
        return extraInfo.getProperty(param);
    }
    
    /**
     * Sets the value for a parameter in extra info.
     * 
     * @param param Parameter name.
     * @param value Parameter value (null to remove).
     */
    public void setParam(String param, Object value) {
        if (value == null) {
            extraInfo.remove(param);
        } else {
            extraInfo.setProperty(param, value.toString());
        }
    }
    
    @Override
    public boolean hasPatient() {
        return getPatientId() != null;
    }
    
    @Override
    public String getPatientName() {
        return getParam("patientName");
    }
    
    @Override
    public String getDisplayText() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public boolean isActionable() {
        return false;
    }
    
    @Override
    public boolean canDelete() {
        return false;
    }
    
    @Override
    public ScheduledMessage getMessage() {
        return this;
    }
    
    @Override
    public String getAlertId() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getBody() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void setDeliveryDate(Date date) {
        // TODO Auto-generated method stub
        
    }
    
    public void setPatientName(String string) {
        // TODO Auto-generated method stub
        
    }
    
    public void setSubject(String value) {
        // TODO Auto-generated method stub
        
    }
    
}
