package br.edu.uea.buri.screens.login

import android.app.Activity
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
import androidx.navigation.fragment.findNavController
import br.edu.uea.buri.R
import br.edu.uea.buri.data.BuriApi
import br.edu.uea.buri.databinding.FragmentLoginBinding
import br.edu.uea.buri.domain.user.UserLogin
import br.edu.uea.buri.screens.MainViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!
    private lateinit var edtEmail: TextInputEditText
    private lateinit var edtPassword: TextInputEditText
    private lateinit var textCreateAccount: TextView
    private lateinit var btnLogin: MaterialButton
    private val loginViewModel: LoginViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListeners()
        loginViewModel.state.observe(viewLifecycleOwner){ state ->
            binding.pbCls.isVisible = state.isProgressVisible
            if(state.isErrorMessageVisible){
                Snackbar.make(
                    binding.btLogin,
                    state.errorMessage ?: "Erro no Login",
                    Snackbar.LENGTH_SHORT)
                    .setTextColor(
                        ContextCompat.getColor(requireContext(),R.color.black)
                    )
                    .setBackgroundTint(
                        ContextCompat.getColor(requireContext(),R.color.color_red)
                    ).show()
            }
            if(state.isAuthenticated) {
                mainViewModel.authenticate()
                findNavController().navigate(R.id.homeFragment)
            }
        }
    }

    private fun setupView() {
        edtEmail = binding.edtLogin
        edtPassword = binding.edtPassword
        textCreateAccount = binding.tvPasswordText2
        btnLogin = binding.btLogin
    }

    private fun setupListeners() {
        textCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.userRegisterFragment)
        }
        btnLogin.setOnClickListener {
            loginViewModel.authenticate(
                edtEmail.text.toString(),
                edtPassword.text.toString()
            )
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