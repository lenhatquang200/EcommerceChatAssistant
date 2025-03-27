package com.example.ecommerce.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerce.R;
import com.example.ecommerce.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProduct;
        private TextView tvName;
        private TextView tvPrice;

        public ProductViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }

        public void bind(Product product) {
            tvName.setText(product.getName());
            tvPrice.setText(String.format("$%.2f", product.getPrice()));

            // Load product image
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                // Check if it's a resource identifier or a URL
                if (product.getImageUrl().startsWith("http")) {
                    Glide.with(itemView.getContext())
                            .load(product.getImageUrl())
                            .placeholder(R.drawable.placeholder_product)
                            .error(R.drawable.placeholder_product)
                            .into(ivProduct);
                } else {
                    // Load from drawable resources
                    int resourceId = itemView.getContext().getResources().getIdentifier(
                            product.getImageUrl(), "drawable", itemView.getContext().getPackageName());
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
