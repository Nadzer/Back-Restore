package com.backrestore.backup;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.CalendarContract;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.text.ParcelableSpan;
import android.util.Log;
import android.widget.Toast;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by Nadzer on 16/03/2016.
 */
public class BackUpContent {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private JSONArray mJsonContactsArray;
    private JSONArray mJsonSmsArray;
    private String[] files_name = {"received.txt", "sent.txt", "contacts.txt", "call_logs.txt"};
    private Context mContext;
    public int filesCount;
    private int contacts;
    private int callLogs;
    private int mSentSms;
    private int mReceivedSms;
    private int eventsCalendar;
    private ContentResolver contentResolver;
    private Set<String> calendars = new HashSet<String>();
    private static final String[] FIELDS = {
            CalendarContract.Calendars.NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.CALENDAR_COLOR,
            CalendarContract.Calendars.VISIBLE
    };

    public BackUpContent(Context context) {
        this.mContext = context;
    }

    public void sendMessage(String object, String message, ArrayList uri) {
        Intent iEmail = new Intent(Intent.ACTION_SEND_MULTIPLE);
        iEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{"new-ssk@hotmail.fr"});
        iEmail.putExtra(Intent.EXTRA_SUBJECT, "Back&Restore - " + object);
        iEmail.putExtra(Intent.EXTRA_TEXT, message);
        iEmail.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uri);
        iEmail.setType("message/utf-8");

        if (uri.size() > 0) {
            mContext.startActivity(Intent.createChooser(iEmail, "Choisissez une messagerie :"));
        } else {
            Toast.makeText(mContext, "No backup file available!", Toast.LENGTH_LONG).show();
        }
    }

    public void backUpCalendars() {
        Uri CALENDAR_URI = Uri.parse("content://com.android.calendar/calendars");
        Cursor cursor = mContext.getContentResolver().query(CALENDAR_URI, FIELDS, null, null, null);
        JSONObject jsonEventsCalendar = null;
        JSONArray jsonArray = new JSONArray();

        while (cursor != null && cursor.moveToNext()) {
            jsonEventsCalendar = new JSONObject();

            for (int i = 0; i < cursor.getColumnCount(); i++) {
                jsonEventsCalendar.put(cursor.getColumnName(i), cursor.getString(i));
            }

            jsonArray.add(jsonEventsCalendar);
            eventsCalendar = cursor.getColumnCount();
        }

        if (cursor != null) cursor.close();
        jsonArray.clear();
        if (jsonEventsCalendar != null) jsonEventsCalendar.clear();
        Log.d("__events_calendar", jsonArray.toJSONString());
        createFile(jsonArray, "events_calendar");
    }

    public void backUpCallLogs() {
        Uri calls = Uri.parse("content://call_log/calls");
        String[] fields = {CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME,
                CallLog.Calls.DURATION, CallLog.Calls.TYPE, CallLog.Calls.DATE};
        Cursor cursor = this.mContext.getContentResolver().query(calls, fields, null, null, null);

        JSONObject jsonLogs = null;
        JSONArray jsonArray = new JSONArray();

        while (cursor != null && cursor.moveToNext()) {
            jsonLogs = new JSONObject();

            for (int i = 0; i < cursor.getColumnCount(); i++) {
                jsonLogs.put(cursor.getColumnName(i), cursor.getString(i));
            }

            this.callLogs = cursor.getColumnCount();
            jsonArray.add(jsonLogs);
        }

        Log.d("jsonArrayCall", jsonArray.toJSONString());
        if (cursor != null) cursor.close();
        jsonArray.clear();
        if (jsonLogs != null) jsonLogs.clear();
        createFile(jsonArray, "call_logs");
    }

    public void backUpContacts() {
        String[] dataContacts = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Photo.PHOTO,
                ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Relation.NAME,
                ContactsContract.CommonDataKinds.Photo.PHOTO_URI};
        Cursor cursor = this.mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, dataContacts, null, null, null);
        JSONObject jsonContacts = null;
        JSONArray jsonArray = new JSONArray();

        while (cursor != null && cursor.moveToNext()) {
            jsonContacts = new JSONObject();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                jsonContacts.put(cursor.getColumnName(i), cursor.getString(i));
            }
            contacts = cursor.getColumnCount();
            jsonArray.add(jsonContacts);
        }


        Log.d("jsonArray", jsonArray.toJSONString());
        if (cursor != null) cursor.close();
        jsonArray.clear();
        if (jsonContacts != null) jsonContacts.clear();
        createFile(jsonArray, "contacts");
    }

    public void backupSms() {
        JSONArray listSmsInbox;
        JSONArray listSmsSent;

        listSmsInbox = getPhoneSms("content://sms/inbox", "received");
        listSmsSent = getPhoneSms("content://sms/sent", "sent");

        Log.i("listSmsInbox", listSmsInbox.toString());
        Log.i("listSmsSent", listSmsSent.toString());
    }

    private JSONArray getPhoneSms(String parsing, String param) {
        Cursor c = this.mContext.getContentResolver().query(Uri.parse(parsing), null, null, null, null);
        JSONObject jsonSms = null;
        JSONArray jsonArray = new JSONArray();

        while (c != null && c.moveToNext()) {
            jsonSms = new JSONObject();
            for (int i = 0; i < c.getColumnCount(); i++) {
                jsonSms.put(c.getColumnName(i), c.getString(i));
            }
            jsonArray.add(jsonSms);

            if (param.equals("received")) {
                mReceivedSms = c.getColumnCount();
            } else {
                mSentSms = c.getColumnCount();
            }
        }
        jsonArray.add(jsonSms);

        Log.i("__jsonArray", jsonArray.toJSONString());
        if (c != null) c.close();
        jsonArray.clear();
        if (jsonSms != null) jsonSms.clear();
        createFile(jsonArray, param);

        return jsonArray;
    }

    public ArrayList getFiles() {
        ArrayList uris = new ArrayList();
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir("com.backrestore", Context.MODE_PRIVATE);

        for (String aFiles_name : files_name)
            uris.add(Uri.fromFile(new File(directory, aFiles_name)));

        return uris;
    }

    private void createFile(JSONArray jsonArray, String param) {
        JSONObject obj = new JSONObject();
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir("com.backrestore", Context.MODE_PRIVATE);
        File fichier = new File(directory, param + ".txt");

        try {

            if (!fichier.exists()) {
                fichier = new File(directory, param + ".txt");
                fichier.createNewFile();
            }
//            File exst = Environment.getExternalStorageDirectory();
//            final File dir = new File(Environment.getExternalStorageDirectory() + "backrestore");
//            String exstPath = exst.getPath();
//            String path = exstPath + "/com.backrestore";
//            new File(path).mkdirs();
//        Log.d("__path", exstPath);
            obj.put(param, jsonArray);
//            File file = new File(exstPath + "/com.backrestore", param + ".txt");
//            Log.d("__getfilesdir", file.getAbsolutePath());
            FileWriter fileWriter = new FileWriter(fichier);
            fileWriter.write(obj.toJSONString());
            Log.d("__fichier", fichier.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        filesCount = mSentSms
                + mReceivedSms
                + contacts
                + callLogs
                + eventsCalendar;
    }

}
