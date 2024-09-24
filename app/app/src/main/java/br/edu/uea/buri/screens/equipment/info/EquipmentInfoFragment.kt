package br.edu.uea.buri.screens.equipment.info

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
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
import br.edu.uea.buri.screens.home.repository.BluetoothEsp32Repository
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class EquipmentInfoFragment : Fragment()  {
    private var _binding: FragmentEquipmentInfoBinding? = null
    private val binding: FragmentEquipmentInfoBinding get() = _binding!!
    private lateinit var airHumidityPieChart: PieChart
    private lateinit var pbLoading: ProgressBar
    private lateinit var tvCOEquipmentName: TextView
    private lateinit var tvCOValue: TextView
    private lateinit var tvTemperatureEquipmentName: TextView
    private lateinit var tvTemperatureValue: TextView
    private lateinit var tvCollectionDateValue: TextView
    private lateinit var tvErrorMessage: TextView
    private lateinit var tvMonoxideTitle: TextView
    private lateinit var cardCO: ConstraintLayout
    private lateinit var cardTemperature: ConstraintLayout
    private lateinit var switchIsOnline: SwitchCompat
    private val args by navArgs<EquipmentInfoFragmentArgs>()
    @Inject lateinit var buriApi: BuriApi
    @Inject lateinit var bluetoothEsp32Repository: BluetoothEsp32Repository
    private val infoViewModel by viewModels<EqpInfoViewModel> {
        EqpInfoViewModelFactory(buriApi,args.equipment.id, bluetoothEsp32Repository)
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
        if(state.isErrorMessageVisible){
            tvErrorMessage.visibility = View.VISIBLE
            tvErrorMessage.text = state.errorMessage

            airHumidityPieChart.visibility = View.GONE
            cardCO.visibility = View.GONE

            cardTemperature.visibility = View.GONE
            tvMonoxideTitle.visibility = View.GONE
        } else {
            tvErrorMessage.visibility = View.GONE
            tvErrorMessage.text = ""

            airHumidityPieChart.visibility = View.VISIBLE
            cardCO.visibility = View.VISIBLE

            cardCO.visibility = View.VISIBLE
            cardTemperature.visibility = View.VISIBLE
            tvMonoxideTitle.visibility = View.VISIBLE
        }
        airHumidityPieChart.clear()
        state.measurement?.let {
            measurement: Measurement ->
                measurement.air?.let {
                    air ->
                        val entries = mutableListOf<PieEntry>()
                        entries.add(PieEntry(air.setScale(2,RoundingMode.HALF_UP).toFloat(), "Umidade"))
                        entries.add(PieEntry(100 - air.setScale(2,RoundingMode.HALF_UP).toFloat(), "Outros (Ar)"))

                        val dataSet = PieDataSet(entries, " ")
                        dataSet.valueTextColor = Color.parseColor("#000000")
                        dataSet.colors = listOf(Color.parseColor("#9DB2FB"), Color.parseColor("#E5E8E8"))
                        dataSet.valueTextSize = 16f

                        val data = PieData(dataSet)

                        airHumidityPieChart.description.isEnabled = false
                        airHumidityPieChart.data = data
                        airHumidityPieChart.legend.textSize = 12f
                        airHumidityPieChart.setDrawEntryLabels(false)

                        airHumidityPieChart.invalidate()
                }
                measurement.co?.let { co ->
                    val formattedCo = co.setScale(2, RoundingMode.HALF_UP)
                    tvCOEquipmentName.text = args.equipment.name
                    tvCOValue.text = "$formattedCo ppm"
                }

                measurement.tmp?.let { temperature ->
                    val formattedTemperature = temperature.setScale(2, RoundingMode.HALF_UP)
                    tvTemperatureEquipmentName.text = args.equipment.name
                    tvTemperatureValue.text = "$formattedTemperature °C"
                }

                val collectionDate = measurement.collectionDate.withZoneSameInstant(ZoneId.of("America/Manaus"))
                val strCollectionDate = collectionDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                tvCollectionDateValue.text = "Última atualização $strCollectionDate"
        }
    }

    private fun setupView(){
        airHumidityPieChart = binding.pcAirHumidity
        pbLoading = binding.pbLoading

        tvCOEquipmentName = binding.tvCardEquipmentName
        tvCOValue = binding.tvCardEquipmentValueCO

        tvTemperatureEquipmentName = binding.tvTemperatureEquipmentName
        tvTemperatureValue = binding.tvTemperatureValue

        tvCollectionDateValue = binding.tvCollectionDateValue
        tvErrorMessage = binding.tvErrorMessage
        cardCO = binding.clCardCO
        cardTemperature = binding.clCardTemperature
        tvMonoxideTitle = binding.tvMonoxideTitle
        switchIsOnline = binding.swtIsOnline
    }

    private fun setupListeners(){
        switchIsOnline.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                infoViewModel.changeForOnline()
            } else {
                infoViewModel.changeForOffline()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}