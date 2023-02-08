package com.MsPassiveProducts.PassiveProducts.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Client {

    private String id;
    private String identityDni;
    private String firstName;
    private String lastName;
    private String address;
    private Integer phone;
    private String email;
    private String typeClient;
    private float ruc;
    private String companyName;
    private LocalDate dateRegister;

}
