package com.fetch.challenge.receipt_processor_challenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.containsString;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.http.MediaType;

@SpringBootTest
@AutoConfigureMockMvc
public class ReceiptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testTargetReceipt() throws Exception {
        String receiptJson = """
            {
                "retailer": "Target",
                "purchaseDate": "2022-01-01",
                "purchaseTime": "13:01",
                "items": [
                    {
                    "shortDescription": "Mountain Dew 12PK",
                    "price": "6.49"
                    },{
                    "shortDescription": "Emils Cheese Pizza",
                    "price": "12.25"
                    },{
                    "shortDescription": "Knorr Creamy Chicken",
                    "price": "1.26"
                    },{
                    "shortDescription": "Doritos Nacho Cheese",
                    "price": "3.35"
                    },{
                    "shortDescription": "   Klarbrunn 12-PK 12 FL OZ  ",
                    "price": "12.00"
                    }
                ],
                "total": "35.35"
            }
            """;
            // Expected points for Target receipt:
            // - "Target" = 6 points (6 alphanumeric chars)
            // - Total $35.35 = 0 points (not round dollar, not multiple of 0.25)
            // - 5 items = 10 points (5 points per pair of items)
            // - Item descriptions:
            //   * "Mountain Dew 12PK" (15 chars) = 3 points (15 is multiple of 3, 6.49 * 0.2 = 1.298 → 2)
            //   * "Emils Cheese Pizza" (not multiple of 3) = 0
            //   * "Knorr Creamy Chicken" (not multiple of 3) = 0
            //   * "Doritos Nacho Cheese" (18 chars) = 1 point (18 is multiple of 3, 3.35 * 0.2 = 0.67 → 1)
            //   * "Klarbrunn 12-PK 12 FL OZ" (21 chars trimmed) = 3 points (21 is multiple of 3, 12.00 * 0.2 = 2.4 → 3)
            // - Purchase at 13:01 = 0 points (not between 2-4 PM)
            // - Day 1 = 0 points (not odd)
            // Total: 28 points

