package com.example.plantdiseaseimage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantdiseaseimage.adaptor.NewsAdapter;
import com.example.plantdiseaseimage.myutils.APIInterface;
import com.example.plantdiseaseimage.myutils.ApiClient;
import com.example.plantdiseaseimage.myutils.Article;
import com.example.plantdiseaseimage.myutils.ResponseModel;
import com.example.plantdiseaseimage.pojo.News_List;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private List<News_List> news_list;
    private NewsAdapter myAdapter;
    private String Url="https://newsapi.org/v2/everything?sources=the-times-of-india&q=Farmer&apiKey=2012066be1c944409c701878d544b5fc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        mRecyclerView = (RecyclerView) findViewById(R.id.recylerView);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        news_list = new ArrayList<>();
        myAdapter = new NewsAdapter(this,news_list);
        mRecyclerView.setAdapter(myAdapter);





        final APIInterface apiService = ApiClient.getClient().create(APIInterface.class);
        Call<ResponseModel> call = apiService.getLatestNews("the-times-of-india","Farmer","8cca79eb565a4b1cb52e59e184d4ac4c");
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel>call, Response<ResponseModel> response) {
                if(response.body().getStatus().equals("ok")) {
                    List<Article> articleList = response.body().getArticles();
                    if(articleList.size()>0) {

                        for(int i=0;i<articleList.size();i++) {
                            Log.i("news:", articleList.get(0).getTitle());

                            News_List item = new News_List(articleList.get(i).getTitle(), articleList.get(i).getDescription(), articleList.get(i).getUrlToImage(),articleList.get(i).getUrl());
                            news_list.add(item);
                        }
                        myAdapter.notifyDataSetChanged();

                    }

                }
            }
            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.e("out", t.toString());
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.logout:
            //add the function to perform here
            Intent mg=new Intent(this,Login.class);

            mg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(mg);
            return(true);

    }
        return(super.onOptionsItemSelected(item));
    }

}