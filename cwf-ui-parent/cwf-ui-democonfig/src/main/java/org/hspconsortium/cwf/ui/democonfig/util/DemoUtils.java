package org.hspconsortium.cwf.ui.democonfig.util;

import org.carewebframework.ui.zk.PopupDialog;

public class DemoUtils {
    
    
    /**
     * Demonstration Configuration Helper Class.
     */
    public static void show() {
        PopupDialog.popup("~./com/cogmedsys/hsp/ui/democonfig/demoConfigWin.zul", true, true, true);
    }
}
