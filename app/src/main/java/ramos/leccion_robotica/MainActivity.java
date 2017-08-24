package ramos.leccion_robotica;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements DResultReceiver.Receiver{

    private ListView listView;
    private Button btnStart;
    private ArrayAdapter arrayAdapter = null;
    private DResultReceiver mReceiver;
    final String url = "https://jsonplaceholder.typicode.com"; //servicio

    NotificationCompat.Builder notification;
    PendingIntent pIntent;
    NotificationManager manager;
    Intent resultIntent;
    TaskStackBuilder stackBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView)findViewById(R.id.listview);
        btnStart = (Button)findViewById(R.id.btnService);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownloader(view);
            }
        });
    }


    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case Downloader.STATUS_RUNNING:
                Toast.makeText(this, "Buscando...", Toast.LENGTH_SHORT).show();

                //startNotification();
                break;

            case Downloader.STATUS_FINISHED:

                String[] results = resultData.getStringArray("result");

                //LLenando lista
                arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, results);
                listView.setAdapter(arrayAdapter);
                break;

            case Downloader.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void startDownloader (View v){

        mReceiver = new DResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, Downloader.class);

        intent.putExtra("url", url);
        intent.putExtra("receiver", mReceiver);
        intent.putExtra("requestId", 101);

        startService(intent);


    }

    protected void startNotification() {
        // TODO Auto-generated method stub
        //Creating Notification Builder
        //Title for Notification
        notification.setContentTitle("Conectado.");
        //Message in the Notification
        notification.setContentText("Notificacion nueva.");
        //Alert shown when Notification is received
        notification.setTicker("Aviso nuevo!");
        pIntent =  stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pIntent);
        manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification.build());

    }

}
