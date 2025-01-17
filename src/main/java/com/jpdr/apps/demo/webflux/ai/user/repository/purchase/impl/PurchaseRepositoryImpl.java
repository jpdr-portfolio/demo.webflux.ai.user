package com.jpdr.apps.demo.webflux.ai.user.repository.purchase.impl;

import com.jpdr.apps.demo.webflux.ai.user.exception.product.ProductRepositoryException;
import com.jpdr.apps.demo.webflux.ai.user.repository.purchase.PurchaseRepository;
import com.jpdr.apps.demo.webflux.ai.user.service.dto.purchase.PurchaseDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class PurchaseRepositoryImpl implements PurchaseRepository {
  
  private final WebClient webClient;
  
  @Value("${app.max-user-purchases}")
  private String maxUserPurchases;
  
  public PurchaseRepositoryImpl(@Qualifier(value = "purchaseWebClient") WebClient webClient ){
    this.webClient = webClient;
  }
  
  
  @Override
  public Mono<List<PurchaseDto>> getPurchasesByUserId(Long userId) {
    return this.webClient.get()
      .uri( uriBuilder -> uriBuilder
        .path("/purchases")
        .queryParam("limit", this.maxUserPurchases)
        .queryParam("userId", userId)
        .build())
      .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .retrieve()
      .onStatus(HttpStatusCode::isError,
        response -> response.createException()
          .map(error -> new ProductRepositoryException(userId, error))
      )
      .bodyToMono(new ParameterizedTypeReference<>(){});
  }
}
