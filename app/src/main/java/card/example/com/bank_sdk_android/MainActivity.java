package card.example.com.bank_sdk_android;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.OSInfo;
import util.AndoirdInfoUtil;
import util.HttpCallbackListener;
import util.HttpUtil;
import util.SystemUtil;

import static util.AndoirdInfoUtil.getMacAddress;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editText_PARTENER_KEY; //接入者key
    private EditText editText_SECERT_KEY;   //接入者scret
    private EditText editText_PRODUCT_KEY;  //产品key
    private EditText editText_SAVEPOINT_KEY;//保全点key
    private EditText editText_GROUP_KEY;    //企业/部门key

    private TelephonyManager mTelephonyManager;

    private static final int PERMISSIONS_REQUEST_CODE = 1;  //申请权限的requestCode
    private String publicIP;    //外网地址
    private LocationManager locationManager;
    private String locationProvider;


    //需要的权限数组
    String[] requirePermissions= {Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initButtno();   //初始化控件及对应值
        permission();   //权限检查，检查成功后


        Button button_sdk = (Button) findViewById(R.id.save_data);
        button_sdk.setOnClickListener(this);

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);


        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
//            // 检查该权限是否已经获取
//            int i = ContextCompat.checkSelfPermission(this, permissions[0]);
//            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
//            if (i != PackageManager.PERMISSION_GRANTED) {
//                // 如果没有授予该权限，就去提示用户请求
//                showDialogTipUserRequestPermission();
//            }
//        }
    }

    public void  getInfo(){
//        AndoirdInfoUtil.getAPPVersionCode(MainActivity.this);
//        String aaaaaaa = getLocation();
//
//        String netWorkInfo = AndoirdInfoUtil.getMAC(MainActivity.this).toString();
//        String ip = AndoirdInfoUtil.getIP(MainActivity.this);
//        String cpu = AndoirdInfoUtil.getCpuName();
//        String getDeviceId = getDeviceId(MainActivity.this);
//        String getDeviceSoftwareVersion = getDeviceSoftwareVersion(MainActivity.this);
//        String getSubscriberId = getSubscriberId(MainActivity.this);
//        String getLine1Number = getLine1Number(MainActivity.this);
//        String getSubscriberId222 = getSubscriberId222(MainActivity.this);
//        String getMEID = getMEID(MainActivity.this);
//        Map<String, String> map = new HashMap<>();
//        map.put("netWorkInfo", netWorkInfo);
//        map.put("ip", ip);
//        map.put("cpu", cpu);
//        map.put("getDeviceId", getDeviceId);
//        map.put("getDeviceSoftwareVersion", getDeviceSoftwareVersion);
//        map.put("getSubscriberId", getSubscriberId);
//        map.put("getLine1Number", getLine1Number);
//        map.put("getSubscriberId222", getSubscriberId222);
//        map.put("getMEID", getMEID);
//        map.put("publicIP", publicIP);
//        map.put("aaaaaaa", aaaaaaa);
//        map.put("AAAAAAAAAgetMacAddress", getMacAddress(MainActivity.this));
//
//
//
//        TextView response_test = (TextView) findViewById(R.id.response_test);
//        response_test.setText(map.toString());
    }

    //button单击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_data:

                if(publicIP.isEmpty()){
                    getPublicIP();
                }



