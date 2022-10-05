package com.legend.modular_contract_sdk.repository.model;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/10/20 16:20
 *   desc    : 朋友圈图片
 * </pre>
 */
public class BlogImage {

    private int id;

    private int moments_id;

    private String img_name;

    /**
     * 获取朋友圈图片路径
     *
     * @return
     */
    public String getImagePath() {
        return img_name == null ? "" : img_name;
    }

}
