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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Condition.ConditionVerificationStatus;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.DateType;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.MedicationAdministration.MedicationAdministrationDosageComponent;
import org.hl7.fhir.dstu3.model.MedicationOrder;
import org.hl7.fhir.dstu3.model.MedicationOrder.MedicationOrderDosageInstructionComponent;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Procedure;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.SimpleQuantity;
import org.hspconsortium.cwf.fhir.common.FhirTerminology;
import org.hspconsortium.cwf.fhir.common.FhirUtil;
import org.hspconsortium.cwf.fhir.condition.ConditionService;
import org.hspconsortium.cwf.fhir.medication.MedicationService;
import org.hspconsortium.cwf.fhir.patient.PatientService;

import ca.uhn.fhir.rest.api.MethodOutcome;

/**
 * Currently hard coded but in later iterations, bootstrapper should be configured based on a
 * configuration file to support greater flexibility during demos or connectathons.
 */
public class Bootstrapper {
    
    
    private static final String IMAGE_PATH = "web/org/hspconsortium/cwf/ui/democonfig/images/";
    
    /**
     * Identifier used to define 'set of medication administration resources' to support bulk
     * deletes.
     */
    public static final Identifier MED_ADMIN_GROUP_IDENTIFIER = new Identifier()
            .setSystem("urn:cogmedsys:hsp:model:medicationadministration").setValue("gen");
    
    /**
     * Identifier used to define 'set of medication order resources' to support bulk deletes.
     */
    public static final Identifier MED_ORDER_GROUP_IDENTIFIER = new Identifier()
            .setSystem("urn:cogmedsys:hsp:model:medicationorder").setValue("gen");
    
    /**
     * Identifier used to define 'set of condition resources' to support bulk deletes.
     */
    public static final Identifier CONDITION_GROUP_IDENTIFIER = new Identifier()
            .setSystem("urn:cogmedsys:hsp:model:condition").setValue("gen");
    
    public static final Identifier CONDITION_IDENTIFIER_1 = new Identifier().setSystem("urn:cogmedsys:hsp:model:condition")
            .setValue("1");
    
    public static final Identifier CONDITION_IDENTIFIER_2 = new Identifier().setSystem("urn:cogmedsys:hsp:model:condition")
            .setValue("2");
    
    public static final Identifier CONDITION_IDENTIFIER_3 = new Identifier().setSystem("urn:cogmedsys:hsp:model:condition")
            .setValue("3");
    
    /**
     * Identifier for clinical subject of demonstration.
     */
    public static final Identifier PATIENT_GROUP_IDENTIFIER = new Identifier().setSystem("urn:cogmedsys:hsp:model:patient")
            .setValue("gen");
    
    public static final Identifier PATIENT_IDENTIFIER_1 = new Identifier().setSystem("urn:cogmedsys:hsp:model:patient")
            .setValue("1");
    
    public static final Identifier PATIENT_IDENTIFIER_2 = new Identifier().setSystem("urn:cogmedsys:hsp:model:patient")
            .setValue("2");
    
    public static final Identifier PATIENT_IDENTIFIER_3 = new Identifier().setSystem("urn:cogmedsys:hsp:model:patient")
            .setValue("3");
    
    private static final Log log = LogFactory.getLog(Bootstrapper.class);
    
    /**
     * FHIR service for medication administration CRUD
     */
    @Autowired
    MedicationService medicationService;
    
    /**
     * FHIR service for patient administration CRUD TODO May need to remove in favor of Doug
     * Martin's code.
     */
    @Autowired
    PatientService patientService;
    
    @Autowired
    ConditionService conditionService;
    
    /**
     * Formatter for effective date
     */
    private final DateFormat formatter = new SimpleDateFormat("MM/dd/yy");
    
    /**
     * Medication index
     */
    private final Map<String, CodeableConcept> medicationList = new HashMap<>();
    
    private final Map<String, CodeableConcept> conditionList = new HashMap<>();
    
    /**
     * No-arg constructor which initializes medication index
     */
    public Bootstrapper() {
        populateMedicationCodes();
        populateConditionCodes();
    }
    
