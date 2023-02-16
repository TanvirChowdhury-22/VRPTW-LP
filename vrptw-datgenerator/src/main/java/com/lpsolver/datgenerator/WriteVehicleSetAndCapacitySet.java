package com.lpsolver.datgenerator;

import java.util.Formatter;

public class WriteVehicleSetAndCapacitySet {
    public static String VEHICLE_SET = "VEHICLE1  VEHICLE2";
    public static Integer VEHICLE_1_CAPACITY = 300;
    public static Integer VEHICLE_2_CAPACITY = 300;

    void writeCapacitySet(Formatter formatter) {
        formatter.format("\n%s\r\n", "param    CAPACITY := ");
        formatter.format("\t%s%d\r\n", "VEHICLE1 ", VEHICLE_1_CAPACITY);
        formatter.format("\t%s%d ", "VEHICLE2 ", VEHICLE_2_CAPACITY);
        formatter.format("%s\r\n\n", ";");
    }

    void writeVehicleSetForVrptwDatFile(Formatter formatter) {
        formatter.format("\n%s %s %s\r\n", "set VEHICLE :=", VEHICLE_SET, ";");
    }
}
