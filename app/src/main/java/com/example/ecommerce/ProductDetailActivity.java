package com.example.ecommerce;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.ecommerce.db.DatabaseHelper;
import com.example.ecommerce.models.CartItem;
import com.example.ecommerce.models.Product;
import com.example.ecommerce.utils.Constants;
import com.google.android.material.snackbar.Snackbar;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView ivProduct;
    private TextView tvName;
    private TextView tvPrice;
    private TextView tvDescription;
    private TextView tvStock;
    private Button btnAddToCart;
    private Button btnDecrease;
    private Button btnIncrease;
    private TextView tvQuantity;
    
    private DatabaseHelper dbHelper;
    private Product product;
    private int quantity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        ivProduct = findViewById(R.id.iv_product);
        tvName = findViewById(R.id.tv_name);
        tvPrice = findViewById(R.id.tv_price);
        tvDescription = findViewById(R.id.tv_description);
        tvStock = findViewById(R.id.tv_stock);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnDecrease = findViewById(R.id.btn_decrease);
        btnIncrease = findViewById(R.id.btn_increase);
        tvQuantity = findViewById(R.id.tv_quantity);

        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        // Get product ID from intent
        int productId = getIntent().getIntExtra("product_id", -1);
        if (productId == -1) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Get product from database
        product = dbHelper.getProduct(productId);
        if (product == null) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Display product information
        updateUI();

        // Set up quantity selector
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        btnIncrease.setOnClickListener(v -> {
            if (quantity < product.getStock()) {
                quantity++;
                tvQuantity.setText(String.valueOf(quantity));
            } else {
                Toast.makeText(this, "Cannot exceed available stock", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up add to cart button
        btnAddToCart.setOnClickListener(v -> addToCart());
    }

    private void updateUI() {
        // Set product name
        tvName.setText(product.getName());
        
        // Set product price
        tvPrice.setText(String.format("$%.2f", product.getPrice()));
        
        // Set product description
        tvDescription.setText(product.getDescription());
        
        // Set product stock
        tvStock.setText(String.format("In Stock: %d", product.getStock()));
        
        // Set product image
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            // Check if it's a resource identifier or a URL
            if (product.getImageUrl().startsWith("http")) {
                Glide.with(this)
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.placeholder_product)
                        .error(R.drawable.placeholder_product)
                        .into(ivProduct);
            } else {
                // Load from drawable resources
                int resourceId = getResources().getIdentifier(
                        product.getImageUrl(), "drawable", getPackageName());
                if (resourceId != 0) {
                    Glide.with(this)
                            .load(resourceId)
                            .placeholder(R.drawable.placeholder_product)
                            .error(R.drawable.placeholder_product)
                            .into(ivProduct);
                } else {
                    ivProduct.setImageResource(R.drawable.placeholder_product);
                }
            }
        } else {
            ivProduct.setImageResource(R.drawable.placeholder_product);
        }
        
        // Update quantity
        tvQuantity.setText(String.valueOf(quantity));
        
        // Handle out of stock
        if (product.getStock() <= 0) {
            btnAddToCart.setEnabled(false);
            btnIncrease.setEnabled(false);
            btnDecrease.setEnabled(false);
            tvStock.setText("Out of Stock");
            tvStock.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    private void addToCart() {
        if (quantity <= 0 || quantity > product.getStock()) {
            Toast.makeText(this, "Invalid quantity", Toast.LENGTH_SHORT).show();
            return;
        }

        CartItem cartItem = new CartItem();
        cartItem.setProductId(product.getId());
        cartItem.setProductName(product.getName());
        cartItem.setProductPrice(product.getPrice());
        cartItem.setProductImageUrl(product.getImageUrl());
        cartItem.setQuantity(quantity);

        long result = dbHelper.addToCart(cartItem);
        if (result > 0) {
            Snackbar.make(btnAddToCart, "Added to cart", Snackbar.LENGTH_SHORT)
                    .setAction("View Cart", v -> {
                        finish();
                        startActivity(getIntent());
                    })
                    .show();
        } else {
            Toast.makeText(this, "Failed to add to cart", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
