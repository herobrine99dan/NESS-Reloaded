package com.github.ness.reflect.locator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VersionDeterminationTest {

    @Test
    public void getNmsVersion() {
        assertEquals(VersionDetermination.getNmsVersion("org.bukkit.craftbukkit.v1_8_R3"), "v1_8_R3");
        assertEquals(VersionDetermination.getNmsVersion("org.bukkit.craftbukkit.v1_12_R1"), "v1_12_R1");
        assertEquals(VersionDetermination.getNmsVersion("org.bukkit.craftbukkit.v1_16_R3"), "v1_16_R3");
        assertEquals(VersionDetermination.getNmsVersion("org.bukkit.craftbukkit.v1_17_R1"), "v1_17_R1");
    }

    @Test
    public void is16OrBelowFalse() {
        assertFalse(new VersionDetermination("v1_17_R1").is16OrBelow());
    }

    @ParameterizedTest
    @ValueSource(strings = {"v1_8_R3", "v1_12_R1", "v1_16_R3"})
    public void is16OrBelowTrue(String nmsVersion) {
        assertTrue(new VersionDetermination(nmsVersion).is16OrBelow());
    }

    @Test
    public void is16OrBelow1pt7() {
        assertThrows(IllegalStateException.class, new VersionDetermination("v1_7_R4")::is16OrBelow);
    }

}
