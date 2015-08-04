package ua.klo.lp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
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

public class InetActivity extends Activity implements OnClickListener {
	Spinner spinnerForms;
	ListView listView_Inet;
	List<String> arrayList_Inet;
	Handler h;
	String ip;
    String resp;
    String add_inet;
    String add_inter;
	Toast toast = null;
	ProgressDialog dialog;
	Context cont = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inet);
		findViewById(R.id.InetButt).setOnClickListener(this);
		String[] objects = { "1", "3", "24" };
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
				android.R.layout.simple_spinner_item, objects);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinnerForms = (Spinner) findViewById(R.id.spinnerIP);
		spinnerForms.setAdapter(adapter);

		listView_Inet = (ListView) findViewById(R.id.ListView_Inet);
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
                    if (arrayList_Inet != null) {
                        ArrayAdapter<String> aad = new ArrayAdapter<>(
                                cont, android.R.layout.simple_list_item_1, arrayList_Inet);
                        listView_Inet.setAdapter(aad);
                    } else {
                        listView_Inet.setAdapter(null);
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
                    showDialog(1);
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
            };
        };

        Thread tr = new Thread(new Runnable() {
            @Override
            public void run() {
                h.sendEmptyMessage(1);
                arrayList_Inet = new SocketWorker().getInet();
                h.sendEmptyMessage(3);
                h.sendEmptyMessage(2);
            }
        });
        tr.start();

		listView_Inet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
		final EditText subEd = (EditText) findViewById(R.id.subNetEdit);
		final EditText ipEd = (EditText) findViewById(R.id.IpEdit);
		if ((subEd.getText().length() != 0) && (ipEd.getText().length() != 0)) {
			add_inet = "10.0." + subEd.getText().toString() + "."
					+ ipEd.getText().toString();
			add_inter = (String) spinnerForms.getSelectedItem();
            Thread btn = new Thread(new Runnable() {
                @Override
                public void run() {
            h.sendEmptyMessage(1);
            resp = new SocketWorker().setInet(add_inet, add_inter);
            if (resp != null) {
				h.sendEmptyMessage(5);
                arrayList_Inet = new SocketWorker().getInet();

			} else {
				h.sendEmptyMessage(5);
			}
                    h.sendEmptyMessage(3);
                    h.sendEmptyMessage(2);
                }
            });
            btn.start();
		}else{
			Thread tr = new Thread(new Runnable() {
				@Override
				public void run() {
                    h.sendEmptyMessage(1);
					arrayList_Inet = new SocketWorker().getInet();
					h.sendEmptyMessage(3);
					h.sendEmptyMessage(2);
				}
			});
			tr.start();
		}
	}

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1 :
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(getString(R.string.full_access));
                adb.setMessage(ip);
                adb.setNegativeButton(getString(R.string.cancel), myEmptyClickListener);
                adb.setPositiveButton(getString(R.string.delete), myCallListener);
                adb.setCancelable(false);
                return adb.create();
            case 2 :
                return null;
        }
        return null;
    }
    android.content.DialogInterface.OnClickListener myEmptyClickListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            removeDialog(1);
        }
    };

    android.content.DialogInterface.OnClickListener myCallListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            Thread tr = new Thread(new Runnable() {
                @Override
                public void run() {
            h.sendEmptyMessage(1);
            resp = new SocketWorker().remInet(ip);
            h.sendEmptyMessage(5);
            removeDialog(1);
                    arrayList_Inet = new SocketWorker().getInet();
                    h.sendEmptyMessage(3);
                    h.sendEmptyMessage(2);
                }
            });
            tr.start();

        }
    };
}
