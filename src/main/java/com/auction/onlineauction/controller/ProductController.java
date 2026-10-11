package com.auction.onlineauction.controller;

import com.auction.onlineauction.entity.Product;
import com.auction.onlineauction.entity.User;
import com.auction.onlineauction.repository.UserRepository;
import com.auction.onlineauction.service.ProductService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final UserRepository userRepository;

    public ProductController(
            ProductService productService,
            UserRepository userRepository) {

        this.productService = productService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(
            @RequestBody Product product,
            Authentication authentication) {

        String email = authentication.getName();

        User seller = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Seller not found"));

        product.setSellerId(seller.getId());

        return ResponseEntity.ok(
                productService.addProduct(product)
        );
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(
            @PathVariable Long id) {

        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product) {

        return productService.getProductById(id)
                .map(existingProduct -> {

                    existingProduct.setName(product.getName());
                    existingProduct.setDescription(product.getDescription());
                    existingProduct.setCategory(product.getCategory());
                    existingProduct.setImageUrl(product.getImageUrl());
                    existingProduct.setStartingPrice(product.getStartingPrice());

                    return ResponseEntity.ok(
                            productService.addProduct(existingProduct)
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }
}