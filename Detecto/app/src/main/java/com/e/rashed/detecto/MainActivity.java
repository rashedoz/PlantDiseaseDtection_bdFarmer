package com.e.rashed.detecto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaPlayer;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
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

    private StorageReference mStorageRef;
    Integer lastEntry= -1;
    DatabaseReference last_entry_ref;
    DatabaseReference last_url_ref;
    DatabaseReference last_name;

    public String download_url;


    Uri downloadUri;


    public Boolean captureImg = false;

    private Context mContext=MainActivity.this;
    private static final int REQUEST = 112;
    private int PICK_IMAGE_REQUEST = 121;
    private int REQUEST_TAKE_PHOTO = 911;
    static final int REQUEST_IMAGE_CAPTURE = 1;

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
                Log.e(TAG,"Has Permissions");
            }
        } else {
            //do here
            Log.e(TAG,"Lower SDK Version");
        }


        ImageView cameraBtn = (ImageView) findViewById(R.id.cameraBtn);
        mImageView = (ImageView) findViewById(R.id.imageView);

        Button hmbtn = (Button) findViewById(R.id.button);
        Button galleryBtn = (Button) findViewById(R.id.galleryBtn);


        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.chobi_uthbe);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                mp=null;
            }
        });
        final Context mContext = this;

        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(findViewById(R.id.cameraBtn), "ছবি তুলুন ", "এখানে  চাপলে  ছবি উঠবে")
                        // All options below are optional
                        .outerCircleColor(R.color.colorAccent)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.colorPrimary)   // Specify a color for the target circle
                        .titleTextSize(50)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.bg)      // Specify the color of the title text
                        .descriptionTextSize(30)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.corngreen)  // Specify the color of the description text
                        .textColor(R.color.yellow)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.bg)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        // Specify a custom drawable to draw as the target
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional

                        final MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.ager_menu);
                        mediaPlayer.start(); // no need to call prepare(); create() does that for you
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.reset();
                                mp.release();
                                mp=null;
                            }
                        });
                        targetView(mContext,R.id.galleryBtn);


//                        Toast.makeText(MainActivity.this,
//                                "Ready", Toast.LENGTH_LONG).show();
                    }
                });

        hmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(i);
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);



            }
        });

        Log.e("Main","View initialized");
        FirebaseApp.initializeApp(this);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        last_entry_ref = database.getReference("last_entry");
        last_url_ref = database.getReference("last_url");
        last_name = database.getReference("last_name");



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



        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
    }




    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
