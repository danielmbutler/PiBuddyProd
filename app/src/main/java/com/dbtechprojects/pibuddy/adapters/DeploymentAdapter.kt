import android.graphics.Color
import android.widget.TextView
import com.dbtechprojects.pibuddy.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item


class DeploymentAdapter(val deploymentResult: DeploymentResult): Item<GroupieViewHolder>(){

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val ipTextView = viewHolder.itemView.findViewById<TextView>(R.id.deployment_row_ip)
        val outputTextView = viewHolder.itemView.findViewById<TextView>(R.id.deployment_item_output_text)
        val availabilityTextView = viewHolder.itemView.findViewById<TextView>(R.id.deployment_Available)

        ipTextView.text = deploymentResult.ip

        deploymentResult.output?.let { output ->
            outputTextView.text = if (output.isEmpty()) "output empty" else output
        }

        if (deploymentResult.connected){
            availabilityTextView.text = "Available"
            availabilityTextView.setTextColor(Color.GREEN)
        } else {
            availabilityTextView.text = "Not Available"
            availabilityTextView.setTextColor(Color.RED)
        }


    }

    override fun getLayout(): Int {
        return R.layout.deployment_device_row
    }
}

data class DeploymentResult(
    val output: String? = null,
    val ip: String,
    val connected: Boolean = false
)