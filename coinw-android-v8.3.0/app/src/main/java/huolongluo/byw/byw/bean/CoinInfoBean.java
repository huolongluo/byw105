package huolongluo.byw.byw.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by 火龙裸 on 2017/12/27.
 */
public class CoinInfoBean implements Parcelable
{
    /**
     * id : 3
     * fname : BC
     * fShortName : BC
     * fSymbol : b
     */

    private int id;
    private String fname;
    private String fShortName;
    private String fSymbol;

    public CoinInfoBean() {}

    protected CoinInfoBean(Parcel in) {
        id = in.readInt();
        fname = in.readString();
        fShortName = in.readString();
        fSymbol = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(fname);
        dest.writeString(fShortName);
        dest.writeString(fSymbol);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CoinInfoBean> CREATOR = new Creator<CoinInfoBean>() {
        @Override
        public CoinInfoBean createFromParcel(Parcel in) {
            return new CoinInfoBean(in);
        }

        @Override
        public CoinInfoBean[] newArray(int size) {
            return new CoinInfoBean[size];
        }
    };

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getFname()
    {
        return fname;
    }

    public void setFname(String fname)
    {
        this.fname = fname;
    }

    public String getFShortName()
    {
        return fShortName;
    }

    public void setFShortName(String fShortName)
    {
        this.fShortName = fShortName;
    }

    public String getFSymbol()
    {
        return fSymbol;
    }

    public void setFSymbol(String fSymbol)
    {
        this.fSymbol = fSymbol;
    }

    @Override
    public String toString() {
        return "CoinInfoBean{" +
                "id=" + id +
                ", fname='" + fname + '\'' +
                ", fShortName='" + fShortName + '\'' +
                ", fSymbol='" + fSymbol + '\'' +
                '}';
    }
}
