package huolongluo.byw.byw.bean;


import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Created by 火龙裸 on 2017/12/24 0024.
 */
public class HomeAdvertBean implements Serializable
{

    /**
     * result : true
     * lastUpdateTime : 2017-12-24 22:36:47
     * banners : [{"img":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201709010355057_a6Ia9.png","url":"#","title":"标题"},
     * {"img":"https://btc018.oss-cn-shenzhen.aliyuncs.com/201709010356052_k7DGw.png","url":"#","title":"标题"},{"img":"https://btc018.oss-cn-shenzhen.aliyuncs
     * .com/201709010357025_i4oQ6.png","url":"#","title":"标题"}]
     * news : [{"id":100,"title":"关于ETF,EMO糖果分发公告","date":"2017-12-24","from":"币赢网-以太坊价格-以太坊交易-数字资产-Coinw给您最好的交易体验","img":null,"type":"官方新闻"},{"id":99,
     * "title":"Coinw即将开通Maggie(MAG)充值及上线公告","date":"2017-12-24","from":"币赢网-以太坊价格-以太坊交易-数字资产-Coinw给您最好的交易体验","img":null,"type":"官方新闻"},{"id":98,"title":" 
     * Coinw即将开通DEW充值及上线公告","date":"2017-12-24","from":"币赢网-以太坊价格-以太坊交易-数字资产-Coinw给您最好的交易体验","img":null,"type":"官方新闻"},{"id":97,
     * "title":"Coinw即将开通Oneroot（RNT）充值及上线公告","date":"2017-12-18","from":"币赢网-以太坊价格-以太坊交易-数字资产-Coinw给您最好的交易体验","img":null,"type":"官方新闻"},{"id":96,
     * "title":"关于Coinw币赢网金牌经纪人佣金结算公告","date":"2017-12-17","from":"币赢网-以太坊价格-以太坊交易-数字资产-Coinw给您最好的交易体验","img":null,"type":"官方新闻"},{"id":95,
     * "title":"关于ETF（以太雾）糖果分发进展说明","date":"2017-12-17","from":"币赢网-以太坊价格-以太坊交易-数字资产-Coinw给您最好的交易体验","img":null,"type":"官方新闻"},{"id":94,
     * "title":"币赢网即将上线Datum（DAT）公告","date":"2017-12-15","from":"币赢网-以太坊价格-以太坊交易-数字资产-Coinw给您最好的交易体验","img":null,"type":"官方新闻"},{"id":93,
     * "title":"因代领糖果，以太币充值和提现业务调整","date":"2017-12-14","from":"币赢网-以太坊价格-以太坊交易-数字资产-Coinw给您最好的交易体验","img":null,"type":"官方新闻"},{"id":92,
     * "title":"关于12月15日ETF糖果公告","date":"2017-12-14","from":"币赢网-以太坊价格-以太坊交易-数字资产-Coinw给您最好的交易体验","img":null,"type":"官方新闻"},{"id":91,
     * "title":"关于12月15日EMO糖果公告","date":"2017-12-14","from":"币赢网-以太坊价格-以太坊交易-数字资产-Coinw给您最好的交易体验","img":null,"type":"官方新闻"}]
     * currentPage : 1
     * totalPage : 3
     */

    private boolean result;
    private String lastUpdateTime;
    private int currentPage;
    private int totalPage;
    private List<BannersBean> banners;
    private List<NewsBean> news;

    public boolean isResult()
    {
        return result;
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public String getLastUpdateTime()
    {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime)
    {
        this.lastUpdateTime = lastUpdateTime;
    }

    public int getCurrentPage()
    {
        return currentPage;
    }

    public void setCurrentPage(int currentPage)
    {
        this.currentPage = currentPage;
    }

    public int getTotalPage()
    {
        return totalPage;
    }

    public void setTotalPage(int totalPage)
    {
        this.totalPage = totalPage;
    }

    public List<BannersBean> getBanners()
    {
        return banners;
    }

    public void setBanners(List<BannersBean> banners)
    {
        this.banners = banners;
    }

    public List<NewsBean> getNews()
    {
        return news;
    }

    public void setNews(List<NewsBean> news)
    {
        this.news = news;
    }

    public static class BannersBean
    {
        /**
         * img : https://btc018.oss-cn-shenzhen.aliyuncs.com/201709010355057_a6Ia9.png
         * url : #
         * title : 标题
         */

        private String img;
        private String url;
        private String title;

        public String getImg()
        {
            return img;
        }

        public void setImg(String img)
        {
            this.img = img;
        }

        public String getUrl()
        {
            return url;
        }

        public void setUrl(String url)
        {
            this.url = url;
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }
    }

    public static class NewsBean
    {
        /**
         * id : 100
         * title : 关于ETF,EMO糖果分发公告
         * date : 2017-12-24
         * from : 币赢网-以太坊价格-以太坊交易-数字资产-Coinw给您最好的交易体验
         * img : null
         * type : 官方新闻
         */

        private int id;
        private String title;
        private String date;
        private String from;
        private Object img;
        private String type;

        public int getId()
        {
            return id;
        }

        public void setId(int id)
        {
            this.id = id;
        }

        public String getTitle()
        {
            return title;
        }

        public void setTitle(String title)
        {
            this.title = title;
        }

        public String getDate()
        {
            return date;
        }

        public void setDate(String date)
        {
            this.date = date;
        }

        public String getFrom()
        {
            return from;
        }

        public void setFrom(String from)
        {
            this.from = from;
        }

        public Object getImg()
        {
            return img;
        }

        public void setImg(Object img)
        {
            this.img = img;
        }

        public String getType()
        {
            return type;
        }

        public void setType(String type)
        {
            this.type = type;
        }
    }
}
