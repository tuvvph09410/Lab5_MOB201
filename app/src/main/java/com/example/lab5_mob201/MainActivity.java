package com.example.lab5_mob201;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.Toolbar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText edEnterText;
    private Button btnDownload;
    private ListView lvXML;
    private List<String> stringList;
    private List<String> linkList;
    private ArrayAdapter stringArrayAdapter;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initViewById();
        this.initList();
        this.initButton();
        this.initListView();
    }



    private void initListView() {
        this.lvXML.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String link = linkList.get(position);
                intent = new Intent(MainActivity.this, LoadWebView.class);
                intent.putExtra("link", link);
                startActivity(intent);
            }
        });
    }

    private void initButton() {
        this.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String linkXML = edEnterText.getText().toString();
                if (linkXML.length() != 0) {
                    initAsyncTask(linkXML);
                    Log.e("XMLName", linkXML);
                } else {
                    Toast.makeText(MainActivity.this, "Chưa nhập link, Vui lòng nhập vào", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void initAsyncTask(String linkXML) {
        AsyncTask<String, Void, String> asyncTask = new RSSFeed().execute(linkXML);
        this.stringArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stringList);
        this.lvXML.setAdapter(stringArrayAdapter);
    }

    private void initList() {
        this.stringList = new ArrayList<>();
        this.linkList = new ArrayList<>();
    }

    private void initViewById() {
        this.edEnterText = findViewById(R.id.ed_enterLink);
        this.btnDownload = findViewById(R.id.btn_download);
        this.lvXML = findViewById(R.id.lv_readXML);
    }

    public class RSSFeed extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            StringBuilder content = new StringBuilder();
            try {
                URL url = new URL(strings[0]);
                InputStreamReader inputStreamReader = new InputStreamReader(url.openConnection().getInputStream());
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
                ;
            }
            return content.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            XMLParse xmlParse = new XMLParse();
            try {
                Document document = xmlParse.getDocument(s);
                NodeList nodeList = document.getElementsByTagName("item");
                String title = "";
                String link = "";
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Element element = (Element) nodeList.item(i);
                    title = xmlParse.getValue(element, "title");
                    link = xmlParse.getValue(element, "link");
                    stringList.add(title);
                    linkList.add(link);
                }
                stringArrayAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }
    }
}