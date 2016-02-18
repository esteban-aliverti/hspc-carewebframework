/*
 * #%L
 * cwf-api-core
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
package org.hspconsortium.cwf.api.encounter;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Encounter.Participant;
import ca.uhn.fhir.model.dstu2.resource.Practitioner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.api.context.ContextItems;
import org.carewebframework.api.context.ContextManager;
import org.carewebframework.api.context.IContextEvent;
import org.carewebframework.api.context.ISharedContext;
import org.carewebframework.api.context.ManagedContext;
import org.hspconsortium.cwf.api.encounter.EncounterContext.IEncounterContextEvent;

/**
 * Wrapper for shared participant context.
 */
public class EncounterParticipantContext extends ManagedContext<Participant> implements IEncounterContextEvent {
    
    private static final Log log = LogFactory.getLog(EncounterParticipantContext.class);
    
    private static final String SUBJECT_NAME = "Participant";
    
    public interface IEncounterParticipantContextEvent extends IContextEvent {};
    
    /**
     * Returns the managed participant context.
     * 
     * @return Participant context.
     */
    @SuppressWarnings("unchecked")
    static public ISharedContext<Participant> getParticipantContext() {
        return (ISharedContext<Participant>) ContextManager.getInstance().getSharedContext(
            EncounterParticipantContext.class.getName());
    }
    
    /**
     * Returns the current participant from the shared context.
     * 
     * @return Current participant.
     */
    public static Participant getActiveParticipant() {
        return getParticipantContext().getContextObject(false);
    }
    
    /**
     * Requests a context change to the specified participant.
     * 
     * @param participant The participant.
     */
    public static void changeParticipant(Participant participant) {
        try {
            getParticipantContext().requestContextChange(participant);
        } catch (Exception e) {
            log.error("Error during request context change.", e);
        }
    }
    
    /**
     * Returns the current participant from the shared context if it is a practitioner.
     * 
     * @return Current practitioner.
     */
    public static Participant getActivePractitioner() {
        Participant participant = getParticipantContext().getContextObject(false);
        
        return participant == null ? null : participant.getIndividual().getResource() instanceof Practitioner ? participant
                : null;
    }
    
    /**
     * Creates the context wrapper and registers its context change callback interface.
     */
    public EncounterParticipantContext() {
        this(null);
    }
    
    /**
     * Creates the context wrapper and registers its context change callback interface.
     * 
     * @param participant Initial value for context.
     */
    public EncounterParticipantContext(Participant participant) {
        super(SUBJECT_NAME, IEncounterParticipantContextEvent.class, participant);
    }
    
    /**
     * Commits or rejects the pending context change.
     * 
     * @param accept If true, the pending change is committed. If false, the pending change is
     *            canceled.
     */
    @Override
    public void commit(boolean accept) {
        super.commit(accept);
    }
    
    /**
     * Creates a CCOW context from the specified participant object.
     */
    @Override
    protected ContextItems toCCOWContext(Participant participant) {
        //TODO: contextItems.setItem(...);
        return contextItems;
    }
    
    /**
     * Returns a participant instance based on the specified CCOW context.
     */
    @Override
    protected Participant fromCCOWContext(ContextItems contextItems) {
        Participant participant = null;
        
        try {
            participant = new Participant();
            //TODO: Populate participant object from context items.
            return participant;
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }
    
    /**
     * Returns a priority value of 5.
     * 
     * @return Priority value for context manager.
     */
    @Override
    public int getPriority() {
        return 5;
    }
    
    // IEncounterContextEvent
    
    @Override
    public void canceled() {
    }
    
    @Override
    public void committed() {
    }
    
    @Override
    public String pending(boolean silent) {
        Encounter encounter = EncounterContext.getEncounterContext().getContextObject(true);
        changeParticipant(encounter == null ? null : encounter.getParticipantFirstRep());
        return null;
    }
}
