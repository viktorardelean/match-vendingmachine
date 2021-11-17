package com.vardelean.vendingmachine.api;

import com.vardelean.vendingmachine.dto.ProductDto;
import com.vardelean.vendingmachine.service.ProductService;
import javassist.tools.web.BadHttpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductResource {
  private final ProductService productService;

  @GetMapping("/product/{productId}")
  public ResponseEntity<ProductDto> getProduct(@PathVariable Long productId) throws BadHttpRequest {
    return ResponseEntity.ok().body(productService.getProduct(productId));
  }

  @GetMapping("/products")
  public ResponseEntity<List<ProductDto>> getProducts() {
    return ResponseEntity.ok().body(productService.getProducts());
  }

  @PostMapping("/product")
  public ResponseEntity<ProductDto> addProduct(@RequestBody ProductDto productDto)
      throws BadHttpRequest {
    return ResponseEntity.ok().body(productService.saveProduct(productDto));
  }

  @PutMapping("/product/{productId}")
  public ResponseEntity<ProductDto> updateProduct(
      @PathVariable Long productId, @RequestBody ProductDto productDto) {
    return ResponseEntity.ok().body(productService.updateProduct(productId, productDto));
  }

  @DeleteMapping("/product/{productId}")
  public void updateProduct(@PathVariable Long productId) {
    productService.deleteProduct(productId);
  }
}
