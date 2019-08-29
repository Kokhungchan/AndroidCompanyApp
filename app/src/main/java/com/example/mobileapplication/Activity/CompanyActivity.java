package com.example.mobileapplication.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobileapplication.BuildConfig;
import com.example.mobileapplication.Model.Company;
import com.example.mobileapplication.Model.Data;
import com.example.mobileapplication.Model.GraphNode;
import com.example.mobileapplication.Model.NodeChild;
import com.example.mobileapplication.Model.Officer;
import com.example.mobileapplication.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CompanyActivity extends AppCompatActivity {

    private Company company;
    TextView profile;
    private ProgressBar progressBar;
    private String URL = "https://api.companieshouse.gov.uk/";
    private String apiKey = "KYemqTwL_khz_PVxF7X97drmCfqy5LXi2UtlmCOf";
    private List<Data> companies = new ArrayList<>();
    private List<String> fetchedIds = new ArrayList();
    private GraphNode graphNode;
    private final int item = 8;
    Dialog popup;
    boolean gestureBlocked = false;
    private float mScale = 1f;
    private ScaleGestureDetector mScaleDetector;
    GestureDetector gestureDetector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_graph);
        Intent intent = getIntent();
        company = (Company) intent.getSerializableExtra(MainActivity.COMPANY);
        getSupportActionBar().setTitle(company.getName());
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progressBar);
        profile = findViewById(R.id.textView);
        popup = new Dialog(this);
        graphNode = new GraphNode(this);

        //creating horizontal and vertical scroll view
        final HorizontalScrollView hsv = new HorizontalScrollView(this);
        final ScrollView sv = new ScrollView(this);
        sv.addView(hsv);

        //automatically scroll to the middle
        hsv.postDelayed(new Runnable() {
            @Override
            public void run() {
                hsv.smoothScrollBy(hsv.getWidth()/2, 0);
            }
        },100);
        sv.postDelayed(new Runnable() {
            @Override
            public void run() {
                sv.smoothScrollBy(0, sv.getHeight()/2);
            }
        },100);

        graphNode.setBackgroundColor(Color.WHITE);
        setContentView(graphNode);

        setContentView(sv);
        hsv.scrollTo(hsv.getWidth()/2, hsv.getHeight()/2);
        hsv.addView(graphNode);

        //zoom in and zoom out
        gestureDetector = new GestureDetector(this, new GestureListener());

        mScaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener()
        {
            @Override
            public boolean onScale(ScaleGestureDetector detector)
            {
                gestureBlocked = false;
                float scale = 1 - detector.getScaleFactor();

                float prevScale = mScale;
                mScale += scale;

                if (mScale < 0.5f) // Minimum scale condition:
                    mScale = 0.5f;

                if (mScale > 1f) // Maximum scale condition:
                    mScale = 1f;


                ScaleAnimation scaleAnimation = new ScaleAnimation(1f / prevScale, 1f / mScale, 1f / prevScale, 1f / mScale, detector.getFocusX(), detector.getFocusY());
                scaleAnimation.setDuration(0);
                scaleAnimation.setFillAfter(true);
                ScrollView layout = sv;
                layout.startAnimation(scaleAnimation);
                return true;
            }
        });

        //set when the node is clicked
        graphNode.setNodeClickListener(new GraphNode.NodeClickListener() {
            @Override
            public void onClickNode(Data data) {

                if(data instanceof Company) {
                    showPopup(data);
                    return;
                }

                final Officer officer = (Officer) data;

                if(fetchedIds.contains(officer.getOfficerNumber())) {
                    showPopup(data);
                    return;
                }

                //using volley

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                String url = URL + "officers/"+ officer.getOfficerNumber() +"/appointments?items_per_page=" + item;

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                        null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            fetchedIds.add(officer.getOfficerNumber());

                            companies = new ArrayList();
                            JSONArray data = response.getJSONArray("items");
                            for(int i=0; i<data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                Company c = new Company(obj);

                                if(c.equals(company)) {
                                    continue;
                                }

                                companies.add(c);
                            }

                            graphNode.addChildren(new NodeChild(officer, companies));


                        } catch(JSONException e) {
                            Log.d("JSon Error",e.toString());
                            Toast toast = Toast.makeText(getApplicationContext(),"Json error...",Toast.LENGTH_SHORT);
                            toast.show();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Toast toast = Toast.makeText(getApplicationContext(),"Volley error...",Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }) {
                    @Override
                    public Map getHeaders() throws AuthFailureError {
                        HashMap headers = new HashMap();
                        headers.put("Content-Type", "application/json");
                        headers.put("Authorization", apiKey);
                        return headers;
                    }
                };

                queue.add(jsonObjectRequest);

            }
        });
        loadCompanyInfo();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }

            case R.id.sharebutton: {
                Bitmap bitmap = graphNode.getBitmap();
                shareImage(bitmap);
                return true;

            }

        }

        return super.onOptionsItemSelected(item);
    }

    //volley to get company info
    private void loadCompanyInfo() {

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = URL + "company/" +company.getCompanyNumber() + "/officers";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("items");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        Officer officer = new Officer(object);
                        companies.add(officer);
                    }

                    progressBar.setVisibility(View.GONE);
                    graphNode.setCenter(company);
                    graphNode.addChildren(new NodeChild(company,companies));
                    companies = new ArrayList();




                } catch (JSONException e) {
                    Log.d("JSon ERROR", e.toString());
                    Toast toast = Toast.makeText(getApplicationContext(), "Volley Error...", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast toast = Toast.makeText(getApplicationContext(), "Volley Error...", Toast.LENGTH_SHORT);
                toast.show();
            }
        }) {
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap header = new HashMap();
                header.put("Content-Type", "application/json");
                header.put("Authorization", apiKey);
                return header;
            }
        };
        queue.add(jsonObjectRequest);
    }

    //pop up for company/officer details
    public void showPopup(Data data) {
        TextView txtClose;
        TextView companyTitle;
        final TextView contentView;
        final TextView companyAdd;

        popup.setContentView(R.layout.custompopup);
        txtClose = (TextView) popup.findViewById(R.id.txtclose);
        companyTitle = (TextView)popup.findViewById(R.id.Company_title);
        companyAdd = (TextView)popup.findViewById(R.id.Company_address);
        contentView = (TextView)popup.findViewById(R.id.Contents);

        txtClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.dismiss();
            }
        });

        popup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popup.show();

        if (data instanceof Company) {
            final Company company = (Company) data;
            companyTitle.setText(data.getName());


            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            String url = URL + "company/"+ company.getCompanyNumber();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                    null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                    String companyStatus = response.getString("company_status");
                    String companyDOC = response.getString("date_of_creation");
                    String eTag = response.getString("etag");
                    String jurisdiction = response.getString("jurisdiction");

                    String content = "Company Number: "+ company.getCompanyNumber() + "\n\nCompany Status: " +companyStatus + "\n\nDate of Creation: " +companyDOC
                            + "\n\nE-Tag: " +eTag +"\n\nJurisdiction: " +jurisdiction;

                    companyAdd.setText(company.getCompanyAdd());

                    contentView.setText(content);

                    } catch(JSONException e) {
                        Log.d("JSon Error",e.toString());
                        Toast toast = Toast.makeText(getApplicationContext(),"Json error...",Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error
                    Toast toast = Toast.makeText(getApplicationContext(),"Volley error...",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }) {
                @Override
                public Map getHeaders() throws AuthFailureError {
                    HashMap headers = new HashMap();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", apiKey);
                    return headers;
                }
            };
            queue.add(jsonObjectRequest);
        }
        else {
            final Officer officer = (Officer) data;
            companyTitle.setText(data.getName());

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

            String url = URL + "officers/"+ officer.getOfficerNumber() +"/appointments";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                    null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        int dobMonth = response.getJSONObject("date_of_birth").getInt("month");
                        int dobYear = response.getJSONObject("date_of_birth").getInt("year");

                        JSONArray item = response.getJSONArray("items");

                        String content = "Officer Number: " +officer.getOfficerNumber() +"\n\nDate of Birth: " +getDate(dobMonth, dobYear) +"\n\nRole: ";

                        for(int i=0; i<item.length(); i++) {
                            JSONObject obj = item.getJSONObject(i);
                            String role = obj.getString("officer_role");
                            String companyName = obj.getJSONObject("appointed_to").getString("company_name");

                            content += "\n\t< " + role + " - " + companyName +" >";
                        }

                        contentView.setText(content);

                    } catch(JSONException e) {
                        Log.d("JSon Error",e.toString());
                        Toast toast = Toast.makeText(getApplicationContext(),"Json error...",Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error
                    Toast toast = Toast.makeText(getApplicationContext(),"Volley error...",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }) {
                @Override
                public Map getHeaders() throws AuthFailureError {
                    HashMap headers = new HashMap();
                    headers.put("Content-Type", "application/json");
                    headers.put("Authorization", apiKey);
                    return headers;
                }
            };
            queue.add(jsonObjectRequest);
        }

        }

    //for displaying month
    private String getDate(int month, int year) {
        switch (month) {
            case 1:
                return "Jan " + year;
            case 2:
                return "Feb " + year;
            case 3:
                return "Mar " + year;
            case 4:
                return "Apr " + year;
            case 5:
                return "May " + year;
            case 6:
                return "Jun " + year;
            case 7:
                return "Jul " + year;
            case 8:
                return "Aug " + year;
            case 9:
                return "Sep " + year;
            case 10:
                return "Oct " + year;
            case 11:
                return "Nov " + year;
            case 12:
                return "Dec " + year;
            default:
                return "Error";

        }
    }


    // override dispatchTouchEvent()
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        mScaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }

    // add private class GestureListener

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // double tap fired.
            return true;
        }
    }

    // Sharing function
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_node, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void shareImage(Bitmap bitmap){
        // save bitmap to cache directory
        try {
            File cachePath = new File(this.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        File imagePath = new File(this.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.setType("image/png");
            startActivity(Intent.createChooser(shareIntent, "Share Via"));
        }
    }




}
