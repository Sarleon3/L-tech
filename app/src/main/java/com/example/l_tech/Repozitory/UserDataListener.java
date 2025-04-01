package com.example.l_tech.Repozitory;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;
public class UserDataListener {
    private final DatabaseReference userRef;
    private final DataChangeCallback callback;
    private final ValueEventListener valueEventListener;

    public interface DataCheckCallback {
        void onCheck(boolean exists);
    }

    public interface DataChangeCallback {
        void onCartUpdated(String cartData);
        void onFavoritesUpdated(String favoritesData);
        void onOrdersUpdated(String ordersData);
        void onReviewsUpdated(String reviewsData);
    }

    public UserDataListener(String userId, DataChangeCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Callback cannot be null");
        }
        this.callback = callback;
        this.userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        this.valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("cart").exists()) {
                    handleCartUpdate(snapshot.child("cart"));
                }
                if (snapshot.child("favorites").exists()) {
                    handleFavoritesUpdate(snapshot.child("favorites"));
                }
                if (snapshot.child("orders").exists()) {
                    handleOrdersUpdate(snapshot.child("orders"));
                }
                if (snapshot.child("reviews").exists()) {
                    handleReviewsUpdate(snapshot.child("reviews"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserDataListener", "Database error: " + error.getMessage());
            }
        };

        userRef.addValueEventListener(valueEventListener);
    }


    // ---- ОБРАБОТКА ИЗМЕНЕНИЙ ---- //
    private void handleCartUpdate(DataSnapshot cartSnapshot) {
        String cartData = cartSnapshot.getValue().toString();
        if (callback != null) {
            callback.onCartUpdated(cartData);
        } else {
            Log.w("UserDataListener", "Callback is null, skipping cart update.");
        }
    }

    private void handleFavoritesUpdate(DataSnapshot favoritesSnapshot) {
        String favoritesData = favoritesSnapshot.getValue().toString();
        if (callback != null) {
            callback.onFavoritesUpdated(favoritesData);
        } else {
            Log.w("UserDataListener", "Callback is null, skipping favorites update.");
        }
    }

    private void handleOrdersUpdate(DataSnapshot ordersSnapshot) {
        String ordersData = ordersSnapshot.getValue().toString();
        if (callback != null) {
            callback.onOrdersUpdated(ordersData);
        } else {
            Log.w("UserDataListener", "Callback is null, skipping orders update.");
        }
    }

    private void handleReviewsUpdate(DataSnapshot reviewsSnapshot) {
        String reviewsData = reviewsSnapshot.getValue().toString();
        if (callback != null) {
            callback.onReviewsUpdated(reviewsData);
        } else {
            Log.w("UserDataListener", "Callback is null, skipping reviews update.");
        }
    }

    // ---- МЕТОДЫ PUSH ---- //

    // Добавить товар в корзину
    public void addToCart(String productId, int quantity, double price) {
        DatabaseReference cartRef = userRef.child("cart").child(productId);
        Map<String, Object> cartItem = new HashMap<>();
        cartItem.put("quantity", quantity);
        cartItem.put("price", price);
        cartItem.put("timestamp", ServerValue.TIMESTAMP);

        cartRef.setValue(cartItem);
    }

    // Удалить товар из корзины
    public void removeFromCart(String productId) {
        userRef.child("cart").child(productId).removeValue();
    }

    // Добавить в избранное
    public void addToFavorites(String productId) {
        DatabaseReference favRef = userRef.child("favorites").child(productId);
        favRef.setValue(ServerValue.TIMESTAMP);
    }

    // Удалить из избранного
    public void removeFromFavorites(String productId) {
        userRef.child("favorites").child(productId).removeValue();
    }

    // Оформить заказ
    public void addOrder(String orderId, Map<String, Object> orderDetails) {
        DatabaseReference orderRef = userRef.child("orders").child(orderId);
        orderRef.setValue(orderDetails);
    }

    // Добавить отзыв
    public void addReview(String productId, int rating, String pros, String cons, String comment) {
        DatabaseReference reviewRef = userRef.child("reviews").child(productId);
        Map<String, Object> review = new HashMap<>();
        review.put("rating", rating);
        review.put("pros", pros);
        review.put("cons", cons);
        review.put("comment", comment);
        review.put("timestamp", ServerValue.TIMESTAMP);

        reviewRef.setValue(review);
    }

    public void isFavorite(String productId, DataCheckCallback callback) {
        userRef.child("favorites").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onCheck(snapshot.exists()); // true, если товар в избранном
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onCheck(false);
            }
        });
    }

    // Удалить отзыв
    public void removeReview(String productId) {
        userRef.child("reviews").child(productId).removeValue();
    }

    public void stopListening() {
        userRef.removeEventListener(valueEventListener);
    }
}
