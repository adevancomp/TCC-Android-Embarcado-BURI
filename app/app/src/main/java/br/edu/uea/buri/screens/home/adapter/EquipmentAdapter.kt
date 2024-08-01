package br.edu.uea.buri.screens.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.uea.buri.databinding.EquipmentItemBinding
import br.edu.uea.buri.domain.equipment.Equipment

class EquipmentAdapter() : RecyclerView.Adapter<EquipmentAdapter.ViewHolder>() {

    private var equipments: List<Equipment> = emptyList()
    var equipmentItemListener: (Equipment)-> Unit = {}

    fun submitList(newData: List<Equipment>){
        equipments = newData
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = EquipmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = equipments.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.equipmentName.text = equipments[position].name
        holder.equipmentId.text = equipments[position].equipmentId
        holder.content.setOnClickListener {
            val equipment = equipments[position]
            equipmentItemListener(equipment)
        }
    }

    inner class ViewHolder(binding: EquipmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
            var content = binding.cvEquipmentItem
            var equipmentName = binding.tvEquipmentName
            var equipmentId = binding.tvEquipmentId
    }
}