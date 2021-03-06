package com.xxx.gogo.view.user;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.otto.Subscribe;
import com.xxx.gogo.BaseToolBarActivity;
import com.xxx.gogo.R;
import com.xxx.gogo.manager.BusFactory;
import com.xxx.gogo.manager.user.UserEvent;
import com.xxx.gogo.manager.user.UserManager;
import com.xxx.gogo.utils.CommonUtils;
import com.xxx.gogo.utils.DialogHelper;
import com.xxx.gogo.utils.ThreadManager;
import com.xxx.gogo.utils.ToastManager;

public class LoginActivity extends BaseToolBarActivity implements View.OnClickListener{
    private EditText mPhoneEditView;
    private EditText mPasswordEdit;
    private ImageView mCancelPhone;
    private ImageView mCancelPassword;

    private Dialog mLoadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login_view);

        createNormalToolBar(R.string.go_login, this);

        initView();
        BusFactory.getBus().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusFactory.getBus().unregister(this);
    }

    private void initView(){
        findViewById(R.id.id_root).setOnClickListener(this);
        findViewById(R.id.forget_pass).setOnClickListener(this);
        findViewById(R.id.fast_register).setOnClickListener(this);
        findViewById(R.id.go_login).setOnClickListener(this);

        mCancelPhone = (ImageView)findViewById(R.id.cancel_phone);
        mCancelPassword = (ImageView)findViewById(R.id.cancel_pass);
        mPhoneEditView = (EditText)findViewById(R.id.id_edit_phone);
        mPasswordEdit = (EditText)findViewById(R.id.id_edit_password);
        mPhoneEditView.addTextChangedListener(new MyTextWatcher(mCancelPhone));
        mPasswordEdit.addTextChangedListener(new MyTextWatcher(mCancelPassword));
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.bar_back){
            finish();
        }else if(v.getId() == R.id.go_login){
            if(!CommonUtils.isWifiConnected(this)) {
                ToastManager.showToast(this, getString(R.string.network_unavailable));
                return;
            }
            doLogin();
        }else if(v.getId() == R.id.fast_register){
            Intent intent = new Intent(this, UserRegisterActivity.class);
            startActivity(intent);
        }else if(v.getId() == R.id.forget_pass){
            Intent intent = new Intent(this, FindPwdActivity.class);
            startActivity(intent);
        }else if (v.getId() == R.id.id_root){
            CommonUtils.hideInputMethod(mPasswordEdit);
        }
    }

    private void doLogin(){
        final String user = mPhoneEditView.getText().toString();
        final String pass = mPasswordEdit.getText().toString();
        if(TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)){
            ToastManager.showToast(this, getResources().getString(
                    R.string.input_correct_pass_phone_hit));
        }else {
            CommonUtils.hideInputMethod(mPasswordEdit);

            /**
             * TODO
             * post delay task的原因：由于输入框的干扰，造成loading dialog会随着输入框的消失从上方往下移，体验不好
             */
            ThreadManager.postTask(ThreadManager.TYPE_UI, new Runnable() {
                @Override
                public void run() {
                    UserManager.getInstance().login(user, pass);

                    mLoadingDialog = DialogHelper.showLoadingDialog(LoginActivity.this,
                            getString(R.string.login_pending));
                    mLoadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            UserManager.getInstance().cancelLogin();
                        }
                    });
                }
            }, 50);

        }
    }

    private class MyTextWatcher implements TextWatcher {
        private ImageView mView;

        MyTextWatcher(ImageView view){
            mView = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            if(TextUtils.isEmpty(s)){
//                mView.setVisibility(View.GONE);
//            }else {
//                mView.setVisibility(View.VISIBLE);
//            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(Object event){
        if(event instanceof UserEvent.UserLoginSuccess){
            if(mLoadingDialog != null){
                mLoadingDialog.dismiss();
            }
            setResult(RESULT_OK);
            finish();
        }else if (event instanceof UserEvent.UserLoginFail){
            if(mLoadingDialog != null){
                mLoadingDialog.dismiss();
            }
            ToastManager.showToast(this, getString(R.string.login_again));
        }
    }
}
