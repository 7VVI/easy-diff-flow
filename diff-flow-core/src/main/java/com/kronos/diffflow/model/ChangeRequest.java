package com.kronos.diffflow.model;

import com.kronos.diffflow.enums.ChangeStatus;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * @author zhangyh
 * @Date 2025/8/19 9:35
 * @desc 审批变更单（聚合根），记录“如何从当前正式数据”生成“拟生效新数据
 */
@Data
public class ChangeRequest {
    private String       id;                 // 审批单ID
    private String       bizKey;             // 业务主键（如 invoiceId）
    private Long         baseVersion;         // 发起变更时读到的正式版本号（仅参考）
    private ChangeStatus status;       // DRAFT/IN_REVIEW/APPROVED/REJECTED/CANCELED
    private Instant      createdAt;
    private Instant      updatedAt;

    // 累积补丁（应用到“当前最新正式数据”上得到最终right）
    // 建议存 JSON Merge Patch (RFC7396) 或 JSON Patch (RFC6902)
    private String mergedPatchJson;

    // 可选：每次“用户编辑”产生一条子补丁，最终提交前自动合并到 mergedPatchJson
    private List<String> editPatches;
}
