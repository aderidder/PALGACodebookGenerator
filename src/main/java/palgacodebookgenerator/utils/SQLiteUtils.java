/*
 * Copyright 2017 NKI/AvL; VUmc 2018/2019/2020
 *
 * This file is part of PALGA Protocol Codebook Generator.
 *
 * PALGA Protocol Codebook Generator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PALGA Protocol Codebook Generator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PALGA Protocol Codebook Generator. If not, see <http://www.gnu.org/licenses/>
 *
 */

package palgacodebookgenerator.utils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL helper functions
*/
public class SQLiteUtils {
    private static final Logger logger = LogManager.getLogger(SQLiteUtils.class.getName());
    private static Connection conn=null;
    private static String database="";

    /**
     * set the database
     * @param database    database location
     */
    public static void setDatabase(String database){
        SQLiteUtils.database = database;
    }

    /**
     * open the database
     */
    public static void openDB() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:"+database);
        } catch (SQLException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
    }

    /**
     * close the database
     */
    public static void closeDB(){
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            logger.log(Level.ERROR, ex.getMessage());
        }
    }

    /**
     * perform query on the logicnet table and retrieve the names of the nets, based on the netprefix
     * @param netPrefix the prefix of the nets
     * @return list of loggicnet names
     */
    public static List<String> getLogicNetNames(String netPrefix){
        List<String> netList = new ArrayList<>();
        String sql="select name from logicnet where name like '"+netPrefix+"_%' and name not like '%_discontinued%'";
        try (Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            while (rs.next()) {
                netList.add(rs.getString("name"));
            }
        }
        catch (SQLException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        return netList;
    }

    /**
     * perform query on the logicnet table and retrieve the data of the nets, based on a string of nets
     * @param netNames    a string which contains the names of the nets in propber sql format
     * @return list with the data for the nets
     */
    public static List<String> getLogicNetData(String netNames){
        List<String> netData = new ArrayList<>();
        String sql="select data from logicnet where name in ("+netNames+")";
        try (Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            while (rs.next()) {
                netData.add(rs.getString("data"));
            }
        }
        catch (SQLException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        return netData;
    }

    /**
     * fetch the settings from the standalone table. This contains e.g. the version and the table prefix
     * @return the table prefix
     */
    public static String doTableSettingsQuery(){
        String sql = "select value from standalone where key = 'settings'";
        try (Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            return rs.getString("value");
        } catch (SQLException e) {
            logger.log(Level.ERROR, e.getMessage());
        }
        return "";
    }

}
