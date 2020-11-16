package kevlich.fit.bstu.lab4;

import androidx.annotation.NonNull;
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

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ContactInformationActivity extends AppCompatActivity {

    Button savebtn;
    ImageButton emailbtn, locationbtn, phonebtn, profilebtn;
    EditText emailtxt, locationtxt, phonetxt, profiletxt, nametxt;
    String id;

    private DatabaseReference mydb;
    final FirebaseAuth auth = FirebaseAuth.getInstance();
//#тут надо добавить ключ юзера
    private String CONTACT_KEY = auth.getCurrentUser().getUid();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_information);

        nametxt = (EditText) findViewById(R.id.name);
        emailtxt = (EditText) findViewById(R.id.email);
        locationtxt = (EditText) findViewById(R.id.location);
        phonetxt = (EditText) findViewById(R.id.phone);
        profiletxt = (EditText) findViewById(R.id.profile);
        mydb = FirebaseDatabase.getInstance().getReference(CONTACT_KEY);


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
        //id = Integer.parseInt(arguments.get("id")
        id = arguments.get("id").toString();
        nametxt.setText(arguments.get("name").toString());
        emailtxt.setText(arguments.get("email").toString());
        locationtxt.setText(arguments.get("location").toString());
        phonetxt.setText(arguments.get("phone").toString());
        profiletxt.setText(arguments.get("profile").toString());
    }

    public void AddDataToTable(){
        try{
            DatabaseReference ref = mydb.push();
            String id = ref.getKey();
            String name = nametxt.getText().toString();
            String email = emailtxt.getText().toString();
            String location = locationtxt.getText().toString();
            String phone = phonetxt.getText().toString();
            String profile = profiletxt.getText().toString();
            Contacts contact = new Contacts(id, name, email, location, phone, profile);
            mydb.push().setValue(contact);

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
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            Query query = ref.child(auth.getCurrentUser().getUid()).orderByChild("id").equalTo(id);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot snapshot1 : snapshot.getChildren()){
                        snapshot1.child("name").getRef().setValue(nametxt.getText().toString());
                        snapshot1.child("email").getRef().setValue(emailtxt.getText().toString());
                        snapshot1.child("location").getRef().setValue(locationtxt.getText().toString());
                        snapshot1.child("phone").getRef().setValue(phonetxt.getText().toString());
                        snapshot1.child("profile").getRef().setValue(profiletxt.getText().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


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