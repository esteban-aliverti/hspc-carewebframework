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

import org.apache.commons.lang.StringUtils;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

/**
 * Creates a drilldown event listener.
 *
 * @param <T> Class of drill down data object.
 */
public class DrillDownListener<T> implements EventListener<Event> {
    
    private final T dataObject;
    
    private final Class<?> drillDownDisplayClass;
    
    private final Component component;
    
    /**
     * Create a drilldown event listener for the specified component.
     *
     * @param component The component to which the event listener will be attached.
     * @param dataObject Data object for the drilldown.
     * @param drillDownDisplayClass Dialog class for the drilldown display.
     */
    public DrillDownListener(Component component, T dataObject, Class<?> drillDownDisplayClass) {
        super();
        this.component = component;
        this.dataObject = dataObject;
        this.drillDownDisplayClass = drillDownDisplayClass;
        component.addEventListener(Events.ON_CLICK, this);
        
        if (component instanceof HtmlBasedComponent) {
            ((HtmlBasedComponent) component).setStyle(StringUtils.trimToEmpty(((HtmlBasedComponent) component).getStyle())
                    .concat("cursor:pointer;"));
        }
    }
    
    @Override
    public void onEvent(Event event) throws Exception {
        DrillDownUtil.showDrillDown(component, dataObject, drillDownDisplayClass);
    }
    
}
