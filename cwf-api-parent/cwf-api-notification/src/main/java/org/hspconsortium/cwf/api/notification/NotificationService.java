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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.carewebframework.common.StrUtil;

import ca.uhn.fhir.model.dstu2.resource.Communication;
import ca.uhn.fhir.model.dstu2.resource.Patient;

/**
 * Data access services for notifications.
 */
public class NotificationService {
    
    
    private final String scheduledPrefix;
    
    public NotificationService(String scheduledPrefix) {
        this.scheduledPrefix = scheduledPrefix;
    }
    
    /**
     * Returns a bolus of mail groups.
     * 
     * @param startFrom Starting entry.
     * @param forward Direction of traversal.
     * @param result Result of lookup.
     */
    public void getGroups(String startFrom, boolean forward, Collection<Recipient> result) {
        List<String> lst = null; //TODO: logic to retrieve groups
        toRecipients(lst, true, startFrom, result);
    }
    
    /**
     * Returns a bolus of users.
     * 
     * @param startFrom Starting entry.
     * @param forward Direction of traversal.
     * @param result Result of lookup.
     */
    public void getUsers(String startFrom, boolean forward, Collection<Recipient> result) {
        List<String> lst = null; //TODO: logic to lookup users
        toRecipients(lst, false, startFrom, result);
    }
    
    /**
     * Returns notifications for the current user.
     * 
     * @param patient If not null, only notifications associated with the current user are returned.
     *            Otherwise, all notifications for the current user are returned.
     * @param result The list to receive the results.
     */
    public void getNotifications(Patient patient, Collection<Notification> result) {
        List<Communication> lst = null;
        result.clear();
        
        if (patient == null) {
            lst = null; //TODO: logic to get all notifications
        } else if (patient != null) {
            lst = null; //TODO: logic to get patient-specific and unassociated notifications
        }
        
        if (lst != null) {
            for (Communication item : lst) {
                result.add(new Notification(item));
            }
        }
    }
    
    /**
     * Delete a notification.
     * 
     * @param notification The notification to delete.
     * @return True if the operation was successful.
     */
    public boolean deleteNotification(Notification notification) {
        boolean result = notification.canDelete();
        
        if (result) {
            //TODO: logic to delete a notification
        }
        
        return result;
    }
    
    /**
     * Forward multiple notifications.
     * 
     * @param notifications List of notifications to forward.
     * @param recipients List of recipients.
     * @param comment Comment to attach to forwarded notification.
     */
    public void forwardNotifications(Collection<Notification> notifications, Collection<Recipient> recipients,
                                     String comment) {
        List<String> lst1 = new ArrayList<String>();
        
        for (Notification notification : notifications) {
            lst1.add(notification.getAlertId());
        }
        
        List<String> lst2 = prepareRecipients(recipients);
        
        if (!lst1.isEmpty() && !lst2.isEmpty()) {
            //TODO: logic to forward a notification
        }
    }
    
    /**
     * Prepares a recipient list for passing to an RPC.
     * 
     * @param recipients List of recipients.
     * @return List of recipients to pass to RPC.
     */
    private List<String> prepareRecipients(Collection<Recipient> recipients) {
        List<String> lst = new ArrayList<>();
        
        for (Recipient recipient : recipients) {
            lst.add(recipient.getUserId());
        }
        
        return lst;
    }
    
    /**
     * Creates a list of recipients from a list of raw data.
     * 
     * @param recipientData List of raw data as returned by lookup.
     * @param isGroup If true, the list represents mail groups. If false, it represents users.
     * @param filter The text used in the lookup. It will be used to limit the returned results.
     * @param result List of recipients.
     */
    private void toRecipients(List<String> recipientData, boolean isGroup, String filter, Collection<Recipient> result) {
        result.clear();
        
        for (String data : recipientData) {
            Recipient recipient = new Recipient(data, isGroup);
            
            if (StringUtils.startsWithIgnoreCase(recipient.getName(), filter)) {
                result.add(recipient);
            } else {
                break;
            }
        }
    }
    
    /**
     * Returns the message associated with a notification, fetching it from the server if necessary.
     * 
     * @param notification A notification.
     * @return Message associated with the notification.
     */
    public List<String> getNotificationMessage(Notification notification) {
        List<String> message = notification.getMessage();
        
        if (message == null) {
            message = null;//TODO: logic to retrieve a notification message body.
            notification.setMessage(message);
        }
        
        return message;
    }
    
    /**
     * Returns all scheduled notifications for the current user.
     * 
     * @param result The list to receive the results.
     */
    public void getScheduledNotifications(Collection<ScheduledNotification> result) {
        List<String> lst = null; //TODO: logic to retrieve scheduled notifications
        result.clear();
        
        for (String data : lst) {
            result.add(new ScheduledNotification(data));
        }
    }
    
    /**
     * Delete a scheduled notification.
     * 
     * @param notification Scheduled notification to delete.
     * @return True if the operation was successful.
     */
    public boolean deleteScheduledNotification(ScheduledNotification notification) {
        return true; //TODO: logic to delete a scheduled notification
    }
    
    /**
     * Returns a list of recipients associated with a scheduled notification.
     * 
     * @param notification A scheduled notification.
     * @param result Recipients associated with the notification.
     */
    public void getScheduledNotificationRecipients(ScheduledNotification notification, Collection<Recipient> result) {
        List<String> lst = null; //TODO: logic to retrieve recipients.
        result.clear();
        
        for (String data : lst) {
            result.add(new Recipient(data));
        }
    }
    
    /**
     * Returns the message associated with a scheduled notification.
     * 
     * @param notification A scheduled notification.
     * @return Message associated with the scheduled notification.
     */
    public List<String> getScheduledNotificationMessage(ScheduledNotification notification) {
        return null; //TODO: logic to retrieve scheduled notification message body
    }
    
    /**
     * Creates a scheduled notification. If the notification is replacing an existing one, the
     * existing one will be first deleted and a new one created in its place.
     * 
     * @param notification Notification to be scheduled.
     * @param message The associated message, if any.
     * @param recipients The target recipients.
     * @return True if the notification was successfully scheduled.
     */
    public boolean scheduleNotification(ScheduledNotification notification, List<String> message,
                                        Collection<Recipient> recipients) {
        if (notification.getIen() > 0) {
            deleteScheduledNotification(notification);
        }
        
        String extraInfo = StrUtil.fromList(Arrays.asList(notification.getExtraInfo()), StrUtil.U);
        return true; //TODO: logic to create a scheduled notification.
    }
}
