package com.legend.modular_contract_sdk.widget.dialog

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.SeekBar
import androidx.core.view.ViewCompat
import androidx.databinding.ObservableField
import com.google.android.material.tabs.TabLayout
import com.legend.common.util.ThemeUtil
import com.legend.modular_contract_sdk.R
import com.legend.modular_contract_sdk.databinding.McSdkDialogModifyLeverageBinding
import com.legend.modular_contract_sdk.ui.contract.PositionMergeMode
import com.legend.modular_contract_sdk.ui.contract.PositionMode
import com.legend.modular_contract_sdk.ui.contract.QuantityUnit
import com.legend.modular_contract_sdk.utils.*
import com.legend.modular_contract_sdk.widget.gone
import com.legend.modular_contract_sdk.widget.visible
import com.lxj.xpopup.core.BottomPopupView
import com.orhanobut.logger.Logger

class ModifyLeverageDialog(
        context: Context,
        private val currentPositionMode: PositionMode,
        private val currentPositionMergeMode: PositionMergeMode,
        private val currentLeverage: Int,
        private val maxLeverage: Int,
        private val calcCurrentLeverageMaxOpen: (Int) -> String,
        val onConfirm: (PositionMode, PositionMergeMode, Int) -> Unit
) :
    BottomPopupView(context) {

    lateinit var mBinding: McSdkDialogModifyLeverageBinding

    //0.逐仓 1.全仓
    var mPositionMode: PositionMode = PositionMode.PART
    var mPositionMergeMode: PositionMergeMode = PositionMergeMode.MERGE
    var mLeverage = ObservableField<String>("")


    override fun onCreate() {
        super.onCreate()

        mPositionMode = currentPositionMode
        mPositionMergeMode = currentPositionMergeMode

        mLeverage.set(currentLeverage.toString())

        mBinding = McSdkDialogModifyLeverageBinding.bind(popupImplView)
        mBinding.leverage = mLeverage

        mBinding.btnConfirm.setOnClickListener {

            if (mLeverage.get().isNullOrEmpty()) {
                return@setOnClickListener
            }

            onConfirm(mPositionMode, mPositionMergeMode, (mLeverage.get() ?: "$currentLeverage").toInt())
            dismiss()
        }

        mBinding.btnCancel.setOnClickListener {
            dismiss()
        }

        mBinding.tvLeverage.setText(mLeverage.toString())

        mBinding.tvLeverageWarn.visibility = if (currentLeverage >= 20) View.VISIBLE else View.GONE

        mBinding.seekbar.post {
            initSeekBar()
        }

        mBinding.rgPositionType.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rb_position_mode_full -> {
                    mPositionMode = PositionMode.FULL
                    mBinding.rbPositionModeFull.typeface = Typeface.DEFAULT_BOLD
                    mBinding.rbPositionModePart.typeface = Typeface.DEFAULT
                }
                R.id.rb_position_mode_part -> {
                    mPositionMode = PositionMode.PART
                    mBinding.rbPositionModeFull.typeface = Typeface.DEFAULT
                    mBinding.rbPositionModePart.typeface = Typeface.DEFAULT_BOLD
                }
            }
        }

        mBinding.rgPositionMergeType.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rb_position_merge_mode_merge -> {
                    mPositionMergeMode = PositionMergeMode.MERGE
                    mBinding.rbPositionMergeModeMerge.typeface = Typeface.DEFAULT_BOLD
                    mBinding.rbPositionMergeModePartition.typeface = Typeface.DEFAULT
                }
                R.id.rb_position_merge_mode_partition -> {
                    mPositionMergeMode = PositionMergeMode.PARTITION
                    mBinding.rbPositionMergeModeMerge.typeface = Typeface.DEFAULT
                    mBinding.rbPositionMergeModePartition.typeface = Typeface.DEFAULT_BOLD
                }
            }
        }


        mBinding.ivPlus.setOnClickListener {
            val current = mLeverage.get()
            mLeverage.set((current.getDouble() + 1).toInt().toString())
            mBinding.tvLeverage.postDelayed({
                setSelection()
            }, 100)
        }

        mBinding.ivMinus.setOnClickListener {
            val current = mLeverage.get().getDouble()
            if (current <= 1){
                return@setOnClickListener
            }
            mLeverage.set((current - 1).toInt().toString())
            mBinding.tvLeverage.postDelayed({
                setSelection()
            },100)
        }

        when (mPositionMode) {
            PositionMode.PART -> {
                mBinding.rbPositionModePart.isChecked = true
                mBinding.rbPositionModeFull.typeface = Typeface.DEFAULT
                mBinding.rbPositionModePart.typeface = Typeface.DEFAULT_BOLD
            }
            PositionMode.FULL -> {
                mBinding.rbPositionModeFull.isChecked = true
                mBinding.rbPositionModeFull.typeface = Typeface.DEFAULT_BOLD
                mBinding.rbPositionModePart.typeface = Typeface.DEFAULT
            }
        }

        when (mPositionMergeMode) {
            PositionMergeMode.MERGE -> {
                mBinding.rbPositionMergeModeMerge.isChecked = true
                mBinding.rbPositionMergeModeMerge.typeface = Typeface.DEFAULT_BOLD
                mBinding.rbPositionMergeModePartition.typeface = Typeface.DEFAULT
            }
            PositionMergeMode.PARTITION -> {
                mBinding.rbPositionMergeModePartition.isChecked = true
                mBinding.rbPositionMergeModeMerge.typeface = Typeface.DEFAULT
                mBinding.rbPositionMergeModePartition.typeface = Typeface.DEFAULT_BOLD
            }
        }

        val unit = when(SPUtils.getTradeUnit()){
            QuantityUnit.SIZE.unit -> {
                context.getString(R.string.mc_sdk_contract_unit)
            }
            QuantityUnit.USDT.unit -> {
                context.getString(R.string.mc_sdk_usdt)
            }
            QuantityUnit.COIN.unit -> {
                McConstants.COMMON.CURRENT_PRODUCT.mBase.toUpperCase()
            }
            else -> {
                ""
            }
        }


        mBinding.tvLeverage.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val inputNum = s.toString().getInt()

                if (inputNum > mBinding.seekbar.max){
                    mBinding.seekbar.progress = mBinding.seekbar.max
                } else {
                    mBinding.seekbar.progress = inputNum
                }

                mBinding.tvLeverageWarn.visibility = if (inputNum >= 20) View.VISIBLE else View.GONE
                if (inputNum > maxLeverage) {
                    mBinding.tvLeverage.setText(maxLeverage.toString())
                }

                mBinding.tvLeverageMaxOpenValue.text = "${calcCurrentLeverageMaxOpen(inputNum)} $unit"

                mBinding.tvLeverage.postDelayed({
                    setSelection()
                },100)

            }

        })

    }

    private fun setSelection(){
        try {
            mBinding.tvLeverage.setSelection(mLeverage.get()!!.length)
        }catch (t:Throwable){
            t.printStackTrace()
        }
    }
    private fun initSeekBar() {
        val seekBarColorDrawable = resources.getDrawable(R.drawable.bg_seekbar_leverage_progress)
        val drawables = arrayOfNulls<Drawable>(2)

        drawables[0] = resources.getDrawable(R.drawable.bg_seekbar_leverage_default)

        drawables[1] = ClipDrawable(seekBarColorDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL)

        val newLayerDrawable = LayerDrawable(drawables)
        newLayerDrawable.setId(0, android.R.id.background)
        newLayerDrawable.setId(1, android.R.id.progress)

        mBinding.seekbar.progressDrawable = newLayerDrawable

        mBinding.seekbar.thumb = resources.getDrawable(R.drawable.ic_seekbar_leverage_thumb)

        mBinding.seekbar.thumbOffset = 0

        mBinding.seekbar.max = maxLeverage

        mBinding.seekbar.progress = currentLeverage

        mBinding.tvLever2.text = ((maxLeverage * 0.2).toInt()).toString() + "X"
        mBinding.tvLever3.text = ((maxLeverage * 0.4).toInt()).toString() + "X"
        mBinding.tvLever4.text = ((maxLeverage * 0.6).toInt()).toString() + "X"
        mBinding.tvLever5.text = ((maxLeverage * 0.8).toInt()).toString() + "X"
        mBinding.tvLever6.text = (maxLeverage).toString() + "X"

        mBinding.seekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(progress == 0) {
                    mLeverage.set("1")
                } else {
                    mLeverage.set(progress.toString())
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

    }

    override fun getImplLayoutId() = R.layout.mc_sdk_dialog_modify_leverage
}