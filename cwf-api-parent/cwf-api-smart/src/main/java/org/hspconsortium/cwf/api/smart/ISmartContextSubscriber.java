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

import org.hspconsortium.cwf.api.smart.SmartContextBase.ContextMap;

/**
 * Interface to be implemented by every SMART context subscriber.
 */
public interface ISmartContextSubscriber {
    
    
    /**
     * Allows a SMART context to notify a subscriber of a context change.
     * 
     * @param contextScope The name of the SMART context scope.
     * @param context The context map containing the updated state.
     */
    void updateContext(String contextScope, ContextMap context);
    
}
