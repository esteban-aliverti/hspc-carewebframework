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

import java.util.Date;

import org.carewebframework.api.query.AbstractQueryServiceEx;
import org.carewebframework.api.query.IQueryContext;
import org.carewebframework.api.query.IQueryResult;
import org.carewebframework.api.query.QueryUtil;
import org.carewebframework.common.DateRange;
import org.carewebframework.common.DateUtil;

import org.hl7.fhir.dstu3.model.Patient;

/**
 * Data service wrapper for documents service.
 */
public class DocumentListDataService extends AbstractQueryServiceEx<DocumentService, Document> {
    
    
    public DocumentListDataService(DocumentService service) {
        super(service);
    }
    
    @Override
    public IQueryResult<Document> fetch(IQueryContext context) {
        DateRange dateRange = (DateRange) context.getParam("dateRange");
        Date startDate = dateRange.getStartDate();
        Date endDate = DateUtil.endOfDay(dateRange.getEndDate());
        Patient patient = (Patient) context.getParam("patient");
        return QueryUtil
                .packageResult(service.retrieveReferences(patient, startDate, endDate, (String) context.getParam("type")));
    }
    
    @Override
    public boolean hasRequired(IQueryContext context) {
        return true;
    }
}
