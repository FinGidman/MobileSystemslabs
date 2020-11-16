package kevlich.fit.bstu.lb3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.view.View;
import android.widget.*;
import android.widget.CalendarView.OnDateChangeListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME = "notes.json";
    RadioGroup radioGroup;
    Button saveBtn, delBtn;
    EditText noteET;
    CalendarView calendar;
    Gson gson;

    int typeOfStorage = 0;
    String date;
    ArrayList<Note> noteArrayList = new ArrayList<Note>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gson = new Gson();
        readFromFile();

        saveBtn = (Button) findViewById(R.id.SaveBtn);
        delBtn = (Button) findViewById(R.id.DelBtn);
        noteET = (EditText) findViewById(R.id.NoteET);
        calendar = (CalendarView) findViewById(R.id.CalendarView);
        radioGroup = (RadioGroup) findViewById(R.id.RadioGroup);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        date = sdf.format(new Date(calendar.getDate()));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton internalRadioBtn = (RadioButton) findViewById(R.id.InternalRadioBtn);
                if(internalRadioBtn.isChecked())
                    typeOfStorage = 0;
                else
                    typeOfStorage = 1;
            }
        });

        calendar.setOnDateChangeListener(new OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                date = new StringBuilder()
                        .append(day).append("/")
                        .append(month + 1).append("/")
                        .append(year).toString();
                //noteET.setText(date);
                showData(date);
            }
        });
    }

    public void onClick(View view){
        readFromFile();
        switch(view.getId()){
            case R.id.SaveBtn:
                if(typeOfStorage == 0) {
                    if(isExist(date)) {
                        InternalStorage.writeToFile(noteArrayList, this);
                        readFromFile();
                    }
                    else {
                        noteArrayList.add(new Note(date, noteET.getText().toString()));
                        InternalStorage.writeToFile(noteArrayList, this);
                        readFromFile();
                    }
                    break;
                }
                else if(typeOfStorage == 1) {
                    ExternalStorage.writeToFile(noteArrayList, this);
                    break;
                }
            case R.id.DelBtn:
                if(typeOfStorage == 0) {
                    InternalStorage.deleteFromFile(noteArrayList, date, this);
                    break;
                }
                else if(typeOfStorage == 1){
                    ExternalStorage.deleteFromFile(noteArrayList, date, this);
                    break;
                }
        }
    }

    public void readFromFile(){
        if(typeOfStorage == 0){
            try {
                FileInputStream fileInputStream = openFileInput(FILE_NAME);
                InputStreamReader reader = new InputStreamReader(fileInputStream);
                //BufferedReader buffer = new BufferedReader(reader);
                noteArrayList.clear();
                noteArrayList = gson.fromJson(reader, new TypeToken<ArrayList<Note>>() {}.getType());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(typeOfStorage == 1){
            Gson gson = new Gson();
            File file = getExternalPath();
            noteArrayList.clear();
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                InputStreamReader reader = new InputStreamReader(fileInputStream);
                noteArrayList = gson.fromJson(reader, new TypeToken<ArrayList<Note>>() {}.getType());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void showData(String date){
        for(int i = 0; i < noteArrayList.size(); i++){
            if((noteArrayList.get(i).date).equals(date)){
                noteET.setText(noteArrayList.get(i).note);
                break;
            }
            else{
                noteET.setText("");
            }
        }
    }

    public boolean isExist(String date){
        for(int i = 0; i < noteArrayList.size(); i++){
            if((noteArrayList.get(i).date).equals(date)){
                noteArrayList.remove(i);
                noteArrayList.add(new Note(date, noteET.getText().toString()));
                return  true;
            }
        }
        return false;
    }

    private static File getExternalPath() {
        return (new File(Environment.getExternalStorageDirectory(), FILE_NAME));
    }
}
 