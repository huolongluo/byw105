package com.legend.modular_contract_sdk.ui.chart

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.AnimationDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import cn.sharesdk.system.text.ShortMessage
import cn.sharesdk.tencent.qq.QQ
import cn.sharesdk.wechat.friends.Wechat
import cn.sharesdk.wechat.moments.WechatMoments
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.base.BaseActivity
import com.legend.modular_contract_sdk.coinw.CoinwHyUtils
import com.legend.modular_contract_sdk.common.UserConfigStorage
import com.legend.modular_contract_sdk.common.event.ChangeProductEvent
import com.legend.modular_contract_sdk.common.showSelectProductDialog
import com.legend.modular_contract_sdk.component.market_listener.MarketListenerManager
import com.legend.modular_contract_sdk.component.market_listener.MarketSubscribeType
import com.legend.modular_contract_sdk.component.market_listener.Price
import com.legend.modular_contract_sdk.component.market_listener.Ticker
import com.legend.modular_contract_sdk.onekeyshare.OnekeyShare
import com.legend.modular_contract_sdk.repository.model.Product
import com.legend.modular_contract_sdk.ui.chart.depth.DepthFragment
import com.legend.modular_contract_sdk.ui.chart.last_deal.LastDealFragment
import com.legend.modular_contract_sdk.utils.*
import kotlinx.android.synthetic.main.mc_sdk_activity_kline.*
import org.greenrobot.eventbus.EventBus
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

/**
 * 合约k线页面
 */
class McKLineActivity : BaseActivity<McKlineViewModel>(), View.OnClickListener {

    private var dialog: Dialog? = null
    private var ivScreenShot: ImageView? = null
    private var ivScreenBottom: ImageView? = null
    private var bitmapScreenShot: Bitmap? = null //屏幕截图的bitmap
    private var bitmapResult: Bitmap? = null //拼接二维码的最终bitmap
    private lateinit var animation: Animation
    private lateinit var productList: List<Product>
    private lateinit var product: Product//当前币对
    private var mPageAdapter: FragmentPagerAdapter? = null

    companion object {
        private val TAG = McKLineActivity::class.java.simpleName

        private const val REQUEST_FULLSCREEN = 1

        @JvmStatic
        fun launch(context: Context, productList: ArrayList<Product>,product: Product) {
            val intent = Intent(context, McKLineActivity::class.java)
            intent.putExtra("productList", productList)
            intent.putExtra("product", product)
            context.startActivity(intent)
        }
    }

    private val chartFragments by lazy {
        MutableList(McConstants.KLINE.SELECT_TIME_ARRAY.size) {
            McKLineFragment.newInstance(it, McConstants.KLINE.SELECT_TIME_ARRAY[it], product)
        }
    }

    private val selectTimeWindowStrings = intArrayOf(R.string.mc_sdk_1m, R.string.mc_sdk_5m, R.string.mc_sdk_1week)

    private val chartTabs by lazy { listOf(vLineChart, v15Min, v1Hour, v4Hour, v1Day) }

    private lateinit var settingWindow: McKLineSettingWindow

