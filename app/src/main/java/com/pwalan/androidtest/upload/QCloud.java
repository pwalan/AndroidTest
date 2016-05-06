package com.pwalan.androidtest.upload;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.tencent.upload.Const;
import com.tencent.upload.UploadManager;
import com.tencent.upload.task.ITask;
import com.tencent.upload.task.IUploadTaskListener;
import com.tencent.upload.task.data.FileInfo;
import com.tencent.upload.task.impl.PhotoUploadTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public class QCloud {
    /**
     * 腾讯云上传管理类
     */
    static UploadManager photoUploadMgr;

    static String bucket;
    static String sign;
    static String response;
    public static String resultUrl;

    /**
     * 上传初始化
     * @param signUrl 获取签名的URL
     * @param con 上下文
     */
    public static void init(String signUrl,Context con){
        resultUrl="";
        bucket="pwalan";
        //获取APP签名
        getUploadImageSign(signUrl);
        // 实例化Photo业务上传管理类
        photoUploadMgr = new UploadManager(con, "10035979",
                Const.FileType.Photo, "qcloudphoto");
    }

    /**
     * 上传图片
     * @param picPath 图片地址
     * @param con 上下文（用来显示结果）
     */
    public static void UploadPic(String picPath, final Context con){
        resultUrl="";
        PhotoUploadTask task = new PhotoUploadTask(picPath,
                new IUploadTaskListener() {
                    @Override
                    public void onUploadSucceed(final FileInfo result) {
                        Log.i("Demo", "upload succeed: " + result.url);
                        resultUrl=result.url;
                        Toast.makeText(con,"上传成功！",Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onUploadStateChange(ITask.TaskState state) {
                    }
                    @Override
                    public void onUploadProgress(long totalSize, long sendSize){
                        Toast.makeText(con,"上传中，请稍后...",Toast.LENGTH_SHORT).show();
                        long p = (long) ((sendSize * 100) / (totalSize * 1.0f));
                        Log.i("Demo", "上传进度: " + p + "%");
                    }
                    @Override
                    public void onUploadFailed(final int errorCode, final String errorMsg){
                        Log.i("Demo", "上传结果:失败! ret:" + errorCode + " msg:" + errorMsg);
                        Toast.makeText(con,"上传失败！",Toast.LENGTH_SHORT).show();
                    }
                }
        );
        task.setBucket(bucket); // 设置 Bucket(可选)
        task.setFileId("test_fileId_" + UUID.randomUUID()); // 为图片自定义 FileID(可选)
        task.setAuth(sign);
        photoUploadMgr.upload(task); // 开始上传
    }

    // 获取app 的签名
    private static void getUploadImageSign(final String s) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Log.d("Demo", "Start getSign");
                    URL url = new URL(s);
                    HttpURLConnection urlConnection = (HttpURLConnection) url
                            .openConnection();
                    InputStreamReader in = new InputStreamReader(urlConnection
                            .getInputStream());
                    BufferedReader buffer = new BufferedReader(in);
                    String inpuLine = null;
                    while ((inpuLine = buffer.readLine()) != null) {
                        response = inpuLine + "\n";
                    }
                    JSONObject jsonData = new JSONObject(response);
                    sign = jsonData.getString("sign");
                    Log.i("Sign", "SIGN: "+sign);
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        }).start();

    }
}
