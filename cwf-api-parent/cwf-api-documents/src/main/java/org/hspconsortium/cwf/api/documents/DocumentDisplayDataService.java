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

import java.util.List;

import org.carewebframework.api.query.AbstractQueryServiceEx;
import org.carewebframework.api.query.IQueryContext;
import org.carewebframework.api.query.IQueryResult;
import org.carewebframework.api.query.QueryUtil;

/**
 * Data service wrapper for retrieving document contents.
 */
public class DocumentDisplayDataService extends AbstractQueryServiceEx<DocumentService, Document> {
    
    public DocumentDisplayDataService(DocumentService service) {
        super(service);
    }
    
    @Override
    public IQueryResult<Document> fetch(IQueryContext ctx) {
        @SuppressWarnings("unchecked")
        List<Document> documents = (List<Document>) ctx.getParam("documents");
        
        if (documents != null) {
            for (Document document : documents) {
                document.getContent();
            }
        }
        return QueryUtil.packageResult(documents);
    }
    
    @Override
    public boolean hasRequired(IQueryContext context) {
        return true;
    }
}
