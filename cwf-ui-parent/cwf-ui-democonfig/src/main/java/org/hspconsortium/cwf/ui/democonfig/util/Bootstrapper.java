/*
 * #%L
 * Demo Configuration Plugin
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
package org.hspconsortium.cwf.ui.democonfig.util;

import static org.hspconsortium.cwf.fhir.common.FhirTerminology.IDENT_MRN;
import static org.hspconsortium.cwf.fhir.common.FhirTerminology.SYS_COGMED;
import static org.hspconsortium.cwf.fhir.common.FhirTerminology.SYS_RXNORM;
import static org.hspconsortium.cwf.fhir.common.FhirTerminology.SYS_SNOMED;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.DocumentReference;
import org.hl7.fhir.dstu3.model.DocumentReference.DocumentReferenceContentComponent;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.MedicationAdministration.MedicationAdministrationDosageComponent;
import org.hl7.fhir.dstu3.model.MedicationOrder;
import org.hl7.fhir.dstu3.model.MedicationOrder.MedicationOrderDosageInstructionComponent;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.hspconsortium.cwf.fhir.common.BaseService;
import org.hspconsortium.cwf.fhir.common.FhirUtil;

import ca.uhn.fhir.rest.api.MethodOutcome;

/**
 * Currently hard coded but in later iterations, bootstrapper should be configured based on a
 * configuration file to support greater flexibility during demos or connectathons.
 */
public class Bootstrapper {
    
    
    private static final String IMAGE_PATH = "web/org/hspconsortium/cwf/ui/democonfig/images/";
    
    private static final String NOTE_PATH = "web/org/hspconsortium/cwf/ui/democonfig/notes/";
    
    /**
     * Identifier used to locate demo resources for bulk deletes.
     */
    public static final Identifier DEMO_IDENTIFIER = createIdentifier("demo", "gen");
    
    public static final Identifier CONDITION_IDENTIFIER_1 = createIdentifier("condition", "1");
    
    public static final Identifier CONDITION_IDENTIFIER_2 = createIdentifier("condition", "2");
    
    public static final Identifier CONDITION_IDENTIFIER_3 = createIdentifier("condition", "3");
    
    public static final Identifier DOCUMENT_IDENTIFIER_1 = createIdentifier("document", "1");
    
    public static final Identifier DOCUMENT_IDENTIFIER_2 = createIdentifier("document", "2");
    
    public static final Identifier DOCUMENT_IDENTIFIER_3 = createIdentifier("document", "3");
    
    public static final Identifier PRACTITIONER_IDENTIFIER_1 = createIdentifier("practitioner", "1");
    
    public static final Identifier PRACTITIONER_IDENTIFIER_2 = createIdentifier("practitioner", "2");
    
    public static final Identifier PRACTITIONER_IDENTIFIER_3 = createIdentifier("practitioner", "3");
    
    public static final Identifier PATIENT_IDENTIFIER_1 = createIdentifier("patient", "1").setType(IDENT_MRN);
    
    public static final Identifier PATIENT_IDENTIFIER_2 = createIdentifier("patient", "2").setType(IDENT_MRN);
    
    public static final Identifier PATIENT_IDENTIFIER_3 = createIdentifier("patient", "3").setType(IDENT_MRN);
    
    private static final Log log = LogFactory.getLog(Bootstrapper.class);
    
    /**
     * FHIR service for managing resources.
     */
    BaseService fhirService;
    
    /**
     * Medication index
     */
    private final Map<String, CodeableConcept> medicationList = new HashMap<>();
    
    private final Map<String, CodeableConcept> conditionList = new HashMap<>();
    
    /**
     * Convenience method to create a codeable concept with a single coding. TODO May need to move
     * to some util class or check to see if not already present in HAPI FHIR.
     * 
     * @param system
     * @param code
     * @param displayName
     * @return
     */
    public static CodeableConcept createCodeableConcept(String system, String code, String displayName) {
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding().setSystem(system).setCode(code).setDisplay(displayName);
        return codeableConcept;
    }
    
    /**
     * Convenience method for creating identifiers in local system.
     * 
     * @param system
     * @param value
     * @return
     */
    public static Identifier createIdentifier(String system, String value) {
        Identifier identifier = new Identifier();
        identifier.setSystem("urn:cogmedsys:hsp:model:" + system);
        identifier.setValue(value);
        return identifier;
    }
    
