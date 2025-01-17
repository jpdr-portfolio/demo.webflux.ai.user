package com.jpdr.apps.demo.webflux.ai.user.repository.product.impl;

import com.jpdr.apps.demo.webflux.ai.user.exception.product.ProductNotFoundException;
import com.jpdr.apps.demo.webflux.ai.user.exception.product.ProductRepositoryException;
import com.jpdr.apps.demo.webflux.ai.user.repository.product.ProductRepository;
import com.jpdr.apps.demo.webflux.ai.user.service.dto.product.ProductDto;
import com.jpdr.apps.demo.webflux.ai.user.service.dto.product.SubCategoryDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ProductRepositoryImpl implements ProductRepository {
  
  private final WebClient webClient;
  
  public ProductRepositoryImpl(@Qualifier(value = "productWebClient") WebClient webClient ){
    this.webClient = webClient;
  }
  
  @Override
  @Cacheable(key = "#productId", value = "products", sync = true)
  public Mono<ProductDto> getById (Long productId) {
    return this.webClient.get()
      .uri("/products/{productId}", productId)
      .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .retrieve()
      .onStatus(HttpStatus.NOT_FOUND::equals,
        response -> response.createException()
          .map(error -> new ProductNotFoundException(productId,error)))
      .onStatus(HttpStatusCode::isError,
        response -> response.createException()
          .map(error -> new ProductRepositoryException(productId, error))
      )
      .bodyToMono(ProductDto.class);
  }
  
  @Override
  @Cacheable(value = "subCategories", sync = true)
  public Mono<List<SubCategoryDto>> findAllSubCategories() {
    return this.webClient.get()
      .uri("/subcategories")
      .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .retrieve()
      .onStatus(HttpStatusCode::isError,
        response -> response.createException()
          .map(error -> new ProductRepositoryException(error))
      )
      .bodyToMono(new ParameterizedTypeReference<>() {});
  }
  
  @Override
  @Cacheable(key = "#subCategoryId", value = "productsBySubCategory", sync = true)
  public Mono<List<ProductDto>> findAllProductsBySubCategoryId(Long subCategoryId) {
    return this.webClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/products")
        .queryParam("subCategoryId", subCategoryId)
        .build()
      )
      .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .retrieve()
      .onStatus(HttpStatusCode::isError,
        response -> response.createException()
          .map(error -> new ProductRepositoryException(error))
      )
      .bodyToMono(new ParameterizedTypeReference<>() {});
  }
  
  @Override
  @Cacheable(value = "joinedSubCategoriesNames")
  public Mono<String> getAllJoinedSubCategoriesNames() {
    return this.findAllSubCategories()
      .flatMapIterable(subCategoryDtos -> subCategoryDtos)
      .map(subCategoryDto -> subCategoryDto.getId() + ":" +
        subCategoryDto.getSubCategoryName())
      .collect(Collectors.joining(", "))
      .map(subCategories -> subCategories + ". ");
  }
  
  
}
