package com.MsPassiveProducts.PassiveProducts.entity;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@ToString
@EqualsAndHashCode(of = {"identityAccount"})
@AllArgsConstructor
@NoArgsConstructor
@Document(value = "passiveProduct")
public class PassiveProduct {

    @Id
    private String id;
    @NotEmpty
    @Indexed(unique = true)
    @Size(min = 0, max = 20)
    @Column(nullable = false, length = 20)
    private String identityAccount;
    @NotEmpty
    @Size(min = 8, max = 11)
    @Column(nullable = false, length = 11)
    private String document;
    @NotEmpty
    @Size(min = 0, max = 50)
    @Column(nullable = false, length = 50)
    private String typeAccount;
    @NotNull
    @DecimalMax("10000000.00") @DecimalMin("0.0")
    @Column(nullable = false, length = 50)
    private Double availableAmount;
    @NotNull
    @Column(nullable = false)
    private LocalDate dateRegister;



}
