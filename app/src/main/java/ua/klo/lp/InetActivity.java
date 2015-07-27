package ua.klo.lp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import ua.klo.lp.R;

import java.util.ArrayList;
import java.util.List;

public class InetActivity extends Activity implements OnClickListener {
	Spinner spinnerForms;

	ListView lv;
	List<String> list = new ArrayList<>();
	Handler h;
	ProgressBar progress;
	Toast toast = null;
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

		progress = (ProgressBar) findViewById(R.id.progressinet);
		lv = (ListView) findViewById(R.id.lvInet);
		h = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == 1)
					progress.setVisibility(View.VISIBLE);
				if (msg.what == 2)
					progress.setVisibility(View.INVISIBLE);
				if (msg.what == 3) {
					if (list != null) {
						ArrayAdapter<String> aad = new ArrayAdapter<>(
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
			}
		};
		h.sendEmptyMessage(2);
	}

	@Override
	public void onClick(View v) {
		String tmp;
		EditText subEd = (EditText) findViewById(R.id.subNetEdit);
		EditText ipEd = (EditText) findViewById(R.id.IpEdit);
		if ((subEd.getText().length() != 0) && (ipEd.getText().length() != 0)) {
			String ip = "10.0." + subEd.getText().toString() + "."
					+ ipEd.getText().toString();
			String tv = (String) spinnerForms.getSelectedItem();
			tmp = new SokWorker().setInet(ip, tv);
			if (tmp != null) {
				if (toast != null) {
					toast.cancel();
				}
				toast = Toast.makeText(getApplicationContext(), tmp,
						Toast.LENGTH_LONG);
				toast.show();
				this.finish();
			} else {
				if (toast != null) {
					toast.cancel();
				}
				toast = Toast.makeText(getApplicationContext(),
						"Connect FAIL!", Toast.LENGTH_LONG);
				toast.show();
			}
		}else{
			Thread tr = new Thread(new Runnable() {
				@Override
				public void run() {
					h.sendEmptyMessage(1);
					list = new SokWorker().getInet();
					h.sendEmptyMessage(3);
					h.sendEmptyMessage(2);
				}
			});
			tr.start();
		}
	}
}
