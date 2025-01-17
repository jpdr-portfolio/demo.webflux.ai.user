package com.jpdr.apps.demo.webflux.ai.user.service.dto.profile;

import com.jpdr.apps.demo.webflux.ai.user.service.dto.product.ProductDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SuggestedProductsDto {

  List<List<ProductDto>> suggestedProducts;
  
}
