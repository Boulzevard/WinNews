package fr.wcs.teamwinsoft.winnews;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView videoTitle, videoLink, videoAuthor;
    ConstraintLayout containerVideo;

    String tag = "";
    int distance = 0;

    LocationManager mLocationManager = null;
    double mLatitude, mLongitude;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button details = findViewById(R.id.bt_main_video_detail);
        final ConstraintLayout containerDetails = findViewById(R.id.container_detail);
        ImageView validTag = findViewById(R.id.iv_filtre_valid);
        ImageView profil = findViewById(R.id.iv_toobar1);
        ImageView lecture = findViewById(R.id.iv_toobar2);
        ImageView contrib = findViewById(R.id.iv_toobar3);
        final RecyclerView listVideos = findViewById(R.id.container_liste_video);
        SeekBar seekFiltre = findViewById(R.id.sb_filter);
        Spinner spinnerAddTags = findViewById(R.id.spinner_filter_tags);
        final VideoView videoCours = findViewById(R.id.video_en_cours);

        final ArrayList<VideoModel> videoModels = new ArrayList<>();
        final VideoAdapter videoAdapter = new VideoAdapter(videoModels);

        containerVideo = findViewById(R.id.container_video);
        videoTitle = findViewById(R.id.tv_main_video_title);
        videoLink = findViewById(R.id.tv_detail_link);
        videoAuthor = findViewById(R.id.tv_detail_auteur);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_add_tags, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down);
        spinnerAddTags.setAdapter(adapter);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initLocation();
        checkPermission();

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (containerDetails.getVisibility() == View.GONE) {
                    containerDetails.setVisibility(View.VISIBLE);
                } else {
                    containerDetails.setVisibility(View.GONE);
                }
            }
        });


        seekFiltre.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                final TextView tvSeekbarKm = findViewById(R.id.tv_filter_km);
                tvSeekbarKm.setText(String.format(getString(R.string.km), i));

                if (i > 500) {
                    tvSeekbarKm.setText(R.string.max_km);
                }
                distance = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        lecture.setBackgroundColor(getResources().getColor(R.color.bleu));
        TextView textlecture = findViewById(R.id.tv_toobar_lecture);
        textlecture.setTextColor(getResources().getColor(R.color.jaune));

        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfilActivity.class));
            }
        });

        lecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });

        contrib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ContribActivity.class));
            }
        });


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        listVideos.setLayoutManager(layoutManager);
        listVideos.setAdapter(videoAdapter);

        spinnerAddTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
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

        validTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Videos");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        videoModels.clear();
                        for (DataSnapshot videoSnap : dataSnapshot.getChildren()) {
                            VideoModel videoModel = videoSnap.getValue(VideoModel.class);
                            Location loc = new Location("");
                            loc.setLatitude(videoModel.getLatitude());
                            loc.setLongitude(videoModel.getLongitude());
                            Location myLoc = new Location("");
                            myLoc.setLatitude(mLatitude);
                            myLoc.setLongitude(mLongitude);
                            if (tag.equals(videoModel.getTags()) && ((int) myLoc.distanceTo(loc) / 1000) < distance) {
                                videoModels.add(new VideoModel(videoModel.getTitle(), videoModel.getLink(),
                                        videoModel.getVideo(), videoModel.getTags(), videoModel.getLatitude(),
                                        videoModel.getLongitude(), videoModel.getName(), videoModel.getFirstname()));
                            }
                        }
                        videoAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        listVideos.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), listVideos, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                containerVideo.setVisibility(View.VISIBLE);
                VideoModel video = videoModels.get(position);
                videoCours.setVideoPath(video.getVideo());
                videoCours.start();
                videoTitle.setText(video.getTitle());
                videoLink.setText(video.getLink());
                if (video.getFirstname() != null && video.getName() != null) {
                    videoAuthor.setText(String.format(getString(R.string.firstname_name), video.getFirstname(), video.getName()));
                } else {
                    videoAuthor.setText("");
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        DatabaseReference refProfil = FirebaseDatabase.getInstance().getReference("User").child(uid).child("Profil");
        refProfil.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel userModel = dataSnapshot.getValue(UserModel.class);
                String name, firstname, icon;
                name = userModel.getLastname();
                firstname = userModel.getFirstname();
                icon = "";
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
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
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