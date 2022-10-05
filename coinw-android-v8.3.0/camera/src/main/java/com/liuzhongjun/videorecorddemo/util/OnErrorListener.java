package com.liuzhongjun.videorecorddemo.util;
//错误回调接口，将本module的错误信息回调给app做处理
public interface OnErrorListener {
    /**
     * 将错误使用umeng错误统计上传至umeng
     * @param msg 错误的描述信息
     * @param route 错误发生的完整路径
     */
    void report(String msg,String route);
}
