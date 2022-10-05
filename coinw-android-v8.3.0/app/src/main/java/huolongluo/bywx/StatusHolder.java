package huolongluo.bywx;

public class StatusHolder {
    private static StatusHolder mInstance;
    private boolean isKill = true;

    public boolean isKill() {
        return isKill;
    }

    public void setKill(boolean kill) {
        isKill = kill;
    }

    private StatusHolder() {
    }

    public static StatusHolder getInstance() {
        if (mInstance == null) {
            synchronized (StatusHolder.class) {
                if (mInstance == null) {
                    mInstance = new StatusHolder();
                }
            }
        }
        return mInstance;
    }
}
