# Bank Transfer System
This project is a simple RESTful service for managing bank accounts and transferring money between them.

## Features:
#### Account Creation: 
- Allows creating new bank accounts with a unique account ID and an initial balance.
#### Account Retrieval: 
- Provides endpoints for retrieving account information by account ID.
- Money Transfer: Supports transferring money between accounts, ensuring thread safety and preventing overdrafts.
- Notifications: Sends notifications to account holders when a transfer is made, informing them of the amount and the recipient account ID.

## Technologies Used
#### Spring Boot: 
- Framework for building the RESTful service.
- Java: Programming language used for backend development.
- JUnit: Testing framework for unit and integration tests.
- Mockito: Mocking library for creating mocks in tests.
- Lombok: Library for reducing boilerplate code in Java classes.

## Project Structure
#### The project consists of several main components:

- Controllers: Handles HTTP requests and delegates business logic to services.
- Services: Contains business logic for managing accounts and performing transfers.
- Repositories: Interface for accessing and managing account data.
- Exceptions: Custom exception classes for handling various error scenarios.
- Models: Contains domain classes such as Account for representing bank accounts.
- Notifications: Interface and implementation for sending notifications about transfers.

## Setup and Usage
- Clone the Repository: Clone the project repository from GitHub.
- Build the Project: Build the project using Maven or Gradle.
- Run the Application: Start the Spring Boot application on your local machine.
- Test Endpoints: Use tools like Postman to test the API endpoints for creating accounts, transferring money, and retrieving account information.

## API Endpoints
- POST /v1/accounts: Create a new bank account with a unique account ID and an initial balance.
- GET /v1/accounts/{accountId}: Retrieve account information by account ID.
- POST /api/transfers/initiate: Initiate a money transfer between two accounts by providing the account IDs and the transfer amount.

## Testing
- The project includes unit tests for all service classes using JUnit and Mockito. To run the tests, execute the test command with Gradle.

## Testing REST API using Postman: 

![image](https://github.com/MaqsoodCodingPassion/TransferMoney-SpringBoot/assets/54396268/f5ade610-ead3-44ea-84f0-ee602d00fcaf)


![image](https://github.com/MaqsoodCodingPassion/TransferMoney-SpringBoot/assets/54396268/7008da9e-03ec-4ec4-97b2-fe09fdbdc91c)


![image](https://github.com/MaqsoodCodingPassion/TransferMoney-SpringBoot/assets/54396268/c71ff9df-1ddc-42da-a7d2-8b44540145d3)



