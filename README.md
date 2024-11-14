# Receipt Processor Challenge

A Spring Boot application that processes receipts and calculates reward points based on various rules.

## How to Run the Project

### Using Docker

1. Make sure you have Docker and Docker Compose installed
2. Clone the repository
3. Navigate to the project root directory
4. Run the following command:
   ```bash
   docker-compose up --build
   ```
5. The application will be available at `http://localhost:8080`

### Using Java and Maven

#### Prerequisites
- Java 17 or higher
- Maven 3.9.x or higher (or use the included Maven wrapper)

#### Steps
1. Clone the repository
2. Navigate to the project root directory
3. Build the project:
   ```bash
   # Using Maven
   mvn clean package

   # OR using Maven wrapper
   ./mvnw clean package
   ```
4. Run the application:
   ```bash
   # Using Maven
   mvn spring-boot:run

   # OR using Maven wrapper
   ./mvnw spring-boot:run
   ```
5. The application will be available at `http://localhost:8080`

## Project Structure

- `src/main/java/com/fetch/challenge/receipt_processor_challenge/` - Main application code
  - `config/` - Configuration classes
  - `dto/` - Data Transfer Objects
  - `exception/` - Custom exceptions
  - `rule/` - Point calculation rules
  - `service/` - Business logic
  - `controller/` - REST API endpoints

## API Documentation

### Endpoints

#### 1. Process Receipt
- **POST** `/receipts/process`
- Processes a receipt and returns an ID for points lookup
- Request Body:
  ```json
  {
    "retailer": "Target",
    "purchaseDate": "2022-01-01",
    "purchaseTime": "13:01",
    "items": [
      {
        "shortDescription": "Mountain Dew 12PK",
        "price": "6.49"
      }
    ],
    "total": "6.49"
  }
  ```
- Response:
  ```json
  {
    "id": "unique-receipt-id"
  }
  ```

#### 2. Get Points
- **GET** `/receipts/{id}/points`
- Retrieves the points awarded for a receipt
- Response:
  ```json
  {
    "points": 32
  }
  ```

### Point Calculation Rules

The application calculates points based on the following rules:

1. **Retailer Name Rule**
   - One point for every alphanumeric character in the retailer name
   - Example: "Target" = 6 points

2. **Round Dollar Rule**
   - 50 points if the total is a round dollar amount with no cents
   - Example: $100.00 = 50 points

3. **Quarter Multiple Rule**
   - 25 points if the total is a multiple of 0.25
   - Example: $25.50 = 25 points

4. **Item Count Rule**
   - 5 points for every two items on the receipt
   - Example: 6 items = 15 points

5. **Item Description Rule**
   - If the trimmed length of the item description is a multiple of 3
   - Multiply the price by 0.2 and round up to the nearest integer
   - Example: "Mountain Dew 12PK" (length: 15) = ceil(6.49 * 0.2) = 2 points

6. **Odd Day Rule**
   - 6 points if the day in the purchase date is odd
   - Example: "2022-01-01" = 6 points

7. **Time Range Rule**
   - 10 points if the time of purchase is between 2:00pm and 4:00pm
   - Example: "14:30" = 10 points

### Adding New Point Rules

The application is designed to make adding new point calculation rules easy and modular. Here's how to add a new rule:

1. **Create a New Rule Class**
   - Create a new class in the `rule` package
   - Implement the `PointRule` interface
   - Add the `@Component` annotation
   ```java
   @Component
   public class NewCustomRule implements PointRule {
       @Override
       public int calculatePoints(ReceiptDTO receipt) {
           // Implement your rule logic here
           return calculatedPoints;
       }
   }
   ```

2. **Add Rule to Configuration**
   - The rule will be automatically detected by Spring's component scanning
   - Add the rule parameter to the `pointRules` method in `RuleConfiguration`
   ```java
   @Bean
   public List<PointRule> pointRules(
       // ... existing rules ...
       NewCustomRule newCustomRule
   ) {
       return List.of(
           // ... existing rules ...
           newCustomRule
       );
   }
   ```

The system will automatically include the new rule in point calculations without requiring changes to other components

## Testing

The project includes comprehensive test coverage across multiple layers:

#### Controller Tests
- `ReceiptControllerTest`: Tests the REST endpoints with various receipt scenarios:
  - Target receipt with multiple items
  - Afternoon time range receipt (2-4 PM)
  - Odd day and description length combinations
  - Round dollar amount receipts
  - Multiple quarter multiples
  - Edge cases (single item, minimal points, empty receipt)
  - Invalid receipt validation

#### Service Tests
- `ReceiptServiceImplTest`: Tests the business logic implementation:
  - Unique ID generation for receipts
  - Point calculation aggregation
  - Receipt persistence and retrieval
  - Error handling for invalid IDs
  - Multiple point calculation consistency

#### Rule Tests
- Individual test classes for each point calculation rule:
  - `ItemDescriptionRuleTest`: Tests points for item description lengths
  - `RetailerNameRuleTest`: Tests alphanumeric character counting
  - `RoundDollarRuleTest`: Tests round dollar amount detection
  - `TimeRangeRuleTest`: Tests time-based point calculation

#### Utility Tests
- `ValidationUtilTest`: Tests receipt validation logic

### Test Data

The tests use a variety of receipt scenarios to ensure comprehensive coverage:
- Different retailer names
- Various purchase times and dates
- Multiple item combinations
- Different total amounts
- Special cases (round numbers, multiples of 0.25, random decimals)

### Running Tests Locally

#### Using Maven
```bash
# Run all tests
mvn test

# OR using Maven wrapper
./mvnw test

# Run specific test class
mvn test -Dtest=ReceiptControllerTest
```

### Running Tests with Docker

1. Build and run tests in a container:
```bash
docker build --target test -t receipt-processor-tests .
```

2. Run specific test class in container:
```bash
docker run --rm receipt-processor-tests mvn test -Dtest=ReceiptControllerTest
```

3. To keep test results, mount a volume:
```bash
docker run --rm -v "$(pwd)/target:/app/target" receipt-processor-tests mvn test
```