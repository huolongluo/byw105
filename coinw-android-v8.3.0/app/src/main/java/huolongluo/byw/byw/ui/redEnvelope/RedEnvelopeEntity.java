package huolongluo.byw.byw.ui.redEnvelope;

import android.os.Parcel;
import android.os.Parcelable;

public class RedEnvelopeEntity implements Parcelable {

    /**
     * param : ["11"]
     * url : “redenvelope”
     * type : 11111
     * data : {“isHaveNew”:1}
     */

    public int isHaveNew;

    protected RedEnvelopeEntity(Parcel in) {
        isHaveNew = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(isHaveNew);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RedEnvelopeEntity> CREATOR = new Creator<RedEnvelopeEntity>() {
        @Override
        public RedEnvelopeEntity createFromParcel(Parcel in) {
            return new RedEnvelopeEntity(in);
        }

        @Override
        public RedEnvelopeEntity[] newArray(int size) {
            return new RedEnvelopeEntity[size];
        }
    };
}
