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

import java.lang.reflect.Constructor;

import org.apache.commons.lang.BooleanUtils;

import org.carewebframework.api.FrameworkUtil;
import org.carewebframework.api.property.PropertyUtil;
import org.hspconsortium.cwf.ui.reporting.Constants;

import org.zkoss.zk.ui.Component;

/**
 * Utility methods for drilldowns.
 */
public class DrillDownUtil {
    
    /**
     * Returns true if drill down icons are enabled.
     *
     * @return True if drill down icons are enabled.
     */
    public static boolean showIcons() {
        Boolean result = (Boolean) FrameworkUtil.getAttribute(Constants.PROPERTY_ID_DRILLDOWN);
        
        if (result == null) {
            try {
                String value = PropertyUtil.getValue(Constants.PROPERTY_ID_DRILLDOWN);
                result = BooleanUtils.toBoolean(value);
            } catch (Exception e) {
                result = true;
            }
            
            FrameworkUtil.setAttribute(Constants.PROPERTY_ID_DRILLDOWN, result);
        }
        
        return result;
    }
    
    /**
     * Invokes a drill down display given the specified parameters.
     *
     * @param parent Parent component for the drilldown.
     * @param dataObject Data object that is the target of the drilldown.
     * @param drillDownDisplayClass Class of the drilldown dialog.
     * @throws Exception Unspecified exception.
     */
    public static void showDrillDown(Component parent, Object dataObject, Class<?> drillDownDisplayClass) throws Exception {
        if (dataObject instanceof IDrillDownTarget) {
            dataObject = ((IDrillDownTarget) dataObject).getDetailObject();
        }
        
        Class<?>[] constructorParamTypes = new Class<?>[2];
        constructorParamTypes[0] = Component.class;
        constructorParamTypes[1] = dataObject.getClass();
        Object[] constructorArgs = new Object[2];
        constructorArgs[0] = parent;
        constructorArgs[1] = dataObject.getClass().cast(dataObject);
        Constructor<?> c = drillDownDisplayClass.getConstructor(constructorParamTypes);
        Object drillDownDisplayObj = c.newInstance(constructorArgs);
        Object[] drillDownDisplayObjArgs = null;
        Class<?>[] drillDownDisplayMethodParamTypes = null;
        drillDownDisplayObj.getClass().getMethod("show", drillDownDisplayMethodParamTypes)
                .invoke(drillDownDisplayObj, drillDownDisplayObjArgs);
    }
    
    /**
     * Creates a drill down link of the appropriate type.
     *
     * @param drillDownDisplayClass Class of the drilldown dialog.
     * @param dataObject This is the value associated with the drill down.
     * @param iconParent For icon-based drill downs, this is the parent of the icon.
     * @param clickTarget For link-based drill downs, this is the component that will trigger the
     *            drill down.
     */
    public static void createDrillDown(Class<? extends DrillDownDisplay> drillDownDisplayClass, Object dataObject,
                                       Component iconParent, Component clickTarget) {
        if (drillDownDisplayClass != null) {
            if (showIcons()) {
                if (iconParent != null) {
                    new DrillDownLink<Object>(dataObject, drillDownDisplayClass).setParent(iconParent);
                }
            } else if (clickTarget != null) {
                new DrillDownListener<Object>(clickTarget, dataObject, drillDownDisplayClass);
            }
        }
    }
    
    /**
     * Enforces static class.
     */
    private DrillDownUtil() {
    };
}
