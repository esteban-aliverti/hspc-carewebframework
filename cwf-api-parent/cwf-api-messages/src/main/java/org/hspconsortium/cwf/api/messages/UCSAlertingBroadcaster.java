package org.hspconsortium.cwf.api.messages;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.socraticgrid.hl7.services.uc.interfaces.UCSAlertingIntf;
import org.socraticgrid.hl7.services.uc.model.Message;
import org.socraticgrid.hl7.services.uc.model.MessageModel;

/**
 * Alert Listener whose function is to forward the event to the registered alert listeners on this
 * broadcaster.
 * 
 * @author esteban, claude
 */
public class UCSAlertingBroadcaster extends AbstractBroadcaster<UCSAlertingIntf> implements UCSAlertingIntf {
    
    
    private static final Log log = LogFactory.getLog(UCSAlertingBroadcaster.class);
    
    @Override
    public <T extends Message> boolean receiveAlertMessage(MessageModel<T> mm, List<String> list, String string) {
        for (UCSAlertingIntf alertListener : listeners) {
            try {
                alertListener.receiveAlertMessage(mm, list, string);
            } catch (Exception e) {
                log.error("Skipping listener on receiveAlertMessage() " + getClass(), e);
            }
        }
        return true;
    }
    
    @Override
    public <T extends Message> boolean updateAlertMessage(MessageModel<T> mm, MessageModel<T> mm1, List<String> list,
                                                          String string) {
        for (UCSAlertingIntf alertListener : listeners) {
            try {
                alertListener.updateAlertMessage(mm, mm1, list, string);
            } catch (Exception e) {
                log.error("Skipping listener on updateAlertMessage() " + getClass(), e);
            }
        }
        return true;
    }
    
    @Override
    public <T extends Message> boolean cancelAlertMessage(MessageModel<T> mm, List<String> list, String string) {
        for (UCSAlertingIntf alertListener : listeners) {
            try {
                alertListener.cancelAlertMessage(mm, list, string);
            } catch (Exception e) {
                log.error("Skipping listener on cancelAlertMessage() " + getClass(), e);
            }
        }
        
        return true;
    }
    
}
