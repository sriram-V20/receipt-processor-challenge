package com.fetch.challenge.receipt_processor_challenge.rule;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import com.fetch.challenge.receipt_processor_challenge.dto.ReceiptDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RoundDollarRuleTest {
    private final RoundDollarRule rule = new RoundDollarRule();

    @ParameterizedTest
    @CsvSource({
            "100.00, 50",
            "99.99, 0",
            "50.00, 50"
    })
    void calculatePoints_VariousAmounts_ReturnsCorrectPoints(double amount, int expectedPoints) {
        // Arrange
        ReceiptDTO receipt = new ReceiptDTO();
        receipt.setTotal(String.valueOf(amount));

        int points = rule.calculatePoints(receipt);

        assertEquals(expectedPoints, points);
    }
}
