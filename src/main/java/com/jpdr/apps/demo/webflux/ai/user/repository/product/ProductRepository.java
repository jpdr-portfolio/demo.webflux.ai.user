package com.jpdr.apps.demo.webflux.ai.user.repository.product;

import com.jpdr.apps.demo.webflux.ai.user.repository.BaseRepository;
import com.jpdr.apps.demo.webflux.ai.user.service.dto.product.ProductDto;
import com.jpdr.apps.demo.webflux.ai.user.service.dto.product.SubCategoryDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductRepository extends BaseRepository<ProductDto, Long> {
  
  Mono<List<SubCategoryDto>> findAllSubCategories();
  Mono<List<ProductDto>> findAllProductsBySubCategoryId(Long subCategoryId);
  Mono<String> getAllJoinedSubCategoriesNames();
  
}
