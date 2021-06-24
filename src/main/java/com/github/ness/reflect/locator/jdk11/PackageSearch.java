package com.github.ness.reflect.locator.jdk11;

interface PackageSearch {
    Class<?> classForName(String className);

    Iterable<String> obtainPackages();
}
