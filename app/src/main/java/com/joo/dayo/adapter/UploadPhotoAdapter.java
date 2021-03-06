package com.joo.dayo.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.joo.dayo.R;
import com.joo.dayo.data.UploadPhoto;

import java.util.ArrayList;



public class UploadPhotoAdapter extends RecyclerView.Adapter<UploadPhotoAdapter.UploadPhotoViewHolder>{

    private ArrayList<UploadPhoto> uploadPhotos;

    private FirebaseStorage storage;
    int PICK_IMAGE_FROM_ALBUM = 0;
    private Uri uri;
    private Bitmap bitmap;

    public class UploadPhotoViewHolder extends RecyclerView.ViewHolder {

        ImageView uploadPhotoIv, removeIv;


        public UploadPhotoViewHolder(@NonNull final View itemView) {
            super(itemView);
            uploadPhotoIv = (ImageView) itemView.findViewById(R.id.uploadPhotoIv);
            //removeIv = (ImageView) itemView.findViewById(R.id.removeIv);;

            //필요 시 여기서 클릭이벤트 추가
            uploadPhotoIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position == getItemCount() -1 && position != RecyclerView.NO_POSITION) {

                    }
                    else{
                        //올린 사진 클릭 시 사진 업로드 하도록.
                    }
                }
            });

        }

    }

    public UploadPhotoAdapter(ArrayList<UploadPhoto> uploadPhotos) {
        this.uploadPhotos = uploadPhotos;
    }

    @NonNull
    @Override
    public UploadPhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.upload_photo_item, parent, false);
        UploadPhotoViewHolder viewHolder = new UploadPhotoViewHolder(itemView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UploadPhotoViewHolder holder, int position) {
        Drawable drawable = uploadPhotos.get(position).getUploadIv().getDrawable();
        holder.uploadPhotoIv.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount() {
        return (uploadPhotos != null ? uploadPhotos.size() : null );
    }

}
