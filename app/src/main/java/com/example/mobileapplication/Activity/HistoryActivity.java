package com.example.mobileapplication.Activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mobileapplication.Controller.Adapter;
import com.example.mobileapplication.Controller.HistoryAdapter;
import com.example.mobileapplication.Controller.SwipeController;
import com.example.mobileapplication.Model.Company;
import com.example.mobileapplication.R;
import com.example.mobileapplication.Model.Storage;
import com.example.mobileapplication.Controller.SwipeControllerAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.mobileapplication.Activity.MainActivity.QUERY;

// history activity with scroll view and swipe controller for swipe to delete
public class HistoryActivity extends AppCompatActivity {

    RecyclerView rv;
    LinearLayoutManager layoutManager;
    private List<String> cachedData = new ArrayList();
    HistoryAdapter adapter;
    SwipeController swipeController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().setTitle("History");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        rv = findViewById(R.id.history_list);
        layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);

        // swipe controller for swipe to delete
        swipeController = new SwipeController(new SwipeControllerAction() {
            @Override
            public void onRightClicked(int position) {

                String item = adapter.getItem(position);
                adapter.remove(item);

                adapter.notifyItemRangeChanged(position, adapter.getItemCount());

                // remove item from cache
                try {
                    List<String> data = adapter.getmDataset();
                    Collections.reverse(data);

                    Storage.writeObject(getApplicationContext(), MainActivity.KEY, data);
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(rv);

        rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });

        // link back to MainActivity class when it's pressed
        try {
            cachedData = (List<String>) Storage.readObject(this, MainActivity.KEY);
            adapter = new HistoryAdapter((cachedData));
            adapter.setItemClickListener(new HistoryAdapter.ItemClickListener(){
                @Override
                public void onClickItem(View view, int pos) {
                    String query = adapter.getItem(pos);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra(QUERY, query);
                    startActivity(intent);
                }
            });
        }catch (Exception e){
            List<String> list = new ArrayList();
            list.add("no data");
            adapter = new HistoryAdapter(list);
        }
        rv.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
