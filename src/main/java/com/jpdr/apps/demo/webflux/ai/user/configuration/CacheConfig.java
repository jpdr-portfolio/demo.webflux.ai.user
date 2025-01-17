package com.jpdr.apps.demo.webflux.ai.user.configuration;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jpdr.apps.demo.webflux.ai.user.service.dto.product.ProductDto;
import com.jpdr.apps.demo.webflux.ai.user.service.dto.product.SubCategoryDto;
import com.jpdr.apps.demo.webflux.ai.user.service.dto.purchase.PurchaseDto;
import com.jpdr.apps.demo.webflux.commons.caching.DtoSerializer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@EnableCaching
@Configuration
public class CacheConfig {
  
  @Bean
  public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, ObjectMapper objectMapper){
    
    ObjectMapper mapper = objectMapper.copy()
      .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setVisibility(PropertyAccessor.ALL, Visibility.ANY);
    mapper.findAndRegisterModules();
    
    DtoSerializer<PurchaseDto> purchaseDtoDtoSerializer =
      new DtoSerializer<>(mapper, PurchaseDto.class);
    
    DtoSerializer<ProductDto> productDtoDtoSerializer =
      new DtoSerializer<>(mapper, ProductDto.class);
    
    StringRedisSerializer joinedSubCategoriesSerializer = new StringRedisSerializer(StandardCharsets.UTF_8);
    
    Class<List<ProductDto>> productListClass = (Class) List.class;
    DtoSerializer<List<ProductDto>> productDtoListSerializer =
      new DtoSerializer<>(mapper, productListClass);
    
    Class<List<SubCategoryDto>> subCategoriesListClass = (Class) List.class;
    DtoSerializer<List<SubCategoryDto>> subCategoryDtoListSerializer =
      new DtoSerializer<>(mapper, subCategoriesListClass);
    
    RedisSerializationContext.SerializationPair<PurchaseDto> purchaseDtoSerializationPair =
      RedisSerializationContext.SerializationPair.fromSerializer(purchaseDtoDtoSerializer);
    
    RedisSerializationContext.SerializationPair<ProductDto> productDtoSerializationPair =
      RedisSerializationContext.SerializationPair.fromSerializer(productDtoDtoSerializer);
    
    RedisSerializationContext.SerializationPair<List<ProductDto>> productDtoListSerializationPair =
      RedisSerializationContext.SerializationPair.fromSerializer(productDtoListSerializer);
    
    RedisSerializationContext.SerializationPair<String> joinedSubCategoriesSerializationPair =
      RedisSerializationContext.SerializationPair.fromSerializer(joinedSubCategoriesSerializer);
    
    RedisSerializationContext.SerializationPair<List<SubCategoryDto>> subCategoryDtoListSerializationPair =
      RedisSerializationContext.SerializationPair.fromSerializer(subCategoryDtoListSerializer);
    
    RedisCacheConfiguration purchaseCacheConfiguration =
      RedisCacheConfiguration.defaultCacheConfig()
      .serializeValuesWith(purchaseDtoSerializationPair);
    
    RedisCacheConfiguration productCacheConfiguration =
      RedisCacheConfiguration.defaultCacheConfig()
      .serializeValuesWith(productDtoSerializationPair);
    
    RedisCacheConfiguration productDtoListCacheConfiguration =
      RedisCacheConfiguration.defaultCacheConfig()
        .serializeValuesWith(productDtoListSerializationPair);
    
    RedisCacheConfiguration joinedSubCategoriesCacheConfig =
      RedisCacheConfiguration.defaultCacheConfig()
      .serializeValuesWith(joinedSubCategoriesSerializationPair);
    
    RedisCacheConfiguration subCategoryDtoListCacheConfiguration =
      RedisCacheConfiguration.defaultCacheConfig()
        .serializeValuesWith(subCategoryDtoListSerializationPair);
    
    return RedisCacheManager.builder(redisConnectionFactory)
      .withCacheConfiguration("purchases",purchaseCacheConfiguration)
      .withCacheConfiguration("products", productCacheConfiguration)
      .withCacheConfiguration("productsBySubCategory", productDtoListCacheConfiguration)
      .withCacheConfiguration("joinedSubCategoriesNames", joinedSubCategoriesCacheConfig)
      .withCacheConfiguration("subCategories", subCategoryDtoListCacheConfiguration)
      .build();
  }

}
