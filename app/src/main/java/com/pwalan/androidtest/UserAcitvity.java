package com.pwalan.androidtest;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pwalan.androidtest.upload.SelectPicActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import com.tencent.upload.UploadManager;
import com.tencent.upload.Const.FileType;
import com.tencent.upload.task.ITask;
import com.tencent.upload.task.IUploadTaskListener;
import com.tencent.upload.task.data.FileInfo;
import com.tencent.upload.task.impl.PhotoUploadTask;

import org.json.JSONObject;

/**
 * 用户页面 用来测试上传和下载图片
 * 这里主要是选择图片和执行上传操作
 */

public class UserAcitvity extends Activity implements View.OnClickListener {


    private static final String TAG = "uploadImage";
    /**
     * 去上传文件
     */
    protected static final int TO_UPLOAD_FILE = 1;
    /**
     * 上传文件响应
     */
    protected static final int UPLOAD_FILE_DONE = 2;
    /**
     * 选择文件
     */
    public static final int TO_SELECT_PHOTO = 3;
    /**
     * 上传初始化
     */
    private static final int UPLOAD_INIT_PROCESS = 4;
    /**
     * 上传中
     */
    private static final int UPLOAD_IN_PROCESS = 5;

    /**
     * 要上传图片的本地地址
     */
    private String picPath = null;

    /**
     * 腾讯云上传管理类
     */
    private UploadManager photoUploadMgr;

    String bucket;
    String signUrl;
    String sign;
    String result;

    private RoundImageView head;
    private Button btn_up,btn_down;
    private TextView tv_result;
    private ProgressDialog progressDialog;
    private App app;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        tv_result=(TextView)findViewById(R.id.tv_result);

        head=(RoundImageView)findViewById(R.id.head);
        head.setOnClickListener(this);

        btn_up=(Button)findViewById(R.id.btn_up);
        btn_up.setOnClickListener(this);

        btn_down=(Button)findViewById(R.id.btn_down);
        btn_down.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        app=(App)getApplication();

        bucket="pwalan";
        //获取APP签名
        signUrl=app.getServer()+"getSign";
        getUploadImageSign(signUrl);
        // 实例化Photo业务上传管理类
        photoUploadMgr = new UploadManager(this, "10035979",
                FileType.Photo, "qcloudphoto");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.head:
                Intent intent = new Intent(this,SelectPicActivity.class);
                startActivityForResult(intent, TO_SELECT_PHOTO);
                break;
            case R.id.btn_up:
                if(picPath!=null)
                {
                    handler.sendEmptyMessage(TO_UPLOAD_FILE);
                }else{
                    Toast.makeText(this, "上传的文件路径出错", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==Activity.RESULT_OK && requestCode == TO_SELECT_PHOTO)
        {
            picPath = data.getStringExtra(SelectPicActivity.KEY_PHOTO_PATH);
            Log.i(TAG, "最终选择的图片=" + picPath);
            Bitmap bm = BitmapFactory.decodeFile(picPath);
            head.setImageBitmap(bm);

            //更新图库

            Uri localUri = Uri.fromFile(new File(picPath));
            Intent localIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri);
            sendBroadcast(localIntent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TO_UPLOAD_FILE:
                    //上传图片
                    PhotoUploadTask task = new PhotoUploadTask(picPath,
                            new IUploadTaskListener() {
                                @Override
                                public void onUploadSucceed(final FileInfo result) {
                                    Log.i("Demo", "upload succeed: " + result.url);
                                }
                                @Override
                                public void onUploadStateChange(ITask.TaskState state) {
                                }
                                @Override
                                public void onUploadProgress(long totalSize, long sendSize){
                                    long p = (long) ((sendSize * 100) / (totalSize * 1.0f));
                                    Log.i("Demo", "上传进度: " + p + "%");
                                }
                                @Override
                                public void onUploadFailed(final int errorCode, final String errorMsg){
                                    Log.i("Demo", "上传结果:失败! ret:" + errorCode + " msg:" + errorMsg);
                                }
                            }
                    );
                    task.setBucket(bucket); // 设置 Bucket(可选)
                    task.setFileId("test_fileId_" + UUID.randomUUID()); // 为图片自定义 FileID(可选)
                    task.setAuth(sign);
                    photoUploadMgr.upload(task); // 开始上传
                    break;

                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    // 获取app 的签名
    private void getUploadImageSign(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Log.d("Demo","Start getSign");
                    URL url = new URL(s);
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    InputStreamReader in = new InputStreamReader(urlConnection
                            .getInputStream());
                    BufferedReader buffer = new BufferedReader(in);
                    String inpuLine = null;
                    while ((inpuLine = buffer.readLine()) != null) {
                        result = inpuLine + "\n";
                    }
                    JSONObject jsonData = new JSONObject(result);
                    sign = jsonData.getString("sign");
                    Log.i("Sign", "SIGN: "+sign);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }).start();

    }
}

