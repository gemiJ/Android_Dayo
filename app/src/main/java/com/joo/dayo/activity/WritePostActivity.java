package com.joo.dayo.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.joo.dayo.data.PostData;
import com.joo.dayo.R;
import com.joo.dayo.adapter.UploadPhotoAdapter;
import com.joo.dayo.data.UploadPhoto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WritePostActivity extends Activity {

    ArrayList<UploadPhoto> uploadPhotos;
    UploadPhotoAdapter uploadPhotoAdapter;
    RecyclerView uploadPhotoView;
    ImageView cancelIv;
    TextView uploadPostIv;
    EditText explainEdt;
    String imgFileName;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseStorage storage;
    FirebaseFirestore firestore;
    StorageReference storageRef;
    int PICK_IMAGE_FROM_ALBUM = 0;
    Uri uri;
    //Uri[] uris = new Uri[5];
    List<String> photoName;
    byte[] resizedByte = new byte[5];
    ByteArrayOutputStream baos = new ByteArrayOutputStream();


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);

        mAuth=FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //저장소 권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        }

        //사진 선택 하기
        storage = FirebaseStorage.getInstance();
        Intent photoPickIntent = new Intent(Intent.ACTION_PICK);
        photoPickIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        photoPickIntent.setType("image/*");
        //photoPickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); 여러장
        startActivityForResult(photoPickIntent, PICK_IMAGE_FROM_ALBUM);

        //리스트뷰에 사진 등록
        init();

        //공유 버튼 클릭 (firebase에 글과 사진 업로드)
        uploadPostIv = (TextView) findViewById(R.id.uploadPostIv);
        uploadPostIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //사진 업로드
                storage = FirebaseStorage.getInstance();
                String imgTemp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
                String imgFileName = "IMAGE_" + imgTemp + ".png";
                storageRef = storage.getReference("images").child(imgFileName);

                storageRef.putFile(uri).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(),"사진 업로드 성공",Toast.LENGTH_SHORT).show();
                        // ...
                    }
                });

                //글 내용 연결
                explainEdt = (EditText) findViewById(R.id.explainEdt);

                //글 업로드
                 PostData postData = new PostData(explainEdt.getText().toString(), imgFileName, firebaseUser.getUid(), firebaseUser.getEmail(), imgTemp, 0,0);
                 firestore = FirebaseFirestore.getInstance();
                 firestore.collection("post").add(postData);

                finish();

            }
        });

        //x 버튼 클릭
        cancelIv = (ImageView) findViewById(R.id.cancelIv);
        cancelIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == PICK_IMAGE_FROM_ALBUM) {
            if(requestCode == 0 && resultCode == RESULT_OK){
                uri = data.getData();
                    //사진 업로드
                    showIv();
                }
            else{
                finish();
            }
        }
        else {
                //사진 선택 안했을 시 종료
                finish();
        }
    }

    private void init() {
        uploadPhotoView = (RecyclerView) findViewById(R.id.uploadPhotoView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        uploadPhotoView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        uploadPhotos = new ArrayList<>();
        uploadPhotoAdapter = new UploadPhotoAdapter(uploadPhotos);
        uploadPhotoView.setAdapter(uploadPhotoAdapter);

    }

    public void showIv(){
        ImageView uploadIv = new ImageView(this);
        uploadIv.setImageURI(uri);
        UploadPhoto uploadPhoto = new UploadPhoto(uploadIv);
        uploadPhotos.add(uploadPhoto);

        uploadPhotoAdapter.notifyDataSetChanged();
    }
}