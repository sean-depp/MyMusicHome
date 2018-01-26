package com.so.mymusichome.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.so.mymusichome.R;
import com.so.mymusichome.adapter.MyFileAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by so on 2017/12/29.
 * <p>
 * select a file and return its dir and file path
 */

public class SelectFileActivity extends AppCompatActivity {

    private String mRootPath;
    private Stack<String> mCurrPathStack;
    private TextView tv_show_path;
    private ListView lv_dir;
    private File[] mListFiles;
    private ArrayList<File> mFileArrayList = new ArrayList<>();
    private MyFileAdapter mMyFileAdapter;
    public String TAG = "sorrower";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_file);

        //初始化View
        initView();
    }

    private void initView() {
        //1.获取控件
        tv_show_path = (TextView) findViewById(R.id.tv_show_path);
        lv_dir = (ListView) findViewById(R.id.lv_dir);

        //2.获取外部存储
        mRootPath = Environment.getExternalStorageDirectory().toString();
        Log.i(TAG, "mRootPath: " + mRootPath);

        mCurrPathStack = new Stack<>();

        //3.获得本地文件信息列表
        mListFiles = Environment.getExternalStorageDirectory().listFiles();
        for (File file : mListFiles) {
            Log.i(TAG, "mListFiles name: " + file.getName());
            mFileArrayList.add(file);
        }

        for (int i = 0; i < mFileArrayList.size(); i++) {
            Log.i(TAG, "mFileArrayList name: " + mFileArrayList.get(i).getName());
        }

        //4.将根路径推入路径栈
        mCurrPathStack.push(mRootPath);

        //5.显示当前路径
        Log.i(TAG, "CurPath: " + getCurPathString());
        tv_show_path.setText(getCurPathString());

        //6.根据mFileArrayList创建文件适配器
        mMyFileAdapter = new MyFileAdapter(this, mFileArrayList);

        //7.设置适配器
        lv_dir.setAdapter(mMyFileAdapter);

        //8.设置条目点击监听
        lv_dir.setOnItemClickListener(new FileItemClickListener());

        //9.根据当前路径填充文件信息
        refillFileData(getCurPathString());
    }

    private class FileItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            File file = mListFiles[position];
            if (file.isFile()) {
                //是文件, 获取文件所在目录和自身的绝对路径
                String path = file.getAbsolutePath();

                ArrayList<String> dirAndFile = new ArrayList<>();
                dirAndFile.add(getCurPathString());
                dirAndFile.add(path);

                Intent intent = new Intent();
                intent.putStringArrayListExtra("dirAndFile", dirAndFile);
                setResult(1, intent);

                finish();
            } else {
                //是文件夹
                //1.改变栈中当前路径
                mCurrPathStack.push("/" + file.getName());
                //2.根据当前路径重新填充信息
                refillFileData(getCurPathString());
            }
        }
    }

    /**
     * 根据当前路径重新填充信息
     *
     * @param path 当前路径
     */
    private void refillFileData(String path) {
        //1.改变显示的当前路径
        tv_show_path.setText(path);
        //2.获取当前路径下全部新文件
        mListFiles = new File(path).listFiles();
        //3.清空原有的
        mFileArrayList.clear();
        //4.加入新的
        for (File f : mListFiles) {
            mFileArrayList.add(f);
        }
        //5.获取新填充好的
        mListFiles = mMyFileAdapter.setFilesData(mFileArrayList);
    }

    /**
     * @return 得到当前栈路径的String
     */
    private String getCurPathString() {
        //1.创建临时栈tmpStack, 添加当前栈mCurrPathStack到临时栈
        Stack<String> tmpStack = new Stack<>();
        tmpStack.addAll(mCurrPathStack);

        //2.逐步获取路径信息
        String curPath = "";
        while (tmpStack.size() != 0) {
            curPath = tmpStack.pop() + curPath;
        }

        //3.返回获取的路径
        return curPath;
    }

    boolean ifSearching = false;
    long lastBackPressed = 0;

    /**
     * 点击返回按钮
     */
    @Override
    public void onBackPressed() {
        if (ifSearching) {
            ifSearching = false;
            refillFileData(getCurPathString());
        } else {
            if ((mCurrPathStack.peek()).equals(mRootPath)) {
                //1.已经到达根目录
                //2.两秒内连续点击则退出
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastBackPressed < 2000) {
                    super.onBackPressed();
                } else {
                    Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                }
                lastBackPressed = currentTime;
            } else {
                //3.未到根目录则返回上一层
                mCurrPathStack.pop();
                refillFileData(getCurPathString());
            }
        }
    }
}
