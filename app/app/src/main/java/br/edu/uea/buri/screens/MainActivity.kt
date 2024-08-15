package br.edu.uea.buri.screens


import android.content.SharedPreferences
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import br.edu.uea.buri.BuriApplication
import br.edu.uea.buri.R
import br.edu.uea.buri.data.BuriApi
import br.edu.uea.buri.data.work.events.EventsWorkManager
import br.edu.uea.buri.data.work.events.WorkStarter
import br.edu.uea.buri.databinding.ActivityMainBinding
import br.edu.uea.buri.screens.equipment.register.EquipmentRegisterFragment
import br.edu.uea.buri.screens.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var buriApi: BuriApi
    @Inject
    lateinit var shared: SharedPreferences
    @Inject
    lateinit var workerStarter: WorkStarter
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel: MainViewModel by viewModels()
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val allGranted = permissions.values.all { it }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        workerStarter()
        //Se as permissões não foram concedidas, peça novamente
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.POST_NOTIFICATIONS
                ))
            } else {
                ActivityCompat.requestPermissions(this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    REQUEST_LOCATION_PERMISSION_CODE
                )
            }
        }
        viewModel.isConnected.observe(this){
            isOnline ->
                if(isOnline){
                    binding.navHostFragment.isVisible = true
                    binding.iconWifi.isVisible = false
                    binding.tvNoNetwork.isVisible = false
                } else {
                    binding.iconWifi.isVisible = true
                    binding.tvNoNetwork.isVisible = true
                    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                    val currentFragment = navHostFragment.childFragmentManager.fragments.lastOrNull()

                    currentFragment?.let { fragment ->
                        when(fragment){
                            is LoginFragment -> {
                                binding.navHostFragment.isVisible = true
                            }
                            is EquipmentRegisterFragment -> {
                                binding.navHostFragment.isVisible = true
                            }
                            else -> binding.navHostFragment.isVisible = false
                        }
                    }
                }
        }
    }
    companion object {
        const val REQUEST_LOCATION_PERMISSION_CODE = 1001
    }
}

