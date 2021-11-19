package com.vardelean.vendingmachine.api;

import com.vardelean.vendingmachine.dto.ProductDto;
import com.vardelean.vendingmachine.service.ProductService;
import com.vardelean.vendingmachine.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductResource {
  private final ProductService productService;
  private final JwtUtil jwtUtil;

  @GetMapping("/product/{productId}")
  public ResponseEntity<ProductDto> getProduct(@PathVariable Long productId) {
    return ResponseEntity.ok().body(productService.getProduct(productId));
  }

  @GetMapping("/products")
  public ResponseEntity<List<ProductDto>> getProducts() {
    return ResponseEntity.ok().body(productService.getProducts());
  }

  @PostMapping("/product")
  public ResponseEntity<ProductDto> addProduct(@RequestBody @Valid ProductDto productDto) {
    return ResponseEntity.ok().body(productService.saveProduct(productDto));
  }

  @PutMapping("/product/{productId}")
  public ResponseEntity<ProductDto> updateProduct(
      @RequestHeader(name = "Authorization") String authorizationHeader,
      @PathVariable Long productId,
      @RequestBody @Valid ProductDto productDto) {
    Optional<String> jwt = jwtUtil.extractToken(authorizationHeader);
    String username = jwtUtil.extractUsername(jwtUtil.decodeToken(jwt.get()));
    return ResponseEntity.ok().body(productService.updateProduct(username, productId, productDto));
  }

  @DeleteMapping("/product/{productId}")
  public void updateProduct(
      @RequestHeader(name = "Authorization") String authorizationHeader,
      @PathVariable Long productId) {
    Optional<String> jwt = jwtUtil.extractToken(authorizationHeader);
    String username = jwtUtil.extractUsername(jwtUtil.decodeToken(jwt.get()));
    productService.deleteProduct(username, productId);
  }
}
