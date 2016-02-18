/*
 * #%L
 * cwf-api-core
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
package org.hspconsortium.cwf.api;

import java.util.List;

import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.primitive.StringDt;

import org.carewebframework.common.ISerializer;

/**
 * CCOW serializer / deserializer for HumanName class.
 */
public class NameSerializer implements ISerializer<HumanNameDt> {
    
    private static final String NAME_DELIM = "^";
    
    @Override
    public String serialize(HumanNameDt value) {
        return getComponent(value.getFamily(), 0) + NAME_DELIM + getComponent(value.getGiven(), 0) + NAME_DELIM
                + getComponent(value.getGiven(), 1) + NAME_DELIM + getComponent(value.getSuffix(), 0) + NAME_DELIM
                + getComponent(value.getPrefix(), 0) + NAME_DELIM + getComponent(value.getSuffix(), 1) + NAME_DELIM
                + value.getUse();
    }
    
    private String getComponent(List<StringDt> list, int index) {
        if (index >= list.size()) {
            return "";
        }
        
        StringDt value = list.get(index);
        String result = value.getValue();
        return result == null ? "" : result;
    }
    
    @Override
    public HumanNameDt deserialize(String value) {
        String pcs[] = value.split("\\" + NAME_DELIM);
        HumanNameDt result = new HumanNameDt();
        int i = 0;
        
        if ((value = getComponent(pcs, i++)) != null) {
            result.addFamily(value);
        }
        
        if ((value = getComponent(pcs, i++)) != null) {
            result.addGiven(value);
        }
        
        if ((value = getComponent(pcs, i++)) != null) {
            result.addGiven(value);
        }
        
        if ((value = getComponent(pcs, i++)) != null) {
            result.addSuffix(value);
        }
        
        if ((value = getComponent(pcs, i++)) != null) {
            result.addPrefix(value);
        }
        
        if ((value = getComponent(pcs, i++)) != null) {
            result.addSuffix(value);
        }
        
        if ((value = getComponent(pcs, i++)) != null) {
            result.getUseElement().setValueAsString(value);
        }
        
        return result;
    }
    
    private String getComponent(String[] pcs, int index) {
        if (index >= pcs.length) {
            return null;
        }
        
        String result = pcs[index];
        return result == null || result.isEmpty() ? null : result;
    }
    
    @Override
    public Class<HumanNameDt> getType() {
        return HumanNameDt.class;
    }
}
