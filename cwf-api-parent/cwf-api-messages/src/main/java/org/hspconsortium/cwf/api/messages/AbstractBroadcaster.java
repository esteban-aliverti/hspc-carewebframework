package org.hspconsortium.cwf.api.messages;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBroadcaster<T> {
    
    
    protected final List<T> listeners = new ArrayList<>();
    
    public AbstractBroadcaster() {
    }
    
    /**
     * Constructor takes initial list of listeners to whom events will be broadcasted.
     * 
     * @param listeners Initial set of listeners.
     */
    public AbstractBroadcaster(List<T> listeners) {
        this.listeners.addAll(listeners);
    }
    
    /**
     * Registers a listener with the broadcaster
     * 
     * @param listener Listener to register
     */
    public void registerListener(T listener) {
        this.listeners.add(listener);
    }
    
    /**
     * Unregisters a listener from the broadcaster
     * 
     * @param listener Listener to unregister
     */
    public void unregisterListener(T listener) {
        this.listeners.remove(listener);
    }
    
}
