package huolongluo.byw.byw.ui.fragment.contractTab.nima

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import huolongluo.byw.R
import huolongluo.byw.byw.ui.fragment.contractTab.nima.NMAdapter.NMHolder
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.nm_item_view.view.*

class NMAdapter(var context: Context, var data: List<NMHistoryEntity.DataBean>) : Adapter<NMHolder>() {
    var STYLE = 2

    class NMHolder(itemView: View) : ViewHolder(itemView), LayoutContainer {
        override val containerView: View = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NMHolder {
        val inflate = LayoutInflater.from(parent.context).inflate(
                if (STYLE == 1) R.layout.nm_item_view else R.layout.nm_item_view1
                , parent, false)
        return NMHolder(inflate)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: NMHolder, position: Int) {
        holder.containerView.title_des.text = String.format(context.getString(R.string.can_get_nm), data[position].transferQuota)
        holder.containerView.tv_money.text = data[position].mudQuota
        holder.containerView.tv_money_des.text = "USDT ${context.getString(R.string.nm)}"
        holder.containerView.time.text = data[position].grantDt
    }
}