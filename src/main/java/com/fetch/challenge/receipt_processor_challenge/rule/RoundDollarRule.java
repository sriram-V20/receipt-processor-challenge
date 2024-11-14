package com.fetch.challenge.receipt_processor_challenge.rule;

import com.fetch.challenge.receipt_processor_challenge.dto.ReceiptDTO;
import org.springframework.stereotype.Component;

@Component
public class RoundDollarRule implements PointRule{
    private static final int POINTS_FOR_ROUND_DOLLAR = 50;
    private static final int ZERO_POINTS = 0;
    @Override
    public int calculatePoints(ReceiptDTO receipt) {
        double totalAmount = Double.parseDouble(receipt.getTotal());
        return totalAmount % 1.0 == 0 ? POINTS_FOR_ROUND_DOLLAR : ZERO_POINTS;
    }
}
