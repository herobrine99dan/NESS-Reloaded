package com.github.ness;

public abstract class RunnableDataContainer implements Runnable {
    
    NessPlayer nessPlayer;
    Object[] array;
    
    public RunnableDataContainer(NessPlayer nessPlayer, Object...objects) {
        this.nessPlayer = nessPlayer;
        this.array = objects;
    }

}
