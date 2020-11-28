package com.github.ness.utility;

import java.util.List;

import org.bukkit.ChatColor;

import lombok.Getter;

public final class GraphUtils {

    public static GraphResult getGraph(List<Double> values) {
        StringBuilder graph = new StringBuilder();

        double largest = 0;

        for (double value : values) {
            if (value > largest)
                largest = value;
        }

        int GRAPH_HEIGHT = 2;
        int positives = 0, negatives = 0;

        for (int i = GRAPH_HEIGHT - 1; i > 0; i -= 1) {
            StringBuilder sb = new StringBuilder();

            for (double index : values) {
                double value = GRAPH_HEIGHT * index / largest;

                if (value > i && value < i + 1) {
                    ++positives;
                    sb.append(String.format("%s+", ChatColor.GREEN));
                } else {
                    ++negatives;
                    sb.append(String.format("%s-", ChatColor.RED));
                }
            }

            graph.append(sb.toString());
        }

        return new GraphResult(graph.toString(), positives, negatives);
    }
    
}