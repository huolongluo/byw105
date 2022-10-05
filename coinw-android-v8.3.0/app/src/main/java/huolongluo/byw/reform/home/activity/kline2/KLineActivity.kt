package huolongluo.byw.reform.home.activity.kline2

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.sharesdk.system.text.ShortMessage
import cn.sharesdk.tencent.qq.QQ
import cn.sharesdk.wechat.friends.Wechat
import cn.sharesdk.wechat.moments.WechatMoments
import com.android.coinw.api.kx.model.X24HData
import com.android.coinw.biz.event.BizEvent
import com.android.coinw.biz.event.BizEvent.Trade.UpdateSelf
import com.android.coinw.biz.trade.helper.TradeHelper
import com.android.coinw.utils.Utilities
import com.android.legend.model.enumerate.transfer.TransferAccount
import com.android.legend.socketio.SocketIOClient
import com.android.legend.ui.market.MarketBBSideFirstFragment
import com.android.legend.ui.market.MarketETFSideFirstFragment
import com.android.legend.util.TimerUtil
import com.blankj.utilcodes.utils.ToastUtils
import com.google.gson.reflect.TypeToken
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.onekeyshare.OnekeyShare
import com.legend.modular_contract_sdk.utils.StatusBarUtils2
import huolongluo.byw.R
import huolongluo.byw.byw.net.UrlConstants
import huolongluo.byw.byw.ui.fragment.maintab02.SideETFFragment
import huolongluo.byw.byw.ui.present.MarketDataPresent
import huolongluo.byw.io.AppConstants
import huolongluo.byw.log.Logger
import huolongluo.byw.reform.base.BaseActivity
import huolongluo.byw.reform.home.activity.kline2.adapter.KLineBottomAdapter
import huolongluo.byw.reform.home.activity.kline2.common.KLine2Util
import huolongluo.byw.reform.home.activity.kline2.common.KLineEntity
import huolongluo.byw.reform.home.activity.kline2.common.Kline2Constants
import huolongluo.byw.user.UserInfoManager
import huolongluo.byw.util.*
import huolongluo.byw.util.OkhttpManager.DataCallBack
import huolongluo.byw.util.noru.NorUtils
import huolongluo.byw.util.pricing.PricingMethodUtil
import huolongluo.byw.util.tip.SnackBarUtils
import huolongluo.bywx.utils.AppUtils
import huolongluo.bywx.utils.DoubleUtils
import kotlinx.android.synthetic.main.activity_kline.*
import okhttp3.Request
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.math.max

class KLineActivity : BaseActivity(), View.OnClickListener {
    private val viewModel:KLineViewModel by viewModels()

    private var isSelf = false
    private val context by lazy { this }
    private lateinit var entity: KLineEntity//外部传入k线的数据实体
    private val wikiFragment by lazy { KLineWikiFragment.newInstance(entity) }
    private val orderFragment by lazy { KLineOrderFragment.newInstance(entity) }
    private val turnoverFragment by lazy { KLineTurnoverFragment.newInstance(entity) }
    lateinit var sideFragment:Fragment

    private var dialog: Dialog? = null
    private var ivScreenShot: ImageView? = null
    private var ivScreenBottom: ImageView? = null
    private var bitmapScreenShot: Bitmap? = null //屏幕截图的bitmap
    private var bitmapResult: Bitmap? = null //拼接二维码的最终bitmap
    private lateinit var animation: Animation
    private var netValueCountDownTimer:CountDownTimer?=null

    companion object {
        private val TAG = KLineActivity::class.java.simpleName
        /**
         * k线的宽高比
         */
        private const val KLINE_HEIGHT_RATE = 1.2

        private const val REQUEST_FULLSCREEN = 1

        @JvmStatic
        fun launch(context: Context, entity: KLineEntity) {
            val intent = Intent(context, KLineActivity::class.java)
            intent.putExtra("entity", entity)
            context.startActivity(intent)
        }
    }

