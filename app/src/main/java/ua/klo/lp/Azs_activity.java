package ua.klo.lp;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ua.klo.lp.R;

public class Azs_activity extends Activity implements OnClickListener {

	SqlHelper sqS;
	List<String> tmp;
	Handler h;
	Context cont;
	List<Azs> lazs = new LinkedList<>();
	ProgressBar pb;
	ListView lv;
	String[] list;
	Toast toast = null;

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_azs);
		findViewById(R.id.btRefresh).setOnClickListener(this);
		sqS = new SqlHelper(this);
		cont = this;
		SQLiteDatabase db = sqS.getReadableDatabase();
		Cursor cv = db.query("azsLetsPing", null, null, null, null, null, null);
		tmp = new LinkedList<>();
		if (cv.moveToFirst()) {
			int count = cv.getColumnIndex("adress");
			do {
				tmp.add(cv.getString(count));
			} while (cv.moveToNext());
		}
		db.close();
		ArrayAdapter<String> aad = new ArrayAdapter<>(this,
				android.R.layout.simple_list_item_1, tmp);
		lv = (ListView) findViewById(R.id.lvAZS);
		pb = (ProgressBar) findViewById(R.id.progAzs);
		lv.setAdapter(aad);
		h = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case 1:
					pb.setVisibility(View.VISIBLE);
					break;
				case 2:
					ArrayAdapter<String> aas = new ArrayAdapter<>(cont,
							android.R.layout.simple_list_item_1, tmp);

					lv.setAdapter(aas);
					pb.setVisibility(View.INVISIBLE);
					if (toast != null) {
						toast.cancel();
					}
					toast = Toast.makeText(getApplicationContext(),
							"Update success.", Toast.LENGTH_LONG);
					toast.show();
					break;
				case 3:
					pb.setVisibility(View.INVISIBLE);
					if (toast != null) {
						toast.cancel();
					}
					toast = Toast.makeText(getApplicationContext(),
							"Connect FAIL!", Toast.LENGTH_LONG);
					toast.show();
					break;
				default:
					break;
				}
			}
		};
		EditText edFilter = (EditText) findViewById(R.id.edFilter);
		MyTextWacher mtw = new MyTextWacher(lv, edFilter, this);
		edFilter.addTextChangedListener(mtw);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View itemClicked,
					int position, long id) {
				TextView text = (TextView) itemClicked;
				list = new SokWorker().getInfo(text.getText().toString(), cont);
				showDialog(1);
			}
		});
		lv.requestFocus();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setTitle(list[0]);
		adb.setMessage(list[1] + " " + list[2] + "\n" + getString(R.string.subnet) + " - " + list[3]
				+ "\n" + list[4] + "\n" + getString(R.string.AZS) + " " + list[5]);
		adb.setNegativeButton("Cancel", myEmptyClickListener);
		adb.setPositiveButton("Call AZS", myCallListener);
		adb.setCancelable(false);
		return adb.create();
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
			Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ list[5]));
			startActivity(dialIntent);
			removeDialog(1);
		}
	};

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btRefresh:
			Thread tr = new Thread(new Runnable() {
				@Override
				public void run() {
					h.sendEmptyMessage(1);
					lazs = new SokWorker().refreshTable(cont);
					if (lazs != null) {
						tmp = new LinkedList<>();
						for (Azs t : lazs) {
							tmp.add(t.getAdress());
						}
						h.sendEmptyMessage(2);
					} else {
						h.sendEmptyMessage(3);
					}
				}
			});
			tr.start();
			break;
		default:
			break;
		}
	}

}
