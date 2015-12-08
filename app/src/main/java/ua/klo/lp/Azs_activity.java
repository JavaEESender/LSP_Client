package ua.klo.lp;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

public class Azs_activity extends Activity implements OnClickListener {

    Context cont;
    Handler h;
    ListView lv;
    List<String> tmp;
    List<Azs> linkedList_AZS = new LinkedList<>();
    ProgressDialog dialog;
    SqlHelper sqS;
    String[] list;
    Toast toast = null;

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
        lv.setAdapter(aad);
        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
                        dialog = new ProgressDialog(cont);
                        dialog.setMessage(getString(R.string.update));
                        dialog.setIndeterminate(true);
                        dialog.setCancelable(true);
                        dialog.show();
                        break;
                    case 2:
                        ArrayAdapter<String> aas = new ArrayAdapter<>(cont,
                                android.R.layout.simple_list_item_1, tmp);

                        lv.setAdapter(aas);
                        dialog.dismiss();
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(getApplicationContext(),
                                getString(R.string.updated), Toast.LENGTH_LONG);
                        toast.show();
                        break;
                    case 3:
                        dialog.dismiss();
                        if (toast != null) {
                            toast.cancel();
                        }
                        toast = Toast.makeText(getApplicationContext(),
                                getString(R.string.connect_fail), Toast.LENGTH_LONG);
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
                list = new SocketWorker().getInfo(text.getText().toString(), cont);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(cont);
                alertDialog.setTitle(list[0]);
                alertDialog.setMessage(list[1] + " " + list[2] + "\n" + getString(R.string.subnet) + " - " + list[3]
                        + "\n" + list[4] + "\n" + getString(R.string.AZS) + " " + list[5]);
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton(getString(R.string.call), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent dialIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + list[5]));
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
        });
        lv.requestFocus();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btRefresh:
                Thread tr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        h.sendEmptyMessage(1);
                        linkedList_AZS = new SocketWorker().refreshTable(cont);
                        if (linkedList_AZS != null) {
                            tmp = new LinkedList<>();
                            for (Azs t : linkedList_AZS) {
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
