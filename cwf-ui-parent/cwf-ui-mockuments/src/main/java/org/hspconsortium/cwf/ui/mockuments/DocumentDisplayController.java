/*
 * #%L
 * cwf-ui-mockuments
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
package org.hspconsortium.cwf.ui.mockuments;

import org.carewebframework.ui.FrameworkController;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;

import org.hspconsortium.cwf.fhir.document.Document;

/**
 * Controller for displaying the contents of selected documents.
 */
public class DocumentDisplayController extends FrameworkController {
    
    
    private static final long serialVersionUID = 1L;
    
    private Document document;
    
    private Div printRoot;
    
    private Combobox cboHeader;
    
    private final ComboitemRenderer<Document> comboRenderer = new DocumentDisplayComboRenderer();
    
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        cboHeader.setItemRenderer(comboRenderer);
    }
    
    /**
     * Sets the document to be displayed.
     *
     * @param document The document to be displayed.
     */
    protected void setDocument(Document document) {
        this.document = document;
        refresh();
    }
    
    @Override
    public void refresh() {
        super.refresh();
        Component body = null;
        ZKUtil.detachChildren(printRoot);
        
        if (document.getContentType().equals("text/plain")) {
            Label lbl = new Label(new String(document.getContent()));
            lbl.setMultiline(true);
            lbl.setPre(true);
            body = lbl;
        } else {
            AMedia media = new AMedia(null, null, document.getContentType(), document.getContent());
            Iframe frame = new Iframe();
            frame.setContent(media);
            body = frame;
        }
        
        if (body != null) {
            printRoot.appendChild(body);
        }
    }
}
