package com.github.ness.check;

public enum ChecksPackage {

    MAIN(".impl"),
    COMBAT(".impl.combat"),
    MOVEMENT(".impl.movement"),
    FLYCHEAT(".impl.movement.fly"),
    PACKET(".impl.packet"),
    WORLD(".impl.world"),
    REQUIRED(".impl.required"),
    MISC(".impl.misc");

    /**
     * Class names of checks in the required checks package. <br>
     * Do not mutate the array
     */
    public static final String[] REQUIRED_CHECKS = {"TeleportEvent"};

    private final String prefix;

    ChecksPackage(String prefix) {
        this.prefix = prefix;
    }

    public String prefix() {
        return prefix;
    }

}
