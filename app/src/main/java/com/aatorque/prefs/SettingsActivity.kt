package com.aatorque.prefs

import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.aatorque.datastore.UserPreference
import com.aatorque.stats.App
import com.aatorque.stats.BuildConfig
import com.aatorque.stats.CreditsFragment
import com.aatorque.stats.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLPeerUnverifiedException


class SettingsActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    val br: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.`package` == BuildConfig.APPLICATION_ID) {
                AlertDialog.Builder(this@SettingsActivity).setTitle(R.string.download_complete)
                    .setMessage(R.string.download_dialog)
                    .setPositiveButton(android.R.string.ok, object: DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
                            if (downloadId == -1L) return
                            val uri = (getSystemService(DOWNLOAD_SERVICE) as DownloadManager).getUriForDownloadedFile(downloadId)
                            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Intent(Intent.ACTION_INSTALL_PACKAGE).also {
                                    it.data = uri
                                    it.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                            } else {
                                Intent(Intent.ACTION_VIEW).also {
                                    it.setDataAndTypeAndNormalize(
                                        uri,
                                        "application/vnd.android.package-archive"
                                    )
                                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                            }
                            intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                            intent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, "com.android.vending")
                            applicationContext.startActivity(intent)
                        }
                    }).setNegativeButton(android.R.string.cancel, object: DialogInterface.OnClickListener{
                        override fun onClick(p0: DialogInterface?, p1: Int) {
                            p0?.dismiss()
                        }
                    }).show()
            }
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar!!.setDisplayUseLogoEnabled(true)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings_fragment, SettingsFragment())
                .commit()
        }
        lifecycleScope.launch(Dispatchers.IO) {
            checkUpdate()
        }
        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentResumed(fm: FragmentManager, f: Fragment) {
                    super.onFragmentResumed(fm, f)
                    supportActionBar!!.setDisplayHomeAsUpEnabled(f !is SettingsFragment)
                }
            }, false
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_preview -> {
                launchFragment("preview", DashboardPreviewFragment())
                true
            }

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

            R.id.action_credits -> {
                launchFragment("credits", CreditsFragment())
                true
            }

            R.id.force_update -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    if (!checkUpdate(true)) {
                        Toast.makeText(this@SettingsActivity, R.string.update_failed, Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }

            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openFile() {
        importFileLauncher.launch(EXPORT_MIME)
    }

    private fun exportFile() {
        exportFileLauncher.launch("")
    }

    private fun launchFragment(
        tag: String,
        fragment: Fragment
    ) {
        val existing = supportFragmentManager.findFragmentByTag(tag)
        if (existing?.isVisible == true) return
        supportFragmentManager.beginTransaction()
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .replace(
                R.id.settings_fragment,
                existing ?: fragment,
                tag
            )
            .addToBackStack(null)
            .commit()
    }

    private val exportFileLauncher = registerForActivityResult(ExportFileContract()) { uri: Uri? ->
        // This lambda will be executed when the activity result returns
        lifecycleScope.launch(Dispatchers.IO) {
            val data = applicationContext.dataStore.data.first()
            val result = if (uri != null) {
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    UserPreferenceSerializer.writeTo(data, outputStream)
                }
                R.string.file_exported_successfully
            } else {
                R.string.export_failed
            }
            runOnUiThread {
                Toast.makeText(baseContext, result, Toast.LENGTH_SHORT).show()
            }
        }
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

    private fun checkUpdate(force: Boolean =false): Boolean {
        Timber.i("Checking for update")

        var needsUpdate: Boolean? = if (!force) null else true
        var downloadItem: String? = null

        val url = URL(BuildConfig.RELEASE_URL)
        val urlConnection = url.openConnection() as HttpsURLConnection
        urlConnection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
        try {
            urlConnection.connect()
        } catch (e: UnknownHostException) {
            return false
        } catch (e: ConnectException) {
            return false
        } catch (e: SSLPeerUnverifiedException) {
            return false
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
                    ContextCompat.registerReceiver(
                        baseContext,
                        br,
                        IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                        ContextCompat.RECEIVER_EXPORTED
                    )
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

        return needsUpdate == null
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

