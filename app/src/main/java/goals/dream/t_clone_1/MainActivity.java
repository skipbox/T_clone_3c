package goals.dream.t_clone_1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Random;

import goals.dream.t_clone_1.R;
import goals.dream.t_clone_1.SettingsActivity;

import static android.R.attr.defaultValue;
import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static goals.dream.t_clone_1.R.id.b_click_count;

public class MainActivity extends AppCompatActivity {
    //SharedPreferences========================================================
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static final String key_1 = "k1";
    public static final String key_2 = "k2";
    public static final String key_3 = "k3";
    public static final String key_4 = "k4";
    public static final String key_5 = "k5";
    //can also use integers
    SharedPreferences sharedpreferences;
    //SharedPreferences========================================================

    String email_phone_key = "xxxxxx@gmail.com";
    String password_phone_key = "password_xxx";

    String email;
    String password;

    int current_step;
    int current_step_count;

    int timer_main_delay;
    int timer_sub_delay;
    boolean auto_post = false;
    long post_started_time;

    String title;
    String body;

    String location;
    String geographic_area;
    String postal_code;

    String category;
    String sub_category;
    String m4w;
    String age;

    String employment_type;
    String remuneration;

    String send_line_to_log;
    String next_ad_post_id;


    TextView my_text_log;
    EditText my_row_id;
    WebView wv1;

    Button btn_start_main_timer;
    Button btn_stop_main_timer;
    Button btn_click_counter;
    Button btn_read_nextad_timer_main;
    Button btn_timer_sub;

    private PowerManager.WakeLock wl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        my_text_log = (TextView) findViewById(R.id.text_log);
        my_row_id = (EditText) findViewById(R.id.text_row_id);

        btn_start_main_timer = (Button) findViewById(R.id.b_timer_main_start);
        btn_stop_main_timer = (Button) findViewById(R.id.b_timer_main_stop);
        btn_click_counter = (Button) findViewById(b_click_count);
        btn_read_nextad_timer_main = (Button) findViewById(R.id.b_read_nextad_timer_main);
        btn_timer_sub = (Button) findViewById(R.id.b_timer_sub);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");


        //SharedPreferences========================================================
        // sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        load_preferences();
        //SharedPreferences========================================================

        wv1 = (WebView) findViewById(R.id.webview_main);
        wv1.setWebChromeClient(new WebChromeClient());
        wv1.getSettings().setJavaScriptEnabled(true);
        //wv1.clearCache(true);

