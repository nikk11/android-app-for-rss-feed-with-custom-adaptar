package com.example.topfeed;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class parserApplications extends AsyncTask<Void,Void,Boolean> {
    private static final String TAG = "parserApplications";
    private ArrayList<feedEntry> applications = new ArrayList<>();
    Context cont;
    ListView lv;
    String xmlData="";
    
    @Override
    protected void onPostExecute(Boolean isParsed) {
//        Log.d(TAG, "onPostExecute: on parser post execute");
        lv.setAdapter(new customAdaptar(applications,cont));
//        Log.d(TAG, "onPostExecute: the customadaptar is called");
        super.onPostExecute(isParsed);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
//        Log.d(TAG, "doInBackground: on parser doInbackground");
        parse(xmlData);
        return null;
    }

    public parserApplications(Context c,String s,ListView listView) {
        this.cont=c;
        this.xmlData=s;
        this.lv=listView;
    }

    public ArrayList<feedEntry> getApplications() {
        return applications;
    }

    public boolean parse(String xmlData) {
        boolean status = true;
        feedEntry currentRecord=null;
        boolean inEntry = false;
        String textValue = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("item".equalsIgnoreCase(tagName)) {
//                            Log.d(TAG, "parse: Starting tag for:" + tagName);
                            inEntry = true;
                            currentRecord = new feedEntry();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
//                        Log.d(TAG, "parse: Ending tag for" + tagName);
                        if (inEntry) {
                            if ("item".equalsIgnoreCase(tagName)) {
                                applications.add(currentRecord);
                                inEntry = false;
                            } else if ("title".equalsIgnoreCase(tagName)) {
                                currentRecord.setTitle(textValue);
                            }else if("updatedat".equalsIgnoreCase(tagName)){
                                currentRecord.setUpdatedat(textValue);
                            }
                            else if("storyimage".equalsIgnoreCase(tagName)){
                                currentRecord.setStoryimage(textValue);
                            }
                            else if("description".equalsIgnoreCase(tagName)){
                                currentRecord.setDescription(textValue);
                            }
                        }
                        break;
                    default:
                }
                eventType=xpp.next();
            }
//            for(feedEntry app:applications){
//                Log.d(TAG, "******");
//                Log.d(TAG,app.toString());
//            }
        } catch (Exception e) {
            status = false;
            e.printStackTrace();
        }

        return status;
    }

}
