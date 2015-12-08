package ua.klo.lp;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class MyTextWacher implements TextWatcher {
    public EditText ed;
    public ListView lv;
    Context context;
    private List<String> tmp = new LinkedList<>();
    ArrayAdapter<String> aay;

    public MyTextWacher(ListView lvi, EditText edt, Context cont) {
        this.ed = edt;
        this.context = cont;
        this.lv = lvi;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (aay != null) {
            aay.clear();
        }
        SqlHelper sqH = new SqlHelper(context);
        SQLiteDatabase db = sqH.getReadableDatabase();
        String adr = ed.getText().toString();
        Cursor cv = db.rawQuery(
                "SELECT adress FROM azsLetsPing WHERE LOWER(adress) LIKE '%" + adr
                        + "%'", null);
        if (cv.moveToFirst()) {
            int tbl = cv.getColumnIndex("adress");
            do {
                tmp.add(cv.getString(tbl));
            } while (cv.moveToNext());

            aay = new ArrayAdapter<>(context,
                    android.R.layout.simple_list_item_1, tmp);
            lv.setAdapter(aay);
        }
        db.close();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

}
