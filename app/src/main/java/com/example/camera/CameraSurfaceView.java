package com.example.camera;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    // SurfaceView 는 껍데기 역할만 하고 컨트롤은 SurfaceHolder 가 담당
    private SurfaceHolder mholder;
    private Camera mcamera = null;
    private Context mContext;

    public CameraSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        mContext = context;
        mholder = getHolder();
        mholder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); //here
        mholder.addCallback(this);
    }





    // SurfaceView 가 만들어지는 시점에 호출
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

        if (checkCameraPermission()) {
            // 카메라 객체 오픈
            try {
                // 카메라 객체에 SurfaceView 를 미리보기로 사용
                mcamera = Camera.open();
                if(mcamera !=  null) {
                    mcamera.setDisplayOrientation(90);
                    mcamera.setPreviewDisplay(mholder);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            requestCameraPermission();

        }
    }
    // SurfaceViw 가 변경되는 시점에 호출, 화면에 보여지기 전에 크기를 결정
    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        if(mcamera != null){
            try{
                mcamera.startPreview(); // 미리보기 화면에 렌즈로부터 들어온 영상을 뿌려줌
            } catch(Exception e){
                e.printStackTrace();
            }
        }

    }



    // SurfaceView 가 소멸하는 시점에 호출
    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(mcamera != null){
            mcamera.stopPreview(); // 미리보기 중지
            mcamera.release();   // 리소스 해제
            mcamera = null;
        }

    }

    // 사진 찍기
    public boolean capture(Camera.PictureCallback callback){
        if(mcamera != null){
            mcamera.takePicture(null, null, callback);
            return true;
        } else{
            return false;
        }
    }
    private boolean checkCameraPermission() {
        // 카메라 권한 확인 로직 추가
        // 필요한 권한이 허용되어 있는지 확인하고 true 또는 false 반환
        int permissionResult = ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
        return permissionResult == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }
}
