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

import org.carewebframework.ui.zk.AbstractListitemRenderer;

import org.zkoss.util.media.AMedia;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

import org.hspconsortium.cwf.fhir.document.Document;
import org.hspconsortium.cwf.fhir.document.DocumentContent;
import org.hspconsortium.cwf.ui.reporting.Constants;

/**
 * Renderer for the document display.
 */
public class DocumentDisplayRenderer extends AbstractListitemRenderer<Document, Object> {
    
    
    public DocumentDisplayRenderer() {
        super("", null);
    }
    
    /**
     * Render the list item for the specified document.
     *
     * @param item List item to render.
     * @param doc The document associated with the list item.
     */
    @Override
    public void renderItem(Listitem item, Document doc) {
        Listcell cell = new Listcell();
        item.appendChild(cell);
        Div sep = new Div();
        sep.setSclass("cwf-documents-sep");
        cell.appendChild(sep);
        Div div = new Div();
        div.setSclass(Constants.SCLASS_TEXT_REPORT_TITLE);
        cell.appendChild(div);
        Hbox boxHeader = new Hbox();
        Label header = new Label(doc.getTitle());
        header.setZclass(Constants.SCLASS_TEXT_REPORT_TITLE);
        boxHeader.appendChild(header);
        div.appendChild(boxHeader);
        
        for (DocumentContent content : doc.getContent()) {
            if (content.getContentType().equals("text/html")) {
                Html html = new Html();
                html.setContent(content.toString());
                cell.appendChild(html);
            } else if (content.getContentType().equals("text/plain")) {
                Label lbl = new Label(content.toString());
                lbl.setMultiline(true);
                lbl.setPre(true);
                cell.appendChild(lbl);
            } else {
                AMedia media = new AMedia(null, null, content.getContentType(), content.getData());
                Iframe frame = new Iframe();
                frame.setContent(media);
                cell.appendChild(frame);
            }
        }
    }
    
}
