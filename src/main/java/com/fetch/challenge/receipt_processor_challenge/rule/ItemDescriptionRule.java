package com.fetch.challenge.receipt_processor_challenge.rule;

import com.fetch.challenge.receipt_processor_challenge.dto.ItemDTO;
import com.fetch.challenge.receipt_processor_challenge.dto.ReceiptDTO;
import org.springframework.stereotype.Component;


@Component
public class ItemDescriptionRule implements PointRule{
    private static final double PRICE_MULTIPLIER = 0.2;
    private static final int DESCRIPTION_LENGTH_DIVISOR = 3;

    @Override
    public int calculatePoints(ReceiptDTO receipt) {
        if (receipt == null || receipt.getItems() == null) {
            return 0;
        }

        return receipt.getItems().stream()
                .filter(item -> item != null && item.getShortDescription() != null)
                .mapToInt(this::calculatePointsForItem)
                .sum();
    }

    private int calculatePointsForItem(ItemDTO item) {
        String description = item.getShortDescription().trim();
        if (description.length() % DESCRIPTION_LENGTH_DIVISOR == 0) {
            return (int) Math.ceil(item.getPrice() * PRICE_MULTIPLIER);
        }
        return 0;
    }
}
