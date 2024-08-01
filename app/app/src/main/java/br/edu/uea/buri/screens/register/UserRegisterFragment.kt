package br.edu.uea.buri.screens.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import br.edu.uea.buri.R
import br.edu.uea.buri.databinding.FragmentUserRegisterBinding
import br.edu.uea.buri.screens.login.LoginViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserRegisterFragment : Fragment() {
    private var _binding: FragmentUserRegisterBinding? = null
    private val binding: FragmentUserRegisterBinding  get() = _binding!!
    private val registerViewModel: UserRegisterViewModel by viewModels()
    private lateinit var edName: TextInputEditText
    private lateinit var edEmail: TextInputEditText
    private lateinit var edPassword: TextInputEditText
    private lateinit var switchIsAdmin: SwitchCompat
    private lateinit var btnRegister: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserRegisterBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListeners()
        registerViewModel.state.observe(viewLifecycleOwner){ state ->
            binding.pbCls.isVisible = state.isProgressVisible
            if(state.isErrorMessageVisible){
                //Toast.makeText(requireContext(),state.errorMessage ?: "Erro", Toast.LENGTH_SHORT).show()
                Snackbar.make(
                    binding.btRegister,
                    state.errorMessage ?: "Erro no Cadastro",
                    Snackbar.LENGTH_SHORT)
                    .setTextColor(
                        ContextCompat.getColor(requireContext(),R.color.black)
                    )
                    .setBackgroundTint(
                        ContextCompat.getColor(requireContext(),R.color.blue_accent)
                    ).show()
            }
            if(state.isUserSaved){
                //Toast.makeText(requireContext(),"Cadastrado com sucesso", Toast.LENGTH_SHORT).show()
                Snackbar.make(
                    binding.btRegister,
                    "Cadastro com Sucesso",
                    Snackbar.LENGTH_SHORT)
                    .setTextColor(
                        ContextCompat.getColor(requireContext(),R.color.black)
                    )
                    .setBackgroundTint(
                        ContextCompat.getColor(requireContext(),R.color.blue_accent)
                    ).show()
                findNavController().navigate(R.id.loginFragment)
            }
        }
    }

    private fun setupView(){
        edName = binding.edtName
        edEmail = binding.edtEmail
        edPassword = binding.edtPassword
        switchIsAdmin = binding.swtIsAdmin
        btnRegister = binding.btRegister
    }

    private fun setupListeners(){
        btnRegister.setOnClickListener {
            if(verifyInputs()){
                registerViewModel.register(
                    edName.text.toString(),
                    edEmail.text.toString(),
                    edPassword.text.toString(),
                    if (switchIsAdmin.isChecked) "ROLE_ADM" else "ROLE_CUSTOMER"
                )
            } else {
                /*Toast.makeText(requireContext(),
                    ,
                    Toast.LENGTH_SHORT
                ).show()*/
                Snackbar.make(
                    binding.btRegister,
                    "Campos vazios ou nulos. Preencha corretamente",
                    Snackbar.LENGTH_SHORT)
                    .setTextColor(
                        ContextCompat.getColor(requireContext(),R.color.black)
                    )
                    .setBackgroundTint(
                        ContextCompat.getColor(requireContext(),R.color.blue_accent)
                    ).show()
            }
        }
    }

    private fun verifyInputs() : Boolean {
        return !(edName.text.isNullOrBlank() ||
                edEmail.text.isNullOrBlank() ||
                edPassword.text.isNullOrBlank())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}