package com.pwalan.androidtest;

import android.app.Activity;
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

import com.pwalan.androidtest.upload.QCloud;
import com.pwalan.androidtest.upload.SelectPicActivity;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


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

    //下载
    protected static final int DOWNLOAD_FILE_DONE = 6;

    /**
     * 要上传图片的本地地址
     */
    private String picPath = null;



    private RoundImageView head;
    private Button btn_up,btn_down;
    private TextView tv_result;
    private ProgressDialog progressDialog;
    private App app;

    private Bitmap bitmap;
    private String url=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        tv_result=(TextView)findViewById(R.id.tv_result);
        tv_result.setOnClickListener(this);

        head=(RoundImageView)findViewById(R.id.head);
        head.setOnClickListener(this);

        btn_up=(Button)findViewById(R.id.btn_up);
        btn_up.setOnClickListener(this);

        btn_down=(Button)findViewById(R.id.btn_down);
        btn_down.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);

        app=(App)getApplication();

        //腾讯云上传初始化
        QCloud.init(app.getServer()+"getSign",this);
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
            case R.id.btn_down:
               /* String url="http://photo01-10023565.video.myqcloud.com/20160329_024231.jpg";
                Bitmap bitmap = getHttpBitmap(url);
                head.setImageBitmap(bitmap);*/
                url="http://photo01-10023565.video.myqcloud.com/20160329_024231.jpg";
                getHttpBitmap(url);
                break;
            case R.id.tv_result:
                tv_result.setText(QCloud.resultUrl);
                break;
            default:
                break;
        }
    }

    /**
     * 获取网落图片资源
     * @param url
     */
    public void getHttpBitmap(final String url){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL myFileURL = new URL(url);
                    //获得连接
                    HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
                    //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
                    conn.setConnectTimeout(6000);
                    //连接设置获得数据流
                    conn.setDoInput(true);
                    //不使用缓存
                    conn.setUseCaches(false);
                    //这句可有可无，没有影响
                    //conn.connect();
                    //得到数据流
                    InputStream is = conn.getInputStream();
                    //解析得到图片
                    bitmap = BitmapFactory.decodeStream(is);
                    //关闭数据流
                    is.close();

                    handler.sendEmptyMessage(DOWNLOAD_FILE_DONE);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
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
                    QCloud.UploadPic(picPath, UserAcitvity.this);
                    break;
                case DOWNLOAD_FILE_DONE:
                    head.setImageBitmap(bitmap);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


}