    /**
     * Populates a medication index. TODO Build off a configuration file
     */
    protected void populateMedicationCodes() {
        medicationList.clear();
        medicationList.put("metoprolol", buildCodeableConcept("http://www.nlm.nih.gov/research/umls/rxnorm", "372891",
            "Metoprolol Tartrate 25 MG Oral tablet"));
        medicationList.put("atenolol",
            buildCodeableConcept("http://www.nlm.nih.gov/research/umls/rxnorm", "197379", "Atenolol 100 MG Oral Tablet"));
        medicationList.put("bisoprolol", buildCodeableConcept("http://www.nlm.nih.gov/research/umls/rxnorm", "854901",
            "Bisoprolol Fumarate 10 MG Oral Tablet"));
        medicationList.put("clopidogrel",
            buildCodeableConcept("http://www.nlm.nih.gov/research/umls/rxnorm", "309362", "Clopidogrel 75 MG Oral Tablet"));
        medicationList.put("atorvastatin", buildCodeableConcept("http://www.nlm.nih.gov/research/umls/rxnorm", "597967",
            "Amlodipine 10 MG / Atorvastatin 20 MG Oral Tablet"));
        medicationList.put("acetaminophen", buildCodeableConcept("http://www.nlm.nih.gov/research/umls/rxnorm", "665056",
            "Acetaminophen 500 MG Chewable Tablet"));
        medicationList.put("aspirin",
            buildCodeableConcept("http://www.nlm.nih.gov/research/umls/rxnorm", "198466", "Aspirin 325 MG Oral Capsule"));
        medicationList.put("hydrochlorothiazide", buildCodeableConcept("http://www.nlm.nih.gov/research/umls/rxnorm",
            "310798", "Hydrochlorothiazide 25 MG Oral Tablet"));
        medicationList.put("bisacodyl",
            buildCodeableConcept("http://www.nlm.nih.gov/research/umls/rxnorm", "1550933", "Bisacodyl 5 MG Oral Tablet"));
        medicationList.put("acetazolamide", buildCodeableConcept("http://www.nlm.nih.gov/research/umls/rxnorm", "197304",
            "Acetazolamide 250 MG Oral Tablet"));
    }
    
    /**
     * Populates a medication index. TODO Build off a configuration file
     */
    protected void populateConditionCodes() {
        conditionList.clear();
        conditionList.put("HTN", buildCodeableConcept(FhirTerminology.SYS_SNOMED, "5962100", "Essential Hypertension"));
        conditionList.put("OSTEO", buildCodeableConcept(FhirTerminology.SYS_SNOMED, "396275006", "Osteoarthritis"));
        conditionList.put("CONCUSSION",
            buildCodeableConcept(FhirTerminology.SYS_SNOMED, "110030002", "Concussive Brain Injury"));
    }
    
    /**
     * Convenience method for representing n tablets of medication X
     * 
     * @param numberOfTablets
     * @return
     */
    protected MedicationAdministrationDosageComponent createDosageOneTablet(int numberOfTablets) {
        SimpleQuantity simpleQuantity = new SimpleQuantity();
        simpleQuantity.setValue(numberOfTablets);
        simpleQuantity.setUnit("{tbl}");
        MedicationAdministrationDosageComponent dose = new MedicationAdministrationDosageComponent()
                .setQuantity(simpleQuantity);
        return dose;
    }
    
    /**
     * Convenience method for representing n tablets of medication X
     * 
     * @param numberOfTablets
     * @return
     */
    protected MedicationOrderDosageInstructionComponent createDosageInstructionsOneTablet(int numberOfTablets,
                                                                                          String routeCode, String freqCode,
                                                                                          String prnCode) {
        SimpleQuantity simpleQuantity = new SimpleQuantity();
        simpleQuantity.setValue(numberOfTablets);
        simpleQuantity.setUnit("{tbl}");
        MedicationOrderDosageInstructionComponent dose = new MedicationOrderDosageInstructionComponent()
                .setDose(simpleQuantity);
        if (routeCode != null && routeCode.equalsIgnoreCase("PO")) {
            CodeableConcept route = new CodeableConcept();
            route.addCoding().setSystem(FhirTerminology.SYS_SNOMED).setCode("26643006").setDisplay("Oral route");//TODO Move to mock terminology server when created
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
            CodeableConcept prnCodeableConcept = buildCodeableConcept(FhirTerminology.SYS_COGMED, "1", "As needed for pain");
            dose.setAsNeeded(prnCodeableConcept);
        } else if (prnCode != null && prnCode.equalsIgnoreCase("2")) {
            CodeableConcept prnCodeableConcept = buildCodeableConcept(FhirTerminology.SYS_COGMED, "1",
                "As needed to control hypertension");
            dose.setAsNeeded(prnCodeableConcept);
        }
        return dose;
    }
    
