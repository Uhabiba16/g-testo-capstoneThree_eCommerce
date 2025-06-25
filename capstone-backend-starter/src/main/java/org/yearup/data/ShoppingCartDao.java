package org.yearup.data;

import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import java.util.Map;

public interface ShoppingCartDao {

    ShoppingCart getByUserId(int userId);
    ShoppingCart addItemToCart(int userId, int productId);
    void updateCart(int productid,ShoppingCart cart);
    void deleteCartByUser(int userId);

    //TODO add additional method signatures here
}
