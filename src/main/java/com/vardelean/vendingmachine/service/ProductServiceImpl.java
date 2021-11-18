package com.vardelean.vendingmachine.service;

import com.vardelean.vendingmachine.converter.ProductConverter;
import com.vardelean.vendingmachine.dto.ProductDto;
import com.vardelean.vendingmachine.model.Product;
import com.vardelean.vendingmachine.model.VendingMachineUser;
import com.vardelean.vendingmachine.repo.ProductRepo;
import com.vardelean.vendingmachine.repo.VendingMachineUserRepo;
import com.vardelean.vendingmachine.util.HttpUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.vardelean.vendingmachine.config.SecurityConfig.ROLE_SELLER;
import static com.vardelean.vendingmachine.util.ErrorMessages.*;

@RequiredArgsConstructor
@Slf4j
@Transactional
@Service
public class ProductServiceImpl implements ProductService {

  private final VendingMachineUserRepo vendingMachineUserRepo;
  private final ProductRepo productRepo;
  private final ProductConverter productConverter;
  private final HttpUtil httpUtil;

  @Override
  public ProductDto saveProduct(@NonNull ProductDto productDto) {
    log.info("Save product to DB: {}", productDto);
    Optional<VendingMachineUser> sellerOptional =
        vendingMachineUserRepo.findById(productDto.getSellerId());
    return sellerOptional
        .map(
            seller -> {
              Product product = productConverter.toEntity(productDto);
              if (isSeller(seller)) {
                product.setSeller(seller);
                product = productRepo.save(product);
                return productConverter.toDto(product);
              } else {
                throw httpUtil.badRequest(ERROR_CANNOT_ASSIGN_PRODUCT_TO_BUYER, seller.getId());
              }
            })
        .orElseThrow(() -> httpUtil.badRequest(ERROR_INVALID_USER_ID, productDto.getSellerId()));
  }

  @Override
  public ProductDto getProduct(@NonNull Long productId) {
    log.info("Read product from DB: {}", productId);
    return productConverter.toDto(getProductEntity(productId));
  }

  @Override
  public Product getProductEntity(@NonNull Long productId) {
    log.info("Read product from DB: {}", productId);
    Optional<Product> product = productRepo.findById(productId);
    return product.orElseThrow(() -> httpUtil.badRequest(ERROR_INVALID_PRODUCT_ID, productId));
  }

  @Override
  public List<ProductDto> getProducts() {
    log.info("Read products from DB");
    return productRepo.findAll().stream().map(productConverter::toDto).collect(Collectors.toList());
  }

  @Override
  public ProductDto updateProduct(@NonNull Long productId, ProductDto productDto) {
    log.info("Update product : {}", productId);
    Optional<Product> productOptional = productRepo.findById(productId);
    return productOptional
        .map(
            product -> {
              productConverter.toEntity(productDto, product);
              Optional<VendingMachineUser> sellerOptional =
                  vendingMachineUserRepo.findById(productDto.getSellerId());
              return sellerOptional
                  .map(
                      seller -> {
                        if (isSeller(seller)) {
                          product.setSeller(seller);
                          return productConverter.toDto(product);
                        } else {
                          throw httpUtil.badRequest(
                              ERROR_CANNOT_ASSIGN_PRODUCT_TO_BUYER, seller.getId());
                        }
                      })
                  .orElseThrow(
                      () -> httpUtil.badRequest(ERROR_INVALID_USER_ID, productDto.getSellerId()));
            })
        .orElseThrow(() -> httpUtil.badRequest(ERROR_INVALID_PRODUCT_ID, productId));
  }

  @Override
  public void deleteProduct(@NonNull Long productId) {
    log.info("Delete product : {}", productId);
    productRepo.deleteById(productId);
  }

  private boolean isSeller(VendingMachineUser seller) {
    return seller.getRoles().stream().anyMatch(role -> ROLE_SELLER.equals(role.getName()));
  }
}
