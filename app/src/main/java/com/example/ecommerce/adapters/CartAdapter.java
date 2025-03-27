package com.example.ecommerce.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce.R;
import com.example.ecommerce.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private CartItemListener listener;

    public interface CartItemListener {
        void onQuantityChanged(int position, int quantity);
        void onRemoveItem(int position);
    }

    public CartAdapter(List<CartItem> cartItems, CartItemListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProduct;
        private TextView tvName;
        private TextView tvPrice;
        private TextView tvQuantity;
        private Button btnDecrease;
        private Button btnIncrease;
        private ImageButton btnRemove;
        private TextView tvTotalPrice;
        private CartItemListener listener;

        public CartViewHolder(@NonNull View itemView, CartItemListener listener) {
            super(itemView);
            this.listener = listener;
            
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
            
            btnDecrease.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    int quantity = Integer.parseInt(tvQuantity.getText().toString());
                    if (quantity > 1) {
                        quantity--;
                        tvQuantity.setText(String.valueOf(quantity));
                        listener.onQuantityChanged(position, quantity);
                    }
                }
            });
            
            btnIncrease.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    int quantity = Integer.parseInt(tvQuantity.getText().toString());
                    quantity++;
                    tvQuantity.setText(String.valueOf(quantity));
                    listener.onQuantityChanged(position, quantity);
                }
            });
            
            btnRemove.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onRemoveItem(position);
                }
            });
        }

        public void bind(CartItem cartItem) {
            tvName.setText(cartItem.getProductName());
            tvPrice.setText(String.format("$%.2f", cartItem.getProductPrice()));
            tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
            tvTotalPrice.setText(String.format("$%.2f", cartItem.getTotalPrice()));

            // Load product image
            if (cartItem.getProductImageUrl() != null && !cartItem.getProductImageUrl().isEmpty()) {
                // Check if it's a resource identifier or a URL
                if (cartItem.getProductImageUrl().startsWith("http")) {
                    Glide.with(itemView.getContext())
                            .load(cartItem.getProductImageUrl())
                            .placeholder(R.drawable.placeholder_product)
                            .error(R.drawable.placeholder_product)
                            .into(ivProduct);
                } else {
                    // Load from drawable resources
                    int resourceId = itemView.getContext().getResources().getIdentifier(
                            cartItem.getProductImageUrl(), "drawable", itemView.getContext().getPackageName());
                    if (resourceId != 0) {
                        Glide.with(itemView.getContext())
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
        }
    }
}
