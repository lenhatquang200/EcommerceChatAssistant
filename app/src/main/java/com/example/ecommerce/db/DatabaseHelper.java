package com.example.ecommerce.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ecommerce.models.CartItem;
import com.example.ecommerce.models.Category;
import com.example.ecommerce.models.ChatMessage;
import com.example.ecommerce.models.Product;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ecommerce.db";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_CATEGORIES = "categories";
    private static final String TABLE_CART = "cart";
    private static final String TABLE_CHAT_MESSAGES = "chat_messages";

    // Common column names
    private static final String COLUMN_ID = "id";
    
    // Products table columns
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_IMAGE_URL = "image_url";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_STOCK = "stock";
    
    // Categories table columns
    private static final String COLUMN_ICON_URL = "icon_url";
    
    // Cart table columns
    private static final String COLUMN_PRODUCT_ID = "product_id";
    private static final String COLUMN_PRODUCT_NAME = "product_name";
    private static final String COLUMN_PRODUCT_PRICE = "product_price";
    private static final String COLUMN_PRODUCT_IMAGE_URL = "product_image_url";
    private static final String COLUMN_QUANTITY = "quantity";
    
    // Chat Messages table columns
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    // Create table statements
    private static final String CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_PRODUCTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_DESCRIPTION + " TEXT,"
            + COLUMN_PRICE + " REAL,"
            + COLUMN_IMAGE_URL + " TEXT,"
            + COLUMN_CATEGORY_ID + " INTEGER,"
            + COLUMN_STOCK + " INTEGER"
            + ")";

    private static final String CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_ICON_URL + " TEXT"
            + ")";

    private static final String CREATE_TABLE_CART = "CREATE TABLE " + TABLE_CART + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_PRODUCT_ID + " INTEGER,"
            + COLUMN_PRODUCT_NAME + " TEXT,"
            + COLUMN_PRODUCT_PRICE + " REAL,"
            + COLUMN_PRODUCT_IMAGE_URL + " TEXT,"
            + COLUMN_QUANTITY + " INTEGER"
            + ")";

    private static final String CREATE_TABLE_CHAT_MESSAGES = "CREATE TABLE " + TABLE_CHAT_MESSAGES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_MESSAGE + " TEXT,"
            + COLUMN_TYPE + " INTEGER,"
            + COLUMN_TIMESTAMP + " INTEGER"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PRODUCTS);
        db.execSQL(CREATE_TABLE_CATEGORIES);
        db.execSQL(CREATE_TABLE_CART);
        db.execSQL(CREATE_TABLE_CHAT_MESSAGES);
        
        // Insert sample categories
        insertSampleCategories(db);
        
        // Insert sample products
        insertSampleProducts(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_MESSAGES);
        onCreate(db);
    }
    
    private void insertSampleCategories(SQLiteDatabase db) {
        // Sample categories for demonstration purposes
        String[] categories = {"Electronics", "Clothing", "Home & Kitchen", "Books", "Beauty"};
        
        for (int i = 0; i < categories.length; i++) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, categories[i]);
            // Using material icons as placeholders
            values.put(COLUMN_ICON_URL, "category_icon_" + (i + 1));
            
            db.insert(TABLE_CATEGORIES, null, values);
        }
    }
    
    private void insertSampleProducts(SQLiteDatabase db) {
        // Sample products for demonstration purposes
        String[][] products = {
            // name, description, price, image_url, category_id, stock
            {"Smartphone X", "Latest smartphone with incredible features", "999.99", "phone_image", "1", "50"},
            {"Laptop Pro", "High-performance laptop for professionals", "1499.99", "laptop_image", "1", "30"},
            {"Smartwatch", "Track your fitness and receive notifications", "299.99", "watch_image", "1", "100"},
            {"T-shirt", "Comfortable cotton t-shirt", "19.99", "tshirt_image", "2", "200"},
            {"Jeans", "Classic blue jeans", "49.99", "jeans_image", "2", "150"},
            {"Coffee Maker", "Automatic coffee maker for your home", "89.99", "coffee_maker_image", "3", "40"},
            {"Blender", "Powerful blender for smoothies", "69.99", "blender_image", "3", "60"},
            {"Novel Collection", "Set of bestselling novels", "39.99", "book_image", "4", "80"},
            {"Face Cream", "Hydrating face cream", "24.99", "cream_image", "5", "120"}
        };
        
        for (String[] product : products) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, product[0]);
            values.put(COLUMN_DESCRIPTION, product[1]);
            values.put(COLUMN_PRICE, Double.parseDouble(product[2]));
            values.put(COLUMN_IMAGE_URL, product[3]);
            values.put(COLUMN_CATEGORY_ID, Integer.parseInt(product[4]));
            values.put(COLUMN_STOCK, Integer.parseInt(product[5]));
            
            db.insert(TABLE_PRODUCTS, null, values);
        }
    }

    // Product methods
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                product.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));
                product.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
                product.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
                product.setStock(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK)));
                
                products.add(product);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return products;
    }
    
    public List<Product> getProductsByCategory(int categoryId) {
        List<Product> products = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_CATEGORY_ID + " = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(categoryId)});
        
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                product.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));
                product.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
                product.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
                product.setStock(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK)));
                
                products.add(product);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return products;
    }
    
    public List<Product> searchProducts(String query) {
        List<Product> products = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_NAME + " LIKE ? OR " 
            + COLUMN_DESCRIPTION + " LIKE ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{"%" + query + "%", "%" + query + "%"});
        
        if (cursor.moveToFirst()) {
            do {
                Product product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                product.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
                product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));
                product.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
                product.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
                product.setStock(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK)));
                
                products.add(product);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return products;
    }
    
    public Product getProduct(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_PRODUCTS, null, COLUMN_ID + " = ?", 
                new String[]{String.valueOf(id)}, null, null, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            Product product = new Product();
            product.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
            product.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
            product.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)));
            product.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE)));
            product.setImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_URL)));
            product.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY_ID)));
            product.setStock(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK)));
            
            cursor.close();
            return product;
        }
        
        if (cursor != null) {
            cursor.close();
        }
        
        return null;
    }

    // Category methods
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                category.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)));
                category.setIconUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ICON_URL)));
                
                categories.add(category);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return categories;
    }

    // Cart methods
    public long addToCart(CartItem cartItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Check if product already exists in cart
        Cursor cursor = db.query(TABLE_CART, new String[]{COLUMN_ID, COLUMN_QUANTITY}, 
                COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(cartItem.getProductId())}, 
                null, null, null);
        
        long cartItemId;
        
        if (cursor != null && cursor.moveToFirst()) {
            // Product exists, update quantity
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            int currentQuantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
            int newQuantity = currentQuantity + cartItem.getQuantity();
            
            ContentValues values = new ContentValues();
            values.put(COLUMN_QUANTITY, newQuantity);
            
            db.update(TABLE_CART, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            cartItemId = id;
        } else {
            // Product doesn't exist, insert new item
            ContentValues values = new ContentValues();
            values.put(COLUMN_PRODUCT_ID, cartItem.getProductId());
            values.put(COLUMN_PRODUCT_NAME, cartItem.getProductName());
            values.put(COLUMN_PRODUCT_PRICE, cartItem.getProductPrice());
            values.put(COLUMN_PRODUCT_IMAGE_URL, cartItem.getProductImageUrl());
            values.put(COLUMN_QUANTITY, cartItem.getQuantity());
            
            cartItemId = db.insert(TABLE_CART, null, values);
        }
        
        if (cursor != null) {
            cursor.close();
        }
        
        return cartItemId;
    }
    
    public List<CartItem> getCartItems() {
        List<CartItem> cartItems = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_CART;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                CartItem cartItem = new CartItem();
                cartItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                cartItem.setProductId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID)));
                cartItem.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME)));
                cartItem.setProductPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)));
                cartItem.setProductImageUrl(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_IMAGE_URL)));
                cartItem.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)));
                
                cartItems.add(cartItem);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return cartItems;
    }
    
    public int updateCartItemQuantity(int cartItemId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, quantity);
        
        return db.update(TABLE_CART, values, COLUMN_ID + " = ?", new String[]{String.valueOf(cartItemId)});
    }
    
    public int removeCartItem(int cartItemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CART, COLUMN_ID + " = ?", new String[]{String.valueOf(cartItemId)});
    }
    
    public int clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CART, null, null);
    }
    
    public double getCartTotal() {
        double total = 0;
        
        String selectQuery = "SELECT SUM(" + COLUMN_PRODUCT_PRICE + " * " + COLUMN_QUANTITY + ") as total FROM " + TABLE_CART;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getDouble(0);
            cursor.close();
        }
        
        return total;
    }

    // Chat messages methods
    public long addChatMessage(ChatMessage chatMessage) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE, chatMessage.getMessage());
        values.put(COLUMN_TYPE, chatMessage.getType());
        values.put(COLUMN_TIMESTAMP, chatMessage.getTimestamp());
        
        return db.insert(TABLE_CHAT_MESSAGES, null, values);
    }
    
    public List<ChatMessage> getChatMessages() {
        List<ChatMessage> chatMessages = new ArrayList<>();
        
        String selectQuery = "SELECT * FROM " + TABLE_CHAT_MESSAGES + " ORDER BY " + COLUMN_TIMESTAMP + " ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        if (cursor.moveToFirst()) {
            do {
                ChatMessage chatMessage = new ChatMessage();
                chatMessage.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                chatMessage.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE)));
                chatMessage.setType(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TYPE)));
                chatMessage.setTimestamp(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)));
                
                chatMessages.add(chatMessage);
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        return chatMessages;
    }
    
    public int clearChatMessages() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_CHAT_MESSAGES, null, null);
    }
}
