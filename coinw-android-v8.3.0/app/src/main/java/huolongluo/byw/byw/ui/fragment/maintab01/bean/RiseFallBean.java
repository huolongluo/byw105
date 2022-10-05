package huolongluo.byw.byw.ui.fragment.maintab01.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.github.mikephil.charting.data.Entry;
import huolongluo.byw.util.noru.NorUtils;
import huolongluo.bywx.utils.DoubleUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hy on 2018/10/18 0018.
 */
public class RiseFallBean implements Parcelable {

    public List<CoinInfoBean> riseList;
    public List<CoinInfoBean> fallList;
    public List<CoinInfoBean> legalMoneyList;
    public List<CoinInfoBean> appRecommend;
    public List<CoinInfoBean> newList;
    public List<CoinInfoBean> mainList; //主流榜
    public List<List<String>> totalAsset = new ArrayList<>();
    public ArrayList<Entry> values;

    protected RiseFallBean(Parcel in) {
        riseList = in.readArrayList(CoinInfoBean.class.getClassLoader());
        fallList = in.readArrayList(CoinInfoBean.class.getClassLoader());
        legalMoneyList = in.readArrayList(CoinInfoBean.class.getClassLoader());
        appRecommend = in.readArrayList(CoinInfoBean.class.getClassLoader());
        newList = in.readArrayList(CoinInfoBean.class.getClassLoader());
        mainList = in.readArrayList(CoinInfoBean.class.getClassLoader());
        in.readList(totalAsset,List.class.getClassLoader());
        values = in.createTypedArrayList(Entry.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(riseList);
        dest.writeList(fallList);
        dest.writeList(legalMoneyList);
        dest.writeList(appRecommend);
        dest.writeList(newList);
        dest.writeList(mainList);
        dest.writeList(totalAsset);
        dest.writeTypedList(values);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RiseFallBean> CREATOR = new Creator<RiseFallBean>() {
        @Override
        public RiseFallBean createFromParcel(Parcel in) {
            return new RiseFallBean(in);
        }

        @Override
        public RiseFallBean[] newArray(int size) {
            return new RiseFallBean[size];
        }
    };

    public List<CoinInfoBean> getRiseList() {
        return riseList;
    }

    public void setRiseList(List<CoinInfoBean> riseList) {
        this.riseList = riseList;
    }

    public List<CoinInfoBean> getFallList() {
        return fallList;
    }

    public void setFallList(List<CoinInfoBean> fallList) {
        this.fallList = fallList;
    }

    public List<CoinInfoBean> getLegalMoneyList() {
        return legalMoneyList;
    }

    public void setLegalMoneyList(List<CoinInfoBean> legalMoneyList) {
        this.legalMoneyList = legalMoneyList;
    }

    public List<CoinInfoBean> getAppRecommend() {
        return appRecommend;
    }

    public void setAppRecommend(List<CoinInfoBean> appRecommend) {
        this.appRecommend = appRecommend;
    }

    @Override
    public String toString() {
        return "RiseFallBean{" +
                "riseList=" + riseList +
                ", fallList=" + fallList +
                ", mainList=" + mainList +
                ", legalMoneyList=" + legalMoneyList +
                ", appRecommend=" + appRecommend +
                ", totalAsset=" + totalAsset +
                '}';
    }

    public List<List<String>> getTotalAsset() {
        return totalAsset;
    }

    public void setTotalAsset(List<List<String>> totalAsset) {
        this.totalAsset = totalAsset;
    }

    public ArrayList<Entry> getEntryList() {
        return values;
    }

    /**
     * 由历史原因，优化数据处理过程
     */
    public void parseEntry() {
        values = new ArrayList<>();
        if (totalAsset == null || totalAsset.isEmpty()) {
            return;
        }
//        for (int i = 0; i < totalAsset.size(); i++) {
//            //  float val = (float) (Math.random() * range) + 3;
//            //  values.add(new Entry(i, val));
//            // values.add(new Entry(Float.valueOf(list.get(i).get(2) + ""), Float.valueOf(NorUtils.NumberFormat(2).format(Double.parseDouble(list.get(i).get(1)) / 100000000)), ""));
//            values.add(new Entry(i, Float.valueOf(NorUtils.NumberFormat(2).format(Double.parseDouble(list.get(i).get(1)) / 100000000)), ""));
//        }
        int index = 0;
        for (List<String> data : totalAsset) {
            values.add(new Entry(index++, Float.valueOf(NorUtils.NumberFormat(2).format(DoubleUtils.parseDouble(data.get(1)) / 100000000)), ""));
        }
    }
}