    /**
     * Convenience method to create a time offset for MAR display
     * 
     * @param minuteOffset
     * @return
     */
    public static Date createDateWithMinuteOffset(long minuteOffset) {
        return new Date(System.currentTimeMillis() - minuteOffset * 60 * 1000);
    }
    
    /**
     * Convenience method to create a time offset for MAR display
     * 
     * @param dayOffset
     * @return
     */
    public static Date createDateWithDayOffset(long dayOffset) {
        return createDateWithMinuteOffset(dayOffset * 24 * 60);
    }
    
    /**
     * Convenience method to create a time offset for MAR display
     * 
     * @param yearOffset
     * @return
     */
    public static Date createDateWithYearOffset(long yearOffset) {
        return createDateWithDayOffset(yearOffset * 365);
    }
    
    /**
     * No-arg constructor which initializes medication index
     * 
     * @param fhirService
     */
    public Bootstrapper(BaseService fhirService) {
        this.fhirService = fhirService;
        populateMedicationCodes();
        populateConditionCodes();
    }
    
    // ------------- General operations -------------
    
    /**
     * Method clears all demo data.
     */
    public int deleteAll() {
        return deleteMedicationAdministrations() + deleteMedicationOrders() + deleteConditions() + deleteDocuments()
                + deletePractitioners() + deletePatients();
    }
    
    /**
     * Creates a resource on the server.
     * 
     * @param resource Resource to create.
     * @return True if created.
     */
    private boolean createResource(DomainResource resource) {
        MethodOutcome outcome = fhirService.createResource(resource);
        return outcome.getResource() != null;
    }
    
    /**
     * Get demo resources of given type.
     * 
     * @param clazz Type of demo resource.
     * @return List of demo resources.
     */
    private <T extends DomainResource> List<T> getResources(Class<T> clazz) {
        return fhirService.searchResourcesByIdentifier(DEMO_IDENTIFIER, clazz);
    }
    
    /**
     * Delete demo resources of given type.
     * 
     * @param clazz Type of demo resource.
     * @return Count of deleted resources
     */
    private <T extends DomainResource> int deleteResources(Class<T> clazz) {
        return fhirService.deleteResourcesByIdentifier(DEMO_IDENTIFIER, clazz);
    }
    
    // ------------- Medication Administration operations -------------
    
    /**
     * Method populates the patient record with some sample medication administrations.
     * 
     * @param patient
     * @return
     */
    public List<MedicationAdministration> addMedicationAdministrations(Patient patient) {
        List<MedicationOrder> medOrders = addMedicationOrders(patient);
        createResource(buildMedicationAdministration(patient, medicationList.get("metoprolol"), createDosageOneTablet(1),
            createDateWithMinuteOffset(45), medOrders.get(0)));
        createResource(buildMedicationAdministration(patient, medicationList.get("clopidogrel"), createDosageOneTablet(1),
            createDateWithMinuteOffset(35), medOrders.get(1)));
        return getResources(MedicationAdministration.class);
    }
    
    /**
     * Clears all medication administrations that share the given identifier.
     */
    public int deleteMedicationAdministrations() {
        return deleteResources(MedicationAdministration.class);
    }
    
    /**
     * Method builds a medication administration instance.
     * 
     * @param identifier
     * @param medCode
     * @param dose
     * @param effectiveDate
     * @param prescription
     * @return
     */
    private MedicationAdministration buildMedicationAdministration(Patient patient, CodeableConcept medCode,
                                                                   MedicationAdministrationDosageComponent dose,
                                                                   Date effectiveDate, MedicationOrder prescription) {
        MedicationAdministration medAdmin = new MedicationAdministration();
        medAdmin.addIdentifier(DEMO_IDENTIFIER);
        medAdmin.setPatient(new Reference(patient));
        medAdmin.setMedication(medCode);
        medAdmin.setEffectiveTime(new DateTimeType(effectiveDate));
        medAdmin.setDosage(dose);
        if (prescription != null) {
            medAdmin.setPrescription(new Reference(prescription));
        } else {
            throw new RuntimeException("An order must be associated with this medicationa administration");
        }
        return medAdmin;
    }
    
    /**
     * Convenience method for representing n tablets of medication X
     * 
     * @param numberOfTablets
     * @return
     */
    private MedicationAdministrationDosageComponent createDosageOneTablet(int numberOfTablets) {
        SimpleQuantity simpleQuantity = new SimpleQuantity();
        simpleQuantity.setValue(numberOfTablets);
        simpleQuantity.setUnit("{tbl}");
        MedicationAdministrationDosageComponent dose = new MedicationAdministrationDosageComponent()
                .setQuantity(simpleQuantity);
        return dose;
    }
    
