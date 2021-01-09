package com.github.ness.reflect;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

public class ReflectionArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext extensionContext) throws Exception {
        Reflection reflection = new CoreReflection();
        return Stream.of(
                reflection, new MethodHandleReflection(reflection), new InvokerCachingReflection(reflection))
                .map(Arguments::of);
    }

}
