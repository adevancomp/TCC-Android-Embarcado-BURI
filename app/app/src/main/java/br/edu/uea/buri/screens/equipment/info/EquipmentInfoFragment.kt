package br.edu.uea.buri.screens.equipment.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import br.edu.uea.buri.data.BuriApi
import br.edu.uea.buri.databinding.FragmentEquipmentInfoBinding
import br.edu.uea.buri.screens.equipment.info.viewmodel.EqpInfoViewModel
import br.edu.uea.buri.screens.equipment.info.viewmodel.EqpInfoViewModelFactory
import com.github.mikephil.charting.charts.PieChart
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EquipmentInfoFragment : Fragment()  {
    private var _binding: FragmentEquipmentInfoBinding? = null
    private val binding: FragmentEquipmentInfoBinding get() = _binding!!
    private lateinit var airHumidityPieChart: PieChart
    private val args by navArgs<EquipmentInfoFragmentArgs>()
    @Inject lateinit var buriApi: BuriApi
    private val infoViewModel by viewModels<EqpInfoViewModel> {
        EqpInfoViewModelFactory(buriApi,args.equipment.equipmentId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEquipmentInfoBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupListeners()
    }

    private fun setupView(){
        airHumidityPieChart = binding.pcAirHumidity
    }

    private fun setupListeners(){

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}