    /**
     * Convenience method to create a time offset for MAR display
     * 
     * @param minuteOffset
     * @return
     */
    protected Date createDateWithMinuteOffset(int minuteOffset) {
        return new Date(System.currentTimeMillis() - minuteOffset * 60 * 1000);
    }
    
    /**
     * Convenience method to create a time offset for MAR display
     * 
     * @param minuteOffset
     * @return
     */
    protected Date createDateWithDayOffset(int dayOffset) {
        return createDateWithMinuteOffset(dayOffset * 24 * 60 * 1000);
    }
    
    /**
     * Convenience method to create a time offset for MAR display
     * 
     * @param minuteOffset
     * @return
     */
    protected Date createDateWithYearOffset(int yearOffset) {
        return createDateWithDayOffset(yearOffset * 365);
    }
    
    /**
     * Convenience method to create a codeable concept with a single coding. TODO May need to move
     * to some util class or check to see if not already present in HAPI FHIR.
     * 
     * @param system
     * @param code
     * @param displayName
     * @return
     */
    protected CodeableConcept buildCodeableConcept(String system, String code, String displayName) {
        CodeableConcept codeableConcept = new CodeableConcept();
        codeableConcept.addCoding().setSystem(system).setCode(code).setDisplay(displayName);
        return codeableConcept;
    }
    
    /**
     * Method populates the patient record with some sample medication administrations.
     * 
     * @param patient
     */
    public List<MedicationAdministration> addSampleMedicationAdmins(Identifier medAdminSetIdentifier,
                                                                    Identifier medOrdersSetIdentifier, Patient patient) {
        List<MedicationOrder> medOrders = addSampleMedicationOrders(medOrdersSetIdentifier, patient);
        List<MedicationAdministration> medAdmins = new ArrayList<>();
        medAdmins.add(createMedicationAdministration(patient, medAdminSetIdentifier, medicationList.get("metoprolol"),
            createDosageOneTablet(1), createDateWithMinuteOffset(45), medOrders.get(0)));
        medAdmins.add(createMedicationAdministration(patient, medAdminSetIdentifier, medicationList.get("clopidogrel"),
            createDosageOneTablet(1), createDateWithMinuteOffset(35), medOrders.get(1)));
        return medAdmins;
    }
    
    /**
     * Method populates the patient record with some sample medication orders.
     * 
     * @param patient
     */
    public List<MedicationOrder> addSampleMedicationOrders(Identifier medOrderSetIdentifier, Patient patient) {
        List<MedicationOrder> medOrders = new ArrayList<>();
        medOrders.add(createMedicationOrder(patient, medOrderSetIdentifier, medicationList.get("metoprolol"),
            createDosageInstructionsOneTablet(1, "PO", "QD", null), createDateWithMinuteOffset(45)));
        medOrders.add(createMedicationOrder(patient, medOrderSetIdentifier, medicationList.get("clopidogrel"),
            createDosageInstructionsOneTablet(1, "PO", "QD", null), createDateWithMinuteOffset(35)));
        medOrders.add(createMedicationOrder(patient, medOrderSetIdentifier, medicationList.get("atorvastatin"),
            createDosageInstructionsOneTablet(1, "PO", "QD", null), createDateWithMinuteOffset(25)));
        medOrders.add(createMedicationOrder(patient, medOrderSetIdentifier, medicationList.get("acetaminophen"),
            createDosageInstructionsOneTablet(1, "PO", "Q8H", "1"), createDateWithMinuteOffset(15)));
        return medOrders;
    }
    
