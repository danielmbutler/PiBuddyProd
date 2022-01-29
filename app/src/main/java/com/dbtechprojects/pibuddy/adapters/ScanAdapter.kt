import android.widget.TextView
import com.dbtechprojects.pibuddy.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item


class ScanAdapter(val IP: String): Item<GroupieViewHolder>(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val ipTextView = viewHolder.itemView.findViewById<TextView>(R.id.deployment_row_ip)
        ipTextView.text = IP

    }

    override fun getLayout(): Int {
        return R.layout.available_device_row
    }
}