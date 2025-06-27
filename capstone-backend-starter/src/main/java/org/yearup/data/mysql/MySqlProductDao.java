package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.models.Product;
import org.yearup.data.ProductDao;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlProductDao extends MySqlDaoBase implements ProductDao {
    public MySqlProductDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<Product> search(Integer categoryId, BigDecimal minPrice, BigDecimal maxPrice, String color) {
        List<Product> products = new ArrayList<>();
        //FIXed changed sql to query; fixed the sql string; fixed the preparedStatement
        String query = "SELECT * FROM products WHERE (? =-1 or category_id = ?) " +
                "   AND (? = -1 OR price >= ?) AND (? = -1 OR price <= ?) " +
                "   AND (? = '' or color = ?) ";

//        categoryId = categoryId == null ? -1 : categoryId;
//        minPrice = minPrice == null ? new BigDecimal("-1") : minPrice;
//        maxPrice = maxPrice == null ? new BigDecimal("-1") : maxPrice;
//        color = color == null ? "" : color;

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, categoryId == null ? -1 : categoryId);
            preparedStatement.setInt(2, categoryId == null ? -1 : categoryId);

            preparedStatement.setBigDecimal(3, minPrice == null ? new BigDecimal(-1) : minPrice);
            preparedStatement.setBigDecimal(4, minPrice == null ? new BigDecimal(-1) : minPrice);

            preparedStatement.setBigDecimal(5, maxPrice == null ? new BigDecimal(-1) : maxPrice);
            preparedStatement.setBigDecimal(6, maxPrice == null ? new BigDecimal(-1) : maxPrice);

            preparedStatement.setString(7, color == null ? "" : color);
            preparedStatement.setString(8, color == null ? "" : color);
            try (
                    ResultSet resultSet = preparedStatement.executeQuery();
            ) {
                if (resultSet.next()) {
                    do {
                        Product product = mapRow(resultSet);
                        products.add(product);
                    } while (resultSet.next());
                }
            }
//            while (row.next()) {
//                Product product = mapRow(row);
//                products.add(product);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    @Override
    public List<Product> listByCategoryId(int categoryId) {
        List<Product> products = new ArrayList<>();

        String query = "SELECT * FROM products WHERE category_id = ? ";

        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, categoryId);
            try (
                    ResultSet resultSet = preparedStatement.executeQuery()
            ) {
                if (resultSet.next()) {
                    do {
                        Product product = mapRow(resultSet);
                        products.add(product);
                    } while (resultSet.next());
                }
            }
//            while (row.next()) {
//                Product product = mapRow(row);
//                products.add(product);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }


    @Override
    public Product getById(int productId) {
        String query = "SELECT * FROM products WHERE product_id = ?";
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, productId);
            try (
                    ResultSet resultSet = preparedStatement.executeQuery();
            ) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                } else {
                    System.out.println("No Product Found");
                }
            }
//            if (row.next()) {
//                return mapRow(row);
//            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Product create(Product product) {

        String query = "INSERT INTO products(name, price, category_id, description, color, image_url, stock, featured)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query,
                        PreparedStatement.RETURN_GENERATED_KEYS);
        ) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setBigDecimal(2, product.getPrice());
            preparedStatement.setInt(3, product.getCategoryId());
            preparedStatement.setString(4, product.getDescription());
            preparedStatement.setString(5, product.getColor());
            preparedStatement.setString(6, product.getImageUrl());
            preparedStatement.setInt(7, product.getStock());
            preparedStatement.setBoolean(8, product.isFeatured());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("New Product Added Successfully");
                // Retrieve the generated keys
                ResultSet resultSet = preparedStatement.getGeneratedKeys();

                if (resultSet.next()) {
                    // Retrieve the auto-incremented ID
                    int generatedProductId = resultSet.getInt(1);
                    // get the newly inserted category
                    return getById(generatedProductId);
                } else {
                    System.out.println("No Product Created...");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void update(int productId, Product product) {
        String query = "UPDATE products SET name = ?, price = ?, category_id = ?, description = ?, color = ?, " +
                "image_url = ?, stock = ?, featured = ? WHERE product_id = ?;";
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setString(1, product.getName());
            preparedStatement.setBigDecimal(2, product.getPrice());
            preparedStatement.setInt(3, product.getCategoryId());
            preparedStatement.setString(4, product.getDescription());
            preparedStatement.setString(5, product.getColor());
            preparedStatement.setString(6, product.getImageUrl());
            preparedStatement.setInt(7, product.getStock());
            preparedStatement.setBoolean(8, product.isFeatured());
            preparedStatement.setInt(9, productId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Product Updated Successfully!");
            } else {
                System.out.println("Product Update Failed...");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int productId) {
        String query = "DELETE FROM products WHERE product_id = ?;";
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
        ) {
            preparedStatement.setInt(1, productId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Product Deleted Successfully!");
            } else {
                System.out.println("Product Deletion Failed...");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected static Product mapRow(ResultSet resultSet) throws SQLException {
        int productId = resultSet.getInt("product_id");
        String name = resultSet.getString("name");
        BigDecimal price = resultSet.getBigDecimal("price");
        int categoryId = resultSet.getInt("category_id");
        String description = resultSet.getString("description");
        String color = resultSet.getString("color");
        String imageUrl = resultSet.getString("image_url");
        int stock = resultSet.getInt("stock");
        boolean isFeatured = resultSet.getBoolean("featured");

        return new Product(productId, name, price, categoryId, description, color, imageUrl, stock, isFeatured);
    }
}