    private val chartFragments by lazy {
        MutableList(Kline2Constants.SELECT_TIME_ARRAY.size) {
            KLineFragment.newInstance(it, entity.kLineId, entity.tradeType, entity.id, entity)
        }
    }

    private val selectTimeWindowStrings = intArrayOf(R.string._1, R.string._5, R.string._30,
            R.string.str_6hour, R.string.str_1week, R.string._1month)

    private val chartTabs by lazy { listOf(vLineChart, v15Min, v1Hour, v4Hour, v1Day) }

    private val bottomTabs by lazy {
        if (isEtf()) {
            listOf(vBottomTab1, vBottomTab2)
        } else {
            listOf(vBottomTab1, vBottomTab2, vBottomTab3)
        }
    }

    private lateinit var settingWindow: KLineSettingWindow

    private val selectTimeWindow by lazy {
        KLineSelectTimeWindow(this, entity.tradeType) { position, text ->
            viewPager.setCurrentItem(position, false)
            chartTabs.forEach { it.isSelected = false }
            vMore.setTitle(text)
            vMore.isSelected = true
        }
    }

    private val chartAdapter = object : FragmentStateAdapter(this) {
        override fun getItemCount() = chartFragments.size
        override fun createFragment(position: Int) = chartFragments[position]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils2.setStatusBarDefault(this)
        setContentView(R.layout.activity_kline)
        EventBus.getDefault().register(this)
        entity = intent.getParcelableExtra("entity")!!
        Logger.getInstance().debug(TAG, "onCreate entity:${GsonUtil.obj2Json(entity, KLineEntity::class.java)}")
        initViews()
        initDatas()
        initObserver()
    }


