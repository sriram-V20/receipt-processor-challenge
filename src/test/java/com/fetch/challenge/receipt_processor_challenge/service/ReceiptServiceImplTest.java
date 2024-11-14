package com.fetch.challenge.receipt_processor_challenge.service;

import com.fetch.challenge.receipt_processor_challenge.dto.ItemDTO;
import com.fetch.challenge.receipt_processor_challenge.dto.ReceiptDTO;
import com.fetch.challenge.receipt_processor_challenge.exception.ResourceNotFoundException;
import com.fetch.challenge.receipt_processor_challenge.servcie.ReceiptService;
import com.fetch.challenge.receipt_processor_challenge.servcie.ReceiptServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.BeforeEach;


import com.fetch.challenge.receipt_processor_challenge.rule.PointRule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReceiptServiceImplTest {

    @Mock
    private PointRule rule1;

    @Mock
    private PointRule rule2;

    private ReceiptService receiptService;

    @BeforeEach
    void setUp() {
        receiptService = new ReceiptServiceImpl(List.of(rule1, rule2));
    }

    @Test
    void processReceipt_ShouldGenerateUniqueId() {
        // Arrange
        ReceiptDTO receipt1 = createSampleReceipt();
        ReceiptDTO receipt2 = createSampleReceipt();
        
        // Act
        String id1 = receiptService.processReceipt(receipt1);
        String id2 = receiptService.processReceipt(receipt2);
        
        // Assert
        assertNotNull(id1);
        assertNotNull(id2);
        assertNotEquals(id1, id2);
    }

    @Test
    void processReceipt_ShouldAggregatePointsFromAllRules() {
        // Arrange
        ReceiptDTO receipt = createSampleReceipt();
        when(rule1.calculatePoints(any(ReceiptDTO.class))).thenReturn(10);
        when(rule2.calculatePoints(any(ReceiptDTO.class))).thenReturn(20);

        // Act
        String id = receiptService.processReceipt(receipt);
        int points = receiptService.getPoints(id);

        // Assert
        assertEquals(30, points);
    }

    @Test
    void getPoints_ShouldReturnCorrectPointsForStoredReceipt() {
        // Arrange
        ReceiptDTO receipt = createSampleReceipt();
        when(rule1.calculatePoints(any(ReceiptDTO.class))).thenReturn(15);
        when(rule2.calculatePoints(any(ReceiptDTO.class))).thenReturn(25);
        
        // Act
        String id = receiptService.processReceipt(receipt);
        int points = receiptService.getPoints(id);
        
        // Assert
        assertEquals(40, points);
    }

    @Test
    void getPoints_ShouldReturnSamePointsOnMultipleCalls() {
        // Arrange
        ReceiptDTO receipt = createSampleReceipt();
        when(rule1.calculatePoints(any(ReceiptDTO.class))).thenReturn(10);
        when(rule2.calculatePoints(any(ReceiptDTO.class))).thenReturn(20);
        
        // Act
        String id = receiptService.processReceipt(receipt);
        int firstCall = receiptService.getPoints(id);
        int secondCall = receiptService.getPoints(id);
        
        // Assert
        assertEquals(firstCall, secondCall);
    }

    @Test
    void getPoints_InvalidId_ThrowsException() {
        assertThrows(ResourceNotFoundException.class,
                () -> receiptService.getPoints("non-existent-id"));
    }

    @Test
    void processReceipt_NullReceipt_ThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> receiptService.processReceipt(null));
    }

    @Test
    void processReceipt_ShouldPersistReceiptForLaterRetrieval() {
        // Arrange
        ReceiptDTO receipt = createSampleReceipt();
        when(rule1.calculatePoints(any(ReceiptDTO.class))).thenReturn(5);
        when(rule2.calculatePoints(any(ReceiptDTO.class))).thenReturn(10);
        
        // Act
        String id = receiptService.processReceipt(receipt);
        
        // Assert
        assertDoesNotThrow(() -> receiptService.getPoints(id));
        assertEquals(15, receiptService.getPoints(id));
    }

    private ReceiptDTO createSampleReceipt() {
        ReceiptDTO receipt = new ReceiptDTO();
        receipt.setRetailer("Target");
        receipt.setPurchaseDate(LocalDate.now());
        receipt.setPurchaseTime(LocalTime.now());
        receipt.setTotal("35.35");

        ItemDTO item1 = new ItemDTO();
        item1.setShortDescription("Mountain Dew 12PK");
        item1.setPrice(6.49);

        ItemDTO item2 = new ItemDTO();
        item2.setShortDescription("Emils Cheese Pizza");
        item2.setPrice(12.25);

        receipt.setItems(List.of(item1, item2));
        return receipt;
    }
}
