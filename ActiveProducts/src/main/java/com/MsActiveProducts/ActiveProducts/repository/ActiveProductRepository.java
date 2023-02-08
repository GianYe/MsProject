package com.MsActiveProducts.ActiveProducts.repository;

import com.MsActiveProducts.ActiveProducts.entity.ActiveProduct;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ActiveProductRepository extends ReactiveMongoRepository<ActiveProduct, String> {
    Mono<ProductActive> findByIdentityContract(String identityContract);

    Mono<ProductActive> findByTypeCreditAndDocument(String typeCredit, String document);
}
