package br.edu.uea.buri.screens.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.uea.buri.databinding.EquipmentItemBinding
import br.edu.uea.buri.domain.equipment.Equipment

class EquipmentAdapter(private val equipments: List<Equipment>,private val onClick: (Equipment)->Unit) : RecyclerView.Adapter<EquipmentAdapter.ViewHolder>() {

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
            onClick(equipment)
        }
    }

    inner class ViewHolder(binding: EquipmentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
            var content = binding.clCardContent
            var equipmentName = binding.tvEquipmentName
            var equipmentId = binding.tvEquipmentId
    }
}