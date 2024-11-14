package com.fetch.challenge.receipt_processor_challenge.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class ReceiptDTO {
    @NotBlank(message = "Retailer is required")
    private String retailer;

    @NotNull(message = "Purchase date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate purchaseDate;

    @NotNull(message = "Purchase time is required")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime purchaseTime;

    @NotNull(message = "Items cannot be null")
    @NotEmpty(message = "Items cannot be empty")
    private List<ItemDTO> items;

    @NotBlank(message = "Total is required")
    private String total;


}
