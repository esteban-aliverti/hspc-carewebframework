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
package org.hspconsortium.cwf.api.context;

import static org.junit.Assert.assertEquals;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.valueset.NameUseEnum;

import org.hspconsortium.cwf.api.NameSerializer;

import org.junit.Test;

public class NameSerializerTest {
    
    @Test
    public void test() {
        NameSerializer ns = new NameSerializer();
        HumanNameDt name = new HumanNameDt();
        name.addFamily("Martin");
        name.addGiven("Douglas");
        name.addGiven("Kent");
        name.addPrefix("Dr.");
        name.addSuffix("M.D.");
        name.getUseElement().setValueAsEnum(NameUseEnum.OFFICIAL);
        String s = ns.serialize(name);
        System.out.println(s);
        HumanNameDt name2 = ns.deserialize(s);
        assertEquals(name.getFamily(), name2.getFamily());
        assertEquals(name.getGiven(), name2.getGiven());
        assertEquals(name.getPrefix(), name2.getPrefix());
        assertEquals(name.getSuffix(), name2.getSuffix());
        assertEquals(name.getUse(), name2.getUse());
    }
    
}
