package com.legend.modular_contract_sdk.ui.contract

/**
 * 开仓方向
 */
enum class Direction(val direction: String) {
    LONG("long"),
    SHORT("short"),
}

/**
 * 下单数量单位
 */
enum class QuantityUnit(val unit: Int) {
    USDT(0),
    SIZE(1),// 张
    COIN(2) // 币
}

/**
 * 仓位模式
 */
enum class PositionMode(val mode: Int) {
    PART(0),
    FULL(1)
}

/**
 * 仓位分仓模式
 */
enum class PositionMergeMode(val mode: Int) {
    MERGE(0), // 合仓
    PARTITION(1) // 分仓
}

/**
 * 仓位类型
 * EXECUTE          市价单
 * PLAN             限价委托
 * PLAN_TRIGGER     计划委托
 */
enum class PositionType(val type: String) {
    EXECUTE("execute"),
    PLAN("plan"),
    PLAN_TRIGGER("planTrigger"),
}

/**
 * 合约类型
 * PERPETUAL    永续合约
 * LIGHTNING    闪电合约
 */
enum class ContractType(val type: Int) {
    PERPETUAL(1),
    LIGHTNING(0)
}

/**
 * 计划委托触发类型
 * PLAN 限价
 * EXECUTE 市价
 */
enum class TriggerType(val type: Int) {
    PLAN(0),
    EXECUTE(1)
}

/**
 * 止盈止损设置类型
 * PERCENT 比例
 * PRICE 价格
 */
enum class TriggerSetType() {
    PERCENT,
    PRICE
}

/**
 * 平仓类型
 * PERCENTAGE 比例
 * SHEET 张数
 */
enum class ClosePositionType(val type: Int) {
    PERCENTAGE(1),
    SHEET(2)
}

/**
 * 加仓类型
 * AMOUNT 金额USDT
 * SHEET 张数
 */
enum class AddPositionType(val type: Int) {
    AMOUNT(0),
    SHEET(1)
}

/**
 * 体验金状态类型
 * USED 已使用
 * EXPIRED 已过期
 * 0-待激活 1-待使用 3-已使用 4-已过期
 */
enum class ExperienceGoldType(val type: Int) {
    NOT_ACTIVE(0),
    ACTIVE(1),
    USED(3),
    EXPIRED(4)
}

/**
 *
 *开仓类型 OPEN开仓  CLOSE平仓
 *
 */
enum class OpenPositionType(val type: String) {
    OPEN("open"),
    CLOSE("close")
}

/**
 * 移动止盈止损状态
 * 移动止盈止损状态 0-未激活 1-已激活 2-已触发 3-已撤销（前端只需判断0，1）
 */
enum class MoveTPSLStatus(val status: Int) {
    INACTIVATED(0),
    ACTIVATED(1),
    TRIGGERED(2),
    REVOKED(3)
}