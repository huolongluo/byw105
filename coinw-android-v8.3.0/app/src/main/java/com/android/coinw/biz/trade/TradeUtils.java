package com.android.coinw.biz.trade;

import android.text.TextUtils;

import com.android.coinw.api.kx.model.XDepthData;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.reform.trade.bean.TradeOrder;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.MathHelper;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.byw.util.pricing.PricingMethodUtil;
import huolongluo.bywx.utils.DoubleUtils;

//币币交易工具类
public class TradeUtils {
    private static final String TAG = "TradeUtils";
    private List<TradeOrder.OrderInfo> depthList = new ArrayList<>();//缓存最新的盘口数据，用于合并时保证数据
    private static TradeUtils instance;
    private DecimalFormat df = new DecimalFormat("#");

    private TradeUtils() {
    }

    public static TradeUtils getInstance() {
        if (instance == null) {
            instance = new TradeUtils();
        }
        return instance;
    }

    public void setDepthList(List<TradeOrder.OrderInfo> depthList) {
        this.depthList.clear();
        if (depthList == null) {
            return;
        }
        this.depthList.addAll(depthList);
    }

    public List<TradeOrder.OrderInfo> getDepthList() {
        return depthList;
    }

    //盘口数据因为精度原因可能需要合并数据,买盘精度向上取整
    public List<TradeOrder.OrderInfo> mergeBuyDepthList(List<TradeOrder.OrderInfo> list, int depth) {
        list.clear();
        if (depth <= 0) {
            df.setRoundingMode(RoundingMode.UP);
            for (int i = 0; i < depthList.size(); i++) {
                TradeOrder.OrderInfo info = (TradeOrder.OrderInfo) depthList.get(i).clone();//重新创建一个相同的对象给list，保证depthList始终为最新数据
                if (info != null) {
                    list.add(info);
                }
            }
            if (list.size() == 0 || TextUtils.isEmpty(list.get(0).buyPrice)) return list;
            if (PricingMethodUtil.getPrecision(list.get(0).buyPrice) <= depth) {//数据的精度和约束的精度小于等于不做操作
                return list;
            }
            int num = list.size() - 1;
            ArrayList<Integer> removePosition = new ArrayList<>();//记录需要删除的位置

            for (int i = 0; i < num; i++) {
                double buyPrice = DoubleUtils.parseDouble(list.get(i).buyPrice);
                double buyPrice2 = DoubleUtils.parseDouble(list.get(i + 1).buyPrice);
                int intPrice = getDepthValue(buyPrice, depth);
                int intPrice2 = getDepthValue(buyPrice2, depth);
                double buyAmount = DoubleUtils.parseDouble(list.get(i).buyAmount);
                if (intPrice == 0 || buyAmount == 0) {
                    removePosition.add(i);//做完合并再删除数据
                    continue;
                }
                list.get(i).buyPrice = intPrice + "";
                if (intPrice == intPrice2) {//精度控制后值相同需要合并数据
                    list.get(i + 1).buyAmount = MathHelper.add(list.get(i).buyAmount, list.get(i + 1).buyAmount) + "";
                    removePosition.add(i);//做完合并再删除数据
                    continue;
                }
            }
            for (int i = 0; i < removePosition.size(); i++) {
                list.remove(removePosition.get(i).intValue() - i);//list每移除一条，自身size会减一，所以之前的记录的位置需要减去i
            }
            //返回数据
            return list;
        }
        for (int i = 0; i < depthList.size(); i++) {
            TradeOrder.OrderInfo info = (TradeOrder.OrderInfo) depthList.get(i).clone();//重新创建一个相同的对象给list，保证depthList始终为最新数据
            if (info != null) {
                list.add(info);
            }
        }
        if (list.size() == 0 || TextUtils.isEmpty(list.get(0).buyPrice)) return list;
        if (PricingMethodUtil.getPrecision(list.get(0).buyPrice) <= depth) {//数据的精度和约束的精度小于等于不做操作
            return list;
        }
        int num = list.size() - 1;
        ArrayList<Integer> removePosition = new ArrayList<>();//记录需要删除的位置
        for (int i = 0; i < num; i++) {
            String price = NorUtils.NumberFormat(depth, RoundingMode.UP).format(DoubleUtils.parseDouble(list.get(i).buyPrice));
            String price2 = NorUtils.NumberFormat(depth, RoundingMode.UP).format(DoubleUtils.parseDouble(list.get(i + 1).buyPrice));
            if (TextUtils.equals(price, price2)) {//精度控制后值相同需要合并数据
                list.get(i + 1).buyAmount = MathHelper.add(list.get(i).buyAmount, list.get(i + 1).buyAmount) + "";
                removePosition.add(i);//做完合并再删除数据
                continue;
            }
        }
        for (int i = 0; i < removePosition.size(); i++) {
            list.remove(removePosition.get(i).intValue() - i);//list每移除一条，自身size会减一，所以之前的记录的位置需要减去i
        }
        return list;
    }

