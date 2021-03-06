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
package org.hspconsortium.cwf.api.encounter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.carewebframework.api.spring.SpringUtil;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.EncounterLocationComponent;
import org.hl7.fhir.dstu3.model.Encounter.EncounterParticipantComponent;
import org.hl7.fhir.dstu3.model.Encounter.EncounterStatus;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.HumanName.NameUse;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.hl7.fhir.dstu3.model.UriType;
import org.hl7.fhir.dstu3.model.ValueSet;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hspconsortium.cwf.api.ClientUtil;
import org.hspconsortium.cwf.api.query.IResourceQueryEx;
import org.hspconsortium.cwf.fhir.common.FhirUtil;

/**
 * Encounter-related utility functions.
 */
public class EncounterUtil {
    
    
    public static final Coding primaryType = new Coding("http://hl7.org/fhir/v3/ParticipationType", "PPRF",
            "primary performer");
    
    private static volatile Map<String, CodeableConcept> serviceCategories;
    
    /**
     * Returns a reference to the encounter search engine.
     * 
     * @return Encounter search engine.
     */
    @SuppressWarnings("unchecked")
    public static IResourceQueryEx<Encounter, EncounterSearchCriteria> getSearchEngine() {
        return SpringUtil.getBean("encounterSearchEngine", IResourceQueryEx.class);
    }
    
    /**
     * Perform a search based on given criteria.
     * 
     * @param criteria Search criteria.
     * @return Resources matching the search criteria.
     */
    public static List<Encounter> search(EncounterSearchCriteria criteria) {
        return getSearchEngine().search(criteria);
    }
    
    /**
     * Returns the default encounter for the current institution for the specified patient. Search
     * is restricted to encounters belonging to the current institution, with care setting codes of
     * 'O', 'E', or 'I'. For inpatient encounters, the discharge date must be null and the admission
     * date must precede the current date (there are anomalous entries where the admission date is
     * in the future). For non-inpatient encounters, the admission date must fall on the same day as
     * the current date. If more than one encounter meets these criteria, further filtering is
     * applied. An encounter whose location matches the current location is selected preferentially.
     * Failing a match on location, non-inpatient encounters are given weight over inpatient
     * encounters. Failing all that, the first matching encounter is returned.
     *
     * @param patient Patient whose default encounter is sought.
     * @return The default encounter or null if one was not found.
     */
    public static Encounter getDefaultEncounter(Patient patient) {
        if (patient == null) {
            return null;
        }
        
        return null;
    }
    
    public static Encounter create(Patient patient, Date date, Location location, String sc) {
        Encounter encounter = new Encounter();
        Reference pat = new Reference(patient);
        encounter.setPatient(pat);
        Period period = new Period();
        period.setStart(date);
        encounter.setPeriod(period);
        Reference loc = new Reference(location);
        EncounterLocationComponent encloc = encounter.addLocation();
        encloc.setPeriod(period);
        encloc.setLocation(loc);
        CodeableConcept type = encounter.addType();
        CodeableConcept cat = getServiceCategory(sc);
        type.setText(cat.getText());
        type.getCoding().addAll(cat.getCoding());
        return encounter;
    }
    
    public static CodeableConcept createServiceCategory(String sc, String shortDx, String longDx) {
        CodeableConcept cpt = new CodeableConcept();
        cpt.setText(longDx);
        Coding coding = new Coding();
        coding.setCode(sc);
        coding.setDisplay(shortDx);
        cpt.getCoding().add(coding);
        return cpt;
    }
    
    public static CodeableConcept getServiceCategory(String category) {
        initServiceCategories();
        
        if (category == null) {
            return null;
        }
        
        CodeableConcept cat = serviceCategories.get(category);
        
        if (cat == null) {
            cat = createServiceCategory(category, "Unknown", "Unknown service category");
        }
        
        return cat;
    }
    
    public static Collection<CodeableConcept> getServiceCategories() {
        initServiceCategories();
        return serviceCategories.values();
    }
    
    private static void initServiceCategories() {
        if (serviceCategories == null) {
            loadServiceCategories();
        }
    }
    
    private static synchronized void loadServiceCategories() {
        if (serviceCategories == null) {
            Map<String, CodeableConcept> map = new LinkedHashMap<String, CodeableConcept>();
            Bundle bundle = ClientUtil.getFhirClient().search().forResource(ValueSet.class)
                    .where(CodeSystem.NAME.matchesExactly().value("EncounterType")).returnBundle(Bundle.class).execute();
            
            for (CodeSystem cs : FhirUtil.getEntries(bundle, CodeSystem.class)) {
                UriType system = cs.getUrlElement();
                
                for (ConceptDefinitionComponent concept : cs.getConcept()) {
                    CodeableConcept cc = new CodeableConcept();
                    Coding coding = cc.addCoding();
                    coding.setCode(concept.getCode());
                    coding.setDisplay(concept.getDisplay());
                    coding.setSystemElement(system);
                    cc.setText(concept.getDefinition());
                    map.put(coding.getCode(), cc);
                }
            }
            
            serviceCategories = map;
        }
        
        return;
    }
    
