package com.example.l_tech;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.example.l_tech.Adapter.ProductImageAdapter;
import com.example.l_tech.Model.Product;

import java.util.ArrayList;
import java.util.List;

public class Product_veiw extends AppCompatActivity {
    private ViewPager2 viewPager2;
    private TextView productNameText, productPriceText, productDescriptionText, productCodeText, productRatingText, productReviewsText, productOldPriceText, tvReadMore, tvAllSpecs, totalPriceText;
    private TextView btnDescription, btnSpecs, btnReviews;
    private LinearLayout infoDescription, infoSpecs;

    private String fullDescription;

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

        // Set initial visibility
        infoDescription.setVisibility(View.VISIBLE);
        infoSpecs.setVisibility(View.GONE);

        // Get data from intent
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Product product = extras.getParcelable("product");
            if (product != null) {
                // Set product information
                productNameText.setText(product.getProductName());
                productPriceText.setText(String.format("%.2f ₽", product.getPrice()));
                fullDescription = product.getDescription(); // Store full description
                productCodeText.setText("ID " + product.getProductId());
                // You would typically get rating and reviews from your data source
                productRatingText.setText(String.valueOf(product.getRating()));
                productReviewsText.setText("29 отзывов"); // Example value
                totalPriceText.setText(String.format("%.2f ₽", product.getPrice())); // Example: total price is just the product price

                // Handle Description Text and Read More button
                if (fullDescription != null && fullDescription.length() > 200) {
                    productDescriptionText.setText(fullDescription.substring(0, 200) + "...");
                    tvReadMore.setVisibility(View.VISIBLE);
                } else {
                    productDescriptionText.setText(fullDescription);
                    tvReadMore.setVisibility(View.GONE);
                }

                // Setup ViewPager2 with images
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    imageAdapter = new ProductImageAdapter(product.getImages());
                    viewPager2.setAdapter(imageAdapter);
                } else if (product.getImage() != null && !product.getImage().isEmpty()){
                    // Fallback for single image if images array is empty/null
                    List<String> singleImageList = new ArrayList<>();
                    singleImageList.add(product.getImage());
                    imageAdapter = new ProductImageAdapter(singleImageList);
                    viewPager2.setAdapter(imageAdapter);
                }
            }
        }

        // Set up click listeners for tabs
        btnDescription.setOnClickListener(v -> showSection(infoDescription));
        btnSpecs.setOnClickListener(v -> showSection(infoSpecs));
        btnReviews.setOnClickListener(v -> { /* TODO: Implement Reviews Section */ });

        // Set up click listener for Read More button
        tvReadMore.setOnClickListener(v -> {
            productDescriptionText.setText(fullDescription);
            tvReadMore.setVisibility(View.GONE);
        });
        
        // Set up click listener for All Specs button (placeholder)
        tvAllSpecs.setOnClickListener(v -> { /* TODO: Implement All Specs screen/dialog */ });

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
}