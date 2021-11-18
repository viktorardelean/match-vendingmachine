package com.vardelean.vendingmachine.service;

import com.vardelean.vendingmachine.converter.ProductConverter;
import com.vardelean.vendingmachine.dto.ProductDto;
import com.vardelean.vendingmachine.model.Product;
import com.vardelean.vendingmachine.model.VendingMachineUser;
import com.vardelean.vendingmachine.repo.ProductRepo;
import com.vardelean.vendingmachine.repo.VendingMachineUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.vardelean.vendingmachine.config.SecurityConfig.ROLE_SELLER;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class ProductServiceImpl implements ProductService {

  private final VendingMachineUserRepo vendingMachineUserRepo;
  private final ProductRepo productRepo;
  private final ProductConverter productConverter;

  @Override
  public ProductDto saveProduct(@NonNull ProductDto productDto) {
    Product product = productConverter.toEntity(productDto);
    if (productDto.getSellerId() == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sellerId must not be null!");
    }
    VendingMachineUser seller = vendingMachineUserRepo.getById(productDto.getSellerId());
    if (isNotSeller(seller)) {
      throw badRequest(seller.getId(), "Cannot assign a product to a user that is not a seller: ");
    }
    product.setSeller(seller);

    log.info("Save product to DB: {}", product);
    product = productRepo.save(product);
    return productConverter.toDto(product);
  }

  @Override
  public ProductDto getProduct(@NonNull Long productId) {
    log.info("Read product from DB: {}", productId);
    Optional<Product> product = productRepo.findById(productId);
    return product
        .map(productConverter::toDto)
        .orElseThrow(() -> badRequest(productId, "Product does not exists: "));
  }

  @Override
  public List<ProductDto> getProducts() {
    log.info("Read products from DB");
    return productRepo.findAll().stream().map(productConverter::toDto).collect(Collectors.toList());
  }

  @Override
  public ProductDto updateProduct(Long productId, ProductDto productDto) {
    log.info("Update product : {}", productId);
    Optional<Product> productOptional = productRepo.findById(productId);
    return productOptional
        .map(
            product -> {
              productConverter.toEntity(productDto, product);
              VendingMachineUser seller = vendingMachineUserRepo.getById(productDto.getSellerId());
              if (isNotSeller(seller)) {
                throw badRequest(
                    seller.getId(), "Cannot assign a product to a user that is not a seller: ");
              }
              product.setSeller(seller);
              return productConverter.toDto(product);
            })
        .orElseThrow(() -> badRequest(productId, "Product does not exists: "));
  }

  @Override
  public void deleteProduct(Long productId) {
    log.info("Delete product : {}", productId);
    productRepo.deleteById(productId);
  }

  private RuntimeException badRequest(Long productId, String message) {
    log.error(message + "{}", productId);
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message + productId);
  }

  private boolean isNotSeller(VendingMachineUser seller) {
    return seller.getRoles().stream().anyMatch(role -> ROLE_SELLER.equals(role.getName()));
  }
}
