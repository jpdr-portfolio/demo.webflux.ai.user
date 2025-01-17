package com.jpdr.apps.demo.webflux.ai.user.repository.purchase;

import com.jpdr.apps.demo.webflux.ai.user.repository.BaseRepository;
import com.jpdr.apps.demo.webflux.ai.user.service.dto.purchase.PurchaseDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface PurchaseRepository {
  
  Mono<List<PurchaseDto>> getPurchasesByUserId(Long userId);
  
}
