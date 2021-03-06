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

/**
 * Interface to be implemented by every SMART context.
 */
public interface ISmartContext {
    
    
    String getContextScope();
    
    /**
     * Registers the subscriber to receive notification of changes to the SMART context.
     * 
     * @param subscriber The subscriber.
     */
    void subscribe(ISmartContextSubscriber subscriber);
    
    /**
     * Unregisters the subscriber to no longer receive notification of changes to the SMART context.
     * 
     * @param subscriber The subscriber.
     */
    void unsubscribe(ISmartContextSubscriber subscriber);
}
