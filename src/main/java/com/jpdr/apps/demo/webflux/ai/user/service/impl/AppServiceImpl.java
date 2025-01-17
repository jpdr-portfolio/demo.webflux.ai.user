package com.jpdr.apps.demo.webflux.ai.user.service.impl;

import com.jpdr.apps.demo.webflux.ai.user.repository.product.ProductRepository;
import com.jpdr.apps.demo.webflux.ai.user.repository.purchase.PurchaseRepository;
import com.jpdr.apps.demo.webflux.ai.user.repository.user.UserRepository;
import com.jpdr.apps.demo.webflux.ai.user.service.AppService;
import com.jpdr.apps.demo.webflux.ai.user.service.dto.profile.SuggestedProductsDto;
import com.jpdr.apps.demo.webflux.ai.user.service.dto.purchase.PurchaseDto;
import com.jpdr.apps.demo.webflux.ai.user.service.dto.user.UserDto;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppServiceImpl implements AppService {
  
  private final OllamaChatModel ollamaChatModel;
  private final UserRepository userRepository;
  private final PurchaseRepository purchaseRepository;
  private final ProductRepository productRepository;
  
  @Override
  public Mono<SuggestedProductsDto> getSuggestedProducts(Long userId) {
    return Mono.zip(
        Mono.from(this.userRepository.getById(userId)),
        Mono.from(this.purchaseRepository.getPurchasesByUserId(userId)),
        Mono.from(this.productRepository.getAllJoinedSubCategoriesNames()))
      .flatMap(tuple ->
        Mono.zip(
          Mono.from(getSystemMessage()),
          Mono.from(getUserMessage(tuple))))
      .map(tuple ->
        new Prompt(List.of(tuple.getT1(), tuple.getT2())))
      .flatMap(this::callModel)
      .map(response -> response.replace("\\n", ""))
      .flatMapIterable(response -> Arrays.asList(response.split(",")))
      .take(3L)
      .map(String::strip)
      .map(idAndSubCategory -> Long.parseLong(idAndSubCategory.split(":")[0]))
      .flatMap(this.productRepository::findAllProductsBySubCategoryId)
      .collectList()
      .map(productsLists -> SuggestedProductsDto.builder()
        .suggestedProducts(productsLists)
        .build()
      );
  }
  
  private Mono<SystemMessage> getSystemMessage(){
    return Mono.defer(() -> Mono.just(
      new SystemMessage("""
          You are an assistant. You must return JUST THE ID AND THE CATEGORY JOINED BY ":" AND SEPARATE BY COMMA EACH ITEM. NOTHING ELSE. DON'T SUGGEST elements from other sources, ONLY FROM THE GIVEN PROMPT. Keep your response in one sentence. Return exactly 3 ITEMS.
          Considering the following data:
        """)))
      .doOnNext(data -> log.info("System Message: " + data));
  }
  
  private Mono<UserMessage> getUserMessage(Tuple3<UserDto,List<PurchaseDto>, String> userTuple){
    return Mono.defer(() -> Mono.just(userTuple))
      .flatMap(tuple ->
        Mono.zip(
          Mono.from(getUserPrompt(tuple.getT1())),
          Mono.from(getPurchasesPrompt(tuple.getT2())),
          Mono.from(getSubCategoriesPrompt(tuple.getT3())),
          Mono.from(getQuestionPrompt())))
      .map(tuple -> new UserMessage(
        tuple.getT1() + "\n" + tuple.getT2() + "\n" + tuple.getT3() + tuple.getT4()))
      .doOnNext(data -> log.info("User Message: " + data));
  }
  
  private Mono<String> getUserPrompt(UserDto userDto){
    return Mono.defer(() -> Mono.fromCallable(() -> {
      Long userAge = ChronoUnit.YEARS.between(
        LocalDate.parse(userDto.getBirthDate()), LocalDate.now());
      return " TARGET_PERSON: A person with the following " +
        "characteristics separated by comma ending in dot: \n" +
        "   person age: " + userAge + ", person gender: " + userDto.getGender() +
        ", person location: " + userDto.getCity() + ", " + userDto.getCountry();
      }));
  }
  
  private Mono<String> getPurchasesPrompt(List<PurchaseDto> purchaseDtos){
    return Mono.defer(() -> Flux.fromIterable(purchaseDtos)
      .flatMap(purchaseDto -> this.productRepository.getById(purchaseDto.getId()))
      .map(productDto ->
        productDto.getSubCategoryName() + " - '" + productDto.getProductName() +"'")
      .collect(Collectors.joining(", ")))
      .map(purchases ->
        "PURCHASES_LIST: A comma separated list of recent product purchases ending in dot: \n" +
        purchases + ".");
  }
  
  private Mono<String> getSubCategoriesPrompt(String subCategories){
    return Mono.defer(() -> Mono.just(
      "CATEGORIES_LIST: A comma separated list of available product categories ending in dot. Each ITEM is formated ID:CATEGORY NAME: \n" +
        subCategories + "."));
  }
  
  private Mono<String> getQuestionPrompt(){
    return Mono.defer(() -> Mono.just(
      "QUESTION: I need you to propose EXACTLY 3 categories (NO MORE, NO LESS) from CATEGORIES_LIST " +
        "that could be interesting to a person like TARGET_PERSON " +
        "and could be complementary to the purchases from PURCHASES_LIST. " +
        "EXCLUDE CATEGORIES ALREADY PRESENT IN PURCHASES_LIST. " +
        "DON'T INCLUDE CATEGORIES FROM OTHER SOURCES. DON'T REPEAT CATEGORIES. " +
        "You must return JUST THE ID AND THE CATEGORY JOINED BY \":\" AND SEPARATE BY COMMA EACH ITEM. "));
  }
  
  private Mono<String> callModel(Prompt prompt){
    return Mono.defer(() -> Mono.fromCallable(() -> this.ollamaChatModel.call(prompt)
      .getResult()
      .getOutput()
      .getContent())
        .retryWhen(Retry.backoff(3, Duration.ofSeconds(10))
          .filter(ReadTimeoutException.class::isInstance)
          .filter(IOException.class::isInstance)
          .filter(ResourceAccessException.class::isInstance)))
      .doOnNext(data -> log.info("Model Response: " + data));
  }
  
}
