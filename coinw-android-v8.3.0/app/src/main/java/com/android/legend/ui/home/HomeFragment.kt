package com.android.legend.ui.home

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.webkit.URLUtil
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.android.coinw.biz.event.BizEvent.ChangeExchangeRate
import com.android.legend.extension.gone
import com.android.legend.extension.visible
import com.android.legend.model.home.*
import com.android.legend.socketio.SocketIOClient.subscribe
import com.android.legend.socketio.SocketIOClient.unsubscribe
import com.android.legend.ui.login.LoginActivity
import com.android.legend.ui.market.MarketSearchActivity
import com.android.legend.ui.mine.MineActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.reflect.TypeToken
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.base.BaseFragment
import com.legend.modular_contract_sdk.component.adapter.DataBindingRecyclerViewAdapter
import com.mob.tools.utils.ResHelper
import com.to.aboomy.pager2banner.IndicatorView
import com.umeng.analytics.MobclickAgent
import com.uuzuche.lib_zxing.activity.CodeUtils
import huolongluo.byw.BR
import huolongluo.byw.R
import huolongluo.byw.byw.bean.MarketListBean
import huolongluo.byw.byw.inform.activity.MailActivity
import huolongluo.byw.byw.inform.activity.NoticeActivity
import huolongluo.byw.byw.inform.activity.NoticeDetailActivity
import huolongluo.byw.byw.net.UrlConstants
import huolongluo.byw.byw.share.Event.SwitchHomeUi
import huolongluo.byw.byw.share.Event.refreshInfo

