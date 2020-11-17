package com.cucutain.spartansbarbers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.cucutain.spartansbarbers.Common.Common;
import com.cucutain.spartansbarbers.Models.UserModel;
import com.cucutain.spartansbarbers.Remote.ICloudFunctions;
import com.cucutain.spartansbarbers.Remote.RetrofitCloudClient;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {

    private static final int APP_REQUEST_CODE = 1234;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private List<AuthUI.IdpConfig> providers;

    private AlertDialog dialog;
    CollectionReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        init();




    }

    private void init() {
        providers = Collections.singletonList(new AuthUI.IdpConfig.PhoneBuilder().build());


        userRef = FirebaseFirestore.getInstance().collection(Common.USER_REFERENCES);
        firebaseAuth = FirebaseAuth.getInstance();
        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
        ICloudFunctions cloudFunctions = RetrofitCloudClient.getInstance().create(ICloudFunctions.class);
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //Toast.makeText(MainActivity.this, "ya registrado", Toast.LENGTH_SHORT).show();
                    MainActivity.this.checkUserFromFirebase(user);


                } else {
                    //not login
                    MainActivity.this.phoneLogin();

                }
            }
        };
    }

    private void checkUserFromFirebase(FirebaseUser user) {

        DocumentReference currentUser = userRef.document(user.getPhoneNumber());



        currentUser.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful())
                {
                    DocumentSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists())
                    {
                        Toast.makeText(MainActivity.this, "you already registed", Toast.LENGTH_SHORT).show();
                        UserModel userModel = dataSnapshot.toObject(UserModel.class);
                        goToHomeActivity(userModel);
                    }
                    else
                    {
                        showRegisterDialog(user);
                    }
                }
            }
        });





    }

    private void showRegisterDialog(FirebaseUser user) {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Registro");
        builder.setMessage("Porfavor llena la informacion");



        View itemView = LayoutInflater.from(this).inflate(R.layout.layout_register,null);
        EditText edt_name = itemView.findViewById(R.id.edt_name);
        EditText edt_address = itemView.findViewById(R.id.edt_address);
        EditText edt_phone = itemView.findViewById(R.id.edt_phone);



        //set
        edt_phone.setText(user.getPhoneNumber());

        builder.setView(itemView);
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                MainActivity.this.finish();
            }
        });
        builder.setPositiveButton("Registrar", (dialogInterface, i) -> {
            if (TextUtils.isEmpty(edt_name.getText().toString()))
            {
                Toast.makeText(this, "Por favor ingresa tu nombre", Toast.LENGTH_SHORT).show();

            }else if (TextUtils.isEmpty(edt_address.getText().toString())) {
                Toast.makeText(this, "Por favor ingresa tu direccion", Toast.LENGTH_SHORT).show();


            }
            UserModel userModel = new UserModel();
            userModel.setUid(user.getUid());
            userModel.setName(edt_name.getText().toString());
            userModel.setAddress(edt_address.getText().toString());
            userModel.setPhone(edt_phone.getText().toString());

            userRef.document(edt_phone.getText().toString()).set(userModel)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful())
                        {
                            dialogInterface.dismiss();
                            Toast.makeText(MainActivity.this, "Felicitacione registro exitoso", Toast.LENGTH_SHORT).show();

                            FirebaseInstanceId.getInstance()
                                    .getInstanceId()
                                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                            if(task.isSuccessful()){
                                                Common.updateToken(getBaseContext(), Objects.requireNonNull(task.getResult()).getToken());
                                                goToHomeActivity(userModel);
                                            }


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });



                        }
                    });





        });

        builder.setView(itemView);

        androidx.appcompat.app.AlertDialog dialog= builder.create();
        dialog.show();

    }

    private void goToHomeActivity(UserModel userModel) {

        Common.currentUser = userModel;
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Common.updateToken(getBaseContext(), Objects.requireNonNull(task.getResult()).getToken());
                        startActivity(new Intent(MainActivity.this,HomeActivity.class));

                    }


                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        finish();
    }

    private void phoneLogin() {

        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers).build(),
                APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE)
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (requestCode == RESULT_OK)
            {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            }
            else
            {
                Toast.makeText(this, "Login falló", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if (listener != null)
            firebaseAuth.removeAuthStateListener(listener);
        compositeDisposable.clear();
        super.onStop();
    }


}