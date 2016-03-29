/*
 * #%L
 * UCS Messaging API
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
package org.hspconsortium.cwf.api.ucs;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import org.carewebframework.common.NumUtil;
import org.carewebframework.common.StrUtil;

public enum Urgency {
    HIGH, MEDIUM, LOW;
    
    /**
     * Returns the urgency associated with the input value.
     * 
     * @param value The input value. May either be numeric, or the urgency name.
     * @return The corresponding urgency.
     */
    public static Urgency fromString(String value) {
        if (StringUtils.isNumeric(value)) {
            return Urgency.values()[NumUtil.enforceRange(NumberUtils.toInt(value) - 1, 0, 2)];
        } else {
            return valueOf(value);
        }
    }
    
    /**
     * Returns the name of the image resource representing the graphical representation of the
     * urgency.
     * 
     * @return The image resource name.
     */
    public String getIcon() {
        return getLabel("icon");
    }
    
    /**
     * Returns the color to be used when displaying alerts.
     * 
     * @return A color.
     */
    public String getColor() {
        return getLabel("color");
    }
    
    /**
     * Returns the display name for the urgency.
     * 
     * @return Display name
     */
    public String getDisplayName() {
        return getLabel("label");
    }
    
    /**
     * Returns the label property for the specified attribute name and urgency.
     * 
     * @param name The attribute name.
     * @return The label value.
     */
    private String getLabel(String name) {
        return StrUtil.getLabel("cwfmessages.urgency." + name + "." + name());
    }
    
};
