package com.maple.trafficmonitoring;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.List;

public class MainActivity extends Activity {
    private Button btnStart,btnStart1;
    private ListView lstViProgramme; ;
    private ProcessInfo processInfo;
    private int pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStart = (Button) findViewById(R.id.btnStart);
        lstViProgramme = (ListView) findViewById(R.id.listView);
        btnStart1 = (Button) findViewById(R.id.btnStart1);


        processInfo = new ProcessInfo();
        lstViProgramme.setAdapter(new ListAdapter());
        lstViProgramme.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RadioButton rdBtn = (RadioButton) ((LinearLayout) view).getChildAt(0);
                rdBtn.setChecked(true);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListAdapter adapter = (ListAdapter) lstViProgramme.getAdapter();
                if (adapter.checkedProg != null){
                    String packageName = adapter.checkedProg.getPackageName();
                    String APPName = adapter.checkedProg.getProcessName();
                    Intent intent = new Intent(MainActivity.this,StartMonitor.class);
                    intent.putExtra("packageName",packageName);
                    intent.putExtra("APPName",APPName);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this, "请选择需要测试的应用程序", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnStart1.setOnClickListener(new View.OnClickListener() {

            public void onClick (View v){
                ListAdapter adapter = (ListAdapter) lstViProgramme.getAdapter();
                if (adapter.checkedProg != null) {
                    String packageName = adapter.checkedProg.getPackageName();
                    String APPName = adapter.checkedProg.getProcessName();
                    Intent intent = new Intent(MainActivity.this, CpuAndPSS.class);
                    intent.putExtra("packageName", packageName);
                    intent.putExtra("APPName", APPName);
                    startActivity(intent);
                }else {
                    Toast.makeText(MainActivity.this, "请选择需要测试的应用程序", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    private class ListAdapter extends BaseAdapter {
        List<Programe> programes;
        Programe checkedProg;
        int lastCheckedPosition = -1;

        public ListAdapter() {
            programes = processInfo.getAllPackages(getBaseContext());
        }

        @Override
        public int getCount() {
            return programes.size();
        }

        @Override
        public Object getItem(int position) {
            return programes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Programe pr = (Programe) programes.get(position);
            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.list_item, parent, false);
            Viewholder holder = (Viewholder) convertView.getTag();
            if (holder == null) {
                holder = new Viewholder();
                convertView.setTag(holder);
                holder.imgViAppIcon = (ImageView) convertView.findViewById(R.id.image);
                holder.txtAppName = (TextView) convertView.findViewById(R.id.text);
                holder.rdoBtnApp = (RadioButton) convertView.findViewById(R.id.rb);
                holder.rdoBtnApp.setFocusable(false);
                holder.rdoBtnApp.setOnCheckedChangeListener(checkedChangeListener);
            }
            holder.imgViAppIcon.setImageDrawable(pr.getIcon());
            holder.txtAppName.setText(pr.getProcessName());
            holder.rdoBtnApp.setId(position);
            holder.rdoBtnApp.setChecked(checkedProg != null && getItem(position) == checkedProg);
            return convertView;
        }

        CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    final int checkedPosition = buttonView.getId();
                    if (lastCheckedPosition != -1) {
                        RadioButton tempButton = (RadioButton) findViewById(lastCheckedPosition);
                        if ((tempButton != null) && (lastCheckedPosition != checkedPosition)) {
                            tempButton.setChecked(false);
                        }
                    }
                    checkedProg = programes.get(checkedPosition);
                    lastCheckedPosition = checkedPosition;
                }
            }
        };
    }

    /**
     * save status of all installed processes
     *
     * @author andrewleo
     */
    static class Viewholder {
        TextView txtAppName;
        ImageView imgViAppIcon;
        RadioButton rdoBtnApp;
    }


}
