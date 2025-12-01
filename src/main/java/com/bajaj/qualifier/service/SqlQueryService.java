package com.bajaj.qualifier.service;

import org.springframework.stereotype.Service;

@Service
public class SqlQueryService {

    /**
     * Generate SQL query based on registration number
     * If last 2 digits are odd -> Question 1
     * If last 2 digits are even -> Question 2
     */
    public String generateSqlQuery(String regNo) {
        int lastTwoDigits = Integer.parseInt(regNo.substring(regNo.length() - 2));
        
        if (lastTwoDigits % 2 != 0) {
            // Odd - Question 1
            return getQuestion1Query();
        } else {
            // Even - Question 2
            return getQuestion2Query();
        }
    }

    /**
     * Question 1: Highest salaried employee per department (excluding 1st day payments)
     */
    private String getQuestion1Query() {
        return "SELECT d.DEPARTMENT_NAME, " +
                "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS EMPLOYEE_NAME, " +
                "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, " +
                "SUM(p.AMOUNT) AS SALARY " +
                "FROM DEPARTMENT d " +
                "JOIN EMPLOYEE e ON d.DEPARTMENT_ID = e.DEPARTMENT " +
                "JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID " +
                "WHERE DAY(p.PAYMENT_TIME) != 1 " +
                "GROUP BY d.DEPARTMENT_NAME, e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, e.DOB " +
                "HAVING SUM(p.AMOUNT) = (" +
                "SELECT MAX(total_salary) FROM (" +
                "SELECT SUM(p2.AMOUNT) as total_salary " +
                "FROM PAYMENTS p2 " +
                "JOIN EMPLOYEE e2 ON p2.EMP_ID = e2.EMP_ID " +
                "WHERE e2.DEPARTMENT = e.DEPARTMENT AND DAY(p2.PAYMENT_TIME) != 1 " +
                "GROUP BY e2.EMP_ID" +
                ") AS dept_salaries" +
                ") " +
                "ORDER BY d.DEPARTMENT_NAME";
    }

    /**
     * Question 2: Department average age and employee list for high earners
     * For each department, calculate avg age of employees earning >70k
     * and show comma-separated list of up to 10 employee names
     */
    private String getQuestion2Query() {
        return "WITH HighEarners AS ( " +
                "SELECT DISTINCT e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, e.DOB, e.DEPARTMENT, p.AMOUNT " +
                "FROM EMPLOYEE e " +
                "JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID " +
                "WHERE p.AMOUNT > 70000 " +
                "), " +
                "DeptAvgAge AS ( " +
                "SELECT d.DEPARTMENT_ID, d.DEPARTMENT_NAME, " +
                "AVG(TIMESTAMPDIFF(YEAR, he.DOB, CURDATE())) AS AVERAGE_AGE " +
                "FROM DEPARTMENT d " +
                "JOIN HighEarners he ON d.DEPARTMENT_ID = he.DEPARTMENT " +
                "GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME " +
                "), " +
                "EmployeeNames AS ( " +
                "SELECT d.DEPARTMENT_ID, " +
                "GROUP_CONCAT(CONCAT(he.FIRST_NAME, ' ', he.LAST_NAME) ORDER BY he.EMP_ID SEPARATOR ', ') AS EMPLOYEE_LIST " +
                "FROM DEPARTMENT d " +
                "JOIN HighEarners he ON d.DEPARTMENT_ID = he.DEPARTMENT " +
                "GROUP BY d.DEPARTMENT_ID " +
                ") " +
                "SELECT daa.DEPARTMENT_NAME, daa.AVERAGE_AGE, " +
                "SUBSTRING_INDEX(en.EMPLOYEE_LIST, ', ', 10) AS EMPLOYEE_LIST " +
                "FROM DeptAvgAge daa " +
                "JOIN EmployeeNames en ON daa.DEPARTMENT_ID = en.DEPARTMENT_ID " +
                "ORDER BY daa.DEPARTMENT_ID DESC";
    }
}
