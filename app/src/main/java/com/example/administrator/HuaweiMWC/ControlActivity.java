package com.example.administrator.HuaweiMWC;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Environment;
import android.os.Messenger;
import android.os.Process;
import android.os.RemoteException;
import android.os.StatFs;
import android.print.PageRange;
import android.print.PrintJob;
import android.print.PrintJobInfo;
import android.print.PrintManager;
import android.provider.Settings;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.os.Message;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android_serialport_api.SerialPortFinder;


public class ControlActivity extends AppCompatActivity {
//    private static final int REQUEST_CODE_BLUETOOTH_ON = 1313;
//    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 100;
//    private static final int BLUETOOTH_DISCOVERABLE_DURATION = 120;
    public final static int CONTROLACTIVITY_SRART = 0;
    public final static int CONTROLACTIVITY_SERIALPORTSRART = 1;
    public final static int CONTROLACTIVITY_SERIALPORTEND = 2;
    public Messenger servuceHandler;
    public Handler activityHandler;
    private static AssetManager assetManager;
    private static ProgressDialog waitingDialog;
    private static ProgressDialog mProgressDialog;
    private BluetoothAdapter adapter;
    private String TAG = "ControlActivity";
    public static final int UPDATE_COMPLETE = 1;
    public static final int GETDATA_COMPLETE = 2;
    public static final int BLUETOOTHLE_COMPLETE = 3;
    private static final int NAMEPLATEF = 10;
    private static final int CONTROLLERF = 11;
    private static final int FULLSCREENF = 12;
    private static final int VIDEOF = 13;
    private static final int DRESSUP = 14;
    private static final int PRINTF = 15;
    private static final int SELECTF = 16;
    private static final int RECOGNIZE = 17;
    private static final int FAST = 18;
    private boolean isAnimationPlay;
    private static int curreUI=0;
    private static int curreBtn=0;
    private static int curreImg=0;

    private SerialPortEntity sp;
    private static SurfaceViewControl sfc;
    private  SurfaceView sv;
    private static FrameLayout controllerF;
    private static FrameLayout nameplateF;
    private static FrameLayout welcomeF;
    private static FrameLayout endF;
    private static boolean isStart;
    private static boolean isEnd;
    //Mate10、pro、保时捷 Hall
    private static FrameLayout recognizeF;
    private static FrameLayout fastF;
    //Mate10 Lite Hall 1、3
    private static FrameLayout fullScreenF;
    private static FrameLayout selectF;
    //Nova2、2P
    private static FrameLayout shootF;
    //Watch2
    private static FrameLayout callF;
    //通用
    private static FrameLayout dressUpF;
    private static FrameLayout videoF;
    //铭牌
    private Button bjBtn;
    private BitmapDrawable nameplateFB;
    private BitmapDrawable controllerFB;
    private ImageView bj1;
    private ImageView bj2;
    private ImageView bj3;
    //控制UI
    private ImageView title;
    private ImageView title1;
    private Button recognizeBtn;
    private Button fastBtn;
    private Button videoBtn;
    private Button dressUpBtn;
    private Button back;
    //慧眼识物
    private ImageView recognizeImg1;
    private ImageView recognizeImg2;
    private Button recognizeEBtn;
    private ImageView recognizeImg3;
    private ImageView recognizeImg4;
    private ImageView recognizeImg5;
    private ImageView recognizeImg6;
    private ImageView recognizeImg7;
    private ImageView recognizeImg8;
    private ImageView recognizeImg9;
    private ImageView recognizeImg10;
    private ImageView recognizeImg11;
    private ImageView recognizeImg12;
    private int recognizeType;
    //快充
    private Button tab1;
    private Button tab2;
    private ImageView ivGif1;
    private ImageView ivGif2;
    private ImageView ivGif3;
    private ImageView ivGif4;
    private ImageView image1;
    private ImageView image2;
    private ImageView image3;
    private ImageView image4;
    private ImageView image5;
    private ImageView image6;
    private ImageView image7;
    //换装
    private ImageView title2;
    private ImageView title3;
    private Button color1;
    private Button color2;
    private Button color3;
    private Button color4;
    private final long durationMillis=300;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.i("ControlActivity", "onCreate");

        //蓝牙
        adapter = BluetoothAdapter.getDefaultAdapter();

//        mProgressDialog = ProgressDialog.show(ControlActivity.this, "请等待", "Loading......", true);

        assetManager = this.getAssets();
        controllerF = (FrameLayout) findViewById(R.id.controllerF);
        nameplateF = (FrameLayout) findViewById(R.id.nameplateF);
        welcomeF = (FrameLayout) findViewById(R.id.welcomeF);
        endF = (FrameLayout) findViewById(R.id.endF);
        //Mate10、pro、保时捷 Hall
        recognizeF = (FrameLayout) findViewById(R.id.recognizeF);
        fastF = (FrameLayout) findViewById(R.id.fastF);
        //Mate10 Lite Hall 1、3
        fullScreenF = (FrameLayout) findViewById(R.id.fullScreenF);
        selectF = (FrameLayout) findViewById(R.id.selectF);
        //Nova2、2P
        shootF = (FrameLayout) findViewById(R.id.shootF);
        //Watch2
        callF = (FrameLayout) findViewById(R.id.callF);
        //通用
        videoF = (FrameLayout) findViewById(R.id.videoF);
        dressUpF = (FrameLayout) findViewById(R.id.dressUpF);


