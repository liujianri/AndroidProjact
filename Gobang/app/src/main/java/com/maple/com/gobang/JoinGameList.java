package com.maple.com.gobang;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;


import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by 80087647 on 2016-04-28.
 */
public class JoinGameList extends Activity implements AdapterView.OnItemClickListener {

    private Button btnSearch,btnCreateGame;
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private ListView deviceListView;
    private List<String> deviceList = new ArrayList<String>();
    private ArrayAdapter<String> mAdapter;
    private boolean hasregister=false;
    private DeviceReceiver mydevice =new DeviceReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_game_list);
        inti();
        deviceListView = (ListView) findViewById(R.id.listView);
        mAdapter = new ArrayAdapter<String>(JoinGameList.this, android.R.layout.simple_list_item_1, deviceList);
        deviceListView.setAdapter(mAdapter);
        deviceListView.setOnItemClickListener(this);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnCreateGame = (Button) findViewById(R.id.btnCreatGame);

        btnCreateGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mBluetoothAdapter.isEnabled()){
                    inti();
                }else {
                    Intent intent = new Intent(JoinGameList.this, CreateGame.class);
                    if (mBluetoothAdapter.isDiscovering()) {
                        mBluetoothAdapter.cancelDiscovery();
                        btnSearch.setText(getString(R.string.repeat_search));
                    }
                    startActivity(intent);
                }
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                    btnSearch.setText(getString(R.string.repeat_search));
                } else {
                    if (!mBluetoothAdapter.isEnabled()){
                        inti();
                    }else {
                        findAvalibleDevice();
                        mBluetoothAdapter.startDiscovery();
                        btnSearch.setText(getString(R.string.stop_search));
                    }
                }


            }
        });
    }

    private void inti() {
        gtAd();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                //直接开启，不经过提示
                mBluetoothAdapter.enable();
            }
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, RESULT_FIRST_USER);
            //使蓝牙设备可见，方便配对
            Intent in = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            in.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 200);
            startActivity(in);

        } else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("No bluetooth devices");
            dialog.setMessage("Your equipment does not support bluetooth, please change device");

            dialog.setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            dialog.show();
        }


    }

    private void findAvalibleDevice() {
        Set<BluetoothDevice> device = mBluetoothAdapter.getBondedDevices();
        if (mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering()) {
            deviceList.clear();
            mAdapter.notifyDataSetChanged();
        }
        if (device.size() > 0) {
            for (Iterator<BluetoothDevice> it = device.iterator(); it.hasNext(); ) {
                BluetoothDevice btd = it.next();
                deviceList.add(btd.getName() + '\n' + btd.getAddress());
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        final String msg = deviceList.get(position);
        if(mBluetoothAdapter!=null&&mBluetoothAdapter.isDiscovering()){
            mBluetoothAdapter.cancelDiscovery();
            btnSearch.setText(getString(R.string.repeat_search));
        }
        String s = msg.substring(msg.length() - 17);
        Intent intent = new Intent(JoinGameList.this,JoinGame.class);
        intent.putExtra("device",s);
        startActivity(intent);
    }

    private class DeviceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action =intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){    //搜索到新设备
                BluetoothDevice btd=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //搜索没有配过对的蓝牙设备
                if (btd.getBondState() != BluetoothDevice.BOND_BONDED) {
                    deviceList.add(btd.getName()+'\n'+btd.getAddress());
                    mAdapter.notifyDataSetChanged();
                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){   //搜索结束

                if (deviceListView.getCount() == 0) {
                    deviceList.add(getString(R.string.No_can_be_matched_to_use_bluetooth));
                    mAdapter.notifyDataSetChanged();
                }
                btnSearch.setText(getString(R.string.repeat_search));
            }
        }
    }
    protected void onStart() {
        //注册广播
        if(!hasregister){
            hasregister=true;
            IntentFilter filterStart=new IntentFilter(BluetoothDevice.ACTION_FOUND);
            IntentFilter filterEnd=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(mydevice, filterStart);
            registerReceiver(mydevice, filterEnd);
        }
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mydevice);
        super.onDestroy();
    }

    BannerView bv;
    private boolean show = false;
    private void gtAd (){
        RelativeLayout bad = (RelativeLayout) findViewById(R.id.ad2);
        bv = new BannerView(JoinGameList.this, ADSize.BANNER, data.APPID, data.BannerPosID);
        bv.setRefresh(30);
        bv.setADListener(new AbstractBannerADListener() {

            @Override
            public void onNoAD(int arg0) {
                Log.i("AD_DEMO", "BannerNoAD，eCode=" + arg0);
            }

            @Override
            public void onADReceiv() {
                Log.i("AD_DEMO", "ONBannerReceive");
            }
        });
        bad.addView(bv);
        bv.loadAD();
        show = true;
    }
    InterstitialAD iad;
    private InterstitialAD getIAD() {
        if (iad == null) {
            iad = new InterstitialAD(JoinGameList.this, data.APPID, data.InterteristalPosID);
        }
        return iad;
    }

    private void showAD() {
        getIAD().setADListener(new AbstractInterstitialADListener() {

            @Override
            public void onNoAD(int arg0) {
                Log.i("AD_DEMO", "LoadInterstitialAd Fail:" + arg0);
            }

            @Override
            public void onADReceive() {
                Log.i("AD_DEMO", "onADReceive");
                iad.show();
            }
        });
        iad.loadAD();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            if (show) {
                showAD();
                show = false;
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
