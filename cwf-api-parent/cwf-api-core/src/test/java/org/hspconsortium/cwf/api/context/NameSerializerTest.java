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

import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.HumanName.NameUse;
import org.hspconsortium.cwf.api.NameSerializer;
import org.junit.Test;

public class NameSerializerTest {
    
    
    @Test
    public void test() {
        NameSerializer ns = new NameSerializer();
        HumanName name = new HumanName();
        name.addFamily("Martin");
        name.addGiven("Douglas");
        name.addGiven("Kent");
        name.addPrefix("Dr.");
        name.addSuffix("M.D.");
        name.getUseElement().setValue(NameUse.OFFICIAL);
        String s = ns.serialize(name);
        System.out.println(s);
        HumanName name2 = ns.deserialize(s);
        assertEquals(name.getFamily(), name2.getFamily());
        assertEquals(name.getGiven(), name2.getGiven());
        assertEquals(name.getPrefix(), name2.getPrefix());
        assertEquals(name.getSuffix(), name2.getSuffix());
        assertEquals(name.getUse(), name2.getUse());
    }
    
}
