package com.example.myapplication;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDataServices {

    @GET("?method=flickr.photos.search&api_key="+Util.KEY+"&format=json&nojsoncallback=1&per_page=20")
    Call<RetroPhoto> getAllPhotos(@Query("text") String text, @Query("page") int page);
}
