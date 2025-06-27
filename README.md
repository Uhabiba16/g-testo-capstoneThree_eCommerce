### Project Overview

This project is an E-Commerce API and Site, a Java Spring Boot backend for an online store called EasyShop. The API powers an e-commerce website that allows users to browse products, search/filter them, manage shopping carts, and place orders. The application uses a MySQL database for data storage, and Postman was used extensively for testing the API endpoints.

In this project, I completed Phase 1, 2, and 3:

Phase 1: Implemented the CategoriesController and DAO logic, enabling CRUD operations for product categories (only admins can add, update, or delete).

Phase 2: Found and fixed bugs in the product search/filter and product update logic to ensure accurate search results and proper product updates.

Phase 3: Added shopping cart functionality that lets logged-in users add products to their cart, update Quantity of products in their cart, view their cart, and clear it, with cart data persisting between sessions.

### Application Screenshots
#### SQL query test before implementing with java 
![Screenshot 2025-06-27 091004](https://github.com/user-attachments/assets/888447cc-3deb-4f12-98b9-28073776a4b1)

#### Postman example of reading products Filter Search: The API correctly returns a JSON array with a matching product 
![Screenshot 2025-06-27 091313](https://github.com/user-attachments/assets/1d09f3a9-ac34-4e14-8c2c-93b5a2a1333e)

#### Postman example of creating a new category by an authorized user using a Bearer token for authentication.
![Screenshot 2025-06-27 092156](https://github.com/user-attachments/assets/b0066eaa-5fa3-4ad2-b4c9-5c61d6bfdf7f)

#### Postman example of updating a category by an authorized user using a Bearer token for authentication.
![Screenshot 2025-06-27 092701](https://github.com/user-attachments/assets/6357282f-e53d-4a34-9a83-426cd14fadf0)
![Screenshot 2025-06-27 092730](https://github.com/user-attachments/assets/a6625722-bc9a-47c2-b7e6-68d16d263357)

#### Postman example of updating a Shopping Cart by the authorized user using a Bearer token for authentication.
![Screenshot 2025-06-27 093243](https://github.com/user-attachments/assets/26a77db8-68a1-4275-9f14-46f63c5860ec)
![Screenshot 2025-06-27 093321](https://github.com/user-attachments/assets/28e48243-5c8c-4af2-9a60-0ae0511c8c24)

#### Frontend Screenshots 
![Screenshot 2025-06-27 094249](https://github.com/user-attachments/assets/8c68114f-235c-4830-8a8b-068d44dae72d)
![Screenshot 2025-06-27 094313](https://github.com/user-attachments/assets/d9fc0ebe-b5e1-4504-b168-4260ee4fc666)



### Interesting Piece of Code
One interesting piece of code is the getByUserId method in the MySqlCartDao class. This method retrieves all shopping cart items for a specific user from the database using a secure SQL query with a prepared statement. It loops through each product in the cart, gets detailed product information from the ProductDao, and builds a ShoppingCart object that includes all the items and their quantities. This method demonstrates how to combine database queries, Java collections (a Map), and object-oriented design to create a complete and organized shopping cart for a logged-in user.
```
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
```
