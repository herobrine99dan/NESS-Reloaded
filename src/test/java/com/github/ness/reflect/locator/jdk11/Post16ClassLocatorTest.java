package com.github.ness.reflect.locator.jdk11;

import com.github.ness.reflect.locator.ClassLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class Post16ClassLocatorTest {

    private final PackageSearch packageSearch;
    private final ClassLocator craftbukkitDelegate;

    public Post16ClassLocatorTest(@Mock PackageSearch packageSearch, @Mock ClassLocator craftbukkitDelegate) {
        this.packageSearch = packageSearch;
        this.craftbukkitDelegate = craftbukkitDelegate;
    }

    @BeforeEach
    public void disableCraftbukkitDelegate() {
        lenient().when(craftbukkitDelegate.getNmsClass(any())).thenThrow(new AssertionError());
        lenient().when(craftbukkitDelegate.getObcClass(any())).thenThrow(new AssertionError());
    }

    private ClassLocator newLocator(String prefix) {
        return new Post16ClassLocator(packageSearch, prefix, craftbukkitDelegate);
    }

    @Test
    public void noClassFoundNoPackages() {
        when(packageSearch.obtainPackages()).thenReturn(List.of());
        assertNull(newLocator("").getNmsClass(getClass().getSimpleName()));
    }

    @Test
    public void noClassFoundNoClass() {
        when(packageSearch.obtainPackages()).thenReturn(List.of(getClass().getPackageName()));
        assertNull(newLocator("").getNmsClass("No such class"));
    }

    @Test
    public void classFoundSimply() {
        when(packageSearch.obtainPackages()).thenReturn(List.of(getClass().getPackageName()));
        when(packageSearch.classForName(getClass().getName())).thenAnswer((i) -> getClass());
        assertEquals(getClass(),
                newLocator(getClass().getPackageName()).getNmsClass(getClass().getSimpleName()));
    }

    @Test
    public void classFoundMultiplePackages() {
        when(packageSearch.obtainPackages()).thenReturn(getClass().getModule().getPackages());
        when(packageSearch.classForName(getClass().getName())).thenAnswer((i) -> getClass());
        assertEquals(getClass(),
                newLocator("com.github.ness").getNmsClass(getClass().getSimpleName()));
    }
}