        String response = mockMvc.perform(post("/receipts/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(receiptJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String receiptId = objectMapper.readTree(response).get("id").asText();
        String expectedPoints = "28";
        // Get points and verify
        mockMvc.perform(get("/receipts/{id}/points", receiptId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points").value(expectedPoints));
    }

    @Test
    public void testAfternoonTimeRangeReceipt() throws Exception {
        String receiptJson = """
            {
                "retailer": "M&M Corner Market",
                "purchaseDate": "2022-03-20",
                "purchaseTime": "14:33",
                "items": [
                        {
                        "shortDescription": "Gatorade",
                        "price": "2.25"
                    },{
                    "shortDescription": "Gatorade",
                    "price": "2.25"
                },{
                    "shortDescription": "Gatorade",
                    "price": "2.25"
                },{
                    "shortDescription": "Gatorade",
                    "price": "2.25"
                }
            ],
            "total": "9.00"
            }
            """;
            // Expected points for M&M Corner Market receipt:
            // - "M&M Corner Market" = 14 points (14 alphanumeric chars)
            // - Total $9.00 = 75 points (50 for round dollar + 25 for multiple of 0.25)
            // - 4 items = 10 points (5 points per pair)
            // - Item descriptions: "Gatorade" (8 chars) = 0 points (not multiple of 3)
            // - Purchase at 14:33 = 10 points (between 2-4 PM)
            // - Day 20 = 0 points (not odd)
            // Total: 109 points
            String expectedPoints = "109";

        String response = mockMvc.perform(post("/receipts/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(receiptJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String receiptId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/receipts/{id}/points", receiptId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points").value(expectedPoints));
    }

    @Test
    public void testOddDayAndDescriptionLengthReceipt() throws Exception {
        String receiptJson = """
            {
              "retailer": "Walgreens",
              "purchaseDate": "2022-01-15",
              "purchaseTime": "09:20",
              "items": [
                {
                  "shortDescription": "Milk 1 Gallon",
                  "price": "3.99"
                },
                {
                  "shortDescription": "Exactly Twenty Chars",
                  "price": "9.99"
                }
              ],
              "total": "13.98"
            }
            """;

        String response = mockMvc.perform(post("/receipts/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(receiptJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String receiptId = objectMapper.readTree(response).get("id").asText();
        String expectedPoints = "20";
        
        mockMvc.perform(get("/receipts/{id}/points", receiptId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points").value(expectedPoints));
    }

    @Test
    public void testRoundDollarAmount() throws Exception {
        String receiptJson = """
            {
              "retailer": "Walmart",
              "purchaseDate": "2022-01-02",
              "purchaseTime": "13:37",
              "items": [
                {
                  "shortDescription": "Item 1",
                  "price": "35.00"
                },
                {
                  "shortDescription": "Item 2",
                  "price": "15.00"
                }
              ],
              "total": "50.00"
            }
            """;

        // Expected points:
        // - "Walmart" = 7 points
        // - Total $50.00 = 75 points (50 for round dollar + 25 for multiple of 0.25)
        // - 2 items = 5 points
        // - Other rules = 0 points
        // Total: 97 points

        String response = mockMvc.perform(post("/receipts/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(receiptJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String receiptId = objectMapper.readTree(response).get("id").asText();
        String expectedPoints = "97";
        mockMvc.perform(get("/receipts/{id}/points", receiptId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points").value(expectedPoints));
    }

    @Test
    public void testMultipleQuarterMultiples() throws Exception {
        String receiptJson = """
            {
              "retailer": "7-11",
              "purchaseDate": "2022-01-02",
              "purchaseTime": "13:37",
              "items": [
                {
                  "shortDescription": "Coffee",
                  "price": "1.25"
                },
                {
                  "shortDescription": "Donut",
                  "price": "0.75"
                }
              ],
              "total": "2.00"
            }
            """;
        
        // Expected points:
        // - "7-11" = 4 points
        // - Total $2.00 = 75 points (50 for round dollar + 25 for multiple of 0.25)
        // - 2 items = 5 points
        // - Other rules = 0 points
        // Total: 84 points
        String expectedPoints = "84";  
        String response = mockMvc.perform(post("/receipts/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(receiptJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String receiptId = objectMapper.readTree(response).get("id").asText();
 

        mockMvc.perform(get("/receipts/{id}/points", receiptId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points").value(expectedPoints));
    }

    @Test
    public void testEdgeCase_SingleItemWithMaxPoints() throws Exception {
        String receiptJson = """
            {
              "retailer": "Super Store 123456789",
              "purchaseDate": "2022-03-15",
              "purchaseTime": "14:00",
              "items": [
                {
                  "shortDescription": "Exactly Twenty Chars!",
                  "price": "0.75"
                }
              ],
              "total": "0.75"
            }
            """;

        // Expected points for Super Store receipt:
        // - "Super Store 123456789" = 20 points (20 alphanumeric chars)
        // - Total $0.75 = 25 points (multiple of 0.25)
        // - 1 item = 0 points (need pairs for points)
        // - Purchase at 14:00 = 10 points (between 2-4 PM)
        // - Day 15 = 6 points (odd day)
        // - "Exactly Twenty Chars!" (20 chars) = 0 points (not multiple of 3)
        // Total: 51 points
        String expectedPoints = "51";

        String response = mockMvc.perform(post("/receipts/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(receiptJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String receiptId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/receipts/{id}/points", receiptId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points").value(expectedPoints));
    }

    @Test
    public void testEdgeCase_MinimalPoints() throws Exception {
        String receiptJson = """
            {
              "retailer": "X",
              "purchaseDate": "2022-02-02",
              "purchaseTime": "12:00",
              "items": [
                {
                  "shortDescription": "A",
                  "price": "0.01"
                }
              ],
              "total": "0.01"
            }
            """;

        // Expected points:
        // 1 point - retailer name length
        // Total: 1 points
        String expectedPoints = "1";
        String response = mockMvc.perform(post("/receipts/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(receiptJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String receiptId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/receipts/{id}/points", receiptId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points").value(expectedPoints));
    }

    @Test
    public void testEdgeCase_EmptyReceipt() throws Exception {
        String receiptJson = """
            {
              "retailer": "",
              "purchaseDate": null,
              "purchaseTime": null,
              "items": null,
              "total": null
            }
            """;

        mockMvc.perform(post("/receipts/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(receiptJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("error")));
    }

    @Test
    public void testInvalidReceipt_MissingRequiredFields() throws Exception {
        String receiptJson = """
            {
              "retailer": "",
              "items": []
            }
            """;

        mockMvc.perform(post("/receipts/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(receiptJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("error")));
    }
}
