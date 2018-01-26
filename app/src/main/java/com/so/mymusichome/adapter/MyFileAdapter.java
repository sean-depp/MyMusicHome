package com.so.mymusichome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.so.mymusichome.R;
import com.so.mymusichome.utils.FileSortFactory;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

/**
 * Created by so on 2017/12/29.
 */

public class MyFileAdapter extends BaseAdapter {
    ArrayList<File> mFileArrayList;
    Context mContext;
    //排序方法
    int sortWay = FileSortFactory.SORT_BY_FOLDER_AND_NAME;

    /**
     * @param context
     * @param fileArrayList
     */
    public MyFileAdapter(Context context, ArrayList<File> fileArrayList) {
        this.mContext = context;
        this.mFileArrayList = fileArrayList;
    }

    /**
     * @param fileArrayList 传入的新fileArrayList
     * @return
     */
    public File[] setFilesData(ArrayList<File> fileArrayList) {
        //1.ArrayList<File>赋值
        mFileArrayList = fileArrayList;
        //2.排序
        this.notifyDataSetChanged();
        //3.创建files并填充返回
        File[] files = new File[mFileArrayList.size()];
        for (int i = 0; i < files.length; i++) {
            files[i] = mFileArrayList.get(i);
        }
        return files;
    }

    @Override
    public void notifyDataSetChanged() {
        //重新排序
        MySort();
        super.notifyDataSetChanged();
    }

    private void MySort() {
        Collections.sort(this.mFileArrayList, FileSortFactory.getWebFileQueryMethod(sortWay));
    }

    @Override
    public int getCount() {
        return mFileArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return mFileArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        File file = mFileArrayList.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_file_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (file.isDirectory()) {
            viewHolder.fileImage.setImageResource(R.drawable.dir);
            viewHolder.fileSize.setText("文件夹");
        } else {
            viewHolder.fileImage.setImageResource(R.drawable.file);
            viewHolder.fileSize.setText(NormalSize(file));
        }
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        viewHolder.fileName.setText(file.getName());
        viewHolder.fileTime.setText(format.format(new Date(file.lastModified())));
        return convertView;
    }

    /**
     * 控件设置
     */
    public static class ViewHolder {
        ImageView fileImage;
        TextView fileName;
        TextView fileSize;
        TextView fileTime;

        public ViewHolder(View v) {
            fileImage = (ImageView) v.findViewById(R.id.iv_file_image);
            fileName = (TextView) v.findViewById(R.id.tv_file_name);
            fileSize = (TextView) v.findViewById(R.id.tv_file_size);
            fileTime = (TextView) v.findViewById(R.id.tv_file_time);
        }
    }

    /**
     * @param file 当前文件
     * @return 文件大小
     */
    public static String NormalSize(File file) {
        if (file.isFile()) {
            long result = file.length();
            long gb = 2 << 29;
            long mb = 2 << 19;
            long kb = 2 << 9;
            if (result < kb) {
                return result + "B";
            } else if (result >= kb && result < mb) {
                return String.format("%.2fKB", result / (double) kb);
            } else if (result >= mb && result < gb) {
                return String.format("%.2fMB", result / (double) mb);
            } else if (result >= gb) {
                return String.format("%.2fGB", result / (double) gb);
            }
        }
        return null;
    }
}
