/*
 * #%L
 * cwf-ui-patientselection-core
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
package org.hspconsortium.cwf.ui.patientselection;

import org.carewebframework.common.AbstractRegistry;
import org.carewebframework.common.RegistryMap.DuplicateAction;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Registry for patient selectors.
 */
public class PatientSelectorRegistry extends AbstractRegistry<String, IPatientSelectorFactory> implements BeanPostProcessor {
    
    private static final PatientSelectorRegistry instance = new PatientSelectorRegistry();
    
    public static PatientSelectorRegistry getInstance() {
        return instance;
    }
    
    /**
     * Enforce singleton instance.
     */
    private PatientSelectorRegistry() {
        super(DuplicateAction.ERROR);
    }
    
    @Override
    protected String getKey(IPatientSelectorFactory item) {
        return item.getFactoryBeanId();
    }
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof PatientSelectorFactoryBase) {
            PatientSelectorFactoryBase factory = (PatientSelectorFactoryBase) bean;
            factory.setFactoryBeanId(beanName);
            register(factory);
        }
        
        return bean;
    }
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
    
}
