package ltd.evilcorp.atox.ui.settings

import android.content.ComponentName
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ltd.evilcorp.atox.BootReceiver
import ltd.evilcorp.atox.tox.ToxStarter
import ltd.evilcorp.domain.tox.Tox

class SettingsViewModel @Inject constructor(
    private val context: Context,
    private val preferences: SharedPreferences,
    private val toxStarter: ToxStarter,
    private val tox: Tox
) : ViewModel(), CoroutineScope by GlobalScope {
    private var restartNeeded = false

    fun getUdpEnabled(): Boolean = preferences.getBoolean("udp_enabled", false)
    fun setUdpEnabled(enabled: Boolean) {
        preferences.edit().putBoolean("udp_enabled", enabled).apply()
        restartNeeded = true
    }

    fun getRunAtStartup(): Boolean = context.packageManager.getComponentEnabledSetting(
        ComponentName(context, BootReceiver::class.java)
    ) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED

    fun setRunAtStartup(enabled: Boolean) {
        val state = if (enabled) {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        }

        context.packageManager.setComponentEnabledSetting(
            ComponentName(context, BootReceiver::class.java),
            state,
            PackageManager.DONT_KILL_APP
        )
    }

    fun commit() {
        if (!restartNeeded) return
        toxStarter.stopTox()

        launch {
            while (tox.started) {
                delay(200)
            }
            toxStarter.tryLoadTox()
        }
    }
}
