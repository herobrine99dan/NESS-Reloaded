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

    public Violation(String check, String details) {
        this.check = check;
        this.details = details;
    }
    
    public void validateValues() {
        if(check == null || details == null) {
            throw new NullPointerException("String check or String detail can't be null!");
        }
    }

}
