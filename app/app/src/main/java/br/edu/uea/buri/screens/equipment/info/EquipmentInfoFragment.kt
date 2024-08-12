package br.edu.uea.buri.screens.equipment.info

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import br.edu.uea.buri.data.BuriApi
import br.edu.uea.buri.databinding.FragmentEquipmentInfoBinding
import br.edu.uea.buri.domain.measurement.Measurement
import br.edu.uea.buri.screens.equipment.info.viewmodel.EqpInfoViewModel
import br.edu.uea.buri.screens.equipment.info.viewmodel.EqpInfoViewModelFactory
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@AndroidEntryPoint
class EquipmentInfoFragment : Fragment()  {
    private var _binding: FragmentEquipmentInfoBinding? = null
    private val binding: FragmentEquipmentInfoBinding get() = _binding!!
    private lateinit var airHumidityPieChart: PieChart
    private lateinit var pbLoading: ProgressBar
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
        lifecycleScope.launch {
            infoViewModel.state.collectLatest {
                    state ->
                        updateUI(state)
            }
        }
    }

    private fun updateUI(state: EqpInfoViewModel.InfoState){
        airHumidityPieChart.clear()
        state.measurement?.let {
            measurement: Measurement ->
                measurement.air?.let {
                    air ->
                        val entries = mutableListOf<PieEntry>()
                        entries.add(PieEntry(air.toFloat(), "Umidade"))
                        entries.add(PieEntry(1 - air.toFloat(), "Outros (Ar)"))

                        val dataSet = PieDataSet(entries, " ")
                        dataSet.valueTextColor = Color.parseColor("#000000")
                        dataSet.colors = listOf(Color.parseColor("#9DB2FB"), Color.parseColor("#E5E8E8"))
                        dataSet.valueTextSize = 16f

                        val data = PieData(dataSet)

                        airHumidityPieChart.description.isEnabled = false
                        airHumidityPieChart.data = data
                        airHumidityPieChart.setDrawEntryLabels(false)

                        airHumidityPieChart.invalidate()
                }
        }
    }

    private fun setupView(){
        airHumidityPieChart = binding.pcAirHumidity
        pbLoading = binding.pbLoading
    }

    private fun setupListeners(){

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}