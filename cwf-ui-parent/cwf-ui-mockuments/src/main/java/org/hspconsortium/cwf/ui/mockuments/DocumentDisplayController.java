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
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;

import org.hspconsortium.cwf.fhir.document.Document;
import org.hspconsortium.cwf.fhir.document.DocumentContent;
import org.hspconsortium.cwf.ui.reporting.Util;

/**
 * Controller for displaying the contents of selected documents.
 */
public class DocumentDisplayController extends FrameworkController {
    
    
    private static final long serialVersionUID = 1L;
    
    private Button btnPrint;
    
    private Div printRoot;
    
    private Document document;
    
    /**
     * Sets the document to be displayed.
     *
     * @param document The document to be displayed.
     */
    protected void setDocument(Document document) {
        this.document = document;
        btnPrint.setDisabled(document == null);
        
        if (printRoot != null) {
            ZKUtil.detachChildren(printRoot);
        }
        
        if (document != null) {
            for (DocumentContent content : document.getContent()) {
                if (content.getType().equals("text/html")) {
                    Html html = new Html();
                    html.setContent(content.toString());
                    printRoot.appendChild(html);
                } else if (content.getType().equals("text/plain")) {
                    Label lbl = new Label(content.toString());
                    lbl.setMultiline(true);
                    lbl.setPre(true);
                    printRoot.appendChild(lbl);
                } else {
                    AMedia media = new AMedia(null, null, content.getType(), content.getData());
                    Iframe frame = new Iframe();
                    frame.setContent(media);
                    printRoot.appendChild(frame);
                }
            }
        }
    }
    
    public void onClick$btnPrint() {
        Util.print(printRoot, document.getTitle(), "patient", null, false);
    };
}
