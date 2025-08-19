package com.example.diffflowspringbootstarter;

import com.example.diffflowspringbootstarter.model.InvoiceRecords;
import com.example.diffflowspringbootstarter.model.Records;
import com.kronos.diffflow.enums.CodegenColumnHtmlTypeEnum;
import com.kronos.diffflow.model.DiffItem;
import com.kronos.diffflow.support.DiffEngine;
import com.kronos.diffflow.support.DiffRegistry;
import com.kronos.diffflow.support.function.FieldComparator;
import com.kronos.diffflowspring.DiffFlowCoreApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest(classes = DiffFlowCoreApplication.class)
class DiffFlowSpringBootStarterApplicationTests {

    @Test
    void contextLoads() {
        DiffRegistry.of("invoiceRecords-vs-records", InvoiceRecords.class, Records.class)
                .map(InvoiceRecords::getInvoiceStatus, Records::getInvoiceStatus, "开票状态", CodegenColumnHtmlTypeEnum.ENUM,   b -> b.comparator(FieldComparator.defaultComparator()).formatter(FieldComparator.noop()))
                .map(InvoiceRecords::getInvoiceAmount, Records::getAmount, "开票金额", CodegenColumnHtmlTypeEnum.MONEY,
                        b -> b.comparator(FieldComparator.defaultComparator()).formatter(FieldComparator.money2()))
                .map(InvoiceRecords::getBuyerName, Records::getBuyerName, "购方名称",
                        CodegenColumnHtmlTypeEnum.TEXT, b -> b.comparator(FieldComparator.ignoringBlank()))
                // 集合：items[].id 为键
//                .map(InvoiceRecords::getItems, Records::getItems, "明细列表", CodegenColumnHtmlTypeEnum.TEXT,
//                        b -> b.collectionKeyPath("items[].id"))
                .register();
        System.out.println(DiffRegistry.get("invoiceRecords-vs-records"));

        // 2. 构造数据
        InvoiceRecords invoice = new InvoiceRecords();
        invoice.setInvoiceStatus("SUCCESS");
        invoice.setInvoiceAmount(new BigDecimal("100.00"));
        invoice.setBuyerName("张三公司");

        Records record = new Records();
        record.setInvoiceStatus("FAILED");
        record.setAmount(new BigDecimal("100.005")); // 误差在 0.01 以内
        record.setBuyerName("张三公司");

        // 3. 执行对比
        List<DiffItem> diffs = DiffEngine.diff(invoice, record, DiffRegistry.get("invoiceRecords-vs-records"));

        // 4. 打印结果
        diffs.forEach(System.out::println);
    }

}
