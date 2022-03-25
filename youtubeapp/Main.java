package com.example.sll_youtube;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends YouTubeBaseActivity {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private static final JsonFactory JSON_FACTORY = new GsonFactory();

    private static final long NUMBER_OF_VIDEOS_RETURNED = 2;

    private static YouTube youtube;



    //객체 선언.
    YouTubePlayerView playerView;
    YouTubePlayer player;
    //유튜브 API KEY와 동영상 ID 변수 설정
    private String API_KEY = "AIzaSyACb7G5oZvYGGXaXFqwHTkyelukBOVw9SQ";
    //https://www.youtube.com/watch?v=hl-ii7W4ITg ▶ 유튜브 동영상 v= 다음 부분이 videoId
    private String videoId;
    //logcat 사용 설정
    private static final String TAG = "MainActivity";
    private String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView output = findViewById(R.id.output);
        TextView output2 = findViewById(R.id.output2);
        TextView output3 = findViewById(R.id.output3);

        EditText input = findViewById(R.id.dateText);
        Button show = findViewById(R.id.search);


        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result = input.getText().toString();
                output.setText(result);

                try{
                    //output2.setText("OK");  --->  try문은 실행됨
                    youtube = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY, new HttpRequestInitializer() {
                        public void initialize(HttpRequest request) throws IOException {}
                    }).setApplicationName("youtube-cmdline-search-sample").build();

                    // Get query term from user.
                    String queryTerm=result;

                    //여기까지 됨
                    YouTube.Search.List search = youtube.search().list("snippet");

                    /*
                     * It is important to set your API key from the Google Developer Console for
                     * non-authenticated requests (found under the Credentials tab at this link:
                     * console.developers.google.com/). This is good practice and increased your quota.
                     */

                    String apiKey = "AIzaSyACb7G5oZvYGGXaXFqwHTkyelukBOVw9SQ";
                    search.setKey(apiKey);
                    search.setQ(queryTerm);

                    search.setType("video");

                    //search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
                    search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
                    new Thread(() -> {
                        SearchListResponse searchResponse = null;
                        try {
                            searchResponse = search.execute();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        List<SearchResult> searchResultList = searchResponse.getItems();

                        if (searchResultList != null) {
                            output2.setText("searchResultList is not null");
                            Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();
                            String query = queryTerm;
                            System.out.println("\n=============================================================");
                            System.out.println(
                                    "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
                            System.out.println("=============================================================\n");

                            if (!iteratorSearchResults.hasNext()) {
                                System.out.println(" There aren't any results for your query.");
                                // output2.setText(" There aren't any results for your query.");
                            }

                            while (iteratorSearchResults.hasNext()) {

                                SearchResult singleVideo = iteratorSearchResults.next();
                                ResourceId rId = singleVideo.getId();
                                videoId = rId.getVideoId();

                                // Double checks the kind is video.
                                if (rId.getKind().equals("youtube#video")) {
                                    Thumbnail thumbnail = (Thumbnail) singleVideo.getSnippet().getThumbnails().get("default");

                                    System.out.println(" Video Id" + rId.getVideoId());
                                    System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                                    System.out.println(" Thumbnail: " + thumbnail.getUrl());
                                    System.out.println("\n-------------------------------------------------------------\n");
                                }
                            }
                            // prettyPrint(searchResultList.iterator(), queryTerm);
                        }
                        else
                        {
                            output2.setText("searchResultList is not null");
                        }
                    }).start();



                }catch (GoogleJsonResponseException e) {
                    System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                            + e.getDetails().getMessage());
                } catch (IOException e) {
                    System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
                } catch (Throwable t) {
                    t.printStackTrace();
                    System.err.println("There was an IO error: " + t.getCause() + " : " + t.getMessage());
                }
            }
        });





        initPlayer();
        Button btnload = findViewById(R.id.load);
        btnload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.cueVideo(videoId);
            }
        });


        Button btnPlay = findViewById(R.id.youtubeBtn);
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playVideo();
            }
        });

        Button btnfullScreen = findViewById(R.id.fullscreen);
        btnfullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {player.setFullscreen(true);}
        });
    }
    private void playVideo() {
        if(player != null) {
            if(player.isPlaying()) {
                player.pause();
            }
            else
            {
                player.play();
            }

        }
    }
    //유튜브 플레이어 메서드
    private void initPlayer() {
        playerView = findViewById(R.id.youTubePlayerView);
        playerView.initialize(API_KEY, new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                player = youTubePlayer;
                player.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                    @Override
                    public void onLoading() {
                    }
                    @Override
                    public void onLoaded(String id) {
                        Log.d(TAG, "onLoaded: " + id);
                        player.play();
                    }
                    @Override
                    public void onAdStarted() {
                    }
                    @Override
                    public void onVideoStarted() {
                    }
                    @Override
                    public void onVideoEnded() {
                    }
                    @Override
                    public void onError(YouTubePlayer.ErrorReason errorReason) {
                        Log.d(TAG, "onError: " + errorReason);
                    }
                });
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            }
        });
    }

    private void prettyPrint(Iterator<SearchResult> iteratorSearchResults, String query) {

        System.out.println("\n=============================================================");
        System.out.println(
                "   First " + NUMBER_OF_VIDEOS_RETURNED + " videos for search on \"" + query + "\".");
        System.out.println("=============================================================\n");

        if (!iteratorSearchResults.hasNext()) {
            System.out.println(" There aren't any results for your query.");
            //output2.setText(" There aren't any results for your query.");

        }

        while (iteratorSearchResults.hasNext()) {

            SearchResult singleVideo = iteratorSearchResults.next();
            ResourceId rId = singleVideo.getId();

            // Double checks the kind is video.
            if (rId.getKind().equals("youtube#video")) {
                Thumbnail thumbnail = (Thumbnail) singleVideo.getSnippet().getThumbnails().get("default");

                System.out.println(" Video Id" + rId.getVideoId());
                //output3.setText(" Video Id" + rId.getVideoId());
                System.out.println(" Title: " + singleVideo.getSnippet().getTitle());
                System.out.println(" Thumbnail: " + thumbnail.getUrl());
                System.out.println("\n-------------------------------------------------------------\n");
            }
        }
    }
}

