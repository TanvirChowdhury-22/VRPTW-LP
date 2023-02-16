package com.lpsolver.datgenerator;

import java.util.ArrayList;
import java.util.Formatter;

public class WriteDeliveryNodes {
    // WRITE ALL THE DELIVERY NODES FOR USER BASED VRPTW FROM RECEIVED DATA
    void writeDeliveryNodesForUserBasedVRPTW(ArrayList<String> nodeUsrIdArray, String rootNode, int noOfAreas,
            Formatter formatter, String finalNode) {
        formatter.format("%s", "set DELIVERY_NODES := ");
        formatter.format("%s ", rootNode.replace(" ", ""));

        for (int i = 1; i < noOfAreas + 1; i++) {
            formatter.format("%s ", nodeUsrIdArray.get(i).replace(" ", ""));
        }

        formatter.format("%s ", finalNode.replace(" ", ""));
        formatter.format("%s\r\n", ";");
    }

    // WRITE ALL THE DELIVERY NODES FOR USER BASED VRPTW FROM RECEIVED DATA
    void writeDeliveryNodesForLocationBasedVRPTW(ArrayList<String> nodeArray, String rootNode, int noOfAreas,
            Formatter formatter, String finalNode) {
        formatter.format("%s", "set DELIVERY_NODES := ");
        formatter.format("%s ", rootNode);
        for (int i = 1; i < noOfAreas + 1; i++) {
            formatter.format("%s ", nodeArray.get(i).replace(" ", ""));
        }
        formatter.format("%s ", finalNode);
        formatter.format("%s\r\n", ";");
    }

}
