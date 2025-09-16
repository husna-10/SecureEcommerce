package com.ecommerce.backend.config;

import com.ecommerce.backend.entity.Product;
import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.enums.Role;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing sample data...");
        
        initializeUsers();
        initializeProducts();
        
        log.info("Sample data initialization completed!");
    }

    private void initializeUsers() {
        if (userRepository.count() == 0) {
            log.info("Creating default users...");
            
            // Create admin user
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setUsername("admin");
            admin.setEmail("admin@ecommerce.com");
            admin.setPassword(passwordEncoder.encode("admin1234"));
            admin.setRole(Role.ADMIN);
            admin.setEnabled(true);
            admin.setAccountNonExpired(true);
            admin.setAccountNonLocked(true);
            admin.setCredentialsNonExpired(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            userRepository.save(admin);
            
            // Create regular user
            User user = new User();
            user.setFirstName("Test");
            user.setLastName("User");
            user.setUsername("user");
            user.setEmail("user@ecommerce.com");
            user.setPassword(passwordEncoder.encode("user1234"));
            user.setRole(Role.USER);
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            
            log.info("Default users created successfully!");
        }
    }

    private void initializeProducts() {
        if (productRepository.count() == 0) {
            log.info("Creating sample products...");
            
            // Electronics
            createProduct("iPhone 14 Pro", "Latest iPhone with advanced camera system", 
                    new BigDecimal("999.99"), "Electronics", 50, "Apple", "IPH-14-PRO");
            
            createProduct("Samsung Galaxy S23", "Flagship Android smartphone with excellent display", 
                    new BigDecimal("849.99"), "Electronics", 45, "Samsung", "SGS-23");
            
            createProduct("MacBook Pro M2", "Powerful laptop for professionals", 
                    new BigDecimal("1299.99"), "Electronics", 25, "Apple", "MBP-M2");
            
            createProduct("Sony WH-1000XM4", "Premium noise-canceling wireless headphones", 
                    new BigDecimal("349.99"), "Electronics", 75, "Sony", "WH-1000XM4");
            
            // Clothing
            createProduct("Classic Denim Jacket", "Timeless denim jacket for casual wear", 
                    new BigDecimal("79.99"), "Clothing", 100, "Levi's", "DJ-CLASSIC");
            
            createProduct("Cotton T-Shirt", "Comfortable cotton t-shirt available in multiple colors", 
                    new BigDecimal("19.99"), "Clothing", 200, "Uniqlo", "CT-BASIC");
            
            createProduct("Running Sneakers", "Lightweight running shoes for athletes", 
                    new BigDecimal("129.99"), "Clothing", 80, "Nike", "RUN-SNKR");
            
            // Home & Garden
            createProduct("Coffee Maker", "Automatic drip coffee maker with timer", 
                    new BigDecimal("89.99"), "Home & Garden", 40, "Cuisinart", "CM-AUTO");
            
            createProduct("Succulent Plant Set", "Collection of 6 easy-care succulent plants", 
                    new BigDecimal("29.99"), "Home & Garden", 60, "GreenThumb", "SUCC-SET6");
            
            createProduct("Yoga Mat", "Non-slip yoga mat perfect for home workouts", 
                    new BigDecimal("39.99"), "Sports", 120, "Manduka", "YOGA-MAT");
            
            // Books
            createProduct("The Art of Programming", "Comprehensive guide to software development", 
                    new BigDecimal("49.99"), "Books", 30, "TechBooks", "ART-PROG");
            
            createProduct("Modern JavaScript", "Learn modern JavaScript techniques and frameworks", 
                    new BigDecimal("39.99"), "Books", 25, "WebDev Press", "MOD-JS");
            
            log.info("Sample products created successfully!");
        }
    }

    private void createProduct(String name, String description, BigDecimal price, 
                             String category, int stock, String brand, String sku) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setCategory(category);
        product.setStockQuantity(stock);
        product.setBrand(brand);
        product.setSku(sku);
        product.setActive(true);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        // Add some sample image URLs
        product.setImageUrl("https://via.placeholder.com/400x300?text=" + 
                name.replace(" ", "+"));
        
        productRepository.save(product);
    }
}