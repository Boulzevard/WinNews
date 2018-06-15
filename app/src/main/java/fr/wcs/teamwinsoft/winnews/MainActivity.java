package fr.wcs.teamwinsoft.winnews;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView videoTitle, videoLink, videoAuthor;

    String tag = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Spinner spinnerAddTags = findViewById(R.id.spinner_filter_tags);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_add_tags, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_drop_down);
        spinnerAddTags.setAdapter(adapter);

        Button details = findViewById(R.id.bt_main_video_detail);
        final ConstraintLayout containerDetails = findViewById(R.id.container_detail);
        ImageView validTag = findViewById(R.id.iv_filtre_valid);

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

        videoTitle = findViewById(R.id.tv_main_video_title);
        videoLink = findViewById(R.id.tv_detail_link);
        videoAuthor = findViewById(R.id.tv_detail_auteur);

        SeekBar seekFiltre = findViewById(R.id.sb_filter);
        seekFiltre.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                final TextView tvSeekbarKm = findViewById(R.id.tv_filter_km);
                tvSeekbarKm.setText(i + " km");

                if (i > 500){
                    tvSeekbarKm.setText("+500 km");
                }

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Reglage intent toolbar
        ImageView profil = findViewById(R.id.iv_toobar1);
        ImageView lecture = findViewById(R.id.iv_toobar2);
        ImageView contrib = findViewById(R.id.iv_toobar3);
        final VideoView videoCours = findViewById(R.id.video_en_cours);

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

        final ArrayList<VideoModel> videoModels = new ArrayList<>();

        final VideoAdapter videoAdapter = new VideoAdapter(videoModels);

        final RecyclerView listVideos = findViewById(R.id.container_liste_video);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        listVideos.setLayoutManager(layoutManager);
        listVideos.setAdapter(videoAdapter);

        spinnerAddTags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Toast.makeText(MainActivity.this, "Merci de sélectionner un tag", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        tag = "Ecologie";
                        break;
                    case 2:
                        tag = "Social";
                        break;
                    case 3:
                        tag = "Economie";
                        break;
                    case 4:
                        tag = "Technologie";
                        break;
                    case 5:
                        tag = "Santé";
                        break;
                    case 6:
                        tag = "Evénement";
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
                        for (DataSnapshot videoSnap: dataSnapshot.getChildren()) {
                            VideoModel videoModel = videoSnap.getValue(VideoModel.class);
                            if (tag.equals(videoModel.getTags())) {
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
                VideoModel video = videoModels.get(position);
                videoCours.setVideoPath(video.getVideo());
                videoCours.start();
                videoTitle.setText(video.getTitle());
                videoLink.setText(video.getLink());
                if (video.getFirstname() != null && video.getName() != null) {
                    videoAuthor.setText(video.getFirstname() + " " + video.getName());
                } else {
                    videoAuthor.setText("");
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();

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


    }
}