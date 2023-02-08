package com.MsClients.Clients.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientModel {

    @JsonIgnore
    private String id;

    @NotBlank(message="DNI Number cannot be null or empty")
    private String identityDni;

    @NotBlank(message="Name cannot be null or empty")
    private String firstName;

    @NotBlank(message="LastName cannot be null or empty")
    private String lastName;

    @NotBlank(message="BusinessName cannot be null or empty")
    private String address;

    @NotNull(message="Phone cannot be null or empty")
    private Integer phone;

    @NotBlank(message="Email cannot be null or empty")
    private String email;

    @NotBlank(message="TypeClient cannot be null or empty")
    private String typeClient;

    @NotNull(message="RUC cannot be null or empty")
    private float ruc;

    @NotBlank(message="CompanyName cannot be null or empty")
    private String companyName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateRegister;


}
