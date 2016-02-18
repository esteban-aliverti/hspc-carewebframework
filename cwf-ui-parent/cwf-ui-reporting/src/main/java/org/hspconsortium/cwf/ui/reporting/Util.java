/*
 * #%L
 * cwf-ui-reporting
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
package org.hspconsortium.cwf.ui.reporting;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

import org.carewebframework.common.StrUtil;
import org.carewebframework.ui.zk.ZKUtil;

import org.zkoss.zk.ui.Component;

/**
 * Utility methods for processing report elements.
 */
public class Util {

    /**
     * Invokes a client-side print request.
     *
     * @param printRoot Root component for printing.
     * @param title Optional title text.
     * @param header Header to print at top of first page.
     * @param styleSheet Style sheet to be applied.
     * @param preview If true, show print preview.
     */
    public static void print(Component printRoot, String title, String header, String styleSheet, boolean preview) {
        List<String> content = new ArrayList<String>();

        if (header != null && !header.isEmpty()) {
            content.add("$report_headers $" + header);
        }

        if (title != null && !title.isEmpty()) {
            content.add("<div><div class='cwf-reporting-header-title'>" + StringEscapeUtils.escapeHtml(title)
                    + "</div></div>");
        }

        content.add("#" + printRoot.getUuid());

        ZKUtil.printToClient(content, StrUtil.toList(styleSheet, ","), preview);
    }

    /**
     * Enforces static class.
     */
    private Util() {
    };
}
