package huolongluo.byw.model;

import huolongluo.byw.byw.bean.UserInfoBean;

public class UserInfoResult {

    public String message;
    public String code;
    public SubUserInfoResult<UserInfoBean> data;

    public class SubUserInfoResult<UserInfoBean> {

        public String value;
        public int code;
        public UserInfoBean userInfo;
    }
}
