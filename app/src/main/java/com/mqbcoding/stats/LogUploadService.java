package com.mqbcoding.stats;
import android.app.job.JobInfo;

import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.SparseArray;

import com.github.martoreto.aauto.vex.FieldSchema;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.model.Job;
import com.google.api.services.bigquery.model.JobConfiguration;
import com.google.api.services.bigquery.model.JobConfigurationLoad;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableReference;
import com.google.api.services.bigquery.model.TableSchema;
import com.google.api.services.bigquery.model.TimePartitioning;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class LogUploadService extends JobService {
    private static final String TAG = "LogUploadService";

    public static final String EXTRA_FILENAME = "filename";

    public static final String PREF_BIGQUERY_ENABLED = "uploadToBigquery";
    public static final String PREF_BIGQUERY_PROJECT_ID = "bigqueryProjectId";
    public static final String PREF_BIGQUERY_DATASET = "bigqueryDataset";
    public static final String PREF_BIGQUERY_TABLE = "bigqueryTable";

    public static final int MAX_DESCRIPTION_LENGTH = 100;

    private final HttpTransport mHttpTransport = new NetHttpTransport();
    private final JsonFactory mJsonFactory = GsonFactory.getDefaultInstance();
    private final Gson mGson = new Gson();

    private final SparseArray<LogUploadTask> mTasks = new SparseArray<>();

    public static synchronized void schedule(Context context, File logFile) {
        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        assert scheduler != null;
        PersistableBundle extras = new PersistableBundle();
        extras.putString(EXTRA_FILENAME, logFile.getAbsolutePath());
        ComponentName jobService =
                new ComponentName("BuildConfig.PACKAGE_NAME", LogUploadService.class.getName());
        int jobId = logFile.hashCode();
        JobInfo jobInfo = new JobInfo.Builder(jobId, jobService)
                .setExtras(extras)
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();
        if (scheduler.schedule(jobInfo) != JobScheduler.RESULT_SUCCESS) {
            Log.e(TAG, logFile + ": failed to schedule upload");
        }
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        LogUploadTask task = new LogUploadTask(jobParameters);
        mTasks.put(jobParameters.getJobId(), task);
        task.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        LogUploadTask task = mTasks.get(jobParameters.getJobId());
        if (task != null) {
            task.cancel(true);
            mTasks.remove(jobParameters.getJobId());
        }
        return true;
    }

    private class LogUploadTask extends AsyncTask<Void, Void, Boolean> {
        private final JobParameters mJobParameters;
        private final File mLogFile;
        private final Bundle mAnalyticsBundle;

        @SuppressWarnings("ConstantConditions")
        public LogUploadTask(JobParameters jobParameters) {
            this.mJobParameters = jobParameters;
            this.mLogFile = new File(jobParameters.getExtras().getString(EXTRA_FILENAME));
            this.mAnalyticsBundle = new Bundle();
            this.mAnalyticsBundle.putString("filename", mLogFile.getName());
        }

        @Override
        protected Boolean doInBackground(Void... args) {
            if (!mLogFile.exists()) {
                Log.d(TAG, mLogFile + ": does not exist anymore");
                return true;
            }
            if (mLogFile.length() == 0) {
                Log.d(TAG, mLogFile + ": is empty");
                return true;
            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LogUploadService.this);
            if (!preferences.getBoolean(PREF_BIGQUERY_ENABLED, false)) {
                Log.d(TAG, mLogFile + ": upload disabled");
                return true;
            }
            if (!preferences.contains(PREF_BIGQUERY_PROJECT_ID) || !preferences.contains(PREF_BIGQUERY_DATASET)) {
                Log.d(TAG, mLogFile + ": missing preferences");
                return true;
            }

            try {
                Log.d(TAG, mLogFile + ": starting upload");

                File schemaFile = new File(mLogFile.getParentFile(), "schema.json");
                if (!schemaFile.exists()) {
                    throw new FileNotFoundException(schemaFile.toString());
                }

                // Read schema.
                Type type = new TypeToken<TreeMap<String, FieldSchema>>(){}.getType();
                Map<String, FieldSchema> schemaMap = mGson.fromJson(
                        Files.asCharSource(schemaFile, StandardCharsets.UTF_8).read(), type);
                List<TableFieldSchema> fields = new ArrayList<>();
                fields.add(new TableFieldSchema()
                        .setName("timestamp")
                        .setDescription("Timestamp")
                        .setType("TIMESTAMP"));
                for (Map.Entry<String, FieldSchema> e: schemaMap.entrySet()) {
                    String key = CarStatsLogger.makeJsonKey(e.getKey());
                    FieldSchema field = e.getValue();
                    TableFieldSchema fieldSchema = new TableFieldSchema();
                    fieldSchema.setName(key);
                    String description = field.getDescription();
                    if (description != null) {
                        if (description.length() > MAX_DESCRIPTION_LENGTH) {
                            description = description.substring(0, MAX_DESCRIPTION_LENGTH);
                        }
                        if (field.getUnit() != null) {
                            description += " [" + field.getUnit() + "]";
                        }
                        fieldSchema.setDescription(description);
                    }
                    switch (field.getType()) {
                        case FieldSchema.TYPE_BOOLEAN: fieldSchema.setType("BOOLEAN"); break;
                        case FieldSchema.TYPE_FLOAT: fieldSchema.setType("FLOAT"); break;
                        case FieldSchema.TYPE_INTEGER: fieldSchema.setType("INT64"); break;
                        case FieldSchema.TYPE_STRING: fieldSchema.setType("STRING"); break;
                    }
                    fields.add(fieldSchema);
                }

                TableSchema schema = new TableSchema();
                schema.setFields(fields);

                // Save bq-schema.json
                File bqSchemaFile = new File(mLogFile.getParentFile(), "bq-schema.json");
                Files.asCharSink(bqSchemaFile, StandardCharsets.UTF_8).write(
                        mJsonFactory.toPrettyString(schema));

                // Cleanup file
                File finalFile = new File(mLogFile.getParentFile(), mLogFile.getName() + ".bq");
                try {
                    try (final BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(mLogFile)), StandardCharsets.UTF_8))) {
                        try (FileOutputStream fileOutputStream = new FileOutputStream(finalFile)) {
                            try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream)) {
                                try (final Writer writer = new OutputStreamWriter(gzipOutputStream)) {
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        int endOfRecord;
                                        while ((endOfRecord = line.indexOf('}')) != -1) {
                                            writer.write(line.substring(0, endOfRecord + 1));
                                            writer.write('\n');
                                            line = line.substring(endOfRecord + 1);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.w(TAG, mLogFile + ": Exception cleaning up file", e);
                    }

                    App app = (App) getApplication();
                    Bigquery bigquery =
                            new Bigquery.Builder(mHttpTransport, mJsonFactory, app.getGoogleCredential())
                                    .setApplicationName("AAPlus").build();
                    String projectId = preferences.getString(PREF_BIGQUERY_PROJECT_ID, "");
                    String datasetId = preferences.getString(PREF_BIGQUERY_DATASET, "");
                    String tablePrefix = preferences.getString(PREF_BIGQUERY_TABLE, "aastats");
                    String[] filenameParts = mLogFile.getName().split("-");
                    String dateSuffix = filenameParts[filenameParts.length - 1].substring(0, 8);
                    String tableId = tablePrefix + "$" + dateSuffix;
                    FileContent content = new FileContent(MediaType.JSON_UTF_8.toString(), finalFile);
                    Job job = new Job().setConfiguration(
                            new JobConfiguration().setLoad(
                                    new JobConfigurationLoad()
                                            .setSourceFormat("NEWLINE_DELIMITED_JSON")
                                            .setSchema(schema)
                                            .setTimePartitioning(new TimePartitioning().setType("DAY"))
                                            .setWriteDisposition("WRITE_APPEND")
                                            .setSchemaUpdateOptions(ImmutableList.of(
                                                    "ALLOW_FIELD_ADDITION", "ALLOW_FIELD_RELAXATION"))
                                            .setIgnoreUnknownValues(true)
                                            .setDestinationTable(
                                                    new TableReference()
                                                            .setProjectId(projectId)
                                                            .setDatasetId(datasetId)
                                                            .setTableId(tableId))
                            ));
                    Log.d(TAG, mLogFile + ": upload size is " + content.getLength());
                    Job result = bigquery.jobs().insert(projectId, job, content).execute();
                    Log.d(TAG, mLogFile + ": job id is " + result.getId());

                    // Move to uploaded/ subfolder.
                    File uploadedFolder = new File(mLogFile.getParentFile(), "uploaded");
                    uploadedFolder.mkdir();
                    Files.move(mLogFile, new File(uploadedFolder, mLogFile.getName()));

                    return true;
                } finally {
                    finalFile.delete();
                }
            } catch (UserRecoverableAuthIOException e) {
                Log.w(TAG, mLogFile + ": authorization needed", e);
                mAnalyticsBundle.putString("exception", e.getClass().getSimpleName());
                Intent intent = new Intent(LogUploadService.this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(SettingsActivity.EXTRA_AUTHORIZATION_INTENT, e.getIntent());
                startActivity(intent);
                return false;
            } catch (GoogleJsonResponseException e) {
                mAnalyticsBundle.putString("exception", e.getClass().getSimpleName());
                if (e.getStatusCode() == 400) {
                    Log.w(TAG, mLogFile + ": BigQuery fatal error", e);
                    return true;
                } else {
                    Log.w(TAG, mLogFile + ": BigQuery retriable error", e);
                    return false;
                }
            } catch (Exception e) {
                Log.w(TAG, mLogFile + ": upload failed", e);
                mAnalyticsBundle.putString("exception", e.getClass().getSimpleName());
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            mTasks.remove(mJobParameters.getJobId());
            jobFinished(mJobParameters, !success);
        }
    }
}