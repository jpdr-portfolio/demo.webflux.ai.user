package com.jpdr.apps.demo.webflux.ai.user.exception.user;

public class UserNotFoundException extends RuntimeException{
  
  public UserNotFoundException(long userId, Throwable ex){
    super("User "+ userId +" not found", ex);
  }
  
}
