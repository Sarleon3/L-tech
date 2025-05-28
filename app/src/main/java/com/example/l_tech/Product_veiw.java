package com.example.l_tech;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.GridLayout;
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
import com.example.l_tech.retofit2_API.ProductApi;
import com.example.l_tech.retofit2_API.RetrofitClient;

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

    private String fullDescription;
    private Product currentProduct; // Store the product object
    private List<AttributeValue> allAttributes; // Store all fetched attributes
    private boolean isSpecsExpanded = false; // Track specs expansion state
    private boolean isDescriptionExpanded = false; // Track description expansion state

    private ProductImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_veiw);

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

        btnDescription = findViewById(R.id.btnDescription);
        btnSpecs = findViewById(R.id.btnSpecs);
        btnReviews = findViewById(R.id.btnAccessories);

        infoDescription = findViewById(R.id.infoDescription);
        infoSpecs = findViewById(R.id.infoSpecs);
        specsGridLayout = infoSpecs.findViewById(R.id.specsGridLayout);

        // Set initial visibility
        infoDescription.setVisibility(View.VISIBLE);
        infoSpecs.setVisibility(View.GONE);
        tvAllSpecs.setVisibility(View.GONE); // Hide initially

        // Get data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentProduct = extras.getParcelable("product"); // Store in class variable
            if (currentProduct != null) {
                // Set product information
                productNameText.setText(currentProduct.getProductName());
                productPriceText.setText(String.format("%.2f ₽", currentProduct.getPrice()));
                fullDescription = currentProduct.getDescription(); // Store full description
                productCodeText.setText("ID " + currentProduct.getProductId());
                // You would typically get rating and reviews from your data source
                productRatingText.setText(String.valueOf(currentProduct.getRating()));
                productReviewsText.setText("29 отзывов"); // Example value
                totalPriceText.setText(String.format("%.2f ₽", currentProduct.getPrice())); // Example: total price is just the product price

                // Handle initial Description Text and Read More button state
                if (fullDescription != null && fullDescription.length() > 200) {
                    productDescriptionText.setText(fullDescription.substring(0, 200) + "...");
                    tvReadMore.setVisibility(View.VISIBLE);
                    tvReadMore.setText("Читать полностью");
                    isDescriptionExpanded = false;
                } else {
                    productDescriptionText.setText(fullDescription);
                    tvReadMore.setVisibility(View.GONE);
                    isDescriptionExpanded = true; // No need to expand if already full
                }

                // Setup ViewPager2 with images
                if (currentProduct.getImages() != null && !currentProduct.getImages().isEmpty()) {
                    imageAdapter = new ProductImageAdapter(currentProduct.getImages());
                    viewPager2.setAdapter(imageAdapter);
                } else if (currentProduct.getImage() != null && !currentProduct.getImage().isEmpty()){
                    // Fallback for single image if images array is empty/null
                    List<String> singleImageList = new ArrayList<>();
                    singleImageList.add(currentProduct.getImage());
                    imageAdapter = new ProductImageAdapter(singleImageList);
                    viewPager2.setAdapter(imageAdapter);
                }
            }
        }

        // Set up click listeners for tabs
        btnDescription.setOnClickListener(v -> showSection(infoDescription));
        btnSpecs.setOnClickListener(v -> {
            showSection(infoSpecs);
            if (allAttributes == null) { // Fetch only if not already fetched
                fetchAndDisplayAttributes();
            } else {
                updateSpecsDisplay(); // Just update display if already fetched
            }
        });
        btnReviews.setOnClickListener(v -> { /* TODO: Implement Reviews Section */ });

        // Set up click listener for Read More button
        tvReadMore.setOnClickListener(v -> {
            if (isDescriptionExpanded) {
                // Collapse description
                productDescriptionText.setText(fullDescription != null && fullDescription.length() > 200 ? fullDescription.substring(0, 200) + "..." : fullDescription);
                tvReadMore.setText("Читать полностью");
            } else {
                // Expand description
                productDescriptionText.setText(fullDescription);
                tvReadMore.setText("Скрыть описание");
            }
            isDescriptionExpanded = !isDescriptionExpanded; // Toggle state
        });

        // Set up click listener for All Specs button
        tvAllSpecs.setOnClickListener(v -> {
            isSpecsExpanded = !isSpecsExpanded; // Toggle state
            updateSpecsDisplay(); // Update display based on new state
        });

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showSection(View sectionToShow) {
        infoDescription.setVisibility(View.GONE);
        infoSpecs.setVisibility(View.GONE);
        // TODO: Add other sections here (e.g., infoReviews)

        sectionToShow.setVisibility(View.VISIBLE);
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
}