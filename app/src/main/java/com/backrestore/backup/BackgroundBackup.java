package com.backrestore.backup;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Handler;

import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

/**
 * Created by Nadzer on 24/03/2016.
 */
public class BackgroundBackup extends AsyncTask<Void, Integer, Void> {
    private CircularFillableLoaders mLoader;
    private BackUpContent mBackUpContent;
    private String mParam;

    public BackgroundBackup(CircularFillableLoaders loader, BackUpContent backUpContent, String param) {
        this.mLoader = loader;
        this.mBackUpContent = backUpContent;
        this.mParam = param;
    }

    @Override
    protected Void doInBackground(Void... params) {
        switch (mParam) {
            case Manifest.permission.READ_CONTACTS:
                mBackUpContent.backUpContacts();
                break;
            case Manifest.permission.READ_SMS:
                mBackUpContent.backupSms();
                break;
            case Manifest.permission.READ_CALL_LOG:
                mBackUpContent.backUpCallLogs();
                break;
            case Manifest.permission.READ_CALENDAR:
                mBackUpContent.backUpCalendars();
                break;
        }

        publishProgress(0);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void s) {
        super.onPostExecute(s);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                mLoader.setProgress(75);
            }
        }, 1000);

        Handler handlerr = new Handler();
        handlerr.postDelayed(new Runnable() {
            public void run() {
                mLoader.setProgress(50);
            }
        }, 2000);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        mLoader.setProgress(values[0]);
    }
}
