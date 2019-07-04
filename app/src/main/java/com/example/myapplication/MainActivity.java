package com.example.myapplication;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    static String input = "";
    private static RecyclerView recycler;
    @SuppressLint("StaticFieldLeak")
    private static Adapter adapter;
    ProgressDialog progressDialog;


    EditText searchBox;
    ImageButton search;
    Handler handler = new Handler();
    long delay = 500; //500 milisecond Delay
    long last_text_edit = 0;

    EditText rowCount;
    private RecyclerView.LayoutManager manager;
    private ArrayList<JSONObject> list = new ArrayList<>();

    //Run after delay
    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + delay)) {
                list.clear();
                if (!String.valueOf(rowCount.getText()).trim().equals("")) {
                    ((GridLayoutManager) manager).setSpanCount(Integer.parseInt(String.valueOf(rowCount.getText())));
                }else {
                    ((GridLayoutManager) manager).setSpanCount(2);
                }
                getList(String.valueOf(searchBox.getText()), progressDialog, MainActivity.this, list, Util.page);
                input = String.valueOf(searchBox.getText());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recycler = findViewById(R.id.recyclerView);
        searchBox = findViewById(R.id.searchBox);
        search = findViewById(R.id.search);
        rowCount = findViewById(R.id.rowCount);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading....");

        setImageLoaderGlobalConfig();


        //setting span size according to images and button Button =1 span size and for other it is
        recycler.setHasFixedSize(true);
        manager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        ((GridLayoutManager) manager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getItemViewType(position)==R.layout.single_unit) {
                   return 1;
                } else {
                    if (!String.valueOf(rowCount.getText()).trim().equals("")) {
                        return Integer.parseInt(String.valueOf(rowCount.getText()));
                    }else {
                        return 2;
                    }                }
            }

        });
        recycler.setLayoutManager(manager);

        //check for text change if text not change for .5 sec it get data from flickr api
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                handler.removeCallbacks(input_finish_checker);

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    last_text_edit = System.currentTimeMillis();
                    handler.postDelayed(input_finish_checker, delay);
                }

            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                if (!String.valueOf(rowCount.getText()).trim().equals("")) {
                    ((GridLayoutManager) manager).setSpanCount(Integer.parseInt(String.valueOf(rowCount.getText())));
                }else {
                    ((GridLayoutManager) manager).setSpanCount(2);
                }
                getList(String.valueOf(searchBox.getText()), progressDialog, MainActivity.this, list, Util.page);
                input = String.valueOf(searchBox.getText());

            }
        });


    }
    // used to set config of imageLoader
    private void setImageLoaderGlobalConfig() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().resetViewBeforeLoading(true).cacheInMemory(true)
                .displayer(new FadeInBitmapDisplayer(500, true, true, false))
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config);
    }
//get data from flick api
    public static void getList(String inp, final ProgressDialog progressDialog, final Context context, final ArrayList<JSONObject> list, int page) {
        //show progress dialog
        progressDialog.show();

        //used to save current state
        final Parcelable recyclerViewState;
        recyclerViewState = recycler.getLayoutManager().onSaveInstanceState();

        /*Create handle for the RetrofitInstance interface*/

        GetDataServices service = RetrofitClientInstance.getRetrofitInstance().create(GetDataServices.class);
        Call<RetroPhoto> call = service.getAllPhotos(String.valueOf(inp), page);

        call.enqueue(new Callback<RetroPhoto>() {
            @Override
            public void onResponse(@NonNull Call<RetroPhoto> call, @NonNull Response<RetroPhoto> response) {
                progressDialog.dismiss();
                assert response.body() != null;
                Gson gson = new Gson();
                String json = gson.toJson(response.body().getTitle());

                //populate data in list of jsonObject
                try {
                    JSONObject obj = new JSONObject(json);
                    JSONArray arr = obj.getJSONArray("photo");
                    for (int i = 0; i < arr.length(); i++) {
                        list.add(arr.getJSONObject(i));
                    }

                    //set adapter
                    adapter = new Adapter(list, context);
                    recycler.setAdapter(adapter);
                    recycler.getLayoutManager().onRestoreInstanceState(recyclerViewState);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetroPhoto> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(context, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