        //铭牌
        bj1 = (ImageView)findViewById(R.id.bj1);
        bj2 = (ImageView)findViewById(R.id.bj2);
        bj3 = (ImageView)findViewById(R.id.bj3);
        //控制UI
        title = (ImageView)findViewById(R.id.title);
        title1 = (ImageView)findViewById(R.id.title1);
        recognizeBtn = (Button)findViewById(R.id.recognizeBtn);
        fastBtn = (Button)findViewById(R.id.fastBtn);
        videoBtn = (Button)findViewById(R.id.videoBtn);
        dressUpBtn = (Button)findViewById(R.id.dressUpBtn);
        //慧眼识物
        recognizeEBtn= (Button)findViewById(R.id.recognizeEBtn);
        recognizeImg1 = (ImageView)findViewById(R.id.recognizeImg1);
        recognizeImg2 = (ImageView)findViewById(R.id.recognizeImg2);
        recognizeImg3 = (ImageView)findViewById(R.id.recognizeImg3);
        recognizeImg4 = (ImageView)findViewById(R.id.recognizeImg4);
        recognizeImg5 = (ImageView)findViewById(R.id.recognizeImg5);
        recognizeImg6 = (ImageView)findViewById(R.id.recognizeImg6);
        recognizeImg7 = (ImageView)findViewById(R.id.recognizeImg7);
        recognizeImg8 = (ImageView)findViewById(R.id.recognizeImg8);
        recognizeImg9 = (ImageView)findViewById(R.id.recognizeImg9);
        recognizeImg10 = (ImageView)findViewById(R.id.recognizeImg10);
        recognizeImg11 = (ImageView)findViewById(R.id.recognizeImg11);
        recognizeImg12 = (ImageView)findViewById(R.id.recognizeImg12);
        //快充
        tab1 = (Button)findViewById(R.id.tab1);
        tab2 = (Button)findViewById(R.id.tab2);
        image1 = (ImageView)findViewById(R.id.image1);
        image2 = (ImageView)findViewById(R.id.image2);
        image3 = (ImageView)findViewById(R.id.image3);
        image4 = (ImageView)findViewById(R.id.image4);
        image5 = (ImageView)findViewById(R.id.image5);
        image6 = (ImageView)findViewById(R.id.image6);
        image7 = (ImageView)findViewById(R.id.image7);
        ivGif1 = (ImageView) findViewById(R.id.ivGif1);
        ivGif2 = (ImageView) findViewById(R.id.ivGif2);
        ivGif3 = (ImageView) findViewById(R.id.ivGif3);
        ivGif4 = (ImageView) findViewById(R.id.ivGif4);
        //换装
        title2 = (ImageView)findViewById(R.id.title2);
        title3 = (ImageView)findViewById(R.id.title3);
        color1 = (Button)findViewById(R.id.color1);
        color2 = (Button)findViewById(R.id.color2);
        color3 = (Button)findViewById(R.id.color3);
        color4 = (Button)findViewById(R.id.color4);



        //电子名牌
        bjBtn = (Button)findViewById(R.id.bjBtn);
        bjBtn.setVisibility(View.INVISIBLE);
        bjBtn.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        nameplateF.setVisibility(View.VISIBLE);
                        setAlphaAnimation(nameplateF);
                        break;
                    case MotionEvent.ACTION_UP:
                        nameplateF.setVisibility(View.INVISIBLE);
//                        bjBtn.setVisibility(View.GONE);
//                        nameplateF.setVisibility(View.GONE);
//                        if(curreUI == NAMEPLATEF) {
//                            welcomeF.setVisibility(View.VISIBLE);
//                            new Handler().postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    changeF(CONTROLLERF);
//                                    bjBtn.setBackground(nameplateFB);
//                                }
//                            },2000);
//                        }
//                        else if(curreUI == CONTROLLERF) {
//                            changeF(NAMEPLATEF);
//                            bjBtn.setBackground(controllerFB);
//                        }
//                        break;
                    default:
                        break;
                }
                return true;
            }
        });
