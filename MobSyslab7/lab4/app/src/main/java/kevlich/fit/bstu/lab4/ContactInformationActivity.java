package kevlich.fit.bstu.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.sqlite.*;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class ContactInformationActivity extends AppCompatActivity {

    Button savebtn;
    ImageButton emailbtn, locationbtn, phonebtn, profilebtn;
    EditText emailtxt, locationtxt, phonetxt, profiletxt, nametxt;
    int id;

    ContactsDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_information);

        //Экземпляр
        dbHelper = new ContactsDatabaseHelper(this);

        nametxt = (EditText) findViewById(R.id.name);
        emailtxt = (EditText) findViewById(R.id.email);
        locationtxt = (EditText) findViewById(R.id.location);
        phonetxt = (EditText) findViewById(R.id.phone);
        profiletxt = (EditText) findViewById(R.id.profile);



        final Bundle arguments = getIntent().getExtras();
        if(arguments!= null) {
            SetDataFromIntent();
        }

        savebtn = (Button) findViewById(R.id.savebtn);
        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(arguments == null) {
                    AddDataToTable();
                }
                else
                {
                    UpdateData();
                }
            }
        });

        emailbtn = (ImageButton) findViewById(R.id.goToEmail); //email
        emailbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri mail = Uri.parse(emailtxt.getText().toString());
                Intent sendMail = new Intent(Intent.ACTION_SENDTO);
                sendMail.setType("text/plain");
                sendMail.putExtra(Intent.EXTRA_EMAIL, mail);
                startActivity(sendMail);
            }
        });
        locationbtn = (ImageButton) findViewById(R.id.GoToLocation); //location
        locationbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri location = Uri.parse(locationtxt.getText().toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
                startActivity(mapIntent);
            }
        });
        phonebtn = (ImageButton) findViewById(R.id.GoToPhone); //phonr
        phonebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Uri number = Uri.parse(phonetxt.getText().toString());
                String number = phonetxt.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel",number, null));
                startActivity(callIntent);
            }
        });
        profilebtn = (ImageButton) findViewById(R.id.GoToProfile); //
        profilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri webpage = Uri.parse(profiletxt.getText().toString());
                Intent WebIntent =  new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(WebIntent);
            }
        });
    }

    public void SetDataFromIntent(){
        nametxt = (EditText) findViewById(R.id.name);
        emailtxt = (EditText) findViewById(R.id.email);
        locationtxt = (EditText) findViewById(R.id.location);
        phonetxt = (EditText) findViewById(R.id.phone);
        profiletxt = (EditText) findViewById(R.id.profile);


        Bundle arguments = getIntent().getExtras();
        id = Integer.parseInt(arguments.get("id").toString());
        nametxt.setText(arguments.get("name").toString());
        emailtxt.setText(arguments.get("email").toString());
        locationtxt.setText(arguments.get("location").toString());
        phonetxt.setText(arguments.get("phone").toString());
        profiletxt.setText(arguments.get("profile").toString());
    }

    public void AddDataToTable(){
        try{
            SQLiteDatabase database = dbHelper.getWritableDatabase();

            ContentValues contentValues = new ContentValues();
            contentValues.put(dbHelper.KEY_NAME, nametxt.getText().toString());
            contentValues.put(dbHelper.KEY_EMAIL, emailtxt.getText().toString());
            contentValues.put(dbHelper.KEY_LOCATION, locationtxt.getText().toString());
            contentValues.put(dbHelper.KEY_PHONE, phonetxt.getText().toString());
            contentValues.put(dbHelper.KEY_PROFILE, profiletxt.getText().toString());
            contentValues.put(dbHelper.KEY_FAVORITE, "0");

            database.insert(dbHelper.TABLE_CONTACTS, null, contentValues);

            Toast.makeText(getApplicationContext(),"Запись добавлена успешно", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        catch (Exception E){
            Toast.makeText(getApplicationContext(),"Не удалось добавить запись", Toast.LENGTH_LONG).show();
        }
    }

    public void UpdateData(){
        try{
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(dbHelper.KEY_NAME,nametxt.getText().toString());
            cv.put(dbHelper.KEY_EMAIL,emailtxt.getText().toString());
            cv.put(dbHelper.KEY_LOCATION,locationtxt.getText().toString());
            cv.put(dbHelper.KEY_PHONE,phonetxt.getText().toString());
            cv.put(dbHelper.KEY_PROFILE,profiletxt.getText().toString());
            db.update(dbHelper.TABLE_CONTACTS,cv,"id="+id,null);

            Toast.makeText(getApplicationContext(),"Запись успешно изменена", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        catch (Exception E){
            Toast.makeText(getApplicationContext(),"Не удалось изменить запись", Toast.LENGTH_LONG).show();
        }
    }
}