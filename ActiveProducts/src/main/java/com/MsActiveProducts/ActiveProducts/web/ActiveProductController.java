package com.MsActiveProducts.ActiveProducts.web;


import com.MsActiveProducts.ActiveProducts.entity.ActiveProduct;
import com.MsActiveProducts.ActiveProducts.service.ActiveProductService;
import com.MsActiveProducts.ActiveProducts.web.mapper.ActiveProductMapper;
import com.MsActiveProducts.ActiveProducts.web.model.ActiveProductModel;
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
@RequestMapping("/v1/activeProduct")
public class ActiveProductController {

    @Value("${spring.application.name}")
    String name;

    @Value("${server.port}")
    String port;

    @Autowired
    private ActiveProductService activeProductService;


    @Autowired
    private ActiveProductMapper activeProductMapper;


    @GetMapping("/findAll")
    public Mono<ResponseEntity<Flux<ActiveProductModel>>> getAll(){
        log.info("getAll executed");
        return Mono.just(ResponseEntity.ok()
                .body(activeProductService.findAll()
                        .map(activeProduct -> activeProductMapper.entityToModel(activeProduct))));
    }

    @GetMapping("/findById/{id}")
    public Mono<ResponseEntity<ActiveProductModel>> findById(@PathVariable String id){
        log.info("findById executed {}", id);
        Mono<ActiveProduct> response = activeProductService.findById(id);
        return response
                .map(activeProduct -> activeProductMapper.entityToModel(activeProduct))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/findByIdentityContract/{identityContract}")
    public Mono<ResponseEntity<ActiveProductModel>> findByIdentityContract(@PathVariable String identityContract){
        log.info("findByIdentityContract executed {}", identityContract);
        Mono<ActiveProduct> response = activeProductService.findByIdentityContract(identityContract);
        return response
                .map(activeProduct -> activeProductMapper.entityToModel(activeProduct))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Mono<ResponseEntity<ActiveProductModel>> create(@Valid @RequestBody ActiveProductModel request){
        log.info("create executed {}", request);
        return activeProductService.create(activeProductMapper.modelToEntity(request))
                .map(activeProduct -> activeProductMapper.entityToModel(activeProduct))
                .flatMap(p -> Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", name, port, "activeProduct", p.getId())))
                        .body(p)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<ActiveProductModel>> updateById(@PathVariable String id, @Valid @RequestBody ActiveProductModel request){
        log.info("updateById executed {}:{}", id, request);
        return activeProductService.update(id, activeProductMapper.modelToEntity(request))
                .map(activeProduct -> activeProductMapper.entityToModel(activeProduct))
                .flatMap(p -> Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", name, port, "activeProduct", p.getId())))
                        .body(p)))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable String id){
        log.info("deleteById executed {}", id);
        return activeProductService.delete(id)
                .map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

}
