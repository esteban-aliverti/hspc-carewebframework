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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.carewebframework.api.context.ContextItems;
import org.carewebframework.api.context.ContextManager;
import org.carewebframework.api.context.IContextEvent;
import org.carewebframework.api.context.ISharedContext;
import org.carewebframework.api.context.ManagedContext;
import org.hspconsortium.cwf.api.patient.PatientContext.IPatientContextEvent;

/**
 * Wrapper for shared encounter context.
 */
public class EncounterContext extends ManagedContext<Encounter> implements IPatientContextEvent {
    
    private static final Log log = LogFactory.getLog(EncounterContext.class);
    
    private static final String SUBJECT_NAME = "Encounter";
    
    public interface IEncounterContextEvent extends IContextEvent {};
    
    /**
     * Returns the managed encounter context.
     * 
     * @return Encounter context.
     */
    @SuppressWarnings("unchecked")
    static public ISharedContext<Encounter> getEncounterContext() {
        return (ISharedContext<Encounter>) ContextManager.getInstance().getSharedContext(EncounterContext.class.getName());
    }
    
    /**
     * Returns the current encounter from the shared context.
     * 
     * @return Current encounter.
     */
    public static Encounter getActiveEncounter() {
        return getEncounterContext().getContextObject(false);
    }
    
    /**
     * Requests a context change to the specified encounter.
     * 
     * @param encounter The encounter.
     */
    public static void changeEncounter(Encounter encounter) {
        try {
            getEncounterContext().requestContextChange(encounter);
        } catch (Exception e) {
            log.error("Error during request context change.", e);
        }
    }
    
    /**
     * Creates the context wrapper and registers its context change callback interface.
     */
    public EncounterContext() {
        this(null);
    }
    
    /**
     * Creates the context wrapper and registers its context change callback interface.
     * 
     * @param encounter Initial value for context.
     */
    public EncounterContext(Encounter encounter) {
        super(SUBJECT_NAME, IEncounterContextEvent.class, encounter);
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
     * Creates a CCOW context from the specified encounter object.
     */
    @Override
    protected ContextItems toCCOWContext(Encounter encounter) {
        //TODO: contextItems.setItem(...);
        return contextItems;
    }
    
    /**
     * Returns an encounter instance based on the specified CCOW context.
     */
    @Override
    protected Encounter fromCCOWContext(ContextItems contextItems) {
        Encounter encounter = null;
        
        try {
            encounter = new Encounter();
            //TODO: Populate encounter object from context items.
            return encounter;
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
    
    // IPatientContextEvent
    
    @Override
    public void canceled() {
    }
    
    @Override
    public void committed() {
    }
    
    @Override
    public String pending(boolean silent) {
        //Patient patient = PatientContext.getPatientContext().getContextObject(true);
        changeEncounter(null);
        //changeEncounter(EncounterUtil.getDefaultEncounter(patient));
        return null;
    }
}