    /**
     * Populates a medication index. TODO Build off a configuration file
     */
    private void populateMedicationCodes() {
        medicationList.clear();
        medicationList.put("metoprolol",
            createCodeableConcept(SYS_RXNORM, "372891", "Metoprolol Tartrate 25 MG Oral tablet"));
        medicationList.put("atenolol", createCodeableConcept(SYS_RXNORM, "197379", "Atenolol 100 MG Oral Tablet"));
        medicationList.put("bisoprolol",
            createCodeableConcept(SYS_RXNORM, "854901", "Bisoprolol Fumarate 10 MG Oral Tablet"));
        medicationList.put("clopidogrel", createCodeableConcept(SYS_RXNORM, "309362", "Clopidogrel 75 MG Oral Tablet"));
        medicationList.put("atorvastatin",
            createCodeableConcept(SYS_RXNORM, "597967", "Amlodipine 10 MG / Atorvastatin 20 MG Oral Tablet"));
        medicationList.put("acetaminophen",
            createCodeableConcept(SYS_RXNORM, "665056", "Acetaminophen 500 MG Chewable Tablet"));
        medicationList.put("aspirin", createCodeableConcept(SYS_RXNORM, "198466", "Aspirin 325 MG Oral Capsule"));
        medicationList.put("hydrochlorothiazide",
            createCodeableConcept(SYS_RXNORM, "310798", "Hydrochlorothiazide 25 MG Oral Tablet"));
        medicationList.put("bisacodyl", createCodeableConcept(SYS_RXNORM, "1550933", "Bisacodyl 5 MG Oral Tablet"));
        medicationList.put("acetazolamide", createCodeableConcept(SYS_RXNORM, "197304", "Acetazolamide 250 MG Oral Tablet"));
    }
    
    // ------------- Medication Order operations -------------
    
    /**
     * Clears all medication orders that share the given identifier.
     */
    public int deleteMedicationOrders() {
        return deleteResources(MedicationOrder.class);
    }
    
    /**
     * Method populates the patient record with some sample medication orders.
     * 
     * @param medOrderSetIdentifier
     * @param patient
     * @return
     */
    public List<MedicationOrder> addMedicationOrders(Patient patient) {
        createResource(buildMedicationOrder(patient, medicationList.get("metoprolol"),
            createDosageInstructionsOneTablet(1, "PO", "QD", null), createDateWithMinuteOffset(45)));
        createResource(buildMedicationOrder(patient, medicationList.get("clopidogrel"),
            createDosageInstructionsOneTablet(1, "PO", "QD", null), createDateWithMinuteOffset(35)));
        createResource(buildMedicationOrder(patient, medicationList.get("atorvastatin"),
            createDosageInstructionsOneTablet(1, "PO", "QD", null), createDateWithMinuteOffset(25)));
        createResource(buildMedicationOrder(patient, medicationList.get("acetaminophen"),
            createDosageInstructionsOneTablet(1, "PO", "Q8H", "1"), createDateWithMinuteOffset(15)));
        return getResources(MedicationOrder.class);
    }
    
    /**
     * Method builds a medication order instance.
     * 
     * @param identifier
     * @param medCode
     * @param dose
     * @param dateWritten
     * @return
     */
    private MedicationOrder buildMedicationOrder(Patient patient, CodeableConcept medCode,
                                                 MedicationOrderDosageInstructionComponent dose, Date dateWritten) {
        MedicationOrder medOrder = new MedicationOrder();
        medOrder.setPatient(new Reference(patient));
        medOrder.addIdentifier(DEMO_IDENTIFIER);
        medOrder.setMedication(medCode);
        medOrder.setDateWritten(dateWritten);
        medOrder.addDosageInstruction(dose);
        return medOrder;
    }
    
