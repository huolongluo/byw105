package huolongluo.byw.byw.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * <p>
 * Created by 火龙裸 on 2017/12/27 0027.
 */
public class RmbBean implements Parcelable
{
    /**
     * total : 0
     * frozen : 0
     * canBuy : 0
     */

    private double total;
    private double frozen;
    private double canBuy;

    protected RmbBean(Parcel in) {
        total = in.readDouble();
        frozen = in.readDouble();
        canBuy = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(total);
        dest.writeDouble(frozen);
        dest.writeDouble(canBuy);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RmbBean> CREATOR = new Creator<RmbBean>() {
        @Override
        public RmbBean createFromParcel(Parcel in) {
            return new RmbBean(in);
        }

        @Override
        public RmbBean[] newArray(int size) {
            return new RmbBean[size];
        }
    };

    public double getTotal()
    {
        return total;
    }

    public void setTotal(double total)
    {
        this.total = total;
    }

    public double getFrozen()
    {
        return frozen;
    }

    public void setFrozen(double frozen)
    {
        this.frozen = frozen;
    }

    public double getCanBuy()
    {
        return canBuy;
    }

    public void setCanBuy(double canBuy)
    {
        this.canBuy = canBuy;
    }
}
