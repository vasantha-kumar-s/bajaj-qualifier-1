package com.bajaj.qualifier.repository;

import com.bajaj.qualifier.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    
    @Query(value = "SELECT " +
            "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS FULL_NAME, " +
            "DATE_FORMAT(p.PAYMENT_TIME, '%Y-%m') AS MONTH, " +
            "SUM(p.AMOUNT) AS TOTAL_SALARY, " +
            "SUM(SUM(p.AMOUNT)) OVER (PARTITION BY e.EMP_ID ORDER BY DATE_FORMAT(p.PAYMENT_TIME, '%Y-%m')) AS RUNNING_TOTAL, " +
            "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE " +
            "FROM EMPLOYEE e " +
            "JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID " +
            "GROUP BY e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, e.DOB, DATE_FORMAT(p.PAYMENT_TIME, '%Y-%m') " +
            "ORDER BY e.EMP_ID, MONTH", 
            nativeQuery = true)
    List<Map<String, Object>> getMonthlySalaryReport();
}
