package com.backrestore.backup;

import android.Manifest;
import android.os.AsyncTask;
import android.os.Handler;

import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

/**
 * Created by Nadzer on 24/03/2016.
 */
public class BackgroundRestore extends AsyncTask<Void, Integer, Void> {
    private CircularFillableLoaders loader;
    private RestoreContent restoreContent;
    private String param;

    public BackgroundRestore(CircularFillableLoaders loader, RestoreContent restoreContent, String param) {
        this.loader = loader;
        this.restoreContent = restoreContent;
        this.param = param;
    }

    @Override
    protected Void doInBackground(Void... params) {
        switch (param) {
            case Manifest.permission.WRITE_CONTACTS:
                restoreContent.restoreContacts();
                break;
            case Manifest.permission.READ_SMS:
                restoreContent.restoreContacts();
                break;
            case Manifest.permission.READ_CALL_LOG:
                restoreContent.restoreContacts();
                break;
            case Manifest.permission.READ_CALENDAR:
                restoreContent.restoreContacts();
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
                loader.setProgress(75);
            }
        }, 1000);

        Handler handlerr = new Handler();
        handlerr.postDelayed(new Runnable() {
            public void run() {
                loader.setProgress(50);
            }
        }, 2000);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        loader.setProgress(values[0]);
    }
}
