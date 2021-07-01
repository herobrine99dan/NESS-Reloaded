package com.github.ness.reflect.locator;

import com.github.ness.reflect.locator.jdk11.Post16ClassLocator;
import org.bukkit.Server;

public interface ClassLocator {

    Class<?> getNmsClass(String className);

    Class<?> getObcClass(String className);

    static ClassLocator create(Server server) {
        String craftbukkitPackage = server.getClass().getPackage().getName();
        String nmsVersion = VersionDetermination.getNmsVersion(craftbukkitPackage);
        SimpleClassLocator simpleLocator = new SimpleClassLocator(nmsVersion);
        if (new VersionDetermination(nmsVersion).is16OrBelow()) {
            return simpleLocator;
        }
        return Post16ClassLocator.create(server, simpleLocator);
    }
}
