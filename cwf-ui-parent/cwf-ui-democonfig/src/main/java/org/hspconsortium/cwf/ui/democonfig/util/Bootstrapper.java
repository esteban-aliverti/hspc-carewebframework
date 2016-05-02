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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
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
    
    private static final String[] STREETS = { "123 Yellowbrick Road", "325 Emory Lane", "1201 Regenstrief Blvd",
            "353 Intermountain Street" };
    
    private static final String[] CITIES = { "Los Angeles,CA,90001", "Indianapolis,IN,46202", "New York,NY,10010",
            "Sanibel,FL,33957" };
    
    private static final Class<?>[] DEMO_RESOURCE_TYPES = { Condition.class, MedicationOrder.class,
            MedicationAdministration.class, DocumentReference.class, Patient.class, Practitioner.class };
    
    private static final Log log = LogFactory.getLog(Bootstrapper.class);
    
    /**
     * Identifier used to locate demo resources for bulk deletes.
     */
    private final Identifier DEMO_IDENTIFIER = createIdentifier("demo", "gen");
    
    /**
     * FHIR service for managing resources.
     */
    BaseService fhirService;
    
    /**
     * Medication index
     */
    private final Map<String, CodeableConcept> medicationList = new HashMap<>();
    
    private final Map<String, CodeableConcept> conditionList = new HashMap<>();
    
    private final Map<Class<? extends DomainResource>, List<? extends DomainResource>> resourceCache = new HashMap<>();
    
    /**
     * Initialize with FHIR service and populate demo codes.
     * 
     * @param fhirService The FHIR service.
     */
    public Bootstrapper(BaseService fhirService) {
        this.fhirService = fhirService;
        populateMedicationCodes();
        populateConditionCodes();
        fetchAll();
    }
    
    // ------------- Internal operations -------------
    
    /**
     * Convenience method to create a codeable concept with a single coding. TODO May need to move
     * to some util class or check to see if not already present in HAPI FHIR.
     * 
     * @param system The code system.
     * @param code The code value.
     * @param displayName The display name.
     * @return The newly created codeable concept.
     */
    private CodeableConcept createCodeableConcept(String system, String code, String displayName) {
        return new CodeableConcept().addCoding(new Coding(system, code, displayName));
    }
    
    /**
     * Convenience method for creating identifiers in local system.
     * 
     * @param system The identifier system.
     * @param value The identifier value.
     * @return The newly created identifier.
     */
    private Identifier createIdentifier(String system, Object value) {
        Identifier identifier = new Identifier();
        identifier.setSystem("urn:cogmedsys:hsp:model:" + system);
        identifier.setValue(value.toString());
        return identifier;
    }
    
    /**
     * Convenience method for creating identifiers for resources belonging to a patient. The
     * identifier generated will be unique across all resources.
     * 
     * @param system The identifier system.
     * @param idnum The identifier value.
     * @param patient Owner of the resource to receive the identifier.
     * @return The newly created identifier.
     */
    private Identifier createIdentifier(String system, int idnum, Patient patient) {
        String value = getMainIdentifier(patient).getValue() + "_" + idnum;
        return createIdentifier(system, value);
    }
    
    /**
     * Convenience method to create a time offset.
     * 
     * @param minuteOffset Offset in minutes.
     * @return A date minus the offset.
     */
    private Date createDateWithMinuteOffset(long minuteOffset) {
        return new Date(System.currentTimeMillis() - minuteOffset * 60 * 1000);
    }
    
    /**
     * Convenience method to create a time offset.
     * 
     * @param dayOffset Offset in days.
     * @return A date minus the offset.
     */
    private Date createDateWithDayOffset(long dayOffset) {
        return createDateWithMinuteOffset(dayOffset * 24 * 60);
    }
    
    /**
     * Convenience method to create a time offset.
     * 
     * @param yearOffset Offset in years.
     * @return A date minus the offset.
     */
    private Date createDateWithYearOffset(long yearOffset) {
        return createDateWithDayOffset(yearOffset * 365);
    }
    
    /**
     * Returns a random element from a string array.
     * 
     * @param choices The array of possible choices.
     * @return A random element.
     */
    private String getRandom(String[] choices) {
        int index = (int) (Math.random() * choices.length);
        return choices[index];
    }
    
    /**
     * Fetches all demo data from FHIR server.
     * 
     * @return Number of resources fetched.
     */
    @SuppressWarnings("unchecked")
    public int fetchAll() {
        int count = 0;
        
        for (Class<?> clazz : DEMO_RESOURCE_TYPES) {
            count += fetchByType((Class<? extends DomainResource>) clazz).size();
        }
        
        return count;
    }
    
    /**
     * Fetches demo data of specified type into cache.
     * 
     * @param clazz Type of resource.
     * @return The list of fetched resources.
     */
    private <D extends DomainResource> List<D> fetchByType(Class<D> clazz) {
        List<D> list = fhirService.searchResourcesByIdentifier(DEMO_IDENTIFIER, clazz);
        resourceCache.put(clazz, list);
        return list;
    }
    
    @SuppressWarnings("unchecked")
    private <D extends DomainResource> List<D> getCachedResources(Class<D> clazz) {
        List<D> list = (List<D>) resourceCache.get(clazz);
        
        if (list == null) {
            resourceCache.put(clazz, list = new ArrayList<>());
        }
        
        return list;
    }
    
    /**
     * Returns the principal identifier for the given resource.
     * 
     * @param resource
     * @return
     */
    private Identifier getMainIdentifier(DomainResource resource) {
        List<Identifier> identifiers = FhirUtil.getIdentifiers(resource);
        
        for (Identifier identifier : identifiers) {
            if (!identifier.equalsShallow(DEMO_IDENTIFIER)) {
                return identifier;
            }
        }
        
        return null;
    }
    
    @SuppressWarnings("unchecked")
    private <D extends DomainResource> D getOrCreate(D resource) {
        Identifier identifier = getMainIdentifier(resource);
        List<D> list = (List<D>) getCachedResources(resource.getClass());
        
        for (D cachedResource : list) {
            Identifier identifier2 = getMainIdentifier(cachedResource);
            
            if (identifier.equalsShallow(identifier2)) {
                return cachedResource;
            }
        }
        
        return createResource(resource);
    }
    
    /**
     * Creates a resource on the server.
     * 
     * @param resource Resource to create.
     * @return True if created.
     */
    @SuppressWarnings("unchecked")
    private <D extends DomainResource> D createResource(D resource) {
        MethodOutcome outcome = fhirService.createResource(resource);
        D newResource = null;
        
        if (outcome.getResource().getClass() == resource.getClass()) {
            newResource = (D) outcome.getResource();
        } else {
            List<? extends DomainResource> results = fhirService.searchResourcesByIdentifier(getMainIdentifier(resource),
                (Class<? extends DomainResource>) resource.getClass());
            
            if (!results.isEmpty()) {
                newResource = (D) results.get(0);
                getCachedResources((Class<D>) resource.getClass()).add(newResource);
            }
        }
        
        return newResource;
    }
    
    /**
     * Deletes demo data of specified type from server and clears the cache.
     * 
     * @param clazz Type of resource
     * @return Count of resources deleted.
     */
    private <D extends DomainResource> int deleteByType(Class<D> clazz) {
        getCachedResources(clazz).clear();
        return fhirService.deleteResourcesByIdentifier(DEMO_IDENTIFIER, clazz);
    }
    
    // ------------- General operations -------------
    
    public int addAll(Patient patient) {
        // @formatter:off
        return addConditions(patient).size()
                + addDocuments(patient).size()
                + addMedicationOrders(patient).size()
                + addMedicationAdministrations(patient).size();
        // @formatter:on
    }
    
    public int deleteAll(Patient patient) {
        List<DomainResource> list = new ArrayList<>();
        
        list.addAll(fhirService.searchResourcesForPatient(patient, Condition.class));
        list.addAll(fhirService.searchResourcesForPatient(patient, MedicationAdministration.class));
        list.addAll(fhirService.searchResourcesForPatient(patient, MedicationOrder.class));
        list.addAll(fhirService.searchResourcesForPatient(patient, DocumentReference.class));
        fhirService.deleteResources(list);
        return list.size();
    }
    
    /**
     * Deletes all demo data.
     * 
     * @return Number of resources deleted.
     */
    @SuppressWarnings("unchecked")
    public int deleteAll() {
        int count = 0;
        
        for (Class<?> clazz : DEMO_RESOURCE_TYPES) {
            count += deleteByType((Class<? extends DomainResource>) clazz);
        }
        
        return count;
    }
    
    // ------------- Patient-related operations -------------
    
    /**
     * Adds a demo patient. TODO Read from configuration file.
     * 
     * @return
     */
    public List<Patient> addPatients() {
        int idnum = 0;
        getOrCreate(buildPatient(++idnum, "LeMalade,Jacques", 365 * 56, AdministrativeGender.MALE, "male_adult.jpeg"));
        getOrCreate(buildPatient(++idnum, "Intermountain,Jane", 365 * 26, AdministrativeGender.FEMALE, "female_adult.jpeg"));
        getOrCreate(buildPatient(++idnum, "Intermountain,Jose", 1, AdministrativeGender.MALE, "male_newborn.jpeg"));
        return getCachedResources(Patient.class);
    }
    
    /**
     * Deletes all patient sharing the PATIENT_GROUP_IDENTIFIER
     * 
     * @return Count of deleted resources.
     */
    public int deletePatients() {
        return deleteByType(Patient.class);
    }
    
    /**
     * Builds a patient instance but does NOT persist it.
     * 
     * @param idnum
     * @param name
     * @param dobOffset
     * @param gender
     * @param photo
     * @return
     */
    private Patient buildPatient(int idnum, String name, int dobOffset, AdministrativeGender gender, String photo) {
        Patient patient = new Patient();
        patient.addIdentifier(createIdentifier("patient", idnum).setType(IDENT_MRN));
        patient.addIdentifier(DEMO_IDENTIFIER);
        patient.addName(FhirUtil.parseName(name));
        patient.setGender(gender);
        patient.setBirthDate(createDateWithDayOffset(dobOffset));
        setPatientPhoto(patient, photo);
        setPatientAddress(patient);
        return patient;
    }
    
    /**
     * Convenience method to add a random address to a patient resource
     * 
     * @param patient
     */
    private void setPatientAddress(Patient patient) {
        Address address = new Address();
        address.addLine(getRandom(STREETS));
        String[] csz = getRandom(CITIES).split("\\,");
        address.setCity(csz[0]);
        address.setState(csz[1]);
        address.setPostalCode(csz[2]);
        patient.addAddress(address);
    }
    
    /**
     * Convenience method for creating and setting patient photo.
     * 
     * @param patient
     */
    private void setPatientPhoto(Patient patient, String file) {
        try {
            byte[] data = FhirUtil.getResourceAsByteArray(IMAGE_PATH + file);
            Attachment photo = patient.addPhoto().setData(data);
            photo.setContentType("image/jpeg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // ------------- Medication Administration operations -------------
    
    /**
     * Method populates the patient record with some sample medication administrations.
     * 
     * @param patient
     * @return
     */
    public List<MedicationAdministration> addMedicationAdministrations(Patient patient) {
        addMedicationOrders(patient);
        int idnum = 0;
        List<MedicationAdministration> list = new ArrayList<>();
        
        list.add(
            getOrCreate(buildMedicationAdministration(++idnum, patient, "metoprolol", 1, createDateWithMinuteOffset(45))));
        list.add(
            getOrCreate(buildMedicationAdministration(++idnum, patient, "clopidogrel", 1, createDateWithMinuteOffset(35))));
        return list;
    }
    
    /**
     * Clears all demo medication administrations.
     * 
     * @return Count of deleted resources.
     */
    public int deleteMedicationAdministrations() {
        return deleteByType(MedicationAdministration.class);
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
    private MedicationAdministration buildMedicationAdministration(int idnum, Patient patient, String medCode, int tabCount,
                                                                   Date effectiveDate) {
        MedicationAdministration medAdmin = new MedicationAdministration();
        medAdmin.addIdentifier(createIdentifier("medadmin", idnum, patient));
        medAdmin.addIdentifier(DEMO_IDENTIFIER);
        medAdmin.setPatient(new Reference(patient));
        medAdmin.setMedication(medicationList.get(medCode));
        medAdmin.setEffectiveTime(new DateTimeType(effectiveDate));
        medAdmin.setDosage(createDosage(tabCount));
        medAdmin.setPrescription(new Reference(findMedicationOrder(patient, medCode)));
        return medAdmin;
    }
    
    /**
     * Convenience method for representing n tablets of medication X
     * 
     * @param numberOfTablets
     * @return
     */
    private MedicationAdministrationDosageComponent createDosage(int numberOfTablets) {
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
     * Method populates the patient record with some sample medication orders.
     * 
     * @param medOrderSetIdentifier
     * @param patient
     * @return
     */
    public List<MedicationOrder> addMedicationOrders(Patient patient) {
        int idnum = 0;
        List<MedicationOrder> list = new ArrayList<>();
        
        list.add(getOrCreate(buildMedicationOrder(++idnum, patient, "metoprolol",
            createDosageInstructions(1, "PO", "QD", null), createDateWithMinuteOffset(45))));
        list.add(getOrCreate(buildMedicationOrder(++idnum, patient, "clopidogrel",
            createDosageInstructions(1, "PO", "QD", null), createDateWithMinuteOffset(35))));
        list.add(getOrCreate(buildMedicationOrder(++idnum, patient, "atorvastatin",
            createDosageInstructions(1, "PO", "QD", null), createDateWithMinuteOffset(25))));
        list.add(getOrCreate(buildMedicationOrder(++idnum, patient, "acetaminophen",
            createDosageInstructions(1, "PO", "Q8H", "1"), createDateWithMinuteOffset(15))));
        return list;
    }
    
    /**
     * Clears all medication orders that share the given identifier.
     * 
     * @return
     */
    public int deleteMedicationOrders() {
        return deleteByType(MedicationOrder.class);
    }
    
    private MedicationOrder findMedicationOrder(Patient patient, String med) {
        CodeableConcept medCode = medicationList.get(med);
        Reference ref = new Reference(patient);
        
        for (MedicationOrder order : getCachedResources(MedicationOrder.class)) {
            if (order.getPatient().equalsShallow(ref) && medCode.equalsShallow(order.getMedication())) {
                return order;
            }
        }
        
        return null;
    }
    
    /**
     * Method builds a medication order instance.
     * 
     * @param medCode The medication code.
     * @param dose The dosage instruction.
     * @param dateWritten When written.
     * @return The new medication order.
     */
    private MedicationOrder buildMedicationOrder(int idnum, Patient patient, String medCode,
                                                 MedicationOrderDosageInstructionComponent dose, Date dateWritten) {
        MedicationOrder medOrder = new MedicationOrder();
        medOrder.setPatient(new Reference(patient));
        medOrder.addIdentifier(createIdentifier("medorder", idnum, patient));
        medOrder.addIdentifier(DEMO_IDENTIFIER);
        medOrder.setMedication(medicationList.get(medCode));
        medOrder.setDateWritten(dateWritten);
        medOrder.addDosageInstruction(dose);
        return medOrder;
    }
    
    /**
     * Convenience method for representing n tablets of medication X
     * 
     * @param numberOfTablets
     * @param routeCode
     * @param freqCode
     * @param prnCode
     * @return
     */
    private MedicationOrderDosageInstructionComponent createDosageInstructions(int numberOfTablets, String routeCode,
                                                                               String freqCode, String prnCode) {
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
        int idnum = 0;
        List<Condition> list = new ArrayList<>();
        
        list.add(getOrCreate(
            buildCondition(++idnum, patient, "HTN", "ACTIVE", "Strong family history HTN.", createDateWithYearOffset(1))));
        list.add(getOrCreate(buildCondition(++idnum, patient, "OSTEO", "ACTIVE", "Patient played linebacker in NFL.",
            createDateWithYearOffset(3))));
        list.add(getOrCreate(buildCondition(++idnum, patient, "CONCUSSION", "ACTIVE", "Secondary to automobile accident.",
            createDateWithDayOffset((3)))));
        return list;
    }
    
    /**
     * Deletes all conditions that share the given identifier.
     * 
     * @return
     */
    public int deleteConditions() {
        return deleteByType(Condition.class);
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
    private Condition buildCondition(int idnum, Patient patient, String conditionCode, String status, String notes,
                                     Date dateRecorded) {
        Condition condition = new Condition();
        condition.setPatient(new Reference(patient));
        condition.addIdentifier(createIdentifier("condition", idnum, patient));
        condition.addIdentifier(DEMO_IDENTIFIER);
        condition.setDateRecorded(dateRecorded);
        condition.setCode(conditionList.get(conditionCode));
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
    
    // ------------- Document-related operations -------------
    
    public List<DocumentReference> addDocuments(Patient patient) {
        int idnum = 0;
        List<Practitioner> practitioners = addPractitioners();
        List<DocumentReference> list = new ArrayList<>();
        
        list.add(getOrCreate(buildDocument(++idnum, patient, practitioners.get(0), 234, "Discharge Summary",
            "Discharge Summary", "discharge_summary.txt")));
        list.add(getOrCreate(buildDocument(++idnum, patient, practitioners.get(1), 5, "Progress Report", "Progress Report",
            "progress_report.txt")));
        
        if (patient.getGender() == AdministrativeGender.FEMALE) {
            list.add(getOrCreate(buildDocument(++idnum, patient, practitioners.get(2), 1, "Lactation Assessment",
                "Lactation Assessment", "lactation_assessment.txt")));
        }
        return list;
    }
    
    /**
     * Deletes all demo documents.
     * 
     * @return Count of deleted resources.
     */
    public int deleteDocuments() {
        return deleteByType(DocumentReference.class);
    }
    
    private DocumentReference buildDocument(int idnum, Patient patient, Practitioner author, int createOffset, String type,
                                            String description, String body) {
        DocumentReference doc = new DocumentReference();
        doc.setType(createCodeableConcept(SYS_COGMED, type, description));
        doc.setSubject(new Reference(patient));
        doc.addIdentifier(createIdentifier("document", idnum, patient));
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
        int idnum = 0;
        List<Practitioner> list = new ArrayList<>();
        
        list.add(getOrCreate(buildPractitioner("Fry,Emory", ++idnum)));
        list.add(getOrCreate(buildPractitioner("Martin,Doug", ++idnum)));
        list.add(getOrCreate(buildPractitioner("Huff,Stan", ++idnum)));
        return list;
    }
    
    /**
     * Deletes all practitioners that share the given identifier.
     * 
     * @return
     */
    public int deletePractitioners() {
        return deleteByType(Practitioner.class);
    }
    
    private Practitioner buildPractitioner(String name, int idnum) {
        Practitioner p = new Practitioner();
        p.addName(FhirUtil.parseName(name));
        p.addIdentifier(createIdentifier("practitioner", idnum));
        p.addIdentifier(DEMO_IDENTIFIER);
        return p;
    }
    
}
