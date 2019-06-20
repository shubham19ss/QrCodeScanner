package com.e.qrscanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

public class DisplayQrDataActivity extends AppCompatActivity {
    private static final String TAG = "CardListActivity";
    private CardArrayAdapter cardArrayAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_qr_data);
        Intent i=getIntent();
        List<QrData> li=(List<QrData>)i.getSerializableExtra("qrlist");

        listView = (ListView) findViewById(R.id.card_listView);

        listView.addHeaderView(new View(this));
        listView.addFooterView(new View(this));

        cardArrayAdapter = new CardArrayAdapter(getApplicationContext(), R.layout.list_item_card);
        for (QrData qr:li)
        {
            cardArrayAdapter.add(new QrData(qr.type,qr.data,qr.description));
        }
        listView.setAdapter(cardArrayAdapter);

    }
}
