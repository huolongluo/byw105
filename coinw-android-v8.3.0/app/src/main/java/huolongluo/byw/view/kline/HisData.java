package huolongluo.byw.view.kline;
public class HisData {
    private double close;
    private double high;
    private double low;
    private double open;
    private double turnover;//成交额
    private double vol;//成交量
    private long date;


    // MA
    private double ma5;
    private double ma10;
    private double ma20;
    private double ma30;

    private double volMa5;
    private double volMa10;

    // MACD 指标的三个属性
    private double dea;
    private double diff;
    private double macd;

    // KDJ 指标的三个属性
    private double k;
    private double d;
    private double j;

    // RSI 指标的三个属性
    private double rsi1;
    private double rsi2;
    private double rsi3;

    // BOLL 指标的三个属性
    private double up; // 上轨线
    private double mb; // 中轨线
    private double dn; // 下轨线

    // EMA 指标
    private double ema5;
    private double ema10;
    private double ema30;

    //SAR 指标
    private double sar;

    //WR 指标
    private double wr10;
    private double wr6;

    //OBV 指标
    private double obv;
    private double maobv;

    public HisData() {
    }

    public HisData(long date,double open, double close, double high, double low, double vol,double turnover ) {
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.turnover=turnover;
        this.vol = vol;
        this.date = date;
    }

    public double getTurnover() {
        return turnover;
    }

    public void setTurnover(double turnover) {
        this.turnover = turnover;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getVol() {
        return vol;
    }

    public void setVol(double vol) {
        this.vol = vol;
    }


    public double getMa5() {
        return ma5;
    }

    public void setMa5(double ma5) {
        this.ma5 = ma5;
    }

    public double getMa10() {
        return ma10;
    }

    public void setMa10(double ma10) {
        this.ma10 = ma10;
    }

    public double getMa20() {
        return ma20;
    }

    public void setMa20(double ma20) {
        this.ma20 = ma20;
    }

    public double getMa30() {
        return ma30;
    }

    public void setMa30(double ma30) {
        this.ma30 = ma30;
    }


    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HisData hisData = (HisData) o;

        return date == hisData.date;
    }

    @Override
    public int hashCode() {
        return (int) (date ^ (date >>> 32));
    }

    public double getDea() {
        return dea;
    }

    public void setDea(double dea) {
        this.dea = dea;
    }

    public double getDiff() {
        return diff;
    }

    public void setDiff(double diff) {
        this.diff = diff;
    }

    public double getMacd() {
        return macd;
    }

    public void setMacd(double macd) {
        this.macd = macd;
    }

    public double getK() {
        return k;
    }

    public void setK(double k) {
        this.k = k;
    }

    public double getD() {
        return d;
    }

    public void setD(double d) {
        this.d = d;
    }

    public double getJ() {
        return j;
    }

    public void setJ(double j) {
        this.j = j;
    }

    public double getRsi1() {
        return rsi1;
    }

    public void setRsi1(double rsi1) {
        this.rsi1 = rsi1;
    }

    public double getRsi2() {
        return rsi2;
    }

    public void setRsi2(double rsi2) {
        this.rsi2 = rsi2;
    }

    public double getRsi3() {
        return rsi3;
    }

    public void setRsi3(double rsi3) {
        this.rsi3 = rsi3;
    }

    public double getUp() {
        return up;
    }

    public void setUp(double up) {
        this.up = up;
    }

    public double getMb() {
        return mb;
    }

    public void setMb(double mb) {
        this.mb = mb;
    }

    public double getDn() {
        return dn;
    }

    public void setDn(double dn) {
        this.dn = dn;
    }

    public double getEma5() {
        return ema5;
    }

    public void setEma5(double ema5) {
        this.ema5 = ema5;
    }

    public double getEma10() {
        return ema10;
    }

    public void setEma10(double ema10) {
        this.ema10 = ema10;
    }

    public double getEma30() {
        return ema30;
    }

    public void setEma30(double ema30) {
        this.ema30 = ema30;
    }

    public double getSar() {
        return sar;
    }

    public void setSar(double sar) {
        this.sar = sar;
    }

    public double getWr10() {
        return wr10;
    }

    public void setWr10(double wr10) {
        this.wr10 = wr10;
    }

    public double getWr6() {
        return wr6;
    }

    public void setWr6(double wr6) {
        this.wr6 = wr6;
    }

    public double getObv() {
        return obv;
    }

    public void setObv(double obv) {
        this.obv = obv;
    }

    public double getMaobv() {
        return maobv;
    }

    public void setMaobv(double maobv) {
        this.maobv = maobv;
    }

    public double getVolMa5() {
        return volMa5;
    }

    public void setVolMa5(double volMa5) {
        this.volMa5 = volMa5;
    }

    public double getVolMa10() {
        return volMa10;
    }

    public void setVolMa10(double volMa10) {
        this.volMa10 = volMa10;
    }

    @Override
    public String toString() {
        return "HisData{" + "close=" + close + ", high=" + high + ", low=" + low + ", open=" + open + ", vol=" + vol + ", date=" + date + ", ma5=" + ma5 + ", ma10=" + ma10 + ", ma20=" + ma20 + ", ma30=" + ma30 + ", volMa5=" + volMa5 + ", volMa10=" + volMa10 + ", dea=" + dea + ", diff=" + diff + ", macd=" + macd + ", k=" + k + ", d=" + d + ", j=" + j + ", rsi1=" + rsi1 + ", rsi2=" + rsi2 + ", rsi3=" + rsi3 + ", up=" + up + ", mb=" + mb + ", dn=" + dn + ", ema5=" + ema5 + ", ema10=" + ema10 + ", ema30=" + ema30 + ", sar=" + sar + ", wr10=" + wr10 + ", wr6=" + wr6 + ", obv=" + obv + ", maobv=" + maobv + '}';
    }
}
