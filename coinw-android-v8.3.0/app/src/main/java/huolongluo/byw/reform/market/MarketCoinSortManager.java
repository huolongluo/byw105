package huolongluo.byw.reform.market;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import huolongluo.byw.byw.bean.MarketListBean;

/**
 * Created by Administrator on 2018/12/24 0024.
 */

public class MarketCoinSortManager {




//    private    static List<Integer> mLocalSelIdList=new ArrayList<>();
//    private    static List<Integer> mServiceSelIdList=new ArrayList<>();
//    private    static List<Integer> mAllSelIdList=new ArrayList<>();
//
    private    static List<Integer> mMyIdList=new ArrayList<>();
//
//
//    public static List<Integer> getmServiceSelIdList() {
//        return mServiceSelIdList;
//    }
//
//    public static void setmLocalSelIdList(List<Integer> localSelIdList){
//        mLocalSelIdList=localSelIdList;
//        mAllSelIdList.clear();
//        mAllSelIdList.addAll(mLocalSelIdList);
//        mAllSelIdList.addAll(mServiceSelIdList);
//    }
//    public static void setAllSelIdList(List<Integer> allSelIdList){
//           mAllSelIdList=allSelIdList;
//    }
//
//    public static void setServiceSelIdList(List<MarketListBean> serviceSelIdList){
//        mServiceSelIdList.clear();
//        mAllSelIdList.clear();
//        if(serviceSelIdList!=null){
//            for(MarketListBean bean:serviceSelIdList){
//                mServiceSelIdList .add(bean.getTrademId());
//            }
//            mAllSelIdList.addAll(mServiceSelIdList);
//        }else {
//
//
//            mAllSelIdList.addAll(mLocalSelIdList);
//        }
//
//
//
//        Log.e("行情查询","mServiceSelIdList==  "+mServiceSelIdList);
//    }
//
    public static void setMyIdList(List<Integer> myIdList) {

        if(myIdList!=null){
            mMyIdList.clear();
                mMyIdList .addAll(myIdList);


        }

    }

    public static List<Integer> getmyIdList() {
        return mMyIdList;
    }
//
//    public static List<Integer> getAllSelIdList() {
//        return mAllSelIdList;
//    }
//
//    public static List<Integer> getmLocalSelIdList() {
//        return mLocalSelIdList;
//    }
}
