package com.ogangi.barcode.writer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CreateNewBarcodeFragment.CreateBarcodeListener {
    private RecyclerView list;
    private ListAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new CreateNewBarcodeFragment();
                newFragment.show(getSupportFragmentManager(), "createBarcode");
            }
        });

        //SetUp RecyclerView
        list = (RecyclerView)findViewById(R.id.list);
        layoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(layoutManager);

        adapter = new ListAdapter(getStoredBarcodes());
        list.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBarcodeCreate(Barcode barcode) {
        adapter.addBarcode(barcode);
    }

    @Override
    public void onBarcodeCancel() {

    }

    @NonNull
    private List<Barcode> getStoredBarcodes() {
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("BarcodeDB", Context.MODE_PRIVATE);
        Map<String, String> tmp = (Map<String, String>) sharedPreferences.getAll();
        List<String> storedJsons = new ArrayList<>(tmp.values());
        List<Barcode> barcodes = new ArrayList<>();
        for (String json: storedJsons) {
            try {
                JSONObject jsonBarcode = new JSONObject(json);
                Barcode barcode = new Barcode(jsonBarcode);
                barcodes.add(barcode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return  barcodes;
    }

}
