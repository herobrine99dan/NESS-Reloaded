package com.github.ness.api;

import java.util.Objects;

import lombok.Getter;

/**
 * Old bug Good violation object
 * 
 * @author A248
 *
 */
public class Violation {

    @Getter
    private final String check;
    @Getter
    private final String details;
    @Getter
    private final int violationCount;

    public Violation(String check, String details, int violationCount) {
        Objects.requireNonNull(check, details);
        this.check = check;
        this.details = details;
        this.violationCount = violationCount;
    }
}
