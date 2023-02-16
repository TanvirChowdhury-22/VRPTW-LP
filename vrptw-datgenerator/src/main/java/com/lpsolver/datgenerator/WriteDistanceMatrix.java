package com.lpsolver.datgenerator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WriteDistanceMatrix {

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

    // CALCULATE AND WRITE DISTANCE MATRIX FROM LATITUDE AND LONGITUDE FOR USER
    // BASED VRPTW
    void writeDistanceMatrixForUserBasedVRPTW(String problem_id, List<String> areaList, ArrayList<String> nodeArray,
            ArrayList<String> nodeUsrIdArray, ArrayList<String> usrIdArray, String rootNode, int noOfAreas,
            Formatter formatter, String finalNode) {

        formatter.format("\n%s\r\n\t", "param  DISTANCE:");
        formatter.format("%s ", rootNode.replace(" ", ""));
        for (int i = 1; i < noOfAreas + 1; i++) {
            formatter.format("%s ", nodeUsrIdArray.get(i).replace(" ", ""));
        }
        formatter.format("%s ", finalNode.replace(" ", ""));
        formatter.format("%s", ":=");

        ArrayList<String> nodeArrayWithDepo = new ArrayList<String>();
        nodeArrayWithDepo.add(0, rootNode);
        for (int i = 1; i <= areaList.size(); i++) {
            nodeArrayWithDepo.add(i, nodeUsrIdArray.get(i));
        }
        nodeArrayWithDepo.add(areaList.size() + 1, finalNode);

        // CALCULATING DISTANCE FROM LATITUDE AND LONGITUDE
        ArrayList<String> latitudeStr = new ArrayList<String>();
        ArrayList<String> longitudeStr = new ArrayList<String>();
        String latLonDepoStr = findLatLonByAreaAndUsrId(nodeArray.get(0), "1", problem_id);
        String[] latLonArrayDepo = latLonDepoStr.split(";", 2);

        latitudeStr.add(0, latLonArrayDepo[0]);
        longitudeStr.add(0, latLonArrayDepo[1]);
        for (int i = 1; i <= areaList.size(); i++) {
            String latLonStr = findLatLonByAreaAndUsrId(nodeArray.get(i), usrIdArray.get(i), problem_id);
            String[] latLonArray = latLonStr.split(";", 2);
            latitudeStr.add(i, latLonArray[0]);
            longitudeStr.add(i, latLonArray[1]);
            System.out.println("latitude: " + latitudeStr.get(i) + "longitude: " + longitudeStr.get(i));
        }
        latitudeStr.add(areaList.size() + 1, latLonArrayDepo[0]);
        longitudeStr.add(areaList.size() + 1, latLonArrayDepo[1]);

        double[][] dis = new double[550][550];
        for (int i = 0; i <= areaList.size() + 1; i++) {
            for (int j = 0; j <= areaList.size() + 1; j++) {
                dis[i][j] = distance(latitudeStr.get(i), longitudeStr.get(i), latitudeStr.get(j), longitudeStr.get(j));
                if (dis[i][j] == 0) {
                    dis[i][j] = 9999999;
                }
                if (j == 0 && i != j) {
                    dis[i][j] = 9999999;
                }
                if (i == 0 && j == areaList.size() + 1) {
                    dis[i][j] = 3;
                }
                if (j == 0 && i == areaList.size() + 1) {
                    dis[i][j] = 3;
                }
                if (i == areaList.size() + 1 && j != 0) {
                    dis[i][j] = 99999999;
                }
                if (i == j) {
                    dis[i][j] = 99999999;
                }
                System.out.println("distance" + "[" + i + j + "]" + ":" + dis[i][j]);
            }
        }

        for (int i = 0; i <= areaList.size() + 1; i++) {
            formatter.format("\r\n%s ", nodeArrayWithDepo.get(i).replace(" ", ""));
            for (int j = 0; j <= areaList.size() + 1; j++) {
                formatter.format("%.4f ", dis[i][j]);
            }
        }
        formatter.format("%s\r\n", ";");
    }

    // CALCULATE AND WRITE DISTANCE MATRIX FROM LATITUDE AND LONGITUDE FOR LOCATION
    // BASED VRPTW
    void writeDistanceMatrixForLocationBasedVRPTW(String problem_id, List<String> areaList, ArrayList<String> nodeArray,
            String rootNode, int noOfAreas, Formatter formatter, String finalNode) {
        formatter.format("\n%s\r\n\t", "param  DISTANCE:");
        formatter.format("%s ", rootNode);
        for (int i = 1; i < noOfAreas + 1; i++) {
            formatter.format("%s ", nodeArray.get(i).replace(" ", ""));
        }
        formatter.format("%s ", finalNode);
        formatter.format("%s", ":=");

        ArrayList<String> nodeArrayWithDepo = new ArrayList<String>();
        nodeArrayWithDepo.add(0, rootNode);
        for (int i = 1; i <= areaList.size(); i++) {
            nodeArrayWithDepo.add(i, nodeArray.get(i));
        }
        nodeArrayWithDepo.add(areaList.size() + 1, finalNode);

        // CALCULATING DISTANCE FROM LATITUDE AND LONGITUDE
        ArrayList<String> latitudeStr = new ArrayList<String>();
        ArrayList<String> longitudeStr = new ArrayList<String>();
        String latLonDepoStr = findLatLonByArea(nodeArray.get(0), problem_id);
        String[] latLonArrayDepo = latLonDepoStr.split(";", 2);

        latitudeStr.add(0, latLonArrayDepo[0]);
        longitudeStr.add(0, latLonArrayDepo[1]);
        for (int i = 1; i <= areaList.size(); i++) {
            String latLonStr = findLatLonByArea(nodeArray.get(i), problem_id);
            String[] latLonArray = latLonStr.split(";", 2);
            latitudeStr.add(i, latLonArray[0]);
            longitudeStr.add(i, latLonArray[1]);
            System.out.println("latitude: " + latitudeStr.get(i) + "longitude: " + longitudeStr.get(i));
        }
        latitudeStr.add(areaList.size() + 1, latLonArrayDepo[0]);
        longitudeStr.add(areaList.size() + 1, latLonArrayDepo[1]);

        double[][] dis = new double[550][550];
        for (int i = 0; i <= areaList.size() + 1; i++) {
            for (int j = 0; j <= areaList.size() + 1; j++) {
                dis[i][j] = distance(latitudeStr.get(i), longitudeStr.get(i), latitudeStr.get(j), longitudeStr.get(i));
                if (dis[i][j] == 0) {
                    dis[i][j] = 9999999;
                }
                if (j == 0 && i != j) {
                    dis[i][j] = 9999999;
                }
                if (i == 0 && j == areaList.size() + 1) {
                    dis[i][j] = 3;
                }
                if (j == 0 && i == areaList.size() + 1) {
                    dis[i][j] = 3;
                }
                if (i == areaList.size() + 1 && j != 0) {
                    dis[i][j] = 99999999;
                }
                if (i == j) {
                    dis[i][j] = 99999999;
                }
                System.out.println("distance" + "[" + i + j + "]" + ":" + dis[i][j]);
            }
        }

        for (int i = 0; i <= areaList.size() + 1; i++) {
            formatter.format("\r\n%s ", nodeArrayWithDepo.get(i).replace(" ", ""));
            for (int j = 0; j <= areaList.size() + 1; j++) {
                formatter.format("%.4f ", dis[i][j]);
            }
        }
        formatter.format("%s\r\n", ";");
    }

    public String findLatLonByAreaAndUsrId(String area, String usrId, String problem_id) {
        String SQL = "SELECT latitude, longitude " + "FROM problem_details "
                + "WHERE area =? AND user_id =? AND problem_id =?";
        String latLonStr = "";
        Long user_id = Long.parseLong(usrId);
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, area);
            pstmt.setLong(2, user_id);
            pstmt.setString(3, problem_id);
            ResultSet rs = pstmt.executeQuery();
            latLonStr = getLatLon(rs);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return latLonStr;
    }

    private double distance(String latitudeStr1, String longitudeStr1, String latitudeStr2, String longitudeStr2) {
        double latitude1 = Double.parseDouble(latitudeStr1);
        double longitude1 = Double.parseDouble(longitudeStr1);
        double latitude2 = Double.parseDouble(latitudeStr2);
        double longitude2 = Double.parseDouble(longitudeStr2);
        double theta = longitude1 - longitude2;
        double dist = Math.sin(deg2rad(latitude1)) * Math.sin(deg2rad(latitude2))
                + Math.cos(deg2rad(latitude1)) * Math.cos(deg2rad(latitude2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        return (dist);
    }

    public String findLatLonByArea(String area, String problem_id) {
        String SQL = "SELECT DISTINCT latitude, longitude AS latitude, longitude " + "FROM problem_details "
                + "WHERE area =? AND problem_id =?";
        String latLonStr = "";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, area);
            pstmt.setString(2, problem_id);
            ResultSet rs = pstmt.executeQuery();
            latLonStr = getLatLon(rs);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return latLonStr;
    }

    private String getLatLon(ResultSet rs) throws SQLException {
        String latLonStr = "";
        while (rs.next()) {
            String latitudeStr = rs.getString("Latitude");
            String longitudeStr = rs.getString("Longitude");
            latLonStr = latitudeStr + ";" + longitudeStr;
        }
        return latLonStr;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
