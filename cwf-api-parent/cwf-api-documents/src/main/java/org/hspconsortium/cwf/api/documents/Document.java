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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.util.StringUtils;

import org.hl7.fhir.dstu3.model.Binary;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DocumentReference;
import org.hl7.fhir.dstu3.model.DocumentReference.DocumentReferenceContextComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.hspconsortium.cwf.api.ClientUtil;
import org.hspconsortium.cwf.fhir.common.FhirUtil;

/**
 * Model object wrapping a document reference and its contents (lazily loaded).
 */
public class Document implements Comparable<Document> {
    
    
    private static final byte[] EMPTY_CONTENT = {};
    
    private final DocumentReference reference;
    
    private Set<String> types;
    
    private byte[] content;
    
    public Document(DocumentReference reference) {
        this.reference = reference;
    }
    
    public DocumentReference getReference() {
        return reference;
    }
    
    public String getTitle() {
        String title = reference.getDescription();
        
        if (title == null) {
            Coding coding = FhirUtil.getFirst(reference.getType().getCoding());
            title = coding == null ? null : coding.getDisplay();
        }
        
        return title == null ? "" : title;
    }
    
    public Date getDateTime() {
        return reference.getCreated();
    }
    
    public String getLocationName() {
        DocumentReferenceContextComponent ctx = reference.getContext();
        CodeableConcept facilityType = ctx == null ? null : ctx.getFacilityType();
        Coding coding = facilityType == null ? null : FhirUtil.getFirst(facilityType.getCoding());
        return coding == null ? "" : coding.getDisplay().toString();
    }
    
    public String getAuthorName() {
        Reference resource = FhirUtil.getFirst(reference.getAuthor());
        return resource == null ? "" : resource.getDisplay().toString();
    }
    
    public Collection<String> getTypes() {
        if (types == null) {
            types = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            CodeableConcept dt = reference.getType();
            List<Coding> codings = dt == null ? null : dt.getCoding();
            
            if (codings != null) {
                for (Coding coding : codings) {
                    String type = coding.getDisplay().toString();
                    
                    if (!StringUtils.isEmpty(type)) {
                        types.add(type);
                    }
                }
                
            }
        }
        
        return types;
    }
    
    public boolean hasType(String type) {
        return getTypes().contains(type);
    }
    
    public String getContentType() {
        return FhirUtil.getFirst(reference.getContent()).getAttachment().getContentType();
    }
    
    public byte[] getContent() {
        if (content == null) {
            content = FhirUtil.getFirst(reference.getContent()).getAttachment().getData();
            
            if (content == null || content.length == 0) {
                Binary binary = ClientUtil.getFhirClient().read(Binary.class,
                    FhirUtil.getFirst(reference.getContent()).getAttachment().getUrl());
                content = binary.getContent();
            }
            
            if (content == null) {
                content = EMPTY_CONTENT;
            }
        }
        
        return content;
    }
    
    @Override
    public int compareTo(Document document) {
        return getTitle().compareToIgnoreCase(document.getTitle());
    }
}
