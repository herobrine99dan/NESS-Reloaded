package com.github.ness.api;

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
        this.check = check;
        this.details = details;
        this.violationCount = violationCount;
    }
    
    public void validateValues() {
        if(check == null || details == null) {
            throw new NullPointerException("String check or String detail can't be null!");
        }
    }

}
