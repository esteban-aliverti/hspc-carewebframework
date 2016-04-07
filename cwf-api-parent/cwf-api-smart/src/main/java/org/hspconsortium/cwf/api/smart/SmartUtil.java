/*
 * #%L
 * cwf-api-smart
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
package org.hspconsortium.cwf.api.smart;

import org.carewebframework.api.spring.SpringUtil;

import org.springframework.util.Assert;

/**
 * Static utility class.
 */
public class SmartUtil {
    
    
    private static final String SMART_CONTEXT_PREFIX = "smart.context.";
    
    /**
     * Returns the SMART context implementation corresponding to the specified scope.
     * 
     * @param contextScope The name of the SMART context scope.
     * @return The context implementation.
     */
    public static ISmartContext getContext(String contextScope) {
        ISmartContext context = SpringUtil.getBean(SMART_CONTEXT_PREFIX + contextScope, ISmartContext.class);
        Assert.notNull(context, "Unknown SMART context type: " + contextScope);
        return context;
    }
    
    /**
     * Attaches a subscriber to a SMART context scope.
     * 
     * @param contextScope The name of the SMART context scope.
     * @param subscriber A SMART context subscriber.
     */
    public static void subscribe(String contextScope, ISmartContextSubscriber subscriber) {
        getContext(contextScope).subscribe(subscriber);
    }
    
    /**
     * Detaches a subscriber from a SMART context scope.
     * 
     * @param contextScope The name of the SMART context scope.
     * @param subscriber A SMART context subscriber.
     */
    public static void unsubscribe(String contextScope, ISmartContextSubscriber subscriber) {
        getContext(contextScope).unsubscribe(subscriber);
    }
    
    /**
     * Enforce static class.
     */
    private SmartUtil() {
    }
}
