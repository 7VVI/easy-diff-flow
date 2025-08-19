package com.example.diffflowspringbootstarter.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhangyh
 * @Date 2025/8/19 11:17
 * @desc
 */
@Data
public class InvoiceRecords {
    private String invoiceStatus;
    private BigDecimal invoiceAmount;
    private String buyerName;
}
