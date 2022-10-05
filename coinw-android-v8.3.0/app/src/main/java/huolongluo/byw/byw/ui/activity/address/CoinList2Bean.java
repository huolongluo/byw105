package huolongluo.byw.byw.ui.activity.address;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by LS on 2018/7/16.
 */

public class CoinList2Bean implements Parcelable{
    private String id;
    private String logo;
    private String fullName;

    protected CoinList2Bean(Parcel in) {
        id = in.readString();
        logo = in.readString();
        fullName = in.readString();
    }

    public static final Creator<CoinList2Bean> CREATOR = new Creator<CoinList2Bean>() {
        @Override
        public CoinList2Bean createFromParcel(Parcel in) {
            return new CoinList2Bean(in);
        }

        @Override
        public CoinList2Bean[] newArray(int size) {
            return new CoinList2Bean[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(logo);
        parcel.writeString(fullName);
    }
}
