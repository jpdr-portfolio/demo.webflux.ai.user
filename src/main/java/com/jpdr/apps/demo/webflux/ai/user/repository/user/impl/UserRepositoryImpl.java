package com.jpdr.apps.demo.webflux.ai.user.repository.user.impl;

import com.jpdr.apps.demo.webflux.ai.user.exception.user.UserNotFoundException;
import com.jpdr.apps.demo.webflux.ai.user.exception.user.UserRepositoryException;
import com.jpdr.apps.demo.webflux.ai.user.repository.user.UserRepository;
import com.jpdr.apps.demo.webflux.ai.user.service.dto.user.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Repository
public class UserRepositoryImpl implements UserRepository {
  
  private final WebClient webClient;
  
  public UserRepositoryImpl(@Qualifier(value = "userWebClient") WebClient webClient ){
    this.webClient = webClient;
  }
  
  @Override
  public Mono<UserDto> getById (Long userId) {
    return this.webClient.get()
      .uri("/users/{userId}", userId)
      .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .retrieve()
      .onStatus(HttpStatus.NOT_FOUND::equals,
        response -> response.createException()
          .map(error -> new UserNotFoundException(userId,error)))
      .onStatus(HttpStatusCode::isError,
        response -> response.createException()
          .map(error -> new UserRepositoryException(userId, error))
        )
      .bodyToMono(UserDto.class);
  }
  
}
