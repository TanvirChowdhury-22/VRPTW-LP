package com.lpsolver.datgenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomerApplication {
    public static String FOLDER_DIR = "test/examples/Problems/";
    public static String VEHICLE_SET = "VEHICLE1  VEHICLE2";

    public static Integer VEHICLE_1_CAPACITY = 300;
    public static Integer VEHICLE_2_CAPACITY = 300;
    public static Integer SERVICE_TIME = 1;

    public static boolean SOLVE_FOR_LOCATION = true;

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

    public static void main(String[] args) {
        // SCHEDULER
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                System.out.println("Scheduled task on " + new Date());
                List<String> problemIdList = new ArrayList<String>();
                problemIdList = findProblemIdByIsRouteProcessed();
                if (!problemIdList.isEmpty()) {
                    for (String problem_id : problemIdList) {
                        CustomerApplication conn = new CustomerApplication();
                        conn.connect();
                        System.out.println(conn.countNoOfProblems());
                        conn.findDepoNodebyProblemId(problem_id);
                    }
                } else {
                    CustomerApplication conn = new CustomerApplication();
                    conn.connect();
                }
            }
        };

        Timer timer = new Timer("Timer");
        long delay = 1000L;
        long period = 1000L * 5L;
        timer.scheduleAtFixedRate(repeatedTask, delay, period);
    }

    // GENERATING INITIAL DATA FOR USER BASED VRPTW
    private void generateAndWriteDatFileForUserBasedVRPTW(ResultSet rs, String problem_id, String depoNode)
            throws SQLException {
        List<String> areaList = new ArrayList<String>();
        List<String> areaUsrIdList = new ArrayList<String>();
        List<String> usrIdList = new ArrayList<String>();
        while (rs.next()) {
            System.out.println(rs.getString("area"));
            String area = rs.getString("area");
            Long userId = rs.getLong("user_id");
            String usrIdStr = Long.toString(userId);
            String areaUsrIdStr = area + usrIdStr;

            areaList.add(area);
            areaUsrIdList.add(areaUsrIdStr);
            usrIdList.add(usrIdStr);
        }

        UsrBasedVRPTW usrBasedVRPTW = new UsrBasedVRPTW();
        usrBasedVRPTW.writeUserBasedVRPTW(problem_id, depoNode, areaList, areaUsrIdList, usrIdList);
    }

    // GENERATING INITIAL DATA FOR LOCATION BASED VRPTW
    private void generateAndWriteDatFileForLocationBasedVRPTW(ResultSet rs, String problem_id, String depoNode)
            throws SQLException {
        List<String> areaList = new ArrayList<String>();
        while (rs.next()) {
            System.out.println(rs.getString("distinct_area"));
            String area = rs.getString("distinct_area");
            areaList.add(area);
        }
        System.out.println(areaList);

        LocBasedVRPTW locBasedVRPTW = new LocBasedVRPTW();
        locBasedVRPTW.writeLocationBasedVRPTW(problem_id, depoNode, areaList);
    }

    public String findDepoNodebyProblemId(String problem_id) {
        String SQL = "SELECT area, problem_details_id FROM problem_details WHERE problem_id =? ORDER BY problem_details_id LIMIT 1";
        String depoNode = "";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, problem_id);
            ResultSet rs = pstmt.executeQuery();
            depoNode = getDepoNode(rs);
            if (SOLVE_FOR_LOCATION) {
                generateLocationDatFile(problem_id, depoNode);
            } else {
                // TODO
                findLocationAndUserIdForDatFile(problem_id, depoNode);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return depoNode;
    }

    private String getDepoNode(ResultSet rs) throws SQLException {
        String depoNode = "";
        String problem_details_id = "";
        while (rs.next()) {
            depoNode = rs.getString("area");

            problem_details_id = rs.getString("problem_details_id");
            depoNode = depoNode + ";" + problem_details_id;
        }
        return depoNode;
    }

    public void generateLocationDatFile(String problem_id, String depoNodeprbid) {
        String SQL = "SELECT DISTINCT area AS distinct_area " + "FROM problem_details "
                + "WHERE problem_id =? AND problem_details_id NOT IN (?)";
        String[] depoNodewithproblemdetailsidarray = depoNodeprbid.split(";", 2);
        String depoNode = depoNodewithproblemdetailsidarray[0];
        String problem_details_idStr = depoNodewithproblemdetailsidarray[1];
        int problem_details_id = Integer.parseInt(problem_details_idStr);
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, problem_id);
            pstmt.setInt(2, problem_details_id);
            ResultSet rs = pstmt.executeQuery();
            generateAndWriteDatFileForLocationBasedVRPTW(rs, problem_id, depoNode);

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void findLocationAndUserIdForDatFile(String problem_id, String depoNodeprbid) {
        String SQL = "SELECT area, user_id " + "FROM problem_details "
                + "WHERE problem_id =? AND problem_details_id NOT IN (?)";
        String[] depoNodewithproblemdetailsidarray = depoNodeprbid.split(";", 2);
        String depoNode = depoNodewithproblemdetailsidarray[0];
        String problem_details_idStr = depoNodewithproblemdetailsidarray[1];
        int problem_details_id = Integer.parseInt(problem_details_idStr);
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, problem_id);
            pstmt.setInt(2, problem_details_id);
            ResultSet rs = pstmt.executeQuery();
            generateAndWriteDatFileForUserBasedVRPTW(rs, problem_id, depoNode);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public int countNoOfProblems() {
        String SQL = "SELECT count(*) FROM problem_details";
        int count = 0;
        try (Connection conn = connect();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(SQL)) {
            rs.next();
            count = rs.getInt(1);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return count;
    }

    public static List<String> findProblemIdByIsRouteProcessed() {
        String SQL = "SELECT problem_id FROM delivery_problem WHERE is_route_processed = 1 ";
        List<String> problemIdList = new ArrayList<String>();
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            ResultSet rs = pstmt.executeQuery();
            problemIdList = getProblems(rs);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return problemIdList;
    }

    private static List<String> getProblems(ResultSet rs) throws SQLException {
        List<String> problemIdList = new ArrayList<String>();
        while (rs.next()) {
            String problem_id = rs.getString("problem_id");
            if (!problem_id.isEmpty()) {
                problemIdList.add(problem_id);
            }
        }
        return problemIdList;
    }
}
