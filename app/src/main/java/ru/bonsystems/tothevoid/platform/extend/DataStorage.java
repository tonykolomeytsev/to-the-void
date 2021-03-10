package ru.bonsystems.tothevoid.platform.extend;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import ru.bonsystems.tothevoid.platform.Controller;

/**
 * Created by Kolomeytsev Anton on 30.12.2015.
 */
public class DataStorage {
    private static DataStorage ourInstance;

    public static DataStorage getInstance() {
        if (ourInstance == null) {
            ourInstance = new DataStorage();
        }
        return ourInstance;
    }

    private DataStorage() {
    }

    private HashMap<String, String> data = new HashMap<>();

    public DataStorage set(String key, String value) {
        data.put(key, value);
        return this;
    }

    public String get(String key) {
        return data.get(key);
    }

    public DataStorage reload(){
        File external = Controller.getInstance().getApplicationContext().getExternalFilesDir(null);
        File heap = new File(external + "/heap.json");
        String json = readFromFile(heap);
        if (json == null) return this;
        try {
            JSONObject jsonObject = new JSONObject(json);
            data.clear();
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = (String) jsonObject.get(key);
                data.put(key, value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this;
    }

    private String readFromFile(File file) {
        String string = null;
        try {
            InputStream inputStream = Controller.getInstance().openFileInput(file.getName());
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                string = stringBuilder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return string;
    }

    public DataStorage apply(){
        File external = Controller.getInstance().getApplicationContext().getExternalFilesDir(null);
        File heap = new File(external + "/heap.json");
        FileOutputStream outputStream;

        JSONObject jsonObject = new JSONObject(data);
        try {
            outputStream = Controller.getInstance().openFileOutput(heap.getName(), Context.MODE_PRIVATE);
            outputStream.write(jsonObject.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }
}
