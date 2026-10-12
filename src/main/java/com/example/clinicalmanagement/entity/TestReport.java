package com.example.clinicalmanagement.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "test_reports")
public class TestReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", referencedColumnName = "id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id", nullable = false)
    private Doctor doctor;

    @Column(name = "test_name", nullable = false, length = 150)
    private String testName;

    @Column(name = "test_category", nullable = false, length = 80)
    private String testCategory; // Pathology, Radiology, Cardiology, Biochemistry

    @Column(name = "test_date", nullable = false)
    private LocalDate testDate;

    @Column(name = "test_result", columnDefinition = "TEXT", nullable = false)
    private String testResult;

    @Column(name = "normal_range", length = 255)
    private String normalRange;

    @Column(nullable = false, length = 30)
    private String status; // NORMAL, ABNORMAL, PENDING

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "lab_reference_number", length = 50)
    private String labReferenceNumber;

    @Column(name = "test_fee", precision = 10, scale = 2)
    private BigDecimal testFee;

    @Column(name = "payment_status", length = 20, nullable = false)
    private String paymentStatus; // PAID, PENDING

    @Column(name = "payment_method", length = 30)
    private String paymentMethod; // UPI, CASH, NET_BANKING

    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public TestReport() {
        this.createdAt = LocalDateTime.now();
        this.paymentStatus = "PAID";
        this.status = "NORMAL";
    }

    public TestReport(Patient patient, Doctor doctor, String testName, String testCategory,
                      LocalDate testDate, String testResult, String normalRange, String status,
                      String remarks, String labReferenceNumber, BigDecimal testFee, String paymentStatus) {
        this.patient = patient;
        this.doctor = doctor;
        this.testName = testName;
        this.testCategory = testCategory;
        this.testDate = testDate;
        this.testResult = testResult;
        this.normalRange = normalRange;
        this.status = status;
        this.remarks = remarks;
        this.labReferenceNumber = labReferenceNumber;
        this.testFee = testFee;
        this.paymentStatus = paymentStatus;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestCategory() {
        return testCategory;
    }

    public void setTestCategory(String testCategory) {
        this.testCategory = testCategory;
    }

    public LocalDate getTestDate() {
        return testDate;
    }

    public void setTestDate(LocalDate testDate) {
        this.testDate = testDate;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getNormalRange() {
        return normalRange;
    }

    public void setNormalRange(String normalRange) {
        this.normalRange = normalRange;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getLabReferenceNumber() {
        return labReferenceNumber;
    }

    public void setLabReferenceNumber(String labReferenceNumber) {
        this.labReferenceNumber = labReferenceNumber;
    }

    public BigDecimal getTestFee() {
        return testFee;
    }

    public void setTestFee(BigDecimal testFee) {
        this.testFee = testFee;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
