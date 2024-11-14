package com.fetch.challenge.receipt_processor_challenge.rule;

import com.fetch.challenge.receipt_processor_challenge.dto.ReceiptDTO;

public interface PointRule {
    int calculatePoints(ReceiptDTO receipt);
}
