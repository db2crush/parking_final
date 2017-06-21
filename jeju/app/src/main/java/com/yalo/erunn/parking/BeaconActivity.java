package com.yalo.erunn.parking;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.kakao.kakaonavi.KakaoNaviParams;
import com.kakao.kakaonavi.KakaoNaviService;
import com.kakao.kakaonavi.Location;
import com.kakao.kakaonavi.NaviOptions;
import com.kakao.kakaonavi.options.CoordType;
import com.kakao.kakaonavi.options.RpOption;
import com.kakao.kakaonavi.options.VehicleType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by erunn on 2017-06-20.
 */

public class BeaconActivity extends AppCompatActivity {
    private BeaconManager beaconManager;
    private Region region;
    private boolean isConnected;
    private TextView location;
    private TextView distance;
    boolean flag;
    private Intent intent;
    private String name, free;
    private Date date;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private String formateDate;
    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beacon);

        intent = getIntent();
        name = intent.getStringExtra("name");
        free = intent.getStringExtra("free");

        distance = (TextView) findViewById(R.id.beacon_distance);
        location = (TextView) findViewById(R.id.beacon_location);
        location.setText(name);
        button = (Button) findViewById(R.id.btn_navi);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert_navi_confirm = new AlertDialog.Builder(BeaconActivity.this);
                alert_navi_confirm.setTitle("네비게이션 길 찾기");
                alert_navi_confirm.setMessage("이용하시겠습니까?").setCancelable(false).setPositiveButton("네",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                flag = false;
//                                              Location destination = Location.newBuilder(name, latitude, longitude).build();
                                Location destination = Location.newBuilder("카카오 판교 오피스", 127.10821222694533, 37.40205604363057).build();
                                NaviOptions options = NaviOptions.newBuilder().setCoordType(CoordType.WGS84).setVehicleType(VehicleType.FIRST).setRpOption(RpOption.SHORTEST).build();
                                KakaoNaviParams.Builder builder = KakaoNaviParams.newBuilder(destination).setNaviOptions(options);
                                KakaoNaviService.navigate(BeaconActivity.this, builder.build());
                            }
                        }).setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                flag = true;
                                // 'No'
                                return;
                            }
                        });
                AlertDialog alert = alert_navi_confirm.create();
                alert.show();
            }
        });

        region = new Region("ranged region",
                UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 40259, 11605);
        // 'YES'



        beaconManager = new BeaconManager(this);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                Log.v("beacon", " onServiceReady");
                beaconManager.startMonitoring(region);
            }
        });

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon beacon = list.get(0);
                    if (!isConnected && beacon.getRssi() > -70) {
                        beaconManager.stopRanging(region);
                        isConnected = true;
                        Log.v("beacon", "near beacon");

                        AlertDialog.Builder dialog = new AlertDialog.Builder(BeaconActivity.this);
                        dialog.setTitle("알림")
                                .setMessage("주차장에 근접하였습니다.").setCancelable(false).setPositiveButton("확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        long now = System.currentTimeMillis();
                                        date = new Date(now);
                                        formateDate = simpleDateFormat.format(date);
                                        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(BeaconActivity.this);
                                        alert_confirm.setTitle("'" + name + "'" + " 주차 하시겠습니까?");
                                        alert_confirm.setMessage("주차시각: " + formateDate).setCancelable(false).setPositiveButton("네",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(BeaconActivity.this, ParkingActivity.class);
                                                        intent.putExtra("name", name);
                                                        intent.putExtra("checkin",formateDate);
                                                        intent.putExtra("free",free);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }).setNegativeButton("취소",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        // 'No'
                                                        return;
                                                    }
                                                });
                                        AlertDialog alert = alert_confirm.create();
                                        alert.show();
                                    }
                                }).create().show();
                    } else if (beacon.getRssi() < -70) {
                        Toast.makeText(BeaconActivity.this, "주차장에 가까이 가주세요", Toast.LENGTH_SHORT).show();
                        distance.setText(beacon.getRssi() + "");
                        Log.v("beacon", "far beacon");
                        isConnected = false;
                    }
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        beaconManager.stopRanging(region);
        super.onDestroy();
    }
}
