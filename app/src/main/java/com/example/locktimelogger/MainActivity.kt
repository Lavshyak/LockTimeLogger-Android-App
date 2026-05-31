package com.example.locktimelogger

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.accessibility.AccessibilityManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LogEntryAdapter
    private lateinit var statusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE_POST_NOTIFICATIONS)
        }

        // Запускаем foreground service — работает без ограничений на Android 13,
        // а на Android 14+ будет жить пока система не убьет (AccessibilityService
        // будет фоллбеком).
        startForegroundService(Intent(this, ScreenStateAccessibilityService::class.java))

        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        recyclerView = findViewById(R.id.logRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<Button>(R.id.btnRefresh).setOnClickListener {
            loadLog()
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            LogUtils.clearLog(this)
            loadLog()
        }

        loadLog()
    }

    override fun onResume() {
        super.onResume()
        checkAccessibilityServiceStatus()
    }

    /**
     * Проверяет, включен ли AccessibilityService, и предлагает пользователю его включить.
     */
    private fun checkAccessibilityServiceStatus() {
        val accessibilityManager = getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
            AccessibilityServiceInfo.FEEDBACK_GENERIC
        )

        val isServiceEnabled = enabledServices.any { it.resolveInfo.serviceInfo.packageName == packageName }

        statusText.text = if (isServiceEnabled) {
            "AccessibilityService: ВКЛЮЧЕН ✓\nРаботает в фоне без ограничений."
        } else {
            "AccessibilityService: ОТКЛЮЧЕН ✗\nНа Android 14+ foreground service будет убит системой."
        }

        if (!isServiceEnabled) {
            showEnableAccessibilityDialog()
        }
    }

    private fun showEnableAccessibilityDialog() {
        AlertDialog.Builder(this)
            .setTitle("Включить специальные возможности?")
            .setMessage(
                "В Android 14+ (особенно 16+) foreground service убивается системой через ~6 часов.\n\n" +
                "Для надежной работы в фоне приложение использует AccessibilityService.\n\n" +
                "Пожалуйста, включите его в настройках:\n" +
                "Настройки → Специальные возможности → LockTimeLogger → Включить"
            )
            .setPositiveButton("Открыть настройки") { _, _ ->
                openAccessibilitySettings()
            }
            .setNegativeButton("Позже") { _, _ ->
                Toast.makeText(this, "Foreground service будет работать, но может быть остановлен системой", Toast.LENGTH_LONG).show()
            }
            .setCancelable(false)
            .show()
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Разрешение получено — можно показывать уведомления
            } else {
                // Разрешение не получено — уведомления не будут отображаться
            }
        }
    }

    private val REQUEST_CODE_POST_NOTIFICATIONS = 101

    private fun loadLog() {
        val entries = LogUtils.getLogEntries(this)
        adapter = LogEntryAdapter(entries)
        recyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}