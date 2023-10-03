package com.aatorque.prefs

import android.app.Activity
import android.app.DownloadManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import timber.log.Timber
import android.view.Menu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.aatorque.datastore.UserPreference
import com.aatorque.stats.App
import com.aatorque.stats.BuildConfig
import com.aatorque.stats.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class SettingsActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_fragment, SettingsFragment())
                .commit()
        }
        lifecycleScope.launch(Dispatchers.IO) {
            checkUpdate()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_export_dashboards -> {
                // Export the file to a user provided location
                exportFile()
                true
            }

            R.id.action_import_dashboards -> {
                openFile()
                true
            }

            R.id.action_copy_logs -> {
                logsToClipboard()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openFile() {
        importFileLauncher.launch(EXPORT_MIME)
    }

    private fun exportFile() {
        val file = File(filesDir, "datastore/user_prefs.pb")

        if (file.exists() && file.canRead()) {
            // Launch the activity result with the contract input
            exportFileLauncher.launch(file.name)
        } else {
            // Show a toast message that the file is not available
            Toast.makeText(this, R.string.file_not_available, Toast.LENGTH_SHORT).show()
        }
    }

    private val exportFileLauncher = registerForActivityResult(ExportFileContract()) { uri: Uri? ->
        // This lambda will be executed when the activity result returns
        var msg = if (uri != null) {
            // Get the content resolver and open an output stream to the URI
            val resolver = contentResolver
            resolver.openOutputStream(uri)?.use { outputStream ->
                // Get the file to be exported and open an input stream from it
                val file = File(filesDir, "datastore/user_prefs.pb")
                file.inputStream().use { inputStream ->
                    // Copy the bytes from the input stream to the output stream
                    inputStream.copyTo(outputStream)
                }
            }
            // Show a toast message that the file was exported successfully
            R.string.file_exported_successfully
        } else {
            // Show a toast message that the export failed
            R.string.export_failed
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


    private val importFileLauncher = registerForActivityResult(ImportFileContract()) { uri: Uri? ->
        if (uri != null) {
            val inStream = contentResolver.openInputStream(uri)
            this@SettingsActivity.lifecycleScope.launch {
                this@SettingsActivity.applicationContext.dataStore.updateData {
                    return@updateData UserPreference.parseFrom(inStream)
                }
            }
        }
    }

    class ExportFileContract : ActivityResultContract<String, Uri?>() {

        override fun createIntent(context: Context, input: String): Intent {
            return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                type = EXPORT_MIME
                // Suggest a file name based on the original file name
                putExtra(Intent.EXTRA_TITLE, "dashboards.pb")
            }
        }

        // This function parses the activity result and returns the URI of the user selected location
        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (resultCode == Activity.RESULT_OK) {
                intent?.data
            } else {
                null
            }
        }
    }

    class ImportFileContract : ActivityResultContract<String, Uri?>() {

        override fun createIntent(context: Context, input: String): Intent {
            return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = input
            }
        }

        // This function parses the activity result and returns the URI of the user selected location
        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (resultCode == Activity.RESULT_OK) {
                intent?.data
            } else {
                null
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }


    override fun onResume() {
        super.onResume()
        val permissionsToRequest: MutableList<String> = ArrayList()
        if (ContextCompat.checkSelfPermission(this, PERMISSION_CAR_VENDOR_EXTENSION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(PERMISSION_CAR_VENDOR_EXTENSION)
        }
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(permissionsToRequest.toTypedArray(), REQUEST_PERMISSIONS)
            return
        }

    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        // Instantiate the new Fragment
        val args = Bundle()
        args.putCharSequence("title", pref.title)
        args.putString("prefix", pref.key)
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment!!
        )
        fragment.arguments = args
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(R.id.settings_fragment, fragment)
            .addToBackStack(null)
            .commit()
        return true
    }

    private fun checkUpdate() {
        Timber.i("Checking for update")

        var needsUpdate: Boolean? = null
        var downloadItem: String? = null

        val url = URL(BuildConfig.RELEASE_URL)
        val urlConnection = url.openConnection() as HttpsURLConnection
        urlConnection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
        try {
            urlConnection.connect()
        } catch (e: ConnectException) {
            return
        }
        try {
            if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                val response = urlConnection.inputStream.bufferedReader().use {
                    JSONObject(it.readText())
                }
                val tagName = response.getString("tag_name")
                downloadItem = response.getJSONArray("assets")
                    .getJSONObject(0)
                    .getString("browser_download_url")

                val tagSplit = tagName.split(".")
                val existingSplit = BuildConfig.VERSION_NAME.split(".")

                tagSplit.forEachIndexed { pos, fromServer ->
                    if (needsUpdate == null && existingSplit.size > pos) {
                        val toComp = existingSplit[pos]
                        if (
                            fromServer != toComp &&
                            fromServer.isDigitsOnly() && toComp.isDigitsOnly()
                        ) {
                            needsUpdate = fromServer.toInt() > toComp.toInt()
                        }
                    }
                }
            }
        } catch (e: JSONException) {
            Timber.e("Failed to parse json on update check")
        } finally {
            urlConnection.disconnect()
        }


        if (needsUpdate == true) {
            assert(downloadItem != null)
            Snackbar.make(
                findViewById(android.R.id.content),
                R.string.new_version,
                Snackbar.LENGTH_LONG
            )
                .setDuration(10000)
                .setActionTextColor(resources.getColor(R.color.white, null))
                .setAction(R.string.download) {
                    val downloadRequest = DownloadManager.Request(Uri.parse(downloadItem))
                    downloadRequest.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "aa-torque.apk"
                    )
                    downloadRequest.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                    dm.enqueue(downloadRequest)
                    Toast.makeText(
                        baseContext,
                        R.string.download_instructions,
                        Toast.LENGTH_SHORT
                    ).show()
                }.show()
        }
    }

    private fun logsToClipboard() {
        val logs = (application as App).logTree.logToString()
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("AA Torque Log", logs.joinToString("\n")))
    }


    companion object {
        private const val REQUEST_PERMISSIONS = 0
        private const val PERMISSION_CAR_VENDOR_EXTENSION =
            "com.google.android.gms.permission.CAR_VENDOR_EXTENSION"
        const val EXPORT_MIME = "application/octet-stream"
    }
}

