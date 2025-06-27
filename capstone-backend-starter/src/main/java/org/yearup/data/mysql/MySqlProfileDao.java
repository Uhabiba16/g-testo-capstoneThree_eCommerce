package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.data.ProfileDao;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class MySqlProfileDao extends MySqlDaoBase implements ProfileDao {
    private UserDao userDao;

    public MySqlProfileDao(DataSource dataSource,UserDao userDao) {
        super(dataSource);
        this.userDao=userDao;
    }

    @Override
    public Profile create(Profile profile) {

        String query = "INSERT INTO profiles (user_id, first_name, last_name, phone, email, address, city, state, zip)"
                +" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query,
                        PreparedStatement.RETURN_GENERATED_KEYS);
                ){
            preparedStatement.setInt(1, profile.getUserId());
            preparedStatement.setString(2, profile.getFirstName());
            preparedStatement.setString(3, profile.getLastName());
            preparedStatement.setString(4, profile.getPhone());
            preparedStatement.setString(5, profile.getEmail());
            preparedStatement.setString(6, profile.getAddress());
            preparedStatement.setString(7, profile.getCity());
            preparedStatement.setString(8, profile.getState());
            preparedStatement.setString(9, profile.getZip());

            int rowAffected= preparedStatement.executeUpdate();
            if(rowAffected==1){
                System.out.println("Profile Created Successfully!");
                return profile;
            }else{
                System.out.println("Failed To Create Profile...");
            }
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Profile getByUserId(int userId) {
        String query= "Select * from profiles where user_Id=?;";

        try(
                Connection connection=getConnection();
                PreparedStatement preparedStatement= connection.prepareStatement(query)
                ){
            preparedStatement.setInt(1,userId);
            try(
                    ResultSet resultSet=preparedStatement.executeQuery()
                    ){
                if(resultSet.next()){
                    System.out.println("Profile Successfully Found By Id");
                    return mapRow(resultSet);
                }else {
                    System.out.println("No Profile Found");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateProfile(int userId, Profile profile) {
        String query= "update profiles set first_name=?,last_name=?,phone=?,email=?,address=?,city=?,state=?,zip=?)" +
                "where user_id=?;";

        try(
                Connection connection=getConnection();
                PreparedStatement preparedStatement= connection.prepareStatement(query)
                ){
            preparedStatement.setString(1, profile.getFirstName());
            preparedStatement.setString(2, profile.getLastName());
            preparedStatement.setString(3, profile.getPhone());
            preparedStatement.setString(4, profile.getEmail());
            preparedStatement.setString(5, profile.getAddress());
            preparedStatement.setString(6, profile.getCity());
            preparedStatement.setString(7, profile.getState());
            preparedStatement.setString(8, profile.getZip());

            int rowAffected= preparedStatement.executeUpdate();
            if(rowAffected==1){
                System.out.println("Profile Updated Successfully");
            }else{
                System.out.println("Profile Update Failed");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private Profile mapRow(ResultSet row) throws SQLException{
        int userId= row.getInt("user_id");
        String firstName= row.getString("first_name");
        String lastName= row.getString("last_name");
        String phone= row.getString("phone");
        String email= row.getString("email");
        String address= row.getString("address");
        String city= row.getString("city");
        String state= row.getString("state");
        String zip= row.getString("zip");

        return new Profile(userId,firstName,lastName,phone,email,address,city,state,zip);
    }
}
