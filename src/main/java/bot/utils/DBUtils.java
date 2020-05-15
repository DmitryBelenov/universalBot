package bot.utils;

import bot.factory.handlers.impl.AliasMapManager;
import bot.property.BotProperties;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DBUtils {

    private static final String dataBaseUrlFormat = "jdbc:postgresql:%s?user=%s&password=%s";
    private static Logger log = Logger.getLogger(DBUtils.class);
    private Connection connection;

    public DBUtils() {
        connection = getConnection();
    }

    public void connectionClose() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("Ошибка закрытия соединения с базой данных\n" + e);
        }
    }

    private Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            String db = BotProperties.get().getProperty("ubot.db.name");
            String user = BotProperties.get().getProperty("ubot.db.user");
            String pass = BotProperties.get().getProperty("ubot.db.password");

            connection = DriverManager.getConnection(String.format(dataBaseUrlFormat, db, user, pass));
        } catch (SQLException e) {
            log.error("Ошибка подключения к базе данных: " + e);
        } catch (ClassNotFoundException cnf) {
            log.error("Ошибка драйвера базы данных: " + cnf);
        }
        return connection;
    }

    public int addUserYoFirstStage(Integer userId, String interests) {
        boolean userYoAlreadyExists = false;
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM yo WHERE user_id=" + userId);
            if (rs.next())
                userYoAlreadyExists = true;
        } catch (SQLException e) {
            log.error("Ошибка получения YO данных пользователя: " + e);
            return -1;
        }
        if (userYoAlreadyExists) {
            try {
                Statement s = connection.createStatement();
                s.executeUpdate("update yo set user_interests='" + interests + "', yo_date=current_timestamp  where user_id=" + userId);
            } catch (SQLException e) {
                log.error("Ошибка обновления YO пользователя: " + e);
                return -1;
            }
            return 1;
        } else {
            try {
                Statement s = connection.createStatement();
                s.executeUpdate("INSERT INTO yo (yo_id, user_id, user_interests, yo_date) " +
                        "VALUES ('" + UUID.randomUUID().toString() + "','" + userId + "','" + interests + "', current_timestamp)");
            } catch (SQLException e) {
                log.error("Ошибка создания YO пользователя: " + e);
                return -1;
            }
            return 0;
        }
    }

    public int addUserYoSecondStage(Integer userId, String name, String phone) {
        boolean userYoExists = false;
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM yo WHERE user_id=" + userId);
            if (rs.next())
                userYoExists = true;
        } catch (SQLException e) {
            log.error("Ошибка получения YO данных пользователя: " + e);
            return -1;
        }
        if (userYoExists) {
            try {
                Statement s = connection.createStatement();
                s.executeUpdate("update yo set user_name='" + name + "', user_phone='" + phone + "', yo_date=current_timestamp  where user_id=" + userId);
            } catch (SQLException e) {
                log.error("Ошибка обновления YO пользователя: " + e);
                return -1;
            }
            return 1;
        } else {
            return -1;
        }
    }

    public int addUserYoThirdStage(Integer userId, String lat, String lon) {
        boolean userYoExists = false;
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM yo WHERE user_id=" + userId);
            if (rs.next())
                userYoExists = true;
        } catch (SQLException e) {
            log.error("Ошибка получения YO данных пользователя: " + e);
            return -1;
        }
        if (userYoExists) {
            try {
                Statement s = connection.createStatement();
                s.executeUpdate("update yo set user_latitude='" + lat + "', user_longitude='" + lon + "', yo_date=current_timestamp  where user_id=" + userId);
            } catch (SQLException e) {
                log.error("Ошибка обновления YO пользователя: " + e);
                return -1;
            }
            return 1;
        } else {
            return -1;
        }
    }

    public String nearbyYoCheck(Integer userId, String lat, String lon) {
        String result = "You say YO!\nNow I do not see anyone around with similar interests\nTry /yo command without arguments later";

        String myInterests = null;
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT user_interests FROM yo WHERE user_id=" + userId);
            if (rs.next())
                myInterests = rs.getString("user_interests");
        } catch (SQLException e) {
            log.error("Ошибка получения YO интересов пользователя: " + e);
        }

        result = nearbyCalculate(result, userId, lat, lon, myInterests);
        return result;
    }

    public String nearbyCheckByYoCommand(Integer userId) {
        String result = "Try to say YO! with arguments";

        String myInterests = null;
        String latitude = null;
        String longitude = null;

        boolean foundMyData = false;
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT user_interests, user_latitude, user_longitude  FROM yo WHERE user_id=" + userId
                    + " and user_interests is not null and user_latitude is not null and user_longitude is not null");
            if (rs.next()) {
                myInterests = rs.getString("user_interests");
                latitude = rs.getString("user_latitude");
                longitude = rs.getString("user_longitude");

                foundMyData = true;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения YO интересов и координат пользователя: " + e);
        }

        if (foundMyData) {
            result = "Nobody around with interest '" + myInterests + "'\nTry to say YO! again later";
            result = nearbyCalculate(result, userId, latitude, longitude, myInterests);
        }

        return result;
    }

    private String nearbyCalculate(String result, Integer userId, String lat, String lon, String myInterests) {
        String noLookAround = "You say YO!\nBut looks like I cant look around for you";
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT user_id, user_name, user_phone, user_latitude, user_longitude, user_interests FROM yo WHERE " +
                    "user_name is not null and user_phone is not null and user_latitude is not null and user_longitude is not null");

            double lat1 = Double.parseDouble(lat);
            double lon1 = Double.parseDouble(lon);

            StringBuilder sb = new StringBuilder();

            int i = 0;
            while (rs.next()) {
                int id = rs.getInt("user_id");
                if (id != userId) {
                    double lat2 = rs.getDouble("user_latitude");
                    double lon2 = rs.getDouble("user_longitude");

                    double distance = calculateDistance(lat1, lon1, lat2, lon2, 'K'); // пример расчета = 0.2741365941750706
                    if (distance < 2.0) {
                        String dis;
                        String type;
                        if (distance < 1.0) {
                            double meters = distance * 1000;
                            dis = String.format("%.1f", meters);
                            type = "m";
                        } else {
                            dis = String.format("%.2f", distance);
                            type = "km";
                        }

                        String interests = rs.getString("user_interests");

                        StringBuilder similarInterest = new StringBuilder();
                        if (myInterests != null) {
                            boolean manyInterests = false;
                            List<String> listOfMyInt = new ArrayList<>();
                            if (myInterests.contains(",")) {
                                listOfMyInt = Arrays.asList(myInterests.split(","));
                                manyInterests = true;
                            }

                            if (interests.contains(",")) {
                                String[] iList = interests.split(",");
                                int k = 0;
                                for (String il : iList) {
                                    if (manyInterests && listOfMyInt.size() > 0) {
                                        if (listOfMyInt.contains(il.trim())) {
                                            similarInterest.append(il).append(k == iList.length - 1 ? "" : ",");
                                        }
                                    } else {
                                        if (myInterests.equals(il)) {
                                            similarInterest.append(il);
                                        }
                                    }
                                    k++;
                                }
                            } else {
                                if (manyInterests && listOfMyInt.size() > 0) {
                                    if (listOfMyInt.contains(interests.trim())) {
                                        similarInterest.append(interests);
                                    }
                                } else {
                                    if (myInterests.equals(interests)) {
                                        similarInterest.append(interests);
                                    }
                                }
                            }

                            if (similarInterest.length() > 0) {
                                sb.append(rs.getString("user_name"));
                                sb.append("\n");
                                sb.append("interested in: ");
                                sb.append(similarInterest.toString());
                                sb.append("\n");
                                sb.append(dis).append(type).append(" from you").append("\n");
                                sb.append("phone: ").append(rs.getString("user_phone")).append("\n");
                                sb.append("******************************\n");

                                i++;
                            }
                        } else {
                            result = noLookAround;
                        }
                    }
                }
            }
            if (i > 0) {
                String pre = "YO!\uD83D\uDE03\nSomebody with similar interests nearby you\nTry write or call that person" + (i == 1 ? ":" : "s:") + "\n******************************\n";
                result = pre + sb.toString();
            }
        } catch (SQLException e) {
            log.error("Ошибка расчета YO расстояний: " + e);
            result = noLookAround;
        }

        return result;
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function calculate distance between two coordinates      :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    // K - kilometers M - miles N - nautical miles
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2, char unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == 'K') {
            dist = dist * 1.609344;
        } else if (unit == 'N') {
            dist = dist * 0.8684;
        }
        return (dist);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts decimal degrees to radians             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function converts radians to decimal degrees             :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    boolean checkYoDataCleaner() {
        ArrayList<Integer> idsToRemove = new ArrayList<>();
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("select user_id from yo where (yo_date + INTERVAL '1 DAY') < NOW()");
            while (rs.next()) {
                idsToRemove.add(rs.getInt("user_id"));
            }
        } catch (SQLException e) {
            log.error("Ошибка получения просроченных YO записей: " + e);
            return false;
        }

        if (idsToRemove.size() > 0) {
            String sqlQuery = "delete from yo where user_id in (%s)";
            StringBuilder ids = new StringBuilder();
            int i = 0;
            for (Integer id : idsToRemove) {
                ids.append(id).append(i == idsToRemove.size() - 1 ? "" : ",");
                AliasMapManager.yoStatesMap.remove(id);
                i++;
            }
            String q = String.format(sqlQuery, ids.toString());

            try {
                Statement s = connection.createStatement();
                s.executeUpdate(q);
            } catch (SQLException e) {
                log.error("Ошибка удаления просроченных YO записей: " + e);
                return false;
            }
        }
        return true;
    }

    boolean checkMyWorldDataCleaner() {
        ArrayList<Integer> idsToRemove = new ArrayList<>();
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("select user_id from my_world where (register_date + INTERVAL '1 HOUR') < NOW()");
            while (rs.next()) {
                idsToRemove.add(rs.getInt("user_id"));
            }
        } catch (SQLException e) {
            log.error("Ошибка получения просроченных YO записей: " + e);
            return false;
        }

        if (idsToRemove.size() > 0) {
            String sqlQuery = "delete from my_world where user_id in (%s)";
            StringBuilder ids = new StringBuilder();
            int i = 0;
            for (Integer id : idsToRemove) {
                ids.append(id).append(i == idsToRemove.size() - 1 ? "" : ",");
                AliasMapManager.myWorldStatesMap.remove(id);
                i++;
            }
            String q = String.format(sqlQuery, ids.toString());

            try {
                Statement s = connection.createStatement();
                s.executeUpdate(q);
            } catch (SQLException e) {
                log.error("Ошибка удаления просроченных записей My World: " + e);
                return false;
            }
        }
        return true;
    }

    public int checkMyWorldAvailability(Integer userId){
        int res = -2;
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM my_world where user_id="+userId);
            if (rs.next()){
                res = -1;
            }
        } catch (SQLException e) {
            log.error("Ошибка получения записей пользователя My World: " + e);
            return res;
        }

        if (res == -1) return res;

        int count = 0;
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT COUNT(*) AS total FROM my_world");
            if (rs.next()){
                count = rs.getInt("total");
            }
        } catch (SQLException e) {
            log.error("Ошибка получения количества записей My World: " + e);
            return -2;
        }

        return count <= 100 ? 1 : 0;
    }

    public int createMyWorld(Integer userId, String picId){
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("INSERT INTO my_world (register_id, user_id, user_pic_id, register_date, hide_contact) " +
                    "VALUES ('" + UUID.randomUUID().toString() + "','" + userId + "','" + picId + "', current_timestamp, false)");
        } catch (SQLException e) {
            log.error("Ошибка создания записи My World: " + e);
            return -1;
        }
        return 1;
    }

    public int addContactToMyWorld(Integer userId, String name, String phone){
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("update my_world set user_name='" + name + "', user_phone='" + phone + "', register_date=current_timestamp  where user_id=" + userId);
        } catch (SQLException e) {
            log.error("Ошибка обновления записи My World: " + e);
            return -1;
        }
        return 1;
    }

    public int addDescriptionToMyWorld(Integer userId, String description){
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("update my_world set user_description='" + description + "', register_date=current_timestamp  where user_id=" + userId);
        } catch (SQLException e) {
            log.error("Ошибка обновления записи My World: " + e);
            return -1;
        }
        return 1;
    }

    public int hideMyWorldContact(Integer userId){
        try {
            Statement s = connection.createStatement();
            s.executeUpdate("update my_world set hide_contact=true, register_date=current_timestamp  where user_id=" + userId);
        } catch (SQLException e) {
            log.error("Ошибка обновления записи My World: " + e);
            return -1;
        }
        return 1;
    }

    public List<InputMedia> getMyWorlds(){
        List<InputMedia> worlds = new ArrayList<>();

        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT user_pic_id, user_name, user_phone, user_description, hide_contact FROM my_world where " +
                    "user_name is not null and user_phone is not null and hide_contact is not null order by register_date");
            while (rs.next()){
                InputMediaPhoto photo = new InputMediaPhoto();

                String picId = rs.getString("user_pic_id");
                String userName = rs.getString("user_name");
                photo.setMedia(picId);

                String description = rs.getString("user_description");
                if (description != null){
                   userName += "\n"+description;
                }

                boolean hideContact = rs.getBoolean("hide_contact");
                if (!hideContact){
                   String phone = rs.getString("user_phone");
                   userName += "\nContact: "+phone;
                }

                photo.setCaption(userName);
                worlds.add(photo);
            }
        } catch (SQLException e) {
            log.error("Ошибка получения данных My World: " + e);
        }

        return worlds;
    }
}
