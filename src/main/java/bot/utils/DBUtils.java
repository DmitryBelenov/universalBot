package bot.utils;

import bot.Bot;
import bot.property.BotProperties;
import org.apache.log4j.Logger;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class DBUtils {

    private static Logger log = Logger.getLogger(DBUtils.class);

    private Connection connection;
    private static final String dataBaseUrlFormat = "jdbc:postgresql:%s?user=%s&password=%s";

    public DBUtils() {
        connection = getConnection();
    }

    public void connectionClose(){
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("Ошибка закрытия соединения с базой данных\n"+e);
        }
    }

    private Connection getConnection(){
        Connection connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            String db = BotProperties.get().getProperty("ubot.db.name");
            String user = BotProperties.get().getProperty("ubot.db.user");
            String pass = BotProperties.get().getProperty("ubot.db.password");

            connection = DriverManager.getConnection(String.format(dataBaseUrlFormat, db, user, pass));
        } catch (SQLException e) {
            log.error("Ошибка подключения к базе данных: " + e);
        } catch (ClassNotFoundException cnf){
            log.error("Ошибка драйвера базы данных: " + cnf);
        }
        return connection;
    }

    public int addUserYoFirstStage(Integer userId, String interests){
        boolean userYoAlreadyExists = false;
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM yo WHERE user_id="+userId);
            if (rs.next())
                userYoAlreadyExists = true;
        } catch (SQLException e) {
            log.error("Ошибка получения YO данных пользователя: " + e);
            return -1;
        }
        if (userYoAlreadyExists) {
            try {
                Statement s = connection.createStatement();
                s.executeUpdate("update yo set user_interests='"+interests+"', yo_date=current_timestamp  where user_id="+userId);
            } catch (SQLException e) {
                log.error("Ошибка обновления YO пользователя: " + e);
                return -1;
            }
            return 1;
        } else {
            try {
                Statement s = connection.createStatement();
                s.executeUpdate("INSERT INTO yo (yo_id, user_id, user_interests, yo_date) " +
                        "VALUES ('"+UUID.randomUUID().toString()+"','"+userId+"','"+interests+"', current_timestamp)");
            } catch (SQLException e) {
                log.error("Ошибка создания YO пользователя: " + e);
                return -1;
            }
            return 0;
        }
    }

    public int addUserYoSecondStage(Integer userId, String name, String phone){
        boolean userYoExists = false;
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM yo WHERE user_id="+userId);
            if (rs.next())
                userYoExists = true;
        } catch (SQLException e) {
            log.error("Ошибка получения YO данных пользователя: " + e);
            return -1;
        }
        if (userYoExists) {
            try {
                Statement s = connection.createStatement();
                s.executeUpdate("update yo set user_name='"+name+"', user_phone='"+phone+"', yo_date=current_timestamp  where user_id="+userId);
            } catch (SQLException e) {
                log.error("Ошибка обновления YO пользователя: " + e);
                return -1;
            }
            return 1;
        } else {
            return -1;
        }
    }

    public int addUserYoThirdStage(Integer userId, String lat, String lon){
        boolean userYoExists = false;
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM yo WHERE user_id="+userId);
            if (rs.next())
                userYoExists = true;
        } catch (SQLException e) {
            log.error("Ошибка получения YO данных пользователя: " + e);
            return -1;
        }
        if (userYoExists) {
            try {
                Statement s = connection.createStatement();
                s.executeUpdate("update yo set user_latitude='"+lat+"', user_longitude='"+lon+"', yo_date=current_timestamp  where user_id="+userId);
            } catch (SQLException e) {
                log.error("Ошибка обновления YO пользователя: " + e);
                return -1;
            }
            return 1;
        } else {
            return -1;
        }
    }

    public String nearbyYoCheck(Integer userId, String lat, String lon){
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT user_id, user_name, user_phone, user_latitude, user_longitude, user_interests * FROM yo");

            while (rs.next()){
                Integer id = rs.getInt(1);
                String name = rs.getString(2);
                String phone = rs.getString(3);
                String latitude = rs.getString(4);
                String longitude = rs.getString(5);
                String interests = rs.getString(6);


                // пример расчета = 0.2741365941750706
            }
        } catch (SQLException e) {
            log.error("Ошибка получения YO данных пользователя: " + e);
            return "You say YO!\nBut looks like I cant look around for you";
        }
        return "";
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::  This function calculate distance between two coordinates      :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
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











