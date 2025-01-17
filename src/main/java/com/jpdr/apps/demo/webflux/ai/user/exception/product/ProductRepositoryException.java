package com.jpdr.apps.demo.webflux.ai.user.exception.product;

public class ProductRepositoryException extends RuntimeException{
  
  public ProductRepositoryException(Long productId, Throwable ex){
    super("There was an error while retrieving the product " + productId, ex);
  }
  public ProductRepositoryException(Throwable ex){
    super("There was an error while calling the products service.", ex);
  }
  
  
}
