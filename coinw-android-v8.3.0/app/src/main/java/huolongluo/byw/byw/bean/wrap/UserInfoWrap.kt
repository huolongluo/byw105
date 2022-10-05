package huolongluo.byw.byw.bean.wrap

import android.content.Context
import android.graphics.drawable.Drawable
import huolongluo.byw.R
import huolongluo.byw.byw.bean.UserInfoBean
import huolongluo.byw.log.Logger

class UserInfoWrap(val userInfoBean: UserInfoBean?) {
    fun getPhoneNum(context: Context, isEncrypt: Boolean): String {
        if (userInfoBean != null) {
            if(userInfoBean.loginName==null) return context.resources.getString(R.string.mine_desc1)
            if (!isEncrypt) {
                return userInfoBean.loginName
            }
            else {
                try {
                    val atIndex = userInfoBean.loginName.indexOf("@")
                    return if (atIndex >= 0){
                        var email = userInfoBean.loginName.substring(0, atIndex)
                        val emailSuffix = userInfoBean.loginName.substring(atIndex)
                        Logger.getInstance().error("email = $email  emailSuffix=$emailSuffix")
                        "${email.first()}****${email.last()} $emailSuffix"
                    } else {
                        val sub = userInfoBean.loginName.substring(3, 7)
                        userInfoBean.loginName.replace(sub, "****")
                    }
                } catch (e: Exception){
                    return userInfoBean.loginName?:context.resources.getString(R.string.mine_desc1)
                }

            }
        } else {
            return context.resources.getString(R.string.mine_desc1)
        }
    }

    fun getUid(context: Context): String {
        if (userInfoBean != null) {
            return "UID: ${userInfoBean.fid}"
        } else {
            return context.resources.getString(R.string.click_to_login)
        }
    }

    fun getVipLevel(context: Context): Drawable? {
        if (userInfoBean != null) {
            val res = context.resources
            return when(userInfoBean.vip){
                1 -> {
                    res.getDrawable(R.drawable.ic_vip_1)
                }
                2 -> {
                    res.getDrawable(R.drawable.ic_vip_2)
                }
                3 -> {
                    res.getDrawable(R.drawable.ic_vip_3)
                }
                4 -> {
                    res.getDrawable(R.drawable.ic_vip_4)
                }
                5 -> {
                    res.getDrawable(R.drawable.ic_vip_5)
                }
                6 -> {
                    res.getDrawable(R.drawable.ic_vip_6)
                }
                else -> {
                    null
                }
            }
        } else return null
    }
}