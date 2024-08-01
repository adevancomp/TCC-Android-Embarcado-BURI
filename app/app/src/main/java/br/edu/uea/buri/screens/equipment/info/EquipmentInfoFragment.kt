package br.edu.uea.buri.screens.equipment.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import br.edu.uea.buri.databinding.FragmentEquipmentInfoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EquipmentInfoFragment : Fragment()  {
    private var _binding: FragmentEquipmentInfoBinding? = null
    private val binding: FragmentEquipmentInfoBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEquipmentInfoBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}