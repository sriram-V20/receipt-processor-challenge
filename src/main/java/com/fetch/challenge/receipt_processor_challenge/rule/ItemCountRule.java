package com.fetch.challenge.receipt_processor_challenge.rule;

import com.fetch.challenge.receipt_processor_challenge.dto.ReceiptDTO;
import org.springframework.stereotype.Component;


@Component
public class ItemCountRule implements PointRule {
    
    private static final int POINTS_PER_PAIR = 5;
    private static final int ITEMS_PER_GROUP = 2;

    @Override
    public int calculatePoints(ReceiptDTO receipt) {
        if (receipt == null || receipt.getItems() == null) {
            return 0;
        }

        int itemCount = receipt.getItems().size();
        return (itemCount / ITEMS_PER_GROUP) * POINTS_PER_PAIR;
    }
}
