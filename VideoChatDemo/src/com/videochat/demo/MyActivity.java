package com.videochat.demo;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;

public class MyActivity extends Activity implements AnyChatBaseEvent {

    private static final String SERVER = "demo.anychat.cn";

    private static final int PORT = 8906;

    private AnyChatCoreSDK anyChatCoreSDK;

    private SurfaceView mLocalView;
    private SurfaceView mRemoteView;

    private Button mBack;

    private Button mTrans;

    private int localUserId;
    private int remoteUserId;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSDK();

        initLayout();
    }

    private void initLayout() {
        setContentView(R.layout.main);
        mLocalView = (SurfaceView) findViewById(R.id.sv_local);
        mRemoteView = (SurfaceView) findViewById(R.id.sv_remote);
        mBack = (Button) findViewById(R.id.btn_back);
        mTrans = (Button) findViewById(R.id.btn_trans);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AnyChatCoreSDK.GetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER) == AnyChatDefine.VIDEOCAP_DRIVER_JAVA) {
                    AnyChatCoreSDK.mCameraHelper.SwitchCamera();
                    return;
                }
                String strVideoCaptures[] = anyChatCoreSDK.EnumVideoCapture();
                String temp = anyChatCoreSDK.GetCurVideoCapture();
                for (int i = 0; i < strVideoCaptures.length; i++) {
                    if (!temp.equals(strVideoCaptures[i])) {
                        anyChatCoreSDK.UserCameraControl(-1, 0);
                        anyChatCoreSDK.SelectVideoCapture(strVideoCaptures[i]);
                        anyChatCoreSDK.UserCameraControl(-1, 1);
                        break;
                    }
                }
            }
        });

        anyChatCoreSDK.EnterRoom(1, "");


        mLocalView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mLocalView.getHolder().addCallback(anyChatCoreSDK.mCameraHelper);

        anyChatCoreSDK.UserCameraControl(-1, 1);
        anyChatCoreSDK.UserSpeakControl(-1, 1);


        remoteUserId = getIntent().getIntExtra("userId", -1);
        if (remoteUserId != localUserId) {
            int index = anyChatCoreSDK.mVideoHelper.bindVideo(mRemoteView.getHolder());
            anyChatCoreSDK.mVideoHelper.SetVideoUser(index, remoteUserId);
            anyChatCoreSDK.UserCameraControl(remoteUserId, 1);
            anyChatCoreSDK.UserSpeakControl(remoteUserId, 1);
        }
    }

    private void initSDK() {
        anyChatCoreSDK = new AnyChatCoreSDK();
        anyChatCoreSDK.SetBaseEvent(this);
        anyChatCoreSDK.mSensorHelper.InitSensor(this);
        AnyChatCoreSDK.mCameraHelper.SetContext(this);
        anyChatCoreSDK.InitSDK(Build.VERSION.SDK_INT, 0);//初始化SDK  第一个参数是android的系统版本号
        anyChatCoreSDK.Connect(SERVER, PORT);
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION, 1);//本地视频采集随着设备的旋转而处理
    }

    @Override
    public void OnAnyChatConnectMessage(boolean bSuccess) {
        if (bSuccess) {
            Toast.makeText(this, "连接成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "连接失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
        if (dwErrorCode == 0) {
            Toast.makeText(this, "登陆成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "登陆失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
        if (dwErrorCode == 0) {
            Toast.makeText(this, "进入房间" + dwRoomId + "成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "进入房间" + dwRoomId + "失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
        Toast.makeText(this, "房间内人数 ＝" + dwUserNum, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
        if (bEnter) {
            Toast.makeText(this, dwUserId + "进入房间", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, dwUserId + "退出房间", Toast.LENGTH_SHORT).show();
            anyChatCoreSDK.LeaveRoom(-1);
            anyChatCoreSDK.Release();
        }
    }

    @Override
    public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
        anyChatCoreSDK.Logout();
    }

    @Override
    protected void onPause() {
        super.onPause();
        anyChatCoreSDK.UserCameraControl(-1, 0);
        anyChatCoreSDK.UserCameraControl(remoteUserId, 0);
        anyChatCoreSDK.UserSpeakControl(-1, 0);
        anyChatCoreSDK.UserSpeakControl(remoteUserId, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
