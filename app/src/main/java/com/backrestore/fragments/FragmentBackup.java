package com.backrestore.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.backrestore.R;
import com.backrestore.backup.BackUpContent;
import com.backrestore.backup.BackgroundBackup;
import com.backrestore.interfaces.IBackRestore;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nadzer
 * 29/03/2016.
 */
public class FragmentBackup extends Fragment implements IBackRestore {
    private CircularFillableLoaders mCircularFillableLoaders;
    private BackUpContent mBackUpContent;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 2;
    private GridView mGridView;
    private View view;
    private BackgroundBackup mBackgroundBackup;
    private String[] mTitlesBack = {
            "Backup SMS", "Backup Contacts",
            "Backup call logs", "Upload to Google Drive",
            "Backup Calendar events", "Send to email address"
    };
    private int[] mImagesBack = {
            R.drawable.ic_menu_camera, R.drawable.ic_menu_gallery,
            R.drawable.ic_menu_slideshow, R.drawable.ic_menu_share,
            R.drawable.ic_menu_send, R.drawable.ic_menu_send
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_backup, container, false);
        mCircularFillableLoaders = (CircularFillableLoaders) getActivity()
                .findViewById(R.id.circularFillableLoaders);
        mBackUpContent = new BackUpContent(getContext());

        setUI();
        return view;
    }

    @Override
    public void setUI() {
        mGridView = (GridView) view.findViewById(R.id.main_grid);
        String[] from = {"image", "title"};
        int[] to = {R.id.image, R.id.title};

        SimpleAdapter adapter = new SimpleAdapter(getContext(), getData(), R.layout.gridview_content, from, to);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        if (permissionWrapper(Manifest.permission.READ_SMS) == 2) {
                            mBackgroundBackup = new BackgroundBackup(mCircularFillableLoaders,
                                    mBackUpContent, Manifest.permission.READ_SMS);
                            mBackgroundBackup.execute();
                        }
                        break;
                    case 1:
                        if (permissionWrapper(Manifest.permission.READ_CONTACTS) == 2) {
                            mBackgroundBackup = new BackgroundBackup(mCircularFillableLoaders,
                                    mBackUpContent, Manifest.permission.READ_CONTACTS);
                            mBackgroundBackup.execute();
                        }
                        break;
                    case 2:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            if (permissionWrapper(Manifest.permission.READ_CALL_LOG) == 2) {
                                mBackgroundBackup = new BackgroundBackup(mCircularFillableLoaders,
                                        mBackUpContent, Manifest.permission.READ_CALL_LOG);
                                mBackgroundBackup.execute();
                            }
                        }
                        break;
                    case 3:
//                        Intent i = new Intent(getContext(), GoogleDriveActivity.class);
//                        startActivity(i);
                        break;
                    case 4:
                        if (permissionWrapper(Manifest.permission.READ_CALENDAR) == 2) {
                            mBackgroundBackup = new BackgroundBackup(mCircularFillableLoaders,
                                    mBackUpContent, Manifest.permission.READ_CALENDAR);
                            mBackgroundBackup.execute();
                        }
                        break;
                    case 5:
                        mBackUpContent.sendMessage("Fichiers de backup", "Bonjour, Veuillez " +
                                        "trouver en pièce jointe les fichiers de backup",
                                mBackUpContent.getFiles());
                }
            }
        });
    }

    @Override
    public int permissionWrapper(String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            int hasReadContactsPermission = ContextCompat
                    .checkSelfPermission(getContext(), permission);

            mCircularFillableLoaders.setProgress(25);
            if (hasReadContactsPermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permission},
                        REQUEST_CODE_ASK_PERMISSIONS);
                return 1;
            }
        }

        return 2;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Permission Granted
                    mBackgroundBackup = new BackgroundBackup(mCircularFillableLoaders,
                            mBackUpContent, permissions[0]);
                    mBackgroundBackup.execute();
                    return;
                } else { // Permission Denied
                    Toast.makeText(this.getActivity(), permissions[0] + " denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public List<HashMap<String, String>> getData() {
        List<HashMap<String, String>> mData = new ArrayList<>();

        for (int i = 0; i < mTitlesBack.length; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("image", Integer.toString(mImagesBack[i]));
            hashMap.put("title", mTitlesBack[i]);
            mData.add(hashMap);
        }

        return mData;
    }
}
