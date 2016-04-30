/*
 * #%L
 * cwf-ui-documents
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
package org.hspconsortium.cwf.ui.documents;

import org.carewebframework.ui.zk.AbstractComboitemRenderer;

import org.zkoss.zul.Comboitem;

import org.hspconsortium.cwf.fhir.document.Document;

/**
 * Renderer for the document display combo box selector.
 */
public class DocumentDisplayComboRenderer extends AbstractComboitemRenderer<Document> {
    
    
    /**
     * Render the combo item for the specified document.
     *
     * @param item Combo item to render.
     * @param doc The document associated with the list item.
     */
    @Override
    public void renderItem(Comboitem item, Document doc) {
        item.setLabel(doc.getTitle());
    }
}
