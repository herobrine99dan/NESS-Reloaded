package com.github.ness.check;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.github.ness.NessPlayer;

import lombok.Getter;

public class CheckFactory {

    private final Constructor<?> constructorCache;
    @Getter
    private final Class<?> clazz;
 
    public CheckFactory(Class<?> clazz) {
        this.constructorCache = clazz.getConstructors()[0];
        this.clazz = clazz;
    }

    public Check makeEqualCheck(NessPlayer nessPlayer) throws InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, SecurityException {
        Check check = (Check) this.constructorCache.newInstance(clazz, nessPlayer);
        check.startScheduler();
        return check;
    }

}