//        nameplateF.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bjBtn.setVisibility(View.GONE);
//                nameplateF.setVisibility(View.GONE);
//                welcomeF.setVisibility(View.VISIBLE);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        changeF(CONTROLLERF);
//                        bjBtn.setBackground(nameplateFB);
//                    }
//                },2000);
//            }
//        });
        //Mate10、pro、保时捷 Hall
        //慧眼识物按钮
        recognizeBtn.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        changeF(RECOGNIZE);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        final FrameLayout recognizeM = (FrameLayout)findViewById(R.id.recognizeM);
        recognizeImg3.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        if(isAnimationPlay)
                            break;
                        if(recognizeType == 2)
                            setRecognizeImg(65,400);
                        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) recognizeM.getLayoutParams();
                        if(curreImg == 6){
                            setTranslateAnimation(recognizeM, false,
                                    -750, 0,
                                    mlp.leftMargin, 405, 0, 10,
                                    -1790 - (-750 - 50), 405, 0, 60,
                                    new AnimationCallback() {
                                        @Override
                                        public void end() {
                                            super.end();
                                            curreImg = 3;
                                            if(recognizeType == 2)
                                                setRecognizeImg(0,530);
                                        }
                                    });
                        }else {
                            setTranslateAnimation(recognizeM,false,
                                    -750,0,
                                    mlp.leftMargin,405,0,10,
                                    mlp.leftMargin - 750 - 50,405,0,60,
                                    new AnimationCallback() {
                                        @Override
                                        public void end() {
                                            super.end();
                                            if(recognizeType == 2)
                                                setRecognizeImg(0,530);
                                        }
                                    });
                            curreImg ++;
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        recognizeImg4.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        if(isAnimationPlay)
                            break;
                        if(recognizeType == 2)
                            setRecognizeImg(65,400);
                        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) recognizeM.getLayoutParams();
                        if(curreImg == 3){
                            setTranslateAnimation(recognizeM, false,
                                    750, 0,
                                    mlp.leftMargin, 405, 0, 10,
                                    -1790 + (-750 - 50) * 2, 405, 0, 60,
                                    new AnimationCallback() {
                                        @Override
                                        public void end() {
                                            super.end();
                                            curreImg = 6;
                                            if(recognizeType == 2)
                                                setRecognizeImg(0,530);
                                        }
                                    });
                        }else {
                            setTranslateAnimation(recognizeM, false,
                                    750, 0,
                                    mlp.leftMargin, 405, 0, 10,
                                    mlp.leftMargin + 750 + 50, 405, 0, 60,
                                    new AnimationCallback() {
                                        @Override
                                        public void end() {
                                            super.end();
                                            if(recognizeType == 2)
                                                setRecognizeImg(0,530);
                                        }
                                    });
                            curreImg--;
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        recognizeEBtn.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        if(recognizeImg2.getVisibility() == View.VISIBLE) {
//                            recognizeEBtn.setBackgroundResource(R.mipmap.mate10recognize_6);
                            recognizeEBtn.setBackgroundResource(R.mipmap.mate10recognize_8);
                            recognizeImg2.setVisibility(View.INVISIBLE);
                            recognizeType=2;
                            setRecognizeImg(0,530);
                        }
                        else {
//                            recognizeEBtn.setBackgroundResource(R.mipmap.mate10recognize_5);
                            recognizeEBtn.setBackgroundResource(R.mipmap.mate10recognize_7);
                            recognizeImg2.setVisibility(View.VISIBLE);
                            recognizeType=1;
                            setRecognizeImg(65,400);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        //快充按钮
        fastBtn.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        changeF(FAST);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        ivGif1.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        ScaleAnimation animation = new ScaleAnimation( 1.0f, 1.3f, 1.0f, 1.3f,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);;
                        animation.setDuration(durationMillis-200);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                switch (curreBtn){
                                    case 1:
                                        ivGif2.setVisibility(View.INVISIBLE);
                                        image1.clearAnimation();
                                        image1.invalidate();
                                        image1.setVisibility(View.INVISIBLE);
                                        ivGif3.setVisibility(View.VISIBLE);
                                        image2.setVisibility(View.VISIBLE);
                                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) ivGif1.getLayoutParams();
                                        lp.setMargins(0, 800, 100, 0);
                                        ivGif1.setLayoutParams(lp);
                                        curreBtn = 2;
                                        break;
                                    case 2:
                                        ivGif1.clearAnimation();
                                        ivGif1.invalidate();
                                        ivGif1.setVisibility(View.INVISIBLE);
                                        image3.setVisibility(View.VISIBLE);
                                        setAlphaAnimation(image3);
                                        break;
                                    case 3:
                                        image6.setVisibility(View.VISIBLE);
                                        setAlphaAnimation(image6);
                                        image7.setVisibility(View.VISIBLE);
                                        setAlphaAnimation(image7);
                                        ivGif1.clearAnimation();
                                        ivGif1.invalidate();
                                        ivGif1.setVisibility(View.INVISIBLE);
                                        break;
                                }
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                        ivGif1.startAnimation(animation);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        tab1.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        tab2.setBackgroundResource(R.mipmap.tab2);
                        tab1.setBackgroundResource(R.mipmap.tab1_h);
                        curreBtn=1;
                        ivGif1.clearAnimation();
                        ivGif1.invalidate();
                        ivGif1.setVisibility(View.VISIBLE);
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) ivGif1.getLayoutParams();
                        lp.setMargins(0, 330, 310, 0);
                        ivGif1.setLayoutParams(lp);
                        ivGif2.setVisibility(View.VISIBLE);
                        ivGif3.setVisibility(View.INVISIBLE);
                        ivGif4.setVisibility(View.INVISIBLE);
                        image1.setVisibility(View.VISIBLE);
                        image2.setVisibility(View.INVISIBLE);
                        image3.clearAnimation();
                        image3.invalidate();
                        image3.setVisibility(View.INVISIBLE);
                        image4.clearAnimation();
                        image4.invalidate();
                        image4.setVisibility(View.INVISIBLE);
                        image5.clearAnimation();
                        image5.invalidate();
                        image5.setVisibility(View.INVISIBLE);
                        image6.clearAnimation();
                        image6.invalidate();
                        image6.setVisibility(View.INVISIBLE);
                        image7.clearAnimation();
                        image7.invalidate();
                        image7.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        tab2.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        tab1.setBackgroundResource(R.mipmap.tab1);
                        tab2.setBackgroundResource(R.mipmap.tab2_h);
                        ivGif1.clearAnimation();
                        ivGif1.invalidate();
                        ivGif1.setVisibility(View.INVISIBLE);
                        ivGif2.setVisibility(View.INVISIBLE);
                        ivGif3.setVisibility(View.INVISIBLE);
                        ivGif4.setVisibility(View.INVISIBLE);
                        image1.clearAnimation();
                        image1.invalidate();
                        image1.setVisibility(View.INVISIBLE);
                        image2.clearAnimation();
                        image2.invalidate();
                        image2.setVisibility(View.INVISIBLE);
                        image3.clearAnimation();
                        image3.invalidate();
                        image3.setVisibility(View.INVISIBLE);
                        image4.clearAnimation();
                        image4.invalidate();
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) image4.getLayoutParams();
                        lp.setMargins(750, 310, 0, 0);
                        image4.setLayoutParams(lp);
                        image4.setVisibility(View.VISIBLE);
                        image5.clearAnimation();
                        image5.invalidate();
                        image5.setVisibility(View.INVISIBLE);
                        image6.clearAnimation();
                        image6.invalidate();
                        image6.setVisibility(View.INVISIBLE);
                        image7.clearAnimation();
                        image7.invalidate();
                        image7.setVisibility(View.INVISIBLE);
                        curreBtn = 3;
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        image4.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        setTranslateAnimation(image4, true,
                                -450, 0,
                                0, 0, 0, 0,
                                0, 0, 0, 0,
                                new AnimationCallback() {
                                    @Override
                                    public void end() {
                                        super.end();
                                        image4.clearAnimation();
                                        image4.invalidate();
                                        image4.setVisibility(View.INVISIBLE);
                                        ivGif4.setVisibility(View.VISIBLE);
                                        ivGif1.clearAnimation();
                                        ivGif1.invalidate();
                                        ivGif1.setVisibility(View.VISIBLE);
                                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) ivGif1.getLayoutParams();
                                        lp.setMargins(0, 800, 100, 0);
                                        ivGif1.setLayoutParams(lp);
                                        setAlphaAnimation(ivGif1);
                                        image5.setVisibility(View.VISIBLE);
                                        setAlphaAnimation(image5);
                                    }
                                });
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        ivGif1.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ivGif2.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ivGif3.setScaleType(ImageView.ScaleType.FIT_CENTER);
        ivGif4.setScaleType(ImageView.ScaleType.FIT_CENTER);
        Glide.with(ControlActivity.this)
                .load(R.mipmap.btnnew)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new GlideDrawableImageViewTarget(ivGif1) {
                    @Override
                    public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                        super.onResourceReady(drawable, anim);
                    }
                });
        Glide.with(ControlActivity.this)
                .load(R.mipmap.k1)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new GlideDrawableImageViewTarget(ivGif2) {
                    @Override
                    public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                        super.onResourceReady(drawable, anim);
                    }
                });
        Glide.with(ControlActivity.this)
                .load(R.mipmap.k2new)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new GlideDrawableImageViewTarget(ivGif3) {
                    @Override
                    public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                        super.onResourceReady(drawable, anim);
                        ivGif3.setVisibility(View.INVISIBLE);
                    }
                });
        Glide.with(ControlActivity.this)
                .load(R.mipmap.k3new)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new GlideDrawableImageViewTarget(ivGif4) {
                    @Override
                    public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                        super.onResourceReady(drawable, anim);
//                        ivGif3.setVisibility(View.INVISIBLE);
                    }
                });
        //Mate10 Lite Hall 1、3
