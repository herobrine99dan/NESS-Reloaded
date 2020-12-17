package com.github.ness.reflect;

public interface ClassLocator {

    Class<?> getNmsClass(String className);

    Class<?> getObcClass(String className);
}
