package huolongluo.byw.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 火龙裸先生 on 2017/11/13.
 */

public class ListUtils
{
    private static final String TAG = "ListUtils";

    /**
     * 从一个集合里面，从0位置开始，
     * 每次从集合截取指定长度的一段集合，取出其中的最大值。
     * 最终把每次拿到的最大值，形成一个新的集合作为返回值。
     *
     * @param oldList 待处理的集合
     * @param space   每次从老集合中取多少个值进行比较
     */
    public static List<Float> getNewList(List<Float> oldList, int space)
    {
        Log.e(TAG, "===============数据源==============" + oldList);

        List<Float> last = new ArrayList<>(); // 数据源末尾多出来的几个数

        int index = 0;
        for (int i = 0; i < oldList.size(); i++)
        {
            if (index + space < oldList.size())
            {
                Log.e(TAG, "==========截取到的是======" + oldList.subList(index, index + space));
                last.add(Collections.max(oldList.subList(index, index + space)));
            }
            else
            {
                if (index < oldList.size())
                {
                    last.add(Collections.max(oldList.subList(index, oldList.size())));
                    Log.e(TAG, "********末尾集合********" + oldList.subList(index, oldList.size()));
                }
            }
            index = index + space;
        }
        Log.e(TAG, "=================最终结果=============" + last);
        Log.e(TAG, "=================最终结果===条数==========" + last.size());

        return last;
    }

    /**
     * 从一个集合里面，从0位置开始，
     * 每次从集合截取指定长度space的一段集合，取这段集合之和的平均值。
     * 最终把每次拿到的平均值，形成一个新的集合作为返回值。
     *
     * @param oldList 待处理的集合
     * @param space   每次从老集合中取多少个值进行比较
     */
    public static List<Float> getNewListCenter(List<Float> oldList, int space)
    {
        Log.e(TAG, "===============数据源==============" + oldList);

        List<Float> last = new ArrayList<>(); // 数据源末尾多出来的几个数

        int index = 0;
        for (int i = 0; i < oldList.size(); i++)
        {
            if (index + space < oldList.size())
            {
                float sum = 0;
                Log.e(TAG, "==========截取到的是======" + oldList.subList(index, index + space));
                List<Float> cutOutList = new ArrayList<>();
                cutOutList.addAll(oldList.subList(index, index + space));
                for (int j = 0; j < cutOutList.size(); j++)
                {
                    sum += cutOutList.get(j);
                }
                last.add(sum / cutOutList.size());
            }
            else
            {
                if (index < oldList.size())
                {
                    float sum = 0;
                    List<Float> endList = new ArrayList<>();
                    endList.addAll(oldList.subList(index, oldList.size()));
                    for (int j = 0; j < endList.size(); j++)
                    {
                        sum += endList.get(j);
                    }
                    last.add(sum / endList.size());
                    Log.e(TAG, "********末尾集合********" + oldList.subList(index, oldList.size()));
                }
            }
            index = index + space;
        }
        Log.e(TAG, "=================最终结果=============" + last);
        Log.e(TAG, "=================最终结果===条数==========" + last.size());

        return last;
    }

    /**
     * 获取VMA均线
     * <p>
     * 变异平均线是用每日的开盘价、收盘价、最高价和最低价相加后除以4得出的数据计算平均线
     */
    public static List<Float> getVMAList(List<Float> highListsFinal, List<Float> lowListsFinal, List<Float> openListsFinal, List<Float> closeListsFinal)
    {
        List<Float> last = new ArrayList<>();
        for (int i = 0; i < highListsFinal.size(); i++)
        {
            last.add((highListsFinal.get(i) + lowListsFinal.get(i) + openListsFinal.get(i) + closeListsFinal.get(i)) / 4);
        }
        L.e("===========VMA 条数========== ：" + last.size());
        return last;
    }

    /**
     * 从一个集合里面，从0位置开始，
     * 每次从集合截取指定长度space的一段集合，取这段集合中的最后一只值。
     * 最终把每次拿到的值，形成一个新的集合作为返回值。
     *
     * @param dateList 待处理的集合
     * @param space    每次从老集合中取多少个值作为一段集合
     */
    public static List<Long> getDateNew(List<Long> dateList, int space)
    {
        List<Long> last = new ArrayList<>();

        int index = 0;
        for (int i = 0; i < dateList.size(); i++)
        {
            if (index + space < dateList.size())
            {
                Log.e(TAG, "==========截取到的是======" + dateList.subList(index, index + space));

                last.add(dateList.subList(index, index + space).get(space - 1));
            }
            else
            {
                if (index < dateList.size())
                {

                    int overSize = dateList.subList(index, dateList.size()).size();

                    last.add((dateList.subList(index, dateList.size())).get(overSize - 1));
                    Log.e(TAG, "********末尾集合********" + dateList.subList(index, dateList.size()));
                }
            }
            index = index + space;
        }
        Log.e(TAG, "=================最终结果=============" + last);
        Log.e(TAG, "=================最终结果===条数==========" + last.size());

        return last;
    }
    
}
