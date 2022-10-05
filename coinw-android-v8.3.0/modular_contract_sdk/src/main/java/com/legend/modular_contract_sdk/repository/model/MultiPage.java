package com.legend.modular_contract_sdk.repository.model;

/**
 * <pre>
 *   @author  : tocci.feng
 *   e-mail  : fengfei0205@gmail.com
 *   time    : 2020/10/20 16:12
 *   desc    : 多页数据格式
 * </pre>
 */
public class MultiPage {

    /**
     * 当前页数
     */
    private int page;

    /**
     * 总页数
     */
    private int totalpage;

    public int getCurrentPage() {
        return page;
    }

    public int getTotalPage() {
        return totalpage;
    }

}
