package huolongluo.byw.byw.bean;


import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Created by 火龙裸 on 2017/12/15 0015.
 */
public class ChongZhiBean implements Serializable
{

    /**
     * code : 0
     * list : [{"id":153040,"fremark":"27551","fcreateTime":"2017-11-24 00:18:27","fremittanceType":"商户充值","famount":1000.43,"fstatus":"等待银行到账"},
     * {"id":153031,"fremark":"02879","fcreateTime":"2017-11-24 00:14:02","fremittanceType":"商户充值","famount":5000.43,"fstatus":"等待银行到账"},{"id":152257,
     * "fremark":null,"fcreateTime":"2017-11-23 21:20:52","fremittanceType":"商户充值","famount":1000.26,"fstatus":"等待银行到账"},{"id":152107,"fremark":null,
     * "fcreateTime":"2017-11-23 20:58:25","fremittanceType":"商户充值","famount":1000.42,"fstatus":"等待银行到账"},{"id":151966,"fremark":null,
     * "fcreateTime":"2017-11-23 20:29:56","fremittanceType":"商户充值","famount":1000.38,"fstatus":"等待银行到账"},{"id":151965,"fremark":null,
     * "fcreateTime":"2017-11-23 20:29:50","fremittanceType":"商户充值","famount":1000.38,"fstatus":"等待银行到账"},{"id":151959,"fremark":null,
     * "fcreateTime":"2017-11-23 20:29:10","fremittanceType":"商户充值","famount":1000.38,"fstatus":"等待银行到账"},{"id":144507,"fremark":null,
     * "fcreateTime":"2017-11-21 00:38:59","fremittanceType":"商户充值","famount":1000.42,"fstatus":"等待银行到账"},{"id":144503,"fremark":null,
     * "fcreateTime":"2017-11-21 00:37:35","fremittanceType":"商户充值","famount":1000.42,"fstatus":"等待银行到账"},{"id":144484,"fremark":null,
     * "fcreateTime":"2017-11-21 00:23:31","fremittanceType":"商户充值","famount":1000.43,"fstatus":"等待银行到账"}]
     */

    private int code;
    private List<ListBean> list;

    public int getCode()
    {
        return code;
    }

    public void setCode(int code)
    {
        this.code = code;
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
         * id : 153040
         * fremark : 27551
         * fcreateTime : 2017-11-24 00:18:27
         * fremittanceType : 商户充值
         * famount : 1000.43
         * fstatus : 等待银行到账
         */

        private int id;
        private String fremark;
        private String fcreateTime;
        private String fremittanceType;
        private double famount;
        private String fstatus;

        public int getId()
        {
            return id;
        }

        public void setId(int id)
        {
            this.id = id;
        }

        public String getFremark()
        {
            return fremark;
        }

        public void setFremark(String fremark)
        {
            this.fremark = fremark;
        }

        public String getFcreateTime()
        {
            return fcreateTime;
        }

        public void setFcreateTime(String fcreateTime)
        {
            this.fcreateTime = fcreateTime;
        }

        public String getFremittanceType()
        {
            return fremittanceType;
        }

        public void setFremittanceType(String fremittanceType)
        {
            this.fremittanceType = fremittanceType;
        }

        public double getFamount()
        {
            return famount;
        }

        public void setFamount(double famount)
        {
            this.famount = famount;
        }

        public String getFstatus()
        {
            return fstatus;
        }

        public void setFstatus(String fstatus)
        {
            this.fstatus = fstatus;
        }
    }
}
