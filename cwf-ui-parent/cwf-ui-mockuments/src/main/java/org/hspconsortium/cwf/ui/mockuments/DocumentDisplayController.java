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
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Include;
import org.zkoss.zul.Label;

import org.hspconsortium.cwf.fhir.document.Document;
import org.hspconsortium.cwf.fhir.document.DocumentContent;
import org.hspconsortium.cwf.ui.reporting.Util;

/**
 * Controller for displaying the contents of selected documents.
 */
public class DocumentDisplayController extends FrameworkController {
    
    
    private static final long serialVersionUID = 1L;
    
    public static final String QUESTIONNAIRE_CONTENT_TYPE = "application/xcwf-";
    
    private Component toolbar;
    
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
        
        if (printRoot != null) {
            ZKUtil.detachChildren(printRoot);
            ZKUtil.detachChildren(toolbar);
            btnPrint.setDisabled(document == null);
        }
        
        if (document != null) {
            for (DocumentContent content : document.getContent()) {
                String ctype = content.getType();
                
                if (ctype.startsWith(QUESTIONNAIRE_CONTENT_TYPE)) {
                    String zul = "~./org/hspconsortium/cwf/ui/mockuments/"
                            + ctype.substring(QUESTIONNAIRE_CONTENT_TYPE.length()) + ".zul"; // Hack - need to register these somehow
                    Include include = new Include();
                    include.setAttribute("document", document);
                    include.setAttribute("displayController", this);
                    include.setSrc(zul);
                    printRoot.appendChild(include);
                } else if (ctype.equals("text/html")) {
                    Html html = new Html();
                    html.setContent(content.toString());
                    printRoot.appendChild(html);
                } else if (ctype.equals("text/plain")) {
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
    }
    
    public void addToToolbar(Component source) {
        moveComponents(source, toolbar);
    }
    
    public void removeFromToolbar(Component target) {
        moveComponents(toolbar, target);
    }
    
    private void moveComponents(Component source, Component target) {
        Component child;
        
        while ((child = source.getFirstChild()) != null) {
            child.setParent(target);
        }
    }
}
