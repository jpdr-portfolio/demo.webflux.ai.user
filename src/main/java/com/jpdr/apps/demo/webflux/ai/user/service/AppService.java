package com.jpdr.apps.demo.webflux.ai.user.service;

import com.jpdr.apps.demo.webflux.ai.user.service.dto.profile.SuggestedProductsDto;
import reactor.core.publisher.Mono;

public interface AppService {
  
  Mono<SuggestedProductsDto> getSuggestedProducts(Long userId);
  
}
