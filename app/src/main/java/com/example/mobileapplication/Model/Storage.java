package com.example.mobileapplication.Model;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Storage {
    private Storage() {}
        public static void writeObject(Context context, String key, Object object) throws IOException {
            FileOutputStream out = context.openFileOutput(key, Context.MODE_PRIVATE);
            ObjectOutputStream outputStream= new ObjectOutputStream(out);
            outputStream.writeObject(object);
            outputStream.close();
            out.close();
        }

        public static Object readObject(Context context, String key) throws IOException, ClassNotFoundException {
            FileInputStream in = context.openFileInput(key);
            ObjectInputStream inputStream = new ObjectInputStream(in);
            Object object = inputStream.readObject();
            return object;
        }

}