//        Button fullScreenBtn = (Button) findViewById(R.id.fullScreenBtn);
//        fullScreenBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeF(FULLSCREENF);
//            }
//        });
//        Button fullScreenFBack = (Button) findViewById(R.id.fullScreenFBack);
//        fullScreenFBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeF(CONTROLLERF);
//            }
//        });
//        Button game = (Button) findViewById(R.id.game);
//        game.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeF(SELECTF);
//            }
//        });
//        Button selectFBack = (Button) findViewById(R.id.selectFBack);
//        selectFBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeF(FULLSCREENF);
//            }
//        });
          //Nova2、2P
//        Button shootBtn = (Button) findViewById(R.id.shootBtn);
//        shootBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        Button printBtn = (Button) findViewById(R.id.printBtn);
//        printBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //系统打印
//                PrintHelper printHelper = new PrintHelper(ControlActivity.this);
//                printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
//                Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.mipmap.huaweiicon);
//                printHelper.printBitmap("Print Bitmap", bmp);
//            }
//        });
        //Watch2
//        Button callBtn = (Button) findViewById(R.id.callBtn);
//        callBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
        //视频
        Button videoBtn = (Button) findViewById(R.id.videoBtn);
        videoBtn.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        bjBtn.setVisibility(View.INVISIBLE);
                        sfc = new SurfaceViewControl(sv,assetManager);
                        sfc.setOnMedialCompletionListener(new SurfaceViewControl.OnMedialCompletionListener() {
                            @Override
                            public void onMedialCompletion() {
                                sfc.pause();
                                sfc.seekTo(0);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        sfc.start();
                                        sfc.destroy();
                                        sfc = null;
                                        changeF(CONTROLLERF);
                                    }
                                },50);
                            }
                        });
                        sfc.setOnMedialPreparedListener(new SurfaceViewControl.OnMedialPreparedListener() {
                            @Override
                            public void onMedialPrepared() {
                                changeF(VIDEOF);
                            }
                        });
                        sfc.setOnSurfaceCreatedListener(new SurfaceViewControl.OnSurfaceCreatedListener() {
                            @Override
                            public void onSurfaceCreated() {

                            }
                        });
                        sfc.play(0,"1.mp4");
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        sv = (SurfaceView) findViewById(R.id.sv);
        sv.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        sfc.pause();
                        sfc.seekTo(0);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sfc.start();
                                sfc.destroy();
                                sfc = null;
                                changeF(CONTROLLERF);
                            }
                        },50);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        //换装按钮
        Button dressUpBtn = (Button) findViewById(R.id.dressUpBtn);
        dressUpBtn.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        changeF(DRESSUP);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        back = (Button) findViewById(R.id.back);
        back.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        changeF(CONTROLLERF);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        color1.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        setColorScaleAnimation(color1,R.mipmap.mate10color_1);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        color2.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        setColorScaleAnimation(color2,R.mipmap.mate10color_2);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        color3.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        setColorScaleAnimation(color3,R.mipmap.mate10color_3);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        color4.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        setColorScaleAnimation(color4,R.mipmap.mate10color_4);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

