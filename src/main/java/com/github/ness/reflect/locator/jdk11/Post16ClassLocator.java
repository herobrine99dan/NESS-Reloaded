package com.github.ness.reflect.locator.jdk11;

import com.github.ness.reflect.locator.ClassLocator;
import org.bukkit.Server;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * A class locator for minecraft versions 1.17 and later, which searches all packages
 * starting with {@code net.minecraft} for the given NMS class. <br>
 * <br>
 * For locating OBC classes, a delegate class locator is used.
 *
 */
public final class Post16ClassLocator implements ClassLocator {

    private final PackageSearch packageSearch;
    private final String packagePrefix;
    private final ClassLocator craftbukkitDelegate;

    Post16ClassLocator(PackageSearch packageSearch, String packagePrefix, ClassLocator craftbukkitDelegate) {
        this.packageSearch = Objects.requireNonNull(packageSearch, "packageSearch");
        this.packagePrefix = Objects.requireNonNull(packagePrefix, "packagePrefix");
        this.craftbukkitDelegate = Objects.requireNonNull(craftbukkitDelegate, "craftbukkitDelegate");
    }

    public static Post16ClassLocator create(Server server, ClassLocator craftbukkitDelegate) {
        Method getServerMethod;
        try {
            getServerMethod = server.getClass().getMethod("getServer");
        } catch (NoSuchMethodException ex) {
            throw new IllegalStateException(
                    "Unable to find CraftServer#getServer method. Has there been a Minecraft version update?", ex);
        }
        Class<?> serverClass = getServerMethod.getReturnType();
        Module nmsModule = serverClass.getModule();
        return new Post16ClassLocator(new ModulePackageSearch(nmsModule), "net.minecraft", craftbukkitDelegate);
    }

    @Override
    public Class<?> getNmsClass(String className) {
        for (String packageToSearch : packageSearch.obtainPackages()) {
            if (packageToSearch.startsWith(packagePrefix)) {
                String fullClassName = packageToSearch + '.' + className;
                Class<?> nmsClass =  packageSearch.classForName(fullClassName);
                if (nmsClass != null) {
                    return nmsClass;
                }
            }
        }
        return null;
    }

    @Override
    public Class<?> getObcClass(String className) {
        return craftbukkitDelegate.getObcClass(className);
    }

}
