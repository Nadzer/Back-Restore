package com.backrestore.backup;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Nadzer
 * 08/05/2016.
 */
public class RestoreContent {
    private Context mContext;

    public RestoreContent(Context mContext) {
        this.mContext = mContext;
    }

    public void restoreContacts()
    {
        ContentValues values = new ContentValues();
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir("com.backrestore", Context.MODE_PRIVATE);
        File contacts = new File(directory, String.format("%s", "contacts.txt"));

        try {
            InputStream is = new FileInputStream(contacts);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;

            while ((line = br.readLine()) != null) {
                Log.d("___line", line);
                values.put("display_name", line);
                values.put("number", line);
                values.put("id", line);
                values.put("photo", line);
                values.put("address", line);
                values.put("name", line);
                values.put("photo_uri", line);
            }

            br.close();
            isr.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        values.put("mimetype", ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        mContext.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, values);
    }
}
