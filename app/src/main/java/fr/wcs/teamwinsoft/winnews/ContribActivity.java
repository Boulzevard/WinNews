package fr.wcs.teamwinsoft.winnews;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

public class ContribActivity extends AppCompatActivity {

    private boolean mPermission;
    private static final int RECORD_VIDEO = 1;
    private static final int PERMISSIONS_REQUEST = 2;

    private Uri videoUri;
    private String getVideoUrl = "";

    LocationManager mLocationManager = null;
    double mLatitude, mLongitude;
    private FusedLocationProviderClient mFusedLocationClient;

    private String name = "";
    private String firstname = "";
    private String icon = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrib);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initLocation();
        checkPermission();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User").child(uid).child("Profil");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                name = userModel.getLastname();
                firstname = userModel.getFirstname();
                TextView tvIcon = findViewById(R.id.tv_toolbar_profil);
                char lettre1 = firstname.charAt(0);
                char lettre2 = name.charAt(0);
                icon += lettre1;
                icon += lettre2;
                icon.toUpperCase();
                tvIcon.setText(icon);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Button newVideo = findViewById(R.id.new_video);
        ImageView btnValidate = findViewById(R.id.iv_contrib_valid);
        final EditText etVideoTitle = findViewById(R.id.video_title);
        final EditText etLink = findViewById(R.id.link);

        Spinner spinnerAddTags = findViewById(R.id.spinner_add_tags);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_add_tags, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down);
        spinnerAddTags.setAdapter(adapter);

        newVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
                startActivityForResult(intent, RECORD_VIDEO);
            }
        });

        /*
        newVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ContribActivity.this);
                builder.setTitle("Ajouter une vidéo")
                        .setPositiveButton("Gallerie", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(intent, RECORD_VIDEO);
                            }
                        })
                        .setNegativeButton("Caméra", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
                                startActivityForResult(intent, RECORD_VIDEO);
                            }
                        })
                        .show();
            }
        });
*/
        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = etVideoTitle.getText().toString();
                final String link = etLink.getText().toString();

                if (title.isEmpty() || link.isEmpty()){
                    if (title.isEmpty()) {
                        Toast.makeText(ContribActivity.this, "Merci de renseigner un titre", Toast.LENGTH_SHORT).show();
                    }
                    if (link.isEmpty()) {
                        Toast.makeText(ContribActivity.this, "Merci d'entrer un lien", Toast.LENGTH_SHORT).show();
                    }
                }

                else {
                    getVideoUrl = videoUri.getPath();
                    Random r = new Random();
                    String numVideo = "video" + String.valueOf(r.nextInt(57) + 52);

                    if (!getVideoUrl.equals("") && getVideoUrl != null) {
                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(numVideo);
                        ref.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUri = taskSnapshot.getDownloadUrl();
                                String video = downloadUri.toString();
                                VideoModel videoModel = new VideoModel(title, link, video, "un tag", mLatitude, mLongitude, name, firstname);
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Videos");
                                ref.push().setValue(videoModel);
                            }
                        });
                    }

                    etVideoTitle.getText().clear();
                    etLink.getText().clear();

                    Toast.makeText(ContribActivity.this, "Vidéo ajoutée", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            mPermission = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSIONS_REQUEST);
        }


        // Reglage intent toolbar
        ImageView profil = findViewById(R.id.iv_toobar1);
        ImageView lecture = findViewById(R.id.iv_toobar2);
        ImageView contrib = findViewById(R.id.iv_toobar3);

        contrib.setBackgroundColor(getResources().getColor(R.color.bleu));
        TextView textcontrib = findViewById(R.id.tv_toolbar_contrib);
        textcontrib.setTextColor(getResources().getColor(R.color.jaune));

        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContribActivity.this, ProfilActivity.class));
            }
        });

        lecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContribActivity.this, MainActivity.class));
            }
        });

        contrib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ContribActivity.this, ContribActivity.class));
            }
        });
    }

    protected void onActivityResult(int request, int result, Intent intent) {
        if (request == RECORD_VIDEO && result == RESULT_OK) {
            videoUri = intent.getData();
        }
    }

    @SuppressLint({"MissingPersmission", "MissingPermission"})
    private void initLocation() {
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            mLatitude = location.getLatitude();
                            mLongitude = location.getLongitude();
                        }
                    }
                });

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLatitude = location.getLatitude();
                mLongitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, locationListener);
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(ContribActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ContribActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(ContribActivity.this,
                        new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        100);
            }
        } else {
            initLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    initLocation();

                } else {

                    // l'autorisation a été refusée
                }
                return;
            }

        }
    }
}