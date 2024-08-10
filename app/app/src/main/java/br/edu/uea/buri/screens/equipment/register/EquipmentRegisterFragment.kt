package br.edu.uea.buri.screens.equipment.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import br.edu.uea.buri.databinding.FragmentEquipmentRegisterBinding
import br.edu.uea.buri.screens.MainViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EquipmentRegisterFragment : Fragment() {
    private var _binding: FragmentEquipmentRegisterBinding? = null
    private val binding: FragmentEquipmentRegisterBinding get() = _binding!!
    private lateinit var btRegister: MaterialButton
    private lateinit var btOwner: MaterialButton
    private lateinit var btSaveEquipment: MaterialButton
    private lateinit var btSaveNewOwner: MaterialButton
    private lateinit var edtEquipmentName: TextInputEditText
    private lateinit var edtEquipmentId : TextInputEditText
    private val mainViewModel : MainViewModel by activityViewModels<MainViewModel>()
    private val eqpViewModel: EqpRegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEquipmentRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListeners()
    }

    private fun setupView() {
        btRegister = binding.btNewEquipment
        btOwner = binding.btChangeOwner
        btSaveEquipment = binding.btSaveEquipment
        btSaveNewOwner = binding.btSaveNewOwner
        edtEquipmentName = binding.edtEquipmentName
        edtEquipmentId = binding.edtEquipmentId
    }

    private fun setupListeners() {
        btRegister.setOnClickListener {

        }
        btOwner.setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}