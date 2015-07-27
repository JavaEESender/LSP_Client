package ua.klo.lp;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ua.klo.lp.R;

@SuppressLint("HandlerLeak")
public class MainActivity extends Activity implements OnClickListener {
	static String server;
	static int port;
	ListView lv;
	List<String> list = new ArrayList<String>();
	String[] item = new String[6];
	SharedPreferences spref;
	ProgressBar progress;
	Handler h;
	Context cont = this;
	String s;
	Toast toast = null;

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
		progress = (ProgressBar) findViewById(R.id.progress);
		lv = (ListView) findViewById(R.id.lv1);
		h = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == 1)
					progress.setVisibility(View.VISIBLE);
				if (msg.what == 2)
					progress.setVisibility(View.INVISIBLE);
				if (msg.what == 3) {
					if (list != null) {
						ArrayAdapter<String> aad = new ArrayAdapter<String>(
								cont, android.R.layout.simple_list_item_1, list);
						lv.setAdapter(aad);
					} else {
						lv.setAdapter(null);
						if (toast != null) {
							toast.cancel();
						}
						toast = Toast.makeText(getApplicationContext(),
								"Connect FAIL!",
								Toast.LENGTH_LONG);
						toast.show();
					}
				}
				if (msg.what == 4)
					showDialog(1);
			};
		};
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked,
					int position, long id) {
				TextView textView = (TextView) itemClicked;
				s = textView.getText().toString();
				Thread tr = new Thread(new Runnable() {
					@Override
					public void run() {
						h.sendEmptyMessage(1);
						item = new SokWorker().getAzs(s);
						h.sendEmptyMessage(2);
						h.sendEmptyMessage(4);
					}
				});
				tr.start();
			}
		});

		spref = getPreferences(MODE_PRIVATE);
		if (spref.contains("SERVER_GW")) {
			String savedText = spref.getString("SERVER_GW", "");
			MainActivity.server = savedText;
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
      // TODO Auto-generated method stub
      // добавляем пункты меню
      menu.add(0, 1, 0, "Full access");
      menu.add(0, 2, 0, "List AZS");
      menu.add(0, 3, 0, "Setings");
      
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
			men = new Intent(this, ConfActivity.class);
			startActivity(men);
			break;
		}
      
      return super.onOptionsItemSelected(item);
    }

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(item[0]);
		adb.setMessage(item[1] + " " + item[2] + "\n" + getString(R.string.subnet) + " - " + item[3]
				+ "\n" + item[4] + "\n" + getString(R.string.AZS) + " " + item[5]);
		adb.setNeutralButton("Restart", myClickListener);
		adb.setNegativeButton("Cancel", myEmptyClickListener);
		adb.setPositiveButton("Call ISP", myCallListener);
		adb.setCancelable(false);
		return adb.create();
	}

	android.content.DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			String tmp;
			tmp = new SokWorker().RestartVPN(item[0]);
			removeDialog(1);
			Toast.makeText(getApplicationContext(),
					tmp,
					Toast.LENGTH_LONG).show();
		}
	};

	android.content.DialogInterface.OnClickListener myEmptyClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			removeDialog(1);
		}
	};
	
	android.content.DialogInterface.OnClickListener myCallListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + item[2]));
			startActivity(dialIntent);
			removeDialog(1);
		}
	};

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
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
					list = new SokWorker().getPing();
					h.sendEmptyMessage(3);
					h.sendEmptyMessage(2);
				}
			});
			tr.start();
			break;
		case R.id.confButt:
			intent = new Intent(this, ConfActivity.class);
			startActivity(intent);
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