//        try {
//            if(sp!=null){
//                sp.close();
//                sp = null;
//            }
//            FrameLayout test = (FrameLayout)findViewById(R.id.testF);
//            test.setVisibility(View.VISIBLE);
//            sp = new SerialPortEntity("/dev/ttyS3", 115200, 8, 1, 'N');
//            sp.setOnReadyDataListener(new SerialPortEntity.OnReadyDataListener() {
//                @Override
//                public void OnReadData(final int msg) {
//                    final TextView tx = (TextView)findViewById(R.id.serialPortTxt);
//                    final SimpleDateFormat   formatter  =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:sss");
//                    final Date curDate =  new Date(System.currentTimeMillis());
//                    tx.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if(tx.getText().length() > 400)
//                                tx.setText("("+formatter.format(curDate)+")" + msg);
//                            else
//                                tx.setText("("+formatter.format(curDate)+")" + msg + "\n" +  tx.getText());
//                        }
//                    });
//                }
//            });
//        } catch (SecurityException e) {
//
//        }


        //线程回调
        activityHandler =  new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case CONTROLACTIVITY_SERIALPORTSRART:
                            bjBtn.setVisibility(View.GONE);
                            back.setVisibility(View.GONE);
                            nameplateF.setVisibility(View.INVISIBLE);
                            welcomeF.setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    changeF(CONTROLLERF);
                                    bjBtn.setBackground(nameplateFB);
                                }
                            },2000);
                        break;
                    case CONTROLACTIVITY_SERIALPORTEND:
                            bjBtn.setVisibility(View.GONE);
                            back.setVisibility(View.GONE);
                            endF.setVisibility(View.VISIBLE);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    bjBtn.setVisibility(View.GONE);
                                    changeF(NAMEPLATEF);
                                }
                            },2000);
                        break;
                }
            }
        };
        //服务回调线程
        servuceHandler = (Messenger) getIntent().getExtras().get("messenger");
        Message message = Message.obtain();
        message.what = BluetoothLeAppService.SERVICE_SRART;
        message.replyTo = new Messenger(activityHandler);
        try {
            servuceHandler.send(message);
        } catch (RemoteException e) {
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                changeF(NAMEPLATEF);
            }
        }, 2000);

    }
    //换装
    private void setColorScaleAnimation(View view, int resid){
        setScaleAnimation(view,durationMillis-100,
                1.0f, 1.3f, 1.0f, 1.3f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ImageView dressUpIBtn= (ImageView) findViewById(R.id.dressUpIBtn);
        dressUpIBtn.setBackgroundResource(resid);
    }
    //更换背景
    private void  setRecognizeImg(int top,int height){
        switch (curreImg){
            case 1:
                setImgBackgroundResource(recognizeImg5,R.mipmap.mate10recognizeimg_b3,R.mipmap.mate10recognizeimg_3,(curreImg - 1) * (750 + 50),top,height);
                break;
            case 2:
                setImgBackgroundResource(recognizeImg6,R.mipmap.mate10recognizeimg_b4,R.mipmap.mate10recognizeimg_4,(curreImg - 1) * (750 + 50),top,height);
                break;
            case 3:
                setImgBackgroundResource(recognizeImg7,R.mipmap.mate10recognizeimg_b1,R.mipmap.mate10recognizeimg_1,(curreImg - 1) * (750 + 50),top,height);
                break;
            case 4:
                setImgBackgroundResource(recognizeImg8,R.mipmap.mate10recognizeimg_b2,R.mipmap.mate10recognizeimg_2,(curreImg - 1) * (750 + 50),top,height);
                break;
            case 5:
                setImgBackgroundResource(recognizeImg9,R.mipmap.mate10recognizeimg_b3,R.mipmap.mate10recognizeimg_3,(curreImg - 1) * (750 + 50),top,height);
                break;
            case 6:
                setImgBackgroundResource(recognizeImg10,R.mipmap.mate10recognizeimg_b4,R.mipmap.mate10recognizeimg_4,(curreImg - 1) * (750 + 50),top,height);
                break;
            case 7:
                setImgBackgroundResource(recognizeImg11,R.mipmap.mate10recognizeimg_b1,R.mipmap.mate10recognizeimg_1,(curreImg - 1) * (750 + 50),top,height);
                break;
            case 8:
                setImgBackgroundResource(recognizeImg12,R.mipmap.mate10recognizeimg_b2,R.mipmap.mate10recognizeimg_2,(curreImg - 1) * (750 + 50),top,height);
                break;
        }
    }
    private void setImgBackgroundResource(View view,int resid,int changeResid, int left, int top,int height){
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) view.getLayoutParams();
        mlp.setMargins(left, top, 0, 0);
        view.setLayoutParams(mlp);
        lp.height = height;
        view.setLayoutParams(lp);
        if(top == 0)
            view.setBackgroundResource(resid);
        else
            view.setBackgroundResource(changeResid);
    }
    //转换图片
    private Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        /** 如果不设置canvas画布为白色，则生成透明 */
        v.layout(0, 0, w, h);
        v.draw(c);
        //压缩
