import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dbtechprojects.pibuddy.databinding.DeploymentDeviceRowBinding

data class DeploymentResult(
    val output: String? = null,
    val ip: String,
    val connected: Boolean = false
)
fun MutableList<DeploymentResult>.findByIp(ipAddress: String): Int? {
    this.forEachIndexed { index, deploymentResult ->  if (deploymentResult.ip == ipAddress) return index   }
    return  null
}

class DeploymentAdapter(private var onClickListener: OnClickListener) : RecyclerView.Adapter<DeploymentAdapter.DeploymentViewHolder>() {

    private var devices = mutableListOf<DeploymentResult>()


    class DeploymentViewHolder(private val binding: DeploymentDeviceRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DeploymentResult, onClickListener: OnClickListener?) {
            val ipTextView = binding.deploymentRowIp
            val outputTextView = binding.deploymentItemOutputText
            val checkBox = binding.checkBox

            ipTextView.text = item.ip
            setupOutput(item, outputTextView)

            checkBox.setOnCheckedChangeListener { compoundButton, b ->
                if (checkBox.isChecked) checkedIPs.add(item.ip) else checkedIPs.remove(item.ip)
                Log.d("checked ips", "list: $checkedIPs")
            }
            binding.root.setOnClickListener {
                onClickListener?.onClick(item)
            }

        }

        private fun setupOutput(item: DeploymentResult, outputTextView: TextView) {

            item.output?.let { output ->
                outputTextView.text = if (output.isEmpty()) "output empty" else  if (output.length < 10) "output: $output" else output
                outputTextView.setTextColor(Color.BLACK)
                return
            }
            if (item.connected){
                outputTextView.text = "Available, waiting deployment..."
                outputTextView.setTextColor(Color.GREEN)
            } else {
                outputTextView.text = "Not Available"
                outputTextView.setTextColor(Color.RED)
            }



        }

        companion object {
            fun inflateLayout(parent: ViewGroup): DeploymentViewHolder {
                parent.apply {
                    val inflater = LayoutInflater.from(parent.context)
                    val binding = DeploymentDeviceRowBinding.inflate(inflater, parent, false)
                    return DeploymentAdapter.DeploymentViewHolder(binding)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeploymentViewHolder {
        return DeploymentViewHolder.inflateLayout(parent)
    }

    override fun onBindViewHolder(holder: DeploymentViewHolder, position: Int) {
        holder.bind(devices[position], onClickListener)
    }

    override fun getItemCount(): Int = devices.size

    interface OnClickListener {
        fun onClick(item: DeploymentResult)
    }

    fun setList(list: List<DeploymentResult>) {
        devices = list as MutableList<DeploymentResult>
        notifyDataSetChanged()
    }

    fun getDevices() = devices

    fun getCheckedIps() = checkedIPs

    fun updateDeviceAtIndex(deploymentResult: DeploymentResult, index: Int){
        devices[index] = deploymentResult
        notifyDataSetChanged()
    }

    companion object {
        val checkedIPs = mutableListOf<String>()
    }

}