//For storage save
//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        // Ensure that there's a camera activity to handle the intent
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            // Create the File where the photo should go
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//            }
//            // Continue only if the File was successfully created
//            if (photoFile != null) {
//                Uri photoURI = FileProvider.getUriForFile(this, "com.e.rashed.detecto.fileprovider",photoFile);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//                Log.e(TAG,"Camera Intent");
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e(TAG,"Onactivity Result:"+requestCode);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, 512, 512, true);
            mImageView.setImageBitmap(imageBitmap);

            ProgressDialog progress = new ProgressDialog(this);
                progress.setTitle("Loading");
                progress.setMessage("ছবি পাঠানো হচ্ছে....");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();

                final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.chobi_pathano_hocche );
                mediaPlayer.start(); // no need to call prepare(); create() does that for you
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.reset();
                        mp.release();
                        mp=null;
                    }
                });

                //Upload Original File
                //UploadFile(progress);

                //Upload resized bitmap
                UploadBitmap(imageBitmap,progress);
        }
        //Camera option request
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Log.e(TAG,"Result ok file created "+mCurrentPhotoPath );
//            File file = new File(mCurrentPhotoPath);
//            Log.e(TAG,"Result ok file created "+mCurrentPhotoPath );
//            Context context = this;
//            Bitmap bitmap = null;
//            Bitmap resized = null;
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(file));
//
//                //Resizing Bitmap for uploading
//                 resized = Bitmap.createScaledBitmap(bitmap, 512, 512, true);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (bitmap != null) {
//                Log.e(TAG,"PHOTO BITMAP CREATED");
//
//                //Loading image
//                Bitmap bmImg = BitmapFactory.decodeFile(mCurrentPhotoPath);
//                mImageView.setImageBitmap(bmImg);
//
//                ProgressDialog progress = new ProgressDialog(this);
//                progress.setTitle("Loading");
//                progress.setMessage("ছবি পাঠানো হচ্ছে....");
//                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
//                progress.show();
//
//                final MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.chobi_upload_hocche );
//                mediaPlayer.start(); // no need to call prepare(); create() does that for you
//                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//                    @Override
//                    public void onCompletion(MediaPlayer mp) {
//                        mp.reset();
//                        mp.release();
//                        mp=null;
//                    }
//                });
//
//                //Upload Original File
//                //UploadFile(progress);
//
//                //Upload resized bitmap
//                UploadBitmap(resized,progress);
//
//
//            }
//        }

        //Gallery option request
        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);

                //Resizing Bitmap for uploading
                Bitmap resized = Bitmap.createScaledBitmap(bitmap, 512, 512, true);

                ProgressDialog progress = new ProgressDialog(this);
                progress.setTitle("Loading");
                progress.setMessage("ছবি পাঠানো হচ্ছে....");
                progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress.show();

                final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.chobi_pathano_hocche );
                mediaPlayer.start(); // no need to call prepare(); create() does that for you
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.reset();
                        mp.release();
                        mp=null;
                    }
                });


                UploadBitmap(resized,progress);

            } catch (IOException e) {
                e.printStackTrace();
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
                        //Updating last entry
                        if(lastEntry!=-1){
                            lastEntry = lastEntry+1;
                            last_entry_ref.setValue(lastEntry);
                        }
                        progress.dismiss();

                        captureImg = true;


                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });

    }

    public void UploadBitmap(Bitmap bitmap,final ProgressDialog progress){

        //creating reference to firebase storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://diseasedetect-e39f6.appspot.com");    //change the url according to your firebase app

        // Create a reference to image name
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        final String imgName = timeStamp + ".jpg";
        final StorageReference imageRef = storageRef.child(imgName);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);

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
                    download_url = downloadUri.toString();
                    last_url_ref.setValue(download_url);
                    Log.e("Debug", "onSuccess: uri= "+ download_url);
                    //Updating last entry
                    if(lastEntry!=-1){
                        lastEntry = lastEntry+1;
                        last_entry_ref.setValue(lastEntry);
                    }
                    progress.dismiss();

                    captureImg = true;
                    CompleteUpload();


                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    public void CompleteUpload(){
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Processing..");
        progress.setMessage("অপেক্ষা করুন....");
        progress.setIcon(R.drawable.camera);
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.opekkha_korun);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                mp=null;
                progress.dismiss();

                //Call Server Uri
                SendServerRequest();
                Intent i = new Intent(getApplicationContext(),LoadingActivity.class);
                i.putExtra("download_url",download_url);
                startActivity(i);


            }
        });
    }

    public void SendServerRequest(){

        final

            // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(this);
                    String url ="http://192.168.0.120:5000/";
                    String server_ur = "http://34.73.136.82:5000";

            // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, server_ur,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Display the first 500 characters of the response string.
//                                    Log.e("Req","Response is: " + response.substring(0, 500));
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Req","No response from server!");
                        }
                    });

            // Add the request to the RequestQueue.
                    queue.add(stringRequest);
    }

    public void targetView(Context mContext, Integer id){

        TapTargetView.showFor((Activity) mContext,                 // `this` is an Activity
                TapTarget.forView(findViewById(id), "ছবির মেনু তে যান ", "এখানে  চাপলে আগের ছবি পাঠানো যাবে")
                        // All options below are optional
                        .outerCircleColor(R.color.colorAccent)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.colorPrimary)   // Specify a color for the target circle
                        .titleTextSize(50)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.bg)      // Specify the color of the title text
                        .descriptionTextSize(30)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.corngreen)  // Specify the color of the description text
                        .textColor(R.color.yellow)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.bg)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        // Specify a custom drawable to draw as the target
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional

                    }
                });
    }



}
