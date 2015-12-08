package ua.klo.lp;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
    static String salt;

    Context cont = this;
    EditText server_txt;
    EditText port_txt;
    EditText usr_login;
    EditText usr_pass;
    Handler h;
    ListView listView_AZS;
    List<String> arrayList_AZS;
    ProgressDialog dialog;
    String[] item = new String[7];
    SharedPreferences sharedPreferences;
    String s;
    Toast toast = null;
    PackageInfo pInfo = null;
    boolean actual_version = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        findViewById(R.id.pingButton).setOnClickListener(this);
        listView_AZS = (ListView) findViewById(R.id.ListView_AZS);
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
                        dialog = new ProgressDialog(cont);
                        dialog.setMessage(getString(R.string.connect));
                        dialog.setIndeterminate(true);
                        dialog.setCancelable(true);
                        dialog.show();
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                    case 3:
                        pingAZS();
                        break;
                    case 4:
                        infoAZS();
                        break;
                    case 5:
                        checkUpdate();
                        break;
                    case 6:
                        dialog = new ProgressDialog(cont);
                        dialog.setMessage(getString(R.string.downloading_update));
                        dialog.setIndeterminate(true);
                        dialog.setCancelable(true);
                        dialog.show();
                        break;
                    case 7:
                        dialog.dismiss();
                        if (arrayList_AZS.size() < 1){
                            if (toast != null) {
                                toast.cancel();
                            }
                            toast = Toast.makeText(getApplicationContext(),
                                    getString(R.string.all_connected),
                                    Toast.LENGTH_LONG);
                            toast.show();
                        }
                        break;
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

        sharedPreferences = getPreferences(MODE_PRIVATE);
        if (sharedPreferences.contains("SERVER_GW")) {
            MainActivity.server = sharedPreferences.getString("SERVER_GW", "");
        } else {
            Editor ed = sharedPreferences.edit();
            ed.putString("SERVER_GW", "gw.klo.ua");
            ed.apply();
            MainActivity.server = "gw.klo.ua";
        }
        if (sharedPreferences.contains("PORT_GW")) {
            String savedText = sharedPreferences.getString("PORT_GW", "");
            MainActivity.port = Integer.parseInt(savedText);
        } else {
            Editor ed = sharedPreferences.edit();
            ed.putString("PORT_GW", "7855");
            ed.apply();
            MainActivity.port = 7855;
        }
        if (sharedPreferences.contains("SALT_GW")) {
            MainActivity.salt = sharedPreferences.getString("SALT_GW", "");
        } else {
            logIN();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent men;
        switch (item.getItemId()) {
            case R.id.action_full_access:
                men = new Intent(this, AccessNetActivity.class);
                startActivity(men);
                break;
            case R.id.action_black_list:
                men = new Intent(this, RejActivity.class);
                startActivity(men);
                break;
            case R.id.action_list_azs:
                men = new Intent(this, Azs_activity.class);
                startActivity(men);
                break;
            case R.id.action_settings:
                getSettings();
                break;
            case R.id.action_exit:
                getExit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pingButton:
                Thread tr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        h.sendEmptyMessage(1);
                        arrayList_AZS = new SocketWorker().getPing();
                        h.sendEmptyMessage(3);
                        h.sendEmptyMessage(7);
                    }
                });
                tr.start();
                break;
        }
    }

    public void logIN() {
        LayoutInflater li = LayoutInflater.from(cont);
        @SuppressLint("InflateParams")
        View loginView = li.inflate(R.layout.activity_login, null);
        usr_login = (EditText) loginView.findViewById(R.id.editText_login);
        usr_pass = (EditText) loginView.findViewById(R.id.editText_password);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(cont);
        mDialogBuilder.setView(loginView);
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.log_in),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!usr_login.getText().toString().equals("") && !usr_pass.getText().toString().equals("")) {
                                    String slt = new SocketWorker().getLogin(usr_login.getText().toString(), usr_pass.getText().toString());
                                    if (slt == null) {
                                        logIN();
                                        toast = Toast.makeText(getApplicationContext(),
                                                "Bad login or password",
                                                Toast.LENGTH_LONG);
                                        toast.show();
                                    } else {
                                        Editor ed = sharedPreferences.edit();
                                        ed.putString("SALT_GW", slt);
                                        ed.apply();
                                        MainActivity.salt = slt;
                                        toast = Toast.makeText(getApplicationContext(),
                                                "Success",
                                                Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                } else {
                                    logIN();
                                    toast = Toast.makeText(getApplicationContext(),
                                            "Fill in all the fields.",
                                            Toast.LENGTH_LONG);
                                    toast.show();
                                }
                            }
                        })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    public void getExit() {
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(cont);
        mDialogBuilder
                .setTitle(getString(R.string.exit))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Editor ed = sharedPreferences.edit();
                                ed.remove("SALT_GW");
                                ed.apply();
                                finish();
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
    }

    public void infoAZS() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(cont);
        alertDialog
                .setTitle(item[0])
                .setCancelable(false)
                .setMessage(item[1] + " " + item[2] + "\n" + getString(R.string.subnet) + " - " + item[3]
                        + "\n" + item[4] + "\n" + getString(R.string.AZS) + " " + item[5]
                        + "\n" + getString(R.string.no_connection) + " " + item[6])
                .setPositiveButton(getString(R.string.call), new DialogInterface.OnClickListener() {

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
        alertDialog.show();
    }

    public void pingAZS() {
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

    public void getSettings() {
        LayoutInflater li = LayoutInflater.from(cont);
        @SuppressLint("InflateParams")
        View settingsView = li.inflate(R.layout.settings_main, null);
        server_txt = (EditText) settingsView.findViewById(R.id.editText_server);
        port_txt = (EditText) settingsView.findViewById(R.id.editText_port);
        server_txt.setText(server);
        port_txt.setText("" + port);
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ((TextView) settingsView.findViewById(R.id.textView_v)).setText(pInfo.versionName);

        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(cont);
        mDialogBuilder.setView(settingsView);
        mDialogBuilder
                .setTitle(getString(R.string.settings))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.save),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Editor ed = sharedPreferences.edit();
                                ed.putString("SERVER_GW", server_txt.getText().toString());
                                ed.putString("PORT_GW", port_txt.getText().toString());
                                ed.apply();
                                MainActivity.server = server_txt.getText().toString();
                                MainActivity.port = Integer.parseInt(port_txt.getText().toString());
                            }
                        })
                .setNegativeButton(getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .setNeutralButton(getString(R.string.get_update), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Thread tr = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                h.sendEmptyMessage(1);
                                actual_version = new SocketWorker().checkUpdate(pInfo.versionName);
                                h.sendEmptyMessage(5);
                                h.sendEmptyMessage(2);
                            }
                        });
                        tr.start();
                    }
                });
        AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    public void checkUpdate() {
        if (actual_version) {
            toast = Toast.makeText(getApplicationContext(),
                    getString(R.string.have_latest_version),
                    Toast.LENGTH_LONG);
            toast.show();
        } else {
            getDialogUpdate();
        }
    }

    public void getDialogUpdate() {
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(cont);
        mDialogBuilder
                .setTitle(getString(R.string.new_version))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.get_update),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Thread tr = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        h.sendEmptyMessage(6);
                                        new SocketWorker().updateApk(cont);
                                        h.sendEmptyMessage(2);
                                    }
                                });
                                tr.start();
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
    }
}