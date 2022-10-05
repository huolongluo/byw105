package huolongluo.bywx.helper;
import android.renderscript.Sampler;

import java.util.ArrayList;
import java.util.List;

import huolongluo.bywx.kline.KData;
import huolongluo.bywx.utils.DoubleUtils;
import huolongluo.bywx.utils.ValueUtils;
public class KLineHelper {
    private static final int QUOTA_DAY5 = 5;
    private static final int QUOTA_DAY10 = 10;
    private static final int QUOTA_DAY30 = 30;
    private static final float BEZIER_RATIO = 0.16f;
    //            dateList.add(Long.parseLong(lists.get(i).get(0)));
//        highLists.add(Float.parseFloat(lists.get(i).get(2)));
//        lowLists.add(Float.parseFloat(lists.get(i).get(3)));
//        openLists.add(Float.parseFloat(lists.get(i).get(1)));
//        closeLists.add(Float.parseFloat(lists.get(i).get(4)));
//        transAmount.add(Float.parseFloat(lists.get(i).get(5)));
////TODO 优化
////                lowListsStr.add(lists.get(i).get(3));
////                highListsStr.add(lists.get(i).get(2));
//        openListsFinalStr.add(lists.get(i).get(1));
//        closeListsFinalStr.add(lists.get(i).get(4));

    private static List<KData> convertData(List<List<String>> dataList) {
        List<KData> kdataList = new ArrayList<KData>();
        for (List<String> list : dataList) {
            KData kdata = new KData();
            kdata.setMaxPrice(DoubleUtils.parseDouble(list.get(2)));//high
            kdata.setMinPrice(DoubleUtils.parseDouble(list.get(3)));//low
            kdata.setOpenPrice(DoubleUtils.parseDouble(list.get(1)));
            kdata.setClosePrice(DoubleUtils.parseDouble(list.get(4)));
            kdata.setTime(ValueUtils.getLong(list.get(0)));
            kdata.setVolume(DoubleUtils.parseDouble(list.get(5)));
        }
        return kdataList;
    }

    public static void initEmaForSrcData(List<List<String>> dataList, boolean isEndData) {
        initEma(convertData(dataList), isEndData);
    }

    public static void initEma(List<KData> dataList, boolean isEndData) {
        long startEma = System.currentTimeMillis();
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        double lastEma5 = dataList.get(0).getClosePrice();
        double lastEma10 = dataList.get(0).getClosePrice();
        double lastEma30 = dataList.get(0).getClosePrice();
        dataList.get(0).setEma5(lastEma5);
        dataList.get(0).setEma10(lastEma10);
        dataList.get(0).setEma30(lastEma30);

        for (int i = 1; i < dataList.size(); i++) {
            if (dataList.get(i).getEma30() != 0 && !isEndData) {
                break;
            }
            double currentEma5 = 2 * (dataList.get(i).getClosePrice() - lastEma5) / (QUOTA_DAY5 + 1) + lastEma5;
            double currentEma10 = 2 * (dataList.get(i).getClosePrice() - lastEma10) / (QUOTA_DAY10 + 1) + lastEma10;
            double currentEma30 = 2 * (dataList.get(i).getClosePrice() - lastEma30) / (QUOTA_DAY30 + 1) + lastEma30;

            dataList.get(i).setEma5(currentEma5);
            dataList.get(i).setEma10(currentEma10);
            dataList.get(i).setEma30(currentEma30);

            lastEma5 = currentEma5;
            lastEma10 = currentEma10;
            lastEma30 = currentEma30;
        }
        long endEma = System.currentTimeMillis();
//        PrintUtil.log("EMA", endEma - startEma);
    }

}
