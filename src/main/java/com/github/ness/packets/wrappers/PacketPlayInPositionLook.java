package com.github.ness.packets.wrappers;

import lombok.Getter;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PacketPlayInPositionLook extends SimplePacket {

    private static final boolean is1_8 = Bukkit.getVersion().contains("1.8");

    private static volatile boolean initialised;
    private static Method aMethod;
    private static Method bMethod;
    private static Method cMethod;

    @Getter
    private final double x;
    @Getter
    private final double y;
    @Getter
    private final double z;

    public PacketPlayInPositionLook(Object object) {
        super(object);
        initialiseMethodsIfNecessary(object);
        this.x = invokeDoubleMethod(object, aMethod);
        this.y = invokeDoubleMethod(object, bMethod);
        this.z = invokeDoubleMethod(object, cMethod);
    }

    private static void initialiseMethodsIfNecessary(Object packetObject) {
        if (!initialised) {
            Class<?> packetClass = packetObject.getClass();
            synchronized (PacketPlayInPositionLook.class) {
                if (!initialised) {
                    aMethod = getDoubleMethod(packetClass, "a");
                    bMethod = getDoubleMethod(packetClass, "b");
                    cMethod = getDoubleMethod(packetClass, "c");
                    initialised = true;
                }
            }
        }
    }

    private static Method getDoubleMethod(Class<?> clazz, String methodName) {
        try {
            if (is1_8) {
                return clazz.getMethod(methodName);
            } else {
                return clazz.getMethod(methodName, double.class);
            }
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static double invokeDoubleMethod(Object packetObject, Method method) {
        try {
            if (is1_8) {
                return (double) method.invoke(packetObject);
            } else {
                return (double) method.invoke(packetObject, 0D);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new IllegalStateException(ex);
        }
    }

}