    private fun initViews() {
        animation = AnimationUtils.loadAnimation(this, R.anim.anim_kline_screenshot)
        refreshCoinName()
        if (isEtf()) {
            tvEtfTag.visibility = View.VISIBLE
            rltEtfRate.visibility = View.VISIBLE
            vBottomTab3.visibility = View.INVISIBLE
        }
        mSwipeBackLayout.setEnableGesture(false)
        // 初始化K线指标弹窗
        settingWindow = KLineSettingWindow(this, { mainIndex ->
            chartFragments.forEach { it.showMainIndex(mainIndex) }
        }, { subIndex ->
            chartFragments.forEach { it.showSubIndex(subIndex) }
        }).apply { setOnDismissListener { ivChartIndex.setImageResource(R.mipmap.kline_setting) } }
        // 图表tab及viewPager
        viewPager.isUserInputEnabled = false
        (viewPager.getChildAt(0) as RecyclerView).setItemViewCacheSize(0)
        viewPager.adapter = chartAdapter
//        val lp = viewPager.layoutParams
//        lp.height = (ResHelper.getScreenWidth(this) * KLINE_HEIGHT_RATE).toInt()
//        viewPager.layoutParams = lp

        setLastKLineTimePosition()

        val tabsListener: (View) -> Unit = { v ->
            chartTabs.forEach { it.isSelected = false }
            v.isSelected = true
            vMore.setTitle(getString(R.string.more))
            vMore.isSelected = false
            val tabIndex = chartTabs.indexOf(v)
            AppUtils.setSelectTime(Kline2Constants.SELECT_TIME_ARRAY[tabIndex], entity.tradeType)
            viewPager.setCurrentItem(tabIndex, false)
        }
        if (entity.tradeType == Kline2Constants.TRADE_TYPE_COIN) {
            ivFavor.visibility = View.VISIBLE
        } else {
            ivFavor.visibility = View.GONE
        }
        chartTabs.forEach { it.setOnClickListener(tabsListener) }
        // 底部tab及viewPager
        var l: List<Fragment>
        if (isEtf()) {
            l = listOf(orderFragment, turnoverFragment)
        } else {
            l = listOf(orderFragment, turnoverFragment, wikiFragment)
        }
        viewPagerBottom.adapter = KLineBottomAdapter(supportFragmentManager, l, bottomTabs)
        viewPagerBottom.offscreenPageLimit = bottomTabs.size
        viewPagerBottom.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                bottomTabs.forEach { it.isSelected = false }
                bottomTabs[position].isSelected = true
            }

        })
        vBottomTab1.isSelected = true
        val bottomTabsListener: (View) -> Unit = { v ->
            bottomTabs.forEach { it.isSelected = false }
            v.isSelected = true
            viewPagerBottom.setCurrentItem(bottomTabs.indexOf(v), false)
        }
        bottomTabs.forEach { it.setOnClickListener(bottomTabsListener) }
        // 抽屉
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        drawerLayout.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                if (sideFragment is MarketBBSideFirstFragment) {
                    (sideFragment as MarketBBSideFirstFragment).open()
                }
                if (sideFragment is MarketETFSideFirstFragment) {
                    (sideFragment as MarketETFSideFirstFragment).open()
                }
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                if (sideFragment is MarketBBSideFirstFragment) {
                    (sideFragment as MarketBBSideFirstFragment).close()
                }
                if (sideFragment is MarketETFSideFirstFragment) {
                    (sideFragment as MarketETFSideFirstFragment).close()
                }
            }
        })
        sideFragment = when (entity.tradeType) {
            Kline2Constants.TRADE_TYPE_ETF -> MarketETFSideFirstFragment()
            else -> MarketBBSideFirstFragment.newInstance()
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.rootDrawer, sideFragment)
                .commitAllowingStateLoss()
        ivFavor.setOnClickListener(this)
        flDraw.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        ivBack.setOnClickListener { finish() }
        val animationDrawable = ivDraw.drawable as AnimationDrawable
        animationDrawable.start()

        // 指标弹窗
        ivChartIndex.setOnClickListener {
            ivChartIndex.setImageResource(R.mipmap.kline_setting_selected)
            settingWindow.showAsDropDown(it)
        }
        // 选择时间弹窗
        vMore.setOnClickListener { selectTimeWindow.showAsDropDown(it) }
        // 全屏
        ivFullScreen.setOnClickListener { KLineFullScreenActivity.launch(this, entity, REQUEST_FULLSCREEN) }
        tvBuy.setOnClickListener {
            //此处因为k线只能从交易页进入，暂时简单处理
            EventBus.getDefault().post(BizEvent.Trade.KLineClickEvent(true))
            finish()
        }
        tvSell.setOnClickListener {
            EventBus.getDefault().post(BizEvent.Trade.KLineClickEvent(false))
            finish()
        }
        ivShare.setOnClickListener {
            showPop()

        }
        ivFullScreen.setOnClickListener { KLineFullScreenActivity.launch(this, entity, REQUEST_FULLSCREEN) }

    }

    private fun initDatas() {
        initSelf()
        getNetValue()
    }
    private fun initObserver(){
        viewModel.x24HData.observe(this, androidx.lifecycle.Observer {
            Logger.getInstance().debug(TAG, "viewModel.x24HData:${it}")
            if (it.isSuccess) {
                if (it.data != null) {
                    refreshHeadData(it.data)
                }
            }
        })
        viewModel.netValueData.observe(this, androidx.lifecycle.Observer {
            Logger.getInstance().debug(TAG, "viewModel.netValueData:${it}")
            if (it.isSuccess) {
                if (it.data?.data != null) {
                    tvEtfRate.text = it.data.data.etfFundsRate
                    tvEtfNetWorth.text = it.data.data.price
                } else {
                    tvEtfRate.text = "--"
                    tvEtfNetWorth.text = "--"
                }
            } else {
                tvEtfRate.text = "--"
                tvEtfNetWorth.text = "--"
            }
        })
    }

    override fun onResume() {
        super.onResume()
        subscribeSocket()
        getNetValue()
    }

    override fun onPause() {
        super.onPause()
        unSubscribeSocket()
        netValueCountDownTimer?.cancel()
    }

    private fun refreshHeadData(data: X24HData) {
        Utilities.getMainHandler().post(Runnable {
            refreshHead(data.last, data.high, data.low, data.volValue, data.changeRate)
        })
    }
    fun refreshHead(last: String, high: String, low: String, volValue: String, changeRate: String){
        tvPrice.text = last
        tvExchangePrice.text="≈${PricingMethodUtil.getPricingUnit()}${PricingMethodUtil.getResultByExchangeRate(last, entity.cnyName)}"
        tvHighValue.text = high
        tvLowValue.text = low
        tvVolumeValue.text = PricingMethodUtil.getLargePrice(volValue, 3)
        ivChange.visibility = View.VISIBLE
        var ratio=MathHelper.mul(100.0, changeRate.toDouble())
        if (DoubleUtils.parseDouble(changeRate) > 0) {
            tvRate.text = "+"+NorUtils.NumberFormat(2).format(ratio) + "%"
            ivChange.visibility=View.VISIBLE
            ivChange.setImageResource(R.mipmap.zhang)
            tvPrice.setTextColor(ThemeUtil.getUpColor(context))
            tvRate.setTextColor(ThemeUtil.getUpColor(context))
        } else if(DoubleUtils.parseDouble(changeRate)==0.0){
            tvRate.text = "+"+NorUtils.NumberFormat(2).format(ratio) + "%"
            ivChange.visibility=View.GONE
            tvPrice.setTextColor(resources.getColor(R.color.color_a5a2be))
            tvRate.setTextColor(resources.getColor(R.color.color_a5a2be))
        }
        else {
            tvRate.text = NorUtils.NumberFormat(2).format(ratio) + "%"
            ivChange.visibility=View.VISIBLE
            ivChange.setImageResource(R.mipmap.die)
            tvPrice.setTextColor(ThemeUtil.getDropColor(context))
            tvRate.setTextColor(ThemeUtil.getDropColor(context))
        }
    }
    private fun subscribeSocket() {
        if (entity == null) {
            return
        }
        subscribeCoin()
    }

    private fun subscribeCoin() {
        if (TextUtils.isEmpty(tvPrice.text.toString())) {
            get24HData()
        }

        val pair = entity.coinName + "-" + entity.cnyName
        SocketIOClient.subscribe(TAG, "${AppConstants.SOCKET.SPOT_24H_DATA}$pair", TypeToken.get(X24HData::class.java)) {
            Logger.getInstance().debug(TAG, "socket返回 24h数据:$it")
            refreshHeadData(it)
        }
    }

    private fun unSubscribeSocket() {
        if (entity == null) {
            return
        }
        unSubscribeCoin()
    }

    private fun unSubscribeCoin() {
        val pair = entity.coinName+ "-" + entity.cnyName
        SocketIOClient.unsubscribe(TAG, "${AppConstants.SOCKET.SPOT_24H_DATA}$pair")
    }

    private fun get24HData() {
        viewModel.get24HData("${entity.id}")
    }

    private fun refreshCoinName() {
        val name = entity.coinName + "/" + entity.cnyName
        tvName.text = StringUtil.changePartTextSize(name, entity.coinName.length, name.length, 12)
    }

    /**
     * 从sp获取上一次选中图表时间类型的位置
     */
    private fun setLastKLineTimePosition() {
        chartTabs.forEach { it.isSelected = false }
        vMore.isSelected = false

        val selectIndex = max(0, Kline2Constants.SELECT_TIME_ARRAY.indexOf(AppUtils.getSelectTime(entity.tradeType)))
        if (selectIndex < chartTabs.size) {
            chartTabs[selectIndex].isSelected = true
        } else {
            vMore.isSelected = true
            vMore.setTitle(getString(selectTimeWindowStrings[selectIndex - chartTabs.size]))
        }
        viewPager.setCurrentItem(selectIndex, false)
    }

    private fun refreshKLineViewPager() {
        val currentItemIndex = viewPager.currentItem
        val newList = List(Kline2Constants.SELECT_TIME_ARRAY.size) {
            KLineFragment.newInstance(it, entity.kLineId, entity.tradeType, entity.id, entity) }
        chartFragments.clear()
        chartFragments += newList
        viewPager.adapter = chartAdapter
        viewPager.setCurrentItem(currentItemIndex, false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCoinChangeEvent(event: BizEvent.Trade.CoinEvent) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        if (event.coinName == entity.coinName && event.cnyName == entity.cnyName) {
            return
        }
        tvPrice.text = ""//为了让24h接口调用
        unSubscribeSocket()
        entity = KLineEntity(event.id.toInt(), event.coinName, event.cnyName, entity.tradeType)
        subscribeSocket()
        refreshCoinName()
        initSelf()
        refreshKLineViewPager()
        wikiFragment?.refreshData(entity)
        orderFragment?.refreshData(entity)
        turnoverFragment?.refreshData(entity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FULLSCREEN && resultCode == Activity.RESULT_OK) {
            // 重新设置时间选择框的位置和指标
            setLastKLineTimePosition()
            settingWindow = KLineSettingWindow(this, { mainIndex ->
                chartFragments.forEach { it.showMainIndex(mainIndex) }
            }, { subIndex ->
                chartFragments.forEach { it.showSubIndex(subIndex) }
            }).apply { setOnDismissListener { ivChartIndex.setImageResource(R.mipmap.kline_setting) } }
            chartFragments.forEach { it.showMainIndex(SPUtils.getKLineMainIndex(this)) }
            chartFragments.forEach { it.showSubIndex(SPUtils.getKLineSubIndex(this)) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        netValueCountDownTimer?.cancel()
    }

    private fun initSelf() {
        //杠杆和币币才有自选
        if (entity.tradeType == Kline2Constants.TRADE_TYPE_COIN) {
            if (UserInfoManager.isLogin()) {//登录调用接口获取自选信息
                checkIsSelf()
            } else {//未登录使用sp
                isSelf = SPUtils.getBoolean(this, KLine2Util.getSelfSpKey(entity.coinName,
                        entity.cnyName, entity.tradeType), false)
                refreshSelf(false)
            }
        }
    }
    //etf需要显示费率和净值
    private fun getNetValue(){
        if(!isEtf()) return
        netValueCountDownTimer?.cancel()
        netValueCountDownTimer=TimerUtil.createCountDownTimer(10000000000, AppConstants.TIMER.NET_VALUE, { millisUntilFinished ->
            viewModel.getNetValue("${entity.id}")
        })
        netValueCountDownTimer?.start()
    }
    private fun isEtf():Boolean{
        return entity.tradeType == Kline2Constants.TRADE_TYPE_ETF
    }

    private fun checkIsSelf() { //行情已经调用获取了自选列表
        if (MarketDataPresent.listSelf == null) {
            return
        }
        isSelf = MarketDataPresent.listSelf.contains(entity.id)
        refreshSelf(false)
        EventBus.getDefault().post(UpdateSelf()) //更新侧滑栏自选
    }

    private fun refreshSelf(isShowToast: Boolean) {
        if (isSelf) {
            ivFavor.setImageResource(R.mipmap.ic_star_pressed)
            if (isShowToast) SnackBarUtils.ShowBlue(this, getString(R.string.as74))
        } else {
            ivFavor.setImageResource(R.mipmap.ic_star_normal)
            if (isShowToast) SnackBarUtils.ShowBlue(this, getString(R.string.as75))
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ivFavor -> {
                if (UserInfoManager.isLogin()) {
                    if (isSelf) {
                        delete()
                    } else {
                        saveSelf()
                    }
                } else {
                    isSelf = !isSelf
                    SPUtils.saveBoolean(this, KLine2Util.getSelfSpKey(entity.coinName, entity.cnyName,
                            entity.tradeType), isSelf)
                    EventBus.getDefault().post(BizEvent.Trade.UpdateSelfTrade())//更新交易页自选
                    EventBus.getDefault().post(BizEvent.Market.RefreshSelfLocalList())
                    refreshSelf(true)
                }
            }
            R.id.more_bn -> {
                dialog?.dismiss()
                systemShare()
            }
            R.id.wechat_bn -> {
                dialog?.dismiss()
                showShare(Wechat.NAME)
            }
            R.id.friend_bn -> {
                dialog?.dismiss()
                showShare(WechatMoments.NAME)
            }
            R.id.qq_bn -> {
                dialog?.dismiss()
                showShare(QQ.NAME)
            }
            R.id.message_bn -> {
                dialog?.dismiss()
                showShare(ShortMessage.NAME)
            }
            R.id.savePng_bn -> {
                dialog?.dismiss()
                if (Util.saveImageToGallery(this, bitmapResult)) {
                    ToastUtils.showShortToast(getString(R.string.d5))
                } else {
                    ToastUtils.showShortToast(getString(R.string.d6))
                }
            }

        }
    }

    private fun showShare(platform: String?) {
        val oks = OnekeyShare()
        //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
        if (platform != null) {
            oks.setPlatform(platform)
        }
        if (TextUtils.equals(platform, Wechat.NAME) || TextUtils.equals(platform, WechatMoments.NAME)) {
            oks.setImageData(bitmapResult)
        } else {
            val path = Util.saveBitmap(this, bitmapResult)
            oks.setImagePath(path)
        }
        //启动分享
        oks.show(this)
    }

    private fun systemShare() {
        try {
            if (bitmapResult == null) {
                return
            }
            val bitmap: Bitmap? = bitmapResult
            val uri = Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, bitmap, null, null))
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(Intent.createChooser(intent, "Coinw.me"))
        } catch (t:Throwable) {
            t.printStackTrace()
        }
    }

    private fun createViewBitmap(v: View?): Bitmap? {
        Logger.getInstance().debug(TAG, "createViewBitmap v.width:" + v?.width)
        if (v == null || v.width == 0) {
            return null
        }
        val bitmap = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        v!!.draw(canvas)
        return bitmap
    }

    private fun startAnimationScreenShot() {
        bitmapScreenShot = Util.getScreenShotBitmap(this)
        bitmapResult = Util.addBitmap(bitmapScreenShot, createViewBitmap(ivScreenBottom))

        ivScreenShot?.setImageBitmap(bitmapResult)
        ivScreenShot?.startAnimation(animation)
    }

    fun showPop() {
        dialog = Dialog(this, R.style.DialogTheme)
        val contentView = LayoutInflater.from(this).inflate(R.layout.pop_kline_share, null, false)
        ivScreenShot = contentView.findViewById<ImageView>(R.id.ivScreenShot)
        ivScreenBottom = contentView.findViewById(R.id.ivScreenBottom)
        val wechat_bn = contentView.findViewById<LinearLayout>(R.id.wechat_bn)
        val friend_bn = contentView.findViewById<LinearLayout>(R.id.friend_bn)
        val qq_bn = contentView.findViewById<LinearLayout>(R.id.qq_bn)
        val message_bn = contentView.findViewById<LinearLayout>(R.id.message_bn)
        val savePng_bn = contentView.findViewById<LinearLayout>(R.id.savePng_bn)
        val more_bn = contentView.findViewById<LinearLayout>(R.id.more_bn)
        val tvCancel = contentView.findViewById<TextView>(R.id.tvCancel)

        tvCancel.setOnClickListener(View.OnClickListener { dialog?.dismiss() })
        wechat_bn.setOnClickListener(this)
        friend_bn.setOnClickListener(this)
        qq_bn.setOnClickListener(this)
        message_bn.setOnClickListener(this)
        savePng_bn.setOnClickListener(this)
        more_bn.setOnClickListener(this)
        dialog!!.setContentView(contentView)
        dialog!!.setCanceledOnTouchOutside(true)
        dialog!!.setCancelable(true)
        val window = dialog!!.window
        window?.setGravity(Gravity.BOTTOM)
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog!!.show()

        ivScreenBottom?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                ivScreenBottom?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                startAnimationScreenShot()

            }
        })

    }

    private fun saveSelf() {
        if (!TradeHelper.checkLogin(this)) {
            return
        }
        var params: MutableMap<String?, String?> = HashMap()
        params["trademId"] = "${entity.id}"
        params["tradeType"]= TransferAccount.SPOT.value
        params["type"]="1"
        params = OkhttpManager.encrypt(params)
        params["loginToken"] = UserInfoManager.getToken()
        OkhttpManager.postAsync(UrlConstants.UPDATE_SELF, params, object : DataCallBack {
            override fun requestFailure(request: Request, e: java.lang.Exception, errorMsg: String) {
                SnackBarUtils.ShowRed(context, getString(R.string.qe52))
            }

            override fun requestSuccess(result: String) {
                try {
                    val jsonObject = JSONObject(result)
                    val code = jsonObject.getString("code")
                    val message = jsonObject.getString("message")
                    if (code == "200") {
                        MarketDataPresent.getSelf().setSelect(entity.id, 1) //老逻辑保留
                        isSelf = !isSelf
                        SPUtils.saveBoolean(context, KLine2Util.getSelfSpKey(entity.coinName, entity.cnyName,
                                entity.tradeType), isSelf)
                        refreshSelf(true)
                        EventBus.getDefault().post(BizEvent.Trade.UpdateSelfTrade())//更新交易页
                        EventBus.getDefault().post(BizEvent.Trade.UpdateSelf()) //更新侧滑栏和行情的自选
                        //更新自选列表
                        MarketDataPresent.getSelfHttpList()
                    } else {
                        SnackBarUtils.ShowRed(context, " $message")
                    }
                } catch (e: JSONException) {
                    SnackBarUtils.ShowRed(context, getString(R.string.qe54))
                    e.printStackTrace()
                }
            }
        })
    }

    //删除自选
    private fun delete() {
        if (!TradeHelper.checkLogin(context)) {
            return
        }
        var params: MutableMap<String?, String?> = HashMap()
        params["trademId"] = "${entity.id}"
        params["tradeType"]= TransferAccount.SPOT.value
        params["type"]="1"
        params = OkhttpManager.encrypt(params)
        params["loginToken"] = UserInfoManager.getToken()
        OkhttpManager.postAsync(UrlConstants.UPDATE_SELF, params, object : DataCallBack {
            override fun requestFailure(request: Request, e: java.lang.Exception, errorMsg: String) {
                SnackBarUtils.ShowRed(context, getString(R.string.qe55))
            }

            override fun requestSuccess(result: String) {
                try {
                    val jsonObject = JSONObject(result)
                    val code = jsonObject.getString("code")
                    val message = jsonObject.getString("message")
                    if (code == "200") {
                        MarketDataPresent.getSelf().setSelect(entity.id, 1) //老逻辑保留
                        isSelf = !isSelf
                        SPUtils.saveBoolean(context, KLine2Util.getSelfSpKey(entity.coinName, entity.cnyName,
                                entity.tradeType), isSelf)
                        refreshSelf(true)
                        //删除操作因为更新自选列表是异步操作，需要此处先删除
                        if (MarketDataPresent.listSelf.contains(entity.id)) {
                            MarketDataPresent.listSelf.remove(entity.id)
                        }
                        EventBus.getDefault().post(BizEvent.Trade.UpdateSelfTrade())//更新交易页
                        EventBus.getDefault().post(BizEvent.Trade.UpdateSelf()) //更新侧滑栏和行情的自选
                        //更新自选列表
                        MarketDataPresent.getSelfHttpList()
                    } else {
                        SnackBarUtils.ShowRed(context, " $message")
                    }
                } catch (e: JSONException) {
                    SnackBarUtils.ShowBlue(context, getString(R.string.qe57))
                    e.printStackTrace()
                }
            }
        })
    }
}