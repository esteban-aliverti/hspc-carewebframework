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

import org.carewebframework.api.domain.IUser;

/**
 * Notification recipient.
 */
public class Recipient {
    
    private final IUser user;
    
    /**
     * Creates a recipient based on the specified user.
     *
     * @param user A user.
     */
    public Recipient(IUser user) {
        this.user = user;
    }
    
    /**
     * Creates a recipient based on raw data.
     *
     * @param data Raw data.
     * @param isGroup If true, the raw data represents a group. If false, the type of recipient is
     *            to be inferred from the raw data.
     */
    public Recipient(String data, boolean isGroup) {
        this((IUser) null); //TODO
    }
    
    /**
     * Creates a recipient based on raw data.
     *
     * @param data Raw data.
     */
    public Recipient(String data) {
        this(data, false);
    }
    
    /**
     * Returns true if this recipient is a mail group.
     *
     * @return True if a mail group.
     */
    public boolean isGroup() {
        return false; //TODO
    }
    
    /**
     * Returns the recipient name.
     *
     * @return The recipient name.
     */
    public String getName() {
        return user.getFullName();
    }
    
    /**
     * Returns the recipient's internal entry number. Note that a negative value means that this is
     * a mail group, while a positive value means it is a user.
     *
     * @return Recipient's id.
     */
    public String getUserId() {
        return user.getLogicalId();
    }
    
};
