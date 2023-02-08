package com.MsClients.Clients.repository;

import com.MsClients.Clients.entity.Client;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
@Repository
//@RequiredArgsConstructor
public interface ClientRepository extends ReactiveMongoRepository<Client, String> {

    Mono<Client> findByIdentityDni(String identityDni);


}
