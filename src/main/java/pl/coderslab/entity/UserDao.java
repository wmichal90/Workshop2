package pl.coderslab.entity;

import pl.coderslab.DbUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Arrays;

import static java.util.Arrays.copyOf;

public class UserDao {

    private static final String CREATE_USER_QUERY;

    static {
        CREATE_USER_QUERY = "INSERT INTO users(username, email, password) VALUES (?, ?, ?)";
    }

    private static final String READ_USER_QUERY;

    static {
        READ_USER_QUERY = "SELECT * FROM users WHERE id = ?";
    }

    private static final String READ_USER_BY_EMAIL_QUERY;

    static {
        READ_USER_BY_EMAIL_QUERY = "SELECT * FROM users WHERE email = ?";
    }


    private static final String READ_USER_BY_USERNAME_QUERY;

    static{
        READ_USER_BY_USERNAME_QUERY = "SELECT * FROM users WHERE username = ?";
    }

    private static final String UPDATE_USER_BY_ID;

    static {
        UPDATE_USER_BY_ID = "UPDATE users SET email = ?, username = ?, password = ? WHERE id = ?";
    }

    private static final String DELETE_USER_BY_ID_QUERY;

    static {
        DELETE_USER_BY_ID_QUERY = "DELETE FROM users WHERE id = ?";
    }

    private static final String SELECT_ALL_USERS_QUERY;

    static {
        SELECT_ALL_USERS_QUERY = "SELECT * FROM users";
    }

    public UserDao() {
    }

    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private User[] addUser(User user, User[] users){
        users = Arrays.copyOf(users, users.length + 1);
        users[users.length - 1] = user;
        return users;
    }

    private User[] listUsers(PreparedStatement statement){
        User [] users = new User[1];
        int counter = 0;
        try {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                User user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUserName(resultSet.getString("username"));
                user.setEmail(resultSet.getString("email"));
                user.setPassword(resultSet.getString("password"));
                if (counter == 0){
                    users[0] = user;
                } else {
                    users = addUser(user, users);
                }
                counter++;
            }
            return users;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }

    }

    private User returnUserFromUsersArray (User [] users){
        if (users != null){
            if (users.length <= 1){
                return users[0];
            } else {
                System.out.println("There is more than 1 record returned from query!");
                return null;
            }
        } else {
            return null;
        }
    }

    private void printUsers(User [] users){
        String [] columns = new String[]{"id", "mail", "username", "password"};
        for (String column: columns){
            System.out.print(column + "\t|\t");
        }
        System.out.println();
        for (User user: users){
            System.out.print(user.getId() + "\t|\t" +
                    user.getEmail() + "\t|\t" +
                    user.getUserName() + "\t|\t" +
                    user.getPassword());
            System.out.println();
        }
    }



    public  User create(User user) {
        long userId = -1;
        try (Connection conn = DbUtil.connect()) {
            PreparedStatement statement =
                    conn.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            String hashedPassword = hashPassword(user.getPassword());
            statement.setString(1, user.getUserName());
            statement.setString(2, user.getEmail());
            statement.setString(3, hashedPassword);
            statement.executeUpdate();
            //Pobieramy wstawiony do bazy identyfikator, a następnie ustawiamy id obiektu user.
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
                user.setPassword(hashedPassword);
                userId = resultSet.getInt(1);
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }



    public User read(int userId){

        try(Connection conn = DbUtil.connect()){
            PreparedStatement statement =
                    conn.prepareStatement(READ_USER_QUERY);
            statement.setInt(1, userId);
//            ResultSet resultSet = statement.executeQuery();
            User [] users = listUsers(statement);
            return returnUserFromUsersArray(users);


        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public User read(String email){

        try(Connection conn = DbUtil.connect()){
            PreparedStatement statement =
                    conn.prepareStatement(READ_USER_BY_EMAIL_QUERY);
            statement.setString(1, email);
            User [] users = listUsers(statement);
            return returnUserFromUsersArray(users);


        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }

    }

    public User [] readByUsername(String username){
        try(Connection conn = DbUtil.connect()){
            PreparedStatement statement =
                    conn.prepareStatement(READ_USER_BY_USERNAME_QUERY);
            statement.setString(1, username);
            return listUsers(statement);


        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public void update(User user){
        int id = user.getId();
        String username = user.getUserName();
        String email = user.getEmail();
        String password = hashPassword(user.getPassword());
        User userExists = read(id);
        if (userExists != null){
            try(Connection conn = DbUtil.connect()){
                PreparedStatement statement =
                        conn.prepareStatement(UPDATE_USER_BY_ID);
                statement.setString(1, email);
                statement.setString(2, username);
                statement.setString(3, password);
                statement.setInt(4, id);
                statement.executeUpdate();
            } catch (SQLException e){
                e.printStackTrace();
            }
        } else {
            System.out.printf("User of id = %s doesn't exist in database%n", id);
        }

    }

    public void delete(int userId) {
        User checkUser = read(userId);
        if (checkUser != null){
            try (Connection conn = DbUtil.connect()){
                PreparedStatement statement =
                        conn.prepareStatement(DELETE_USER_BY_ID_QUERY);
                statement.setInt(1, userId);
                statement.executeUpdate();
            } catch (SQLException e){
                e.printStackTrace();
            }
        } else {
            System.out.printf("User of id = %s doesn't exist in database, so it cannot be deleted!%n", userId);
        }
    }

    public User [] findAll(){
        try (Connection conn = DbUtil.connect()){
            PreparedStatement statement =
                    conn.prepareStatement(SELECT_ALL_USERS_QUERY);
            User [] users =  listUsers(statement);
            this.printUsers(users);
            return users;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }



}
