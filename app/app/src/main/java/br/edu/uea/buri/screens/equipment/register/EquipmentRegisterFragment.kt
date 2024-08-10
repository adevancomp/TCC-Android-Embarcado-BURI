package br.edu.uea.buri.screens.equipment.register

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import br.edu.uea.buri.BuildConfig
import br.edu.uea.buri.R
import br.edu.uea.buri.databinding.FragmentEquipmentRegisterBinding
import br.edu.uea.buri.screens.MainViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EquipmentRegisterFragment : Fragment() {
    private var _binding: FragmentEquipmentRegisterBinding? = null
    private val binding: FragmentEquipmentRegisterBinding get() = _binding!!
    private lateinit var btRegister: MaterialButton
    private lateinit var btOwner: MaterialButton
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
        eqpViewModel.id.observe(viewLifecycleOwner){ newId ->
            binding.tvNewIdValue.text = newId
        }
    }

    private fun setupView() {
        btRegister = binding.btNewEquipment
        btOwner = binding.btChangeOwner
    }

    private fun setupListeners() {
        btRegister.setOnClickListener {
                eqpViewModel.generateId()
                Snackbar.make(
                    binding.btNewEquipment,
                     "Necessário conectar ao wifi Buri para realizar a operação",
                    Snackbar.LENGTH_LONG)
                    .setTextColor(
                        ContextCompat.getColor(requireContext(),R.color.black)
                    )
                    .setAction("Copiar URL da API"){
                        val clipBoard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("API URL",BuildConfig.BASE_URL_API)
                        clipBoard.setPrimaryClip(clip)
                        Toast.makeText(requireContext(),"URL copiada para a área de transferência",Toast.LENGTH_SHORT).show()
                    }
                    .setBackgroundTint(
                        ContextCompat.getColor(requireContext(),R.color.blue_accent)
                    ).show()
                binding.tvNewId.isVisible = true
                binding.tvNewIdValue.isVisible = true
        }
        btOwner.setOnClickListener {
            binding.tvNewId.isVisible = false
            binding.tvNewIdValue.isVisible =false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}