    /**
     * Method populates the patient record with some sample medication orders.
     * 
     * @param patient
     */
    public List<Condition> addSampleConditions(Identifier conditionGroupIdentifier, Patient patient) {
        List<Condition> conditions = new ArrayList<>();
        conditions.add(createCondition(patient, conditionGroupIdentifier, CONDITION_IDENTIFIER_1, conditionList.get("HTN"),
            "ACTIVE", "Strong family history HTN.", createDateWithYearOffset(1)));
        conditions.add(createCondition(patient, conditionGroupIdentifier, CONDITION_IDENTIFIER_2, conditionList.get("OSTEO"),
            "ACTIVE", "Patient played linebacker in NFL.", createDateWithYearOffset(3)));
        conditions.add(createCondition(patient, conditionGroupIdentifier, CONDITION_IDENTIFIER_3,
            conditionList.get("CONCUSSION"), "ACTIVE", "Secondary to automobile accident.", createDateWithDayOffset((3))));
        return conditions;
    }
    
    /**
     * Method populates the patient record with some sample medication administrations.
     * 
     * @param patient
     */
    public List<MedicationAdministration> addSampleMedicationAdmins(Patient patient) {
        return addSampleMedicationAdmins(MED_ADMIN_GROUP_IDENTIFIER, MED_ORDER_GROUP_IDENTIFIER, patient);
    }
    
    /**
     * Method populates the patient record with some sample medication orders.
     * 
     * @param patient
     */
    public List<MedicationOrder> addSampleMedicationOrders(Patient patient) {
        return addSampleMedicationOrders(MED_ORDER_GROUP_IDENTIFIER, patient);
    }
    
    /**
     * Method populates the patient record with some sample conditions.
     * 
     * @param patient
     */
    public List<Condition> addSampleConditions(Patient patient) {
        return addSampleConditions(CONDITION_GROUP_IDENTIFIER, patient);
    }
    
    /**
     * Method adds a medication administration record for a given patient.
     * 
     * @param patient
     * @param identifier
     * @param medCode
     * @param dosage
     * @param effectiveDate
     * @return
     */
    public MedicationAdministration createMedicationAdministration(Patient patient, Identifier identifier,
                                                                   CodeableConcept medCode,
                                                                   MedicationAdministrationDosageComponent dosage,
                                                                   Date effectiveDate, MedicationOrder prescription) {
        MedicationAdministration medAdmin = buildMedicationAdministration(identifier, medCode, dosage, effectiveDate,
            prescription);
        medAdmin.setPatientTarget(patient);
        MethodOutcome methodOutcome = medicationService.createMedicationAdministration(medAdmin);
        return medAdmin;
    }
    
    /**
     * Method adds a medication order record for a given patient.
     * 
     * @param patient
     * @param identifier
     * @param medCode
     * @param dosage
     * @param effectiveDate
     * @return
     */
    public MedicationOrder createMedicationOrder(Patient patient, Identifier identifier, CodeableConcept medCode,
                                                 MedicationOrderDosageInstructionComponent dosage, Date effectiveDate) {
        MedicationOrder medOrder = buildMedicationOrder(identifier, medCode, dosage, effectiveDate);
        medOrder.setPatientTarget(patient);
        MethodOutcome methodOutcome = medicationService.createMedicationOrder(medOrder);
        return medOrder;
    }
    
    public Condition createCondition(Patient patient, Identifier groupIdentifier, Identifier individualIdentifier,
                                     CodeableConcept conditionCode, String status, String notes, Date recordedDate) {
        Condition condition = buildCondition(individualIdentifier, conditionCode, status, notes, recordedDate);
        condition.addIdentifier(groupIdentifier);
        condition.setPatientTarget(patient);
        MethodOutcome methodOutcome = conditionService.addConditionIfNotExist(condition, individualIdentifier);
        return condition;
    }
    
