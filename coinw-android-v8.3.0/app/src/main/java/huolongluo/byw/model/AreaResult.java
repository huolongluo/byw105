package huolongluo.byw.model;
import java.util.List;
public class AreaResult{
    //    {"code":"200","data":{"result":true,"areaList":[{"id":"中国","area":"中国"},{"id":"英国","area":"英国"},{"id":"美国","area":"美国"},{"id":"韩国","area":"韩国"},{"id":"日本","area":"日本"},{"id":"越南","area":"越南"},{"id":"马拉西亚","area":"马拉西亚"},{"id":"新加坡","area":"新加坡"},{"id":"中国香港","area":"中国香港"},{"id":"中国台湾","area":"中国台湾"},{"id":"中国澳门","area":"中国澳门"},{"id":"伊朗","area":"伊朗"},{"id":"其他国家和地区","area":"其他国家和地区"}],"code":0,"value":"成功"},"message":"执行成功"}
    public boolean result;
    public String message;
    public List<Area> areaList;
}
