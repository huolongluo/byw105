package huolongluo.bywx.kline;
public class KLineEntity implements IKLine {

    public String getDatetime() {
        return Date;
    }

    @Override
    public float getOpenPrice() {
        return Open;
    }

    @Override
    public float getHighPrice() {
        return High;
    }

    @Override
    public float getLowPrice() {
        return Low;
    }

    @Override
    public float getClosePrice() {
        return Close;
    }

    @Override
    public float getMA5Price() {
        return MA5Price;
    }

    @Override
    public float getMA10Price() {
        return MA10Price;
    }

    @Override
    public float getMA20Price() {
        return MA20Price;
    }

    @Override
    public float getDea() {
        return dea;
    }

    @Override
    public float getDif() {
        return dif;
    }

    @Override
    public float getMacd() {
        return macd;
    }

    @Override
    public float getK() {
        return k;
    }

    @Override
    public float getD() {
        return d;
    }

    @Override
    public float getJ() {
        return j;
    }

    @Override
    public float getRsi1() {
        return rsi1;
    }

    @Override
    public float getRsi2() {
        return rsi2;
    }

    @Override
    public float getRsi3() {
        return rsi3;
    }

    @Override
    public float getUp() {
        return up;
    }

    @Override
    public float getMb() {
        return mb;
    }

    @Override
    public float getDn() {
        return dn;
    }

    @Override
    public float getVolume() {
        return Volume;
    }

    @Override
    public float getInterest() {
        return 0;
    }

    @Override
    public float getMA5Volume() {
        return MA5Volume;
    }

    @Override
    public float getMA10Volume() {
        return MA10Volume;
    }

    public String Date;
    public float Open; //开盘价
    public float Close; //收盘价
    public float High; //最高价
    public float Low; //最低价
    public float Volume; //成交量

    //ma
    public float MA5Price;
    public float MA10Price;
    public float MA20Price;

    //MACD
    public float dea; //DEA值
    public float dif; //DIF值
    public float macd; //MACD值

    //KDJ
    public float k;
    public float d;
    public float j;

    //RSI
    public float rsi1; //RSI1值
    public float rsi2; //RSI2值
    public float rsi3; //RSI3值

    //BOLL
    public float up; //上轨线
    public float mb; //中轨线
    public float dn; //下轨线

    public float MA5Volume;
    public float MA10Volume;
}