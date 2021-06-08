package com.example.jcproject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChooserActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, AdapterView.OnItemClickListener {

    private static final String TAG = "ChooserActivity";
    private static final int PERMISSION_REQUESTS = 1;
    public Button startButton;

    private static final Class<?>[] CLASSES =
            new Class<?>[] {
                    LivePreviewActivity.class//, StillImageActivity.class,
            };

    private static final int[] DESCRIPTION_IDS =
            new int[] {
                    R.string.desc_camera_source_activity, R.string.desc_still_image_activity,
            };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_chooser);

        startButton = (Button) findViewById(R.id.start);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class<?> clicked = CLASSES[0];
                startActivity(new Intent( getApplicationContext(), clicked));
            }
        });

        ListView listView = (ListView) findViewById(R.id.test_activity_list_view);

        MyArrayAdapter adapter = new MyArrayAdapter(this, android.R.layout.simple_list_item_2, CLASSES);
        adapter.setDescriptionIds(DESCRIPTION_IDS);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Class<?> clicked = CLASSES[position];
        startActivity(new Intent(this, clicked));
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }

    private static class MyArrayAdapter extends ArrayAdapter<Class<?>> {

        private final Context context;
        private final Class<?>[] classes;
        private int[] descriptionIds;

        public MyArrayAdapter(Context context, int resource, Class<?>[] objects) {
            super(context, resource, objects);

            this.context = context;
            classes = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (convertView == null) {
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(android.R.layout.simple_list_item_2, null);
            }

//      ((TextView) view.findViewById(android.R.id.text1)).setText(classes[position].getSimpleName());
            ((TextView) view.findViewById(android.R.id.text1)).setText("START");
            ((TextView) view.findViewById(android.R.id.text2)).setText(descriptionIds[position]);

            return view;
        }

        public void setDescriptionIds(int[] descriptionIds) {
            this.descriptionIds = descriptionIds;
        }
    }



}