    private val selectTimeWindow by lazy {
        McKLineSelectTimeWindow(this) { position, text ->
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

    override fun createViewModel()= ViewModelProvider(this).get(McKlineViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtils2.setStatusBarDefault(this)
        setContentView(R.layout.mc_sdk_activity_kline)
        productList=intent.getSerializableExtra("productList") as ArrayList<Product>
        product =intent.getSerializableExtra("product") as Product
        initViews()
        initDatas()
        initObserver()
    }


    private fun initViews() {
        animation = AnimationUtils.loadAnimation(this, R.anim.mc_sdk_anim_kline_screenshot)
        refreshCoinName()
        // 初始化K线指标弹窗
        settingWindow = McKLineSettingWindow(this, { mainIndex ->
            chartFragments.forEach { it.showMainIndex(mainIndex) }
        }, { subIndex ->
            chartFragments.forEach { it.showSubIndex(subIndex) }
        }).apply { setOnDismissListener { ivChartIndex.setImageResource(R.drawable.mc_sdk_kline_setting) } }
        // 图表tab及viewPager
        viewPager.isUserInputEnabled = false
        (viewPager.getChildAt(0) as RecyclerView).setItemViewCacheSize(0)
        viewPager.adapter = chartAdapter

        setLastKLineTimePosition()

        val tabsListener: (View) -> Unit = { v ->
            chartTabs.forEach { it.isSelected = false }
            v.isSelected = true
            vMore.setTitle(getString(R.string.mc_sdk_more))
            vMore.isSelected = false
            val tabIndex = chartTabs.indexOf(v)
            UserConfigStorage.setSelectTime(McConstants.KLINE.SELECT_TIME_ARRAY[tabIndex])
            viewPager.setCurrentItem(tabIndex, false)
        }
        chartTabs.forEach { it.setOnClickListener(tabsListener) }
        flDraw.setOnClickListener {
            showSelectProductDialog(this, productList) {
                if(TextUtils.equals(it.mBase,product.mBase)){
                    return@showSelectProductDialog
                }
                changePair(it)
            }
        }
        ivBack.setOnClickListener { finish() }
        val animationDrawable = ivDraw.drawable as AnimationDrawable
        animationDrawable.start()

        // 指标弹窗
        ivChartIndex.setOnClickListener {
            ivChartIndex.setImageResource(R.drawable.mc_sdk_kline_setting_selected)
            settingWindow.showAsDropDown(it)
        }
        // 选择时间弹窗
        vMore.setOnClickListener { selectTimeWindow.showAsDropDown(it) }
        // 全屏
        ivFullScreen.setOnClickListener { McKLineFullScreenActivity.launch(this, product, REQUEST_FULLSCREEN) }
        tvBuy.setOnClickListener {
            finish()
        }
        tvSell.setOnClickListener {
            finish()
        }
        ivShare.setOnClickListener {
            showPop()
        }

    }

    private fun initDatas() {
        val fragment1 = DepthFragment.getInstance(product)
        val fragment2 = LastDealFragment.getInstance(product)
        supportFragmentManager?.let {
            mPageAdapter = object : FragmentPagerAdapter(
                    it,
                    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            ) {
                override fun getItem(position: Int): Fragment {
                    return when (position) {
                        0 -> {
                            fragment1
                        }
                        1 -> {
                            fragment2
                        }
                        else -> {
                            fragment1
                        }
                    }

                }

                override fun getCount(): Int = 2

                override fun getPageTitle(position: Int): CharSequence? {
                    return when (position) {
                        0 -> {
                            getString(R.string.mc_sdk_depth)
                        }
                        1 -> {
                            getString(R.string.mc_sdk_last_deal)
                        }
                        else -> {
                            ""
                        }
                    }
                }
            }
            tab.setupWithViewPager(vp)
            vp.adapter = mPageAdapter
        }


    }
    private fun initObserver(){
        getViewModel().mTickerLiveData.observe(this, androidx.lifecycle.Observer{
            val ticker = it as Ticker
            val pricePrecision =if (product == null) 4 else product.mPricePrecision
            tvPrice.text = ticker.last.getNum(pricePrecision, true)
            tvHighValue.text = ticker.high.getNum(pricePrecision, true)
            tvLowValue.text = ticker.low.getNum(pricePrecision, true)
            var ratio=MathUtils.mul(100.0, ticker.changeRate.toDoubleOrNull()?:0.0)
            when {
                ticker.changeRate.toDoubleOrNull()?:0.0 > 0 -> {
                    tvRate.text = "+"+ratio.toString().getNum(2) + "%"
                    ivChange.visibility=View.VISIBLE
                    ivChange.setImageResource(R.drawable.mc_sdk_zhang)
                    tvPrice.setTextColor(ThemeUtil.getUpColor(this))
                    tvRate.setTextColor(ThemeUtil.getUpColor(this))
                }
                ticker.changeRate.toDoubleOrNull()?:0.0==0.0 -> {
                    tvRate.text = "+"+ratio.toString().getNum(2) + "%"
                    ivChange.visibility=View.GONE
                    tvPrice.setTextColor(resources.getColor(R.color.mc_sdk_a5a2be))
                    tvRate.setTextColor(resources.getColor(R.color.mc_sdk_a5a2be))
                }
                else -> {
                    tvRate.text = ratio.toString().getNum(2) + "%"
                    ivChange.visibility=View.VISIBLE
                    ivChange.setImageResource(R.drawable.mc_sdk_die)
                    tvPrice.setTextColor(ThemeUtil.getDropColor(this))
                    tvRate.setTextColor(ThemeUtil.getDropColor(this))
                }
            }

            ivChange.visibility = View.GONE
        })

        getViewModel().mPriceLiveData.observe(this, Observer {
            val markPrice = it as Price
            val pricePrecision =if (product == null) 4 else product.mPricePrecision
            tvMarkPriceValue.text = markPrice.p.getNum(pricePrecision)
        })
    }

    override fun onResume() {
        super.onResume()
        McConfigurationUtils.resetLanguage(this)
        subscribeSocket()
    }

    override fun onPause() {
        super.onPause()
        unSubscribeSocket()
    }
    private fun changePair(product: Product){
        tvPrice.text = ""//为了让24h接口调用
        unSubscribeSocket()
        this.product=product
        subscribeSocket()
        refreshCoinName()
        refreshKLineViewPager()
        mPageAdapter?.apply {
            (getItem(0) as DepthFragment).changeProduct(product)
            (getItem(1) as LastDealFragment).changeProduct(product)
        }
        EventBus.getDefault().post(ChangeProductEvent(product))
    }
    private fun subscribeSocket() {
        if (product == null) {
            return
        }
        unSubscribeSocket()

            mMarketListenerList.add(
                    MarketListenerManager.subscribe(
                            MarketSubscribeType.TickerSwap(product.mBase, "usd"),
                            getViewModel().mTickerLiveData
                    )
            )

            mMarketListenerList.add(
                    MarketListenerManager.subscribe(
                            MarketSubscribeType.MarkPrice(product.mBase, "usd"),
                            getViewModel().mPriceLiveData
                    )
            )
    }

    private fun unSubscribeSocket() {
        CoinwHyUtils.removeAllMarketListener(mMarketListenerList)
    }

    private fun refreshCoinName() {
        val name =  product.getProductName()
        tvName.text =name.changePartTextSize(this,product.mBase.length,name.length,12.0f)
    }

    /**
     * 从sp获取上一次选中图表时间类型的位置
     */
    private fun setLastKLineTimePosition() {
        chartTabs.forEach { it.isSelected = false }
        vMore.isSelected = false

        val selectIndex = max(0, McConstants.KLINE.SELECT_TIME_ARRAY.indexOfFirst{
            it.id==UserConfigStorage.getSelectTime().id
        })
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
        val newList = List(McConstants.KLINE.SELECT_TIME_ARRAY.size) {
            McKLineFragment.newInstance(it, McConstants.KLINE.SELECT_TIME_ARRAY[it], product)
        }
        chartFragments.clear()
        chartFragments += newList
        viewPager.adapter = chartAdapter
        viewPager.setCurrentItem(currentItemIndex, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FULLSCREEN && resultCode == Activity.RESULT_OK) {
            // 重新设置时间选择框的位置和指标
            setLastKLineTimePosition()
            settingWindow = McKLineSettingWindow(this, { mainIndex ->
                chartFragments.forEach { it.showMainIndex(mainIndex) }
            }, { subIndex ->
                chartFragments.forEach { it.showSubIndex(subIndex) }
            }).apply { setOnDismissListener { ivChartIndex.setImageResource(R.drawable.mc_sdk_kline_setting) } }
            chartFragments.forEach { it.showMainIndex(UserConfigStorage.getKLineMainIndex()) }
            chartFragments.forEach { it.showSubIndex(UserConfigStorage.getKLineSubIndex()) }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
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
                if(bitmapResult==null) return
                if (McImageUtils.saveImageToGallery(this, bitmapResult!!)) {
                    ToastUtils.showShortToast(getString(R.string.mc_sdk_save_success))
                } else {
                    ToastUtils.showShortToast(getString(R.string.mc_sdk_save_failed))
                }
            }

        }
    }

    private fun showShare(platform: String?) {
        if(bitmapResult==null) return
        val oks = OnekeyShare()
        //指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
        if (platform != null) {
            oks.setPlatform(platform)
        }
        if (TextUtils.equals(platform, Wechat.NAME) || TextUtils.equals(platform, WechatMoments.NAME)) {
            oks.setImageData(bitmapResult)
        } else {
            val path = McImageUtils.saveBitmap(this, bitmapResult!!)
            oks.setImagePath(path)
        }
        //启动分享
        oks.show(this)
    }

    private fun systemShare() {
        if (bitmapResult == null) {
            return
        }
        val bitmap: Bitmap? = bitmapResult
        val uri = Uri.parse(MediaStore.Images.Media.insertImage(contentResolver, bitmap, null, null))
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        startActivity(Intent.createChooser(intent, "Coinw.me"))
    }

    private fun createViewBitmap(v: View): Bitmap? {
        if (v == null || v.width == 0) {
            return null
        }
        val bitmap = Bitmap.createBitmap(v.width, v.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        v!!.draw(canvas)
        return bitmap
    }

    private fun startAnimationScreenShot() {
        if(ivScreenBottom==null) return
        bitmapScreenShot = McImageUtils.getScreenShotBitmap(this)
        val bottomBitmap= createViewBitmap(ivScreenBottom!!) ?: return
        bitmapResult = McImageUtils.addBitmap(bitmapScreenShot!!, bottomBitmap)

        ivScreenShot?.setImageBitmap(bitmapResult)
        ivScreenShot?.startAnimation(animation)

    }

    fun showPop() {
        dialog = Dialog(this, R.style.mc_sdk_Theme_DialogTheme)
        val contentView = LayoutInflater.from(this).inflate(R.layout.mc_sdk_pop_kline_share, null, false)
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
        window.setGravity(Gravity.BOTTOM)
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog!!.show()

        ivScreenBottom?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                ivScreenBottom?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                startAnimationScreenShot()

            }
        })

    }
}