    public int getDepthValue(double price, int depth) {
        if (depth < 0) {
            int d = Math.abs(depth);
            int pow = (int) Math.pow(10, d);
            if (price % pow > 0) {
                price = price / pow + 1;
            } else {
                price = price / pow;
            }
            return (int) price;
        } else {
            if (price % 1 > 0) {
                price = price + 1;
            }
            return (int) price;
        }
    }

    //盘口数据因为精度原因可能需要合并数据,卖盘精度向下取整
    public List<TradeOrder.OrderInfo> mergeSellDepthList(List<TradeOrder.OrderInfo> list, int depth) {
        list.clear();
        if (depth <= 0) {
            df.setRoundingMode(RoundingMode.UP);
            //重新创建一个相同的对象给list，保证depthList始终为最新数据
            for (int i = 0; i < depthList.size(); i++) {
                TradeOrder.OrderInfo info = (TradeOrder.OrderInfo) depthList.get(i).clone();
                if (info != null) {
                    list.add(info);
                }
            }
            if (list.size() == 0 || TextUtils.isEmpty(list.get(0).sellPrice)) return list;
            if (PricingMethodUtil.getPrecision(list.get(0).sellPrice) <= depth) {//数据的精度和约束的精度小于等于不做操作
                return list;
            }
            int num = list.size() - 1;
            ArrayList<Integer> removePosition = new ArrayList<>();//记录需要删除的位置
            for (int i = 0; i < num; i++) {
                double sellPrice = DoubleUtils.parseDouble(list.get(i).sellPrice);
                double sellPrice2 = DoubleUtils.parseDouble(list.get(i + 1).sellPrice);
                int intPrice = getDepthValue(sellPrice, depth);
                int intPrice2 = getDepthValue(sellPrice2, depth);
                double sellAmount = DoubleUtils.parseDouble(list.get(i).sellAmount);
                if (intPrice == 0 || sellAmount == 0) {
                    removePosition.add(i);//做完合并再删除数据
                    continue;
                }
                list.get(i).sellPrice = intPrice + "";
                if (intPrice == intPrice2) {//精度控制后值相同需要合并数据
                    list.get(i + 1).buyAmount = MathHelper.add(list.get(i).sellAmount, list.get(i + 1).sellAmount) + "";
                    removePosition.add(i);//做完合并再删除数据
                    continue;
                }
            }

            for (int i = 0; i < removePosition.size(); i++) {
                list.remove(removePosition.get(i).intValue() - i);
            }
            return list;
        }
        //重新创建一个相同的对象给list，保证depthList始终为最新数据
        for (int i = 0; i < depthList.size(); i++) {
            TradeOrder.OrderInfo info = (TradeOrder.OrderInfo) depthList.get(i).clone();
            if (info != null) {
                list.add(info);
            }
        }
        if (list.size() == 0 || TextUtils.isEmpty(list.get(0).sellPrice)) return list;
        if (PricingMethodUtil.getPrecision(list.get(0).sellPrice) <= depth) {//数据的精度和约束的精度小于等于不做操作
            return list;
        }
        int num = list.size() - 1;
        ArrayList<Integer> removePosition = new ArrayList<>();//记录需要删除的位置
        for (int i = 0; i < num; i++) {
            String price = NorUtils.NumberFormat(depth, RoundingMode.DOWN).format(DoubleUtils.parseDouble(list.get(i).sellPrice));
            String price2 = NorUtils.NumberFormat(depth, RoundingMode.DOWN).format(DoubleUtils.parseDouble(list.get(i + 1).sellPrice));
            if (TextUtils.equals(price, price2)) {//精度控制后值相同需要合并数据
                list.get(i + 1).sellAmount = MathHelper.add(list.get(i).sellAmount, list.get(i + 1).sellAmount) + "";
                removePosition.add(i);//做完合并再删除数据
                continue;
            }
        }
        for (int i = 0; i < removePosition.size(); i++) {
            list.remove(removePosition.get(i).intValue() - i);
        }
        return list;
    }

    /**
     * 新系统数据结构转为以前通用的数据结构
     */
    public static ArrayList<TradeOrder.OrderInfo> getDepthList(XDepthData data) {
        if (data.getAsks() == null && data.getBids() == null) {
            return null;
        }
        int num = 0;
        if (data.getAsks() == null) {
            num = data.getBids().size();
        } else if (data.getBids() == null) {
            num = data.getAsks().size();
        } else {
            num = Math.max(data.getAsks().size(), data.getBids().size());
        }

        ArrayList<TradeOrder.OrderInfo> list = new ArrayList<>();
        try {
            for (int i = 0; i < num; i++) {
                TradeOrder.OrderInfo info = new TradeOrder.OrderInfo();
                if (data.getAsks() != null && data.getAsks().size() > i && data.getAsks().get(0).size() > 1) {//卖的长度足够
                    info.sellPrice = data.getAsks().get(i).get(0);
                    info.sellAmount = data.getAsks().get(i).get(1);
                }
                if (data.getBids() != null && data.getBids().size() > i && data.getBids().get(0).size() > 1) {
                    info.buyPrice = data.getBids().get(i).get(0);
                    info.buyAmount = data.getBids().get(i).get(1);
                }
                list.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
