package com.example.l_tech;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.example.l_tech.Adapter.ProductImageAdapter;
import com.example.l_tech.Model.Product;
import com.example.l_tech.Model.AttributeValue;
import com.example.l_tech.Model.AttributeType;
import com.example.l_tech.Repozitory.UserDataListener;
import com.example.l_tech.retofit2_API.ProductApi;
import com.example.l_tech.retofit2_API.RetrofitClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class Product_veiw extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private TextView productNameText, productPriceText, productDescriptionText, productCodeText, productRatingText, productReviewsText, productOldPriceText, tvReadMore, tvAllSpecs, totalPriceText;
    private TextView btnDescription, btnSpecs, btnReviews;
    private LinearLayout infoDescription;
    private LinearLayout infoSpecs;
    private GridLayout specsGridLayout;
    private ImageButton favoriteButton;
    private Button addToCartButton;
    private LinearLayout cartQuantityLayout;
    private TextView cartQuantity;
    private ImageButton incrementButton;
    private ImageButton decrementButton;

    private String fullDescription;
    private Product currentProduct;
    private List<AttributeValue> allAttributes;
    private boolean isSpecsExpanded = false;
    private boolean isDescriptionExpanded = false;
    private ProductImageAdapter imageAdapter;
    private FirebaseAuth auth;
    private String userId;
    private UserDataListener userDataListener;
    private ValueEventListener favoritesListener, cartListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_veiw);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
        } else {
            userId = "guest";
        }

        // Initialize UserDataListener
        userDataListener = new UserDataListener(userId, new UserDataListener.DataChangeCallback() {
            @Override
            public void onCartUpdated(String cartData) {
                // Handle cart updates
            }

            @Override
            public void onFavoritesUpdated(String favoriteId) {
                // Handle favorites updates
            }

            @Override
            public void onOrdersUpdated(String ordersData) {
                // Handle orders updates
            }

            @Override
            public void onReviewsUpdated(String reviewsData) {
                // Handle reviews updates
            }
        }) {
            @Override
            public void isFavorite(String productId, Callback callback) {
            }
        };

        // Initialize views
        viewPager2 = findViewById(R.id.viewPager2);
        productNameText = findViewById(R.id.productNameText);
        productPriceText = findViewById(R.id.productPriceText);
        productDescriptionText = findViewById(R.id.tvDescription);
        productCodeText = findViewById(R.id.productCodeText);
        productRatingText = findViewById(R.id.productRatingText);
        productReviewsText = findViewById(R.id.productReviewsText);
        tvReadMore = findViewById(R.id.tvReadMore);
        tvAllSpecs = findViewById(R.id.tvAllSpecs);
        totalPriceText = findViewById(R.id.totalPriceText);
        favoriteButton = findViewById(R.id.favoriteButton);
        addToCartButton = findViewById(R.id.addToCartButton);
        cartQuantityLayout = findViewById(R.id.cartQuantityLayout);
        cartQuantity = findViewById(R.id.cartQuantity);
        incrementButton = findViewById(R.id.incrementButton);
        decrementButton = findViewById(R.id.decrementButton);
        btnDescription = findViewById(R.id.btnDescription);
        btnSpecs = findViewById(R.id.btnSpecs);
        btnReviews = findViewById(R.id.btnAccessories);
        infoDescription = findViewById(R.id.infoDescription);
        infoSpecs = findViewById(R.id.infoSpecs);
        specsGridLayout = findViewById(R.id.specsGridLayout);

        // Set initial visibility with null checks
        if (infoDescription != null) {
            infoDescription.setVisibility(View.VISIBLE);
        }
        if (infoSpecs != null) {
            infoSpecs.setVisibility(View.GONE);
        }
        if (tvAllSpecs != null) {
            tvAllSpecs.setVisibility(View.GONE);
        }

        // Get data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentProduct = extras.getParcelable("product");
            if (currentProduct != null) {
                // Set product information
                if (productNameText != null) {
                    productNameText.setText(currentProduct.getProductName());
                }
                if (productPriceText != null) {
                    productPriceText.setText(String.format("%.2f ₽", currentProduct.getPrice()));
                }
                fullDescription = currentProduct.getDescription();
                if (productCodeText != null) {
                    productCodeText.setText("ID " + currentProduct.getProductId());
                }
                if (productRatingText != null) {
                    productRatingText.setText(String.valueOf(currentProduct.getRating()));
                }
                if (productReviewsText != null) {
                    productReviewsText.setText(" отзывов");
                }
                if (totalPriceText != null) {
                    totalPriceText.setText(String.format("%.2f ₽", currentProduct.getPrice()));
                }

                // Handle initial Description Text and Read More button state
                if (productDescriptionText != null && fullDescription != null) {
                    if (fullDescription.length() > 200) {
                        productDescriptionText.setText(fullDescription.substring(0, 200) + "...");
                        if (tvReadMore != null) {
                            tvReadMore.setVisibility(View.VISIBLE);
                            tvReadMore.setText("Читать полностью");
                        }
                        isDescriptionExpanded = false;
                    } else {
                        productDescriptionText.setText(fullDescription);
                        if (tvReadMore != null) {
                            tvReadMore.setVisibility(View.GONE);
                        }
                        isDescriptionExpanded = true;
                    }
                }

                // Setup ViewPager2 with images
                if (viewPager2 != null) {
                    if (currentProduct.getImages() != null && !currentProduct.getImages().isEmpty()) {
                        imageAdapter = new ProductImageAdapter(currentProduct.getImages());
                        viewPager2.setAdapter(imageAdapter);
                    } else if (currentProduct.getImage() != null && !currentProduct.getImage().isEmpty()) {
                        List<String> singleImageList = new ArrayList<>();
                        singleImageList.add(currentProduct.getImage());
                        imageAdapter = new ProductImageAdapter(singleImageList);
                        viewPager2.setAdapter(imageAdapter);
                    }
                }

                // Set up initial favorite state
                setupFavoritesListener();
                // Set up initial cart state
                setupCartListener();
            }
        }

        // Set up click listeners for tabs
        if (btnDescription != null && infoDescription != null) {
            btnDescription.setOnClickListener(v -> showSection(infoDescription));
        }
        if (btnSpecs != null && infoSpecs != null) {
            btnSpecs.setOnClickListener(v -> {
                showSection(infoSpecs);
                if (allAttributes == null) {
                    fetchAndDisplayAttributes();
                } else {
                    updateSpecsDisplay();
                }
            });
        }
        if (btnReviews != null) {
            btnReviews.setOnClickListener(v -> { /* TODO: Implement Reviews Section */ });
        }

        // Set up click listener for Read More button
        if (tvReadMore != null && productDescriptionText != null) {
            tvReadMore.setOnClickListener(v -> {
                if (isDescriptionExpanded) {
                    productDescriptionText.setText(fullDescription != null && fullDescription.length() > 200 ? 
                        fullDescription.substring(0, 200) + "..." : fullDescription);
                    tvReadMore.setText("Читать полностью");
                } else {
                    productDescriptionText.setText(fullDescription);
                    tvReadMore.setText("Скрыть описание");
                }
                isDescriptionExpanded = !isDescriptionExpanded;
            });
        }

        // Set up click listener for All Specs button
        if (tvAllSpecs != null) {
            tvAllSpecs.setOnClickListener(v -> {
                isSpecsExpanded = !isSpecsExpanded;
                updateSpecsDisplay();
            });
        }

        // Set up favorite button click listener
        if (favoriteButton != null) {
            favoriteButton.setOnClickListener(v -> {
                try {
                    if (auth.getCurrentUser() == null) {
                        Toast.makeText(this, "Для добавления в избранное необходимо войти в аккаунт", Toast.LENGTH_LONG).show();
                        return;
                    }
                    userDataListener.isFavorite(String.valueOf(currentProduct.getProductId()), isFavorite -> {
                        if (isFavorite) {
                            userDataListener.removeFromFavorites(String.valueOf(currentProduct.getProductId()));
                            favoriteButton.setImageResource(R.drawable.heart_icon);
                        } else {
                            userDataListener.addToFavorites(String.valueOf(currentProduct.getProductId()));
                            favoriteButton.setImageResource(R.drawable.fill_heart_icon);
                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(this, "Ошибка при работе с избранным", Toast.LENGTH_SHORT).show();
                    Log.e("Favorites", "Error: " + e.getMessage());
                }
            });
        }

        // Set up cart button click listener
        if (addToCartButton != null && cartQuantityLayout != null && cartQuantity != null) {
            addToCartButton.setOnClickListener(v -> {
                try {
                    if (auth.getCurrentUser() == null) {
                        Toast.makeText(this, "Для добавления в корзину необходимо войти в аккаунт", Toast.LENGTH_LONG).show();
                        return;
                    }
                    addToCartButton.setVisibility(View.GONE);
                    cartQuantityLayout.setVisibility(View.VISIBLE);
                    cartQuantity.setText("1");
                    userDataListener.addToCart(String.valueOf(currentProduct.getProductId()), 1, currentProduct.getPrice());
                } catch (Exception e) {
                    Toast.makeText(this, "Ошибка при добавлении в корзину", Toast.LENGTH_SHORT).show();
                    Log.e("Cart", "Error: " + e.getMessage());
                }
            });
        }

        // Set up increment button click listener
        if (incrementButton != null && cartQuantity != null) {
            incrementButton.setOnClickListener(v -> {
                try {
                    if (auth.getCurrentUser() == null) {
                        Toast.makeText(this, "Для изменения количества необходимо войти в аккаунт", Toast.LENGTH_LONG).show();
                        return;
                    }
                    int count = Integer.parseInt(cartQuantity.getText().toString());
                    cartQuantity.setText(String.valueOf(count + 1));
                    userDataListener.addToCart(String.valueOf(currentProduct.getProductId()), count + 1, currentProduct.getPrice());
                } catch (Exception e) {
                    Toast.makeText(this, "Ошибка при изменении количества", Toast.LENGTH_SHORT).show();
                    Log.e("Cart", "Error: " + e.getMessage());
                }
            });
        }

        // Set up decrement button click listener
        if (decrementButton != null && cartQuantity != null && cartQuantityLayout != null) {
            decrementButton.setOnClickListener(v -> {
                try {
                    if (auth.getCurrentUser() == null) {
                        Toast.makeText(this, "Для изменения количества необходимо войти в аккаунт", Toast.LENGTH_LONG).show();
                        return;
                    }
                    int count = Integer.parseInt(cartQuantity.getText().toString());
                    if (count > 1) {
                        cartQuantity.setText(String.valueOf(count - 1));
                        userDataListener.addToCart(String.valueOf(currentProduct.getProductId()), count - 1, currentProduct.getPrice());
                    } else {
                        cartQuantityLayout.setVisibility(View.GONE);
                        addToCartButton.setVisibility(View.VISIBLE);
                        userDataListener.removeFromCart(String.valueOf(currentProduct.getProductId()));
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Ошибка при изменении количества", Toast.LENGTH_SHORT).show();
                    Log.e("Cart", "Error: " + e.getMessage());
                }
            });
        }

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showSection(View sectionToShow) {
        if (infoDescription != null) {
            infoDescription.setVisibility(View.GONE);
        }
        if (infoSpecs != null) {
            infoSpecs.setVisibility(View.GONE);
        }
        if (sectionToShow != null) {
            sectionToShow.setVisibility(View.VISIBLE);
        }
    }

    private void fetchAndDisplayAttributes() {
        if (currentProduct != null) {
            ProductApi apiService = RetrofitClient.getInstance().getApi();
            apiService.getProductAttributes(currentProduct.getProductId()).enqueue(new Callback<List<AttributeValue>>() {
                @Override
                public void onResponse(Call<List<AttributeValue>> call, Response<List<AttributeValue>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        allAttributes = response.body(); // Store the full list
                        updateSpecsDisplay(); // Initial display (limited)
                    } else {
                        Log.e("Product_veiw", "Failed to fetch attributes: " + response.code());
                        // Optionally show a message to the user
                        displayAttributesError(); // Show error message
                    }
                }

                @Override
                public void onFailure(Call<List<AttributeValue>> call, Throwable t) {
                    Log.e("Product_veiw", "Error fetching attributes: " + t.getMessage());
                    // Optionally show an error message to the user
                    displayAttributesError(); // Show error message
                }
            });
        }
    }

    private void updateSpecsDisplay() {
        specsGridLayout.removeAllViews(); // Clear previous views
        specsGridLayout.setColumnCount(2); // Ensure 2 columns

        if (allAttributes != null && !allAttributes.isEmpty()) {
            int attributesToShowCount = isSpecsExpanded ? allAttributes.size() : Math.min(allAttributes.size(), 4);

            for (int i = 0; i < attributesToShowCount; i++) {
                AttributeValue attribute = allAttributes.get(i);
                if (attribute.getAttributeType() != null && attribute.getAttributeValues() != null) {
                    // Attribute Name TextView
                    TextView nameTextView = new TextView(this);
                    nameTextView.setLayoutParams(getSpecTextViewLayoutParams());
                    nameTextView.setText(attribute.getAttributeType().getAttributeName() + ":");
                    nameTextView.setTextSize(16);
                    nameTextView.setTextColor(getResources().getColor(R.color.black));

                    // Attribute Value TextView
                    TextView valueTextView = new TextView(this);
                    valueTextView.setLayoutParams(getSpecTextViewLayoutParams());
                    valueTextView.setText(attribute.getAttributeValues());
                    valueTextView.setTextSize(16);
                    valueTextView.setTextColor(getResources().getColor(R.color.black));

                    // Add to GridLayout
                    specsGridLayout.addView(nameTextView);
                    specsGridLayout.addView(valueTextView);
                }
            }

            // Manage visibility and text of tvAllSpecs
            if (allAttributes.size() > 4) {
                tvAllSpecs.setVisibility(View.VISIBLE);
                tvAllSpecs.setText(isSpecsExpanded ? "Скрыть характеристики" : "Все характеристики");
            } else {
                tvAllSpecs.setVisibility(View.GONE);
            }

        } else {
            displayAttributesError(); // Show error message if list is empty or null
        }
    }

    private void displayAttributesError() {
        specsGridLayout.removeAllViews(); // Clear previous views
        specsGridLayout.setColumnCount(1); // Single column for the message
        TextView noAttributesText = new TextView(this);
        noAttributesText.setLayoutParams(getSpecTextViewLayoutParams());
        noAttributesText.setText("Характеристики недоступны.");
        noAttributesText.setTextSize(16);
        noAttributesText.setTextColor(getResources().getColor(R.color.black));
        specsGridLayout.addView(noAttributesText);
        tvAllSpecs.setVisibility(View.GONE); // Hide button if no attributes
    }

    private GridLayout.LayoutParams getSpecTextViewLayoutParams() {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.setMargins(0, 8, 0, 8); // Add some vertical margin
        return params;
    }

    private void setupFavoritesListener() {
        if (favoritesListener != null) {
            FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites")
                    .removeEventListener(favoritesListener);
        }

        favoritesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot favoriteSnapshot : snapshot.getChildren()) {
                        if (favoriteSnapshot.getKey().equals(String.valueOf(currentProduct.getProductId()))) {
                            favoriteButton.setImageResource(R.drawable.fill_heart_icon);
                            return;
                        }
                    }
                }
                favoriteButton.setImageResource(R.drawable.heart_icon);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FavoritesListener", "Ошибка: " + error.getMessage());
            }
        };

        FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites")
                .addValueEventListener(favoritesListener);
    }

    private void setupCartListener() {
        if (cartListener != null) {
            FirebaseDatabase.getInstance().getReference("users").child(userId).child("cart")
                    .removeEventListener(cartListener);
        }

        cartListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                        if (cartSnapshot.getKey().equals(String.valueOf(currentProduct.getProductId()))) {
                            Long quantityLong = cartSnapshot.child("quantity").getValue(Long.class);
                            if (quantityLong != null) {
                                int quantity = quantityLong.intValue();
                                cartQuantity.setText(String.valueOf(quantity));
                                cartQuantityLayout.setVisibility(View.VISIBLE);
                                addToCartButton.setVisibility(View.GONE);
                            }
                            return;
                        }
                    }
                }
                cartQuantityLayout.setVisibility(View.GONE);
                addToCartButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("CartListener", "Ошибка при получении данных из корзины: " + error.getMessage());
            }
        };

        FirebaseDatabase.getInstance().getReference("users").child(userId).child("cart")
                .addValueEventListener(cartListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove listeners when activity is destroyed
        if (favoritesListener != null) {
            FirebaseDatabase.getInstance().getReference("users").child(userId).child("favorites")
                    .removeEventListener(favoritesListener);
        }
        if (cartListener != null) {
            FirebaseDatabase.getInstance().getReference("users").child(userId).child("cart")
                    .removeEventListener(cartListener);
        }
    }
}