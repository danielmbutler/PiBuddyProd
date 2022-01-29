package com.dbtechprojects.pibuddy.ui.activites

import DeploymentAdapter
import DeploymentResult
import ScanAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import com.dbtechprojects.pibuddy.databinding.ActivityDeploymentBinding
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item

class Deployment_Activity : AppCompatActivity() {

    private var _binding : ActivityDeploymentBinding? = null
    private val binding : ActivityDeploymentBinding get() = _binding!!
    private val adapter = GroupAdapter<GroupieViewHolder>()
    private val devices: MutableList<DeploymentResult> = mutableListOf(
        DeploymentResult("output of command", "192.168.0.1", false),
        DeploymentResult("output of command", "192.168.0.1", true),
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDeploymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerviewDeployment.adapter = adapter
        setupRV()
        refreshRecyclerViewMessages()
    }

    private fun setupRV(){

        adapter.setOnItemClickListener{ item: Item<GroupieViewHolder>, view: View ->

            val IP = item as DeploymentAdapter


        }
        binding.recyclerviewDeployment.adapter = adapter

        // faint line underneath each row
        binding.recyclerviewDeployment.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        devices.forEach {
            adapter.add(DeploymentAdapter(it))
        }
    }
}