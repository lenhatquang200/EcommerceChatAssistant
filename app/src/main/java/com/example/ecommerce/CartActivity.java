package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.adapters.CartAdapter;
import com.example.ecommerce.db.DatabaseHelper;
import com.example.ecommerce.models.CartItem;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemListener {

    private RecyclerView rvCart;
    private TextView tvEmptyCart;
    private TextView tvTotalAmount;
    private Button btnCheckout;
    private Button btnClearCart;
    
    private DatabaseHelper dbHelper;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        rvCart = findViewById(R.id.rv_cart);
        tvEmptyCart = findViewById(R.id.tv_empty_cart);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        btnCheckout = findViewById(R.id.btn_checkout);
        btnClearCart = findViewById(R.id.btn_clear_cart);

        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Shopping Cart");

        // Set up RecyclerView
        rvCart.setLayoutManager(new LinearLayoutManager(this));

        // Load cart items
        loadCartItems();

        // Set up buttons
        btnCheckout.setOnClickListener(v -> proceedToCheckout());
        
        btnClearCart.setOnClickListener(v -> {
            if (cartItems != null && !cartItems.isEmpty()) {
                showClearCartDialog();
            } else {
                Toast.makeText(this, "Cart is already empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCartItems() {
        // Get cart items from database
        cartItems = dbHelper.getCartItems();
        
        // Update UI based on cart items
        if (cartItems.isEmpty()) {
            showEmptyCart();
        } else {
            showCartItems();
        }
    }

    private void showEmptyCart() {
        rvCart.setVisibility(View.GONE);
        tvEmptyCart.setVisibility(View.VISIBLE);
        btnCheckout.setEnabled(false);
        btnClearCart.setEnabled(false);
        tvTotalAmount.setText("$0.00");
    }

    private void showCartItems() {
        rvCart.setVisibility(View.VISIBLE);
        tvEmptyCart.setVisibility(View.GONE);
        btnCheckout.setEnabled(true);
        btnClearCart.setEnabled(true);
        
        // Set up adapter
        cartAdapter = new CartAdapter(cartItems, this);
        rvCart.setAdapter(cartAdapter);
        
        // Update total amount
        updateTotalAmount();
    }

    private void updateTotalAmount() {
        double total = dbHelper.getCartTotal();
        tvTotalAmount.setText(String.format("$%.2f", total));
    }

    private void proceedToCheckout() {
        Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
        startActivity(intent);
    }

    private void showClearCartDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Clear Cart")
                .setMessage("Are you sure you want to remove all items from your cart?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    clearCart();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void clearCart() {
        int result = dbHelper.clearCart();
        if (result > 0) {
            Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show();
            loadCartItems();
        } else {
            Toast.makeText(this, "Failed to clear cart", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onQuantityChanged(int position, int quantity) {
        CartItem cartItem = cartItems.get(position);
        cartItem.setQuantity(quantity);
        
        int result = dbHelper.updateCartItemQuantity(cartItem.getId(), quantity);
        if (result > 0) {
            cartAdapter.notifyItemChanged(position);
            updateTotalAmount();
        } else {
            Toast.makeText(this, "Failed to update quantity", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRemoveItem(int position) {
        CartItem cartItem = cartItems.get(position);
        
        int result = dbHelper.removeCartItem(cartItem.getId());
        if (result > 0) {
            cartItems.remove(position);
            cartAdapter.notifyItemRemoved(position);
            
            if (cartItems.isEmpty()) {
                showEmptyCart();
            } else {
                updateTotalAmount();
            }
            
            Snackbar.make(rvCart, "Item removed", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", v -> {
                        // Add item back to cart
                        dbHelper.addToCart(cartItem);
                        loadCartItems();
                    })
                    .show();
        } else {
            Toast.makeText(this, "Failed to remove item", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        // Reload cart items when returning to this activity
        loadCartItems();
    }
}
