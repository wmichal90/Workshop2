package pl.coderslab;
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class DbUtil {
    private static  String DB_NAME = "workshop2";
    private static String DB_URL  = String.format("jdbc:mysql://localhost:3306/%s?useSSL=false&characterEncoding=utf8&serverTimezone=UTC", DB_NAME);
    //    private static String DB_URL = "jdbc:mysql://localhost:3306/products_ex?useSSL=false&characterEncoding=utf8&serverTimezone=UTC";
    private static String DB_USER = "root";
    private static String DB_PASS = "coderslab";

    private static final String DELETE_QUERY = "DELETE FROM tableName where id = ?";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public static void insert(Connection conn, String query, String... params){
        try (PreparedStatement statement = conn.prepareStatement(query)){
            for (int i = 0; i < params.length; i++){
                statement.setString(i + 1, params[i]);
            }
            statement.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static void printData(Connection conn, String query, String... columnNames) throws SQLException{
        try (PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery();){
            while (resultSet.next()){
                for (String columnName: columnNames){
                    System.out.print(resultSet.getString(columnName) + " |\t");
//
                }
                System.out.println();
            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void remove(Connection conn, String tableName, int id){
        try (PreparedStatement statement =
                     conn.prepareStatement(DELETE_QUERY.replace("tableName", tableName));){
            statement.setInt(1,id);
            statement.executeUpdate();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static double avgValue(Connection conn, String avgQuery){
        double avgValue = -1.0;
        try (Statement statement =
                     conn.createStatement();){
            ResultSet rs = statement.executeQuery(avgQuery);
            while (rs.next()){
                avgValue =  rs.getDouble(1);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return avgValue;
    }

    public static double countAvg(Connection conn, String columnName, String tableName){
        try(PreparedStatement statement = conn.prepareStatement(String.format("SELECT AVG(%s) FROM %s", columnName, tableName))){
            statement.setString(1, columnName);
            statement.setString(2, tableName);
            ResultSet rs = statement.executeQuery();
            rs.next();
            return rs.getDouble("avg");
        } catch (SQLException e){
            throw new RuntimeException();
        }

    }


}

// od wykladowcy:
//package pl.coderslab.sql;
//
//        import java.sql.Connection;
//        import java.sql.DriverManager;
//        import java.sql.PreparedStatement;
//        import java.sql.SQLException;
//        import java.sql.ResultSet;
//
//public class DbUtil {
//    private static final String DB_NAME = "cinemas_ex";
//    private static final String DB_URL= String.format("jdbc:mysql://localhost:3306/%s?useSSL=false&characterEncoding=utf8&serverTimezone=UTC", DB_NAME);
//    private static final String DB_USER= "root";
//    private static final String DB_PASS= "example";
//    private static final String DELETE_QUERY = "DELETE FROM tableName where id = ?";
//
//
//    public static Connection connect() throws SQLException {
//        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
//    }
//
//    public static void insert(Connection conn, String query, String... params) {
//        try (PreparedStatement statement = conn.prepareStatement(query)) {
//            for (int i = 0; i < params.length; i++) {
//                statement.setString(i + 1, params[i]);
//            }
//            statement.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void printData(Connection conn, String query, String... columnNames) {
//        try (PreparedStatement statement = conn.prepareStatement(query);
//             ResultSet resultSet = statement.executeQuery();) {
//            while (resultSet.next()) {
//                for (String columnName : columnNames) {
//                    System.out.print(resultSet.getString(columnName) + " ");
//                }
//                System.out.println();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void remove(Connection conn, String tableName, int id) {
//        try (PreparedStatement statement =
//                     conn.prepareStatement(DELETE_QUERY.replace("tableName", tableName));) {
//            statement.setInt(1, id);
//            statement.executeUpdate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
