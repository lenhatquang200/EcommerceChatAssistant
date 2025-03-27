package com.example.ecommerce.utils;

public class Constants {
    // Database constants
    public static final String DATABASE_NAME = "ecommerce.db";
    public static final int DATABASE_VERSION = 1;
    
    // Shared preferences
    public static final String PREFS_NAME = "ecommerce_prefs";
    public static final String PREF_USER_FIRST_TIME = "user_first_time";
    
    // Intent extras
    public static final String EXTRA_PRODUCT_ID = "product_id";
    public static final String EXTRA_CATEGORY_ID = "category_id";
    public static final String EXTRA_SEARCH_QUERY = "search_query";
    
    // Request codes
    public static final int REQUEST_CODE_CHECKOUT = 1001;
    
    // Grid and list spans
    public static final int GRID_SPAN_COUNT = 2;
    
    // Animation durations
    public static final int ANIM_DURATION_SHORT = 200;
    public static final int ANIM_DURATION_MEDIUM = 400;
    public static final int ANIM_DURATION_LONG = 800;
    
    // API constants
    public static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/";
    
    // Placeholder image paths
    public static final String PLACEHOLDER_PRODUCT = "placeholder_product";
    public static final String PLACEHOLDER_CATEGORY = "placeholder_category";
    
    // Error messages
    public static final String ERROR_NETWORK = "Network error. Please check your connection.";
    public static final String ERROR_SERVER = "Server error. Please try again later.";
    public static final String ERROR_UNKNOWN = "Unknown error occurred. Please try again.";
}
