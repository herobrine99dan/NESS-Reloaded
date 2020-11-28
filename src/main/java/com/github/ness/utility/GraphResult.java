package com.github.ness.utility;

import lombok.Getter;

public class GraphResult {
    @Getter
    private final String graph;
    @Getter
    private final int positives, negatives;
    
    public GraphResult(String graph, int positives, int negatives) {
        this.graph = graph;
        this.positives = positives;
        this.negatives = negatives;
    }
}
