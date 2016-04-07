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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import org.carewebframework.common.JSONUtil;

/**
 * Holds a manifest for a SMART app.
 */
public class SmartManifest {
    
    
    private final Map<String, String> map = new HashMap<>();
    
    public SmartManifest() {
    }
    
    public SmartManifest(Map<String, String> map) {
        init(map);
    }
    
    /**
     * Creates a SmartManifest object from an input stream.
     * 
     * @param strm Input stream.
     * @throws IOException An IO exception.
     */
    @SuppressWarnings("unchecked")
    public SmartManifest(InputStream strm) throws IOException {
        try {
            init(JSONUtil.getMapper().readValue(strm, Map.class));
        } finally {
            IOUtils.closeQuietly(strm);
        }
    }
    
    public void init(Map<String, String> map) {
        this.map.clear();
        
        if (map != null) {
            this.map.putAll(map);
        }
    }
    
    public void init(SmartManifest manifest) {
        init(manifest == null ? null : manifest.map);
    }
    
    /**
     * Returns a manifest value as a string.
     * 
     * @param key The manifest key.
     * @return The manifest value.
     */
    public String getValue(String key) {
        return map.get(key);
    }
}
