package com.iquesoft.andrew.seedprojectchat.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.iquesoft.andrew.seedprojectchat.R;
import com.iquesoft.andrew.seedprojectchat.common.DefaultBackendlessCallback;
import com.iquesoft.andrew.seedprojectchat.model.ChatUser;
import com.iquesoft.andrew.seedprojectchat.model.Friends;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by andru on 9/1/2016.
 */

public class FindFriendAdapter extends RecyclerView.Adapter<FindFriendAdapter.ViewHolder> {

    private List<BackendlessUser> users;
    private Context context;

    public FindFriendAdapter(List<BackendlessUser> users, Context context){
        this.context = context;
        this.users = users;
    }
    @Override
    public FindFriendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_find_friend_row, parent, false);
        return new FindFriendAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FindFriendAdapter.ViewHolder holder, int position) {
        BackendlessUser user = users.get(position);
        if (user.getProperty(ChatUser.PHOTO) != null){
            Uri uri = Uri.parse(user.getProperty(ChatUser.PHOTO).toString());
            Picasso.with(context).load(uri).fit().placeholder(R.drawable.seed_logo).into(holder.cimUserImage);
        }
        if (user.getProperty(ChatUser.NAME) != null){
            holder.tvUserName.setText(user.getProperty(ChatUser.NAME).toString());
        }
        holder.tvUserEMail.setText(user.getEmail());
        holder.send_request_friend.setOnClickListener(v -> {
            Friends friend = new Friends();
            friend.setUser_one(Backendless.UserService.CurrentUser());
            friend.setUser_two(users.get(position));
            friend.setStatus(1);
            friend.saveAsync(new DefaultBackendlessCallback<Friends>(context){
                @Override
                public void handleResponse(Friends response) {
                    super.handleResponse(response);
                    remove(position);
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        FloatingActionButton send_request_friend;
        CircularImageView cimUserImage;
        TextView tvUserName;
        TextView tvUserEMail;

        public ViewHolder(View itemView) {
            super(itemView);
            send_request_friend = (FloatingActionButton) itemView.findViewById(R.id.fab_send_friend_request);
            cimUserImage = (CircularImageView) itemView.findViewById(R.id.cim_user_image);
            tvUserEMail = (TextView) itemView.findViewById(R.id.tv_user_email);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_username);
        }
    }

    // Insert a new item to the RecyclerView
    public void insert(BackendlessUser user, int position) {
        users.add(position, user);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing the Data object
    public void remove(int index) {
        users.remove(index);
        notifyItemRemoved(index);
    }
}