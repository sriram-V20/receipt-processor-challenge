package com.fetch.challenge.receipt_processor_challenge.rule;

import com.fetch.challenge.receipt_processor_challenge.dto.ReceiptDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RetailerNameRuleTest {
    private final RetailerNameRule rule = new RetailerNameRule();

    @Test
    void calculatePoints_AlphanumericCharacters_ReturnsCorrectPoints() {
        // Arrange
        ReceiptDTO receipt = new ReceiptDTO();
        receipt.setRetailer("Target123");

        // Act
        int points = rule.calculatePoints(receipt);

        // Assert
        assertEquals(9, points); // "Target123" has 8 alphanumeric characters
    }
}
