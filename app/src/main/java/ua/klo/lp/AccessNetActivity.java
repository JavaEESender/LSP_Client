package ua.klo.lp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class AccessNetActivity extends Activity implements OnClickListener {

    EditText subnet_txt;
    EditText ip_txt;
    Spinner spinnerForms;
    ListView listView_Net;
    List<String> arrayList_Net;
    Handler h;
    String ip;
    String resp;
    String add_net;
    String add_inter;
    Toast toast = null;
    ProgressDialog dialog;
    Context cont = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inet);
        findViewById(R.id.imageButton_add_inet).setOnClickListener(this);
        listView_Net = (ListView) findViewById(R.id.ListView_Inet);
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
                    if (arrayList_Net != null) {
                        ArrayAdapter<String> aad = new ArrayAdapter<>(
                                cont, android.R.layout.simple_list_item_1, arrayList_Net);
                        listView_Net.setAdapter(aad);
                    } else {
                        listView_Net.setAdapter(null);
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
                    infoIp();
                }
                if (msg.what == 5) {
                    if (resp != null) {
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(getApplicationContext(),
                                resp,
                                Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(getApplicationContext(),
                                getString(R.string.connect_fail),
                                Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            }
        };

        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {
                h.sendEmptyMessage(1);
                arrayList_Net = new SocketWorker().getInet();
                h.sendEmptyMessage(3);
                h.sendEmptyMessage(2);
            }
        });
        tr.start();

        listView_Net.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked,
                                    int position, long id) {
                TextView textView = (TextView) itemClicked;
                ip = textView.getText().toString();
                Thread tr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        h.sendEmptyMessage(1);
                        h.sendEmptyMessage(4);
                        h.sendEmptyMessage(2);
                    }
                });
                tr.start();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton_add_inet:
                getDialogAddInet();
                break;
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        //System.out.println("First ["+this.getFirstVisiblePosition()+"]");
//
//        float y = event.getY();
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN: {
//                Thread tr = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        h.sendEmptyMessage(1);
//                        arrayList_Inet = new SocketWorker().getInet();
//                        h.sendEmptyMessage(3);
//                        h.sendEmptyMessage(2);
//                    }
//                });
//                tr.start();
//            }
//        }
//        return super.onTouchEvent(event);
//    }

    public void getDialogAddInet() {
        LayoutInflater li = LayoutInflater.from(cont);
        @SuppressLint("InflateParams")
        View addInetView = li.inflate(R.layout.activity_add_inet, null);
        subnet_txt = (EditText) addInetView.findViewById(R.id.subNetEdit);
        ip_txt = (EditText) addInetView.findViewById(R.id.IpEdit);
        String[] objects = {"1 " + getString(R.string.hour), "3 " + getString(R.string.hours), "24 " + getString(R.string.hours)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, objects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerForms = (Spinner) addInetView.findViewById(R.id.spinnerIP);
        spinnerForms.setAdapter(adapter);
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(cont);
        mDialogBuilder.setView(addInetView);
        mDialogBuilder
                .setTitle(getString(R.string.full_access))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.open),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if ((subnet_txt.getText().length() != 0) && (ip_txt.getText().length() != 0)) {
                                    add_net = "10.0." + subnet_txt.getText().toString() + "."
                                            + ip_txt.getText().toString();
                                    switch (spinnerForms.getSelectedItemPosition()) {
                                        case 0:
                                            add_inter = "1";
                                            break;
                                        case 1:
                                            add_inter = "3";
                                            break;
                                        case 2:
                                            add_inter = "24";
                                            break;
                                    }
                                    Thread btn = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            h.sendEmptyMessage(1);
                                            resp = new SocketWorker().setInet(add_net, add_inter);
                                            if (resp != null) {
                                                h.sendEmptyMessage(5);
                                                arrayList_Net = new SocketWorker().getInet();

                                            } else {
                                                h.sendEmptyMessage(5);
                                            }
                                            h.sendEmptyMessage(3);
                                            h.sendEmptyMessage(2);
                                        }
                                    });
                                    btn.start();
                                } else {
                                    Thread tr = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            h.sendEmptyMessage(1);
                                            arrayList_Net = new SocketWorker().getInet();
                                            h.sendEmptyMessage(3);
                                            h.sendEmptyMessage(2);
                                        }
                                    });
                                    tr.start();
                                }
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

    public void infoIp() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(cont);
        alertDialog.setTitle(getString(R.string.full_access));
        alertDialog.setMessage(ip + "    " + new SocketWorker().getExpireTime(ip));
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Thread tr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        h.sendEmptyMessage(1);
                        resp = new SocketWorker().remInet(ip);
                        h.sendEmptyMessage(5);
                        arrayList_Net = new SocketWorker().getInet();
                        h.sendEmptyMessage(3);
                        h.sendEmptyMessage(2);
                    }
                });
                tr.start();
            }
        });

        alertDialog.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }
}
