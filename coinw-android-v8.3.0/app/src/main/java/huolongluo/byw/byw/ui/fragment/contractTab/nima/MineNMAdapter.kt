package huolongluo.byw.byw.ui.fragment.contractTab.nima

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import huolongluo.byw.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.nm_item_view2.view.*

class MineNMAdapter(var context: Context, var data: List<NMHistoryMinEntity.DataBean.ListDataBean>) : Adapter<MineNMAdapter.NMHolder>() {
    var STYLE = 2

    class NMHolder(itemView: View) : ViewHolder(itemView), LayoutContainer {
        override val containerView: View = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NMHolder {
        val inflate = LayoutInflater.from(parent.context).inflate(
                if (STYLE == 1) R.layout.nm_item_view2 else R.layout.nm_item_view2
                , parent, false)
        return MineNMAdapter.NMHolder(inflate)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: MineNMAdapter.NMHolder, position: Int) {
        holder.containerView.tv_amount.text = data[position].mudQuota.toString() + " " + data[position].coinName
        holder.containerView.tv_type.text = if (data[position].grantType == 2) context.getString(R.string.str_drop) else context.getString(R.string.promotion)
        holder.containerView.tv_time.text = data[position].grantDt_str
    }
}