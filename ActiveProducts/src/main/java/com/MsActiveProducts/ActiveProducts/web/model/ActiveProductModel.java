package com.MsActiveProducts.ActiveProducts.web.model;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class ActiveProductModel {

   
    private String id;
    @NotBlank(message="Contract Number cannot be null or empty")
    private String identityContract;
    @NotNull(message="Amount Number cannot be null or empty")
    private Double amount;
    @NotNull(message="limitAmount cannot be null or empty")
    private Double limitAmount;
    @NotBlank(message="document cannot be null or empty")
    private String document;
    @NotBlank(message="typeCredit cannot be null or empty")
    private String typeCredit;
    @NotBlank(message="holder cannot be null or empty")
    private String holder;

    private String signatory;
    @NotNull(message="availableAmount cannot be null or empty")
    private Double availableAmount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateRegister;

}
