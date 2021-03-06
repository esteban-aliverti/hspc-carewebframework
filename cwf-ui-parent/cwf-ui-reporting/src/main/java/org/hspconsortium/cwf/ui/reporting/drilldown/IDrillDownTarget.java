/*
 * #%L
 * cwf-ui-reporting
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
package org.hspconsortium.cwf.ui.reporting.drilldown;

/**
 * Data objects may implement this interface if special initialization needs to be done prior to a
 * drilldown (for example, if additional data fields must be populated prior to the drilldown) or if
 * the drilldown object must be constructed on-the-fly. In the former case, the method call could
 * return the original data object reference, but with the augmented data fields. In the latter
 * case, the method call may return an entirely different data object.
 *
 * @author dmartin
 */
public interface IDrillDownTarget {
    
    Object getDetailObject();
}
