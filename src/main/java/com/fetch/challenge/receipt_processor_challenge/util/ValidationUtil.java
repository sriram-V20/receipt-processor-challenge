package com.fetch.challenge.receipt_processor_challenge.util;

import com.fetch.challenge.receipt_processor_challenge.dto.ReceiptDTO;
import org.springframework.stereotype.Component;

@Component
public class ValidationUtil {
    public boolean validateReceipt(ReceiptDTO receipt) {
        return receipt != null &&
                receipt.getRetailer() != null &&
                !receipt.getRetailer().trim().isEmpty() &&
                receipt.getPurchaseDate() != null &&
                receipt.getPurchaseTime() != null &&
                receipt.getTotal() != null &&
                receipt.getItems() != null &&
                !receipt.getItems().isEmpty();
    }
}
