package fr.wcs.teamwinsoft.winnews;

import android.content.Intent;
import android.support.constraint.Constraints;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfilActivity extends AppCompatActivity {

    TextView firstname, lastname, societe, deconnexion;
    EditText etFirstname, etLastname, etSociete;

    View ViewFirstnameCheck, ViewLastnameCheck, ViewSocieteCheck;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        mDatabaseReference = mDatabase.getReference("User");

        firstname = findViewById(R.id.tv_profil_firstname);
        lastname = findViewById(R.id.tv_profil_lastname);
        societe = findViewById(R.id.tv_profil_societe);
        deconnexion = findViewById(R.id.tv_profil_deconnexion);

        etFirstname = findViewById(R.id.et_profil_firstname);
        etLastname = findViewById(R.id.et_profil_lastname);
        etSociete = findViewById(R.id.et_profil_societe);

        ViewFirstnameCheck = findViewById(R.id.layout_profil_firstname);
        ViewLastnameCheck = findViewById(R.id.layout_profil_lastname);
        ViewSocieteCheck = findViewById(R.id.layout_profil_societe);

        // Reglage des OnClick

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference pathID = mDatabase.getReference("User").child(uid);

        pathID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if ((dataSnapshot.child("Profil").child("firstname").getValue() != null) &&
                        (dataSnapshot.child("Profil").child("lastname").getValue() != null))  {

                    String fireFirst = dataSnapshot.child("Profil").child("firstname").getValue().toString();
                    firstname.setText(fireFirst);
                    String fireLast = dataSnapshot.child("Profil").child("lastname").getValue().toString();
                    lastname.setText(fireLast);
                    String fireSociete = dataSnapshot.child("Profil").child("societe").getValue().toString();
                    societe.setText(fireSociete);

                    String icon = "";
                    TextView tvIcon = findViewById(R.id.tv_toolbar_profil);
                    char lettre1 = fireFirst.charAt(0);
                    char lettre2 = fireLast.charAt(0);
                    icon += lettre1;
                    icon += lettre2;
                    icon.toUpperCase();
                    tvIcon.setText(icon);
                    TextView logo = findViewById(R.id.tv_toolbar_profil);
                    logo.setTextColor(getResources().getColor(R.color.jaune));
                    logo.setBackgroundResource(R.drawable.iconprofil2);


                    // Firstname
                    firstname.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            firstname.setVisibility(View.GONE);
                            etFirstname.setVisibility(View.VISIBLE);
                            ViewFirstnameCheck.setVisibility(View.VISIBLE);
                        }
                    });

                    ImageView iv_firstnameValid = findViewById(R.id.iv_profil_firstname_valid);
                    iv_firstnameValid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String writeFirstname = etFirstname.getText().toString();
                            mDatabaseReference.child(user.getUid()).child("Profil").child("firstname").setValue(writeFirstname);
                            firstname.setVisibility(View.VISIBLE);
                            etFirstname.setVisibility(View.GONE);
                            ViewFirstnameCheck.setVisibility(View.GONE);
                        }
                    });

                    ImageView iv_firstnameCancel = findViewById(R.id.iv_profil_firstname_cancel);
                    iv_firstnameCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            firstname.setVisibility(View.VISIBLE);
                            etFirstname.setVisibility(View.GONE);
                            ViewFirstnameCheck.setVisibility(View.GONE);
                        }
                    });


                    // Lastname
                    lastname.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            lastname.setVisibility(View.GONE);
                            etLastname.setVisibility(View.VISIBLE);
                            ViewLastnameCheck.setVisibility(View.VISIBLE);
                        }
                    });

                    ImageView iv_lastnameValid = findViewById(R.id.iv_profil_lastname_valid);
                    iv_lastnameValid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String writeLastname = etLastname.getText().toString();
                            mDatabaseReference.child(user.getUid()).child("Profil").child("lastname").setValue(writeLastname);
                            lastname.setVisibility(View.VISIBLE);
                            etLastname.setVisibility(View.GONE);
                            ViewLastnameCheck.setVisibility(View.GONE);
                        }
                    });

                    ImageView iv_lastnameCancel = findViewById(R.id.iv_profil_lastname_cancel);
                    iv_lastnameCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            lastname.setVisibility(View.VISIBLE);
                            etLastname.setVisibility(View.GONE);
                            ViewLastnameCheck.setVisibility(View.GONE);
                        }
                    });


                    // Societe
                    societe.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            societe.setVisibility(View.GONE);
                            etSociete.setVisibility(View.VISIBLE);
                            ViewSocieteCheck.setVisibility(View.VISIBLE);
                        }
                    });

                    ImageView iv_societeValid = findViewById(R.id.iv_profil_societe_valid);
                    iv_societeValid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String writeSociete = etSociete.getText().toString();
                            mDatabaseReference.child(user.getUid()).child("Profil").child("societe").setValue(writeSociete);
                            societe.setVisibility(View.VISIBLE);
                            etSociete.setVisibility(View.GONE);
                            ViewSocieteCheck.setVisibility(View.GONE);
                        }
                    });

                    ImageView iv_societeCancel = findViewById(R.id.iv_profil_societe_cancel);
                    iv_societeCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            societe.setVisibility(View.VISIBLE);
                            etSociete.setVisibility(View.GONE);
                            ViewSocieteCheck.setVisibility(View.GONE);
                        }
                    });
                }
                else {
                    lastname.setVisibility(View.GONE);
                    etLastname.setVisibility(View.VISIBLE);
                    firstname.setVisibility(View.GONE);
                    etFirstname.setVisibility(View.VISIBLE);
                    societe.setVisibility(View.GONE);
                    etSociete.setVisibility(View.VISIBLE);
                    ViewSocieteCheck.setVisibility(View.VISIBLE);
                    ImageView iv_societeCancel = findViewById(R.id.iv_profil_societe_cancel);
                    iv_societeCancel.setVisibility(View.GONE);
                    deconnexion.setVisibility(View.GONE);
                    ImageView toolProfile = findViewById(R.id.iv_toobar1);
                    ImageView toolPlay = findViewById(R.id.iv_toobar2);
                    ImageView toolContribute = findViewById(R.id.iv_toobar3);
                    toolProfile.setClickable(false);
                    toolPlay.setClickable(false);
                    toolContribute.setClickable(false);


                    ImageView iv_societeValid = findViewById(R.id.iv_profil_societe_valid);
                    iv_societeValid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String writeFirstname = etFirstname.getText().toString();
                            String writeLastname = etLastname.getText().toString();
                            String writeSociete = etSociete.getText().toString();

                            if (writeSociete.isEmpty()) {
                                writeSociete = "Non Renseigné";
                            }

                            UserModel userModel = new UserModel(writeFirstname, writeLastname, writeSociete);

                            if (writeFirstname.isEmpty() || writeLastname.isEmpty()) {
                                Toast.makeText(ProfilActivity.this, "Veuillez entrer un nom et un prénom", Toast.LENGTH_SHORT).show();
                            }
                            else {

                                mDatabaseReference.child(user.getUid()).child("Profil").setValue(userModel);
                                Intent intent = new Intent(ProfilActivity.this, MainActivity.class);
                                startActivity(intent);
                            }

                        }
                    });


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //Deconnexion
        deconnexion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(ProfilActivity.this, ConnectActivity.class));
                finish();
            }
        });



        // Reglage intent toolbar
        ImageView profil = findViewById(R.id.iv_toobar1);
        ImageView lecture = findViewById(R.id.iv_toobar2);
        ImageView contrib = findViewById(R.id.iv_toobar3);

        profil.setBackgroundColor(getResources().getColor(R.color.bleu));

        profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfilActivity.this, ProfilActivity.class));
            }
        });

        lecture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfilActivity.this, MainActivity.class));
            }
        });

        contrib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfilActivity.this, ContribActivity.class));
            }
        });
    }
}
