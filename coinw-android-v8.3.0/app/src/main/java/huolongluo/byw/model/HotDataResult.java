package huolongluo.byw.model;
import java.util.List;
public class HotDataResult {
    public String message;
    public String code;
    public int forceUpdate;
    public SubHotDataResult data;

    public static class SubHotDataResult {
        public List<Hot> hotsearchList;
        public int code;
        public String value;
    }
}
