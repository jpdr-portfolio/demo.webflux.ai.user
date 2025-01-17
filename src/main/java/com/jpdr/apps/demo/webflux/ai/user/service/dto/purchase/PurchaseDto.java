package com.jpdr.apps.demo.webflux.ai.user.service.dto.purchase;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PurchaseDto implements Serializable {
  
  @JsonInclude(Include.NON_NULL)
  Long id;
  @JsonInclude(Include.NON_EMPTY)
  String purchaseDate;
  @JsonInclude(Include.NON_NULL)
  String purchaseDescription;
  @JsonInclude(Include.NON_NULL)
  String purchaseStatus;
  @JsonInclude(Include.NON_NULL)
  Long productId;
  @JsonInclude(Include.NON_NULL)
  String productName;
  @JsonInclude(Include.NON_NULL)
  Integer productQuantity;
  @JsonInclude(Include.NON_NULL)
  BigDecimal productUnitPrice;
  @JsonInclude(Include.NON_NULL)
  BigDecimal productTotalPrice;
  @JsonInclude(Include.NON_NULL)
  Long userId;
  @JsonInclude(Include.NON_NULL)
  String userEmail;
  @JsonInclude(Include.NON_NULL)
  String userAddress;
  @JsonInclude(Include.NON_NULL)
  String userCity;
  @JsonInclude(Include.NON_NULL)
  String userCountry;
  @JsonInclude(Include.NON_NULL)
  Long accountId;
  @JsonInclude(Include.NON_NULL)
  UUID accountNumber;
  @JsonInclude(Include.NON_NULL)
  String purchaseCancellationDate;
  
}
