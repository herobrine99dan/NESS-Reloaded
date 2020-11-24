package com.github.ness.api;

import lombok.Getter;

/**
 * Old violation object
 * 
 * @author A248
 *
 */
public class Violation {

    @Getter
    private final String check;
    @Getter
    private final String details;

    public Violation(String check, String details) {
        this.check = check;
        this.details = details;
    }

}
