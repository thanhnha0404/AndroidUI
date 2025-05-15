package com.example.uiproject.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiproject.R;
import com.example.uiproject.adapter.NotificationAdapter;
import com.example.uiproject.dialog.NotificationDialog;
import com.example.uiproject.entity.CustomerDTO;
import com.example.uiproject.entity.NotificationModel;
import com.example.uiproject.util.DataBaseHandler;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerViewNotifications;
    private LinearLayout emptyState;
    private NotificationAdapter notificationAdapter;
    private List<NotificationModel> list;

    private DataBaseHandler dataBaseHandler;
    private CustomerDTO customerDTO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        // Lấy dữ liệu từ arguments
        if (getArguments() != null){
            customerDTO = (CustomerDTO) getArguments().getSerializable("customer");
        }

        list = new ArrayList<>();
        recyclerViewNotifications = view.findViewById(R.id.recyclerViewNotifications);
        emptyState = view.findViewById(R.id.emptyState);
        dataBaseHandler = new DataBaseHandler(requireContext());

        notificationAdapter = new NotificationAdapter(requireContext(), list);

        // Gán listener khi click vào thông báo
        notificationAdapter.setOnNotificationClickListener(notification -> {
            // Cập nhật is_read trong DB
            String updateSql = "UPDATE Notifications SET is_read = 1 WHERE id = " + notification.getId();
            dataBaseHandler.QueryData(updateSql);

            // Cập nhật local list
            notification.setIs_read(1);
            notificationAdapter.notifyDataSetChanged();

            // mo dialog
            NotificationDialog dialog = new NotificationDialog(
                    requireContext(),                             // Context (trong Activity)
                    notification.getTitle(),                  // title
                    notification.getMessage(), // message
                    notification.getTime()                    // time
            );
            dialog.show();
        });

        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewNotifications.setAdapter(notificationAdapter);

        setUpData();

        return view;
    }

    private void setUpData () {
        String sql = "SELECT * FROM Notifications WHERE idUser = " + customerDTO.getId() + " ORDER BY id DESC";
        Cursor cursor = dataBaseHandler.GetData(sql);

        list.clear(); // clear list cũ trước khi load mới

        while (cursor.moveToNext()) {
            NotificationModel notificationModel = new NotificationModel();

            notificationModel.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            notificationModel.setIdUser(cursor.getInt(cursor.getColumnIndexOrThrow("idUser")));
            notificationModel.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
            notificationModel.setMessage(cursor.getString(cursor.getColumnIndexOrThrow("message")));
            notificationModel.setTime(cursor.getString(cursor.getColumnIndexOrThrow("timestamp")));
            notificationModel.setIs_read(cursor.getInt(cursor.
                    getColumnIndexOrThrow("is_read")));

            list.add(notificationModel);
        }

        cursor.close();

        // Kiểm tra nếu rỗng thì hiện empty state
        toggleEmptyState(list.isEmpty());

        notificationAdapter.notifyDataSetChanged();
    }

    private void toggleEmptyState(boolean isEmpty) {
        if (isEmpty) {
            recyclerViewNotifications.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerViewNotifications.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }
}
