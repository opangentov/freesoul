package com.example.freesoul.ui;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.freesoul.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Home extends AppCompatActivity implements View.OnClickListener{
    TextView _result,_tvemail, onceSync, updateSync, _tvNama;
    Button LogOut;
    ImageButton btnTakePict;
    String currentPhotoPath;
    private RecyclerView recyclerView;
    private ImageView option, fotoProfil;
    private LinearLayout layAddData;
    private EditText etName, etNumber, etAlamat;
//    private EditText etUpdate;
    private Button btnClear, btnSubmit, btnUpdate, btnSelect;
    private ArrayList<HomeModel> personList = new ArrayList<>();
    private HomeAdapter homeAdapter;

    private static final int RC_SIGN_IN = 999;
    private static final int RC_TAKE_PHOTO = 0;
    private static final int RC_TAKE_FROM_GALLERY = 1;


    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(linearLayoutManager);

        setContentView(R.layout.activity_home);
        recyclerView = findViewById(R.id.recycle_contact);
        recyclerView.setHasFixedSize(true);
        layAddData = findViewById(R.id.layout_add);
        option = findViewById(R.id.tv_option);
        etName = findViewById(R.id.et_name);
        etNumber = findViewById(R.id.et_number);
        etAlamat = findViewById(R.id.et_alamat);
//        etUpdate = findViewById(R.id.et_update);

        LogOut = findViewById(R.id.btn_logout);
            LogOut.setOnClickListener(this);
        btnClear = findViewById(R.id.btn_clear);
            btnClear.setOnClickListener(this);
        btnSubmit = findViewById(R.id.btn_submit);
            btnSubmit.setOnClickListener(this);
        btnUpdate  = findViewById(R.id.btn_update);
            btnUpdate.setOnClickListener(this);
        btnSelect = findViewById(R.id.btn_select);
            btnSelect.setOnClickListener(this);

        btnTakePict = findViewById(R.id.btn_ambil_foto);
            btnTakePict.setOnClickListener(this);

        _tvNama = findViewById(R.id.tv_namaProfil);
        fotoProfil = findViewById(R.id.foto_profil);

        onceSync = findViewById(R.id.teks_1Sync);
        updateSync = findViewById(R.id.teks_updateSync);

        //intent topbar email
        _tvemail = findViewById(R.id.tv_result);
        Intent intent = getIntent();
        _tvemail.setText("Welcome, "+intent.getStringExtra("email"));

        //intent login
        Intent dataIntent = getIntent();
        String data =dataIntent.getStringExtra("email");
        _result = findViewById(R.id.tv_result);
        _result.setOnClickListener(this);

        Toast.makeText(this, "Welcome, "+data, Toast.LENGTH_LONG).show();

//        Toast.makeText(this, "Welcome, "+FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();


        //set foto profil
        if (FirebaseAuth.getInstance().getCurrentUser()!= null) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            _tvNama.setText(user.getDisplayName());
            mStorageRef = FirebaseStorage.getInstance().getReference();
            StorageReference fotoRef = mStorageRef.child(user.getUid() + "/image");

            //set image hanya 1 file
            Task<ListResult> listPageTask = fotoRef.list(1);
            listPageTask.addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    List<StorageReference> items = listResult.getItems();
                    if (!items.isEmpty()) {
                        Toast.makeText(Home.this, "Loading foto", Toast.LENGTH_LONG).show();

                        items.get(0).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(Home.this).load(uri).centerCrop().into(fotoProfil);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    } else {
                        Toast.makeText(Home.this,
                                "Belum ada Foto",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Home.this, "tidak bisa load image " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });


            //set biodata
            FirebaseDatabase.getInstance().getReference("biodata").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    updateSync.setText("");
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                        Biodata post = postSnapshot.getValue(Biodata.class);
                        updateSync.append(post.getNama()+" : "+post.getAlamat()+" : "+post.getNomorHP()+" : "+postSnapshot.getKey()+" \n ");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }


        //btn ambil foto
