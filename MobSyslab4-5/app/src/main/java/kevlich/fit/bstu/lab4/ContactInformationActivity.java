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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_information);

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
                    //возвращение на начальное активити с перезаписью списка
                }
                else
                {
                    UpdateData();
                    //возвращение на начальное активити с перезаписью списка и переделкой запроса
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
            SQLiteDatabase db = getBaseContext().openOrCreateDatabase("contact.db", MODE_PRIVATE, null);
            db.execSQL("INSERT INTO contacts (name, email, location, phone, profile) VALUES" +
                    "('"+ nametxt.getText().toString() +"',"+
                    "'"+ emailtxt.getText().toString() +"',"+
                    "'"+ locationtxt.getText().toString() +"',"+
                    "'"+ phonetxt.getText().toString() +"',"+
                    "'"+ profiletxt.getText().toString() +"')");
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
            SQLiteDatabase db = getBaseContext().openOrCreateDatabase("contact.db", MODE_PRIVATE, null);
            ContentValues cv = new ContentValues();
            cv.put("name",nametxt.getText().toString());
            cv.put("email",emailtxt.getText().toString());
            cv.put("location",locationtxt.getText().toString());
            cv.put("phone",phonetxt.getText().toString());
            cv.put("profile",profiletxt.getText().toString());
            db.update("contacts",cv,"id="+id,null);
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