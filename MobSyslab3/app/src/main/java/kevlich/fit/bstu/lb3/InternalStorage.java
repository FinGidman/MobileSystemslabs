package kevlich.fit.bstu.lb3;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class InternalStorage {
    private static final String FILE_NAME = "notes.json";


    static void writeToFile(ArrayList<Note> list, Context context){
        Gson gson = new Gson();
        String json = gson.toJson(list);
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(FILE_NAME,Context.MODE_PRIVATE);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
            Toast.makeText(context,"Сохранено", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void deleteFromFile(ArrayList<Note> list, String date, Context context){

        for(int i=0; i<list.size();i++){
            if((list.get(i).date).equals(date)){
                list.remove(i);
            }
        }
        Gson gson = new Gson();
        String json = gson.toJson(list);
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(FILE_NAME,Context.MODE_PRIVATE);
            fileOutputStream.write(json.getBytes());
            fileOutputStream.close();
            Toast.makeText(context,"Успешно удалено", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
