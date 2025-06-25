package org.yearup.data;

import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.util.Map;

public interface ShoppingCartDao {

    ShoppingCart getByUserId(int userId);
    ShoppingCart addItemToCart(int userId, int productId);
    void updateCart(int userId, int productid, ShoppingCartItem cart);
    void deleteCartByUser(int userId);

    // add additional method signatures here Done
}
