package com.spring.NetFortify.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SimulationResult {
    private int nodesRemoved;
    private double percentageRemoved;
    private int largestConnectedComponentSize;
    private int numberOfComponents;
    private String status;

}
