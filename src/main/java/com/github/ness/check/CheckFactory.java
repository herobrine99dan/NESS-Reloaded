package com.github.ness.check;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.github.ness.NessPlayer;

import lombok.Getter;

public class CheckFactory {

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CheckFactory [constructorCache=" + constructorCache + ", clazz=" + clazz + "]";
    }

    private final Constructor<?> constructorCache;
    @Getter
    private final Class<?> clazz;
 
    public CheckFactory(Class<?> clazz) throws NoSuchMethodException, SecurityException {
        this.constructorCache = clazz.getDeclaredConstructor(NessPlayer.class, CheckManager.class);
        this.clazz = clazz;
    }

    public Check makeEqualCheck(NessPlayer nessPlayer, CheckManager manager) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SecurityException {
        Check check = (Check) this.constructorCache.newInstance(nessPlayer, manager);
        check.startScheduler();
        return check;
    }

}
