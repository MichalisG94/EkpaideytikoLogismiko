package com.michalis.ekpaideytikologismiko;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.graphics.Color.RED;
import static com.michalis.ekpaideytikologismiko.MainActivity.help;
import static com.michalis.ekpaideytikologismiko.MainActivity.professors;

public class ProfileActivity extends AppCompatActivity {

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 321;
    public ImageButton buttonEdit;
    private de.hdodenhof.circleimageview.CircleImageView profilePic;
    private TextView nameLabel, surnameLabel, ageLagel, chapter1, chapter2, chapter3, chapter4, userType, revisionChapter;
    private RelativeLayout rel1, parentRel1;
    private String userChosenTask, imageURL;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private ProgressDialog progress;
    private Uri downloadUrl;
    private ArrayList<PieEntry> chapterStats = new ArrayList<>();
    public final String TAG = "ProfileActivity";
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final PieChart pieChart = (PieChart) findViewById(R.id.pieChart);
        final ArrayList<String> labels = new ArrayList<>();
        labels.add("Πρόσθεση");
        labels.add("Αφαίρεση");
        labels.add("Πολλαπλασιασμός");
        labels.add("Διαίρεση");
        labels.add("Επαναληπτικό");
        chapter1 = (TextView) findViewById(R.id.chapter1Text);
        chapter2 = (TextView) findViewById(R.id.chapter2Text);
        chapter3 = (TextView) findViewById(R.id.chapter3Text);
        chapter4 = (TextView) findViewById(R.id.chapter4Text);
        userType = (TextView) findViewById(R.id.userTypeText2);
        revisionChapter = (TextView) findViewById(R.id.chapterRevisionText);
        nameLabel = (TextView) findViewById(R.id.nameText);
        surnameLabel = (TextView) findViewById(R.id.surnameText);
        ageLagel = (TextView) findViewById(R.id.ageText);
        buttonEdit = (ImageButton) findViewById(R.id.editProfile);
        profilePic = (CircleImageView) findViewById(R.id.profilePic);
        rel1 = (RelativeLayout) findViewById(R.id.nestedRel2);
        parentRel1 = (RelativeLayout) findViewById(R.id.relativeLayout2);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        progress = new ProgressDialog(this);

        final String UserId;
        String UserId1;
        if (this.getIntent().getExtras() != null) {
            extras = this.getIntent().getExtras();
            //Otan to activity kaleitai apo to WallActivity
            UserId1 = (String) extras.get("post");
            if(UserId1 == null) {
                UserId1 = (String) extras.get("SearchAdapter");
            }
            //Ean o xrhsths epilexei na paei sto diko tou profil, mporei na kanei edit allios to koumpi gia to edit ginetai invisible to idio isxyei kai gia tin allagh eikonas profil
            if (!UserId1.equals(mFirebaseUser.getUid())) {
                buttonEdit.setVisibility(View.INVISIBLE);
                profilePic.setEnabled(false);
            }
        } else {
            UserId1 = mFirebaseUser.getUid();
        }

