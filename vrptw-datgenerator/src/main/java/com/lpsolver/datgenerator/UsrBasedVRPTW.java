package com.lpsolver.datgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsrBasedVRPTW {
    public static String FOLDER_DIR = "test/examples/Problems/";
    public static String VEHICLE_SET = "VEHICLE1  VEHICLE2";

    public static Integer VEHICLE_1_CAPACITY = 300;
    public static Integer VEHICLE_2_CAPACITY = 300;
    public static Integer SERVICE_TIME = 1;

    private static final Logger LOGGER = Logger.getLogger(CustomerApplication.class.getName());

    private static Connection connection = null;

    public static Connection connect() {
        String url = "jdbc:postgresql://localhost:5432/deliverymapping";
        String user = "dynamic";
        String password = "dynamic";

        try {
            connection = DriverManager.getConnection(url, user, password);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return connection;
    }

    // WRITE USER BASED VRPTW FROM RECEIVED DATA
    void writeUserBasedVRPTW(String problem_id, String depoNode, List<String> areaList, List<String> areaUsrIdList,
            List<String> usrIdList) {
        // CREATE FILE AND FOLDER FOR VRPTW DAT FILE
        createFolderForVRPTWDatFile();
        createFileToWriteVRPTWDatFile(problem_id);

        // INITIAL DATA
        ArrayList<String> nodeArray = new ArrayList<String>();
        ArrayList<String> nodeUsrIdArray = new ArrayList<String>();
        ArrayList<String> usrIdArray = new ArrayList<String>();
        System.out.println(areaList.size());
        String rootNode = depoNode;
        String rootNodeUsrId = depoNode + "1";
        nodeArray.add(0, rootNode);
        nodeUsrIdArray.add(0, rootNodeUsrId);
        usrIdArray.add(0, "1");
        for (int i = 1; i <= areaList.size(); i++) {
            nodeArray.add(i, areaList.get(i - 1));
            nodeUsrIdArray.add(i, areaUsrIdList.get(i - 1));
            usrIdArray.add(i, usrIdList.get(i - 1));
        }

        // FORMATTING
        Formatter formatter;
        try {
            formatter = new Formatter(FOLDER_DIR + "Problem" + problem_id + ".dat");
            // ROOT NODE
            rootNode = writeRootNode(nodeArray, formatter);
            // FINAL NODE
            String finalNode = writeFinalNode(nodeArray, formatter);

            WriteDeliveryNodes writeDeliveryNodes = new WriteDeliveryNodes();
            // DELIVERY NODES
            writeDeliveryNodes.writeDeliveryNodesForUserBasedVRPTW(nodeUsrIdArray, rootNode, areaList.size(), formatter,
                    finalNode);

            WriteDistanceMatrix writeDistanceMatrix = new WriteDistanceMatrix();
            // DISTANCE MATRIX
            writeDistanceMatrix.writeDistanceMatrixForUserBasedVRPTW(problem_id, areaList, nodeArray, nodeUsrIdArray,
                    usrIdArray, rootNode, areaList.size(), formatter, finalNode);

            WriteVehicleSetAndCapacitySet writeVehicleSetAndCapacitySet = new WriteVehicleSetAndCapacitySet();
            // VEHICLES
            writeVehicleSetAndCapacitySet.writeVehicleSetForVrptwDatFile(formatter);
            // CAPACITY
            writeVehicleSetAndCapacitySet.writeCapacitySet(formatter);

            WriteTimeAndDemandParams writeTimeAndDemandParams = new WriteTimeAndDemandParams();
            // EARLIEST TIME, LAST TIME, SERVICE TIME, DEMAND
            writeTimeAndDemandParams.writeTimeAndDemandParamsForUserBasedVRPTW(problem_id, areaList, nodeArray,
                    nodeUsrIdArray, usrIdArray, rootNode, formatter, finalNode);

            System.out.println("written successfully");
            formatter.close();
            setIsRouteProcessedbyProblemId(problem_id);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // WRITE ROOT/DEPO NODE FOR BOTH LOCATION AND USER BASED VRPTW FROM RECEIVED
    // DATA
    private String writeRootNode(ArrayList<String> nodeArray, Formatter formatter) {
        String rootNode;
        formatter.format("%s ", "set ROOT_NODE :=");
        rootNode = "Depo" + nodeArray.get(0);

        formatter.format("%s ", rootNode.replace(" ", ""));
        formatter.format("%s\r\n", ";");
        return rootNode;
    }

    // WRITE FINAL NODE FOR BOTH LOCATION AND USER BASED VRPTW FROM RECEIVED DATA
    private String writeFinalNode(ArrayList<String> nodeArray, Formatter formatter) {
        formatter.format("%s ", "set FINAL_NODE :=");
        String finalNode = "EndDepo" + nodeArray.get(0);
        formatter.format("%s ", finalNode.replace(" ", ""));
        formatter.format("%s\r\n", ";");
        return finalNode;
    }

    private void createFileToWriteVRPTWDatFile(String problem_id) {
        File file = new File(FOLDER_DIR + "Problem" + problem_id + ".dat");
        if (file.exists()) {
            System.out.println("File Already Created");
        } else {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void createFolderForVRPTWDatFile() {
        File dir = new File(FOLDER_DIR);
        if (dir.exists()) {
            System.out.println("Folder Already Created");
        } else {
            dir.mkdir();
        }
    }

    public void setIsRouteProcessedbyProblemId(String problem_id) {
        String SQL = "UPDATE delivery_problem SET is_route_processed = 2 WHERE problem_id = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, problem_id);
            pstmt.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
