package com.alphaford.pimapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alphaford.pimapplication.StatsDay.StatsActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    EditText emailText,passwordText;
    Button loginButton;
    TextView signupLink;
    public SharedPreferences.Editor editor;
    public SharedPreferences sharedPref;
    ConnexionManager cnx = new ConnexionManager();

    String HttpUrl = cnx.getPath(":8080/api/authenticate");
    RequestQueue requestQueue;

    String EmailHolder, PasswordHolder,success,nom,type,adresse, message;

    public static String token="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailText=(EditText) findViewById(R.id.input_email);
        passwordText=(EditText) findViewById(R.id.input_password);
        loginButton=(Button) findViewById(R.id.btn_login);
        signupLink=(TextView) findViewById(R.id.link_signup);

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
        requestQueue = Volley.newRequestQueue(LoginActivity.this);
        sharedPref = this.getSharedPreferences( "myPref", MODE_PRIVATE);
        editor = sharedPref.edit();

//        if(sharedPref.getInt("logIn",0)==1) {
//           Intent i = new Intent(this, StatsActivity.class);
//            startActivity(i);
//        }

        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!validate()) {

                    return;
                } else {
                  ;
                    EmailHolder = emailText.getText().toString().trim();
                    PasswordHolder = passwordText.getText().toString().trim();
                    UserLogin();
                    //********************FirebaseMessaging.getInstance().subscribeToTopic("news");
                    editor.putInt("logIn", 1);
                    editor.apply();

                }
//                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                startActivity(intent);


            }
        });


    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();



        String email = emailText.getText().toString();

        String password = passwordText.getText().toString();




        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();

                        Intent i =new Intent(LoginActivity.this,StatsActivity.class);
                        startActivity(i);

                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }
    public void UserLogin() {

        // Showing progress dialog at User registration time.
        /*progressDialog.setMessage("Please Wait");
        progressDialog.show();*/

        StringRequest stringRequest = new StringRequest(Request.Method.POST, HttpUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String ServerResponse) {


                       // if(!ServerResponse.equalsIgnoreCase("no")) {
                            try {
                                JSONObject jsonObject = new JSONObject(ServerResponse);
                                success= jsonObject.getString("success");

                                message = jsonObject.getString("message");
                                if (message.equals("User authenticated!")){
                                    //token=jsonObject.getString("token");
                                    editor.putString("token", jsonObject.getString("token"));
                                    editor.putString("username",jsonObject.getString("username"));
                                    editor.putString("permission",jsonObject.getString("permission"));
                                    editor.apply();
                                    Log.d("token",token);
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                                    emailText.setText("");
                                    passwordText.setText("");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                            Intent intent = new Intent(LoginActivity.this, StatsActivity.class);
                            startActivity(intent);
                            //finish();

                        }




                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        // Hiding the progress dialog after all task complete.


                        // Showing error message if something goes wrong.
                        Toast.makeText(LoginActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {

                // Creating Map String Params.
                Map<String, String> params = new HashMap<String, String>();

                // Adding All values to Params.
                // The firs argument should be same sa your MySQL database table columns.
                params.put("username", EmailHolder);
                params.put("password", PasswordHolder);

                return params;
            }

        };
        requestQueue.add(stringRequest);

    }

    public boolean validate() {
        boolean valid = true;

        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

       /* if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("enter a valid email address");
            valid = false;
        } else {
            emailText.setError(null);
        }*/

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            passwordText.setError(null);
        }

        return valid;
    }
}

