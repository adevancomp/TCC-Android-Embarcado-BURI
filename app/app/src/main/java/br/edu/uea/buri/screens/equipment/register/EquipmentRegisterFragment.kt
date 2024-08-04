package br.edu.uea.buri.screens.equipment.register

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
    }

    private fun setupListeners() {
        btRegister.setOnClickListener {
            if(mainViewModel.buriWifiConnected.value == true){
                Snackbar.make(
                    binding.btNewEquipment,
                    "Buri Conectado, partindo pro site",
                    Snackbar.LENGTH_SHORT)
                    .setTextColor(
                        ContextCompat.getColor(requireContext(),R.color.black)
                    )
                    .setBackgroundTint(
                        ContextCompat.getColor(requireContext(),R.color.blue_accent)
                    ).show()
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("http://192.168.4.1")
                    )
                )
            } else {
                Snackbar.make(
                    binding.btNewEquipment,
                     "Necessário conectar ao wifi Buri para realizar a operação",
                    Snackbar.LENGTH_SHORT)
                    .setTextColor(
                        ContextCompat.getColor(requireContext(),R.color.black)
                    )
                    .setBackgroundTint(
                        ContextCompat.getColor(requireContext(),R.color.blue_accent)
                    ).show()
            }
        }
        btOwner.setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}