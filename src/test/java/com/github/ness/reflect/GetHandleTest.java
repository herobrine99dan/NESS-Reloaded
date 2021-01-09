package com.github.ness.reflect;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GetHandleTest {

    @ParameterizedTest
    @ArgumentsSource(ReflectionArgumentsProvider.class)
    public void getHandle(Reflection reflection) {
        assertEquals(
                reflection.getMethod(CraftPlayer.class, MemberDescriptions.forMethod("getHandle"))
                        .unreflect().type().returnType(),
                EntityPlayer.class);

        assertEquals(
                reflection.getMethod(CraftHuman.class, MemberDescriptions.forMethod("getHandle"))
                        .unreflect().type().returnType(),
                EntityHuman.class);
    }

    public static class CraftHuman {

        public EntityHuman getHandle() {
            return null;
        }

    }

    public static class CraftPlayer extends CraftHuman {

        @Override
        public EntityPlayer getHandle() {
            return null;
        }

    }

    public static class EntityHuman { }
    public static class EntityPlayer extends EntityHuman { }
}
