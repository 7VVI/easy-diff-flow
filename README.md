# Easy Diff Flow

基于 Spring Boot 实现的 Diff 比较与审批系统，支持字段级变更比较（含嵌套对象），可结合主流工作流引擎（如 Activiti / Flowable / Camunda / JBPM 等）实现 OA 审批流程，也支持无工作流场景的直接变更处理。

## 功能特性

- 字段级差异比较：支持对象间字段级差异比较，包括嵌套对象、集合、Map和数组的递归比较
- 工作流集成：支持与主流工作流引擎（Activiti/Flowable/Camunda/JBPM）集成，实现审批流程
- 方法代理执行：支持方法代理执行，审批通过后自动执行目标方法
- 注解驱动：提供`@DiffApproval`注解，简化差异比较与审批流程的集成
- 灵活配置：支持丰富的配置选项，可根据需求定制差异比较与审批流程
- 无工作流模式：支持跳过审批流程，直接对比并应用变更
- 智能比较&格式化解析：基于字段类型与`htmlType`自动选择合适的比较器与格式化器，避免空指针并降低配置成本

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.kronos</groupId>
    <artifactId>diff-flow-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. 注册规则（非工作流直用场景）

通过 `DiffRegistry` 以函数式链式方式注册字段映射关系，可选指定字段展示名、字段类型(`htmlType`)、比较器与格式化器：

```java
DiffRegistry.of("invoiceRecords-vs-records", InvoiceRecords.class, Records.class)
    .map(InvoiceRecords::getInvoiceStatus, Records::getInvoiceStatus, "开票状态", CodegenColumnHtmlTypeEnum.ENUM)
    .map(InvoiceRecords::getInvoiceAmount, Records::getAmount, "开票金额", CodegenColumnHtmlTypeEnum.MONEY)
    .map(InvoiceRecords::getInvoiceNo, Records::getInvoiceNo, "发票号", CodegenColumnHtmlTypeEnum.TEXT,
         b -> b.comparator(FieldComparator.ignoringBlank()))
    .map(InvoiceRecords::getItems, Records::getItems, "明细列表", CodegenColumnHtmlTypeEnum.TEXT,
         b -> b.collectionKeyPath("id"))
    .register();
```

对比并生成差异：

```java
List<DiffItem> diffs = DiffEngine.diff(invoice, record, DiffRegistry.get("invoiceRecords-vs-records"));
```

### 3. 使用注解（工作流与审批集成示例）

```java
@DiffApproval(processDefinitionKey = "userUpdateProcess", businessKeyExpression = "'user-' + #newUser.id")
public User updateUser(User oldUser, User newUser) {
    return userRepository.save(newUser);
}
```

## 比较器与格式化器解析策略

系统提供“显式优先，智能兜底”的解析策略，极大减少手工配置：

- 显式优先：若在 `DiffRule` 上显式设置了 `comparator` 或 `formatter`，则直接使用该配置。
- 其后按 `htmlType` 选择：
  - MONEY：比较器使用 `doubleWithEpsilon(…)` 容忍微小误差；格式化器使用 `FieldFormatter.money()` 输出两位小数。
  - NUMBER：比较器使用 `doubleWithEpsilon(0.001)` 容忍 0.001 级误差；格式化器默认 `noop()`。
  - TEXT：比较器 `ignoringBlank()` 忽略前后空白；格式化器默认 `noop()`。
  - ENUM / DATE：比较器使用默认严格比较；格式化器默认 `noop()`。
- 若未设置 `htmlType`，按字段类型兜底：
  - BigDecimal/Double/Float：`doubleWithEpsilon(0.001)`
  - String：`ignoringBlank()`
  - java.util.Date/java.time.LocalDate/LocalDateTime：默认严格比较
  - 其它：默认比较
- 任何情况下若未解析到比较器/格式化器，将回退到：`FieldComparator.defaultComparator()` 与 `FieldFormatter.noop()`。

对应实现：
- 比较器解析：`DefaultComparatorResolver`，入口 `ComparatorResolver#resolve`。
- 格式化器解析：`DefaultFormatterResolver`，入口 `FormatterResolver#resolve`（对 MONEY 返回 `FieldFormatter.money()`，其它返回 `noop()`）。

## 空值与NPE防护

- 当左右值均为 `null` 且未显式指定比较器时，该字段视为“无差异”，直接跳过，避免不必要的比较与潜在 NPE。
- 所有比较均经过空安全处理，默认比较器内部使用 `Objects.equals` 等安全比较。

## 路径访问能力

引擎使用 `ReflectivePathAccessor` 通过路径读取对象字段，支持：
- 普通属性访问（如 `user.name`）
- 集合与数组索引（如 `items[0].id`）
- Map 键访问（如 `ext["key"]`）

## 常用API

- 规则注册：`DiffRegistry.of(name, Left.class, Right.class).map(...).register()`
- 执行对比：`DiffEngine.diff(left, right, DiffRegistry.get(name))`
- 枚举类型：`CodegenColumnHtmlTypeEnum { TEXT, NUMBER, DATE, ENUM, MONEY }`
- 手动指定：
  - 比较器：`FieldComparator.defaultComparator() / equalsComparator() / doubleWithEpsilon(eps) / ignoringBlank()`
  - 格式化器：`FieldFormatter.noop() / money()`

## 示例：金额与文本对比

```java
DiffRegistry.of("demo", L.class, R.class)
  .map(L::getAmount, R::getAmount, "金额", CodegenColumnHtmlTypeEnum.MONEY)
  .map(L::getComment, R::getComment, "备注", CodegenColumnHtmlTypeEnum.TEXT)
  .register();

List<DiffItem> diffs = DiffEngine.diff(l, r, DiffRegistry.get("demo"));
// 金额将按两位小数格式化输出，文本比较将忽略前后空白
```

## 配置说明（节选）

```properties
# 差异比较配置
diff.flow.diff.ignore-null-value=true
# ... 其它同原有说明
```

## 许可证

本项目采用 Apache License 2.0 许可证，详见 [LICENSE](LICENSE)。
