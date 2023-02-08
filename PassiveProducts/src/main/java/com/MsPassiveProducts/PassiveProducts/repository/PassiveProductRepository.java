package com.MsPassiveProducts.PassiveProducts.repository;

import com.MsPassiveProducts.PassiveProducts.entity.PassiveProduct;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface PassiveProductRepository extends ReactiveMongoRepository<PassiveProduct, String> {
    Mono<PassiveProduct> findByIdentityAccount(String identityAccount);

    Mono<PassiveProduct> findByTypeAccountAndDocument(String typeAccount, String document);

}
