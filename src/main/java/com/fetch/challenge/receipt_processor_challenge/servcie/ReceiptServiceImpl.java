package com.fetch.challenge.receipt_processor_challenge.servcie;

import com.fetch.challenge.receipt_processor_challenge.dto.ReceiptDTO;
import com.fetch.challenge.receipt_processor_challenge.exception.ResourceNotFoundException;
import com.fetch.challenge.receipt_processor_challenge.rule.PointRule;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReceiptServiceImpl implements ReceiptService {
    private final Map<String, ReceiptDTO> receiptStore = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Integer> pointStore = Collections.synchronizedMap(new HashMap<>());
    private final List<PointRule> rules;

    public ReceiptServiceImpl(List<PointRule> ruleList) {
        this.rules = Collections.unmodifiableList(new ArrayList<>(ruleList));
    }

    @Override
    public String processReceipt(ReceiptDTO receipt) {
        if (receipt == null) {
            throw new IllegalArgumentException("Receipt cannot be null");
        }

        String id = UUID.randomUUID().toString();
        int totalPoints = rules.stream()
                .mapToInt(rule -> rule.calculatePoints(receipt))
                .sum();

        receiptStore.put(id, receipt);
        pointStore.put(id, totalPoints);
        return id;
    }

    @Override
    public Integer getPoints(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Receipt ID cannot be null or empty");
        }

        return Optional.ofNullable(pointStore.get(id))
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found with id: " + id));
    }
}
