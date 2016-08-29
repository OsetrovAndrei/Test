package com.iquesoft.andrew.seedprojectchat.view.classes.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iquesoft.andrew.seedprojectchat.R;
import com.iquesoft.andrew.seedprojectchat.common.BaseFragment;
import com.iquesoft.andrew.seedprojectchat.di.components.ILoginActivityComponent;
import com.iquesoft.andrew.seedprojectchat.presenter.classes.fragments.RegisterFragmentPresenter;
import com.iquesoft.andrew.seedprojectchat.util.ValidateUtil;
import com.iquesoft.andrew.seedprojectchat.view.classes.activity.LoginActivity;
import com.iquesoft.andrew.seedprojectchat.view.interfaces.fragments.IRegisterFragment;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Andrew on 17.08.2016.
 */

public class RegisterFragment extends BaseFragment implements IRegisterFragment {

    @Inject
    RegisterFragmentPresenter presenter;

    @Inject
    ValidateUtil validateUtil;

    @BindView(R.id.e_mail_tv)
    TextView eMailTV;
    @BindView(R.id.username_tv)
    TextView usernameTV;
    @BindView(R.id.password_tv)
    TextView passwordTV;
    @BindView(R.id.cim_photo_view)
    CircularImageView circleImageView;

    private View rootView;
    private LoginActivity loginActivity;

    static final int GALLERY_REQUEST = 1;

    @OnClick(R.id.register_button)
    void registerButtonClick(View view){
        presenter.onRegisterButtonClicked(eMailTV, usernameTV, passwordTV);
    }

    @OnClick(R.id.cim_photo_view)
    void choosePhotoClick(){
        photoSelector();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_registration, container, false);
            ButterKnife.bind(this, rootView);
        }
        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.getComponent(ILoginActivityComponent.class).inject(this);
        loginActivity = (LoginActivity) getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.init(this);
    }


    @Override
    public Context getActivityContext() {
        return getActivity();
    }

    @Override
    public ValidateUtil getValidateUtil() {
        return validateUtil;
    }

    @Override
    public LoginActivity getLoginActivity() {
        return loginActivity;
    }

    public void photoSelector(){
        if (eMailTV.getText().toString().equals("")){
            eMailTV.setError("Insert you eMail");
            eMailTV.requestFocus();
        } else {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
        }
    }

    @Override
     public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        presenter.uploadUserPhoto(bitmap, circleImageView, eMailTV.getText().toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }

}