    /**
     * Convenience method for representing n tablets of medication X
     * 
     * @param numberOfTablets
     * @return
     */
    private MedicationOrderDosageInstructionComponent createDosageInstructionsOneTablet(int numberOfTablets,
                                                                                        String routeCode, String freqCode,
                                                                                        String prnCode) {
        SimpleQuantity simpleQuantity = new SimpleQuantity();
        simpleQuantity.setValue(numberOfTablets);
        simpleQuantity.setUnit("{tbl}");
        MedicationOrderDosageInstructionComponent dose = new MedicationOrderDosageInstructionComponent()
                .setDose(simpleQuantity);
        if (routeCode != null && routeCode.equalsIgnoreCase("PO")) {
            CodeableConcept route = createCodeableConcept(SYS_SNOMED, "26643006", "Oral route");
            dose.setRoute(route);
        }
        if (freqCode != null && freqCode.equalsIgnoreCase("QD")) {
            dose.getTiming().setRepeat(FhirUtil.getRepeatFromFrequencyCode(freqCode));
        } else if (freqCode != null && freqCode.equalsIgnoreCase("Q8H")) {
            dose.getTiming().setRepeat(FhirUtil.getRepeatFromFrequencyCode(freqCode));
        } else {
            log.error("Unknown frequency code " + freqCode);
        }
        if (prnCode != null && prnCode.equalsIgnoreCase("1")) {
            CodeableConcept prnCodeableConcept = createCodeableConcept(SYS_COGMED, "1", "As needed for pain");
            dose.setAsNeeded(prnCodeableConcept);
        } else if (prnCode != null && prnCode.equalsIgnoreCase("2")) {
            CodeableConcept prnCodeableConcept = createCodeableConcept(SYS_COGMED, "1", "As needed to control hypertension");
            dose.setAsNeeded(prnCodeableConcept);
        }
        return dose;
    }
    
    // ------------- Condition-related operations -------------
    
    /**
     * Method populates the patient record with some sample conditions.
     * 
     * @param patient
     * @return
     */
    public List<Condition> addConditions(Patient patient) {
        createResource(buildCondition(patient, CONDITION_IDENTIFIER_1, conditionList.get("HTN"), "ACTIVE",
            "Strong family history HTN.", createDateWithYearOffset(1)));
        createResource(buildCondition(patient, CONDITION_IDENTIFIER_2, conditionList.get("OSTEO"), "ACTIVE",
            "Patient played linebacker in NFL.", createDateWithYearOffset(3)));
        createResource(buildCondition(patient, CONDITION_IDENTIFIER_3, conditionList.get("CONCUSSION"), "ACTIVE",
            "Secondary to automobile accident.", createDateWithDayOffset((3))));
        return getResources(Condition.class);
    }
    
    /**
     * Deletes all conditions that share the given identifier.
     */
    public int deleteConditions() {
        return deleteResources(Condition.class);
    }
    
    /**
     * Build a condition.
     * 
     * @param identifier
     * @param conditionCode
     * @param status
     * @param notes
     * @param dateRecorded
     * @return
     */
    private Condition buildCondition(Patient patient, Identifier identifier, CodeableConcept conditionCode, String status,
                                     String notes, Date dateRecorded) {
        Condition condition = new Condition();
        condition.setPatient(new Reference(patient));
        condition.addIdentifier(identifier);
        condition.addIdentifier(DEMO_IDENTIFIER);
        condition.setDateRecorded(dateRecorded);
        condition.setCode(conditionCode);
        condition.setClinicalStatus(status);
        condition.setNotes(notes);
        return condition;
    }
    
    /**
     * Populates a condition index. TODO Build off a configuration file
     */
    private void populateConditionCodes() {
        conditionList.clear();
        conditionList.put("HTN", createCodeableConcept(SYS_SNOMED, "5962100", "Essential Hypertension"));
        conditionList.put("OSTEO", createCodeableConcept(SYS_SNOMED, "396275006", "Osteoarthritis"));
        conditionList.put("CONCUSSION", createCodeableConcept(SYS_SNOMED, "110030002", "Concussive Brain Injury"));
    }
    
    // ------------- Patient-related operations -------------
    
    /**
     * Adds a demo patient. TODO Read from configuration file.
     * 
     * @return
     */
    public List<Patient> addPatients() {
        Address address = new Address();
        address.addLine("123 Main Street");
        address.setCity("LA");
        address.setState("CA");
        address.setPostalCode("90049");
        createResource(
            buildPatient(PATIENT_IDENTIFIER_1, "Jacques", "LeMalade", 365 * 56, address, AdministrativeGender.MALE, "Mr."));
        createResource(buildPatient(PATIENT_IDENTIFIER_2, "Jane", "Intermountain", 365 * 26, address,
            AdministrativeGender.FEMALE, "Ms."));
        createResource(
            buildPatient(PATIENT_IDENTIFIER_3, "Jose", "Intermountain", 1, address, AdministrativeGender.MALE, ""));
        return getResources(Patient.class);
    }
    
    /**
     * Deletes all patient sharing the PATIENT_GROUP_IDENTIFIER
     */
    public int deletePatients() {
        return deleteResources(Patient.class);
    }
    
