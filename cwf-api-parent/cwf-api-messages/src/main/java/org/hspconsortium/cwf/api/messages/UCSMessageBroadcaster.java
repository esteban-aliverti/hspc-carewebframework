package org.hspconsortium.cwf.api.messages;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.socraticgrid.hl7.services.uc.exceptions.BadBodyException;
import org.socraticgrid.hl7.services.uc.exceptions.FeatureNotSupportedException;
import org.socraticgrid.hl7.services.uc.exceptions.InvalidContentException;
import org.socraticgrid.hl7.services.uc.exceptions.InvalidMessageException;
import org.socraticgrid.hl7.services.uc.exceptions.MissingBodyTypeException;
import org.socraticgrid.hl7.services.uc.exceptions.ProcessingException;
import org.socraticgrid.hl7.services.uc.exceptions.ServiceAdapterFaultException;
import org.socraticgrid.hl7.services.uc.exceptions.UndeliverableMessageException;
import org.socraticgrid.hl7.services.uc.interfaces.UCSClientIntf;
import org.socraticgrid.hl7.services.uc.model.Conversation;
import org.socraticgrid.hl7.services.uc.model.DeliveryAddress;
import org.socraticgrid.hl7.services.uc.model.Message;
import org.socraticgrid.hl7.services.uc.model.MessageModel;

/**
 * Message Listener whose function is to forward the event to the registered message listeners on
 * this broadcaster.
 * 
 * @author esteban, claude
 */
public class UCSMessageBroadcaster extends AbstractBroadcaster<UCSClientIntf> implements UCSClientIntf {
    
    
    private static final Log log = LogFactory.getLog(UCSMessageBroadcaster.class);
    
    @Override
    public boolean callReady(Conversation c, String string, String string1) {
        for (UCSClientIntf messageListener : listeners) {
            try {
                messageListener.callReady(c, string, string1);
            } catch (Exception e) {
                log.error("Skipping listener on callReady() " + getClass(), e);
            }
        }
        return true;
    }
    
    @Override
    public <T extends Message> boolean handleException(MessageModel<T> mm, DeliveryAddress da, DeliveryAddress da1,
                                                       ProcessingException pe, String string) {
        
        if (pe.getFault() != null && pe.getFault().contains("Reference not found!")) {
            return true;
        }
        
        for (UCSClientIntf messageListener : listeners) {
            try {
                messageListener.handleException(mm, da, da1, pe, string);
            } catch (Exception e) {
                log.error("Skipping listener on handleException() " + getClass(), e);
            }
        }
        return true;
    }
    
    @Override
    public <T extends Message> boolean handleNotification(MessageModel<T> mm, String string) {
        return true;
    }
    
    @Override
    public <T extends Message> MessageModel<T> handleResponse(MessageModel<T> mm,
                                                              String string) throws InvalidMessageException,
                                                                             InvalidContentException,
                                                                             MissingBodyTypeException, BadBodyException,
                                                                             ServiceAdapterFaultException,
                                                                             UndeliverableMessageException,
                                                                             FeatureNotSupportedException {
        for (UCSClientIntf messageListener : listeners) {
            try {
                messageListener.handleResponse(mm, string);
            } catch (Exception e) {
                log.error("Skipping listener on handleResponse() " + getClass(), e);
            }
        }
        return null;
    }
    
    @Override
    public <T extends Message> boolean receiveMessage(MessageModel<T> mm, String string) {
        for (UCSClientIntf messageListener : listeners) {
            try {
                messageListener.receiveMessage(mm, string);
            } catch (Exception e) {
                log.error("Skipping listener on receiveMessage() " + getClass(), e);
            }
        }
        return true;
    }
    
}
