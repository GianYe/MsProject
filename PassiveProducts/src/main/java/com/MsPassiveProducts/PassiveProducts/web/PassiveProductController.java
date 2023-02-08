package com.MsPassiveProducts.PassiveProducts.web;

import com.MsPassiveProducts.PassiveProducts.entity.PassiveProduct;
import com.MsPassiveProducts.PassiveProducts.service.PassiveProductService;
import com.MsPassiveProducts.PassiveProducts.web.mapper.PassiveProductMapper;
import com.MsPassiveProducts.PassiveProducts.web.model.PassiveProductModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/passiveProduct")
public class PassiveProductController {

    @Value("${spring.application.name}")
    String name;

    @Value("${server.port}")
    String port;

    @Autowired
    private PassiveProductService passiveProductService;


    @Autowired
    private PassiveProductMapper passiveProductMapper;


    @GetMapping("/findAll")
    public Mono<ResponseEntity<Flux<PassiveProductModel>>> getAll(){
        log.info("getAll executed");
        return Mono.just(ResponseEntity.ok()
                .body(passiveProductService.findAll()
                        .map(passiveProduct -> passiveProductMapper.entityToModel(passiveProduct))));
    }

    @GetMapping("/findById/{id}")
    public Mono<ResponseEntity<PassiveProductModel>> findById(@PathVariable String id){
        log.info("findById executed {}", id);
        Mono<PassiveProduct> response = passiveProductService.findById(id);
        return response
                .map(passiveProduct -> passiveProductMapper.entityToModel(passiveProduct))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/findByIdentityAccount/{identityAccount}")
    public Mono<ResponseEntity<PassiveProductModel>> findByIdentityAccount(@PathVariable String identityAccount){
        log.info("findByIdentityAccount executed {}", identityAccount);
        Mono<PassiveProduct> response = passiveProductService.findByIdentityAccount(identityAccount);
        return response
                .map(passiveProduct -> passiveProductMapper.entityToModel(passiveProduct))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<PassiveProductModel>> create(@Valid @RequestBody PassiveProductModel request){
        log.info("create executed {}", request);
        return passiveProductService.create(passiveProductMapper.modelToEntity(request))
                .map(passiveProduct -> passiveProductMapper.entityToModel(passiveProduct))
                .flatMap(p -> Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", name, port, "passiveProduct", p.getId())))
                        .body(p)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<PassiveProductModel>> updateById(@PathVariable String id, @Valid @RequestBody PassiveProductModel request){
        log.info("updateById executed {}:{}", id, request);
        return passiveProductService.update(id, passiveProductMapper.modelToEntity(request))
                .map(passiveProduct -> passiveProductMapper.entityToModel(passiveProduct))
                .flatMap(p -> Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", name, port, "passiveProduct", p.getId())))
                        .body(p)))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable String id){
        log.info("deleteById executed {}", id);
        return passiveProductService.delete(id)
                .map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
