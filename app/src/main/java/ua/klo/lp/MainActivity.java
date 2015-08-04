package ua.klo.lp;

import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
    static String server;
    static int port;
    ListView listView_AZS;
    List<String> arrayList_AZS;
    String[] item = new String[6];
    SharedPreferences spref;
    Handler h;
    Context cont = this;
    String s;
    ProgressDialog dialog;
    Toast toast = null;
    EditText server_txt;
    EditText port_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        findViewById(R.id.InetButt).setOnClickListener(this);
        findViewById(R.id.confButt).setOnClickListener(this);
        findViewById(R.id.inetButtM).setOnClickListener(this);
        findViewById(R.id.btActAzs).setOnClickListener(this);
        listView_AZS = (ListView) findViewById(R.id.ListView_AZS);
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == 1)
                    dialog = new ProgressDialog(cont);
                dialog.setMessage(getString(R.string.connect));
                dialog.setIndeterminate(true);
                dialog.setCancelable(true);
                dialog.show();
                if (msg.what == 2)
                    dialog.dismiss();
                if (msg.what == 3) {
                    if (arrayList_AZS != null) {
                        ArrayAdapter<String> aad = new ArrayAdapter<>(
                                cont, android.R.layout.simple_list_item_1, arrayList_AZS);
                        listView_AZS.setAdapter(aad);
                    } else {
                        listView_AZS.setAdapter(null);
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(getApplicationContext(),
                                getString(R.string.connect_fail),
                                Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
                if (msg.what == 4) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(cont);
                    alertDialog.setTitle(item[0]);
                    alertDialog.setMessage(item[1] + " " + item[2] + "\n" + getString(R.string.subnet) + " - " + item[3]
                            + "\n" + item[4] + "\n" + getString(R.string.AZS) + " " + item[5]);
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton(getString(R.string.call), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item[2]));
                            startActivity(dialIntent);

                        }
                    });
                    alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.setNeutralButton("restart", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String tmp;
                            tmp = new SocketWorker().RestartVPN(item[0]);
                            Toast.makeText(getApplicationContext(),
                                    tmp,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                    alertDialog.show();
                }
            }
        };
        listView_AZS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked,
                                    int position, long id) {
                TextView textView = (TextView) itemClicked;
                s = textView.getText().toString();
                Thread tr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        h.sendEmptyMessage(1);
                        item = new SocketWorker().getAzs(s);
                        h.sendEmptyMessage(4);
                        h.sendEmptyMessage(2);
                    }
                });
                tr.start();
            }
        });

        spref = getPreferences(MODE_PRIVATE);
        if (spref.contains("SERVER_GW")) {
            MainActivity.server = spref.getString("SERVER_GW", "");
        } else {
            Editor ed = spref.edit();
            ed.putString("SERVER_GW", "gw.klo.ua");
            ed.commit();
            MainActivity.server = "gw.klo.ua";
        }
        if (spref.contains("PORT_GW")) {
            String savedText = spref.getString("PORT_GW", "");
            MainActivity.port = Integer.parseInt(savedText);
        } else {
            Editor ed = spref.edit();
            ed.putString("PORT_GW", "7855");
            ed.commit();
            MainActivity.port = 7855;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, getString(R.string.full_access));
        menu.add(0, 2, 0, getString(R.string.list_azs));
        menu.add(0, 3, 0, getString(R.string.settings));

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Intent men;
        switch (item.getItemId()) {
            case 1:
                men = new Intent(this, InetActivity.class);
                startActivity(men);
                break;
            case 2:
                men = new Intent(this, Azs_activity.class);
                startActivity(men);
                break;
            case 3:
                LayoutInflater li = LayoutInflater.from(cont);
                View settingsView = li.inflate(R.layout.settings, null);
                server_txt = (EditText) settingsView.findViewById(R.id.editText_server);
                port_txt = (EditText) settingsView.findViewById(R.id.editText_port);
                server_txt.setText(server);
                port_txt.setText("" + port);
                AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(cont);
                mDialogBuilder.setView(settingsView);
                mDialogBuilder
                        .setTitle(getString(R.string.settings))
                        .setCancelable(false)
                        .setPositiveButton(getString(R.string.save),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Editor ed = spref.edit();
                                        ed.putString("SERVER_GW", server_txt.getText().toString());
                                        ed.putString("PORT_GW", port_txt.getText().toString());
                                        ed.commit();
                                        MainActivity.server = server_txt.getText().toString();
                                        MainActivity.port = Integer.parseInt(port_txt.getText().toString());
                                    }
                                })
                        .setNegativeButton(getString(R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                AlertDialog alertDialog = mDialogBuilder.create();
                alertDialog.show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // inter
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.InetButt:
                Thread tr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        h.sendEmptyMessage(1);
                        arrayList_AZS = new SocketWorker().getPing();
                        h.sendEmptyMessage(3);
                        h.sendEmptyMessage(2);
                    }
                });
                tr.start();
                break;
            case R.id.inetButtM:
                intent = new Intent(this, InetActivity.class);
                startActivity(intent);
                break;
            case R.id.btActAzs:
                intent = new Intent(this, Azs_activity.class);
                startActivity(intent);
                break;
        }
    }

}
