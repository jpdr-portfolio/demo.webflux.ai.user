package com.jpdr.apps.demo.webflux.ai.user.service.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDto implements Serializable {
  
  @JsonInclude(Include.NON_NULL)
  Long id;
  @NonNull
  String productName;
  @JsonInclude(Include.NON_NULL)
  Long categoryId;
  @JsonInclude(Include.NON_NULL)
  String categoryName;
  @JsonInclude(Include.NON_NULL)
  Long subCategoryId;
  @JsonInclude(Include.NON_NULL)
  String subCategoryName;
  @JsonInclude(Include.NON_NULL)
  Long retailerId;
  @JsonInclude(Include.NON_NULL)
  String retailerName;
  @JsonInclude(Include.NON_NULL)
  Boolean isActive;
  @JsonInclude(Include.NON_EMPTY)
  String creationDate;
  @JsonInclude(Include.NON_EMPTY)
  String deletionDate;
  
}
