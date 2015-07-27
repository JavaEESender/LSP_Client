package ua.klo.lp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SokWorker {

	public ArrayList<String> getPing() {
		boolean tst = true;
		String s = null;
		ArrayList<String> list = null;
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(MainActivity.server,
					MainActivity.port), 5000);

			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
			out.writeUTF("PING");
			list = new ArrayList<String>();
			while (tst) {
				s = in.readUTF();
				if (s.equals("END")) {
					tst = false;
				} else {
					list.add(s);
				}
			}
			socket.close();
			return list;
		} catch (IOException e) {
			return null;
		}
	}

	public String RestartVPN(String azs) {
		String tmp = null;
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(MainActivity.server,
					MainActivity.port), 5000);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
			out.writeUTF("VPN");
			out.writeUTF(azs);
			tmp = in.readUTF();
			socket.close();
			return tmp;
		} catch (IOException e) {
			return null;
		}
	}

	public String[] getAzs(String azs) {
		String[] item = new String[6];
		item[0] = azs;
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(MainActivity.server,
					MainActivity.port), 5000);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
			out.writeUTF("AZS");
			out.writeUTF(item[0]);
			item[1] = in.readUTF();
			item[2] = in.readUTF();
			item[3] = in.readUTF();
			item[4] = in.readUTF();
			item[5] = in.readUTF();
			socket.close();
			return item;
		} catch (IOException e) {
			return null;
		}
	}

	public ArrayList<String> getInet() {
		boolean tst = true;
		String s = null;
		ArrayList<String> list = null;
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(MainActivity.server,
					MainActivity.port), 5000);

			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
			out.writeUTF("getInet");
			list = new ArrayList<String>();
			while (tst) {
				s = in.readUTF();
				if (s.equals("END")) {
					tst = false;
				} else {
					list.add(s);
				}
			}
			socket.close();
			return list;
		} catch (IOException e) {
			return null;
		}
	}

	public String setInet(String adress, String durat) {
		String tmp = null;
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(MainActivity.server,
					MainActivity.port), 5000);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
			out.writeUTF("setInet");
			out.writeUTF(adress);
			out.writeUTF(durat);
			tmp = in.readUTF();
			socket.close();
			return tmp;
		} catch (IOException e) {
			return null;
		}
	}

	public List<Azs> refreshTable(Context context) {
		List<Azs> list = new LinkedList<Azs>();
		boolean notEnd = true;
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(MainActivity.server,
					MainActivity.port), 5000);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
			out.writeUTF("Sync");
			while (notEnd) {
				Azs tmp = new Azs();
				if (in.readUTF().equals("END")) {
					notEnd = false;
				} else {
					tmp.setAdress(in.readUTF());
					tmp.setProv(in.readUTF());
					tmp.setTelProv(in.readUTF());
					tmp.setSubnet(in.readUTF());
					tmp.setAdmin(in.readUTF());
					tmp.setTelAzs(in.readUTF());
					list.add(tmp);
				}
			}
			ContentValues cv = new ContentValues();
			SqlHelper sqH = new SqlHelper(context);
			SQLiteDatabase db = sqH.getWritableDatabase();
			db.delete("azsLetsPing", null, null);
			for (Azs t : list) {
				cv.put("adress", t.getAdress());
				cv.put("prov", t.getProv());
				cv.put("telProv", t.getTelProv());
				cv.put("subnet", t.getSubnet());
				cv.put("admin", t.getAdmin());
				cv.put("telAzs", t.getTelAzs());
				db.insert("azsLetsPing", null, cv);
			}
			db.close();
			socket.close();
			return list;
		} catch (IOException e) {
			return null;
		}
	}

	public String[] getInfo(String adress, Context cont) {
		String[] list = new String[6];
		SqlHelper sqH = new SqlHelper(cont);
		SQLiteDatabase db = sqH.getReadableDatabase();
		Cursor cv = db.query("azsLetsPing", null, "adress = '" + adress + "'",
				null, null, null, null);
		if (cv.moveToFirst()) {
			int adres = cv.getColumnIndex("adress");
			int prov = cv.getColumnIndex("prov");
			int telprov = cv.getColumnIndex("telProv");
			int subnet = cv.getColumnIndex("subnet");
			int admin = cv.getColumnIndex("admin");
			int telazs = cv.getColumnIndex("telAzs");
			list[0] = cv.getString(adres);
			list[1] = cv.getString(prov);
			list[2] = cv.getString(telprov);
			list[3] = cv.getString(subnet);
			list[4] = cv.getString(admin);
			list[5] = cv.getString(telazs);
		}
		return list;
	}
}
