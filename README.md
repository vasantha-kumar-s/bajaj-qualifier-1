# Bajaj Finserv Java Qualifier

Spring Boot application for Bajaj Finserv Health qualifier assignment.

## What it does

1. Calls API to generate webhook
2. Executes SQL query to find highest salaried employee per department
3. Submits solution with JWT token

## Registration

- RegNo: 22BCE7897
- Name: Vasantha Kumar
- Email: vasanthakumarselvaraj04@gmail.com

## Database Setup

Run the SQL script to create test database:
```bash
mysql -u root -p < setup_database.sql
```

## Build

```bash
mvn clean package
```

## Run

```bash
java -jar target/bajaj-qualifier.jar
```

## Testing with Postman or REST API Client

### Step 1: Generate Webhook

**Request:**
- Method: `POST`
- URL: `https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA`
- Headers: `Content-Type: application/json`
- Body (JSON):
```json
{
  "name": "Vasantha Kumar",
  "regNo": "22BCE7897",
  "email": "vasanthakumarselvaraj04@gmail.com"
}
```

**Expected Response:**
```json
{
  "webhook": "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA",
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJyZWdObyI6IjIyQkNFNzg5NyIsIm5hbWUiOiJWYXNhbnRoYSBLdW1hciIsImVtYWlsIjoidmFzYW50aGFrdW1hcnNlbHZhcmFqMDRAZ21haWwuY29tIiwic3ViIjoid2ViaG9vay11c2VyIiwiaWF0IjoxNzY0NTkxMzkxLCJleHAiOjE3NjQ1OTIyOTF9.PKw_yQT9Q7wepkBnwUoINZ3dwdKCVM-Oodh5Tskmvqc"
}
```

### Step 2: Submit Solution

**Request:**
- Method: `POST`
- URL: Use the `webhook` URL from Step 1 response
- Headers:
  - `Authorization: <accessToken from Step 1>`
  - `Content-Type: application/json`
- Body (JSON):
```json
{
  "finalQuery": "SELECT d.DEPARTMENT_NAME, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS EMPLOYEE_NAME, TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, SUM(p.AMOUNT) AS SALARY FROM DEPARTMENT d JOIN EMPLOYEE e ON d.DEPARTMENT_ID = e.DEPARTMENT JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID WHERE DAY(p.PAYMENT_TIME) != 1 GROUP BY d.DEPARTMENT_NAME, e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, e.DOB HAVING SUM(p.AMOUNT) = (SELECT MAX(total_salary) FROM (SELECT SUM(p2.AMOUNT) as total_salary FROM PAYMENTS p2 JOIN EMPLOYEE e2 ON p2.EMP_ID = e2.EMP_ID WHERE e2.DEPARTMENT = e.DEPARTMENT AND DAY(p2.PAYMENT_TIME) != 1 GROUP BY e2.EMP_ID) AS dept_salaries) ORDER BY d.DEPARTMENT_NAME"
}
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Webhook processed successfully"
}
```

**Note:** The accessToken expires in 15 minutes. Use it immediately after generation.

## Tech Stack

- Java 17
- Spring Boot 3.2.0
- Maven
- MySQL
