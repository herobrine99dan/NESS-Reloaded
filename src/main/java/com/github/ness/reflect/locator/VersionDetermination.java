package com.github.ness.reflect.locator;

import java.util.List;
import java.util.Objects;

class VersionDetermination {

    private final String nmsVersion;

    private static final int MINIMUM_SUPPORTED_VERSION = 8;

    VersionDetermination(String nmsVersion) {
        this.nmsVersion = Objects.requireNonNull(nmsVersion);
    }

    static String getNmsVersion(String craftbukkitPackage) {
        return craftbukkitPackage.substring(craftbukkitPackage.lastIndexOf(".") + 1);
    }

    boolean is16OrBelow() {
        if (isVersion(7)) {
            throw new IllegalStateException("You're running on an outdated server version. NESS does not support 1.7 or below.");
        }
        for (int version = MINIMUM_SUPPORTED_VERSION; version < 17; version++) {
            if (isVersion(version)) {
                return true;
            }
        }
        return false;
    }

    private boolean isVersion(int version) {
        return nmsVersion.startsWith("v1_" + version);
    }
}