    /**
     * Method builds a medication administration instance.
     * 
     * @param identifier
     * @param medCode
     * @param dose
     * @param effectiveDate
     * @return
     */
    public MedicationAdministration buildMedicationAdministration(Identifier identifier, CodeableConcept medCode,
                                                                  MedicationAdministrationDosageComponent dose,
                                                                  Date effectiveDate, MedicationOrder prescription) {
        MedicationAdministration medAdmin = new MedicationAdministration();
        medAdmin.addIdentifier(identifier);
        medAdmin.setMedication(medCode);
        medAdmin.setEffectiveTime(new DateTimeType(effectiveDate));
        medAdmin.setDosage(dose);
        if (prescription != null) {
            medAdmin.setPrescriptionTarget(prescription);
        } else {
            throw new RuntimeException("An order must be associated with this medicationa administration");
        }
        return medAdmin;
    }
    
    /**
     * Method builds a medication order instance.
     * 
     * @param identifier
     * @param medCode
     * @param dose
     * @param effectiveDate
     * @return
     */
    public MedicationOrder buildMedicationOrder(Identifier identifier, CodeableConcept medCode,
                                                MedicationOrderDosageInstructionComponent dose, Date dateWritten) {
        MedicationOrder medOrder = new MedicationOrder();
        medOrder.addIdentifier(identifier);
        medOrder.setMedication(medCode);
        medOrder.setDateWritten(dateWritten);
        medOrder.addDosageInstruction(dose);
        return medOrder;
    }
    
    public Condition buildCondition(Identifier identifier, CodeableConcept conditionCode, String status, String notes,
                                    Date dateRecorded) {
        Condition condition = new Condition();
        condition.addIdentifier(identifier);
        condition.setDateRecorded(dateRecorded);
        condition.setCode(conditionCode);
        condition.setClinicalStatus(status);
        condition.setNotes(notes);
        return condition;
    }
    
    /**
     * Clears all medication administrations that share the given identifier.
     */
    public void clearMedicationAdministrations(Identifier identifier) {
        medicationService.deleteMedicationAdministrationsByIdentifier(identifier);
    }
    
    /**
     * Clears all medication orders that share the given identifier.
     */
    public void clearMedicationOrders(Identifier identifier) {
        medicationService.deleteMedicationOrdersByIdentifier(identifier);
    }
    
    /**
     * Clears all conditions that share the given identifier.
     */
    public void clearConditions(Identifier identifier) {
        medicationService.deleteConditionsByIdentifier(identifier);
    }
    
    /**
     * Clears all medication administrations that share the given identifier.
     */
    public void clearMedicationAdministrations() {
        medicationService.deleteMedicationAdministrationsByIdentifier(MED_ADMIN_GROUP_IDENTIFIER);
    }
    
    /**
     * Clears all medication orders that share the given identifier.
     */
    public void clearMedicationOrders() {
        medicationService.deleteMedicationOrdersByIdentifier(MED_ORDER_GROUP_IDENTIFIER);
    }
    
    /**
     * Clears all conditions that share the given identifier.
     */
    public void clearConditions() {
        medicationService.deleteConditionsByIdentifier(CONDITION_GROUP_IDENTIFIER);
    }
    
    /**
     * Adds a demo patient.
     * 
     * @param patientSetIdentifier
     * @param givenName
     * @param surname
     * @param date
     * @param gender
     * @param title
     * @return
     */
    public Patient addDemoPatient(Identifier patientIdentifier, String givenName, String surname, String date,
                                  Address address, AdministrativeGender gender, String prefix) {
        Patient patient = buildPatient(patientIdentifier, givenName, surname, date, address, gender, prefix);
        patient.addIdentifier(PATIENT_GROUP_IDENTIFIER);
        MethodOutcome methodOutcome = patientService.addPatientIfNotExist(patient, patientIdentifier);
        if (methodOutcome.getCreated()) {
            return patient;
        } else {
            return null;
        }
    }
    
