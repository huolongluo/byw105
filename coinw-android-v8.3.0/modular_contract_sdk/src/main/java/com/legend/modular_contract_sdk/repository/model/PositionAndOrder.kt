package com.legend.modular_contract_sdk.repository.model

import com.google.gson.annotations.SerializedName
import com.legend.modular_contract_sdk.component.market_listener.MarketData
import java.io.Serializable


data class PositionAndOrder(
    @SerializedName("baseSize")
    val mBaseSize: Double,
    @SerializedName("closedPiece")
    val mClosedPiece: Double,
    @SerializedName("closedQuantity")
    val mClosedQuantity: Double,
    @SerializedName("contractType")
    val mContractType: Int,
    @SerializedName("createdDate")
    val mCreatedDate: Long,
    @SerializedName("currentPiece")
    val mCurrentPiece: Double,
    @SerializedName("remainCurrentPiece")
    val mRemainCurrentPiece: Double,
    @SerializedName("direction")
    val mDirection: String,
    @SerializedName("distId")
    val mDistId: Long,
    @SerializedName("fee")
    val mFee: Double,
    @SerializedName("fundingFee")
    val mFundingFee: Double,
    @SerializedName("fundingSettle")
    val mFundingSettle: Double,
    @SerializedName("hedgeId")
    val mHedgeId: Long,
    @SerializedName("id")
    val mId: Long,
    @SerializedName("openId")
    val mOpenId: Long,
    @SerializedName("instrument")
    val mInstrument: String,
    @SerializedName("isProfession")
    val mIsProfession: Int,
    @SerializedName("leaderId")
    val mLeaderId: Long,
    @SerializedName("leaderOrderId")
    val mLeaderOrderId: Long,
    @SerializedName("leverage")
    val mLeverage: Int,
    @SerializedName("margin")
    val mMargin: Double,
    @SerializedName("positionMargin")
    val mPositionMargin: Double,
    @SerializedName("openPrice")
    val mOpenPrice: Double,
//    @SerializedName("executePrice")
//    val mExecutePrice: Double,
    @SerializedName("orderPrice")
    val mOrderPrice: Double,
    @SerializedName("originalType")
    val mOriginalType: String,
    @SerializedName("parentId")
    val mParentId: Long,
    @SerializedName("partnerId")
    val mPartnerId: Long,
    @SerializedName("posType")
    val mPosType: String,
    @SerializedName("positionModel")
    val mPositionModel: Int,
    @SerializedName("profitUnreal")
    val mProfitUnreal: Double,
    @SerializedName("quantity")
    val mQuantity: Double,
    @SerializedName("quantityUnit")
    val mQuantityUnit: Int,
    @SerializedName("salesId")
    val mSalesId: Int,
    @SerializedName("settlementId")
    val mSettlementId: Int,
    @SerializedName("source")
    val mSource: String,
    @SerializedName("status")
    val mStatus: String,
    @SerializedName("stopLossPrice")
    val mStopLossPrice: Double,
    @SerializedName("stopLossRate")
    val mStopLossRate: Double,
    @SerializedName("stopProfitPrice")
    val mStopProfitPrice: Double,
    @SerializedName("stopProfitRate")
    val mStopProfitRate: Double,
    @SerializedName("totalPiece")
    val mTotalPiece: Double,
    @SerializedName("triggerPrice")
    val mTriggerPrice: Double,
    @SerializedName("triggerType")
    val mTriggerType: Int,
    @SerializedName("updatedDate")
    val mUpdatedDate: Long,
    @SerializedName("netProfit")
    val mNetProfit: Double,
    @SerializedName("closePrice")
    val mClosePrice: Double,
    @SerializedName("liquidateBy")
    val mLiquidateBy: String,
    @SerializedName("isShare")
    val mIsShare: Int,
    @SerializedName("userId")
    val mUserId: Int,
    @SerializedName("callbackRate")
    val mCallbackRate: Double, // 移动止盈止损回调率
    @SerializedName("finishStatus")
    val mFinishStatus: Int, // 移动止盈止损状态 0-未激活 1-已激活 2-已触发 3-已撤销（前端只需判断0，1）
//    @SerializedName("triggerPrice")
//    val mTriggerPrice: Int,
    var mIsExperienceGold: Boolean = false

) : MarketData() , Comparable<PositionAndOrder>, Serializable{
    override fun compareTo(other: PositionAndOrder): Int {
        return if (other.mCreatedDate > this.mCreatedDate){
            1
        } else if (other.mCreatedDate < this.mCreatedDate){
            -1
        } else {
            0
        }
    }

}