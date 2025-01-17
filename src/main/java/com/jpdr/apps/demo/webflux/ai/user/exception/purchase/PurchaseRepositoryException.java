package com.jpdr.apps.demo.webflux.ai.user.exception.purchase;

public class PurchaseRepositoryException extends RuntimeException{
  
  public PurchaseRepositoryException(Long purchaseId, Throwable ex){
    super("There was an error while retrieving the purchase " + purchaseId, ex);
  }
  
}