        wv1.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                do_when_page_loaded();
                //super.onPageFinished(view, url);
            }
        });

    }


    public void load_preferences(){
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        email_phone_key = sharedpreferences.getString(key_1,"default");
        password_phone_key = sharedpreferences.getString(key_2,"default");

        String timer_main_delay_string = sharedpreferences.getString(key_3,"5");
        String timer_sub_delay_string = sharedpreferences.getString(key_4,"10");

        timer_main_delay = Integer.parseInt(timer_main_delay_string);
        timer_sub_delay = Integer.parseInt(timer_sub_delay_string);

        String update_text = "keys loaded to vars: " +
                System.getProperty ("line.separator") + "email_phone_key = " + email_phone_key +
                System.getProperty ("line.separator") + "password_phone_key = " + password_phone_key +
                System.getProperty ("line.separator") + "timer_main_delay = " + timer_main_delay_string + " seconds" +
                System.getProperty ("line.separator") + "timer_sub_delay = " + timer_sub_delay_string + " seconds ";
        String update_text_merged = update_text + System.getProperty ("line.separator") + my_text_log.getText().toString();
        my_text_log.setText(update_text_merged);
    }

    public void do_when_page_loaded(){
        String web_url = wv1.getUrl();
        //Toast.makeText(this, web_url+"\n IS LOADED NOW!!!!", Toast.LENGTH_SHORT).show();

        //STEP 1 ===================================================================================
        if(web_url.contains("accounts.craigslist.org/login?lang=en&cc=us") && auto_post){
            String update_text = "STEP 1: Log in = " + email + "/" + password;
            String update_text_merged = update_text + System.getProperty ("line.separator") + my_text_log.getText().toString();
            my_text_log.setText(update_text_merged);

            send_line_to_log = update_text;
            new post_to_log().execute();

            wv1.loadUrl(
                    "javascript:(function() { " +
                            "document.getElementById('inputEmailHandle').value = '"+email+"';" +
                            "document.getElementById('inputPassword').value = '"+password+"';" +
                            "})()");
            new CountDownTimer(timer_sub_delay*1000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    btn_timer_sub.setText(String.valueOf(millisUntilFinished/1000));
                    btn_timer_sub.setBackgroundColor(Color.CYAN);
                }

                @Override
                public void onFinish() {
                    btn_timer_sub.setBackgroundResource(android.R.drawable.btn_default);
                    wv1.loadUrl(
                            "javascript:(function() { " +
                                    "var all_buttons = document.getElementsByTagName(\"button\");" +
                                    "for (i = 0; i < all_buttons.length; i++){if(all_buttons[i].innerText.includes(\"Log\")) all_buttons[i].click();}" +
                                    "})()");
                }
            }.start();
        }
        //==========================================================================================



        //STEP 2====================================================================================
        if(web_url.contains("accounts.craigslist.org/login/home") && !web_url.contains("=drafts") && auto_post){
            String update_text = "STEP 2: Choose Location = " + location;
            String update_text_merged = update_text + System.getProperty ("line.separator") + my_text_log.getText().toString();
            //there would not be ANY delay here if it crashes or is sporadic might be here.
            my_text_log.setText(update_text_merged);

            send_line_to_log = update_text;
            new post_to_log().execute();

            wv1.loadUrl(
                    "javascript:(function() { " +
                            "document.getElementsByName('areaabb')[0].value = '"+location+"'" +
                            "})()");
            new CountDownTimer(timer_sub_delay*1000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    btn_timer_sub.setText(String.valueOf(millisUntilFinished/1000));
                    btn_timer_sub.setBackgroundColor(Color.CYAN);
                }

                @Override
                public void onFinish() {
                    btn_timer_sub.setBackgroundResource(android.R.drawable.btn_default);
                    wv1.loadUrl(
                            "javascript:(function() { " +
                                    "var all_buttons = document.getElementsByTagName(\"button\");" +
                                    "for (i = 0; i < all_buttons.length; i++){if(all_buttons[i].innerText.includes(\"go\")) all_buttons[i].click();}" +
                                    "})()");
                }
            }.start();
        }
        //==========================================================================================



        //STEP 3====================================================================================
        if(web_url.contains("post.craigslist.org") && web_url.contains("=type") && auto_post){
            String update_text = "STEP 3: Choose Ad Type (Cat) = " + category;
            String update_text_merged = update_text + System.getProperty ("line.separator") + my_text_log.getText().toString();
            my_text_log.setText(update_text_merged);

            send_line_to_log = update_text;
            new post_to_log().execute();
            

            /*wv1.loadUrl(
                    "javascript:(function() { " +
                            "var input_selector = document.querySelectorAll('input[value=\""+category+"\"]');" +
                            "input_selector[0].checked = true;" +
                            "input_selector[0].click();" +
                            "})()");*/
            wv1.loadUrl(
                    "javascript:(function() { " +
                            "var by_class_right = document.getElementsByClassName(\"right-side\");" +
                            "for (i = 0; i < by_class_right.length; i++){if(by_class_right[i].innerText.includes(\""+category+"\")) by_class_right[i].click();}" +
                            "})()");

            new CountDownTimer(timer_sub_delay*1000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    btn_timer_sub.setText(String.valueOf(millisUntilFinished/1000));
                    btn_timer_sub.setBackgroundColor(Color.CYAN);
                }

                @Override
                public void onFinish() {
                    btn_timer_sub.setBackgroundResource(android.R.drawable.btn_default);
                    wv1.loadUrl(
                            "javascript:(function() { " +
                                    "var all_buttons = document.getElementsByTagName(\"button\");" +
                                    "for (i = 0; i < all_buttons.length; i++){if(all_buttons[i].innerText.includes(\"continue\")) all_buttons[i].click();}" +
                                    "})()");
                }
            }.start();
        }
        //==========================================================================================



        //STEP 4====================================================================================
        if(web_url.contains("post.craigslist.org") && (web_url.contains("=cat") || web_url.contains("=ptype")) && auto_post){
            String update_text = "STEP 4: Choose Ad Category (Sub Cat) = " + sub_category;
            String update_text_merged = update_text + System.getProperty ("line.separator") + my_text_log.getText().toString();
            my_text_log.setText(update_text_merged);

            send_line_to_log = update_text;
            new post_to_log().execute();

            wv1.loadUrl(
                    "javascript:(function() { " +
                            "var by_class_right = document.getElementsByClassName(\"right-side\");" +
                            "for (i = 0; i < by_class_right.length; i++){if(by_class_right[i].innerText.includes(\""+sub_category+"\")) by_class_right[i].click();}" +
                            "})()");

            new CountDownTimer(timer_sub_delay*1000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    btn_timer_sub.setText(String.valueOf(millisUntilFinished/1000));
                    btn_timer_sub.setBackgroundColor(Color.CYAN);
                }

                @Override
                public void onFinish() {
                    btn_timer_sub.setBackgroundResource(android.R.drawable.btn_default);
                    wv1.loadUrl(
                            "javascript:(function() { " +
                                    "var all_buttons = document.getElementsByTagName(\"button\");" +
                                    "for (i = 0; i < all_buttons.length; i++){if(all_buttons[i].innerText.includes(\"continue\")) all_buttons[i].click();}" +
                                    "})()");
                }
            }.start();
        }


        if(web_url.contains("post.craigslist.org") && web_url.contains("=ltr") && auto_post){
            String update_text = "STEP 4b: Choose Personals Type (m4w) = " + m4w;
            String update_text_merged = update_text + System.getProperty ("line.separator") + my_text_log.getText().toString();
            my_text_log.setText(update_text_merged);

            send_line_to_log = update_text;
            new post_to_log().execute();

            wv1.loadUrl(
                    "javascript:(function() { " +
                            "var by_class_right = document.getElementsByClassName(\"right-side\");" +
                            "for (i = 0; i < by_class_right.length; i++){if(by_class_right[i].innerText.includes(\""+m4w+"\")) by_class_right[i].click();}" +
                            "})()");

            new CountDownTimer(timer_sub_delay*1000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    btn_timer_sub.setText(String.valueOf(millisUntilFinished/1000));
                    btn_timer_sub.setBackgroundColor(Color.CYAN);
                }

                @Override
                public void onFinish() {
                    btn_timer_sub.setBackgroundResource(android.R.drawable.btn_default);
                    wv1.loadUrl(
                            "javascript:(function() { " +
                                    "var all_buttons = document.getElementsByTagName(\"button\");" +
                                    "for (i = 0; i < all_buttons.length; i++){if(all_buttons[i].innerText.includes(\"continue\")) all_buttons[i].click();}" +
                                    "})()");
                }
            }.start();
        }


        //==========================================================================================


        //STEP 5====================================================================================
        if(web_url.contains("post.craigslist.org") && web_url.contains("=edit") && !web_url.contains("=editimage") && auto_post){
            String update_text = "STEP 5: Fill Boxes";
            String update_text_merged = update_text + System.getProperty ("line.separator") + my_text_log.getText().toString();
            my_text_log.setText(update_text_merged);

            send_line_to_log = update_text;
            new post_to_log().execute();

            String box2 = "";
            String selection2 = "";

            if (category.contains("job")){
                box2="remuneration";
                selection2="employment_type";
                wv1.loadUrl(
                        "javascript:(function() { " +
                                "var body_string = '" + body + "';" +
                                "body_string = body_string.replace('\\n','<br>');" +
                                "document.getElementById('PostingTitle').value = '"+title+"';" +
                                "document.getElementById('GeographicArea').value = '"+geographic_area+"';" +
                                "document.getElementById('postal_code').value = '"+postal_code+"';" +
                                "document.getElementById('PostingBody').value = body_string;" +
                                "document.getElementById('"+box2+"').value = '"+remuneration+"';" +
                                "document.getElementById('"+selection2+"').value = '"+employment_type+"';" +
                                "input_selector[0].checked = true;" +
                                "input_selector[0].click();" +
                                "})()");
            }


            if (category.contains("sale")){
                box2="Ask";
                if (category.contains("sale") && sub_category.contains("phone")) selection2="mobile_os";
                wv1.loadUrl(
                        "javascript:(function() { " +
                                "var body_string = '" + body + "';" +
                                "body_string = body_string.replace('\\n','<br>');" +
                                "document.getElementById('PostingTitle').value = '"+title+"';" +
                                "document.getElementById('GeographicArea').value = '"+geographic_area+"';" +
                                "document.getElementById('postal_code').value = '"+postal_code+"';" +
                                "document.getElementById('PostingBody').value = body_string;" +
                                "document.getElementById('"+box2+"').value = '"+remuneration+"';" +
                                "document.getElementById('"+selection2+"').value = '"+employment_type+"';" +
                                "input_selector[0].checked = true;" +
                                "input_selector[0].click();" +
                                "})()");
            }

            if (category.contains("personal")){
                box2="pers_age";
                wv1.loadUrl(
                        "javascript:(function() { " +
                                "var body_string = '" + body + "';" +
                                "body_string = body_string.replace('\\n','<br>');" +
                                "document.getElementById('PostingTitle').value = '"+title+"';" +
                                "document.getElementById('GeographicArea').value = '"+geographic_area+"';" +
                                "document.getElementById('postal_code').value = '"+postal_code+"';" +
                                "document.getElementById('PostingBody').value = body_string;" +
                                "document.getElementById('"+box2+"').value = '"+age+"';" +
                                "})()");
            }

            if (category.contains("service")){
                box2="";
                wv1.loadUrl(
                        "javascript:(function() { " +
                                "var body_string = '" + body + "';" +
                                "body_string = body_string.replace('\\n','<br>');" +
                                "document.getElementById('PostingTitle').value = '"+title+"';" +
                                "document.getElementById('GeographicArea').value = '"+geographic_area+"';" +
                                "document.getElementById('postal_code').value = '"+postal_code+"';" +
                                "document.getElementById('PostingBody').value = body_string;" +
                                "})()");
            }


            new CountDownTimer(timer_sub_delay*1000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    btn_timer_sub.setText(String.valueOf(millisUntilFinished/1000));
                    btn_timer_sub.setBackgroundColor(Color.CYAN);
                }

                @Override
                public void onFinish() {
                    btn_timer_sub.setBackgroundResource(android.R.drawable.btn_default);
                    wv1.loadUrl(
                            "javascript:(function() { " +
                                    "var all_buttons = document.getElementsByTagName(\"button\");" +
                                    "for (i = 0; i < all_buttons.length; i++){if(all_buttons[i].innerText.includes(\"continue\")) all_buttons[i].click();}" +
                                    "})()");
                }
            }.start();
        }
        //==========================================================================================



        //STEP 6====================================================================================
        if(web_url.contains("post.craigslist.org") && web_url.contains("=editimage") && auto_post){
            String update_text = "STEP 6: Skip Images";
            String update_text_merged = update_text + System.getProperty ("line.separator") + my_text_log.getText().toString();
            my_text_log.setText(update_text_merged);

            send_line_to_log = update_text;
            new post_to_log().execute();

            new CountDownTimer(timer_sub_delay*1000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    btn_timer_sub.setText(String.valueOf(millisUntilFinished/1000));
                    btn_timer_sub.setBackgroundColor(Color.CYAN);
                }

                @Override
                public void onFinish() {
                    btn_timer_sub.setBackgroundResource(android.R.drawable.btn_default);
                    wv1.loadUrl(
                            "javascript:(function() { " +
                                    "var all_buttons = document.getElementsByTagName(\"button\");" +
                                    "for (i = 0; i < all_buttons.length; i++){if(all_buttons[i].innerText.includes(\"done with images\")) all_buttons[i].click();}" +
                                    "})()");
                }
            }.start();
        }
        //==========================================================================================



        //STEP 7====================================================================================
        if(web_url.contains("post.craigslist.org") && web_url.contains("=preview") && auto_post){
            String update_text = "STEP 7: Ad Posted - Update SQL";
            String update_text_merged = update_text + System.getProperty ("line.separator") + my_text_log.getText().toString();
            my_text_log.setText(update_text_merged);

            send_line_to_log = update_text;
            new post_to_log().execute();

            new CountDownTimer(timer_sub_delay*1000, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    btn_timer_sub.setText(String.valueOf(millisUntilFinished/1000));
                    btn_timer_sub.setBackgroundColor(Color.CYAN);
                }

                @Override
                public void onFinish() {
                    btn_timer_sub.setBackgroundResource(android.R.drawable.btn_default);
                    new update_ad_posted().execute();
                    //turn off the timer after ad posts below
                    auto_post = false;
                    btn_stop_main_timer.setBackgroundColor(Color.MAGENTA);
                    btn_start_main_timer.setEnabled(true);
                    btn_start_main_timer.setBackgroundResource(android.R.drawable.btn_default);
                }
            }.start();
        }
        //==========================================================================================





    }

    public class post_to_log extends AsyncTask<Void,Void,Void> {

        String words;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Document doc = Jsoup.connect("http://www.dreamgoals.info/cl_post/send_to_log.php")
                        .data("email", email_phone_key)
                        .data("password", password_phone_key)
                        .data("log_data", send_line_to_log)
                        .post();

                words = doc.text();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //my_text_log.setText(words);
        }

    }

    public class update_ad_posted extends AsyncTask<Void,Void,Void>{

        String data_from_php;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Document doc = Jsoup.connect("http://www.dreamgoals.info/cl_post/update_ad_posted.php")
                        .data("email", email_phone_key)
                        .data("id", next_ad_post_id)
                        .post();

                data_from_php = doc.text();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String update_text = data_from_php;
            String update_text_merged = update_text + System.getProperty ("line.separator") + my_text_log.getText().toString();
            my_text_log.setText(update_text_merged);

            send_line_to_log = update_text;
            new post_to_log().execute();

        }

    }

    public class read_nextad_post extends AsyncTask<Void,Void,Void>{

        String data_from_php;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Document doc = Jsoup.connect("http://www.dreamgoals.info/cl_post/read_nextad_no_timer.php?email="+email_phone_key).get();
                data_from_php = doc.text();

            } catch (IOException e) {
                e.printStackTrace();  //this is where the error might be if no internet? or time out to long ?
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            String wakelock_status = " wl OFF";
            if(wl.isHeld()) wakelock_status = " wl ON";

            String update_text = data_from_php;
            String update_text_merged = update_text + wakelock_status + System.getProperty ("line.separator") + my_text_log.getText().toString();
            my_text_log.setText(update_text_merged);

            send_line_to_log = update_text;
            new post_to_log().execute();

            if (data_from_php.startsWith("Post Ad")){
                post_started_time = System.currentTimeMillis();
                next_ad_post_id = data_from_php.substring(8); //strip off "Post Ad:" to get id by itself
                //Toast.makeText(getApplicationContext(), "Hello:"+next_ad_post_id, Toast.LENGTH_LONG).show();
                my_row_id.setText(next_ad_post_id); //update box in corner with id to post
                new read_row_json().execute();  //starts posting process
            }

            if (data_from_php.startsWith("Next Ad: ")){
                next_ad_post_id = data_from_php.substring(data_from_php.indexOf("(")+1,data_from_php.indexOf(")"));
                my_row_id.setText(next_ad_post_id); //update box in corner with id to post
            }

        }

    }

    public class read_row_json extends AsyncTask<Void,Void,Void> {

        String words;
        String sql_row_id = my_row_id.getText().toString();

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Document doc = Jsoup.connect("http://www.dreamgoals.info/cl_post/read_row_json.php?id="+sql_row_id+"&email="+email_phone_key).get();
                words = doc.text();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            my_text_log.setText(words);

            try {
                JSONObject mainObject = new JSONObject(words);

                email = mainObject.getString("email");
                password = mainObject.getString("password");
                location = mainObject.getString("loc_spec");

                geographic_area = mainObject.getString("loc_area");
                postal_code = mainObject.getString("zip_code");
                employment_type = mainObject.getString("job_type");
                remuneration = mainObject.getString("pay");

                category = mainObject.getString("cat");
                sub_category = mainObject.getString("cat_sub");
                m4w = mainObject.getString("m4w");
                age = mainObject.getString("age");

                title = mainObject.getString("title");
                title = title.replaceAll("'", "\\\\'");
                //body = body.replaceAll("\\\\", "\\\\\\\\");

                body = mainObject.getString("body");
                body = body.replaceAll("(\r\n|\n\r|\r|\n)", "<br>");
                body = body.replaceAll("'", "\\\\'");
                //body = body.replaceAll("'", "\\\\'");
                //body = body.replaceAll("(\r\n|\n\r|\r|\n)", System.getProperty ("line.separator")+"\n");


                //body = body.replaceAll("(\r\n|\n\r|\r|\n)", "XxXxYyYy");
                //body = body.replaceAll("XxXx","\\\\");
                //body = body.replaceAll("YyYy", "n");

                String update_text =
                        "--------------- Data Loaded ---------------" + System.getProperty ("line.separator") +
                                "email: " + email + System.getProperty ("line.separator") +
                                "password: " + password + System.getProperty ("line.separator") +
                                "location: " + location + System.getProperty ("line.separator") +
                                "geographic_area: " + geographic_area + System.getProperty ("line.separator") +
                                "postal_code: " + postal_code + System.getProperty ("line.separator") +
                                "employment_type: " + employment_type + System.getProperty ("line.separator") +
                                "remuneration: " + remuneration + System.getProperty ("line.separator") +
                                "age: " + age + System.getProperty ("line.separator") +
                                "category: " + category + System.getProperty ("line.separator") +
                                "sub_category: " + sub_category + System.getProperty ("line.separator") +
                                "title: " + title + System.getProperty ("line.separator") +
                                "body: " + System.getProperty ("line.separator") + body + System.getProperty ("line.separator") +
                                "--------------------------------------------" + System.getProperty ("line.separator") +
                                my_text_log.getText().toString();

                my_text_log.setText(update_text);

                send_line_to_log = System.getProperty ("line.separator") + update_text
                        + System.getProperty ("line.separator") + "================================================"
                        + System.getProperty ("line.separator") + "================================================";
                new post_to_log().execute();

                wv1.loadUrl("https://accounts.craigslist.org/login?lang=en&cc=us");

                //my_title_box.setText(title);
                //my_body_box.setText(body);
                //my_start_button.setText("Done!");

            } catch (JSONException e) {e.printStackTrace();}

        }
    }


    int main_counter = 0;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
      /* do what you need to do */
            if (main_counter >= 10) {main_counter = 0;} else main_counter = main_counter + 1;
            btn_read_nextad_timer_main.setText(String.valueOf(main_counter));

            //pause checking next ad for 10 mins if ad posting started
            if (System.currentTimeMillis() > (post_started_time+180*1000)) {
                new read_nextad_post().execute();
            } else btn_click_counter.setText(String.valueOf(((post_started_time+180*1000)-System.currentTimeMillis())/1000));
      /* and here comes the "trick" */
            if(auto_post){
                handler.postDelayed(this, timer_main_delay*1000);
            } else{
                btn_start_main_timer.setEnabled(true);
                btn_start_main_timer.setBackgroundResource(android.R.drawable.btn_default);
                btn_click_counter.setText("AD");
                btn_read_nextad_timer_main.setText("CL");
                btn_timer_sub.setText("GM");
                wl.release();
            }
        }
    };




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
//icons
        if (id == R.id.icon_1) {
            Toast.makeText(this, "icon_1", Toast.LENGTH_SHORT).show();
            Intent start_act_home = new Intent(this,MainActivity.class);
            startActivity(start_act_home);
            //start_act_2.putExtra(EXTRA_MESSAGE.message);
        }
        if (id == R.id.icon_2) {
            Intent start_act_2 = new Intent(this,Main2Activity.class);
            startActivity(start_act_2);
            //start_act_2.putExtra(EXTRA_MESSAGE.message);
            Toast.makeText(this, "icon_2", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.icon_3) {
            Intent start_act_3 = new Intent(this,Main3Activity.class);
            startActivity(start_act_3);
            Toast.makeText(this, "icon_3", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.icon_4) {
            Toast.makeText(this, "icon_4", Toast.LENGTH_SHORT).show();
        }
        if (id == R.id.icon_5) {
            Toast.makeText(this, "icon_5", Toast.LENGTH_SHORT).show();
        }

        //menu
        if (id == R.id.menu_1) {
            Toast.makeText(this, "menu_1", Toast.LENGTH_SHORT).show();
            //starts the settings activity
            Intent my_intent=new Intent(this,SettingsActivity.class);
            startActivity(my_intent);
            //
        }
        if (id == R.id.menu_2) {
            //paste code heree
            //b_timer_main_stop
            Toast.makeText(this, "OFF has been clicked"+"--b_timer_main_stop", Toast.LENGTH_SHORT).show();
            //
        }
        if (id == R.id.menu_3) {
            Toast.makeText(this, "menu_3", Toast.LENGTH_SHORT).show();
            //
        }
        if (id == R.id.menu_4) {
            Toast.makeText(this, "menu_4", Toast.LENGTH_SHORT).show();
            //
        }
        if (id == R.id.menu_5) {
            Toast.makeText(this, "menu_5", Toast.LENGTH_SHORT).show();
            //
        }
        return super.onOptionsItemSelected(item);
}

    //button clicks------------------------------------------------------------------
    public void buttonOnClick(View view) {
        int the_id = view.getId();

        if (the_id == R.id.b_timer_main_start) {
            //if(auto_post == false) {
                auto_post = true;
                new read_nextad_post().execute();
                //handler.postDelayed(runnable, 100);
                //btn_start_main_timer.setText("ON");
                btn_stop_main_timer.setBackgroundResource(android.R.drawable.btn_default);
                btn_start_main_timer.setBackgroundColor(Color.GREEN);
                btn_start_main_timer.setEnabled(false);
                //wl.acquire();
            //}
        }
        if (the_id == R.id.b_timer_main_stop) {
            auto_post = false;
            btn_stop_main_timer.setBackgroundColor(Color.MAGENTA);

            btn_start_main_timer.setEnabled(true);
            btn_start_main_timer.setBackgroundResource(android.R.drawable.btn_default);
        }
        if (the_id == b_click_count) {
            wv1.loadUrl("http://www.dreamgoals.info/cl_post/select_ads.php?email="+email_phone_key);
            //Toast.makeText(this, "act_3_b_1---"+String.valueOf(count_j), Toast.LENGTH_SHORT).show();
        }
        if (the_id == R.id.b_read_nextad_timer_main) {
            wv1.loadUrl("https://accounts.craigslist.org/login/home?show_tab=drafts");
            //new read_nextad_post().execute();

        /*  sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            String highScore = sharedpreferences.getString(key_1,"default");
            String some_name = sharedpreferences.getString(key_2,"default");
            Toast.makeText(this, "but_2_works -- key_1+key_2"+"\n"+
                            highScore+"\n"+
                            some_name+"\n"
                    , Toast.LENGTH_SHORT).show();

            Button b1_x=(Button)this.findViewById(R.id.b_timer_main_start_stop);
            b1_x.setText(highScore);
            b1_x.setTextColor(Color.parseColor("#0404B4"));*/
        }
        if (the_id == R.id.b_gmail) {
            wv1.loadUrl("https://www.google.com/gmail");
            //wl.acquire();
            //*use this for the settings is using PREFERECE MANAGER

            //sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
            //*find the example_text in the pref_general.xml file
            //String hhh = sharedpreferences.getString("example_text","xxx");
            //Toast.makeText(this, "but_3_works\n"+hhh, Toast.LENGTH_SHORT).show();
        }

    }


}
//notes

