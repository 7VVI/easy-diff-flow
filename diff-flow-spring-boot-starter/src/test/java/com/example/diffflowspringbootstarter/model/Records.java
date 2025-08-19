package com.example.diffflowspringbootstarter.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author zhangyh
 * @Date 2025/8/19 11:17
 * @desc
 */
@Data
public class Records {
    private String invoiceStatus;
    private BigDecimal amount;
    private String buyerName;

    private List<Item> items;
}
