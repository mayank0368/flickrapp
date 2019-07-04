package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class    Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private ArrayList<JSONObject> list;

    private Context context;

    Adapter(ArrayList<JSONObject> list, Context context) {

        this.list = list;

        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == list.size()) ? R.layout.button : R.layout.single_unit;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == R.layout.single_unit) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_unit, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.button, parent, false);
        }


        return new ViewHolder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("position", String.valueOf(position));

        if (position == list.size()) {

            holder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("Loading....");
                    Util.page += 1;
                    MainActivity.getList(MainActivity.input, progressDialog, context, list,
                            Util.page);
                }
            });
        } else {
            String uri = getUri(list.get(position));
            if (holder.imageView != null) {
                holder.imageLoader.displayImage(uri, holder.imageView);
            }
        }
    }
//Generate image uri form the JSON object of image
    private String getUri(JSONObject object) {
        try {
            String uid = object.getString("id");

//            String name = object.getString("title");
            // http://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg

            String url = "http://farm" + object.getInt("farm") + ".staticflickr.com/" + object
                    .getInt("server") + "/" + uid + "_" + object.getString("secret") + ".jpg";
            return url;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override

    public int getItemCount() {

        return list.size() + 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        Button button;
        ImageLoader imageLoader;
        private ImageView imageView;


        ViewHolder(View itemView) {

            super(itemView);
            imageLoader = ImageLoader.getInstance();
            imageView = itemView.findViewById(R.id.imageView);
            button = itemView.findViewById(R.id.viewMore);
        }
    }

}