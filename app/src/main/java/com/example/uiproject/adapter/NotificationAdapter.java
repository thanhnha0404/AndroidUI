package com.example.uiproject.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiproject.R;
import com.example.uiproject.entity.NotificationModel;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final Context context;
    private final List<NotificationModel> notificationList;

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationModel notification);
    }

    private OnNotificationClickListener listener;

    public void setOnNotificationClickListener(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    public NotificationAdapter(Context context, List<NotificationModel> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel notification = notificationList.get(position);

        holder.notificationTitle.setText(notification.getTitle());
        holder.notificationTime.setText(notification.getTime());

        // Hiện hoặc ẩn chấm đỏ nếu chưa đọc
        if (notification.getIs_read() != null && notification.getIs_read() == 0) {
            holder.unreadIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.unreadIndicator.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            if (listener != null){
                listener.onNotificationClick(notification);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationTitle, notificationTime;
        View unreadIndicator;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationTitle = itemView.findViewById(R.id.notificationTitle);
            notificationTime = itemView.findViewById(R.id.notificationTime);
            unreadIndicator = itemView.findViewById(R.id.unreadIndicator);
        }
    }
}
