package com.example.atif.maps_;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class EditProfileActivity extends AppCompatActivity {
    private Button uploadImg,chooseImg, saveChanges;
    private ImageView imgView;

    private int PICK_IMAGE_REQUEST = 111;
    private Uri filePath;
    private ProgressDialog pd;
    private FirebaseUser user;
    private EditText fName,lName;
    private DatabaseReference mDatabase;
    private FirebaseUser loggedUser;
    private UserInfo info;


    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReferenceFromUrl("gs://commutr-149323.appspot.com");    //change the url according to your firebase app


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        uploadImg = (Button) findViewById(R.id.btn_uploadImg);
        chooseImg = (Button) findViewById(R.id.btn_chooseImg);
        saveChanges = (Button) findViewById(R.id.btn_saveChanges);
        fName = (EditText) findViewById(R.id.fName_editProfile);
        lName = (EditText) findViewById(R.id.lName_editProfile);
        imgView = (ImageView) findViewById(R.id.imgView);

        user = FirebaseAuth.getInstance().getCurrentUser();

        //progress circle
        pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");
        //**

        mDatabase = FirebaseDatabase.getInstance().getReference();
        loggedUser= FirebaseAuth.getInstance().getCurrentUser();


        //Reading the userInfo object from db
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                String uid = loggedUser.getUid();
                info = dataSnapshot.child("users").child(uid).getValue(UserInfo.class);

                if(info!=null) {
                    fName.setText(info.getFirstName());
                    lName.setText(info.getLastName());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("dbtest", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(postListener);
        //** Done reading

        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
                Log.v("editProfileTest","finished running chooseImg");
            }
        });

        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (filePath != null) {
                    pd.show();

                    StorageReference childRef = storageRef.child("Images").child(user.getUid());

                    //uploading the image
                    UploadTask uploadTask = childRef.putFile(filePath);

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pd.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Upload successful", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Upload Failed -> " + e, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(EditProfileActivity.this, "Select an image", Toast.LENGTH_SHORT).show();
                }
            }
        });


        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uid = loggedUser.getUid();

                if(info==null) { //for new user
                    UserInfo newInfo= new UserInfo();
                        newInfo.setFirstName(fName.getText().toString());
                        newInfo.setLastName(lName.getText().toString());
                        mDatabase.child("users").child(uid).setValue(newInfo);

                    }
                    else if(info!=null) { //changes for existing user
                    info.setFirstName(fName.getText().toString());
                    info.setLastName(lName.getText().toString());
                    mDatabase.child("users").child(uid).setValue(info);
                }
                Toast.makeText(EditProfileActivity.this, "Profile Updated Successfully", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(EditProfileActivity.this, MapsActivity.class);
                startActivity(intent);



            }
        });
        //Bottom Navigation Bar
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBarEditProfile);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_planner) {
                    Intent planner = new Intent(EditProfileActivity.this, MapsActivity.class);
                    startActivity(planner);
                } else if (tabId == R.id.tab_nearby) {
                    Intent offlineMap = new Intent(EditProfileActivity.this, offMap.class);
                    startActivity(offlineMap);
                } /*else if (tabId == R.id.tab_schedule) {
                    Intent schedule = new Intent(MapsActivity.this, trainSchedule.class);
                    startActivity(schedule);
                } */ else if (tabId == R.id.tab_alerts) {
                    Intent alerts = new Intent(EditProfileActivity.this, AlertActivity.class);
                    startActivity(alerts);
                } else if (tabId == R.id.tab_profile) {
                    Intent setting = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    startActivity(setting);
                }
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("editProfileTest","Started running onActivityResult");

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //getting image from gallery
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Log.v("editProfileTest","Worked");

                //Setting image to ImageView
                imgView.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Log.v("editProfileTest","Didnt work");

            }
        }
        Log.v("editProfileTest","Finished running on activity result method");
    }






}
