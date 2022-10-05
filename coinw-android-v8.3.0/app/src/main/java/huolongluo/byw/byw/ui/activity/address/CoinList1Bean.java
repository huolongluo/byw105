package huolongluo.byw.byw.ui.activity.address;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by LS on 2018/7/12.
 */

public class CoinList1Bean implements Parcelable{
    private String id;
    private String logo;
    private String fullName;

    public CoinList1Bean(){

    }

    protected CoinList1Bean(Parcel in) {
        id = in.readString();
        logo = in.readString();
        fullName = in.readString();
    }

    public static final Creator<CoinList1Bean> CREATOR = new Creator<CoinList1Bean>() {
        @Override
        public CoinList1Bean createFromParcel(Parcel in) {
            return new CoinList1Bean(in);
        }

        @Override
        public CoinList1Bean[] newArray(int size) {
            return new CoinList1Bean[size];
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
