package com.MsPassiveProducts.PassiveProducts.service;

import com.MsPassiveProducts.PassiveProducts.entity.Client;
import com.MsPassiveProducts.PassiveProducts.entity.PassiveProduct;
import com.MsPassiveProducts.PassiveProducts.repository.PassiveProductRepository;
import com.MsPassiveProducts.PassiveProducts.web.mapper.PassiveProductMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@AllArgsConstructor
public class PassiveProductService {

    @Autowired
    private PassiveProductRepository passiveProductRepository;

    @Autowired
    private PassiveProductMapper passiveProductMapper;

    /*public PassiveProductService(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.baseUrl("http://localhost:9040/v1/client").build();
    }*/

    private final String BASE_URL = "http://localhost:9040";

    TcpClient tcpClient = TcpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
            .doOnConnected(connection ->
                    connection.addHandlerLast(new ReadTimeoutHandler(3))
                            .addHandlerLast(new WriteTimeoutHandler(3)));
    private final WebClient client = WebClient.builder()
            .baseUrl(BASE_URL)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))  // timeout
            .build();

    public Mono<Client> findClientByDNI(String identityDni){
        return this.client.get().uri("/v1/client/findByIdentityDni/{identityDni}",identityDni)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(Client.class);
    }


    public Flux<PassiveProduct> findAll(){
        log.debug("findAll executed");
        return passiveProductRepository.findAll();
    }

    public Mono<PassiveProduct> findById(String passiveProductId){
        log.debug("findById executed {}" , passiveProductId);
        return passiveProductRepository.findById(passiveProductId);
    }

    public Mono<PassiveProduct> findByIdentityAccount(String identityAccount){
        log.debug("findByIdentityAccount executed {}" , identityAccount);
        return passiveProductRepository.findByIdentityAccount(identityAccount);
    }

    public Mono<PassiveProduct> findByTypeAccountAndDocument(String typeAccount, String document){
        log.debug("findByTypeAccountAndDocument executed {}" , typeAccount, document);
        return passiveProductRepository.findByTypeAccountAndDocument(typeAccount, document);
    }


    public Mono<PassiveProduct> create(PassiveProduct passiveProduct){
        log.debug("create executed {}",passiveProduct);

        Mono<Client> client = findClientByDNI(passiveProduct.getDocument());

        log.debug("findClientByDNI executed {}" , client);
        log.info("findClientByDNI executed {}" , client);
        System.out.println("client " +client);


        return client.switchIfEmpty(Mono.error(new Exception("Client Not Found" + passiveProduct.getDocument())))
                .flatMap(client1 -> {
                    if(client1.getTypeClient().equals("PERSONAL")){
                        return findByTypeAccountAndDocument(passiveProduct.getTypeAccount(),passiveProduct.getDocument())
                                .flatMap(account1 -> {
                                    if(account1.getTypeAccount().equals("AHORRO") || account1.getTypeAccount().equals("CORRIENTE"))
                                    {
                                        return Mono.error(new Exception("No puede tener más de una cuenta de AHORRO O CORRIENTE" + passiveProduct.getTypeAccount()));
                                    }else if(account1.getTypeAccount().equals("VIP"))
                                    {
                                        return Mono.error(new Exception("Cliente debe tener una tarjeta de crédito con el banco al momento de creación de cuenta" + passiveProduct.getTypeAccount()));
                                    }
                                    else
                                    {
                                        return passiveProductRepository.save(passiveProduct);
                                    }
                                }).switchIfEmpty(passiveProductRepository.save(passiveProduct));
                    }
                    else
                    {
                        if(passiveProduct.getTypeAccount().equals("CORRIENTE")){
                            return passiveProductRepository.save(passiveProduct);
                        }else{
                            return Mono.error(new Exception("No puede tener una cuenta de AHORRO O PLAZO FIJO" + passiveProduct.getTypeAccount()));
                        }
                    }

                });

    }

    public Mono<PassiveProduct> update(String passiveProductId, PassiveProduct passiveProduct){
        log.debug("update executed {}:{}", passiveProductId, passiveProduct);
        return passiveProductRepository.findById(passiveProductId)
                .flatMap(dbPassiveProduct -> {
                    passiveProductMapper.update(dbPassiveProduct, passiveProduct);
                    return passiveProductRepository.save(dbPassiveProduct);
                });
    }

    public Mono<PassiveProduct>delete(String passiveProductId){
        log.debug("delete executed {}",passiveProductId);
        return passiveProductRepository.findById(passiveProductId)
                .flatMap(existingPassiveProduct -> passiveProductRepository.delete(existingPassiveProduct)
                        .then(Mono.just(existingPassiveProduct)));
    }


}
