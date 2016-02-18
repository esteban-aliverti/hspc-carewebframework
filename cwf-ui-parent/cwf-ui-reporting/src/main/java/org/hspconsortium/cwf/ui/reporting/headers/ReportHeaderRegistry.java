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
package org.hspconsortium.cwf.ui.reporting.headers;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hspconsortium.cwf.ui.reporting.Constants;
import org.carewebframework.common.RegistryMap;
import org.carewebframework.common.RegistryMap.DuplicateAction;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zk.ui.util.UiLifeCycle;
import org.zkoss.zul.Idspace;
import org.zkoss.zul.Include;

/**
 * Registry for report headers to be used with print function.
 */
public class ReportHeaderRegistry {
    
    private static final Log log = LogFactory.getLog(ReportHeaderRegistry.class);
    
    private static final ReportHeaderRegistry instance = new ReportHeaderRegistry();
    
    /**
     * This helper class instantiates a hidden instance of each report header when a new desktop is
     * created.
     */
    public static class Init implements UiLifeCycle {
        
        public Init() {
            Execution exec = Executions.getCurrent();
            exec.getDesktop().addListener(this);
        }
        
        @Override
        public void afterComponentAttached(Component comp, Page page) {
        }
        
        @Override
        public void afterComponentDetached(Component comp, Page prevpage) {
        }
        
        @Override
        public void afterComponentMoved(Component parent, Component child, Component prevparent) {
        }
        
        @Override
        public void afterPageAttached(Page page, Desktop desktop) {
            desktop.removeListener(this);
            ReportHeaderRegistry rhr = getInstance();
            Idspace headerRoot = new Idspace();
            headerRoot.setPage(page);
            headerRoot.setStyle("display:none");
            headerRoot.setId("report_headers");
            
            for (Entry<String, String> entry : rhr.map.entrySet()) {
                String key = entry.getKey();
                String url = entry.getValue();
                try {
                    Include root = new Include();
                    root.setId(key);
                    root.setSrc(url);
                    root.setParent(headerRoot);
                } catch (Exception e) {
                    log.error("Error loading report header " + key, e);
                }
            }
        }
        
        @Override
        public void afterPageDetached(Page page, Desktop prevdesktop) {
        }
        
        @Override
        public void afterShadowAttached(ShadowElement shadow, Component host) {
        }
        
        @Override
        public void afterShadowDetached(ShadowElement shadow, Component prevhost) {
        }
    }
    
    static {
        instance.register("user", Constants.RESOURCE_PREFIX + "userReportHeader.zul");
        instance.register("patient", Constants.RESOURCE_PREFIX + "patientReportHeader.zul");
    }
    
    private final Map<String, String> map = new RegistryMap<String, String>(DuplicateAction.ERROR);
    
    public static ReportHeaderRegistry getInstance() {
        return instance;
    }
    
    /**
     * Enforce singleton instance.
     */
    private ReportHeaderRegistry() {
        super();
    }
    
    public void register(String headerName, String url) {
        this.map.put(headerName, url);
    }
    
}
