package com.example.jstock;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.util.IOUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Global {
    public static FirebaseDatabase database;
    public static DatabaseReference dbRef;
    public static FirebaseAuth mAuth;

    public static User loggedUser;
    public static String UID; // For searching in the database

    public static String commonPasswords = "123456|password|12345678|qwerty|abc123|12345|monkey|111111|consumer|letmein|1234|dragon|trustno1|baseball|gizmodo|whatever|superman|1234567|sunshine|iloveyou|fuckyou|starwars|shadow|princess|cheese|123123|computer|gawker|football|blahblah|nintendo|0|soccer|654321|asdfasdf|master|michael|passw0rd|hello|kotaku|pepper|jennifer|666666|welcome|buster|Password|batman|1q2w3e4r|maggie|michelle|pokemon|killer|andrew|internet|biteme|orange|jordan|ginger|123|aaaaaa|tigger|charlie|chicken|nothing|fuckoff|deadspin|valleywa|qwerty12|george|swordfis|summer|asdf|matthew|asdfgh|mustang|yankees|hannah|asdfghjk|1qaz2wsx|cookie|midnight|123qwe|scooter|purple|banana|matrix|jezebel|daniel|hunter|freedom|secret|redsox|spiderma|phoenix|joshua|jessica|asshole|asdf1234|william|qwertyui|jackson|foobar|nicole|123321|peanut|samantha|mickey|booger|poop|hockey|thx1138|ashley|silver|gizmodo1|chocolat|booboo|metallic|1q2w3e|bailey|google|babygirl|thomas|simpsons|remember|gateway|oliver|monster|guitar|qazwsx|taylor|madison|anthony|justin|elizabet|1111|november|drowssap|bubbles|startrek|monkey12|diamond|coffee|butterfl|brooklyn|amanda|adidas|test|love|wordpass|sparky|morgan|merlin|maverick|elephant|Highlife|poopoo|nirvana|liverpoo|lauren|stupid|chelsea|compaq|boomer|yellow|sophie|q1w2e3r4|fucker|coolness|cocacola|blink182|zxcvbnm|snowball|snoopy|gundam|alexande|rachel|jasmine|danielle|basketba|7777777|thunder|snickers|patrick|darkness|boston|abcd1234|pumpkin|creative|88888888|smokey|sample12|godzilla|december|corvette|brandon|bandit|123abc|voodoo|turtle|spider|london|jonathan|hello123|hahaha|chicago|austin|tennis|scooby|naruto|mercedes|maxwell|fluffy|eagles|11111111|penguin|muffin|bullshit|steelers|jasper|flower|ferrari|slipknot|pookie|murphy|joseph|calvin|apples|159753|tucker|martin|11235813|whocares|pineappl|nicholas|jackass|goober|chester|8675309|222222|winston|somethin|please|dakota|112233|rosebud|dallas|696969|shithead|popcorn";

    public static void Login(String email, String password, AppCompatActivity activity) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();

                            dbRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                if(snapshot.getKey().equals(user.getUid())) {
                                                    loggedUser = snapshot.getValue(User.class);
                                                    UID = snapshot.getKey();
                                                    break;
                                                }
                                            }

                                            SharedPreferences sharedPreferences = activity.getSharedPreferences("LoggedUser", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("Email", loggedUser.getEmail());
                                            editor.commit();

                                            Intent intent = new Intent(activity, HomeActivity.class);
                                            activity.startActivity(intent);
                                            activity.finish();
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(activity, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static Stock generateStock(String stockName, Context context) throws IOException {
        Stock stock = new Stock();

        stock.setName(stockName);

        long DAY_MS = 1000 * 60 * 60 * 24;
        
        Date dateTo = new Date();
        Date dateFrom = new Date(System.currentTimeMillis() - (3 * DAY_MS));

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String strDateTo = dateFormat.format(dateTo);
        String strDateFrom = dateFormat.format(dateFrom);

        String URL = "https://api.polygon.io/v2/aggs/ticker/" + stockName.trim() + "/range/1/day/" + strDateFrom + "/" + strDateTo + "?adjusted=true&sort=asc&limit=120&apiKey=mQqhLmXA2YOpheLfnGXtefgMADDsHiii";

        java.net.URL url = new URL(URL);
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            StringBuilder sb = new StringBuilder();
            for(int ch; (ch = in.read()) != -1; ) {
                sb.append((char) ch);
            }

            String rsp = sb.toString();
            JSONObject object = new JSONObject(rsp);

            int open = (int) Double.parseDouble(object.getJSONArray("results").getJSONObject(0).getString("o"));
            int close = (int) Double.parseDouble(object.getJSONArray("results").getJSONObject(0).getString("c"));
            int high = (int) Double.parseDouble(object.getJSONArray("results").getJSONObject(0).getString("h"));
            int low = (int) Double.parseDouble(object.getJSONArray("results").getJSONObject(0).getString("l"));
            int volume = (int) Double.parseDouble(object.getJSONArray("results").getJSONObject(0).getString("v"));

            stock.setCheckTime(strDateTo);
            stock.setOpenPrice(Integer.toString(open));
            stock.setPrevClosePrice(Integer.toString(close));
            stock.setHighPrice(Integer.toString(high));
            stock.setLowPrice(Integer.toString(low));
            stock.setVolume(Integer.toString(volume));
        }
        catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(context, HomeActivity.class);
            intent.putExtra("INVALID", true);
            context.startActivity(intent);
        }

        return stock;
    }
}
