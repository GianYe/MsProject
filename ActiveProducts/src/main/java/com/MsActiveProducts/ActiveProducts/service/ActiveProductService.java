package com.MsActiveProducts.ActiveProducts.service;

import com.MsActiveProducts.ActiveProducts.entity.Client;
import com.MsActiveProducts.ActiveProducts.entity.ActiveProduct;
import com.MsActiveProducts.ActiveProducts.repository.ActiveProductRepository;
import com.MsActiveProducts.ActiveProducts.web.mapper.ActiveProductMapper;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
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
public class ActiveProductService {

    @Autowired
    private ActiveProductRepository activeProductRepository;

    @Autowired
    private ActiveProductMapper activeProductMapper;


    private final String BASE_URL = "http://localhost:9040";
    //private final String BASE_URL = "https://spring-azure-github-laar.azurewebsites.net:443";

    TcpClient tcpClient = TcpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 600000)
            .doOnConnected(connection ->
                    connection.addHandlerLast(new ReadTimeoutHandler(60))
                            .addHandlerLast(new WriteTimeoutHandler(60)));
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



    public Flux<ActiveProduct> findAll(){
        log.debug("findAll executed");
        return activeProductRepository.findAll();
    }

    public Mono<ActiveProduct> findById(String activeProductId){
        log.debug("findById executed {}" , activeProductId);
        return activeProductRepository.findById(activeProductId);
    }

    public Mono<ActiveProduct> findByIdentityContract(String identityContract){
        log.debug("findByIdentityContract executed {}" , identityContract);
        return activeProductRepository.findByIdentityContract(identityContract);
    }

    public Mono<ActiveProduct> findByTypeCreditAndDocument(String typeCredit, String document){
        log.debug("findByTypeCreditAndDocument executed {}" , typeCredit, document);
        return activeProductRepository.findByTypeCreditAndDocument(typeCredit, document);
    }


    public Mono<ActiveProduct> create(ActiveProduct activeProduct){
        log.debug("create executed {}", activeProduct);
        Mono<Client> client = findClientByDNI(activeProduct.getDocument());
        return client.switchIfEmpty(Mono.error(new Exception("Client Not Found" + activeProduct.getDocument())))
                .flatMap(client1 -> {
                    if(client1.getTypeClient().equals("EMPRESARIAL")){
                        return findByTypeCreditAndDocument(activeProduct.getTypeCredit(),activeProduct.getDocument())
                                .flatMap(account1 -> {
                                    if(account1.getTypeCredit().equals("PERSONAL")){
                                        return Mono.error(new Exception("No puede realizar más de un registro Personal."));
                                    }
                                    else if(account1.getTypeCredit().equals("PYME")){
                                        return Mono.error(new Exception("El cliente debe tener una tarjeta de crédito asociado al banco"));
                                    }
                                    else{
                                        return activeProductRepository.save(activeProduct);
                                    }
                                }).switchIfEmpty(activeProductRepository.save(activeProduct));
                    }
                    else
                    {
                            return Mono.error(new Exception("El Producto Activo que desea registrar, tiene asociado un DNI de tipo cliente personal" + client1.getTypeClient()));
                    }
                });
    }


    public Mono<ActiveProduct> update(String activeProductId, activeProduct activeProduct){
        log.debug("update executed {}:{}", activeProductId, activeProduct);
        return activeProductRepository.findById(activeProductId)
                .flatMap(dbActiveProduct -> {
                    activeProductMapper.update(dbActiveProduct, activeProduct);
                    return activeProductRepository.save(dbActiveProduct);
                });
    }

    public Mono<ActiveProduct>delete(String activeProductId){
        log.debug("delete executed {}",activeProductId);
        return activeProductRepository.findById(activeProductId)
                .flatMap(existingActiveProduct -> activeProductRepository.delete(existingActiveProduct)
                        .then(Mono.just(existingActiveProduct)));
    }




}
