package fr.wcs.teamwinsoft.winnews;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;

public class ConnectActivity extends AppCompatActivity {

    private final int REQUEST_LOGIN = 4000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        ImageView logo = findViewById(R.id.iv_logo);
        TextView slogan = findViewById(R.id.tv_connect);
        TextView title = findViewById(R.id.tv_connect_title);

        fadeIn(logo);
        fadeInText(slogan);
        fadeInText(title);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // If already login
            if (!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty()) {
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference pathID = FirebaseDatabase.getInstance().getReference("User").child(uid);
                pathID.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.child("Profil").child("firstname").getValue() != null) &&
                                (dataSnapshot.child("Profil").child("lastname").getValue() != null))  {
                            Intent ConnectIntent = new Intent(ConnectActivity.this, MainActivity.class);
                            startActivity(ConnectIntent);
                            finish();
                        }
                        else {
                            Intent premConnectIntent = new Intent(ConnectActivity.this, ProfilActivity.class);
                            startActivity(premConnectIntent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        else {
            startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder().setAvailableProviders(
                            Arrays.asList(
                                    new AuthUI.IdpConfig
                                            .Builder(AuthUI.PHONE_VERIFICATION_PROVIDER).build())).build(),REQUEST_LOGIN);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LOGIN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            //Sucessfully signed in
            if (resultCode == RESULT_OK) {
                if (!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty()) {
                    Intent connectIntent = new Intent(ConnectActivity.this, ProfilActivity.class);
                    connectIntent.putExtra("phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                    startActivity(connectIntent);
                    finish();
                    return;
                }
                else { //sign in failed
                    if (response == null) {
                        Toast.makeText(this, "Annulé", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                        Toast.makeText(this, "Pas de connexion Internet", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        Toast.makeText(this, "Erreur inconnue", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

                Toast.makeText(this, "Erreur d'authentification inconnue", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void fadeIn(final ImageView image) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000);
        image.startAnimation(fadeIn);
    }

    public void fadeInText(final TextView textView) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(1000);
        textView.startAnimation(fadeIn);
    }
}
