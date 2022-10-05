package huolongluo.byw.byw.ui.activity.address;

/**
 * Created by admin on 2016/7/22.
 */
import java.util.Comparator;

import huolongluo.byw.byw.ui.activity.countryselect.CountrySortModel;
/**
 *
 * 类简要描述
 *
 * <p>
 * 类详细描述
 * </p>
 *
 * @author duanbokan
 *
 */

public class AddressComparator implements Comparator<AddressListBean>
{

    @Override
    public int compare(AddressListBean o1, AddressListBean o2)
    {

        return o1.sortLetters.compareTo(o2.sortLetters);
    }

}
