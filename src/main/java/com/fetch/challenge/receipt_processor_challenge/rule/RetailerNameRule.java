package com.fetch.challenge.receipt_processor_challenge.rule;

import com.fetch.challenge.receipt_processor_challenge.dto.ReceiptDTO;
import org.springframework.stereotype.Component;

@Component
public class RetailerNameRule implements PointRule{
    private static final String ALPHANUMERIC_PATTERN = "[^A-Za-z0-9]";
    
    @Override
    public int calculatePoints(ReceiptDTO receipt) {
        return receipt.getRetailer().replaceAll(ALPHANUMERIC_PATTERN, "").length();
    }
}
