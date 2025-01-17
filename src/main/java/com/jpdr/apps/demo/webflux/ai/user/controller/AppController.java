package com.jpdr.apps.demo.webflux.ai.user.controller;

import com.jpdr.apps.demo.webflux.ai.user.service.AppService;
import com.jpdr.apps.demo.webflux.ai.user.service.dto.profile.SuggestedProductsDto;
import com.jpdr.apps.demo.webflux.eventlogger.component.EventLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AppController {
  
  private final AppService appService;
  private final EventLogger eventLogger;
  
  @GetMapping("/suggested-products/{userId}")
  public Mono<ResponseEntity<SuggestedProductsDto>> getSuggestedProducts(
    @PathVariable(name = "userId") Long userId){
    return this.appService.getSuggestedProducts(userId)
      .doOnNext(suggestedProductsDto ->
        this.eventLogger.logEvent("getSuggestedProducts", suggestedProductsDto))
      .map(suggestedProductsDto ->
        new ResponseEntity<>(suggestedProductsDto, HttpStatus.OK));
  }
  
}
