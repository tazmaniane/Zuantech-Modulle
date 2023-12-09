package id.zuantech.module.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build

class NetworkMonitor : NetworkCallback() {

    companion object {
        val TAG = NetworkMonitor::class.java.simpleName

        enum class ConnectionType {
            NONE,
            WIFI,
            SIM_CARD
        }
    }

    var isConnected = false
    var resultConnection: ((connection: ConnectionType) -> Unit)? = null
    private lateinit var networkCallback: NetworkCallback

    fun isConnected(isConnected: Boolean) {
        this.isConnected = isConnected
    }

    fun resultConnection(resultConnection: (connection: ConnectionType) -> Unit) : NetworkMonitor {
        this.resultConnection = resultConnection
        return this
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        resultConnection?.let {
            it(ConnectionType.NONE)
        }
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities)
        when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                resultConnection?.let {
                    it(ConnectionType.WIFI)
                }
            }
            else -> {
                resultConnection?.let {
                    it(ConnectionType.SIM_CARD)
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    fun register(context: Context): NetworkMonitor {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Use NetworkCallback for Android 9 and above
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager.activeNetwork == null) {
                resultConnection?.let {
                    it(ConnectionType.NONE)
                }
            }
            networkCallback = this
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            // Use Intent Filter for Android 8 and below
            val intentFilter = IntentFilter()
            intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
            context.registerReceiver(networkChangeReceiver, intentFilter)
        }
        return this
    }

    fun unregister(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } else {
            context.unregisterReceiver(networkChangeReceiver)
        }
    }

    @Suppress("DEPRECATION")
    private val networkChangeReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null) {
                // Get Type of Connection
                when (activeNetworkInfo.type) {
                    ConnectivityManager.TYPE_WIFI -> {
                        resultConnection?.let {
                            it(ConnectionType.WIFI)
                        }
                    }
                    else -> {
                        resultConnection?.let {
                            it(ConnectionType.SIM_CARD)
                        }
                    }
                }
            } else {
                resultConnection?.let {
                    it(ConnectionType.NONE)
                }
            }
        }
    }
}