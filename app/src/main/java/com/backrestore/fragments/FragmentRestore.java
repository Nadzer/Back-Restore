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
import com.backrestore.backup.BackgroundRestore;
import com.backrestore.backup.RestoreContent;
import com.backrestore.interfaces.IBackRestore;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nadzer on 29/03/2016.
 */
public class FragmentRestore extends Fragment implements IBackRestore {
    private CircularFillableLoaders mCircularFillableLoaders;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 2;
    private RestoreContent restoreContent = new RestoreContent(getContext());
    private GridView mGridView;
    private View view;
    private BackgroundRestore mBackgroundRestore;
    private String[] mTitlesRestore = {
            "Restore SMS", "Restore Contacts",
            "Restore call logs", "Download from Google Drive",
            "Restore Calendar events"
    };
    private int[] mImagesRestore = {
            R.drawable.ic_menu_camera, R.drawable.ic_menu_gallery,
            R.drawable.ic_menu_slideshow, R.drawable.ic_menu_share,
            R.drawable.ic_menu_send
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_restore, container, false);
        mCircularFillableLoaders = (CircularFillableLoaders) getActivity()
                .findViewById(R.id.circularFillableLoaders);
        restoreContent = new RestoreContent(getContext());

        setUI();
        return view;
    }

    @Override
    public void setUI() {
        mGridView = (GridView) view.findViewById(R.id.main_grid_restore);
        String[] from = {"image", "title"};
        int[] to = {R.id.image, R.id.title};

        SimpleAdapter adapter = new SimpleAdapter(getContext(), getData(),
                R.layout.gridview_content, from, to);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        /*final MimeTypeMap mime = MimeTypeMap.getSingleton();
                        String tmptype = mime.getMimeTypeFromExtension("txt");
                        final File file = new File(Environment
                        .getExternalStorageDirectory().toString()
                                + "/contacts.txt");
                        Log.d("___file", file.getAbsolutePath());
                        Intent i = new Intent();
                        i.setAction(android.content.Intent.ACTION_VIEW);
                        i.setDataAndType(Uri.fromFile(file), "text/plain");
                        startActivity(i);*/
                        if (permissionWrapper(Manifest.permission.WRITE_CONTACTS) == 2) {
                            mBackgroundRestore = new BackgroundRestore(mCircularFillableLoaders,
                                    restoreContent, Manifest.permission.WRITE_CONTACTS);
                            mBackgroundRestore.execute();
                        }
                        break;
                    case 1:
                        if (permissionWrapper(Manifest.permission.READ_CONTACTS) == 2) {
                            mBackgroundRestore = new BackgroundRestore(mCircularFillableLoaders,
                                    restoreContent, Manifest.permission.READ_CONTACTS);
                            mBackgroundRestore.execute();
                        }
                        break;
                    case 2:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            if (permissionWrapper(Manifest.permission.READ_CALL_LOG) == 2) {
                                mBackgroundRestore = new BackgroundRestore(mCircularFillableLoaders,
                                        restoreContent, Manifest.permission.READ_CALL_LOG);
                                mBackgroundRestore.execute();
                            }
                        }
                        break;
                    case 4:
                        if (permissionWrapper(Manifest.permission.READ_CALENDAR) == 2) {
                            mBackgroundRestore = new BackgroundRestore(mCircularFillableLoaders,
                                    restoreContent, Manifest.permission.READ_CALENDAR);
                            mBackgroundRestore.execute();
                        }
                        break;
                }
            }
        });
    }

    @Override
    public List<HashMap<String, String>> getData() {
        List<HashMap<String, String>> mData = new ArrayList<>();

        for (int i = 0; i < mTitlesRestore.length; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("image", Integer.toString(mImagesRestore[i]));
            hashMap.put("title", mTitlesRestore[i]);
            mData.add(hashMap);
        }

        return mData;
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
                    mBackgroundRestore = new BackgroundRestore(mCircularFillableLoaders,
                            restoreContent, permissions[0]);
                    mBackgroundRestore.execute();
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
}
