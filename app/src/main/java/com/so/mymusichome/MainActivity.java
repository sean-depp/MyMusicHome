package com.so.mymusichome;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.so.mymusichome.activity.SelectFileActivity;
import com.so.mymusichome.service.IService;
import com.so.mymusichome.service.MusicService;

import java.util.ArrayList;

import static com.so.mymusichome.utils.PermissionsUtil.verifyStoragePermissions;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String TAG = "sorrower";

    private Button bt_play;
    private Button bt_pause;
    private Button bt_con;
    private IService iService;
    private static SeekBar sb_progress;
    private String mFileDir;
    private String mFilePath;

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            int duration = data.getInt("duration");
            int currentPosition = data.getInt("currentPosition");
            sb_progress.setMax(duration);
            sb_progress.setProgress(currentPosition);
        }
    };

    private EditText et_input_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //23+权限获取
        verifyStoragePermissions(this);

        initUI();

        //4. 开启服务
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);

        //7. 绑定服务
        MyConn myConn = new MyConn();
        bindService(intent, myConn, BIND_AUTO_CREATE);

        //8. 设置进度条拖动事件
        sb_progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                iService.callSeekToPos(seekBar.getProgress());
            }
        });
    }

    class MyConn implements ServiceConnection {
        //5. 定义类MyConn实现接口ServiceConnection
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //6. 获取IBinder对象, 以此调用暴露的函数
            iService = (IService) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    /**
     * 初始化ui
     */
    private void initUI() {
        bt_play = (Button) findViewById(R.id.bt_play);
        bt_pause = (Button) findViewById(R.id.bt_pause);
        bt_con = (Button) findViewById(R.id.bt_con);

        sb_progress = (SeekBar) findViewById(R.id.sb_progress);

        et_input_path = (EditText) findViewById(R.id.et_input_path);

        bt_play.setOnClickListener(this);
        bt_pause.setOnClickListener(this);
        bt_con.setOnClickListener(this);
    }

    /**
     * 按钮点击事件
     *
     * @param v 按钮
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_play:
                Log.i(TAG, "bt_play");
                iService.callPlayMusic(mFilePath);
                break;
            case R.id.bt_pause:
                Log.i(TAG, "bt_pause");
                iService.callPauseMusic();
                break;
            case R.id.bt_con:
                Log.i(TAG, "bt_con");
                iService.callConMusic();
                break;
        }
    }

    /**
     * 选择一个文件, 返回其绝对路径
     *
     * @param v select按钮
     */
    public void SelectFile(View v) {
        Intent intent = new Intent(this, SelectFileActivity.class);
        startActivityForResult(intent, 0);
    }

    /**
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        意图
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1) {
            ArrayList<String> dirAndFile = data.getStringArrayListExtra("dirAndFile");

            mFileDir = dirAndFile.get(0);
            mFilePath = dirAndFile.get(1);

            et_input_path.setText(mFilePath);
//            //确认返回的内容
//            StringBuffer buffer = new StringBuffer();
//            for (int i = 0; i < dirAndFile.size(); i++) {
//                buffer.append(dirAndFile.get(i)).append("---");
//            }
//            String tmpStr = buffer.toString();
//            Log.i(TAG, tmpStr);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 授予结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "成功获取权限");
                } else {
                    Toast.makeText(this, "拒绝权限, 将无法使用程序.", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
            default:
        }
    }
}
