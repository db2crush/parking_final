package com.yalo.erunn.parking;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by erunn on 2017-06-10.
 */

public class ParkingActivity extends AppCompatActivity {
    private static final String TAG = "ParkingActivity";
    private static final int REQUEST_PARKING = 0;
    private Intent intent;
    private String name;
    private String checkin;
    private String free;
    private TextView parking_name, parking_checkin, parking_checkout, parking_price;
    private Button parking_quit, parking_condition;
    private Date date;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private String formateDate;
    private ImageView parking_image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking);

        long now = System.currentTimeMillis();
        date = new Date(now);
        formateDate = simpleDateFormat.format(date);


        intent = getIntent();
        name = intent.getStringExtra("name");
        checkin = intent.getStringExtra("checkin");
        free = intent.getStringExtra("free");

        parking_name = (TextView) findViewById(R.id.parking_name);
        parking_checkin = (TextView) findViewById(R.id.parking_checkin);
        parking_checkout = (TextView) findViewById(R.id.parking_checkout);
        parking_price = (TextView) findViewById(R.id.parking_price);

        parking_image = (ImageView)findViewById(R.id.parking_image);

        parking_name.setText(name);
        parking_checkin.setText(checkin);

        if(free ==null){
            parking_image.setImageResource(R.drawable.yalo);
        }

        if (free != null) {
            parking_checkout.setText(formateDate);
            if (free.equals("무료"))
                parking_price.setText(free);
            else
                parking_price.setText("1000원");
        }


        parking_condition = (Button) findViewById(R.id.parking_condition);
        parking_condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "정보 없음";
                AlertDialog.Builder alert = new AlertDialog.Builder(ParkingActivity.this);
                alert.setTitle(name);
                if (free != null) {
                    if (free.equals("무료")) {
                        text = free;
                    } else {
                        text = "기본요금(30분) : 1000원\n추가요금(10분) : 500원";
                    }
                }

                alert.setMessage(text).setCancelable(false).setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;

                            }
                        });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();

            }
        });


        parking_quit = (Button) findViewById(R.id.parking_quit);
        parking_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = "정보 없음";
                final AlertDialog.Builder alert_confirm = new AlertDialog.Builder(ParkingActivity.this);
                alert_confirm.setTitle("주차 종료하시겠습니까?");
                if (free != null) {
                    if (free.equals("무료"))
                        text = free;
                    else
                        text = "1000원";
                }

                final String finalText = text;
                alert_confirm.setMessage("지불금액: " + text).setCancelable(false).setPositiveButton("네",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog progressDialog = new ProgressDialog(ParkingActivity.this,
                                        R.style.AppTheme_Dark_Dialog);
                                progressDialog.setIndeterminate(true);
                                progressDialog.setMessage("비콘페이 지불...");
                                progressDialog.show();
                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                progressDialog.dismiss();
                                                AlertDialog.Builder alert = new AlertDialog.Builder(ParkingActivity.this);
                                                alert.setTitle("승인");
                                                alert.setMessage("금액: " + finalText).setCancelable(false).setPositiveButton("확인",
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                Intent returnIntent = new Intent();
                                                                setResult(0, returnIntent);
                                                                finish();
                                                            }
                                                        });
                                                AlertDialog alerts = alert.create();
                                                alerts.show();
                                            }
                                        }, 3000);
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
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        long now = System.currentTimeMillis();
        date = new Date(now);
        if (free != null)
            parking_checkout.setText(formateDate);
    }
}
