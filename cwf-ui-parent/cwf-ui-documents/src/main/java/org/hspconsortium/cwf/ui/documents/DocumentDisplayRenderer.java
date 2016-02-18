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

import org.hspconsortium.cwf.api.documents.Document;
import org.hspconsortium.cwf.ui.reporting.Constants;
import org.carewebframework.ui.zk.AbstractListitemRenderer;

import org.zkoss.util.media.AMedia;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

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
        sep.setSclass("cal-documents-sep");
        cell.appendChild(sep);
        Div div = new Div();
        div.setSclass(Constants.SCLASS_TEXT_REPORT_TITLE);
        cell.appendChild(div);
        Hbox boxHeader = new Hbox();
        Label header = new Label(doc.getTitle());
        header.setZclass(Constants.SCLASS_TEXT_REPORT_TITLE);
        boxHeader.appendChild(header);
        div.appendChild(boxHeader);
        byte[] content = doc.getContent();
        Component body;
        
        if (doc.getContentType().equals("text/plain")) {
            Label lbl = new Label(new String(content));
            lbl.setMultiline(true);
            lbl.setPre(true);
            body = lbl;
        } else {
            AMedia media = new AMedia(null, null, doc.getContentType(), content);
            Iframe frame = new Iframe();
            frame.setContent(media);
            body = frame;
        }
        cell.appendChild(body);
    }
    
}
