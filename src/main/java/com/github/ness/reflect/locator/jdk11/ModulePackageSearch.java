package com.github.ness.reflect.locator.jdk11;

import java.util.Objects;

final class ModulePackageSearch implements PackageSearch {

    private final Module module;

    ModulePackageSearch(Module module) {
        this.module = Objects.requireNonNull(module, "module");
    }

    @Override
    public Class<?> classForName(String className) {
        return Class.forName(module, className);
    }

    @Override
    public Iterable<String> obtainPackages() {
        return module.getPackages();
    }
}
