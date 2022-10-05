package huolongluo.byw.model;
import java.util.LinkedList;
import java.util.List;
public class HomeDynamicResult {
    public String message;
    public String code;
    public List<HomeDynamic> level0 = new LinkedList<HomeDynamic>();//第一行
    public List<HomeDynamic> level1 = new LinkedList<HomeDynamic>();//第二行
    public List<HomeDynamic> level2 = new LinkedList<HomeDynamic>();//第三行

    public static class HomeDynamic {
        public boolean isLocal = false;
        public String desc;
        public String imgUrl;
        public int imgResId = 0;
        public int portalType = 0;
        public String portalUrl = "";
        public String secondTitle;
        public int sort;
        public String tag;
        public String title;
        public int type;
    }
}
