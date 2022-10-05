package huolongluo.byw.byw.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * <p>
 * Created by 火龙裸 on 2017/12/27 0027.
 */
public class CoinBean implements Parcelable {
    /**
     * total : 0
     * frozen : 0
     * canSell : 0
     */

    private double total;
    private double frozen;
    private double canSell;

    protected CoinBean(Parcel in) {
        total = in.readDouble();
        frozen = in.readDouble();
        canSell = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(total);
        dest.writeDouble(frozen);
        dest.writeDouble(canSell);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CoinBean> CREATOR = new Creator<CoinBean>() {
        @Override
        public CoinBean createFromParcel(Parcel in) {
            return new CoinBean(in);
        }

        @Override
        public CoinBean[] newArray(int size) {
            return new CoinBean[size];
        }
    };

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getFrozen() {
        return frozen;
    }

    public void setFrozen(double frozen) {
        this.frozen = frozen;
    }

    public double getCanSell() {
        return canSell;
    }

    public void setCanSell(double canSell) {
        this.canSell = canSell;
    }

    @Override
    public String toString() {
        return "CoinBean{" + "total=" + total + ", frozen=" + frozen + ", canSell=" + canSell + '}';
    }
}
