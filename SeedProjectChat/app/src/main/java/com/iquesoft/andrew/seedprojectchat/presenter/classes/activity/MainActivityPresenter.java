package com.iquesoft.andrew.seedprojectchat.presenter.classes.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.async.callback.BackendlessCallback;
import com.backendless.exceptions.BackendlessFault;
import com.iquesoft.andrew.seedprojectchat.Network.ApiCall;
import com.iquesoft.andrew.seedprojectchat.R;
import com.iquesoft.andrew.seedprojectchat.common.DefaultsBackendlessKey;
import com.iquesoft.andrew.seedprojectchat.model.ChatUser;
import com.iquesoft.andrew.seedprojectchat.presenter.interfaces.activity.IMainActivityPresenter;
import com.iquesoft.andrew.seedprojectchat.util.UpdateCurentUser;
import com.iquesoft.andrew.seedprojectchat.view.classes.activity.LoginActivity;
import com.iquesoft.andrew.seedprojectchat.view.classes.activity.MainActivity;
import com.iquesoft.andrew.seedprojectchat.view.interfaces.activity.IMainActivity;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import rx.Observable;

/**
 * Created by Andrew on 18.08.2016.
 */
@InjectViewState
public class MainActivityPresenter extends MvpPresenter<IMainActivity> implements IMainActivityPresenter {

    @Override
    protected void onFirstViewAttach() {
        subscribeToMultipleChanelPushNotification(new LinkedList<>());
        super.onFirstViewAttach();
    }

    private void subscribeToMultipleChanelPushNotification(List<String> listId){
        ApiCall.getGroupChatList().subscribe(response -> {
            Observable.from(response).subscribe(respons -> listId.add(respons.getObjectId()));
            ApiCall.getCurentFriendList().subscribe(friendses ->{
                Observable.from(friendses).subscribe(respons -> listId.add(respons.getObjectId()));
                subscribePushNotification(listId);
            });
        });
    }

    private void subscribePushNotification(List<String> chanels){
        Date date = new Date();
        date.setTime(date.getTime() + 31536000L);
        Backendless.Messaging.registerDevice(DefaultsBackendlessKey.GOOGLE_PROJECT_ID, chanels, date, new BackendlessCallback<Void>() {
            @Override
            public void handleResponse(Void aVoid) {
                Log.d("register", "register ok");
            }
        });
    }

    public void share(Context context){
        Uri imageUri =  Uri.parse("android.resource://com.iquesoft.andrew.seedprojectchat/" + R.drawable.seed_logo);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "IQueChat");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(Intent.createChooser(shareIntent, "send"));
    }

    public void logOut(MainActivity mainActivity, UpdateCurentUser updateCurentUser){
        BackendlessUser backendlessUser = Backendless.UserService.CurrentUser();
        backendlessUser.setProperty(ChatUser.ONLINE, false);
        updateCurentUser.update(backendlessUser);
        Backendless.UserService.logout(new AsyncCallback<Void>() {
            public void handleResponse(Void response) {
                // user has been logged out.
                mainActivity.startActivity(new Intent(mainActivity.getBaseContext(), LoginActivity.class));
                mainActivity.finish();
            }

            public void handleFault(BackendlessFault fault) {
                Log.d("logout", fault.getMessage());
            }
        });
    }

    public void setOnline(UpdateCurentUser updateCurentUser, Boolean online){
        if (Backendless.UserService.CurrentUser() != null) {
            BackendlessUser backendlessUser = Backendless.UserService.CurrentUser();
            backendlessUser.setProperty(ChatUser.ONLINE, online);
            updateCurentUser.update(backendlessUser);
        }
    }
}