    public static String getServiceCategory(Encounter encounter) {
        CodeableConcept cpt = encounter == null ? null : FhirUtil.getFirst(encounter.getType());
        Coding coding = cpt == null ? null : FhirUtil.getFirst(cpt.getCoding());
        return coding == null ? null : coding.getCode();
    }
    
    public static boolean isLocked(Encounter encounter) {
        EncounterStatus status = encounter.getStatus();
        return status != null && status == EncounterStatus.FINISHED;
    }
    
    public static boolean isPrepared(Encounter encounter) {
        return encounter != null && !encounter.getLocation().isEmpty() && !encounter.getParticipant().isEmpty()
                && getServiceCategory(encounter) != null;
    }
    
    public static EncounterParticipantComponent getParticipantByType(Encounter encounter, Coding participationType) {
        for (EncounterParticipantComponent p : encounter.getParticipant()) {
            if (hasType(p, participationType)) {
                return p;
            }
        }
        
        return null;
    }
    
    public static boolean isPrimary(EncounterParticipantComponent participant) {
        return hasType(participant, primaryType);
    }
    
    public static boolean removeType(EncounterParticipantComponent participant, Coding participationType) {
        CodeableConcept cpt;
        boolean found = false;
        
        while ((cpt = findType(participant, participationType)) != null) {
            participant.getType().remove(cpt);
            found = true;
        }
        
        return found;
    }
    
    public static boolean addType(EncounterParticipantComponent participant, Coding participationType) {
        if (!hasType(participant, participationType)) {
            CodeableConcept cpt = participant.addType();
            cpt.getCoding().add(participationType);
            return true;
        }
        
        return false;
    }
    
    public static boolean hasType(EncounterParticipantComponent participant, Coding participationType) {
        return findType(participant, participationType) != null;
    }
    
    private static CodeableConcept findType(EncounterParticipantComponent participant, Coding participationType) {
        if (participant != null) {
            for (CodeableConcept tp : participant.getType()) {
                for (Coding coding : tp.getCoding()) {
                    if (coding.getSystem().equals(participationType.getSystem())
                            && coding.getCode().equals(participationType.getCode())) {
                        return tp;
                    }
                }
            }
        }
        
        return null;
    }
    
    public static EncounterParticipantComponent getPrimaryParticipant(Encounter encounter) {
        return getParticipantByType(encounter, primaryType);
    }
    
    public static HumanName getName(EncounterParticipantComponent participant) {
        IBaseResource resource = ClientUtil.getResource(participant.getIndividual());
        return resource instanceof Practitioner
                ? FhirUtil.getName(((Practitioner) resource).getName(), NameUse.USUAL, NameUse.OFFICIAL)
                : resource instanceof RelatedPerson ? ((RelatedPerson) resource).getNameFirstRep() : null;
    }
    
    public static Practitioner getPractitioner(EncounterParticipantComponent participant) {
        if (participant == null) {
            return null;
        }
        
        Reference resource = participant.getIndividual();
        IBaseResource ele = resource.getResource();
        return ele instanceof Practitioner ? (Practitioner) ele : null;
    }
    
    public static List<Practitioner> getPractitioners(List<EncounterParticipantComponent> participants) {
        List<Practitioner> list = new ArrayList<>();
        
        for (EncounterParticipantComponent participant : participants) {
            Practitioner practitioner = getPractitioner(participant);
            
            if (practitioner != null) {
                list.add(practitioner);
            }
        }
        return list;
    }
    
    /**
     * Returns an encounter location with the specified physical type.
     * 
     * @param encounter An encounter.
     * @param physicalType The physical location type sought.
     * @return The encounter location corresponding to the specified physical type, or null if none
     *         found.
     */
    public static EncounterLocationComponent getLocationByPhysicalType(Encounter encounter, String physicalType) {
        for (EncounterLocationComponent encounterLocation : encounter.getLocation()) {
            Location location = ClientUtil.getResource(encounterLocation.getLocation(), Location.class);
            
            if (physicalType.equals(FhirUtil.getFirst(location.getPhysicalType().getCoding()).getCode())) {
                return encounterLocation;
            }
        }
        
        return null;
    }
    
    /**
     * Enforces static class.
     */
    protected EncounterUtil() {
    }
    
}