//        Matrix matrix = new Matrix();
//        matrix.setScale(0.2f, 0.2f);
//        // 得到新的图片
//        return Bitmap.createBitmap(bmp, 0, 0, w, h, matrix,
//                true);
        return bmp;
    }
    //过度
    private  void changeF(final int type){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                curreUI = type;
//                bjBtn.setVisibility(View.INVISIBLE);
                back.setVisibility(View.VISIBLE);
                controllerF.setVisibility(View.INVISIBLE);
                welcomeF.setVisibility(View.INVISIBLE);
                endF.setVisibility(View.INVISIBLE);
                switch (type){
                    case NAMEPLATEF:

                        fullScreenF.setVisibility(View.INVISIBLE);
                        selectF.setVisibility(View.INVISIBLE);
                        dressUpF.setVisibility(View.INVISIBLE);
                        videoF.setVisibility(View.INVISIBLE);
                        recognizeF.setVisibility(View.INVISIBLE);
                        fastF.setVisibility(View.INVISIBLE);
                        shootF.setVisibility(View.INVISIBLE);
                        callF.setVisibility(View.INVISIBLE);
                        back.setVisibility(View.INVISIBLE);

                        nameplateF.setVisibility(View.VISIBLE);
                        bj1.clearAnimation();
                        bj1.invalidate();
                        nameplatefOpen();
                        break;
                    case CONTROLLERF:
                        fullScreenF.setVisibility(View.INVISIBLE);
                        selectF.setVisibility(View.INVISIBLE);
                        dressUpF.setVisibility(View.INVISIBLE);
                        videoF.setVisibility(View.INVISIBLE);
                        nameplateF.setVisibility(View.INVISIBLE);
                        recognizeF.setVisibility(View.INVISIBLE);
                        fastF.setVisibility(View.INVISIBLE);
                        shootF.setVisibility(View.INVISIBLE);
                        callF.setVisibility(View.INVISIBLE);
                        back.setVisibility(View.INVISIBLE);

                        controllerF.setVisibility(View.VISIBLE);
                        recognizeBtn.clearAnimation();
                        recognizeBtn.invalidate();
                        delete();
                        controllerFOpen();
                        break;
                    case FULLSCREENF:
                        selectF.setVisibility(View.INVISIBLE);
                        fullScreenF.setVisibility(View.VISIBLE);
                        break;
                    case VIDEOF:
                        back.setVisibility(View.INVISIBLE);
                        videoF.setVisibility(View.VISIBLE);
                        break;
                    case DRESSUP:
                        ImageView dressUpIBtn= (ImageView) findViewById(R.id.dressUpIBtn);
                        dressUpIBtn.setBackgroundResource(R.mipmap.mate10color_1);
                        dressUpF.setVisibility(View.VISIBLE);
                        dressUpFOpen();
                        break;
                    case PRINTF:
//                        printF.setVisibility(View.VISIBLE);
                        break;
                    case SELECTF:
                        selectF.setVisibility(View.VISIBLE);
                        break;
                    case FAST:
                        curreBtn = 1;
                        ivGif1.clearAnimation();
                        ivGif1.invalidate();
                        ivGif1.setVisibility(View.VISIBLE);
                        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) ivGif1.getLayoutParams();
                        lp.setMargins(0, 330, 310, 0);
                        ivGif1.setLayoutParams(lp);
                        ivGif2.setVisibility(View.VISIBLE);
                        ivGif3.setVisibility(View.INVISIBLE);
                        ivGif4.setVisibility(View.INVISIBLE);
                        image1.setVisibility(View.VISIBLE);
                        image2.setVisibility(View.INVISIBLE);
                        image3.clearAnimation();
                        image3.invalidate();
                        image3.setVisibility(View.INVISIBLE);
                        image4.clearAnimation();
                        image4.invalidate();
                        image4.setVisibility(View.INVISIBLE);
                        image5.clearAnimation();
                        image5.invalidate();
                        image5.setVisibility(View.INVISIBLE);
                        image6.clearAnimation();
                        image6.invalidate();
                        image6.setVisibility(View.INVISIBLE);
                        image7.clearAnimation();
                        image7.invalidate();
                        image7.setVisibility(View.INVISIBLE);
                        fastF.setVisibility(View.VISIBLE);
                        fastFOpen();
                        break;
                    case RECOGNIZE:
                        recognizeF.setVisibility(View.VISIBLE);
//                        recognizeEBtn.setBackgroundResource(R.mipmap.mate10recognize_5);
                        recognizeEBtn.setBackgroundResource(R.mipmap.mate10recognize_7);
                        recognizeImg2.setVisibility(View.VISIBLE);
                        setRecognizeImg(65,400);
                        FrameLayout recognizeM = (FrameLayout)findViewById(R.id.recognizeM);
                        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) recognizeM.getLayoutParams();
                        mlp.setMargins(1790 * -1  ,405,0,60);
                        recognizeM.setLayoutParams(mlp);
                        curreImg=4;
                        recognizeType = 1;
                        recognizeFOpne();
                        break;
                    default:
                        nameplateF.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });
    }
    //重置
    //清理
    private void delete(){
        //a;清理内存缓存
        Glide.get(ControlActivity.this).clearMemory();

        //b;清理磁盘缓存
        new Thread(new Runnable() {
            @Override
            public void run() {
                //上下文参数传递的不对
                Glide.get(ControlActivity.this).clearDiskCache();
                Log.i(TAG, "已经清理完毕");
            }
        }).start();
    }
    //动画
    private void nameplatefOpen(){
        setAlphaAnimation(nameplateF);

        setTranslateAnimation(bj1, true,
                0, 50,
                0, 0, 0, 0,
                0, 0, 0, 0,
                new AnimationCallback() {
                    @Override
                    public void end() {
                        super.end();
                        if(nameplateFB == null) {
                            Bitmap temBitmap = loadBitmapFromView(nameplateF);
                            nameplateFB = new BitmapDrawable(temBitmap);
                        }
//                        else
//                            bjBtn.setVisibility(View.VISIBLE);
                    }
                });

        setTranslateAnimation(bj2,true,
                50,-50,
                0,0,0,0,
                0,0,0,0,
                null);

        setTranslateAnimation(bj3,true,
                -50,-50,
                0,0,0,0,
                0,0,0,0,
                null);

    }
    private void controllerFOpen(){
        setAlphaAnimation(controllerF);

        setTranslateAnimation(title,true,
                -50,50,
                0,0,0,0,
                0,0,0,0,
                null);
        setTranslateAnimation(title1,true,
                -50,50,
                0,0,0,0,
                0,0,0,0,
                null);

        setTranslateAnimation(recognizeBtn, false,
                50, -50,
                100, 0, 0, 110,
                150, 0, 0, 160,
                new AnimationCallback() {
                    @Override
                    public void end() {
                        super.end();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                                if(controllerFB == null) {
//                                    Bitmap temBitmap = loadBitmapFromView(controllerF);
//                                    controllerFB = new BitmapDrawable(temBitmap);
//                                }
                                bjBtn.setVisibility(View.VISIBLE);
                            }
                        },10);
                    }
                });

        setTranslateAnimation(fastBtn,false,
                50,-50,
                530,0,0,110,
                580,0,0,160,
                null);
        setTranslateAnimation(videoBtn,false,
                -50,-50,
                0,0,530,110,
                0,0,580,160,
                null);
        setTranslateAnimation(dressUpBtn,false,
                -50,-50,
                0,0,100,110,
                0,0,150,160,
                null);
    }
    private void recognizeFOpne(){
        setAlphaAnimation(recognizeF);

        setTranslateAnimation(recognizeImg1,false,
                0,50,
                650,50,0,0,
                650,100,0,0,
                null);
        setTranslateAnimation(recognizeEBtn,false,
                0,-50,
                840,0,0,10,
                840,0,0,60,
                null);
    }
    private void fastFOpen(){
        setAlphaAnimation(fastF);

        setTranslateAnimation(tab1,false,
                0,50,
                200,80,0,0,
                200,130,0,0,
                null);
        setTranslateAnimation(tab2,false,
                0,50,
                0,80,200,0,
                0,130,200,0,
                null);
        setTranslateAnimation(image1,true,
                0,-50,
                0,0,0,0,
                0,0,0,0,
                null);
    }
    private void dressUpFOpen(){
        setAlphaAnimation(dressUpF);

        setTranslateAnimation(title2,true,
                -50,50,
                0,0,0,0,
                0,0,0,0,
                null);

        setTranslateAnimation(title3,true,
                -50,50,
                0,0,0,0,
                0,0,0,0,
                null);

        setTranslateAnimation(color1,false,
                50,-50,
                540,0,0,50,
                590,0,0,100,
                null);

        setTranslateAnimation(color2,false,
                50,-50,
                735,0,0,50,
                785,0,0,100,
                null);

        setTranslateAnimation(color3,false,
                -50,-50,
                0,0,735,50,
                0,0,785,100,
                null);

        setTranslateAnimation(color4,false,
                -50,-50,
                0,0,540,50,
                0,0,590,100,
                null);
    }
    private void setAlphaAnimation(View btn){
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        //动画执行时间
        animation.setDuration(durationMillis);
        btn.startAnimation(animation);
    }
    private void setTranslateAnimation(final View btn, final boolean after,
                                       int x, int y,
                                       int left, int top, int right, int bottom,
                                       final int afterleft, final int aftertop, final int afterright, final int afterbottom,
                                       final AnimationCallback callback){
        TranslateAnimation tanimationOne = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
                Animation.ABSOLUTE, x, Animation.RELATIVE_TO_SELF, 0f, Animation.ABSOLUTE, y);
        tanimationOne.setDuration(durationMillis);
        //动画结束后View停留在结束位置
        tanimationOne.setFillAfter(after);
        tanimationOne.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }
            @Override
            public void onAnimationEnd(Animation animation) {
                if(!after) {
                    btn.clearAnimation();
                    btn.invalidate();
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) btn.getLayoutParams();
                    lp.setMargins(afterleft, aftertop, afterright, afterbottom);
                    btn.setLayoutParams(lp);
                }
                isAnimationPlay = false;
                if(callback != null)
                    callback.end();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if(!after) {
            btn.clearAnimation();
            btn.invalidate();
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) btn.getLayoutParams();
            lp.setMargins(left, top, right, bottom);
            btn.setLayoutParams(lp);
        }
        isAnimationPlay = true;
        btn.startAnimation(tanimationOne);
    }
    private void setScaleAnimation(View btn,long dm,
                                   float fromX, float toX, float fromY, float toY,
                                   int pivotXType, float pivotXValue, int pivotYType, float pivotYValue){
        ScaleAnimation animation = new ScaleAnimation(
                fromX, toX, fromY, toY,
                pivotXType, pivotXValue, pivotYType, pivotYValue
        );
        animation.setDuration(dm);
        btn.startAnimation(animation);
    }
    /**
     * 关闭对话框
     */
    private void dismissDialog(AlertDialog alertDialog) {
        try {
            Field field = alertDialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(alertDialog, true);
        } catch (Exception e) {
        }
        alertDialog.dismiss();
    }

    /**
     * 通过反射 阻止关闭对话框
     */
    private void preventDismissDialog(AlertDialog alertDialog) {
        try {
            Field field = alertDialog.getClass().getSuperclass().getDeclaredField("mShowing");
            field.setAccessible(true);
            //设置mShowing值，欺骗android系统
            field.set(alertDialog, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    /**
//     *打开蓝牙
//     */
//    private void connectBluetooth() {
//        if(adapter != null) {
//            if (!adapter.isEnabled())
//                turnOnBluetooth();
////            else if (!isLocationOpen(getApplicationContext())) {
////                Intent enableLocate = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
////                startActivityForResult(enableLocate, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
////            }
//        }
//    }
//    /**
//     *判断位置信息是否开启
//     * @param context
//     * @return
//     */
//    public static boolean isLocationOpen(final Context context){
//        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        //gps定位
//        boolean isGpsProvider = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        return isGpsProvider;
//    }
//    /**
//     * 弹出系统弹框提示用户打开 Bluetooth
//     */
//    private void turnOnBluetooth(){
//        // 请求打开 Bluetooth
//        Intent requestBluetoothOn = new Intent(
//                BluetoothAdapter.ACTION_REQUEST_ENABLE);
//
//        // 设置 Bluetooth 设备可以被其它 Bluetooth 设备扫描到
//        requestBluetoothOn
//                .setAction(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
//        // 设置 Bluetooth 设备可见时间
//        requestBluetoothOn.putExtra(
//                BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,
//                BLUETOOTH_DISCOVERABLE_DURATION);
//
//        // 请求开启 Bluetooth
//        this.startActivityForResult(requestBluetoothOn,
//                REQUEST_CODE_BLUETOOTH_ON);
//    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data)
//    {
//        // requestCode 与请求开启 Bluetooth 传入的 requestCode 相对应
//        if (requestCode == REQUEST_CODE_BLUETOOTH_ON)
//        {
//            switch (resultCode)
//            {
//                // 点击确认按钮
//                case Activity.RESULT_OK:
////                    if (!isLocationOpen(getApplicationContext())) {
////                        Intent enableLocate = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
////                        startActivityForResult(enableLocate, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
////                    }else {
////                        adapter.startDiscovery();
////                    }
//                    break;
//                // 点击确认按钮
//                case 120:
////                    if (!isLocationOpen(getApplicationContext())) {
////                        Intent enableLocate = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
////                        startActivityForResult(enableLocate, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
////                    }else {
////                        adapter.startDiscovery();
////                    }
//                    break;
//                // 点击取消按钮或点击返回键
//                case Activity.RESULT_CANCELED:
//                    finish();
//                    System.exit(0);
//                    break;
//                default:
//                    adapter.startDiscovery();
//                    break;
//            }
//        }
//        else if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
//            if (!isLocationOpen(getApplicationContext())) {
//                //若未开启位置信息功能，则退出该应用
//                finish();
//                System.exit(0);
//            }else{
//                adapter.startDiscovery();
//            }
//        }
//    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

    }

    @Override
    public void onDestroy() {
//    	stopService(intent);

        super.onDestroy();
        waitingDialog = null;
        if(sfc != null)
            sfc.destroy();
        Process.killProcess(Process.myPid());
//        Log.i(TAG, "onDestory called.");
    }

    //Activity创建或者从后台重新回到前台时被调用
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart called.");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        setIntent(intent);
        Log.i(TAG, "onNewIntent called.");
    }

    //Activity从后台重新回到前台时被调用
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart called.");
    }

    //Activity创建或者从被覆盖、后台重新回到前台时被调用
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume called.");
    }

    //Activity被覆盖到下面或者锁屏时被调用
    @Override
    protected void onPause() {
        super.onPause();
//    	sfc.destroy();
        Log.i(TAG, "onPause called.");
        //有可能在执行完onPause或onStop后,系统资源紧张将Activity杀死,所以有必要在此保存持久数据
    }

    //退出当前Activity或者跳转到新Activity时被调用
    @Override
    protected void onStop() {
        super.onStop();
//        sfc.destroy();
        Log.i(TAG, "onStop called.");
    }


    /**
     * Activity被系统杀死时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死.
     * 另外,当跳转到其他Activity或者按Home键回到主屏时该方法也会被调用,系统是为了保存当前View组件的状态.
     * 在onPause之前被调用.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.i(TAG, "onSaveInstanceState ");
        super.onSaveInstanceState(outState);
    }

    /**
     * Activity被系统杀死后再重建时被调用.
     * 例如:屏幕方向改变时,Activity被销毁再重建;当前Activity处于后台,系统资源紧张将其杀死,用户又启动该Activity.
     * 这两种情况下onRestoreInstanceState都会被调用,在onStart之后.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
    }

    public abstract class AnimationCallback{
        public void end(){

        }
    }
}
