# Easy Diff Flow

基于 Spring Boot 实现的 Diff 比较与审批系统，支持字段级变更比较（含嵌套对象），可结合主流工作流引擎（如 Activiti / Flowable / Camunda / JBPM 等）实现 OA 审批流程，也支持无工作流场景的直接变更处理。

## 功能特性

- **字段级差异比较**：支持对象间字段级差异比较，包括嵌套对象、集合、Map和数组的递归比较
- **工作流集成**：支持与主流工作流引擎（Activiti/Flowable/Camunda/JBPM）集成，实现审批流程
- **方法代理执行**：支持方法代理执行，审批通过后自动执行目标方法
- **注解驱动**：提供`@DiffApproval`注解，简化差异比较与审批流程的集成
- **灵活配置**：支持丰富的配置选项，可根据需求定制差异比较与审批流程
- **无工作流模式**：支持跳过审批流程，直接对比并应用变更

## 快速开始

### 1. 添加依赖

```xml
<dependency>
    <groupId>com.kronos</groupId>
    <artifactId>diff-flow-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- 如果使用Flowable工作流引擎 -->
<dependency>
    <groupId>org.flowable</groupId>
    <artifactId>flowable-spring-boot-starter</artifactId>
    <version>6.7.2</version>
</dependency>
```

### 2. 配置应用

在`application.properties`或`application.yml`中添加配置：

```properties
# 差异比较与审批系统配置
diff.flow.enabled=true

# 差异比较配置
diff.flow.diff.ignore-null-value=true
diff.flow.diff.ignore-empty-collection=true
diff.flow.diff.global-ignore-fields=id,createTime,updateTime,version

# 工作流配置
diff.flow.workflow.enabled=true
diff.flow.workflow.engine-type=flowable
diff.flow.workflow.default-process-key=diffApprovalProcess
```

### 3. 使用注解方式

```java
import com.kronos.diffflow.annotation.DiffApproval;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    /**
     * 更新用户信息，需要审批
     */
    @DiffApproval(processDefinitionKey = "userUpdateProcess", 
                 businessKeyExpression = "'user-' + #newUser.id")
    public User updateUser(User oldUser, User newUser) {
        // 方法体内的代码只有在审批通过后才会执行
        // 无需手动比较差异，框架会自动处理
        return userRepository.save(newUser);
    }
    
    /**
     * 更新用户状态，无需审批
     */
    @DiffApproval(skipApproval = true)
    public User updateUserStatus(User oldUser, User newUser) {
        // 直接执行，无需审批
        return userRepository.save(newUser);
    }
}
```

### 4. 使用API方式

```java
import com.kronos.diffflow.model.DiffResult;
import com.kronos.diffflow.service.DiffFlowService;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final DiffFlowService diffFlowService;
    
    public OrderService(DiffFlowService diffFlowService) {
        this.diffFlowService = diffFlowService;
    }
    
    /**
     * 比较订单差异并提交审批
     */
    public String compareAndSubmitOrder(Order oldOrder, Order newOrder) {
        String businessKey = "order-" + oldOrder.getId();
        return diffFlowService.compareAndSubmitForApproval(
            oldOrder, newOrder, businessKey, "orderApprovalProcess");
    }
    
    /**
     * 比较订单差异并直接应用变更（无需审批）
     */
    public DiffResult compareAndApplyOrder(Order oldOrder, Order newOrder) {
        return diffFlowService.compareAndApply(oldOrder, newOrder, "id", "createTime");
    }
    
    /**
     * 完成审批任务
     */
    public void approveOrder(String taskId, boolean approved, String comment) {
        diffFlowService.completeApprovalTask(taskId, approved, comment);
    }
}
```

## 配置说明

### 差异比较配置

```properties
# 是否忽略空值
diff.flow.diff.ignore-null-value=true

# 是否忽略空集合
diff.flow.diff.ignore-empty-collection=true

# 全局忽略的字段
diff.flow.diff.global-ignore-fields=id,createTime,updateTime,version

# 是否启用缓存
diff.flow.diff.enable-cache=true

# 缓存过期时间（毫秒）
diff.flow.diff.cache-expiration=3600000
```

### 工作流配置

```properties
# 是否启用工作流
diff.flow.workflow.enabled=true

# 工作流引擎类型（支持：activiti, flowable, camunda, jbpm）
diff.flow.workflow.engine-type=flowable

# 默认流程定义键
diff.flow.workflow.default-process-key=diffApprovalProcess

# 是否自动部署流程定义
diff.flow.workflow.auto-deploy=true

# 流程定义文件路径
diff.flow.workflow.process-definition-location=classpath:processes/
```

## 注解参数说明

`@DiffApproval`注解支持以下参数：

| 参数 | 说明 | 默认值 |
| --- | --- | --- |
| processDefinitionKey | 流程定义键 | 配置文件中的默认值 |
| businessKeyExpression | 业务键生成表达式（SpEL） | 自动生成UUID |
| skipApproval | 是否跳过审批 | false |
| sourceIndex | 源对象参数索引 | 0 |
| targetIndex | 目标对象参数索引 | 1 |
| ignoreFields | 忽略的字段 | {} |
| includeFields | 只比较的字段 | {} |
| autoApplyDiff | 是否在审批通过后自动应用差异 | true |

## 工作流集成

系统默认提供了一个基础的差异审批流程定义（`diff-approval-process.bpmn20.xml`），可以根据实际需求进行定制。

### 自定义流程定义

1. 在`src/main/resources/processes/`目录下创建自定义的流程定义文件
2. 在流程定义中使用系统提供的流程变量：
   - `diffResult`：差异结果
   - `businessKey`：业务键
   - `methodContext`：方法执行上下文
   - `approved`：是否批准
   - `comment`：审批意见

### 集成其他工作流引擎

系统默认支持Flowable工作流引擎，如需集成其他工作流引擎，可以实现`WorkflowService`接口并注册为Spring Bean。

## 高级用法

### 自定义差异比较逻辑

```java
import com.kronos.diffflow.service.DiffService;
import org.springframework.stereotype.Service;

@Service
public class CustomDiffService implements DiffService {
    // 实现自定义的差异比较逻辑
}
```

### 自定义工作流服务

```java
import com.kronos.diffflow.service.WorkflowService;
import org.springframework.stereotype.Service;

@Service
public class CustomWorkflowService implements WorkflowService {
    // 实现自定义的工作流服务逻辑
}
```

## 许可证

本项目采用 [Apache License 2.0](LICENSE) 许可证。
