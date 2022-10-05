package com.android.coinw.model.result;
public class AssetResult {
    public boolean result;
    public int tradeId;
    public int selfselection;
    public int code;
    public String value;
    public Asset asset;

    public static class Asset {
        public String totalAsset;
        public Coin rmb;
        public Coin coin;
    }

    public static class Coin {
        public double total;
        public double frozen;
        public double canBuy;
        public double canSell;
    }
}
