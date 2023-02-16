package com.lpsolver.datgenerator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WriteTimeAndDemandParams {

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

    // WRITE EARLIEST TIME, LAST TIME, SERVICE TIME AND DEMAND PARAMS FOR USER BASED
    // VRPTW
    void writeTimeAndDemandParamsForUserBasedVRPTW(String problem_id, List<String> areaList,
            ArrayList<String> nodeArray, ArrayList<String> nodeUsrIdArray, ArrayList<String> usrIdArray,
            String rootNode, Formatter formatter, String finalNode) {
        formatter.format("%s", "param:   EARLIEST_TIME    LAST_TIME    SERVICE_TIME  demand := ");

        String depoDemand = "0";
        String earliestDateStrDepo = findEarliestDateByUser(nodeArray.get(0), "1", problem_id);
        System.out.println("Earliest Date from Depo: " + earliestDateStrDepo);
        String lastDateStrDepo = findLastDateByUser(nodeArray.get(0), "1", problem_id);
        System.out.println("Last Date to Depo: " + lastDateStrDepo);

        String[] earliestDateArrayDepo = earliestDateStrDepo.split("-");
        String[] lastDeliveryDateArrayDepo = lastDateStrDepo.split("-");

        ArrayList<Integer> earliestTimeArray = new ArrayList<Integer>();
        ArrayList<Integer> lastTimeArray = new ArrayList<Integer>();

        ArrayList<String> earlyDateArray = new ArrayList<String>();
        ArrayList<String> earlyMonthArray = new ArrayList<String>();
        ArrayList<String> earlyYearArray = new ArrayList<String>();
        ArrayList<String> lastDateArray = new ArrayList<String>();
        ArrayList<String> lastMonthArray = new ArrayList<String>();
        ArrayList<String> lastYearArray = new ArrayList<String>();

        // CALCULATING EARLIEST TIME AND LAST TIME FOR ALL NODES
        earlyDateArray.add(0, earliestDateArrayDepo[0]);
        earlyMonthArray.add(0, earliestDateArrayDepo[1]);
        earlyYearArray.add(0, earliestDateArrayDepo[2]);

        lastDateArray.add(0, lastDeliveryDateArrayDepo[0]);
        lastMonthArray.add(0, lastDeliveryDateArrayDepo[1]);
        lastYearArray.add(0, lastDeliveryDateArrayDepo[2]);

        earliestTimeArray.add(0,
                calculateEarliestTimeAndLastTime(earlyDateArray.get(0), earlyMonthArray.get(0), earlyYearArray.get(0)));
        lastTimeArray.add(0,
                calculateEarliestTimeAndLastTime(lastDateArray.get(0), lastMonthArray.get(0), lastYearArray.get(0)));
        for (int i = 1; i <= areaList.size(); i++) {
            String earliestDateStr = findEarliestDateByUser(nodeArray.get(i), usrIdArray.get(i), problem_id);
            String lastDateStr = findLastDateByUser(nodeArray.get(i), usrIdArray.get(i), problem_id);

            String[] earliestDateArray = earliestDateStr.split("-");
            earlyDateArray.add(i, earliestDateArray[0]);
            earlyMonthArray.add(i, earliestDateArray[1]);
            earlyYearArray.add(i, earliestDateArray[2]);
            String[] lastDeliveryDateArray = lastDateStr.split("-");
            lastDateArray.add(i, lastDeliveryDateArray[0]);
            lastMonthArray.add(i, lastDeliveryDateArray[1]);
            lastYearArray.add(i, lastDeliveryDateArray[2]);

            earliestTimeArray.add(i, calculateEarliestTimeAndLastTime(earlyDateArray.get(i), earlyMonthArray.get(i),
                    earlyYearArray.get(i)));
            lastTimeArray.add(i, calculateEarliestTimeAndLastTime(lastDateArray.get(i), lastMonthArray.get(i),
                    lastYearArray.get(i)));
        }
        earliestTimeArray.add(areaList.size() + 1, earliestTimeArray.get(0));
        lastTimeArray.add(areaList.size() + 1, lastTimeArray.get(0));

        formatter.format("\r\n%s  \t", rootNode.replace(" ", ""));

        // SETTING EARLIEST TIME = 10 AND LAST TIME = 40 IF THEY ARE FOUND TO BE ZERO OR
        // NEGATIVE
        for (int i = 0; i <= areaList.size() + 1; i++) {
            earliestTimeArray.set(i, earliestTimeArray.get(i) * 24);
            lastTimeArray.set(i, lastTimeArray.get(i) * 24);

            if (earliestTimeArray.get(i) <= 0) {
                earliestTimeArray.set(i, 10);
            }

            if (lastTimeArray.get(i) <= 0) {
                lastTimeArray.set(i, 40);
            }
        }
        System.out.println("Earliest Time from Depo: " + earliestTimeArray.get(0));
        System.out.println("Last Time from Depo: " + lastTimeArray.get(0));
        formatter.format("%d \t%d \t%d \t%s ", earliestTimeArray.get(0), lastTimeArray.get(0), SERVICE_TIME,
                depoDemand);

        for (int i = 1; i <= areaList.size(); i++) {
            formatter.format("\r\n%s  ", nodeUsrIdArray.get(i).replace(" ", ""));
            String demand = findDemandByAreaAndByUserId(nodeArray.get(i), usrIdArray.get(i), problem_id);

            formatter.format("\t%d \t%d \t%d \t%s ", earliestTimeArray.get(i), lastTimeArray.get(i), SERVICE_TIME,
                    demand);
        }
        formatter.format("\r\n%s  \t", finalNode);
        formatter.format("%d \t%d \t%d \t%s ", earliestTimeArray.get(areaList.size() + 1),
                lastTimeArray.get(areaList.size() + 1), SERVICE_TIME, depoDemand);

        formatter.format("%s\r\n", ";");
    }

    // WRITE EARLIEST TIME, LAST TIME, SERVICE TIME AND DEMAND PARAMS FOR LOCATION
    // BASED VRPTW
    void writeTimeAndDemandParamsForLocationBasedVRPTW(String problem_id, List<String> areaList,
            ArrayList<String> nodeArray, String rootNode, Formatter formatter, String finalNode) {
        formatter.format("%s", "param:   EARLIEST_TIME    LAST_TIME    SERVICE_TIME  demand := ");
        String depoDemand = "0";
        String earliestDateStrDepo = findEarliestDateByLocation(nodeArray.get(0), problem_id);
        System.out.println("Earliest Date from Depo: " + earliestDateStrDepo);
        String lastDateStrDepo = findLastDateByLocation(nodeArray.get(0), problem_id);
        System.out.println("Last Date to Depo: " + lastDateStrDepo);

        String[] earliestDateArrayDepo = earliestDateStrDepo.split("-");
        String[] lastDeliveryDateArrayDepo = lastDateStrDepo.split("-");

        ArrayList<Integer> earliestTimeArray = new ArrayList<Integer>();
        ArrayList<Integer> lastTimeArray = new ArrayList<Integer>();

        ArrayList<String> earlyDateArray = new ArrayList<String>();
        ArrayList<String> earlyMonthArray = new ArrayList<String>();
        ArrayList<String> earlyYearArray = new ArrayList<String>();
        ArrayList<String> lastDateArray = new ArrayList<String>();
        ArrayList<String> lastMonthArray = new ArrayList<String>();
        ArrayList<String> lastYearArray = new ArrayList<String>();

        // CALCULATING EARLIEST TIME AND LAST TIME FOR ALL NODES
        earlyDateArray.add(0, earliestDateArrayDepo[0]);
        earlyMonthArray.add(0, earliestDateArrayDepo[1]);
        earlyYearArray.add(0, earliestDateArrayDepo[2]);

        lastDateArray.add(0, lastDeliveryDateArrayDepo[0]);
        lastMonthArray.add(0, lastDeliveryDateArrayDepo[1]);
        lastYearArray.add(0, lastDeliveryDateArrayDepo[2]);
        for (int i = 0; i <= areaList.size(); i++) {
            String earliestDateStr = findEarliestDateByLocation(nodeArray.get(i), problem_id);
            String lastDateStr = findLastDateByLocation(nodeArray.get(i), problem_id);
            String[] earliestDateArray = earliestDateStr.split("-");
            earlyDateArray.add(i, earliestDateArray[0]);
            earlyMonthArray.add(i, earliestDateArray[1]);
            earlyYearArray.add(i, earliestDateArray[2]);
            String[] lastDeliveryDateArray = lastDateStr.split("-");
            lastDateArray.add(i, lastDeliveryDateArray[0]);
            lastMonthArray.add(i, lastDeliveryDateArray[1]);
            lastYearArray.add(i, lastDeliveryDateArray[2]);
            earliestTimeArray.add(i, calculateEarliestTimeAndLastTime(earlyDateArray.get(i), earlyMonthArray.get(i),
                    earlyYearArray.get(i)));
            lastTimeArray.add(i, calculateEarliestTimeAndLastTime(lastDateArray.get(i), lastMonthArray.get(i),
                    lastYearArray.get(i)));
        }
        earliestTimeArray.add(areaList.size() + 1, earliestTimeArray.get(0));
        lastTimeArray.add(areaList.size() + 1, lastTimeArray.get(0));

        // SETTING EARLIEST TIME = 10 AND LAST TIME = 40 IF THEY ARE FOUND TO BE ZERO OR
        // NEGATIVE
        for (int i = 0; i <= areaList.size() + 1; i++) {
            earliestTimeArray.set(i, earliestTimeArray.get(i) * 24);
            lastTimeArray.set(i, lastTimeArray.get(i) * 24);
            if (earliestTimeArray.get(i) <= 0) {
                earliestTimeArray.set(i, 10);
            }
            if (lastTimeArray.get(i) <= 0) {
                lastTimeArray.set(i, 40);
            }
        }

        formatter.format("\r\n%s  \t", rootNode);
        formatter.format("%d \t%d \t%d \t%s ", earliestTimeArray.get(0), lastTimeArray.get(0), SERVICE_TIME,
                depoDemand);
        for (int i = 1; i <= areaList.size(); i++) {
            formatter.format("\r\n%s  ", nodeArray.get(i).replace(" ", ""));
            String demand = findDemandByArea(nodeArray.get(i), problem_id);
            formatter.format("\t%d \t%d \t%d \t%s ", earliestTimeArray.get(i), lastTimeArray.get(i), SERVICE_TIME,
                    demand);
        }
        formatter.format("\r\n%s  \t", finalNode);
        formatter.format("%d \t%d \t%d \t%s ", earliestTimeArray.get(areaList.size() + 1),
                lastTimeArray.get(areaList.size() + 1), SERVICE_TIME, depoDemand);
        formatter.format("%s\r\n", ";");
    }

    public String findDemandByArea(String area, String problem_id) {
        String demandStr = "";
        String SQL = "SELECT SUM(product_weight) as sumproduct " + "FROM problem_details "
                + "WHERE area =? AND problem_id =?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, area);
            pstmt.setString(2, problem_id);
            ResultSet rs = pstmt.executeQuery();
            demandStr = getDemand(rs);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return demandStr;
    }

    private String getDemand(ResultSet rs) throws SQLException {
        String demandStr = "";
        while (rs.next()) {
            demandStr = rs.getString("sumproduct");
        }
        return demandStr;
    }

    public String findEarliestDateByLocation(String area, String problem_id) {
        String earliestDate = "";
        String SQL = "SELECT earliest_delivery_date " + "FROM problem_details " + "WHERE area =? AND problem_id =?";

        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, area);
            pstmt.setString(2, problem_id);
            ResultSet rs = pstmt.executeQuery();
            earliestDate = getEarliestDate(rs);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return earliestDate;
    }

    public String findEarliestDateByUser(String area, String userId, String problem_id) {
        String earliestDate = "";
        String SQL = "SELECT earliest_delivery_date " + "FROM problem_details "
                + "WHERE area =? AND user_id =? AND problem_id =?";
        Long user_id = Long.parseLong(userId);
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, area);
            pstmt.setLong(2, user_id);
            pstmt.setString(3, problem_id);
            ResultSet rs = pstmt.executeQuery();
            earliestDate = getEarliestDate(rs);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return earliestDate;
    }

    private String getEarliestDate(ResultSet rs) throws SQLException {
        String earliestDate = "";
        while (rs.next()) {
            earliestDate = rs.getString("earliest_delivery_date");
        }
        return earliestDate;
    }

    public String findLastDateByLocation(String area, String problem_id) {
        String lastDate = "";
        String SQL = "SELECT last_delivery_date " + "FROM problem_details " + "WHERE area =? AND problem_id =?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, area);
            pstmt.setString(2, problem_id);
            ResultSet rs = pstmt.executeQuery();
            lastDate = getLastDate(rs);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return lastDate;
    }

    public String findLastDateByUser(String area, String userId, String problem_id) {
        String lastDate = "";
        String SQL = "SELECT last_delivery_date " + "FROM problem_details "
                + "WHERE area =? AND user_id =? AND problem_id =?";
        Long user_id = Long.parseLong(userId);
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, area);
            pstmt.setLong(2, user_id);
            pstmt.setString(3, problem_id);
            ResultSet rs = pstmt.executeQuery();
            lastDate = getLastDate(rs);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return lastDate;
    }

    private String getLastDate(ResultSet rs) throws SQLException {
        String lastDate = "";
        while (rs.next()) {
            lastDate = rs.getString("last_delivery_date");
        }
        return lastDate;
    }

    public String findDemandByAreaAndByUserId(String area, String usrId, String problem_id) {
        String demandStr = "";
        String SQL = "SELECT product_weight " + "FROM problem_details "
                + "WHERE area =? AND user_id =? AND problem_id =?";
        Long user_id = Long.parseLong(usrId);
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(SQL)) {
            pstmt.setString(1, area);
            pstmt.setLong(2, user_id);
            pstmt.setString(3, problem_id);
            ResultSet rs = pstmt.executeQuery();
            demandStr = getDemandByUser(rs);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return demandStr;
    }

    private String getDemandByUser(ResultSet rs) throws SQLException {
        String demandStr = "";
        while (rs.next()) {
            demandStr = rs.getString("product_weight");
        }
        return demandStr;
    }

    private int calculateEarliestTimeAndLastTime(String date, String month, String year) {
        year = "20" + year;

        int dateInt = Integer.parseInt(date);
        int monthInt = Integer.parseInt(month);
        int yearInt = Integer.parseInt(year);

        LocalDate start_date = LocalDate.now();
        LocalDate end_date = LocalDate.of(yearInt, monthInt, dateInt);

        Period diff = Period.between(start_date, end_date);

        return diff.getDays();
    }
}
