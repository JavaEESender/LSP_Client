package ua.klo.lp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SocketWorker {

    public ArrayList<String> getPing() {
        boolean tst = true;
        String s;
        ArrayList<String> list;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(MainActivity.server,
                    MainActivity.port), 5000);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(
                    socket.getOutputStream());
            out.writeUTF("PING");
            list = new ArrayList<>();
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

    public String[] getAzs(String azs) {
        String[] item = new String[7];
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
            item[6] = in.readUTF();
            socket.close();
            return item;
        } catch (IOException e) {
            return null;
        }
    }

    public ArrayList<String> getInet() {
        boolean tst = true;
        String s;
        ArrayList<String> list;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(MainActivity.server,
                    MainActivity.port), 5000);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(
                    socket.getOutputStream());
            out.writeUTF("getInet");
            list = new ArrayList<>();
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
        String tmp;
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

    public String remInet(String adress) {
        String tmp;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(MainActivity.server,
                    MainActivity.port), 5000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(
                    socket.getOutputStream());
            out.writeUTF("remInet");
            out.writeUTF(adress);
            tmp = in.readUTF();
            socket.close();
            return tmp;
        } catch (IOException e) {
            return null;
        }
    }


    public ArrayList<String> getReject() {
        boolean tst = true;
        String s;
        ArrayList<String> list;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(MainActivity.server,
                    MainActivity.port), 5000);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(
                    socket.getOutputStream());
            out.writeUTF("getReject");
            list = new ArrayList<>();
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

    public String setReject(String adress, String durat) {
        String tmp;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(MainActivity.server,
                    MainActivity.port), 5000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(
                    socket.getOutputStream());
            out.writeUTF("setReject");
            out.writeUTF(adress);
            out.writeUTF(durat);
            tmp = in.readUTF();
            socket.close();
            return tmp;
        } catch (IOException e) {
            return null;
        }
    }

    public String remReject(String adress) {
        String tmp;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(MainActivity.server,
                    MainActivity.port), 5000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(
                    socket.getOutputStream());
            out.writeUTF("remReject");
            out.writeUTF(adress);
            tmp = in.readUTF();
            socket.close();
            return tmp;
        } catch (IOException e) {
            return null;
        }
    }


    public List<Azs> refreshTable(Context context) {
        List<Azs> list = new LinkedList<>();
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

    public String getLogin(String login, String pass) {
        String salt;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(MainActivity.server,
                    MainActivity.port), 5000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(
                    socket.getOutputStream());
            out.writeUTF("getLogin");
            out.writeUTF(login);
            out.writeUTF(pass);
            salt = in.readUTF();
            socket.close();
            return salt;
        } catch (Exception e) {
            return null;
        }
    }

    public String getExpireTime(String ip) {
        String exptime;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(MainActivity.server,
                    MainActivity.port), 5000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(
                    socket.getOutputStream());
            out.writeUTF("getExpireTime");
            out.writeUTF(ip);
            exptime = in.readUTF();
            socket.close();
            return exptime;
        } catch (Exception e) {
            return null;
        }
    }

    public void updateApk(Context context) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(MainActivity.server,
                    MainActivity.port), 5000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(
                    socket.getOutputStream());
            out.writeUTF("updateApk");
            String PATH = Environment.getExternalStorageDirectory() + "/Download/";
            File file = new File(PATH);
            File outputFile = new File(file, "lp.apk");
            if(outputFile.createNewFile()){
                FileOutputStream fos = new FileOutputStream(outputFile);
                byte[] buffer = new byte[1024];
                int count;
                while ((count = in.read(buffer, 0, buffer.length)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
            }
            socket.close();
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(PATH + "lp.apk")), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

        } catch (IOException e) {
            System.out.print("Error");
        }
    }

    public boolean checkUpdate(String version) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(MainActivity.server,
                    MainActivity.port), 5000);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(
                    socket.getOutputStream());
            out.writeUTF("checkUpdate");
            return in.readUTF().equals(version);
        } catch (IOException e) {
            return false;
        }
    }
}
