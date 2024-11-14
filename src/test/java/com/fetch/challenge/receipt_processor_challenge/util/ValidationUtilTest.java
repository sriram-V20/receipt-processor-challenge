package com.fetch.challenge.receipt_processor_challenge.util;


import com.fetch.challenge.receipt_processor_challenge.dto.ItemDTO;
import com.fetch.challenge.receipt_processor_challenge.dto.ReceiptDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
public class ValidationUtilTest {
    private final ValidationUtil validationUtil = new ValidationUtil();

    @Test
    void validateReceipt_ValidReceipt_ReturnsTrue() {
        ReceiptDTO receipt = new ReceiptDTO();
        receipt.setRetailer("Target");
        receipt.setPurchaseDate(LocalDate.now());
        receipt.setPurchaseTime(LocalTime.now());
        receipt.setTotal("35.35");
        receipt.setItems(Collections.singletonList(new ItemDTO()));

        boolean isValid = validationUtil.validateReceipt(receipt);
        assertTrue(isValid);
    }

    @Test
    void validateReceipt_NullReceipt_ReturnsFalse() {
        assertFalse(validationUtil.validateReceipt(null));
    }
}
