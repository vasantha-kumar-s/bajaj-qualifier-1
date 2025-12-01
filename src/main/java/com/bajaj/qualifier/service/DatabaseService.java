package com.bajaj.qualifier.service;

import com.bajaj.qualifier.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DatabaseService {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseService.class);
    
    @Autowired(required = false)
    private EmployeeRepository employeeRepository;
    
    public boolean testDatabaseConnection() {
        try {
            if (employeeRepository == null) {
                return false;
            }
            
            List<Map<String, Object>> results = employeeRepository.getMonthlySalaryReport();
            logger.info("DB query OK - {} rows", results.size());
            return true;
        } catch (Exception e) {
            logger.error("DB error: {}", e.getMessage());
            return false;
        }
    }
}
