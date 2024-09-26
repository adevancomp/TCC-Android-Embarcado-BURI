package br.edu.uea.buri.screens.equipment.register

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import br.edu.uea.buri.BuildConfig
import br.edu.uea.buri.R
import br.edu.uea.buri.databinding.FragmentEquipmentRegisterBinding
import br.edu.uea.buri.domain.equipment.Equipment
import br.edu.uea.buri.screens.MainViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class EquipmentRegisterFragment : Fragment() {
    private var _binding: FragmentEquipmentRegisterBinding? = null
    private val binding: FragmentEquipmentRegisterBinding get() = _binding!!
    private lateinit var btRegister: MaterialButton
    private lateinit var btOwner: MaterialButton
    private lateinit var btSaveEquipment: MaterialButton
    private lateinit var btSaveNewOwner: MaterialButton
    private lateinit var edtEquipmentName: TextInputEditText
    private lateinit var tilEquipmentName: TextInputLayout
    private lateinit var edtEquipmentId : TextInputEditText
    private lateinit var tilEquipmentId: TextInputLayout
    private lateinit var txtNewId: TextView
    private lateinit var txtNewIdValue: TextView
    @Inject lateinit var shared: SharedPreferences
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
        eqpViewModel.state.observe(viewLifecycleOwner){
            state ->
                txtNewIdValue.text = state.id
                txtNewId.isVisible = state.elementsRegisterNewEquipmentVisible
                txtNewIdValue.isVisible = state.elementsRegisterNewEquipmentVisible
                tilEquipmentName.isVisible = state.elementsRegisterNewEquipmentVisible
                btSaveEquipment.isVisible = state.elementsRegisterNewEquipmentVisible

                tilEquipmentId.isVisible = state.elementsRegisterChangeOwnerVisible
                btSaveNewOwner.isVisible = state.elementsRegisterChangeOwnerVisible

                state.equipment?.let {
                    Toast.makeText(requireContext(),"Conseguiu fazer a operação",Toast.LENGTH_SHORT).show()
                }
                state.errorMessage?.let {
                    Toast.makeText(requireContext(),it,Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupView() {
        txtNewId = binding.tvNewId
        txtNewIdValue = binding.tvNewIdValue
        btRegister = binding.btNewEquipment
        btOwner = binding.btChangeOwner
        btSaveEquipment = binding.btSaveEquipment
        btSaveNewOwner = binding.btSaveNewOwner
        edtEquipmentName = binding.edtEquipmentName
        edtEquipmentId = binding.edtEquipmentId

        tilEquipmentName = binding.tilEquipmentName
        tilEquipmentId = binding.tilEquipmentId
    }

    private fun setupListeners() {
        btRegister.setOnClickListener {
            eqpViewModel.generateId()
            Snackbar.make(
                binding.btNewEquipment,
                "Conecte-se ao Buri-Wifi, coloque o ID e a URL DA API",
                Snackbar.LENGTH_LONG
            ).setTextColor(
                ContextCompat.getColor(requireContext(), R.color.black)
            ).setAction("Copiar URL da API"){
                    val clipBoard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("API URL", BuildConfig.BASE_URL_API)
                    clipBoard.setPrimaryClip(clip)
                    Toast.makeText(requireContext(),"URL copiada para a área de transferência",Toast.LENGTH_SHORT).show()
                }
                .setBackgroundTint(
                    ContextCompat.getColor(requireContext(),R.color.color_red)
                ).show()
        }
        btOwner.setOnClickListener {
            eqpViewModel.chanceOwner()
        }
        btSaveEquipment.setOnClickListener {
            eqpViewModel.saveEquipment(
                Equipment(
                    id = txtNewIdValue.text.toString(),
                    name = edtEquipmentName.text?.toString() ?: "",
                    ownerId = UUID.fromString(shared.getString("id", "") ?: "")
                )
            )
            val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
            val view = activity?.currentFocus
            view?.let {
                imm?.hideSoftInputFromWindow(it.windowToken, 0)
            }
        }
        btSaveNewOwner.setOnClickListener {
            var equipment: Equipment? = null
            lifecycleScope.launch {
                equipment = eqpViewModel.searchEquipmentById(edtEquipmentId.text?.toString() ?: "")
                if(equipment!=null){
                    eqpViewModel.saveEquipment(
                        Equipment(
                            id = equipment!!.id,
                            name = equipment!!.name,
                            ownerId = UUID.fromString(shared.getString("id", "") ?: "")
                        )
                    )
                } else {
                    Toast.makeText(requireContext(),"Não foi encontrado",Toast.LENGTH_SHORT).show()
                }
            }
            val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
            val view = activity?.currentFocus
            view?.let {
                imm?.hideSoftInputFromWindow(it.windowToken, 0)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}