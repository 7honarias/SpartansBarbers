package com.cucutain.spartansbarbers;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cucutain.spartansbarbers.Common.Common;
import com.cucutain.spartansbarbers.Fragments.HomeFragment;
import com.cucutain.spartansbarbers.Fragments.ShopingFragment;
import com.cucutain.spartansbarbers.Models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    BottomSheetDialog bottomSheetDialog;

    CollectionReference userRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;

    @Override
    protected void onResume() {

        super.onResume();
        checkRatingDialog();
    }

    private void checkRatingDialog() {
        Paper.init(this);
        String dataSerialized = Paper.book().read(Common.RATING_INFORMATION_KEY,"");
        if (!TextUtils.isEmpty(dataSerialized))
        {
            Map<String,String> dataReceived = new Gson()
                    .fromJson(dataSerialized,new TypeToken<Map<String, String>>(){}.getType());

            if (dataReceived!=null)
            {
                Common.showRatingDialog(HomeActivity.this,
                        dataReceived.get(Common.RATING_STATE_KEY),
                        dataReceived.get(Common.RATING_SALON_ID),
                        dataReceived.get(Common.RATING_SALON_NAME),
                        dataReceived.get(Common.RATING_BARBER_ID));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userRef = FirebaseFirestore.getInstance().collection(Common.USER_REFERENCES);
        firebaseAuth = FirebaseAuth.getInstance();
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    DocumentReference currentUser = userRef.document(user.getPhoneNumber().toString());



                    currentUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful())
                            {
                                DocumentSnapshot dataSnapshot = task.getResult();
                                if (dataSnapshot.exists())
                                {
                                    UserModel userModel = dataSnapshot.toObject(UserModel.class);
                                    Common.currentUser = userModel;
                                }

                            }
                        }
                    });
                } else {
                    Toast.makeText(HomeActivity.this, "error", Toast.LENGTH_SHORT).show();

                }
            }
        };

        

        Paper.init(HomeActivity.this);
        if (user!=null)
            Paper.book().write(Common.LOGGED_KEY, Objects.requireNonNull(user.getPhoneNumber()));






        ButterKnife.bind(HomeActivity.this);

        //
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            Fragment fragment = null;

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_home)
                    fragment = new HomeFragment();

                if (menuItem.getItemId() == R.id.action_shopping)
                    fragment = new ShopingFragment();

                return loadFrangment(fragment);
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.action_home);




    }

    private boolean loadFrangment(Fragment fragment) {
        if (fragment != null)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment)
                    .commit();
            return true;
        }
        return false;
    }



}
