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

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.carewebframework.api.context.ContextMarshaller;
import org.carewebframework.api.context.UserContext;
import org.carewebframework.api.security.mock.MockUser;
import org.carewebframework.api.test.CommonTest;
import org.carewebframework.common.DateUtil;

import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;
import org.hspconsortium.cwf.api.patient.PatientContext;
import org.hspconsortium.cwf.api.patient.PatientContext.IPatientContextEvent;
import org.hspconsortium.cwf.fhir.common.FhirTerminology;
import org.hspconsortium.cwf.fhir.common.HumanNameParser;
import org.junit.Ignore;
import org.junit.Test;

public class PatientContextTest extends CommonTest {
    
    
    /**
     * Should accept context change on first survey and refuse on all subsequent surveys.
     */
    private class ContextChangeSubscriber implements IPatientContextEvent {
        
        
        private String reason = "";
        
        @Override
        public void canceled() {
        }
        
        @Override
        public void committed() {
        }
        
        @Override
        public String pending(boolean silent) {
            String result = reason;
            reason = "refuse change";
            return result;
        }
        
    }
    
    DateFormat dateFormat = new SimpleDateFormat("mm/dd/yyyy");
    
    @Test
    public void changePatientContext() throws Exception {
        HumanNameParser hnp = new HumanNameParser();
        changeUserContext();
        Patient patient1 = new Patient();
        patient1.setId("321");
        patient1.getName().add(hnp.fromString(null, "Smith, Joe"));
        Identifier ssn = new Identifier();
        ssn.setType(FhirTerminology.IDENT_SSN);
        ssn.setValue("999-99-9999");
        patient1.getIdentifier().add(ssn);
        patient1.setBirthDate(DateUtil.parseDate("1958-07-27"));
        Patient patient2 = new Patient();
        patient2.setId("123");
        patient2.getName().add(hnp.fromString(null, "Doe, Jane"));
        Identifier ssn2 = new Identifier();
        ssn2.setType(FhirTerminology.IDENT_SSN);
        ssn2.setValue("123-45-6789");
        patient2.getIdentifier().add(ssn2);
        patient2.setBirthDate(DateUtil.parseDate("1963-05-01"));
        Object subscriber = new ContextChangeSubscriber(); // Create a patient context change subscriber
        appFramework.registerObject(subscriber); // Register it with the context manager
        PatientContext.changePatient(patient1); // Request a context change
        assertSame(patient1, PatientContext.getActivePatient()); // This time should succeed
        PatientContext.changePatient(patient2); // Reattempt the context change
        assertNotSame(patient2, PatientContext.getActivePatient()); // Subscriber should have refused the context change
        appFramework.unregisterObject(subscriber); // Unregister the subscriber
        PatientContext.changePatient(patient2); // Reattempt the context change
        assertSame(patient2, PatientContext.getActivePatient()); // This time should succeed
    }
    
    public void changeUserContext() throws Exception {
        MockUser user = new MockUser();
        UserContext.changeUser(user);
    }
    
    @Test
    @Ignore
    public void marshalling() throws Exception {
        changePatientContext();
        ContextMarshaller marshaller = contextManager.getContextMarshaller("keystore-test");
        String ctx = marshaller.marshal(contextManager.getMarshaledContext());
        String sig = marshaller.sign(ctx);
        PatientContext.changePatient((Patient) null);
        assertNull(PatientContext.getActivePatient());
        marshaller.unmarshal(ctx, sig);
        Patient patient = PatientContext.getActivePatient();
        assertTrue("Doe, Jane".equalsIgnoreCase(new HumanNameParser().toString(patient.getName().get(0))));
    }
    
}