//                String ip1 = AndoirdInfoUtil.getIPAddress(MainActivity.this).toString();
//                String ip2 = AndoirdInfoUtil.getMAC(MainActivity.this).toString(); //！！
//                String ip3 = AndoirdInfoUtil.getIP(MainActivity.this); //！！数据流量下仍然能获得
//                String ip4 = AndoirdInfoUtil.getCpuName();//!!
//
//                String text = "ip1" + ip1 + "ip2" + ip2 + "ip3" + ip3 + "cpu1" + ip4 + "getDeviceId"
//                        + getDeviceId + "getDeviceSoftwareVersion" + getDeviceSoftwareVersion;
//                sendHttpRequest();


                break;

            default:
                break;
        }
    }

    //初始化 活动编辑框并赋值
    private void initButtno() {
        editText_PARTENER_KEY = (EditText) findViewById(R.id.PARTNER_KEY);
        editText_PARTENER_KEY.setText("24dd625e52c3669b8aaa802989ee6988");

        editText_SECERT_KEY = (EditText) findViewById(R.id.SECRET_KEY);
        editText_SECERT_KEY.setText("6b3feea4db1932b79c59c676474bb18a1fd34155");

        editText_PRODUCT_KEY = (EditText) findViewById(R.id.ProductKey);
        editText_PRODUCT_KEY.setText("I-00010001");

        editText_SAVEPOINT_KEY = (EditText) findViewById(R.id.SavePointKey);
        editText_SAVEPOINT_KEY.setText("X-00010001");

        editText_GROUP_KEY = (EditText) findViewById(R.id.GroupKey);
        editText_GROUP_KEY.setText("G-00010000");

    }

    public void  initOSinfo(){
        Map<String,String> netWorkInfo = AndoirdInfoUtil.getMAC(MainActivity.this);
        Map<String,String> IMEI = getIMEI(MainActivity.this);

        OSInfo osInfo = new OSInfo();
        osInfo.setPublicIP(publicIP);
        osInfo.setIp(netWorkInfo.get("ipAddress"));
        osInfo.setNetMask(netWorkInfo.get("netmask"));
        osInfo.setGateway(netWorkInfo.get("gateway"));
        osInfo.setServerAddress(netWorkInfo.get("serverAddress"));
        osInfo.setDns1(netWorkInfo.get("dns1"));
        osInfo.setDns2(netWorkInfo.get("dns2"));
        osInfo.setIMEI1(IMEI.get("IMEI1"));
        osInfo.setIMEI2(IMEI.get("IMEI2"));
        osInfo.setMAC(getMacAddress(MainActivity.this));
        osInfo.setCpuVersion(AndoirdInfoUtil.getCpuName());
        osInfo.setLocation(getLocation());
        osInfo.setClientPushTime(new Date());
        osInfo.setAndroidVersion(AndoirdInfoUtil.getSystemVersion());
        osInfo.setAppVersionName(  AndoirdInfoUtil.getAPPVersionName(MainActivity.this));



        TextView response_test = (TextView) findViewById(R.id.response_test);
        response_test.setText(osInfo.toString() + getMEID(MainActivity.this));

    }


    //推送数据
    private void sendHttpRequest() {
        new Thread() {
            @Override
            public void run() {

                EditText value1EditText = (EditText) findViewById(R.id.value1);
                String value1 = value1EditText.getText().toString();
                EditText value2EditText = (EditText) findViewById(R.id.value2);
                String valve2 = value2EditText.getText().toString();


                String url = "http://10.14.19.32:9001/interfaceService/testAndroid";
                String PARTENER_KEY = editText_PARTENER_KEY.getText().toString();
                String SECERT_KEY = editText_SECERT_KEY.getText().toString();
                String PRODUCT_KEY = editText_PRODUCT_KEY.getText().toString();
                String SAVEPOINT_KEY = editText_SAVEPOINT_KEY.getText().toString();
                String GROUP_KEY = editText_GROUP_KEY.getText().toString();
                String osInfo = "5d000000010000848de769c60f43d0a82c92207c9db6d7e34c10cf14f9093b8993dfd2d1baa498626f7952ad1b068d8f33fa3d3fc4bb970d7e685a3559ab0b6010c5264ad266e661c0f3bf9870914e7a59fa6e0c143c1dd175f82d1e2dae8d2245c787a237073e16ed9a16d37df21c88099ff52c217d5a02a4b6598c29c2a92f115e4b6fa29f62b60a3800";
                String clientIp = AndoirdInfoUtil.getIP(MainActivity.this);
                Map<String, String> map = new HashMap<>();
                map.put("PARTENER_KEY", PARTENER_KEY);
                map.put("SECERT_KEY", SECERT_KEY);
                map.put("PRODUCT_KEY", PRODUCT_KEY);
                map.put("SAVEPOINT_KEY", SAVEPOINT_KEY);
                map.put("GROUP_KEY", GROUP_KEY);
                map.put("osInfo", osInfo);
                map.put("clientIp", clientIp);
                map.put("value1", value1);
                map.put("valve2", valve2);

                StringBuffer sb = new StringBuffer();
                sb.append("?");
                if (map != null && !map.isEmpty()) {
                    for (Map.Entry<String, String> entry : map.entrySet()) {    //增强for遍历循环添加拼接请求内容
                        sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                }

                String path = url + sb.toString();

                HttpUtil.sendHttpRequest(path, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        showResponse(response);
                    }

                    @Override
                    public void onError(Exception e) {
                        showResponse(e.toString());
                    }
                });
            }
        }.start();
    }


    //获得公网IP
    private void getPublicIP() {
        new Thread() {
            @Override
            public void run() {   //开启线程
                String url = "http://ip.chinaz.com/getip.aspx";
                String path = url;
                HttpUtil.sendHttpRequest(path, new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        try {
                            publicIP = new JSONObject(response).get("ip").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }
        }.start();
    }


    //推送结果回显
    private void showResponse(final String response) {
        runOnUiThread(new Runnable() {  //更新主线程
            @Override
            public void run() {
                TextView response_test = (TextView) findViewById(R.id.response_test);
                response_test.setText(response);
            }
        });
    }


    public TelephonyManager getTelephonyManager(Context context) {
        // 获取telephony系统服务，用于取得SIM卡和网络相关信息
        if (mTelephonyManager == null) {
            mTelephonyManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
        }
        return mTelephonyManager;
    }


    /**
     * 唯一的设备ID： GSM手机的 IMEI 和 CDMA手机的 MEID
     * 取得手机IMEI
     * available.
     */
    public String getDeviceId(Context context) {
        String mDeviceId = getTelephonyManager(context).getDeviceId();
        return mDeviceId;
    }

    /**
     * 取得IMEI SV
     * @param context
     * @return
     */
    public String getDeviceSoftwareVersion(Context context) {
        String mDeviceSoftwareVersion = getTelephonyManager(context).getDeviceSoftwareVersion();
        return mDeviceSoftwareVersion;
    }

    /**
     * 取得手机IMSI
     * @param context
     * @return
     */
    public String getSubscriberId(Context context) {
        String mSubscriberId = getTelephonyManager(context).getSubscriberId();
        return mSubscriberId;
    }

    /**
     * 获取手机MEID
     * @param context
     * @return
     */
    public String getMEID(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String meid1 = getTelephonyManager(context).getMeid(1);
            String meid0 = getTelephonyManager(context).getMeid(0);
            String meid2 = getTelephonyManager(context).getMeid(2);



            TelephonyManager manager= (TelephonyManager) MainActivity.this.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);


            Method method = null;
            try {
                method = manager.getClass().getMethod("getDeviceId", int.class);
                String imei1 = manager.getDeviceId();

                String imei2 = (String) method.invoke(manager, 1);

                String meid = (String) method.invoke(manager, 2);
            } catch (Exception e) {
                e.printStackTrace();
            }




            return "MEID= " + meid0 + meid1 + meid2;
        }
        return null;
    }

    public Map<String,String> getIMEI(Context context) {


        try {
            Map<String,String> map = new HashMap<>();
            Method method = mTelephonyManager.getClass().getMethod("getDeviceId", int.class);

            String imei1 = mTelephonyManager.getDeviceId();

            String imei2 = (String) method.invoke(mTelephonyManager, 1);
            map.put("IMEI1",imei1);
            map.put("IMEI2",imei2);
            return map;
        } catch (Exception e) {

        }
        return null;

    }

    /**
     * 获得手机号
     * @param context
     * @return
     */
    public String getLine1Number(Context context) {
        String mSubscriberId = getTelephonyManager(context).getLine1Number();
        return mSubscriberId;
    }


    /**
     * 获得权限
     */
    private void permission() {

        //未授权的权限集合
        List<String> unPsermissions = new ArrayList<>();
        for(int i= 0; i< requirePermissions.length;i++){
            if(ContextCompat.checkSelfPermission(this,requirePermissions[i]) != PackageManager.PERMISSION_GRANTED){
                unPsermissions.add(requirePermissions[i]);
            }
        }
        if(unPsermissions.isEmpty()){ //为空表示都授权了
            Toast.makeText(this, "权限都授予了", Toast.LENGTH_SHORT).show();
            initOSinfo();
        }else{

            //准备授权的数组
            String[] readyPermissions =  unPsermissions.toArray(new String[unPsermissions.size()]);
            ActivityCompat.requestPermissions(this, readyPermissions, PERMISSIONS_REQUEST_CODE);
        }


//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            for (String permission : permissions) {
//
//            }
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
//                //当拒绝了授权后，为提升用户体验，可以以弹窗的方式引导用户到设置中去进行设置
//                showSetting("获取手机号码、IMEI、IMSI");
//            } else {
//                //没有授权
//                ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_CODE);
//            }
//        } else {
//            //已经授权
//
//            call();
//        }
    }

    /**
     * 如果第一次拒绝，后面几次引导设置
     *
     * @param name
     */
    private void showSetting(String name) {
        //当拒绝了授权后，为提升用户体验，可以以弹窗的方式引导用户到设置中去进行设置
        new AlertDialog.Builder(MainActivity.this)
                .setMessage("应用需要开启" + name + "权限")
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //引导用户到设置中去进行设置
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", getPackageName(), null));
                        startActivity(intent);

                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    /**
     * 请求权限后的回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        Boolean flag = true;
//        switch (requestCode) {
//            case 1:
//                for (int i = 0; i < grantResults.length; i++) {
//                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                        //判断是否勾选禁止后不再询问
//                        boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permissions[i]);
//                        if (showRequestPermission && flag) {//
//                            flag = false;
//                            showSetting("获取手机号码、IMEI、定位");//重新申请权限
//                            return;
//                        } else {
////                            mShowRequestPermission = false;//已经禁止
//                        }
//                    }
//                }
////                delayEntryPage();
//                break;
//            default:
//                break;
//        }
//    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    List<String> deniedPermissionList = new ArrayList<>();
                    boolean countFlag = true;
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissionList.add(permissions[i]);
                            if(countFlag){
//                                Toast.makeText(this, "请开启相关权限", Toast.LENGTH_SHORT).show();
                            }
                            countFlag = false; //控制弹窗只显示一次
                        }
                    }
                    if (deniedPermissionList.isEmpty()) {
                        //已经全部授权
                        call();
                    } else {


                        //勾选了对话框中”Don’t ask again”的选项, 返回false
                        for (String deniedPermission : deniedPermissionList) {
                            boolean flag = false;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                                flag = shouldShowRequestPermissionRationale(deniedPermission);
                            }
                            if (!flag) {
                                //拒绝授权
                                showSetting("获取手机号码、IMEI、定位");//重新申请权限
                                return ;
                            }
                        }
                        //拒绝授权
                        showSetting("获取手机号码、IMEI、定位");//重新申请权限

                        //其他逻辑(这里当权限都同意的话就执行打电话逻辑)
                    }
                }
                break;
            default:
                break;
        }
    }

    public void call() {
    }


    public String getLocation() {
        //获取地理位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
            return "";
        }
        //权限判断
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        //获取Location
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            return location.getLongitude() + "," + location.getLatitude();
        }
        //监视地理位置变化
//        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);

        return "";
    }


}
