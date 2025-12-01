package com.bajaj.qualifier.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAYMENTS")
public class Payment {
    
    @Id
    @Column(name = "PAYMENT_ID")
    private Integer paymentId;
    
    @Column(name = "EMP_ID")
    private Integer empId;
    
    @Column(name = "AMOUNT")
    private BigDecimal amount;
    
    @Column(name = "PAYMENT_TIME")
    private LocalDateTime paymentTime;

    // Constructors
    public Payment() {
    }

    public Payment(Integer paymentId, Integer empId, BigDecimal amount, LocalDateTime paymentTime) {
        this.paymentId = paymentId;
        this.empId = empId;
        this.amount = amount;
        this.paymentTime = paymentTime;
    }

    // Getters and Setters
    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(LocalDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }
}