//
//    public boolean isExists(String data, String var){
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT * FROM organizations WHERE org_"+var+"='"+data.trim()+"'");
//            if (!rs.next())
//                return false;
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения данных организации: " + e);
//        }
//        return true;
//    }
//
//    public boolean isExistsUsers(String data, String var){
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT * FROM org_users WHERE "+var+"='"+data.trim()+"'");
//            if (!rs.next())
//                return false;
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения данных пользователя организации: " + e);
//        }
//        return true;
//    }
//
//    public String getOrgName(String login){
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT org_name FROM organizations WHERE org_login='"+login+"'");
//            if (rs.next()){
//                return rs.getString(1);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения имени организации: " + e);
//        }
//        return "unknown";
//    }
//
//    public String getOrgNameById(String id){
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT org_name FROM organizations WHERE confirmation_uuid='"+id+"'");
//            if (rs.next()){
//                return rs.getString(1);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения имени организации: " + e);
//        }
//        return "Unknown org";
//    }
//
//    public String getLastTaskPrefix(String org_uuid){
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT prefix FROM task_prefix WHERE org_uuid='"+org_uuid+"' ORDER by create_date desc");
//            if (rs.next()){
//                return rs.getString(1);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения последнего префикса задачи: " + e);
//        }
//        return "HV0";
//    }
//
//    public String getLastAppealPrefix(String org_uuid){
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT prefix FROM appeal_prefix WHERE org_uuid='"+org_uuid+"' ORDER by create_date desc");
//            if (rs.next()){
//                return rs.getString(1);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения последнего префикса заявки: " + e);
//        }
//        return "AP0";
//    }
//
//    public boolean addLastTaskPrefix(String org_uuid, String taskPrefix){
//        try {
//            Statement s = connection.createStatement();
//            s.executeUpdate("INSERT INTO task_prefix (org_uuid, prefix, create_date) VALUES ('"+org_uuid+"','"+taskPrefix+"', current_timestamp)");
//        } catch (SQLException e) {
//            System.out.println("Ошибка внесения последнего префикса задачи: " + e);
//            return false;
//        }
//        return true;
//    }
//
//    public boolean addLastAppealPrefix(String org_uuid, String appealPrefix){
//        try {
//            Statement s = connection.createStatement();
//            s.executeUpdate("INSERT INTO appeal_prefix (org_uuid, prefix, create_date) VALUES ('"+org_uuid+"','"+appealPrefix+"', current_timestamp)");
//        } catch (SQLException e) {
//            System.out.println("Ошибка внесения последнего префикса заявки: " + e);
//            return false;
//        }
//        return true;
//    }
//
//    public String getTaskPrefixById(String taskId){
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT head_line FROM task WHERE task_id='"+taskId+"'");
//            if (rs.next()){
//                String headLine = rs.getString(1);
//                return headLine.split(":")[0];
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения префикса задачи: " + e);
//        }
//        return null;
//    }
//
//    public void addTaskComment(String... comment) {
//        String id = UUID.randomUUID().toString();
//        try {
//            Statement s = connection.createStatement();
//            s.executeUpdate("INSERT INTO task_comments (org_id, owner_id, task_id, text_content, create_date, comment_id)" +
//                    " VALUES ('" + comment[0] + "','" + comment[1] + "','" + comment[2] + "','" + comment[3] + "', current_timestamp, '"+id+"')");
//        } catch (SQLException e) {
//            System.out.println("Ошибка добавления комментария к задаче: " + e);
//        }
//    }
//
//    public Map<String, String> getOrgUsersMap(String org_id){
//        Map<String, String> orgUsers = new LinkedHashMap<>();
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT user_uuid, first_name, last_name, user_role FROM org_users WHERE org_uuid='"+org_id+"' and user_confirmed=true order by last_name desc;");
//            while (rs.next()){
//                orgUsers.put(rs.getString(1), rs.getString(2) +" "+ rs.getString(3) +" ("+ rs.getString(4)+")");
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения списка пользователей из таблицы org_users: " + e);
//        }
//        return orgUsers;
//    }
//
//    public OrgUser getOrgUserById(String userId){
//        OrgUser orgUser = new OrgUser();
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT org_uuid, first_name, last_name, user_role, user_email, reg_date, user_icon  FROM org_users" +
//                    " WHERE user_uuid='"+userId+"'");
//            if (rs.next()){
//                orgUser.setUserId(userId);
//                orgUser.setOrgId(rs.getString(1));
//                orgUser.setFirstName(rs.getString(2));
//                orgUser.setLastName(rs.getString(3));
//                orgUser.setRole(RolesEnum.getRoleByName(rs.getString(4)));
//                orgUser.setUserEmail(rs.getString(5));
//                try {
//                    orgUser.setRegDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString(6)));
//                } catch (ParseException e) {
//                    orgUser.setRegDate(null); //почему то не получили дату регистрации пользователя
//                }
//                orgUser.setUserIcon(rs.getString(7));
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения пользователя организации: " + e);
//        }
//        return orgUser;
//    }
//
//    public String getUserPassBase64ById(String userId, String orgId){
//        String passBase64 = null;
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT user_password FROM org_users WHERE user_uuid='"+userId+"' and org_uuid='"+orgId+"' and user_confirmed=true");
//            if (rs.next()){
//                passBase64 = rs.getString(1);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения пароля пользователя в кодировке Base64: " + e);
//        }
//        return passBase64;
//    }
//
//    public boolean updateUserPassBase64ById(String userId, String orgId, String newPassBase64){
//        try {
//            Statement s = connection.createStatement();
//            s.executeUpdate("update org_users set user_password='"+newPassBase64+"' where user_uuid='"+userId+"' and org_uuid='"+orgId+"'");
//        } catch (SQLException e) {
//            System.out.println("Ошибка обновления пароля пользователя: " + e);
//            return false;
//        }
//        return true;
//    }
//
//    public OrgUser getOrgUserByLoginPassPrefix(String login, String pass, String prefix){
//        OrgUser orgUser = null;
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT user_uuid, org_uuid, first_name, last_name, user_role, user_email, reg_date, user_icon  FROM org_users" +
//                    " where org_uuid in (select confirmation_uuid from organizations where org_prefix='"+prefix+"' and confirmation_uuid is not null and email_confirmed=true)" +
//                    " and user_login='"+login+"' and user_password='"+pass+"' and user_confirmed=true");
//            if (rs.next()){
//                orgUser = new OrgUser();
//                orgUser.setUserId(rs.getString(1));
//                orgUser.setOrgId(rs.getString(2));
//                orgUser.setFirstName(rs.getString(3));
//                orgUser.setLastName(rs.getString(4));
//                orgUser.setRole(RolesEnum.getRoleByName(rs.getString(5)));
//                orgUser.setUserEmail(rs.getString(6));
//                try {
//                    orgUser.setRegDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(rs.getString(7)));
//                } catch (ParseException e) {
//                    orgUser.setRegDate(null); //почему то не получили дату регистрации пользователя
//                }
//                orgUser.setUserIcon(rs.getString(8));
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения пользователя организации по связке 'префикс - логин - пароль': " + e);
//        }
//        return orgUser;
//    }
//
//    public List<Task> getShortOrgTaskList(String orgId){
//        List<Task> taskList = new LinkedList<>();
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT task_id, " +
//                    "creator_id, " +
//                    "head_line, " +
//                    "dead_line, " +
//                    "assign_id, " +
//                    "priority,  " +
//                    "state  " +
//                    " FROM task WHERE creator_org_id='"+orgId+"' order by create_date desc LIMIT 100");
//
//            while (rs.next()){
//                Task task = new Task();
//
//                task.setId(rs.getString(1));
//                task.setCreatorId(rs.getString(2));
//                task.setHeadLine(rs.getString(3));
//                task.setDeadLine(rs.getDate(4));
//
//                String assignId = rs.getString(5);
//                OrgUser orgUser = this.getOrgUserById(assignId);
//                task.setAssign(orgUser);
//                task.setPriority(rs.getString(6));
//                task.setState(rs.getString(7));
//
//                taskList.add(task);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения краткого представления списка задач организации: " + e);
//        }
//        return taskList;
//    }
//
//    public List<Task> getShortOrgArchivedTaskList(String orgId){
//        List<Task> taskList = new LinkedList<>();
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT task_id, " +
//                    "creator_id, " +
//                    "head_line, " +
//                    "dead_line, " +
//                    "assign_id, " +
//                    "priority,  " +
//                    "state  " +
//                    " FROM task WHERE creator_org_id='"+orgId+"' and state='"+TaskStatesEnum.archived.getState()+"' order by create_date desc LIMIT 5000");
//
//            while (rs.next()){
//                Task task = new Task();
//
//                task.setId(rs.getString(1));
//                task.setCreatorId(rs.getString(2));
//                task.setHeadLine(rs.getString(3));
//                task.setDeadLine(rs.getDate(4));
//
//                String assignId = rs.getString(5);
//                OrgUser orgUser = this.getOrgUserById(assignId);
//                task.setAssign(orgUser);
//                task.setPriority(rs.getString(6));
//                task.setState(rs.getString(7));
//
//                taskList.add(task);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения краткого представления списка задач организации: " + e);
//        }
//        return taskList;
//    }
//
//    public List<Task> getUserShortTaskList(String userId){
//        List<Task> taskList = new LinkedList<>();
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT task_id, " +
//                    "creator_id, " +
//                    "head_line, " +
//                    "dead_line, " +
//                    "assign_id, " +
//                    "priority, " +
//                    "state  " +
//                    " FROM task WHERE assign_id='"+userId+"' order by create_date desc");
//
//            while (rs.next()){
//                Task task = new Task();
//
//                task.setId(rs.getString(1));
//                task.setCreatorId(rs.getString(2));
//                task.setHeadLine(rs.getString(3));
//                task.setDeadLine(rs.getDate(4));
//
//                String assignId = rs.getString(5);
//                OrgUser orgUser = this.getOrgUserById(assignId);
//                task.setAssign(orgUser);
//                task.setPriority(rs.getString(6));
//                task.setState(rs.getString(7));
//
//                taskList.add(task);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения краткого представления списка задач пользователя: " + e);
//        }
//        return taskList;
//    }
//
//    public Task getTaskById(String taskId){
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT creator_org_id, " +
//                    "creator_id, " +
//                    "head_line, " +
//                    "description, " +
//                    "project, " +
//                    "dead_line, " +
//                    "assign_id, " +
//                    "attachment_line, " +
//                    "create_date, " +
//                    "priority, " +
//                    "state  " +
//                    " FROM task WHERE task_id='"+taskId+"'");
//
//            if (rs.next()){
//                Task task = new Task();
//
//                task.setCreatorOrgId(rs.getString(1));
//                task.setCreatorId(rs.getString(2));
//                task.setHeadLine(rs.getString(3));
//                task.setDescription(rs.getString(4));
//                task.setProject(rs.getString(5));
//                task.setDeadLine(rs.getDate(6));
//
//                String assignId = rs.getString(7);
//                OrgUser orgUser = this.getOrgUserById(assignId);
//                task.setAssign(orgUser);
//
//                task.setAttachmentLine(rs.getString(8));
//                task.setCreateDate(rs.getDate(9));
//                task.setPriority(rs.getString(10));
//                task.setState(rs.getString(11));
//
//                return task;
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения представления задачи: " + e);
//        }
//        return null;
//    }
//
//    public String getTaskAttachmentLine(String taskId){
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT attachment_line FROM task WHERE task_id='"+taskId+"'");
//
//            if (rs.next()){
//                return rs.getString(1);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения списка вложений задачи: " + e);
//        }
//        return null;
//    }
//
//    public void updateTaskAttachmentLine(String taskId, String newAttachmentLine){
//        try {
//            Statement s = connection.createStatement();
//            s.executeUpdate("update task set attachment_line='"+newAttachmentLine+"' WHERE task_id='"+taskId+"'");
//        } catch (SQLException e) {
//            System.out.println("Ошибка обновления списка вложений задачи: " + e);
//        }
//    }
//
//    public boolean changeTaskState(String taskId, String new_state){
//        try {
//            Statement s = connection.createStatement();
//            s.executeUpdate("update task set state='"+new_state+"' WHERE task_id='"+taskId+"'");
//        } catch (SQLException e) {
//            System.out.println("Ошибка обновления статуса задачи задачи: " + e);
//            return false;
//        }
//        return true;
//    }
//
//    public List<TaskComment> getTaskCommentsById(String taskId){
//        List<TaskComment> comments = new LinkedList<>();
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT org_id, " +
//                    "owner_id, " +
//                    "task_id, " +
//                    "text_content, " +
//                    "create_date, " +
//                    "comment_id " +
//                    " FROM task_comments WHERE task_id='"+taskId+"' order by create_date desc");
//
//            while (rs.next()){
//                TaskComment comment = new TaskComment();
//
//                String orgId = rs.getString(1);
//                String ownerId = rs.getString(2);
//
//                // если id создателя коммента и id организации разные,
//                // значит коммент создал пользователь
//                if (!orgId.equals(ownerId)) {
//                    OrgUser user = getOrgUserById(ownerId);
//                    comment.setPersonName(user.getFirstName() + " " + user.getLastName() + " (" + user.getRole().getRoleName() + ") ");
//                } else {
//                    String orgName = getOrgNameById(orgId);
//                    comment.setPersonName(orgName + " (Org)");
//                }
//
//                comment.setOrgId(orgId);
//                comment.setOwnerId(ownerId);
//
//                comment.setTaskId(rs.getString(3));
//                comment.setContent(rs.getString(4));
//
//                Date createDate = rs.getTimestamp(5);
//                String formattedDate = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(createDate);
//
//                comment.setCreateDate(formattedDate);
//                comment.setId(rs.getString(6));
//
//                comments.add(comment);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения комментариев задачи: " + e);
//        }
//        return  comments;
//    }
//
//    public void setTaskStateById(String taskId, String state){
//        try {
//            Statement s = connection.createStatement();
//            s.executeUpdate("update task set state='"+state+"' where task_id='"+taskId+"'");
//        } catch (SQLException e) {
//            System.out.println("Ошибка установки статуса задачи: " + e);
//        }
//    }
//
//    public void setTaskAssignById(String taskId, String newAssignId){
//        try {
//            Statement s = connection.createStatement();
//            s.executeUpdate("update task set assign_id='"+newAssignId+"', state='"+TaskStatesEnum.opened.getState()+"' where task_id='"+taskId+"'");
//        } catch (SQLException e) {
//            System.out.println("Ошибка переназначения задачи на пользователя: " + e);
//        }
//    }
//
//    public String getFilesById(String id){
//        String fileNames = null;
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT file_name FROM notes WHERE note_id='"+id+"'");
//            if (rs.next()){
//                fileNames = rs.getString(1);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения записи о файлах таблицы notes: " + e);
//        }
//        return fileNames;
//    }
//
//    public String getAudioById(String id){
//        String fileNames = null;
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT audio_file FROM notes WHERE note_id='"+id+"'");
//            if (rs.next()){
//                fileNames = rs.getString(1);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения записи об аудио файлах таблицы notes: " + e);
//        }
//        return fileNames;
//    }
//
//    public String getConfirmationUuid(String login){
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT confirmation_uuid FROM organizations WHERE org_login='"+login+"'");
//            if (rs.next()){
//                return rs.getString(1);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения uuid организации: " + e);
//        }
//        return login;
//    }
//
//    public String getValueFromOrganizationByUUID(String var, String uuid){
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT "+var+" FROM organizations WHERE confirmation_uuid='"+uuid+"'");
//            if (rs.next()){
//                return rs.getString(1);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения '"+var+"' организации: " + e);
//        }
//        return null;
//    }
//
//    public boolean isOrgProfileExists(String login, String password){
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT * FROM organizations WHERE org_login='"+login+"' and org_password='"+password+"' and email_confirmed=true");
//            if (rs.next()){
//                return true;
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения профиля организации: " + e);
//        }
//        return false;
//    }
//
//    public boolean isOrgPrefixExists(String prefix){
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT * FROM organizations WHERE org_prefix='"+prefix+"' and confirmation_uuid is not null and email_confirmed=true");
//            if (rs.next()){
//                return true;
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения префикса организации: " + e);
//        }
//        return false;
//    }
//
//    public boolean insert(String... data){
//        try {
//            Statement s = connection.createStatement();
//            s.executeUpdate("INSERT INTO organizations (org_name, org_address, org_email, org_login, org_password, email_confirmed, confirmation_uuid, reg_date, org_prefix) " +
//                    "VALUES ('"+data[0]+"','"+data[1]+"','"+data[2]+"','"+data[3]+"','"+data[4]+"', false,'"+data[5]+"', current_timestamp, '"+data[6]+"')");
//        } catch (SQLException e) {
//            System.out.println("Ошибка внесения записи об организации в таблицу organizations: " + e);
//            return false;
//        }
//        return true;
//    }
//
//    public boolean insertUsers(String... data){
//        try {
//            Statement s = connection.createStatement();
//            s.executeUpdate("INSERT INTO org_users (org_uuid, user_uuid, first_name, last_name, user_role, user_email, user_login, user_password, user_confirmed, reg_date, user_icon) " +
//                    "VALUES ('"+data[0]+"','"+data[1]+"','"+data[2]+"','"+data[3]+"','"+data[4]+"','"+data[5]+"','"+data[6]+"','"+data[7]+"', false, current_timestamp, '"+data[8]+"')");
//        } catch (SQLException e) {
//            System.out.println("Ошибка внесения записи о пользователе организации в таблицу org_users: " + e);
//            return false;
//        }
//        return true;
//    }
//
//    public void addNewTask(Task task){
//        try {
//            Statement s = connection.createStatement();
//            s.executeUpdate("INSERT INTO task (task_id, creator_id, creator_org_id, head_line, description, project, dead_line, assign_id, attachment_line, create_date, priority, state) " +
//                    "VALUES ('"+task.getId()
//                    +"','"+task.getCreatorId()
//                    +"','"+task.getCreatorOrgId()
//                    +"','"+task.getHeadLine()
//                    +"','"+task.getDescription()
//                    +"','"+task.getProject()
//                    +"','"+task.getDeadLine()
//                    +"','"+task.getAssign().getUserId()
//                    +"','"+task.getAttachmentLine()
//                    +"','"+task.getCreateDate()
//                    +"','"+task.getPriority()
//                    +"','"+TaskStatesEnum.opened.getState() +"')");
//        } catch (SQLException e) {
//            System.out.println("Ошибка внесения записи в таблицу task: " + e);
//        }
//    }
//
//    public void addNewAppeal(Appeal appeal){
//        try {
//            Statement s = connection.createStatement();
//            s.executeUpdate("INSERT INTO appeals (appeal_id, " +
//                    "priority_level, " +
//                    "appeal_number, " +
//                    "create_date, " +
//                    "appeal_content, " +
//                    "sender_org_name, " +
//                    "sender_name, " +
//                    "sender_mail, " +
//                    "appeal_state, " +
//                    "performer_id, " +
//                    "appeal_comment, " +
//                    "attachment_line, " +
//                    "org_id) " +
//                    "VALUES ('"+appeal.getAppealId()
//                    +"','"+appeal.getPriorityLevel()
//                    +"','"+appeal.getAppealNumber()
//                    +"', current_timestamp"
//                    +",'"+appeal.getAppealContent()
//                    +"','"+appeal.getSenderOrgName()
//                    +"','"+appeal.getSenderName()
//                    +"','"+appeal.getSenderMail()
//                    +"','"+ AppealsStatesEnum.IN_WORK.getState()
//                    +"','"+appeal.getPerformer().getUserId()
//                    +"','"+appeal.getCreatorsComment()
//                    +"','"+appeal.getAttachmentLine()
//                    +"','"+appeal.getOrgId() +"')");
//        } catch (SQLException e) {
//            System.out.println("Ошибка внесения записи в таблицу appeals: " + e);
//        }
//    }
//
//    public Appeal getAppealById(String appealId){
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT appeal_id, " +
//                    "priority_level, " +
//                    "appeal_number, " +
//                    "create_date, " +
//                    "appeal_content, " +
//                    "sender_org_name, " +
//                    "sender_name, " +
//                    "sender_mail, " +
//                    "appeal_state, " +
//                    "performer_id, " +
//                    "appeal_comment, " +
//                    "attachment_line, " +
//                    "org_id " +
//                    " FROM appeals WHERE appeal_id='"+appealId+"'");
//
//            if (rs.next()){
//                Appeal appeal = new Appeal();
//                appeal.setAppealId(rs.getString(1));
//                appeal.setPriorityLevel(rs.getString(2));
//                appeal.setAppealNumber(rs.getString(3));
//                appeal.setAppealRegDate(rs.getTimestamp(4));
//                appeal.setAppealContent(rs.getString(5));
//                appeal.setSenderOrgName(rs.getString(6));
//                appeal.setSenderName(rs.getString(7));
//                appeal.setSenderMail(rs.getString(8));
//                appeal.setAppealState(rs.getString(9));
//
//                String performerId = rs.getString(10);
//                OrgUser performer = this.getOrgUserById(performerId);
//
//                appeal.setPerformer(performer);
//                appeal.setCreatorsComment( rs.getString(11));
//                appeal.setAttachmentLine( rs.getString(12));
//                appeal.setOrgId( rs.getString(13));
//
//                return appeal;
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения представления заявки: " + e);
//        }
//        return null;
//    }
//
//    public boolean saveAppealChanges(String appeal_id, String new_state, String new_priority, String new_comment){
//        try {
//            Statement s = connection.createStatement();
//            s.executeUpdate("update appeals set appeal_state='"+new_state+"', priority_level='"+new_priority+"', appeal_comment='"+new_comment+"' where appeal_id='"+appeal_id+"'");
//
//            return true;
//        } catch (SQLException e) {
//            System.out.println("Ошибка сохранения изменений заявки: " + e);
//            return false;
//        }
//    }
//
//    public List<Appeal> getAppealsList(String orgId){
//        List<Appeal> appealsList = new LinkedList<>();
//        try {
//            Statement s = connection.createStatement();
//            ResultSet rs = s.executeQuery("SELECT appeal_id, " +
//                    "priority_level, " +
//                    "appeal_number, " +
//                    "create_date, " +
//                    "appeal_content, " +
//                    "sender_org_name, " +
//                    "sender_name, " +
//                    "sender_mail, " +
//                    "appeal_state, " +
//                    "performer_id, " +
//                    "appeal_comment, " +
//                    "attachment_line, " +
//                    "org_id " +
//                    " FROM appeals WHERE org_id='"+orgId+"' order by create_date desc LIMIT 1000");
//
//            while (rs.next()){
//                Appeal appeal = new Appeal();
//                appeal.setAppealId(rs.getString(1));
//                appeal.setPriorityLevel(rs.getString(2));
//                appeal.setAppealNumber(rs.getString(3));
//                appeal.setAppealRegDate(rs.getTimestamp(4));
//                appeal.setAppealContent(rs.getString(5));
//                appeal.setSenderOrgName(rs.getString(6));
//                appeal.setSenderName(rs.getString(7));
//                appeal.setSenderMail(rs.getString(8));
//                appeal.setAppealState(rs.getString(9));
//
//                String performerId = rs.getString(10);
//                OrgUser performer = this.getOrgUserById(performerId);
//
//                appeal.setPerformer(performer);
//                appeal.setCreatorsComment( rs.getString(11));
//                appeal.setAttachmentLine( rs.getString(12));
//                appeal.setOrgId( rs.getString(13));
//
//                appealsList.add(appeal);
//            }
//        } catch (SQLException e) {
//            System.out.println("Ошибка получения списка заявок организации: " + e);
//        }
//        return appealsList;
//    }
//
//    public void deleteOrgEmail(String email){
//        try {
//            Statement s = connection.createStatement();
//            s.executeUpdate("delete from organizations where org_email='"+email+"'");
//        } catch (SQLException e) {
//            System.out.println("Ошибка удаления записи об организации из таблицы organizations: " + e);
//        }
//    }
//
//    public void deleteUserEmail(String email){
//        try {
//            Statement s = connection.createStatement();
//            s.executeUpdate("delete from org_users where user_email='"+email+"'");
//        } catch (SQLException e) {
//            System.out.println("Ошибка удаления записи о пользователе из таблицы org_users: " + e);
//        }
//    }
//
//    public void deleteNote(String id){
//        try {
//            Statement s = connection.createStatement();
//            s.executeUpdate("delete from notes where note_id='"+id+"'");
//        } catch (SQLException e) {
//            System.out.println("Ошибка удаления записи из таблицы notes: " + e);
//        }
//    }
//
//    public void updateConfirmation(String table, String var1, String var2, String uuid){
//        try {
//            Statement s = connection.createStatement();
//            s.executeUpdate("update "+table+" set "+var1+"_confirmed=true where "+var2+"_uuid='"+uuid+"'");
//        } catch (SQLException e) {
//            System.out.println("Ошибка установки признака подтверждения: " + e);
//        }
//    }
}
