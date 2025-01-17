package com.jpdr.apps.demo.webflux.ai.user.exception.user;

public class UserRepositoryException extends RuntimeException{
  
  public UserRepositoryException(Long userId, Throwable ex){
    super("An error occurred while retrieving data for user " + userId +".",ex);
  }
  
}
