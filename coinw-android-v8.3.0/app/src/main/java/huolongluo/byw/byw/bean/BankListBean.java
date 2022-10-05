package huolongluo.byw.byw.bean;


import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Created by 火龙裸 on 2018/1/5 0005.
 */
public class BankListBean implements Serializable
{
    /**
     * result : true
     * list : [{"id":1,"name":"工商银行"},{"id":2,"name":"建设银行"},{"id":3,"name":"农业银行"},{"id":4,"name":"交通银行"},{"id":5,"name":"招商银行"},{"id":6,"name":"邮政储蓄银行"},
     * {"id":7,"name":"中国银行"},{"id":8,"name":"中国民生银行"},{"id":9,"name":"中国光大银行"},{"id":10,"name":"兴业银行"},{"id":11,"name":"上海浦东发展银行"},{"id":12,"name":"中信银行"},
     * {"id":13,"name":"平安银行"},{"id":14,"name":"华夏银行"},{"id":15,"name":"其他银行"}]
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
         * id : 1
         * name : 工商银行
         */

        private int id;
        private String name;

        public int getId()
        {
            return id;
        }

        public void setId(int id)
        {
            this.id = id;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }
    }
}