        UserId = UserId1;
        databaseReference.child("Profile").child(UserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserProfile userProfile = dataSnapshot.getValue(UserProfile.class);
                //Ean o xrhsths kanei login gia proth fora den yparxoun stoixeia gia na provlithoun
                if( userProfile != null) {
                    nameLabel.setText(userProfile.name);
                    surnameLabel.setText(userProfile.surname);
                    ageLagel.setText(String.valueOf(userProfile.age));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if(!checkProfessor(UserId)) {
            userType.setText("Μαθητης");
            databaseReference.child("User Statistics").child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int chapterValue = -1;
                    if (dataSnapshot.child("Chapter 1").exists()) {
                        chapterValue = Integer.parseInt(dataSnapshot.child("Chapter 1").getValue().toString());
                        chapterStats.add(new PieEntry(chapterValue, labels.get(0)));
                        if(chapterValue < 51) {
                            if (extras != null) {
                                chapter1.setText(chapterValue + "%");

                            } else {
                                chapter1.setText(chapterValue + "%" + " - Επανάληψη!");
                                chapter1.setTextColor(RED);
                            }
                        }
                        else if(chapterValue >= 51)
                            chapter1.setText(chapterValue + "%");
                        else
                            chapter1.setText("");
                    }
                    if (dataSnapshot.child("Chapter 2").exists()) {
                        chapterValue = Integer.parseInt(dataSnapshot.child("Chapter 2").getValue().toString());
                        chapterStats.add(new PieEntry(chapterValue, labels.get(1)));
                        if(chapterValue < 51) {
                            if (extras != null) {
                                chapter2.setText(chapterValue + "%");

                            } else {
                                chapter2.setText(chapterValue + "%" + " - Επανάληψη!");
                                chapter2.setTextColor(RED);
                            }
                        }
                        else if(chapterValue >= 51)
                            chapter2.setText(chapterValue + "%");
                        else
                            chapter2.setText("");
                    }
                    if (dataSnapshot.child("Chapter 3").exists()) {
                        chapterValue = Integer.parseInt(dataSnapshot.child("Chapter 3").getValue().toString());
                        chapterStats.add(new PieEntry(chapterValue, labels.get(2)));
                        if(chapterValue < 51) {
                            if (extras != null) {
                                chapter3.setText(chapterValue + "%");

                            } else {
                                labels.add("Πρόσθεση");
                                chapter3.setText(chapterValue + "%" + " - Επανάληψη!");
                                chapter3.setTextColor(RED);
                            }
                        }
                        else if(chapterValue >= 51)
                            chapter3.setText(chapterValue + "%");
                        else
                            chapter3.setText("");
                    }
                    if (dataSnapshot.child("Chapter 4").exists()) {
                        chapterValue = Integer.parseInt(dataSnapshot.child("Chapter 4").getValue().toString());
                        chapterStats.add(new PieEntry(chapterValue, labels.get(3)));
                        if(chapterValue < 51) {
                            if (extras != null) {
                                chapter4.setText(chapterValue + "%");

                            } else {
                                chapter4.setText(chapterValue + "%" + " - Επανάληψη!");
                                chapter4.setTextColor(RED);
                            }
                        }
                        else if(chapterValue >= 51)
                            chapter4.setText(chapterValue + "%");
                        else
                            chapter4.setText("");
                    }
                    if (dataSnapshot.child("Chapter Revision").exists()) {
                        chapterValue = Integer.parseInt(dataSnapshot.child("Chapter Revision").getValue().toString());
                        chapterStats.add(new PieEntry(chapterValue, labels.get(4)));
                        if(chapterValue < 51) {
                            if (extras != null) {
                                revisionChapter.setText(chapterValue + "%");

                            } else {
                                revisionChapter.setText(chapterValue + "%" + " - Γενική Επανάληψη!");
                                revisionChapter.setTextColor(RED);
                            }
                        }
                        else if(chapterValue >= 51)
                            revisionChapter.setText(chapterValue + "%");
                        else
                            revisionChapter.setText("");
                    }

                    PieDataSet dataset = new PieDataSet(chapterStats, "");
                    dataset.setValueTextSize(20);
                    dataset.setValueTextColor(Color.WHITE);
                    PieData data = new PieData(dataset);
                    dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
                    pieChart.setData(data);
                    pieChart.animateY(800);
                    pieChart.setCenterText("Επιδόσεις μαθητή/κεφάλαιο");
                    pieChart.setDescription(null);
                    pieChart.setEntryLabelColor(Color.BLACK);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            userType.setText("Καθηγητής");
            rel1.setVisibility(View.INVISIBLE);
            parentRel1.removeView(rel1);
            pieChart.setVisibility(View.INVISIBLE);
        }

        databaseReference.child("ImagesUrl").child(UserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    imageURL = dataSnapshot.getValue().toString();
                    Log.i(TAG, "imageURL is: " + imageURL);
                    Picasso.with(ProfileActivity.this).load(imageURL).into(profilePic);
                } else {
                    Picasso.with(ProfileActivity.this).load(R.mipmap.ic_launcher).into(profilePic); //default eikona
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ProfileActivity.this, EditProfileActivity.class);
                ProfileActivity.this.startActivity(i);
            }
        });
    }


    public boolean checkProfessor(String UserId) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        //String UserId = mFirebaseUser.getUid();
        boolean isProfessor = false;
        for(int i=0; i<professors.length; i++) {
            if (professors[i].equals(UserId)) { isProfessor = true; }
        }
        return isProfessor;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.icon_search_menu, menu);
        inflater.inflate(R.menu.icon_help_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch ( item.getItemId() ) {
            case R.id.helpIcon: {
                String msg = "Επεξεργαστείτε το προφίλ σας και δείτε την απόδοση σας στα διαγωνίσματα. \nΕπίσης αναζητήστε και δείτε τα προφίλ των συμμαθητών και των καθηγητών σας.";
                String msgTitle = "Help!";
                help(msgTitle, msg, ProfileActivity.this);
                return true;
            }
        }
        switch ( item.getItemId() ) {
            case R.id.searchIcon: {
                Intent i = new Intent(this,SearchResultsActivity.class);
                startActivity(i);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChosenTask.equals("Take Photo")) {
                        cameraIntent();
                    }
                } else {
                }
                break;
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                }
                break;
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo from Camera", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result;

                if (items[item].equals("Take Photo from Camera")) {
                    userChosenTask ="Take Photo";
                    result = checkPermission(ProfileActivity.this, Manifest.permission.CAMERA, MY_PERMISSIONS_REQUEST_CAMERA);
                    Log.i(TAG, "RESULT IS : " + result);
                    if(result) {
                        cameraIntent();
                    }
                } else if (items[item].equals("Choose from Library")) {
                    userChosenTask ="Choose from Library";
                    result = checkPermission(ProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    Log.i(TAG, "RESULT IS : " + result);
                    if(result) {
                        galleryIntent();
                    }
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context, final String permissionType, final int requestCode)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, permissionType) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, permissionType)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission needed");
                    alertBuilder.setMessage("Permission is necessary in order to complete this action!");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{permissionType}, requestCode);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{permissionType}, requestCode);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            Log.i(TAG, "ResultCode == Activity.RESULT_OK");
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Log.i(TAG, "CAMERA REQUEST");
        progress.setMessage("Saving image...");
        progress.show();
        Bundle extras = data.getExtras();
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] dataBAOS = baos.toByteArray();

        //profilePic.setImageBitmap(thumbnail);

        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl("gs://ekpaideytiko-logismiko.appspot.com");

        StorageReference imagesRef = storageRef.child("Profile_Pics").child(mFirebaseUser.getUid());
        UploadTask uploadTask = imagesRef.putBytes(dataBAOS);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progress.dismiss();
                Log.i(TAG, "onSuccess");
                downloadUrl = taskSnapshot.getDownloadUrl();
                Picasso.with(ProfileActivity.this).load(downloadUrl).into(profilePic);
                Toast.makeText(ProfileActivity.this, "Upload Completed!", Toast.LENGTH_LONG).show();

                databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("ImagesUrl").child(mFirebaseUser.getUid()).setValue(downloadUrl.toString());
                //Update the Picture Url on the WallPost object in Firebase
                databaseReference.child("Posts List").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()) {
                            WallPost posts = data.getValue(WallPost.class);
                            String postsKey = data.getKey();
                            if(posts.getUserID().equals(mFirebaseUser.getUid())) {
                                databaseReference.child("Posts List").child(postsKey).child("picUrl").setValue(downloadUrl.toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Saving Failed - Please try again.", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double percentage = (100* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progress.setMessage("Saving image... " +(int)percentage + "%");
            }
        });
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Log.i(TAG, "FILE SELECT");
        progress.setMessage("Saving image...");
        progress.show();
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri uri = data.getData();

        StorageReference filepath = storageReference.child("Profile_Pics").child(mFirebaseUser.getUid());

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                progress.dismiss();

                //@SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                downloadUrl = taskSnapshot.getDownloadUrl();
                Picasso.with(ProfileActivity.this).load(downloadUrl).into(profilePic);
                Toast.makeText(ProfileActivity.this, "Upload Completed!", Toast.LENGTH_LONG).show();

                databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("ImagesUrl").child(mFirebaseUser.getUid()).setValue(downloadUrl.toString());
                //Update the Picture Url on the WallPost object in Firebase
                databaseReference.child("Posts List").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()) {
                            WallPost posts = data.getValue(WallPost.class);
                            String postsKey = data.getKey();
                            if(posts.getUserID().equals(mFirebaseUser.getUid())) {
                                databaseReference.child("Posts List").child(postsKey).child("picUrl").setValue(downloadUrl.toString());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Saving Failed - Please try again.", Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double percentage = (100* taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progress.setMessage("Saving image... " +(int)percentage + "%");
            }
        });
    }
}
