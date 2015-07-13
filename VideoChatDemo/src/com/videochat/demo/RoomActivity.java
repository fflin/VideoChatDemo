package com.videochat.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nicole on 15/7/13.
 */
public class RoomActivity extends Activity implements AnyChatBaseEvent {
    private static final String SERVER = "demo.anychat.cn";

    private static final int PORT = 8906;

    private AnyChatCoreSDK anyChat;

    private ListView usersLv;

    private List<String> users = new ArrayList<>();

    private ArrayAdapter<String> adapter;

    private int[] usersId;

    private int userNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSDK();
        setContentView(R.layout.room);
        anyChat.EnterRoom(1, "");

        usersLv = (ListView) findViewById(R.id.lv_users);

        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,users);

        usersLv.setAdapter(adapter);

        usersLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(RoomActivity.this,MyActivity.class);
                intent.putExtra("userId",usersId[i]);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        usersId = anyChat.GetOnlineUser();
        users.clear();
        for (int i = 0; i < usersId.length; i++) {
            users.add(anyChat.GetUserName(usersId[i]));
        }
        adapter.notifyDataSetChanged();
    }

    private void initSDK() {
        anyChat = new AnyChatCoreSDK();
        anyChat.SetBaseEvent(this);
        anyChat.mSensorHelper.InitSensor(this);
        AnyChatCoreSDK.mCameraHelper.SetContext(this);
        anyChat.InitSDK(Build.VERSION.SDK_INT, 0);//初始化SDK  第一个参数是android的系统版本号
        anyChat.Connect(SERVER, PORT);
        AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION, 1);//本地视频采集随着设备的旋转而处理
        anyChat.Login(getIntent().getStringExtra("UserName"),"");

    }

    @Override
    public void OnAnyChatConnectMessage(boolean bSuccess) {
    }

    @Override
    public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
    }

    @Override
    public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
        if (dwErrorCode == 0) {
            Toast.makeText(this, "进入房间 roomId=" + dwRoomId, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
        userNum = dwUserNum;
        Toast.makeText(this, "房间id＝" + dwRoomId + "房间人数＝" + dwUserNum, Toast.LENGTH_SHORT).show();
        usersId = anyChat.GetOnlineUser();
        users.clear();
        for (int i = 0; i < usersId.length; i++) {
            users.add(anyChat.GetUserName(usersId[i]));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {

    }

    @Override
    public void OnAnyChatLinkCloseMessage(int dwErrorCode) {

    }
}