import huolongluo.byw.byw.ui.activity.main.MainActivity
import huolongluo.byw.byw.ui.fragment.maintab01.home.HomeDynamicHandler
import huolongluo.byw.byw.ui.fragment.maintab05.ScanQRLoginActivity
import huolongluo.byw.databinding.FragmentHomeBinding
import huolongluo.byw.helper.FaceVerifyHelper
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.reform.home.activity.NewsWebviewActivity
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.Constant
import huolongluo.byw.util.SPUtils
import huolongluo.byw.util.tip.DialogUtils
import huolongluo.byw.util.tip.MToast
import huolongluo.byw.util.zxing.MipcaActivityCapture
import huolongluo.bywx.utils.AppUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class HomeFragment : BaseFragment<HomeViewModel>() {

    companion object {
        @JvmStatic
        fun getInstance() = HomeFragment()
    }

    lateinit var mBinding: FragmentHomeBinding

    lateinit var mBannerAdapter: BaseQuickAdapter<Banner, BaseViewHolder>

    lateinit var mRecommendAdapter : DataBindingRecyclerViewAdapter<TickerWrap>

    var mCoinListFragments = mutableListOf<BaseFragment<HomeViewModel>>()

    var mHomeDynamicHandler : HomeDynamicHandler? = null

    var mRecommendCoinList = mutableListOf<Pair>()// 推荐区币种
    var mMainCoinList = mutableListOf<Pair>() // 主流榜币种
    var mNewCoinList = mutableListOf<Pair>() // 新币榜币种
    var mVolumeCoinList :List<Ticker2> = mutableListOf<Ticker2>() // 成交额榜币种
    var mUpCoinList :List<Ticker2> = mutableListOf<Ticker2>() // 涨幅榜币种

    override fun createViewModel(): HomeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

    override fun createRootView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentHomeBinding.inflate(inflater)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
        initLiveData()
    }

    private fun initView() {

        mHomeDynamicHandler = HomeDynamicHandler()
        mHomeDynamicHandler?.init(context, mBinding.includeDynamicMenu.root)

        mBinding.newUserHomeHeaderView.setCallback {
            switchUIMode(true)
        }

        mBinding.includeToolBar.ivMine.setOnClickListener {
            MineActivity.launch(requireContext())
        }

        mBinding.includeToolBar.scanIv.setOnClickListener {
            // 扫码登录
            if (UserInfoManager.isLogin()) {
                val intent = Intent(activity, MipcaActivityCapture::class.java)
                startActivityForResult(intent, 1)
            } else {
                DialogUtils.getInstance().showTwoButtonDialog1(activity, getString(R.string.dd2), getString(R.string.dd7), getString(R.string.dd6))
                DialogUtils.getInstance().setOnclickListener(object : DialogUtils.onBnClickListener() {
                    override fun onLiftClick(dialog: AlertDialog, view: View) {
                        dialog.dismiss()
                    }

                    override fun onRightClick(dialog: AlertDialog, view: View) {
                        dialog.dismiss()
                        startActivity(Intent(requireContext(), LoginActivity::class.java))
                    }
                })
            }
        }

        mBinding.includeToolBar.newsIv.setOnClickListener {
            //  站内信
            if (!UserInfoManager.isLogin()) {
                startActivity(Intent(activity,LoginActivity::class.java))
                return@setOnClickListener
            }
            startActivity(Intent(activity, MailActivity::class.java))
        }

        mBinding.includeToolBar.rlSearch.setOnClickListener {
            //  搜索
            MarketSearchActivity.newInstance(requireContext())
        }

        switchUIMode()

        mBannerAdapter = object : BaseQuickAdapter<Banner, BaseViewHolder>(R.layout.home_banner_item) {

            override fun convert(holder: BaseViewHolder, item: Banner) {
                var imgView = holder.getView(R.id.imgView) as ImageView
                val ro = RequestOptions()
                ro.placeholder(R.drawable.banner_default)
                ro.error(R.drawable.banner_default)
                Glide.with(requireContext()).load(item.img).into(imgView)
                imgView.setOnClickListener {
                    val intent = Intent(requireContext(), NewsWebviewActivity::class.java)

                    MobclickAgent.onEvent(requireContext(), Constant.UMENG_EVENT_PRE_BANNER1 + (holder.adapterPosition + 1), item.id.toString() + "")

                    val bannerUri = Uri.parse(item.url)
                    val type = bannerUri.getQueryParameter("type")
                    val parentType = bannerUri.getQueryParameter("parentType")
                    val channel = bannerUri.getQueryParameter("channel")

                    // 如果是活动banner 先diao'yong上报接口再启动活动页面
                    if ((!type.isNullOrEmpty()) && (!parentType.isNullOrEmpty())){
                        if (UserInfoManager.isLogin()) {
                            getViewModel().postClickActivityBanner(item, type, parentType, channel)
                        } else {
                            startActivity(Intent(requireContext(), LoginActivity::class.java))
                        }
                    } else {
                        if (item.whether == 1) {
                            intent.putExtra("url", item.url)
                            intent.putExtra("token", UserInfoManager.getToken())
                            intent.putExtra("useH5Title", true)
                            startActivity(intent)
                        } else {
                            if (!TextUtils.equals(item.img, "error")) {
                                val intent1 = Intent(requireContext(), NoticeDetailActivity::class.java)
                                intent1.putExtra("id", item.newid.toInt())
                                startActivity(intent1)
                            }
                        }
                    }

                }
            }

        }
        mBinding.banner.adapter = mBannerAdapter

        val indicator: IndicatorView = IndicatorView(this.context)
                .setIndicatorColor(ThemeUtil.getThemeColor(requireContext(), R.attr.col_bg_edit))
                .setIndicatorSelectorColor(ThemeUtil.getThemeColor(requireContext(), R.attr.colorAccent))
                .setIndicatorRatio(10f)
                .setIndicatorSelectedRatio(10f)
                .setIndicatorSpacing(0f)
                .setIndicatorRadius(1f)
                .setIndicatorSelectedRadius(1f)
                .setParams(
                        RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                                .apply {
                                    addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                                    addRule(RelativeLayout.CENTER_HORIZONTAL)
                                    bottomMargin = 0
                                }
                )
                .setIndicatorStyle(IndicatorView.IndicatorStyle.INDICATOR_CIRCLE_RECT)

        mBinding.banner.setIndicator(indicator)


        mBinding.marqueeView.setOnItemClickListener { _, _ ->
            startActivity(Intent(requireActivity(), NoticeActivity::class.java))
        }

        mRecommendAdapter = DataBindingRecyclerViewAdapter(context, R.layout.item_home_recommend_coin, BR.ticker, mutableListOf())
        mBinding.rvRecommend.layoutManager = LinearLayoutManager(context).apply { orientation = RecyclerView.HORIZONTAL }
        mBinding.rvRecommend.adapter = mRecommendAdapter
        mRecommendAdapter.setOnItemClickListener{view1, position ->

            if (mRecommendAdapter == null) {
                return@setOnItemClickListener
            }

            val ticker = mRecommendAdapter.getAllData().get(position)

            if (ticker != null) {
                MobclickAgent.onEvent(getContext(), Constant.UMENG_EVENT_PRE_B + (position + 1), ticker.getBaseName())
                MainActivity.self.gotoTrade(MarketListBean(ticker.getId(), ticker.getRealQuoteName(), ticker.getBaseName()))
            }

        }

    }

    override fun onResume() {
        super.onResume()
        // 刷新用户状态
        if (userVisibleHint && !SPUtils.isProHome(context)) {
            mBinding.newUserHomeHeaderView.refreshStepInfo()
        }
        getMailUnread()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (!AppUtils.isNetworkConnected()) {
            return
        }
        getMailUnread()
        if (isVisibleToUser) {
            if (SPUtils.isProHome(context)) {
                //TODO 测试专用
                mHomeDynamicHandler?.refresh()
            } else {
                if (::mBinding.isInitialized){
                    mBinding.newUserHomeHeaderView.refreshStepInfo() // 更新新手引导状态
                }
            }
            registerEvent()
        } else {
            unregisterEvent()
        }
    }

    private fun getMailUnread(){
        if(!userVisibleHint||!::mBinding.isInitialized) return
        mViewModel.getMailUnread()
    }

    private fun switchUIMode(showAnimation:Boolean = false) {
        if (SPUtils.isProHome(context)) {
            mBinding.proHomeHeader.visibility = View.VISIBLE
            mBinding.newUserHomeHeaderView.visibility = View.GONE

            // 专业版首页
            val titles = mutableListOf<String>(getString(R.string.zhangfub), getString(R.string.str_new_coins), getString(R.string.home_turnover))
            mCoinListFragments = mutableListOf(
                    CoinListFragment.getInstance(false, CoinListFragment.TYPE_UP),
                    CoinListFragment.getInstance(false, CoinListFragment.TYPE_NEW),
                    CoinListFragment.getInstance(true, CoinListFragment.TYPE_VOLUEM)
            )

            mBinding.vpCoinList.adapter = HomeViewPagerAdapter(this, mCoinListFragments)

            TabLayoutMediator(mBinding.tabCoinList, mBinding.vpCoinList) { tab, position ->
                tab.text = titles[position]
            }.attach()
        } else {

            mBinding.proHomeHeader.visibility = View.GONE
            mBinding.newUserHomeHeaderView.visibility = View.VISIBLE

            val titles = mutableListOf<String>(getString(R.string.zhuliub), getString(R.string.zhangfub), getString(R.string.home_turnover))
            mCoinListFragments = mutableListOf(
                    CoinListFragment.getInstance(false, CoinListFragment.TYPE_MAIN),
                    CoinListFragment.getInstance(false, CoinListFragment.TYPE_UP),
                    CoinListFragment.getInstance(true, CoinListFragment.TYPE_VOLUEM)
            )

            mBinding.vpCoinList.adapter = HomeViewPagerAdapter(this, mCoinListFragments)

            TabLayoutMediator(mBinding.tabCoinList, mBinding.vpCoinList) { tab, position ->
                tab.text = titles[position]
            }.attach()
        }

        if (showAnimation) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val x: Int
                val y: Int
                val screenSize = ResHelper.getScreenSize(context)
                val screenWidth = screenSize[0]
                val screenHeight = screenSize[1]
                if (SPUtils.isProHome(context)) {
                    x = screenWidth
                    y = (screenHeight * 0.05).toInt()
                } else {
                    x = (screenWidth * 0.5).toInt()
                    y = (screenHeight * 0.95).toInt()
                }
                ViewAnimationUtils.createCircularReveal(view, x, y, 0f, Math.hypot(screenWidth.toDouble(), screenHeight.toDouble()).toFloat()).setDuration(500).start()
            } else {
                mBinding.root.startAnimation(AnimationUtils.loadAnimation(context, R.anim.otc_act_in))
            }
        }

    }

    private fun initData() {
        mViewModel.fetchBannerList()
        mViewModel.fetchNoticeList()
        mViewModel.fetchCoinList()
//        mViewModel.fetchDynamicMenu()
    }

    private fun initLiveData() {
        mViewModel.mBannerLiveDate.observe(viewLifecycleOwner, Observer {
            applyBanner(it)
        })

        mViewModel.mNoticeLiveDate.observe(viewLifecycleOwner, Observer {
            applyNotice(it)
        })

        mViewModel.mCoinListLiveDate.observe(viewLifecycleOwner, Observer {
            mRecommendCoinList = it.recommendeds.toMutableList()
            mMainCoinList = it.mainCoins.toMutableList()
            mNewCoinList = it.newCoins.toMutableList()
        })

        mViewModel.mDynamicMenuLiveDate.observe(viewLifecycleOwner, Observer {
            applyDynamicMenu(it)
        })

        mViewModel.mPostClickActivityBannerLiveData.observe(viewLifecycleOwner, Observer {item: Banner ->
            val intent = Intent(requireContext(), NewsWebviewActivity::class.java)
            if (item.whether == 1) {
                intent.putExtra("url", item.url)
                intent.putExtra("token", UserInfoManager.getToken())
                intent.putExtra("useH5Title", true)
                startActivity(intent)
            } else {
                if (!TextUtils.equals(item.img, "error")) {
                    val intent1 = Intent(requireContext(), NoticeDetailActivity::class.java)
                    intent1.putExtra("id", item.newid.toInt())
                    startActivity(intent1)
                }
            }
        })
        mViewModel.mMailUnreadLiveData.observe(viewLifecycleOwner, Observer {
            if(it.isSuccess){
                if(it.data?.unReadCount?:0>0){//有未读
                    mBinding.includeToolBar.ivMailRemind.visible()
                }else{
                    mBinding.includeToolBar.ivMailRemind.gone()
                }
            }
        })
    }

    private fun applyDynamicMenu(dynamicHomeMenu: DynamicHomeMenu?) {

    }

    private fun applyNotice(notices: List<Notice>?) {

        notices?.let {
            mBinding.marqueeView.startWithList(it.map { it.title })
        }
    }

    private fun applyBanner(banners: BannerBean?) {
        banners?.let {
            banners?.let {
                mBannerAdapter.setNewInstance(it.bannerList.toMutableList())
            }

        }
    }

    private fun registerEvent() {
        subscribe<Map<String, Ticker>>("HomeFragment", AppConstants.SOCKET.SPOT_HOME_MARKET_PART1, object : TypeToken<Map<String, Ticker>>() {}) { bean: Map<String, Ticker>? ->
//            Logger.getInstance().error("SPOT_HOME_MARKET_PART1"+bean.toString())
            if(mRecommendCoinList.isNotEmpty() ){
                mRecommendCoinList.forEach {
                    it.ticker = bean?.get(it.pairId.toString())
                }
                mRecommendAdapter.refreshData(mRecommendCoinList.map { TickerWrap(ticker = it.ticker) })
            }

            if (mMainCoinList.isNotEmpty()){
                mMainCoinList.forEach {
                    it.ticker = bean?.get(it.pairId.toString())
                }
                mViewModel.mMainCoinListLiveData.postValue(mMainCoinList)
            }

            if (mNewCoinList.isNotEmpty()){
                mNewCoinList.forEach {
                    it.ticker = bean?.get(it.pairId.toString())
                }
                mViewModel.mNewCoinListLiveData.postValue(mNewCoinList)
            }

            null
        }
        subscribe<HomePairList2>("HomeFragment", AppConstants.SOCKET.SPOT_HOME_MARKET_PART2, object : TypeToken<HomePairList2>() {}) { bean: HomePairList2? ->
            Logger.getInstance().error("SPOT_HOME_MARKET_PART2"+bean.toString())
            bean?.let {
                mUpCoinList = it.riseList
                mVolumeCoinList = it.legalMoneyList

                mViewModel.mUpCoinListLiveData.postValue(mUpCoinList)
                mViewModel.mVolumeCoinListLiveData.postValue(mVolumeCoinList)
            }


            null
        }

    }

    private fun unregisterEvent() {
        unsubscribe("HomeFragment", AppConstants.SOCKET.SPOT_HOME_MARKET_PART1)
        unsubscribe("HomeFragment", AppConstants.SOCKET.SPOT_HOME_MARKET_PART2)
    }

    //点击通知做跳转
    fun toPage(notification: String?) {
        if (mHomeDynamicHandler != null) {
            mHomeDynamicHandler?.toPage(notification)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (data != null) {
                val bundle = data.extras ?: return
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    val ret = bundle.getString(CodeUtils.RESULT_STRING)
                    // showLoginDialog(ret);
                    if (aliVerify(ret)) {
                        return
                    }
                    if (!URLUtil.isValidUrl(ret) || TextUtils.isEmpty(ret)) {
                        MToast.show(activity, getString(R.string.dd3), 1)
                        return
                    } else if (!ret.contains("/user/appQrcode/login.html")) {
                        MToast.show(activity, getString(R.string.dd3), 1)
                        return
                    }
                    MToast.show(activity, getString(R.string.dd5), 1)
                    val intent = Intent(activity, ScanQRLoginActivity::class.java)
                    intent.putExtra("loginUrl", ret)
                    startActivityForResult(intent, 100)
                }
            }
        }
    }

    private fun aliVerify(ret: String): Boolean {
        if (ret.contains("faceIdVerifyToken")) { //该二维码用于阿里人脸认证
            FaceVerifyHelper.getInstance().verify(context, ret)
            return true
        }
        return false
    }

    override fun applyTheme(){

        mBinding.invalidateAll()

        if (::mRecommendAdapter.isInitialized){
            mRecommendAdapter.notifyDataSetChanged()
        }

        mCoinListFragments.forEach {
            it.applyTheme()
        }
    }

    //本地计价方式切换需要马上刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun refreshPricingMethod(event: ChangeExchangeRate?) {
        if (::mRecommendAdapter.isInitialized){
            mRecommendAdapter.notifyDataSetChanged()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: SwitchHomeUi) {
        SPUtils.saveProHome(context, !event.isNewbie)
        switchUIMode()
    }

    /**
     * 刷新userInfo后，更新新主页配置
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: refreshInfo?) {
        if (UserInfoManager.isLogin()) {
            val userInfo = UserInfoManager.getUserInfo()
            SPUtils.saveProHome(context, !userInfo.isNewComer)
        }
        switchUIMode()
    }

    class HomeViewPagerAdapter(val fragment:Fragment,val fragments: List<Fragment>) : FragmentStateAdapter(fragment){
        override fun getItemCount(): Int = 3

        override fun createFragment(position: Int): Fragment {
            return fragments[position]
        }

    }

}