import android.graphics.Color
import android.widget.CheckBox
import android.widget.TextView
import com.dbtechprojects.pibuddy.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item


class DeploymentAdapter(val deploymentResult: DeploymentResult): Item<GroupieViewHolder>(){

    private val checkedIPs = mutableListOf<String>()

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        val ipTextView = viewHolder.itemView.findViewById<TextView>(R.id.deployment_row_ip)
        val outputTextView = viewHolder.itemView.findViewById<TextView>(R.id.deployment_item_output_text)
        val checkBox = viewHolder.itemView.findViewById<CheckBox>(R.id.checkBox)

        ipTextView.text = deploymentResult.ip

        deploymentResult.output?.let { output ->
            outputTextView.text = if (output.isEmpty()) "output empty" else output
        }

        if (deploymentResult.connected){
            outputTextView.text = "Available, waiting deployment..."
            outputTextView.setTextColor(Color.GREEN)
        } else {
            outputTextView.text = "Not Available"
            outputTextView.setTextColor(Color.RED)
        }

        checkBox.setOnCheckedChangeListener { compoundButton, b ->
            if (checkBox.isChecked) checkedIPs.add(deploymentResult.ip) else checkedIPs.remove(deploymentResult.ip)
        }


    }
    fun getCheckedDevices() = checkedIPs as List<String>


    override fun getLayout(): Int {
        return R.layout.deployment_device_row
    }
}

data class DeploymentResult(
    val output: String? = null,
    val ip: String,
    val connected: Boolean = false
)