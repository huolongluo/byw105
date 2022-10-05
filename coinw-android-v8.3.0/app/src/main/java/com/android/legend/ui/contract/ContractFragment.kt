package com.android.legend.ui.contract

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.TextView
import com.android.legend.base.BaseFragment
import com.legend.modular_contract_sdk.api.ModularContractSDK
import com.legend.modular_contract_sdk.ui.contract.SwapContractFragment
import huolongluo.byw.R
import huolongluo.byw.byw.net.UrlConstants
import huolongluo.byw.byw.ui.fragment.contractTab.ContractUserInfoEntity
import huolongluo.byw.heyue.HeYueUtil
import huolongluo.byw.log.Logger
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.GsonUtil
import huolongluo.byw.util.OkhttpManager
import huolongluo.byw.util.OkhttpManager.DataCallBack
import okhttp3.Request
import java.util.*

class ContractFragment :BaseFragment() {

    companion object{
        @JvmStatic
        fun getInstance() = ContractFragment()
    }

    lateinit var tvFreeze: TextView

    private val swapContractFragment by lazy { SwapContractFragment.Companion.getInstance() }

    override fun getContentViewId(): Int = R.layout.fragment_contract

    override fun initView(view: View) {

        tvFreeze = view.findViewById(R.id.tv_freeze)

        childFragmentManager
                .beginTransaction()
                .replace(R.id.fl_container, swapContractFragment)
                .commit()
    }

    override fun initData() {

    }

    override fun onResume() {
        Logger.getInstance().error("ContractFragment onResume")
        super.onResume()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        swapContractFragment.userVisibleHint = isVisibleToUser
        Logger.getInstance().error("ContractFragment setUserVisibleHint $isVisibleToUser")
        if(isVisibleToUser){
            if (UserInfoManager.isLogin()){
                getContractUserInfo()
            }
        }
    }

    private fun getContractUserInfo() {
        if (TextUtils.isEmpty(UserInfoManager.getToken())) {
            return
        }
        val params: MutableMap<String, String> = HashMap()
        params["loginToken"] = UserInfoManager.getToken()
        OkhttpManager.postAsync(UrlConstants.GET_HY_USER, params, object : DataCallBack {
            override fun requestFailure(request: Request, e: Exception, errorMsg: String) {
                Logger.getInstance().error(errorMsg, e)
            }

            override fun requestSuccess(result: String) {
                Log.e("CONTRACT_TYPES", result)
                val contractUserInfoEntity = GsonUtil.json2Obj(result, ContractUserInfoEntity::class.java)
                if (null != contractUserInfoEntity && null != contractUserInfoEntity.data) {
                    val data = contractUserInfoEntity.data
                    tvFreeze.setVisibility(View.GONE)
                    if (data.status == 0) { //未开通
                        HeYueUtil.getInstance().openHY()
                    } else if (data.status == 1) { //已开通
                        ModularContractSDK.login(data.token)
                    } else if (data.status == 2) { //冻结
                        tvFreeze.setVisibility(View.VISIBLE)
                    }
                }
            }
        })
    }

    override fun applyTheme() {
        super.applyTheme()
        swapContractFragment.applyTheme()
    }
}