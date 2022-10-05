package huolongluo.byw.reform.mine.bean;

import java.util.List;

/**
 * Created by Administrator on 2019/3/11 0011.
 */

public class fintrolinfoBeam {

    private int code;
    private int currentPage ;
       private List<FintrolInfo> fintrolinfo;
       private int totalPage;
       private String value;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<FintrolInfo> getFintrolinfo() {
        return fintrolinfo;
    }

    public void setFintrolinfo(List<FintrolInfo> fintrolinfo) {
        this.fintrolinfo = fintrolinfo;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


    public static class  FintrolInfo{
         private String   fcreatetime;
         private int fid;
         private String ftitle;

           public String getFcreatetime() {
               return fcreatetime;
           }

           public void setFcreatetime(String fcreatetime) {
               this.fcreatetime = fcreatetime;
           }

           public int getFid() {
               return fid;
           }

           public void setFid(int fid) {
               this.fid = fid;
           }

           public String getFtitle() {
               return ftitle;
           }

           public void setFtitle(String ftitle) {
               this.ftitle = ftitle;
           }
       }
}
