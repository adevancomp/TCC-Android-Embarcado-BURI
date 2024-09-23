package br.edu.uea.buri.screens.home

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.uea.buri.R
import br.edu.uea.buri.data.database.dao.UserDao
import br.edu.uea.buri.data.database.entity.UserWithEquipments
import br.edu.uea.buri.databinding.FragmentHomeBinding
import br.edu.uea.buri.screens.MainViewModel
import br.edu.uea.buri.screens.home.adapter.EquipmentAdapter
import br.edu.uea.buri.screens.home.repository.BluetoothEsp32Repository
import br.edu.uea.buri.screens.home.viewmodel.HomeState
import br.edu.uea.buri.screens.home.viewmodel.HomeViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding get() = _binding!!
    private val mainViewModel: MainViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var adapter: EquipmentAdapter
    private lateinit var btCreateEquipment: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListeners()
        mainViewModel.authenticated.observe(viewLifecycleOwner){ authenticated ->
            if(!authenticated){
                val directions = HomeFragmentDirections.actionGlobalLoginFragment()
                findNavController().navigate(directions)
            }
        }

        homeViewModel.fetchData()
        homeViewModel.homeState.observe(viewLifecycleOwner){
            state ->
                binding.pbCls.isVisible = state.isProgressVisible
                if(state.isErrorMessageVisible){
                    binding.tvEmptyList.text = state.errorMessage
                    binding.tvEmptyList.isVisible = true
                } else{
                    binding.tvEmptyList.isVisible = false
                }
                if(state.isMessageEmptyListVisible) {
                    binding.tvEmptyList.isVisible = true
                } else {
                    binding.tvEmptyList.isVisible = false
                }
            configList(state)
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            requireActivity().finish()
        }
    }

    private fun setupListeners() {
        btCreateEquipment.setOnClickListener {
            findNavController().navigate(R.id.equipmentRegisterFragment)
        }
    }

    private fun setupView() {
        btCreateEquipment = binding.fabAddEquipment
    }

    private fun configList(state: HomeState) {
        adapter = EquipmentAdapter(state.listEquipments) { equipment ->
            val direction = HomeFragmentDirections.goToEquipmentInfoFragment(equipment)
            findNavController().navigate(direction)
        }
        val recyclerView = binding.rvEquipmentList
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}