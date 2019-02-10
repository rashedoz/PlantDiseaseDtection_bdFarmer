package com.e.rashed.detecto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.LongDef;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static String TAG = "Debug";
    ImageView mImageView;
    TextView mTextview;
    private StorageReference mStorageRef;
    Integer lastEntry= -1;
    DatabaseReference last_entry_ref;
    DatabaseReference last_url_ref;
    DatabaseReference pred_text_ref;
    Uri downloadUri;

    private Context mContext=MainActivity.this;
    private static final int REQUEST = 112;

    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE,android.Manifest.permission.READ_EXTERNAL_STORAGE};
            if (!hasPermissions(mContext, PERMISSIONS)) {
                ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
            } else {
                //do here
                Log.e(TAG,"Has Pewrmissions");
            }
        } else {
            //do here
            Log.e(TAG,"Lower SDK Version");
        }



        ImageView cameraBtn = (ImageView) findViewById(R.id.cameraBtn);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mTextview = (TextView) findViewById(R.id.predText);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        last_entry_ref = database.getReference("last_entry");
        last_url_ref = database.getReference("last_url");
        pred_text_ref = database.getReference("prediction/pred");

//        myRef.setValue("Hello, World!");

        // Read from the database the lastEntry
        last_entry_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                lastEntry = dataSnapshot.getValue(Integer.class);
                Log.d("Debug", "Value is: " + lastEntry);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Debug", "Failed to read value.", error.toException());
            }
        });

        // Read from the database the lastEntry
        pred_text_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String pred_result = dataSnapshot.getValue(String.class);
                Log.d("Debug", "Prediction is: " + pred_result);
                mTextview.setText(pred_result);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Debug", "Failed to read value.", error.toException());
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }


    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.e.rashed.detecto.fileprovider",photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                Log.e(TAG,"Camera Intent");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.e(TAG,"Result ok file created "+mCurrentPhotoPath );
            File file = new File(mCurrentPhotoPath);
            Log.e(TAG,"Result ok file created "+mCurrentPhotoPath );
            Context context = this;
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bitmap != null) {
                Log.e(TAG,"PHOTO BITMAP CREATED");

                //Loading image
                Bitmap bmImg = BitmapFactory.decodeFile(mCurrentPhotoPath);
                mImageView.setImageBitmap(bmImg);

                ProgressDialog progress = new ProgressDialog(this);
                progress.setTitle("Loading");
                progress.setMessage("Wait while Uploading...");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();

                UploadFile(progress);


            }
        }

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.e(TAG,"PHOTOPATH CREATED: "+mCurrentPhotoPath );
        return image;
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do here
                    Log.e(TAG,"Permission granted");
                } else {
                    Toast.makeText(mContext, "The app was not allowed to write in your storage", Toast.LENGTH_LONG).show();
                    Log.e(TAG,"Permission STILL NOT granted");
                }
            }
        }
    }

    public void UploadFile(final ProgressDialog progress){

            //creating reference to firebase storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            final StorageReference storageRef = storage.getReferenceFromUrl("gs://diseasedetect-e39f6.appspot.com");    //change the url according to your firebase app

            // Create a reference to image name
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            final String imgName = timeStamp + ".jpg";
            final StorageReference imageRef = storageRef.child(imgName);

            //Photo url
            Uri file = Uri.fromFile(new File(mCurrentPhotoPath));

            UploadTask uploadTask = imageRef.putFile(file);
            //Uploading failure listener
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    Log.e("Debug","Upload failed");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    // ...
                    Log.e("Debug","Upload Success");
                    if(lastEntry!=-1){
                        lastEntry = lastEntry+1;
                        last_entry_ref.setValue(lastEntry);
                    }
                }
            });

            //Download URL Listener
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return imageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        downloadUri = task.getResult();
                        String download_url = downloadUri.toString();
                        last_url_ref.setValue(download_url);
                        Log.e("Debug", "onSuccess: uri= "+ download_url);
                        progress.dismiss();
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

    }

}
