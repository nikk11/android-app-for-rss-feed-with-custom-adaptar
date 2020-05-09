package com.example.topfeed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listApps;
    ArrayList<feedEntry> feedEntries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps=(ListView) findViewById(R.id.listApps);
//        Log.d(TAG, "onCreate: starting async task");
        downloadUrl("https://feeds.feedburner.com/ndtvnews-top-stories");
//        Log.d(TAG, "onCreate: Async task is finished");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feedtype_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId( );
        String feedurl;
        switch(id){
            case R.id.top_Stories:
                feedurl="https://feeds.feedburner.com/ndtvnews-top-stories";
                break;
            case R.id.trending:
                feedurl="https://feeds.feedburner.com/ndtvnews-trending-news";
                break;
            case R.id.india:
                feedurl="https://feeds.feedburner.com/ndtvnews-india-news";
                break;
            case R.id.world:
                feedurl="https://feeds.feedburner.com/ndtvnews-world-news";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        downloadUrl(feedurl);
        return true;

    }
    private void downloadUrl(String feedUrl)
    {
        DownloadData downloadData=new DownloadData();
        downloadData.execute(feedUrl);
    }


    private class DownloadData extends AsyncTask<String, Void, String> {
        Context c;
        private static final String TAG = "DownloadData";
        @Override
        protected void onPostExecute(String s) {
//            Log.d(TAG, "onPostExecute: "+ s);
            super.onPostExecute(s);
            new parserApplications(MainActivity.this,s,listApps).execute();
//            Log.d(TAG, "onPostExecute: Done Post execution");

        }

        @Override
        protected String doInBackground(String... strings) {
//            Log.d(TAG, "doInBackground:" + strings[0]);
            String rssFeed = downLoadXML(strings[0]);
            if(rssFeed==null)
            {
                Log.d(TAG, "doInBackground: Error Downloading");   
            }
            return rssFeed;
        }
        private String downLoadXML(String urlPath){
            StringBuilder XMLResult=new StringBuilder();

            try{
                URL url=new URL(urlPath);
                HttpURLConnection connection=(HttpURLConnection) url.openConnection();
                int response=connection.getResponseCode();
                BufferedReader reader=new BufferedReader(new InputStreamReader(connection.getInputStream()));

                int charsRead ;
                char[] inputBuffer =new char[500];
                while(true)
                {
                    charsRead=reader.read(inputBuffer);
                    if(charsRead<0)
                    {
                        break;
                    }
                    if(charsRead>0)
                    {
                        XMLResult.append(String.copyValueOf(inputBuffer,0,charsRead));
                    }
                }
                reader.close();

                return XMLResult.toString();
            }
            catch(MalformedURLException e)
            {
                Log.d(TAG, "downLoadXML Invalid URL :"+e.getMessage());
            }
            catch(IOException e){
                Log.d(TAG, "downLoadXML: IOException Reading data "+e.getMessage());
            }
            catch(SecurityException e)
            {
                Log.d(TAG, "downLoadXML: Security exception"+e.getMessage());
            }
            return null;
        }
    }

}
