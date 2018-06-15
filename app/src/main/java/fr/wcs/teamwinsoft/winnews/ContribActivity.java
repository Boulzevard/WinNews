package fr.wcs.teamwinsoft.winnews;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

public class ContribActivity extends AppCompatActivity {

    private static final int RECORD_VIDEO = 1;
    private static final int PERMISSIONS_REQUEST = 2;
    LocationManager mLocationManager = null;
    double mLatitude, mLongitude;
    private boolean mPermission;
    private Uri videoUri;
    private String getVideoUrl = "";
    private FusedLocationProviderClient mFusedLocationClient;

    private String name = "";
    private String firstname = "";
    private String icon = "";
    private String tag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contrib);

        Button newVideo = findViewById(R.id.new_video);
        final EditText etVideoTitle = findViewById(R.id.video_title);
        final EditText etLink = findViewById(R.id.link);
        ImageView btnValidate = findViewById(R.id.iv_contrib_valid);
        ImageView profil = findViewById(R.id.iv_toobar1);
        ImageView lecture = findViewById(R.id.iv_toobar2);
        ImageView contrib = findViewById(R.id.iv_toobar3);
        Spinner spinnerAddTags = findViewById(R.id.spinner_add_tags);
        TextView textcontrib = findViewById(R.id.tv_toolbar_contrib);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_add_tags, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down);
        spinnerAddTags.setAdapter(adapter);

        contrib.setBackgroundColor(getResources().getColor(R.color.bleu));
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

        // Récupère la dernière position connue
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initLocation();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("User").child(uid).child("Profil");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                name = userModel.getLastname();
                firstname = userModel.getFirstname();
                TextView tvIcon = findViewById(R.id.tv_toolbar_profil);
                char letter1 = firstname.charAt(0);
                char letter2 = name.charAt(0);
                icon += letter1;
                icon += letter2;
                icon.toUpperCase();
                tvIcon.setText(icon);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

        spinnerAddTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        tag = "";
                        break;
                    case 1:
                        tag = getString(R.string.ecology);
                        break;
                    case 2:
                        tag = getString(R.string.social);
                        break;
                    case 3:
                        tag = getString(R.string.economy);
                        break;
                    case 4:
                        tag = getString(R.string.technology);
                        break;
                    case 5:
                        tag = getString(R.string.health);
                        break;
                    case 6:
                        tag = getString(R.string.event);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = etVideoTitle.getText().toString();
                final String link = etLink.getText().toString();

                if (title.isEmpty() || link.isEmpty() || tag.equals("")) {
                    if (title.isEmpty()) {
                        Toast.makeText(ContribActivity.this, R.string.enter_title, Toast.LENGTH_SHORT).show();
                    } else if (link.isEmpty()) {
                        Toast.makeText(ContribActivity.this, R.string.enter_link, Toast.LENGTH_SHORT).show();
                    } else if (tag.equals("")) {
                        Toast.makeText(ContribActivity.this, R.string.select_tag, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    getVideoUrl = videoUri.getPath();
                    Random r = new Random();
                    String numVideo = getString(R.string.video) + String.valueOf(r.nextInt(20) + 7);

                    if (!getVideoUrl.equals("") && getVideoUrl != null) {
                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(numVideo);
                        ref.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri downloadUri = taskSnapshot.getDownloadUrl();
                                String video = downloadUri.toString();
                                VideoModel videoModel = new VideoModel(title, link, video, tag, mLatitude, mLongitude, name, firstname);
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Videos");
                                ref.push().setValue(videoModel);
                                Toast.makeText(ContribActivity.this, R.string.finish_load, Toast.LENGTH_SHORT).show();
                            }
                        })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                                        Toast.makeText(ContribActivity.this, String.format(getString(R.string.loading), progress), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    etVideoTitle.getText().clear();
                    etLink.getText().clear();
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
    }

    protected void onActivityResult(int request, int result, Intent intent) {
        if (request == RECORD_VIDEO && result == RESULT_OK) {
            videoUri = intent.getData();
        }
    }

    @SuppressLint("MissingPermission")
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
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
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