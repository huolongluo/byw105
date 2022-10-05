package huolongluo.byw.byw.bean;


import java.io.Serializable;
import java.util.List;

/**
 * Created by 火龙裸 on 2017/12/22.
 */
public class QQBean implements Serializable
{

    /**
     * result : true
     * list : [{"fname":"coinw-胖子直冲","fqq":"1933131312","fremark":"coinw-胖子直冲"},{"fname":"coinw-企业代理","fqq":"800176882","fremark":"coinw-企业代理"},
     * {"fname":"coinw-UFO直充","fqq":"1299955580","fremark":"coinw-UFO直充"},{"fname":"coinw-火箭直冲","fqq":"2391111959","fremark":"coinw-火箭直冲"}]
     */

    private boolean result;
    private List<ListBean> list;

    public boolean isResult()
    {
        return result;
    }

    public void setResult(boolean result)
    {
        this.result = result;
    }

    public List<ListBean> getList()
    {
        return list;
    }

    public void setList(List<ListBean> list)
    {
        this.list = list;
    }

    public static class ListBean
    {
        /**
         * fname : coinw-胖子直冲
         * fqq : 1933131312
         * fremark : coinw-胖子直冲
         */

        private String fname;
        private String fqq;
        private String fremark;

        public String getFname()
        {
            return fname;
        }

        public void setFname(String fname)
        {
            this.fname = fname;
        }

        public String getFqq()
        {
            return fqq;
        }

        public void setFqq(String fqq)
        {
            this.fqq = fqq;
        }

        public String getFremark()
        {
            return fremark;
        }

        public void setFremark(String fremark)
        {
            this.fremark = fremark;
        }
    }
}
