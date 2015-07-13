package com.videochat.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by nicole on 15/7/13.
 */
public class LoginActivity extends Activity {
    private EditText mUserName;
    private Button mLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mUserName= (EditText) findViewById(R.id.et_username);
        mLogin= (Button) findViewById(R.id.btn_login);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!"".equals(mUserName.getText().toString().trim())){
                    Intent intent=new Intent(LoginActivity.this,RoomActivity.class);
                    intent.putExtra("UserName",mUserName.getText().toString().trim());
                    startActivity(intent);
                }else{
                    Toast.makeText(LoginActivity.this,"用户名不能为空",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