/*

https://accounts.craigslist.org/login

https://accounts.craigslist.org/login/home?lang=en&cc=us

https://post.craigslist.org/k/EkHNznEu5xG5aGaMbNMO4w/LPAj7?lang=en&cc=us&s=type

https://post.craigslist.org/k/EkHNznEu5xG5aGaMbNMO4w/LPAj7?lang=en&cc=us&s=cat

https://post.craigslist.org/k/EkHNznEu5xG5aGaMbNMO4w/LPAj7?lang=en&cc=us&s=edit

https://post.craigslist.org/k/EkHNznEu5xG5aGaMbNMO4w/LPAj7?lang=en&cc=us&s=editimage

https://post.craigslist.org/k/EkHNznEu5xG5aGaMbNMO4w/LPAj7?lang=en&cc=us&s=preview

<button class="button" type="submit" tabindex="1" name="go" value="Continue">publish</button>
<button type="submit" tabindex="1" name="go" value="Edit Post">edit post</button>
<button type="submit" tabindex="1" name="go" value="Edit Images">edit images</button>
<button class="bigbutton" type="submit" tabindex="1" name="go" value="Continue">publish</button>

https://post.craigslist.org/k/EkHNznEu5xG5aGaMbNMO4w/LPAj7?lang=en&cc=us&s=redirect

android:configChanges="keyboard|keyboardHidden|orientation"
android:screenOrientation="portrait"
android:launchMode="singleTask"

*/
