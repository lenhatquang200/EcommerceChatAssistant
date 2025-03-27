package com.example.ecommerce;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.ecommerce.db.DatabaseHelper;
import com.google.android.material.textfield.TextInputLayout;

public class CheckoutActivity extends AppCompatActivity {

    private TextView tvSubtotal;
    private TextView tvShipping;
    private TextView tvTax;
    private TextView tvTotal;
    private TextInputLayout tilName;
    private TextInputLayout tilEmail;
    private TextInputLayout tilPhone;
    private TextInputLayout tilAddress;
    private TextInputLayout tilCity;
    private TextInputLayout tilZipCode;
    private RadioGroup rgPaymentMethod;
    private Button btnPlaceOrder;
    
    private DatabaseHelper dbHelper;
    private double subtotal = 0;
    private double shipping = 5.99; // Fixed shipping fee
    private double taxRate = 0.07; // 7% tax rate
    private double tax = 0;
    private double total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        Toolbar toolbar = findViewById(R.id.toolbar);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvShipping = findViewById(R.id.tv_shipping);
        tvTax = findViewById(R.id.tv_tax);
        tvTotal = findViewById(R.id.tv_total);
        tilName = findViewById(R.id.til_name);
        tilEmail = findViewById(R.id.til_email);
        tilPhone = findViewById(R.id.til_phone);
        tilAddress = findViewById(R.id.til_address);
        tilCity = findViewById(R.id.til_city);
        tilZipCode = findViewById(R.id.til_zip_code);
        rgPaymentMethod = findViewById(R.id.rg_payment_method);
        btnPlaceOrder = findViewById(R.id.btn_place_order);

        // Set up toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Checkout");

        // Calculate and display order summary
        calculateOrderSummary();

        // Set up place order button
        btnPlaceOrder.setOnClickListener(v -> validateAndPlaceOrder());
    }

    private void calculateOrderSummary() {
        // Get cart subtotal from database
        subtotal = dbHelper.getCartTotal();
        
        // Calculate tax
        tax = subtotal * taxRate;
        
        // Calculate total
        total = subtotal + shipping + tax;
        
        // Display values
        tvSubtotal.setText(String.format("$%.2f", subtotal));
        tvShipping.setText(String.format("$%.2f", shipping));
        tvTax.setText(String.format("$%.2f", tax));
        tvTotal.setText(String.format("$%.2f", total));
    }

    private void validateAndPlaceOrder() {
        // Validate form fields
        if (!validateForm()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Validate payment method
        if (rgPaymentMethod.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Mock order placement
        showOrderConfirmationDialog();
    }

    private boolean validateForm() {
        boolean valid = true;
        
        // Validate name
        String name = tilName.getEditText().getText().toString().trim();
        if (name.isEmpty()) {
            tilName.setError("Name is required");
            valid = false;
        } else {
            tilName.setError(null);
        }
        
        // Validate email
        String email = tilEmail.getEditText().getText().toString().trim();
        if (email.isEmpty()) {
            tilEmail.setError("Email is required");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Invalid email format");
            valid = false;
        } else {
            tilEmail.setError(null);
        }
        
        // Validate phone
        String phone = tilPhone.getEditText().getText().toString().trim();
        if (phone.isEmpty()) {
            tilPhone.setError("Phone is required");
            valid = false;
        } else {
            tilPhone.setError(null);
        }
        
        // Validate address
        String address = tilAddress.getEditText().getText().toString().trim();
        if (address.isEmpty()) {
            tilAddress.setError("Address is required");
            valid = false;
        } else {
            tilAddress.setError(null);
        }
        
        // Validate city
        String city = tilCity.getEditText().getText().toString().trim();
        if (city.isEmpty()) {
            tilCity.setError("City is required");
            valid = false;
        } else {
            tilCity.setError(null);
        }
        
        // Validate zip code
        String zipCode = tilZipCode.getEditText().getText().toString().trim();
        if (zipCode.isEmpty()) {
            tilZipCode.setError("ZIP Code is required");
            valid = false;
        } else {
            tilZipCode.setError(null);
        }
        
        return valid;
    }

    private void showOrderConfirmationDialog() {
        // Get payment method
        int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();
        RadioButton selectedPayment = findViewById(selectedPaymentId);
        String paymentMethod = selectedPayment.getText().toString();
        
        new AlertDialog.Builder(this)
                .setTitle("Confirm Order")
                .setMessage("Total amount: $" + String.format("%.2f", total) + "\nPayment method: " + paymentMethod)
                .setPositiveButton("Place Order", (dialog, which) -> {
                    placeOrder();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void placeOrder() {
        // Clear the cart in the database
        dbHelper.clearCart();
        
        // Show success message
        new AlertDialog.Builder(this)
                .setTitle("Order Placed")
                .setMessage("Your order has been placed successfully. Thank you for shopping with us!")
                .setPositiveButton("Continue Shopping", (dialog, which) -> {
                    // Navigate back to MainActivity
                    Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false)
                .show();
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