//        btnTakePict.setOnClickListener(v -> {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
//                    String[] Permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                    requestPermissions(Permissions, 100);
//                } else {
//                selectImage(Home.this);
//                }
//            }
//
//
//        });


        //btn tambah data
        btnSubmit.setOnClickListener(v -> {
            Biodata baru = new Biodata(etName.getText().toString(), etAlamat.getText().toString(), etNumber.getText().toString());
            mDatabase = FirebaseDatabase.getInstance().getReference();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            mDatabase.child("biodata").child(user.getUid()).push().setValue(baru);
            Toast.makeText(Home.this, "Berhasil Ditambahkan", Toast.LENGTH_LONG).show();

        });
        //button delete data
        btnClear.setOnClickListener(v -> {
            onceSync.setText("");
            updateSync.setText("");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase.getInstance().getReference("biodata").child(user.getUid()).orderByChild("nama").equalTo(etName.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapShot : snapshot.getChildren()){
                        postSnapShot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
        //button update
        btnUpdate.setOnClickListener(v -> {
            onceSync.setText("");
            updateSync.setText("");
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase.getInstance().getReference("biodata").child(user.getUid()).orderByChild("nama").equalTo(etName.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot postSnapShot : snapshot.getChildren()){
                        postSnapShot.getRef().child("nama").setValue(etName.getText().toString());
                        postSnapShot.getRef().child("alamat").setValue(etAlamat.getText().toString());
                        postSnapShot.getRef().child("nomorHP").setValue(etNumber.getText().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
        //btn select
        btnSelect.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseDatabase.getInstance().getReference("biodata").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    onceSync.setText("");
                    for (DataSnapshot postSnapShot : snapshot.getChildren()){
                        Biodata post = postSnapShot.getValue(Biodata.class);
                        onceSync.append(post.getNama()+" : "+post.getAlamat()+" : " +post.getNomorHP()+" : "+postSnapShot.getKey()+" \n ");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        option.setOnClickListener(v -> {
            if (recyclerView.getVisibility() == View.VISIBLE){
                recyclerView.setVisibility(View.GONE);
                layAddData.setVisibility(View.VISIBLE);
//                clearData();
            }else {
                recyclerView.setVisibility(View.VISIBLE);
                layAddData.setVisibility(View.GONE);
            }
        });




//        btnClear.setOnClickListener(v -> {
//            clearData();
//        });
//        btnSubmit.setOnClickListener(v -> {
//            if (etName.getText().toString().equals("") ||
//                    etNumber.getText().toString().equals("") ||
//                    etInstagram.getText().toString().equals("") ||
//                    etGroup.getText().toString().equals("") )
//            {
//                Toast.makeText(this, "Please fill in the entire form", Toast.LENGTH_SHORT).show();
//            } else {
//                personList.add(new
//                        HomeModel(etName.getText().toString(),
//                        etNumber.getText().toString(),
//                        etGroup.getText().toString(),
//                        etInstagram.getText().toString()));
//                homeAdapter = new HomeAdapter(this,personList);
//                recyclerView.setAdapter(homeAdapter);
//                recyclerView.setVisibility(View.VISIBLE);
//                layAddContact.setVisibility(View.GONE);
//            }
//        });
        personList.add(new HomeModel("Dr. Jusuf Latifah", "+62878555504", "dokter-dokteran", "bayerhilarious"));
        personList.add(new HomeModel("Dr. Burhanuddin Taufik", "+628785555041", "dokter keluarga", "integersjunior"));
        personList.add(new HomeModel("Dr. Latifah Bagus", "+628785555042", "dokter syaraf", "clearcarbon"));
        personList.add(new HomeModel("Dr. Agung Nurul", "+628785555043", "dokter keluarga", "opticalwwf"));
        personList.add(new HomeModel("Dr. Cahaya Krisna", "+628785555044", "dokter saham", "gisremedy"));

        homeAdapter = new HomeAdapter(this, personList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Home.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getBaseContext(),DividerItemDecoration.VERTICAL));
        homeAdapter.setOnItemClickListener((position, v) ->
        {
            personList.remove(position);
            homeAdapter = new HomeAdapter(this, personList);
            recyclerView.setAdapter(homeAdapter);
        });
        recyclerView.setAdapter(homeAdapter);
    }

//    private void clearData() {
//        etName.setText("");
//        etNumber.setText("");
//        etInstagram.setText("");
//        etGroup.setText("");
//    }
    ActivityResultLauncher <Intent> activityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult activityResult) {
                int result = activityResult.getResultCode();
                Intent data = activityResult.getData();



            }
        });
    @Override
    //kamera & logout
    public void onClick(View v) {

        if (v.getId() == R.id.btn_ambil_foto) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] Permisions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(Permisions, 100);
                } else {
                    selectImage(Home.this);

                }
            } else {
                selectImage(Home.this);

            }
        }else {
            FirebaseAuth.getInstance().signOut();
            this.finish();
        }

    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);  //getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    //methode select image
    private void selectImage (final Context context){
        final CharSequence[] options = {"Take Photo", "Choose From Galery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your Profile Picture");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")){
                    Intent takePicture  = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePicture.resolveActivity(getPackageManager()) != null){
                        File photoFIle = null;
                        try {
                            photoFIle = createImageFile();
                        }catch (IOException ex){

                        }
                        if (photoFIle != null){
                            Uri photoURI = FileProvider.getUriForFile(Home.this, "com.example.freesoul", photoFIle);
                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(takePicture, RC_TAKE_PHOTO);
                            activityResultLauncher.launch(takePicture);
                        }
                    }
                } else if (options[item].equals("Choose From Galerry")){
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, RC_TAKE_FROM_GALLERY);
                    activityResultLauncher.launch(pickPhoto);

                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectImage(Home.this);

        } else {
            Toast.makeText(this, "denied", Toast.LENGTH_LONG).show();
        }

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED){
            switch (requestCode){
                case RC_TAKE_PHOTO:
                    if (resultCode == RESULT_OK && currentPhotoPath != null){
                        Glide.with(this).load(new File(currentPhotoPath)).centerCrop().into(fotoProfil);
                        //menyimpang di galeri
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        File f = new File(currentPhotoPath);
                        Uri contentUri = Uri.fromFile(f);
                        mediaScanIntent.setData(contentUri);
                        this.sendBroadcast(mediaScanIntent);
                        ////////////////////////////////////
                        uploadToStorage(contentUri);
                    } break;
                case RC_TAKE_FROM_GALLERY:
                    if (resultCode == RESULT_OK &&  data != null){
                        Uri selectedImage = data.getData();
                        String[] filePathColoumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null){
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColoumn, null,null,null);
                            if (cursor!=null){
                                cursor.moveToFirst();
                                int coloumnIndex = cursor.getColumnIndex(filePathColoumn[0]);
                                String picturePath = cursor.getString(coloumnIndex);
                                Glide.with(this).load(new File(picturePath)).centerCrop().into(fotoProfil);
                                cursor.close();
                            }
                            uploadToStorage(selectedImage);
                        }
                    }
                    break;
            }
        }
    }


    public void uploadToStorage (Uri file){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UploadTask uploadTask;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        StorageReference fotoRef = mStorageRef.child(user.getUid()+ "/image/" + user.getUid() +".jpg");
        uploadTask = fotoRef.putFile(file);
        Toast.makeText(Home.this, "uploading image", Toast.LENGTH_LONG).show();
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(Home.this, "can't upload image, "+ exception.getMessage(),Toast.LENGTH_LONG).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(Home.this, "Image Uploaded", Toast.LENGTH_LONG).show();
            }
        });
    }



}