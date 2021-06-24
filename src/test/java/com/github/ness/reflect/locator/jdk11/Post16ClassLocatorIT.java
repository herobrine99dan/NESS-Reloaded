package com.github.ness.reflect.locator.jdk11;

import com.github.ness.NessAnticheat;
import com.github.ness.reflect.locator.ClassLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
public class Post16ClassLocatorIT {

    private ClassLocator classLocator;

    @BeforeEach
    public void setClassLocator(@Mock ClassLocator craftbukkitDelegate) {
        lenient().when(craftbukkitDelegate.getNmsClass(any())).thenThrow(new AssertionError());
        lenient().when(craftbukkitDelegate.getObcClass(any())).thenThrow(new AssertionError());

        classLocator = new Post16ClassLocator(
                new ModulePackageSearch(getClass().getModule()),
                "com.github.ness",
                craftbukkitDelegate);
    }

    @Test
    public void classFoundMultiplePackages() {
        assertEquals(getClass(),
                classLocator.getNmsClass(getClass().getSimpleName()));
    }

    @Test
    public void classFoundMultiplePackagesBasePackage() {
        assertEquals(NessAnticheat.class,
                classLocator.getNmsClass(NessAnticheat.class.getSimpleName()));
    }
}
