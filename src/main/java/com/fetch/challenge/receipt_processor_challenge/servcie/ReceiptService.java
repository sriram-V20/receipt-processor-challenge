package com.fetch.challenge.receipt_processor_challenge.servcie;

import com.fetch.challenge.receipt_processor_challenge.dto.ReceiptDTO;

public interface ReceiptService {
    String processReceipt(ReceiptDTO receipt);
    Integer getPoints(String id);
}
