package com.github.ness.it.java8.v1_12_R1;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import org.bukkit.Server;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicInteger;

public final class ServerImpl {

    private final Server delegate;

    private ServerImpl(Server delegate) {
        this.delegate = delegate;
    }

    public static Server create(Server delegate) {
        return new ServerImpl(delegate).makeServer();
    }

    private Server makeServer() {
        Class<?> serverImplClass = new ByteBuddy()
                .with(new Naming())
                .subclass(Object.class)
                .defineField("delegate", Server.class, Visibility.PRIVATE)
                .defineMethod("setDelegate", Void.TYPE, Visibility.PUBLIC)
                .withParameters(Server.class)
                .intercept(FieldAccessor.ofBeanProperty())
                .defineMethod("getDelegate", Server.class, Visibility.PUBLIC)
                .intercept(FieldAccessor.ofBeanProperty())
                .implement(Server.class)
                .intercept(
                        MethodCall.invoke(MethodCall.MethodLocator.ForInstrumentedMethod.INSTANCE)
                                .onMethodCall(MethodCall.invoke(ElementMatchers.named("getDelegate")))
                                .withAllArguments()
                )
                .make().load(getClass().getClassLoader()).getLoaded();
        Object serverImpl;
        try {
            serverImpl = serverImplClass.getDeclaredConstructor().newInstance();
            serverImplClass.getMethod("setDelegate", Server.class).invoke(serverImpl, delegate);
        } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
        return (Server) serverImpl;
    }

    private static class Naming extends NamingStrategy.AbstractBase {

        private static final AtomicInteger COUNTER = new AtomicInteger();

        @Override
        protected String name(TypeDescription superClass) {
            return getClass().getPackage().getName() + ".Generated" + COUNTER.getAndIncrement();
        }
    }

}
