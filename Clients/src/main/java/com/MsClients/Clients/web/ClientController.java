package com.MicroClients.Clients.web;


import com.MicroClients.Clients.entity.Client;
import com.MicroClients.Clients.entity.Response;
import com.MicroClients.Clients.service.ClientService;
import com.MicroClients.Clients.web.mapper.ClientMapper;
import com.MicroClients.Clients.web.model.ClientModel;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import javax.validation.Valid;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/client")
public class ClientController {

    @Value("${spring.application.name}")
    String name;

    @Value("${server.port}")
    String port;

    @Autowired
    private ClientService clientService;
    @Autowired
    private ClientMapper clientMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private static final String RESILIENCE4J_INSTANCE_NAME = "example";

    private static final String FALLBACK_METHOD = "fallback";


    @Operation(summary = "Listar todos los clientes registrados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se listaron todos los clientes registrados",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Client.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @GetMapping("/findAll")
    @CircuitBreaker(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = FALLBACK_METHOD)
    @TimeLimiter(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = FALLBACK_METHOD)
    public Mono<ResponseEntity<Flux<ClientModel>>> getAll(){
        log.info("getAll executed");
        ValueOperations<String, String> valueOp = redisTemplate.opsForValue();
        valueOp.set(getKey2("Lista Clientes"), clientService.findAll().toString(), Duration.ofSeconds(120L));
        return Mono.just(ResponseEntity.ok()
                .body(clientService.findAll()
                        .map(client -> clientMapper.entityToModel(client))));
    }


    @Operation(summary = "Listar todos los clientes por Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se listaron todos los clientes por Id",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Client.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @GetMapping("/findById/{id}")
    @CircuitBreaker(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = FALLBACK_METHOD)
    @TimeLimiter(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = FALLBACK_METHOD)
    public Mono<ResponseEntity<ClientModel>> findById(@PathVariable String id){
        log.info("findById executed {}", id);
        ValueOperations<String, String> valueOp = redisTemplate.opsForValue();
        valueOp.set(getKey(id), clientService.findById(id).toString(), Duration.ofSeconds(20));
        return clientService.findById(id)
                .map(client -> clientMapper.entityToModel(client))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }

    private String getKey(String id)
    {
        return "Clients -" .concat(id);
    }
    private String getKey2(String param)
    {
        return "Listado Clients" .concat(param);
    }

    @Operation(summary = "Listar todos los clientes por DNI")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se listaron todos los clientes por DNI",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Client.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @GetMapping("/findByIdentityDni/{identityDni}")
    @CircuitBreaker(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = FALLBACK_METHOD)
    @TimeLimiter(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = FALLBACK_METHOD)
    public Mono<ResponseEntity<ClientModel>> findByIdentityDni(@PathVariable String identityDni){
        log.info("findByIdentityDni executed {}", identityDni);
        Mono<Client> response = clientService.findByIdentityDni(identityDni);
        return response
                .map(client -> clientMapper.entityToModel(client))
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Registro de los Clientes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se registro el cliente de manera exitosa",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Client.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @PostMapping
    @CircuitBreaker(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = FALLBACK_METHOD)
    @TimeLimiter(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = FALLBACK_METHOD)
    public Mono<ResponseEntity<ClientModel>> create(@Valid @RequestBody ClientModel request){
        log.info("create executed {}", request);
        return clientService.create(clientMapper.modelToEntity(request))
                .map(client -> clientMapper.entityToModel(client))
                .flatMap(c -> Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", name, port, "client", c.getId())))
                        .body(c)))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar el cliente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se actualizará el cliente por el ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Client.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @PutMapping("/{id}")
    @CircuitBreaker(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = FALLBACK_METHOD)
    @TimeLimiter(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = FALLBACK_METHOD)
    public Mono<ResponseEntity<ClientModel>> updateById(@PathVariable String id, @Valid @RequestBody ClientModel request){
        log.info("updateById executed {}:{}", id, request);
        return clientService.update(id, clientMapper.modelToEntity(request))
                .map(client -> clientMapper.entityToModel(client))
                .flatMap(c -> Mono.just(ResponseEntity.created(URI.create(String.format("http://%s:%s/%s/%s", name, port, "client", c.getId())))
                        .body(c)))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @Operation(summary = "Eliminar Cliente por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Se elimino el cliente por ID",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Client.class)) }),
            @ApiResponse(responseCode = "400", description = "Parametros invalidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No se encontraron registros",
                    content = @Content) })
    @DeleteMapping("/{id}")
    @CircuitBreaker(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = FALLBACK_METHOD)
    @TimeLimiter(name = RESILIENCE4J_INSTANCE_NAME, fallbackMethod = FALLBACK_METHOD)
    public Mono<ResponseEntity<Void>> deleteById(@PathVariable String id){
        log.info("deleteById executed {}", id);
        return clientService.delete(id)
                .map( r -> ResponseEntity.ok().<Void>build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }


    private Response<Boolean> toOkResponse() {
        return toResponse(HttpStatus.OK, Boolean.TRUE);
    }

    private Response<Boolean> toResponse(HttpStatus httpStatus, Boolean result) {
        return Response.<Boolean>builder()
                .code(httpStatus.value())
                .status(httpStatus.getReasonPhrase())
                .data(result)
                .build();
    }





}