    /**
     * Adds a demo patient. TODO Read from configuration file.
     * 
     * @return
     */
    public List<Patient> addDemoPatients() {
        List<Patient> patients = new ArrayList<>();
        Address address = new Address();
        address.addLine("123 Main Street");
        address.setCity("LA");
        address.setState("CA");
        address.setPostalCode("90049");
        patients.add(addDemoPatient(PATIENT_IDENTIFIER_1, "Jacques", "LeMalade", "06/30/1956", address,
            AdministrativeGender.MALE, "Mr."));
        patients.add(addDemoPatient(PATIENT_IDENTIFIER_2, "Juan", "Interop", "06/30/1956", address,
            AdministrativeGender.MALE, "Mr."));
        patients.add(addDemoPatient(PATIENT_IDENTIFIER_3, "Jack", "Intermountain", "06/30/1956", address,
            AdministrativeGender.MALE, "Mr."));
        return patients;
    }
    
    /**
     * Deletes all patient sharing the identifier argument
     *
     * @param patientSetIdentifier The identifier for the set of generated patients
     */
    public void deleteDemoPatient(Identifier patientSetIdentifier) {
        patientService.deletePatientByIdentifier(patientSetIdentifier);
    }
    
    /**
     * Deletes all patient sharing the PATIENT_GROUP_IDENTIFIER
     */
    public void deleteDemoPatients() {
        deleteDemoPatient(PATIENT_GROUP_IDENTIFIER);
    }
    
    /**
     * Builds a patient instance but does NOT persist it.
     * 
     * @param identifier
     * @param givenName
     * @param surname
     * @param birthdate
     * @param gender
     * @param title
     * @return
     */
    public Patient buildPatient(Identifier identifier, String givenName, String surname, String birthdate, Address address,
                                AdministrativeGender gender, String prefix) {
        Patient patient = new Patient();
        identifier.setType(FhirTerminology.IDENT_MRN);
        patient.addIdentifier(identifier);
        if (address != null) {
            patient.addAddress(address);//Fix
        }
        Attachment photo = patient.addPhoto().setData(FhirUtil.getResourceAsByteArray(IMAGE_PATH + "patient1.jpeg"));
        photo.setContentType("image/jpeg");
        addPatientName(patient, givenName, surname, prefix);
        patient.setGender(gender);
        setPatientBirthdate(patient, birthdate);
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
     * Convenience method to add a patient birthdate in the right format
     * 
     * @param patient
     * @param dateString
     */
    private void setPatientBirthdate(Patient patient, String dateString) {
        try {
            Date date = formatter.parse(dateString);
            patient.setBirthDate(date);
        } catch (Exception e) {
            e.printStackTrace();//ignore error for now
        }
    }
    
    /**
     * Create Sepsis condition for demo
     */
    public Condition buildSepsisCondition(Patient patient) {
        Condition condition = new Condition();
        condition.setCode(buildCodeableConcept(FhirTerminology.SYS_SNOMED, "10001005", "Bacterial sepsis (disorder)"));
        condition.setVerificationStatus(ConditionVerificationStatus.DIFFERENTIAL);
        condition.setDateRecorded(new Date());
        condition.setPatientTarget(patient);
        return condition;
    }
    
    public Procedure buildGeneticTestProcedure(Patient patient) {
        Procedure procedure = new Procedure();
        procedure.setCode(
            buildCodeableConcept(FhirTerminology.SYS_SNOMED, "405825005", "Molecular genetic test (procedure)"));
        procedure.setPerformed(new DateType(new Date()));
        Reference reference = new Reference(patient);
        procedure.setSubject(reference);
        procedure.setOutcome(
            buildCodeableConcept("http://demo/terminology", "123", "Substance metabolic disorder - Metoprolol"));
        return procedure;
    }
    
    /**
     * Method to invoke to patch demo data
     */
    public void patchDemoData() {
    }
    
    /**
     * Method clears all demo data.
     */
    public void clearData() {
        deleteDemoPatients();
        clearMedicationAdministrations();
        clearMedicationOrders();
        clearConditions();
    }
    
    /**
     * Method clears all demo data.
     */
    public void clearData(Identifier patientSetId, Identifier medAdminId, Identifier medOrdersId, Identifier conditionId) {
        deleteDemoPatient(patientSetId);
        clearMedicationAdministrations(medAdminId);
        clearMedicationOrders(medOrdersId);
        clearConditions(conditionId);
    }
}
