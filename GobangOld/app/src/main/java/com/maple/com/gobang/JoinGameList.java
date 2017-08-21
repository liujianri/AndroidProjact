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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.baidu.mobads.AdView;
import com.baidu.mobads.AdViewListener;
import com.baidu.mobads.AppActivity;

import org.json.JSONObject;

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
    private AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_game_list);
        inti();
        AD();
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
        adView.destroy();
        super.onDestroy();
    }



    private void AD (){


        RelativeLayout yourOriginnalLayout = (RelativeLayout) findViewById(R.id.ad2);
        // setContentView(yourOriginnalLayout);
        // 代码设置AppSid，此函数必须在AdView实例化前调用
        // AdView.setAppSid("debug");

        // 设置'广告着陆页'动作栏的颜色主题
        // 目前开放了七大主题：黑色、蓝色、咖啡色、绿色、藏青色、红色、白色(默认) 主题
        AppActivity.setActionBarColorTheme(AppActivity.ActionBarColorTheme.ACTION_BAR_WHITE_THEME);
//        另外，也可设置动作栏中单个元素的颜色, 颜色参数为四段制，0xFF(透明度, 一般填FF)DE(红)DA(绿)DB(蓝)
//        AppActivity.getActionBarColorTheme().set[Background|Title|Progress|Close]Color(0xFFDEDADB);

        // 创建广告View
        String adPlaceId = "2992942"; //  重要：请填上您的广告位ID，代码位错误会导致无法请求到广告
        adView = new AdView(this, adPlaceId);
        // 设置监听器
        adView.setListener(new AdViewListener() {
            public void onAdSwitch() {
                Log.w("", "onAdSwitch");
            }

            public void onAdShow(JSONObject info) {
                // 广告已经渲染出来
                Log.w("", "onAdShow " + info.toString());
            }

            public void onAdReady(AdView adView) {
                // 资源已经缓存完毕，还没有渲染出来
                Log.w("", "onAdReady " + adView);
            }

            public void onAdFailed(String reason) {

                Log.w("", "onAdFailed " + reason);
            }

            public void onAdClick(JSONObject info) {
                // Log.w("", "onAdClick " + info.toString());

            }

            @Override
            public void onAdClose(JSONObject arg0) {
                Log.w("", "onAdClose");
            }
        });
        // 将adView添加到父控件中(注：该父控件不一定为您的根控件，只要该控件能通过addView能添加广告视图即可)
        RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        rllp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        yourOriginnalLayout.addView(adView, rllp);
    }
}
