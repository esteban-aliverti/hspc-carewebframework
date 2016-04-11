/*
 * #%L
 * Medication Administration Record
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
package org.hspconsortium.cwf.ui.mar.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.socraticgrid.fhir.generated.IQICoreMedicationAdministration;
import org.socraticgrid.fhir.generated.QICoreMedicationAdministrationAdapter;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;

public class MarModelTest {
    
    
    @Test
    public void testMarInit() {
        List<IQICoreMedicationAdministration> medAdmins = new ArrayList<IQICoreMedicationAdministration>();
        for (int i = 0; i < 5; i++) {
            QICoreMedicationAdministrationAdapter adapter = new QICoreMedicationAdministrationAdapter();
            MedicationAdministration medAdmin = new MedicationAdministration();
            adapter.setAdaptee(medAdmin);//TODO Fix in code generator. Do this in constructor!
            Date date = new Date();
            date.setMinutes(date.getMinutes() - i * 10);//TODO Fix and use Joda Time
            adapter.setEffectiveTimeDateTime(date);
            CodeableConceptDt medCode = new CodeableConceptDt();
            CodingDt code = new CodingDt().setCode("" + i).setSystem("http://domain.org/meds").setDisplay("Med" + i);
            adapter.setMedicationCodeableConcept(medCode);
            medAdmins.add(adapter);
        }
        //		MarModel model = new MarModel(medAdmins);
        //		assertEquals(7, model.getHeaders().size());
        //		assertEquals(5, model.getRows().size());
        //		assertEquals("Medication", model.getHeaders().get(0));
        //		assertEquals("Action", model.getHeaders().get(6));
    }
    
}
