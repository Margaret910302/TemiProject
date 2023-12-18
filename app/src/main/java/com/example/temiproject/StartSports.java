package com.example.temiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Camera;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Size;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import android.util.Log;

import com.google.mediapipe.components.CameraHelper;
import com.google.mediapipe.components.CameraXPreviewHelper;
import com.google.mediapipe.components.ExternalTextureConverter;
import com.google.mediapipe.components.FrameProcessor;
import com.google.mediapipe.components.PermissionHelper;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.framework.AndroidAssetUtil;
import com.google.mediapipe.framework.PacketGetter;
import com.google.mediapipe.glutil.EglManager;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.HashSet;
import java.util.Set;

/* 使用 MediaPipe 框架對攝影機預覽畫面進行姿態追蹤。 */
public class StartSports extends AppCompatActivity
        implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {
    private static final String TAG = "startSport";
    // 指定 MediaPipe 圖形的二進制文件名稱
    private static final String BINARY_GRAPH_NAME = "pose_tracking_gpu.binarypb";
    /* 輸入、輸出即時影像 */
    private static final String INPUT_VIDEO_STREAM_NAME = "input_video";
    private static final String OUTPUT_VIDEO_STREAM_NAME = "output_video";

    private static final String OUTPUT_LANDMARKS_STREAM_NAME = "pose_landmarks";

    // 姿勢計數器
    String[] sportType = {"向下甩手", "搖頭擺尾", "輕提腳跟", "轉體運動", "雙手托天", "蹲下起立"};
    private float poseCount = 0;
    private int direction = 0;
    TextView poseCountTextView;


    /* 語音功能 */
    private TextToSpeech tts;
    private Handler handler = new Handler();
    private Set<String> spokenNumbers = new HashSet<>();

    String chooseExercise;

    // 指定使用前置鏡頭
    private static final CameraHelper.CameraFacing CAMERA_FACING = CameraHelper.CameraFacing.FRONT;
    // 預處理：將相機預覽畫面傳送到mediapipe框架處理器中
    private static final boolean FLIP_FRAMES_VERTICALLY = true;

    /* 載入動態連結資料庫 */
    static {
        System.loadLibrary("mediapipe_jni");    // mediapipe資料庫
        System.loadLibrary("opencv_java3"); // opencv資料庫
    }

    // 與建立資料庫有關之變數宣告
    private static final String DATABASE_NAME = "TemiRobot.db";   // 建立"資料庫名稱"常數
    private static final int DATABASE_VERSION = 1;                // 建立"資料庫版本"常數
    private static String DATABASE_TABLE2 = "sports_message";     // 宣告"運動紀錄資料表"常數
    private SQLiteDatabase db;    // 宣告資料庫變數
    private MyDBHelper dbHelper;  // 宣告資料庫幫助器變數

    // 顯示相機預覽幀
    private SurfaceTexture previewFrameTexture;
    private SurfaceView previewDisplayView;
    private EglManager eglManager;

    // 處理幀
    private FrameProcessor processor;

    // 將幀轉換為MediaPipe圖形可以使用的格式
    private ExternalTextureConverter converter;

    // 獲取應用程序訊息、幫助設置相機預覽
    private ApplicationInfo applicationInfo;
    private CameraXPreviewHelper cameraHelper;

    //播放影片
    private VideoView videoView;
    private ImageView imageView;
    private boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_sports);

        videoView = findViewById(R.id.videoView);
        imageView = findViewById(R.id.imageView);

        /* 讀取頁面傳遞的資訊 */
        Bundle chooseExerciseName = getIntent().getExtras();
        if (chooseExerciseName != null) {
            chooseExercise = chooseExerciseName.getString("chooseExerciseName");
        }

        poseCountTextView = findViewById(R.id.poseCount);
        previewDisplayView = new SurfaceView(this);
        setupPreviewDisplayView();

        dbHelper = new MyDBHelper(getApplicationContext(), DATABASE_NAME, null, DATABASE_VERSION );
        db = dbHelper.getWritableDatabase();

        //播放影片
        try {
            Uri uri = null;
            switch (chooseExercise) {
                case "向下甩手":
                    uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.handdown);
                    break;
                case "搖頭擺尾":
                    uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.shakehead);
                    break;
                case "輕提腳跟":
                    uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tiptoe);
                    break;
                case "轉體運動":
                    uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.rotatebody);
                    break;
                case "雙手托天":
                    uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.handup);
                    break;
                case "蹲下起立":
                    uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.squatdown);
                    break;
            }

            videoView.setVideoURI(uri);
            videoView.start();
            imageView.setOnClickListener (new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isPlaying) {
                        pauseVideo();
                    } else {
                        playVideo();
                    }
                }

                private void playVideo() {
                    videoView.start();
                    imageView.setImageResource(android.R.drawable.ic_media_pause);
                    isPlaying = true;
                }

                private void pauseVideo() {
                    videoView.pause();
                    imageView.setImageResource(android.R.drawable.ic_media_play);
                    isPlaying = false;
                }
            });
        } catch (Exception ex) {
            Toast.makeText(StartSports.this,"影片播放： " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        /* 嘗試獲取應用程序信息 */
        try {
            applicationInfo =
                    getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(this,"Cannot find application info: " + e, Toast.LENGTH_LONG).show();
        }

        /* 處理即時影像 */
        try {
            AndroidAssetUtil.initializeNativeAssetManager(this);
            eglManager = new EglManager(null);
            processor =
                    new FrameProcessor(
                            this,
                            eglManager.getNativeContext(),
                            BINARY_GRAPH_NAME,
                            INPUT_VIDEO_STREAM_NAME,
                            OUTPUT_VIDEO_STREAM_NAME);
            processor
                    .getVideoSurfaceOutput()
                    .setFlipY(FLIP_FRAMES_VERTICALLY);
        } catch (Exception ex) {
            Toast.makeText(StartSports.this,"處理即時影像： " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }


        /* 從處理後的影像中擷取出人體關鍵點的資訊，並將其顯示在畫面上 */
        try {
            processor.addPacketCallback(
                    OUTPUT_LANDMARKS_STREAM_NAME,   // 繪畫出關鍵點
                    (packet) -> {
                        Log.v(TAG, "Received Pose landmarks packet.");
                        try {
                            byte[] landmarksRaw = PacketGetter.getProtoBytes(packet);
                            LandmarkProto.NormalizedLandmarkList poseLandmarks = LandmarkProto.NormalizedLandmarkList.parseFrom(landmarksRaw);
                            Log.v(TAG, "[TS:" + packet.getTimestamp() + "] " + PoseLandMark.getkey(poseLandmarks));
                            SurfaceHolder surfaceHolder = previewDisplayView.getHolder();

                            if ("向下甩手".equals(chooseExercise)) {
                                start_Sport(PoseLandMark.handDown(poseLandmarks) , "向下甩手：");
                            } else if ("搖頭擺尾".equals(chooseExercise)) {
                                start_Sport(PoseLandMark.shakeHead(poseLandmarks), "搖頭擺尾：");
                            } else if ("輕提腳跟".equals(chooseExercise)) {
                                start_Sport(PoseLandMark.tiptoe(poseLandmarks) , "輕提腳跟：");
                            } else if ("轉體運動".equals(chooseExercise)) {
                                start_Sport(PoseLandMark.rotateBody(poseLandmarks) , "轉體運動：");
                            } else if ("雙手托天".equals(chooseExercise)) {
                                start_Sport(PoseLandMark.handUp((poseLandmarks)), "雙手托天：");
                            } else if ("蹲下起立".equals(chooseExercise)) {
                                start_Sport(PoseLandMark.squatDown((poseLandmarks)), "蹲下起立：");
                            }

                        } catch (InvalidProtocolBufferException exception) {
                            Log.e(TAG, "failed to get proto.", exception);
                        }
                    }
            );
            PermissionHelper.checkAndRequestCameraPermissions(this);
        } catch (Exception ex) {
            Toast.makeText(StartSports.this," 從處理後的影像中擷取出人體關鍵點的資訊，並將其顯示在畫面上： " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        converter = new ExternalTextureConverter(eglManager.getContext(), 2);
        converter.setFlipY(FLIP_FRAMES_VERTICALLY);
        converter.setConsumer(processor);

        /* 如果已獲取相機權限，啟動相機預覽 */
        if (PermissionHelper.cameraPermissionsGranted(this)) {
            startCamera();
        }
    }

    /* 跳轉到其他畫面時：關閉資源，隱藏預覽視圖 */
    @Override
    protected void onPause() {
        super.onPause();
        converter.close();
        previewDisplayView.setVisibility(View.GONE);
    }

    /* 系統向使用者發出提示，詢問是否允許使用權限。當使用者回應後，系統會呼叫此方法，應用程式可以獲取權限狀態，並執行相應的操作 */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /* 相機啟動時，繪製預覽畫面 */
    protected void onCameraStarted(SurfaceTexture surfaceTexture) {
        previewFrameTexture = surfaceTexture;
        previewDisplayView.setVisibility(View.VISIBLE);
    }

    /* 回傳相機預覽畫面的解析度 */
    protected Size cameraTargetResolution() {
        return null;
    }

    /* 啟動相機，顯示即時影像 */
    public void startCamera() {
        cameraHelper = new CameraXPreviewHelper();
        cameraHelper.setOnCameraStartedListener(
                surfaceTexture -> {
                    onCameraStarted(surfaceTexture);
                });
        CameraHelper.CameraFacing cameraFacing = CameraHelper.CameraFacing.FRONT;    // 指定相機的方向為前置鏡頭
        cameraHelper.startCamera( this, cameraFacing, /*unusedSurfaceTexture=*/ null, cameraTargetResolution());    // 畫面顯示
    }

    /* 畫面的寬度和高度 */
    protected Size computeViewSize(int width, int height) {
        return new Size(width, height);
    }

    /* 當設備的螢幕方向改變調用此函式 */
    protected void onPreviewDisplaySurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Size viewSize = computeViewSize(width, height);
        Size displaySize = cameraHelper.computeDisplaySizeFromViewSize(viewSize);
        boolean isCameraRotated = cameraHelper.isCameraRotated();   // 檢查手機是否旋轉

        converter.setSurfaceTextureAndAttachToGLContext(
                previewFrameTexture,
                isCameraRotated ? displaySize.getHeight() : displaySize.getWidth(),
                isCameraRotated ? displaySize.getWidth() : displaySize.getHeight());
    }

    /* 設置預覽視圖 */
    private void setupPreviewDisplayView() {
        previewDisplayView.setVisibility(View.GONE);    // 將預覽視圖設為不可見，在設置預覽視圖之前將其隱藏，確保在設置之前不會顯示空白視圖
        ViewGroup viewGroup = findViewById(R.id.preview_display_layout);    // 找到佈局中的預覽視圖佈局
        viewGroup.addView(previewDisplayView);  // 將預覽視圖添加到佈局中
        previewDisplayView
                .getHolder()
                .addCallback(
                        new SurfaceHolder.Callback() {
                            /* 創建預覽視圖時調用 */
                            @Override
                            public void surfaceCreated(SurfaceHolder holder) {
                                processor.getVideoSurfaceOutput().setSurface(holder.getSurface());
                                Log.d("Surface","Surface Created");
                            }

                            /* 變更預覽視圖時調用 */
                            @Override
                            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                                onPreviewDisplaySurfaceChanged(holder, format, width, height);
                                Log.d("Surface","Surface Changed");
                            }

                            /* 銷毀預覽視圖時調用 */
                            @Override
                            public void surfaceDestroyed(SurfaceHolder holder) {
                                processor.getVideoSurfaceOutput().setSurface(null);
                                Log.d("Surface","Surface destroy");
                            }
                        });
    }

    private void start_Sport(String sport, String sportTAG) {
        if (sport.equals("UP")) {
            if (direction == 0) {
                poseCount += 0.5;
                direction = 1;
            }
        }
        if (sport.equals("DOWN")) {
            if (direction == 1) {
                poseCount += 0.5;
                direction = 0;
            }
        }
        if (poseCount % 1 == 0) {
            final String poseCountStr = String.format("%.0f", poseCount);

            Log.v(TAG, sportTAG + poseCountStr);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    poseCountTextView.setText(sportTAG + poseCountStr);
                }
            });
        }
    }

    private void saveData() {
        try {
//            for(int i = 0; i < 6; i++) {
//                if(sportArr[i] > 0) {
//                    // "紀錄運動資料表"分別要存入到_id(int)、name(str)、sportsType(str)、calories(float)、timeLong(int)、date(date)、remark(str)七個欄位中  暫時拿掉
//                    // "紀錄運動資料表"分別要存入到_id(int)、name(str)、sportsType(str)、times(int)、date(date)五個欄位中
//                    String commandString="INSERT INTO " + DATABASE_TABLE2 + " (name, sportsType, times, date) VALUES " +
//                            "('" + Login.ID + "', '" + sportType[i] + "', " + sportArr[i] + ", CURDATE())";
//                    db.execSQL(commandString); //執行SQL資料插入指令
//                }
//                Toast.makeText(this, "成功儲存''" + sportType[i] + "''運動紀錄!", Toast.LENGTH_LONG).show();
//            }
//            "INSERT INTO table_name (column1, column2, column3, ..., columnN) VALUES (value1, value2, value3, ..., valueN);"
            int poseCountForInt = Float.valueOf(poseCount).intValue();
            String commandString="INSERT INTO " + DATABASE_TABLE2 + " (name, sportsType, times, date) VALUES " +
                    "('" + Login.NAME + "', '" + chooseExercise + "', " + poseCountForInt + ", DATE('now'))";
            db.execSQL(commandString); //執行SQL資料插入指令
            Toast.makeText(this,"成功儲存''" + chooseExercise + "''運動紀錄!", Toast.LENGTH_LONG).show();

        } catch (Exception ex) {
            Toast.makeText(StartSports.this,"saveData： " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        db.close();
    }

    public void btnConfirm_Click(View view)
    {
        try
        {
            saveData();
            Intent intent = new Intent(this, ChooseSports.class);
            startActivity(intent);
            finish();
        }
        catch(Exception ex)
        {
            Toast.makeText(StartSports.this,"btnConfirm_Click，原因如下： " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    //android 返回鍵
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //android Home鍵
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Toast.makeText(this, "onUserLeaveHint", Toast.LENGTH_SHORT).show();
    }

    //android menu鍵
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_MENU) {
            Toast.makeText(this, "Menu鍵測試", Toast.LENGTH_SHORT).show();
            super.openOptionsMenu(); //跳出menu
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}