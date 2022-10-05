package huolongluo.byw.model

import com.sunfusheng.marqueeview.IMarqueeItem

class ScrollNoticeItemBean(val content: String,
                           val datetime: String,
                           val goOut: Boolean,
                           val id: Int,
                           val outUrl: String,
                           val title: String) : IMarqueeItem {
    override fun marqueeMessage(): CharSequence {
        return title
    }

}
