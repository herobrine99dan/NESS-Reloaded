package com.github.ness;

import lombok.Getter;

public abstract class RunnableDataContainer implements Runnable {
    
    /**
     * This class is used in SyncScheduler, contains the NessPlayer object and some arguments
     * @since 3.0.0
     * @author herobrine99dan
     */
    @Getter
    final NessPlayer nessPlayer;
    @Getter
    final Object[] array;
    
    public RunnableDataContainer(NessPlayer nessPlayer, Object...objects) {
        this.nessPlayer = nessPlayer;
        this.array = objects;
    }

}
