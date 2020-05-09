package com.example.topfeed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class customAdaptar extends BaseAdapter {
    ArrayList<feedEntry> feed;
    Context cont;
    private static final String TAG = "customAdaptar";

    public customAdaptar(ArrayList<feedEntry> fe, Context c){
            this.feed=fe;
            this.cont=c;
    }
    @Override
    public Object getItem(int position) {
        return feed.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return feed.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.d(TAG, "getView: in getView");
        if(convertView==null)
        {
            convertView= LayoutInflater.from(cont).inflate(R.layout.list_item_custom,parent,false);
        }

        TextView titleTxt= (TextView) convertView.findViewById(R.id.item_Title);
        titleTxt.setTextColor(Color.BLUE);
        TextView descTxt= (TextView) convertView.findViewById(R.id.item_Description);
        TextView dateTxt= (TextView) convertView.findViewById(R.id.item_UpdateDate);
        ImageView img= (ImageView) convertView.findViewById(R.id.item_Image);

        feedEntry article= (feedEntry) this.getItem(position);

        String title=article.getTitle();
        String desc=article.getDescription();
        String date=article.getUpdatedat();
        String imageUrl=article.getStoryimage();

        titleTxt.setText(title);
        descTxt.setText(desc);
        dateTxt.setText(date);
        img.setTag(imageUrl);
        new DownloadImageTask().execute(img);

        return convertView;
    }

    public class DownloadImageTask extends AsyncTask<ImageView,Void, Bitmap>{
        ImageView imageView=null;
        private static final String TAG = "DownloadImageTask";
        @Override
        protected Bitmap doInBackground(ImageView... imageViews) {
//            Log.d(TAG, "doInBackground: image downloading start");
            this.imageView=imageViews[0];
            return download_Image((String)imageView.getTag());
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
//            Log.d(TAG, "onPostExecute: image downloading end");

        }
        private Bitmap download_Image(String url){
            Bitmap bmp=null;
            try{
                URL url1=new URL(url);
                HttpURLConnection con=(HttpURLConnection)url1.openConnection();
                InputStream is=con.getInputStream();
                bmp= BitmapFactory.decodeStream(is);
                if(null!=bmp)
                    return bmp;
            }catch(Exception e){
                Log.d(TAG, "download_Image: Error in Downloading the image"+e.getMessage());
            }
            return bmp;
        }
    }
}
