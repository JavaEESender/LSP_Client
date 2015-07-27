package ua.klo.lp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import ua.klo.lp.R;

@SuppressLint("InflateParams")
public class ConfActivity extends Activity implements OnClickListener {
	SharedPreferences spref;
	private String server = "SERVER_GW", port = "PORT_GW";
	EditText dialEdit;
	int dlg;
	TextView sedit;
	TextView pedit;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conf);
		findViewById(R.id.ServView).setOnClickListener(this);
		findViewById(R.id.PortView).setOnClickListener(this);
		sedit = (TextView) findViewById(R.id.ServView);
		pedit = (TextView) findViewById(R.id.PortView);
		spref = getSharedPreferences("MainActivity", MODE_PRIVATE);
		String savedText = spref.getString(server, "");
		sedit.setText("Server (" + savedText + ")");
		savedText = spref.getString(port, "");
		pedit.setText("Port (" + savedText + ")");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ServView:
			dlg = 1;
			showDialog(1);
			break;
		case R.id.PortView:
			dlg = 2;
			showDialog(1);
			break;
		default:
			break;
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		LinearLayout view = (LinearLayout) getLayoutInflater().inflate(
				R.layout.dialogs, null);
		switch (dlg) {
		case 1:
			adb.setTitle("Server dialog");
			adb.setView(view);
			adb.setPositiveButton("Ok", myDialogClickListener);
			dialEdit = (EditText) view.findViewById(R.id.dialEdit);
			String savedText = spref.getString(server, "");
			dialEdit.setText(savedText);
			break;
		case 2:
			adb.setTitle("Port dialog");
			adb.setView(view);
			adb.setPositiveButton("Ok", myDialogClickListener);
			dialEdit = (EditText) view.findViewById(R.id.dialEdit);
			String savedText1 = spref.getString(port, "");
			dialEdit.setText(savedText1);
			break;
		}
		adb.setCancelable(false);

		return adb.create();
	}

	android.content.DialogInterface.OnClickListener myDialogClickListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Editor ed = spref.edit();
			switch (dlg) {
			case 1:
				ed.putString(server, dialEdit.getText().toString());
				ed.commit();
				sedit.setText("Server (" + dialEdit.getText().toString() + ")");
				MainActivity.server = dialEdit.getText().toString();
				removeDialog(1);
				break;
			case 2:
				ed.putString(port, dialEdit.getText().toString());
				ed.commit();
				pedit.setText("Port (" + dialEdit.getText().toString() + ")");
				MainActivity.port = Integer.parseInt(dialEdit.getText()
						.toString());
				removeDialog(1);
				break;
			default:
				break;
			}
		}
	};

}
