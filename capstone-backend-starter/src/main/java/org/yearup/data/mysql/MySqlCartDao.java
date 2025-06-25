package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class MySqlCartDao extends MySqlDaoBase implements ShoppingCartDao {
    private ProductDao productDao;

    public MySqlCartDao(DataSource dataSource, ProductDao productDao) {
        super(dataSource);
        this.productDao = productDao;
    }

    @Override
    public ShoppingCart getByUserId(int userId) {
        Map<Integer, ShoppingCartItem> items = new HashMap<>();
        String query = "select product_id,quantity from shopping_cart where user_id=?;";

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, userId);
            try (
                    ResultSet resultSet = preparedStatement.executeQuery();
            ) {
                while (resultSet.next()) {
                    int productId = resultSet.getInt("product_id");
                    int quantity = resultSet.getInt("quantity");

                    Product product = productDao.getById(productId);
                    ShoppingCartItem item = new ShoppingCartItem();
                    item.setProduct(product);
                    item.setQuantity(quantity);

                    items.put(productId, item);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        ShoppingCart cart = new ShoppingCart();
        cart.setItems(items);
        return cart;
    }

    @Override
    public ShoppingCart addItemToCart(int userId, int productId) {

        String query = "INSERT INTO shopping_cart (user_id, product_id, quantity) VALUES (?, ?, 1) " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + 1;";
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, productId);

            int rowAffected = preparedStatement.executeUpdate();
            if (rowAffected > 0) {
                System.out.println("Item Added Successfully!");
            } else {
                System.out.println("No Item Added To Cart...");
            }
            return getByUserId(userId);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void updateCart(int userId, int productid, ShoppingCartItem cart) {

        String query= "update shopping_cart set quantity=? where user_id=? and product_id=?;";

        try(
                Connection connection=getConnection();
                PreparedStatement preparedStatement= connection.prepareStatement(query)
                ){
            preparedStatement.setInt(1,cart.getQuantity());
            preparedStatement.setInt(2,userId);
            preparedStatement.setInt(3,productid);

            int rowAffected= preparedStatement.executeUpdate();
            if(rowAffected==1){
                System.out.println("Quantity Updated Successfully In Cart");
            }else{
                System.out.println("Quantity Update Failed...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void deleteCartByUser(int userId) {

        String query="delete from shopping_cart where user_id=?;";

        try(
                Connection connection=getConnection();
                PreparedStatement preparedStatement= connection.prepareStatement(query)
                ){
            preparedStatement.setInt(1,userId);

            int rowAffected= preparedStatement.executeUpdate();
            if(rowAffected==1){
                System.out.println("Cart Emptied successfully");
            }else {
                System.out.println("Cart Emptying Failed...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
