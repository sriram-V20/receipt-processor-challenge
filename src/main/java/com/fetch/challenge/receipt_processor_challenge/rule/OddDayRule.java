package com.fetch.challenge.receipt_processor_challenge.rule;

import com.fetch.challenge.receipt_processor_challenge.dto.ReceiptDTO;
import org.springframework.stereotype.Component;


@Component
public class OddDayRule implements PointRule {
    
    private static final int POINTS_FOR_ODD_DAY = 6;
    private static final int NO_POINTS = 0;
    private static final int DIVISOR = 2;
    private static final int ODD_REMAINDER = 1;

    @Override
    public int calculatePoints(ReceiptDTO receipt) {
        if (receipt == null || receipt.getPurchaseDate() == null) {
            return NO_POINTS;
        }

        return isOddDay(receipt.getPurchaseDate().getDayOfMonth()) 
            ? POINTS_FOR_ODD_DAY 
            : NO_POINTS;
    }

    private boolean isOddDay(int day) {
        return day % DIVISOR == ODD_REMAINDER;
    }
}
