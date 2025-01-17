package com.jpdr.apps.demo.webflux.ai.user.exception.purchase;

public class PurchaseNotFoundException extends RuntimeException{
  
  public PurchaseNotFoundException(Long purchaseId, Throwable ex){
    super("The purchase " + purchaseId + " wasn't found.", ex);
  }
  
}
