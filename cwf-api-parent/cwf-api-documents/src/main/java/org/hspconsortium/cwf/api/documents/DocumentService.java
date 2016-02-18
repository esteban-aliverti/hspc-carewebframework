/*
 * #%L
 * cwf-api-documents
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
package org.hspconsortium.cwf.api.documents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import org.carewebframework.api.spring.SpringUtil;
import org.hspconsortium.cwf.api.ClientUtil;
import org.hspconsortium.cwf.fhir.common.FhirUtil;

import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.dstu2.resource.DocumentReference;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.resource.ValueSet;
import ca.uhn.fhir.rest.gclient.IQuery;
import ca.uhn.fhir.rest.gclient.ReferenceClientParam;

/**
 * This is the documents api implementation.
 */
public class DocumentService {
    
    public static DocumentService getInstance() {
        return SpringUtil.getBean("documentService", DocumentService.class);
    }
    
    public boolean hasDocuments(Patient patient) {
        return patient != null;
    }
    
    /**
     * Retrieves document references for a given patient.
     * 
     * @param patient Patient whose documents are to be retrieved.
     * @param startDate Start date for retrieval.
     * @param endDate End date for retrieval.
     * @param type Document type.
     * @return List of matching documents.
     */
    public List<Document> retrieveReferences(Patient patient, Date startDate, Date endDate, String type) {
        ReferenceClientParam subject = new ReferenceClientParam(DocumentReference.SP_SUBJECT + ":Patient");
        
        IQuery<Bundle> query = ClientUtil.getFhirClient().search().forResource(DocumentReference.class)
                .where(subject.hasId(patient.getId().getIdPart()));
        //.forResource("Patient/" + patient.getId().getIdPart() + "/DocumentReference");
        
        if (startDate != null) {
            query.where(DocumentReference.PERIOD.afterOrEquals().day(startDate));
        }
        
        if (endDate != null) {
            query.where(DocumentReference.PERIOD.beforeOrEquals().day(endDate));
        }
        
        if (type != null) {
            query.where(DocumentReference.TYPE.exactly().code(type));
            
        }
        
        Bundle bundle = query.execute();
        List<DocumentReference> list = FhirUtil.getEntries(bundle, DocumentReference.class);
        List<Document> results = new ArrayList<Document>(list.size());
        
        for (DocumentReference ref : list) {
            Document doc = new Document(ref);
            results.add(doc);
        }
        return results;
    }
    
    public Collection<String> getTypes() {
        TreeSet<String> results = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        
        try {
            Bundle bundle = ClientUtil.getFhirClient().search().forResource(ValueSet.class)
                    .where(ValueSet.NAME.matchesExactly().value("DocumentType")).execute();
                    
            for (ValueSet vs : FhirUtil.getEntries(bundle, ValueSet.class)) {
                for (ValueSet.CodeSystemConcept concept : vs.getCodeSystem().getConcept()) {
                    results.add(concept.getDisplay().toString());
                }
            }
            
        } catch (Exception e) {}
        
        return results;
    }
}