    /**
     * Builds a patient instance but does NOT persist it.
     * 
     * @param identifier
     * @param givenName
     * @param surname
     * @param birthdate
     * @param address
     * @param gender
     * @param prefix
     * @return
     */
    private Patient buildPatient(Identifier identifier, String givenName, String surname, int dobOffset, Address address,
                                 AdministrativeGender gender, String prefix) {
        Patient patient = new Patient();
        patient.addIdentifier(identifier);
        patient.addIdentifier(DEMO_IDENTIFIER);
        
        if (address != null) {
            patient.addAddress(address);//Fix
        }
        setPatientPhoto(patient);
        addPatientName(patient, givenName, surname, prefix);
        patient.setGender(gender);
        patient.setBirthDate(createDateWithDayOffset(dobOffset));
        return patient;
    }
    
    /**
     * Convenience method to add a simple name to a patient resource.
     * 
     * @param patient
     * @param givenName
     * @param familyName
     */
    private void addPatientName(Patient patient, String givenName, String familyName, String prefix) {
        patient.addName().addGiven(givenName).addFamily(familyName).addPrefix(prefix);
    }
    
    /**
     * Convenience method for creating and setting patient photo.
     * 
     * @param patient
     */
    private void setPatientPhoto(Patient patient) {
        try {
            String jpeg = "patient" + patient.getIdentifier().get(0).getValue() + ".jpeg";
            byte[] data = FhirUtil.getResourceAsByteArray(IMAGE_PATH + jpeg);
            Attachment photo = patient.addPhoto().setData(data);
            photo.setContentType("image/jpeg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // ------------- Document-related operations -------------
    
    public List<DocumentReference> addDocuments(Patient patient) {
        List<Practitioner> practitioners = addPractitioners();
        createResource(buildDocument(patient, practitioners.get(0), DOCUMENT_IDENTIFIER_1, 234, "Discharge Summary",
            "Discharge Summary", "note1.txt"));
        createResource(buildDocument(patient, practitioners.get(1), DOCUMENT_IDENTIFIER_2, 5, "Progress Report",
            "Progress Report", "note2.txt"));
        
        if (patient.getGender() == AdministrativeGender.FEMALE) {
            createResource(buildDocument(patient, practitioners.get(2), DOCUMENT_IDENTIFIER_3, 1, "Lactation Assessment",
                "Lactation Assessment", "note3.txt"));
        }
        return getResources(DocumentReference.class);
    }
    
    /**
     * Deletes all documents that share the given identifier.
     * 
     * @return
     */
    public int deleteDocuments() {
        return deleteResources(DocumentReference.class);
    }
    
    private DocumentReference buildDocument(Patient patient, Practitioner author, Identifier identifier, int createOffset,
                                            String type, String description, String body) {
        DocumentReference doc = new DocumentReference();
        doc.setType(createCodeableConcept(SYS_COGMED, type, description));
        doc.setSubject(new Reference(patient));
        doc.addIdentifier(identifier);
        doc.addIdentifier(DEMO_IDENTIFIER);
        doc.setCreated(createDateWithDayOffset(createOffset));
        doc.addAuthor(new Reference(author));
        DocumentReferenceContentComponent content = doc.addContent();
        Attachment attachment = new Attachment();
        attachment.setContentType("text/plain");
        attachment.setData(getNoteData(body));
        content.setAttachment(attachment);
        return doc;
    }
    
    private byte[] getNoteData(String note) {
        try {
            return FhirUtil.getResourceAsByteArray(NOTE_PATH + note);
        } catch (Exception e) {
            return null;
        }
    }
    
    // ------------- Practitioner-related operations -------------
    
    public List<Practitioner> addPractitioners() {
        createResource(buildPractitioner("Fry,Emory", PRACTITIONER_IDENTIFIER_1));
        createResource(buildPractitioner("Martin,Doug", PRACTITIONER_IDENTIFIER_2));
        createResource(buildPractitioner("Huff,Stan", PRACTITIONER_IDENTIFIER_3));
        return getResources(Practitioner.class);
    }
    
    /**
     * Deletes all practitioners that share the given identifier.
     */
    public int deletePractitioners() {
        return deleteResources(Practitioner.class);
    }
    
    private Practitioner buildPractitioner(String name, Identifier identifier) {
        Practitioner p = new Practitioner();
        p.addName(FhirUtil.parseName(name));
        p.addIdentifier(identifier);
        p.addIdentifier(DEMO_IDENTIFIER);
        return p;
    }
    
}
