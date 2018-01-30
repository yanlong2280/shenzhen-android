package com.example.administrator.HuaweiMWC;

/**
 * Created by Administrator on 2016/12/21.
 */
import java.io.IOException;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class SurfaceViewControl {
    private final String TAG = "main";
    private SurfaceView sv;
    private MediaPlayer mediaPlayer;
    private int currentPosition = 0;
    private boolean isPlaying;
    public static String url;
    public static boolean isLoop = false;
    private AssetManager assetManager;
//    private MyThread myThread;
    public SurfaceViewControl(SurfaceView sv2,AssetManager assetManager) {
        this.sv = sv2;
//		this.url = url;
        this.assetManager = assetManager;
        // 为SurfaceHolder添加回调
        sv.getHolder().addCallback(callback);

//        sv.setZOrderOnTop(true);
//        sv.getHolder().setFormat(PixelFormat.TRANSLUCENT);
//        myThread = new MyThread(sv.getHolder());//创建一个绘图线程
        // 4.0版本之下需要设置的属性
        // 设置Surface不维护自己的缓冲区，而是等待屏幕的渲染引擎将内容推送到界面
        // sv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }
    //定义播放完毕回调接口
    public interface OnMedialCompletionListener{
        public void  onMedialCompletion();
    }
    private OnMedialCompletionListener onMedialCompletionListener;
    //设置回调方法
    public  void  setOnMedialCompletionListener(OnMedialCompletionListener listener){
        onMedialCompletionListener = listener;
    }
    //定义装载完毕回调接口
    public interface OnMedialPreparedListener{
        public void  onMedialPrepared();
    }
    private OnMedialPreparedListener onMedialPreparedListener;
    //设置装载完毕回调方法
    public  void  setOnMedialPreparedListener(OnMedialPreparedListener listener){
        onMedialPreparedListener = listener;
    }
    //定义SurfaceHolder 被创建回调接口
    public interface OnSurfaceCreatedListener{
        public void  onSurfaceCreated();
    }
    private OnSurfaceCreatedListener onSurfaceCreatedListener;
    //设置回调方法
    public  void  setOnSurfaceCreatedListener(OnSurfaceCreatedListener listener){
        onSurfaceCreatedListener = listener;
    }
    private Callback callback = new Callback() {
        // SurfaceHolder被修改的时候回调
        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "SurfaceHolder 被销毁");
            // 销毁SurfaceHolder的时候记录当前的播放位置并停止播放
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                currentPosition = mediaPlayer.getCurrentPosition();
                mediaPlayer.stop();
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "SurfaceHolder 被创建");
            if (currentPosition > 0) {
                // 创建SurfaceHolder的时候，如果存在上次播放的位置，则按照上次播放位置进行播放
                play(currentPosition,url);
                currentPosition = 0;
            }
            onSurfaceCreatedListener.onSurfaceCreated();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            Log.i(TAG, "SurfaceHolder 大小被改变");
        }

    };

    /*
     * 停止播放
     */
    protected void stop() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
//			btn_play.setEnabled(true);
            isPlaying = false;
        }
    }

    /**
     * 开始播放
     *
     * @param msec 播放初始位置
     */
    public void play(final int msec,String url) {
        this.url = url;
        // 获取视频文件地址
        String path = this.url;
        AssetFileDescriptor file = null;
        try {
            file = assetManager.openFd(url);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
        }
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置播放的视频源
            mediaPlayer.setDataSource(file.getFileDescriptor(),
                    file.getStartOffset(),
                    file.getLength());

            // 设置显示视频的SurfaceHolder
            mediaPlayer.setDisplay(sv.getHolder());


            mediaPlayer.setLooping(isLoop);
            Log.i(TAG, "开始装载");
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    Log.i(TAG, "装载完成");
                    mediaPlayer.start();
                    // 按照初始位置播放
                    mediaPlayer.seekTo(msec);
                    onMedialPreparedListener.onMedialPrepared();

                    // 设置进度条的最大进度为视频流的最大播放时长
//					seekBar.setMax(mediaPlayer.getDuration());
                    // 开始线程，更新进度条的刻度
//					new Thread() {
//						@Override
//						public void run() {
//							try {
//								isPlaying = true;
//								while (isPlaying) {
//									int current = mediaPlayer
//											.getCurrentPosition();
////									seekBar.setProgress(current);
//
//									sleep(500);
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//					}.start();

//					btn_play.setEnabled(false);
                }
            });
            // 播放完毕回调
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
//					replay();
//					btn_play.setEnabled(true);
//                    mediaPlayer.start();

//                    Message message = Message.obtain();
//                    message.arg2 =  200;
//                    ControlActivity.mHandler.sendMessage(message);
                    onMedialCompletionListener.onMedialCompletion();
                }
            });
            // 播放错误回调
            mediaPlayer.setOnErrorListener(new OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    if(mediaPlayer != null){
                        mediaPlayer.release();   //释放资源
                    }
                    isPlaying = false;
//					play(0,SurfaceViewControl.url);
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 重新开始播放
     */
    public void replay() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(0);
//			Toast.makeText(this, "重新播放", 0).show();
//			btn_pause.setText("暂停");
            return;
        }
        mediaPlayer.release();   //释放资源
        mediaPlayer = null;
        isPlaying = false;
        play(0,this.url);
    }

    /**
     * 暂停
     */
    public void pause() {
        if(mediaPlayer != null)
            mediaPlayer.pause();
    }
    /**
     * 继续
     */
    public void start() {
        mediaPlayer.start();
    }
    public  void seekTo(int seek){
        if(mediaPlayer != null)
            mediaPlayer.seekTo(seek);
    }
    protected void destroy() {
        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()){
                mediaPlayer.stop();  //停止播放视频
            }
//            mediaPlayer.seekTo(0);
            mediaPlayer.release();   //释放资源
            mediaPlayer = null;


//            SurfaceHolder mHolder = sv.getHolder();
//            // 创建画布
//            Canvas canvas = mHolder.lockCanvas();
//            // 设置画布颜色
//            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//            // 锁定画布
//            mHolder.unlockCanvasAndPost(canvas);
        }
    }
}

