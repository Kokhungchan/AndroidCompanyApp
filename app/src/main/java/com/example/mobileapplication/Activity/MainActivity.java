package com.example.mobileapplication.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mobileapplication.Controller.Adapter;
import com.example.mobileapplication.Controller.SwipeController;
import com.example.mobileapplication.Model.Company;
import com.example.mobileapplication.R;
import com.example.mobileapplication.Controller.ScrollListener;
import com.example.mobileapplication.Model.Storage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    // api key and url
    private String URL = "https://api.companieshouse.gov.uk/search/companies";
    private String apiKey = "KYemqTwL_khz_PVxF7X97drmCfqy5LXi2UtlmCOf";

    RecyclerView rv;
    LinearLayoutManager linearLayoutManager;
    Adapter adapter;
    ProgressBar progressBar;

    private static final int PAGE_START = 0;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = PAGE_START;
    private String query;
    private int totalItems;
    private final int ITEM_PER_PAGE = 10;

    public static final String QUERY = "com.example.mobileapplication.QUERY";
    public static final String COMPANY = "com.example.mobileapplication.COMPANY";
    public static final String KEY = "com.example.mobileapplication.STORAGE";


    private EditText mSearch;
    private ImageButton mSearchBtn;
    private List<String> cachedData = new ArrayList();
    LinearLayoutManager manager;
    int resId = R.anim.layout_animation_fall_down;
    SwipeController swipeController = null;

    @Override
    protected void onRestart() {
        super.onRestart();

        try {
            cachedData = (List<String>) Storage.readObject(getApplicationContext(), KEY);
        } catch(Exception e) {
            cachedData = new ArrayList();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.result_list);
        progressBar = findViewById(R.id.progressBar);
        mSearch = (EditText) findViewById(R.id.editText);

        query = getIntent().getStringExtra(QUERY);
        if(query != null) {
            mSearch.setText(query);
            progressBar.setVisibility(View.VISIBLE);
            loadFirstPage();
        }

        try{
            cachedData = (List<String>)Storage.readObject(getApplicationContext(), KEY);
        } catch(Exception e) {
            cachedData = new ArrayList();
        }

        progressBar.setVisibility(View.INVISIBLE);

        adapter = new Adapter(this, new Adapter.ItemClickListener() {
            @Override
            public void onClickItem(View view, int position) {
                Company company = adapter.getItem(position);
                Intent intent = new Intent(getApplicationContext(), CompanyActivity.class);
                intent.putExtra(COMPANY, company);
                startActivity(intent);
            }

        });

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(adapter);
        //rv.setItemAnimator(new DefaultItemAnimator());


        rv.addOnScrollListener(new ScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                loadNextPage();
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        // set listener for search button
        mSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() != KeyEvent.ACTION_DOWN) {
                    return false;
                }
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    mSearch.clearFocus();
                    query = mSearch.getText().toString();
                    query = query.trim();


                    if (query.equals("")) {
                        Toast toast = Toast.makeText(getApplicationContext(), "Fill in", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        loadFirstPage();
                    }
                    return true;
                }
                return false;
            }
        });


        mSearchBtn = (ImageButton) findViewById(R.id.imageButton3);

        manager = new LinearLayoutManager(this);

    }

    // volley to return query results
    private void loadFirstPage() {
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = URL + "?q=" + query + "&items_per_page" + ITEM_PER_PAGE + "&start_index=" + 0;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("items");
                    List<Company> companies = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        Company company = new Company(object);
                        companies.add(company);
                    }

                    progressBar.setVisibility(View.GONE);
                    runAnimationAgain();
                    adapter.addAll(companies);
                    totalItems = response.getInt("total_results");
                    checkMoreToLoad();
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

    // method to load the second result list
    private void loadNextPage() {
        RequestQueue queue = Volley.newRequestQueue(this);
        int startIndex = currentPage * ITEM_PER_PAGE;
        String url = URL + "?q=" + query + "&items_per_page" + ITEM_PER_PAGE + "&start_index=" + startIndex;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("items");
                    List<Company> companies = new ArrayList();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        Company company = new Company(object);
                        companies.add(company);
                    }
                    adapter.removeLoadingFooter();
                    isLoading = false;
                    adapter.addAll(companies);
                    checkMoreToLoad();
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

    private void checkMoreToLoad() {
        if (totalItems != adapter.getItemCount()) {
            adapter.addLoadingFooter();
        } else {
            isLastPage = true;
        }
    }

    // load query result when search button was pressed
    public void onClick(View view) {
        query = mSearch.getText().toString();
        query = query.trim();
        adapter.removeAll();

        if (query.equals("")) {
            Toast toast2 = Toast.makeText(getApplicationContext(), "Fill in", Toast.LENGTH_SHORT);
            toast2.show();
        } else {
            if (!cachedData.contains(query)) {
                cachedData.add(query);
            }
            try {
                // store the cached for history
                Storage.writeObject(getApplicationContext(), KEY, cachedData);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.VISIBLE);
            // call the method
            loadFirstPage();

            try {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    // go back function
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.mybutton) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    // animation for scrollview
    private void runAnimationAgain() {

        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);

        rv.setLayoutAnimation(controller);
        rv.getAdapter().notifyDataSetChanged();
        rv.scheduleLayoutAnimation();

    }




}








