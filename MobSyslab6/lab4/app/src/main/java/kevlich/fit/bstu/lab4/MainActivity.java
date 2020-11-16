package kevlich.fit.bstu.lab4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.view.menu.*;
import androidx.core.view.MenuItemCompat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.os.Bundle;
import android.database.sqlite.*;
import android.util.SparseBooleanArray;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.view.Menu;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.app.ActionBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.net.Proxy.Type.HTTP;

public class MainActivity extends AppCompatActivity implements customdialog.CustomDialogListener {

    Button addbtn, delbtn, cancelbtn;
    ListView contacts;
    ArrayList<Contacts> ContactsArray = new ArrayList<Contacts>();;
    ListVIewItemAdapter adapter;
    int choosemode = 0, sort = 0, applydelete;
    private FirebaseAuth mAuth;
    private String userId;

    private DatabaseReference mydb;
    final FirebaseAuth auth = FirebaseAuth.getInstance();
    private String CONTACT_KEY = auth.getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mydb = FirebaseDatabase.getInstance().getReference(CONTACT_KEY);


        //DBcontext();
        FillListView(0);



        addbtn = (Button) findViewById(R.id.add);
        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenSecondActivity();
                //OpenUri();
                //OpenLocation();
                //CallPerson();
                //penEmail();
            }
        });

        delbtn = (Button) findViewById(R.id.delete);
        delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customdialog customdialog = new customdialog();
                customdialog.show(getSupportFragmentManager(), "custom dialog");

            }
        });

        cancelbtn = (Button) findViewById(R.id.cancel);
        cancelbtn.setVisibility(cancelbtn.INVISIBLE);
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosemode = 0;
                int count = contacts.getCount();
                for(int i=0; i<count;i++) {
                contacts.setItemChecked(i, false);
                    //contacts.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.white));
                    CheckBox ch = contacts.getChildAt(i).findViewById(R.id.checkItem);
                    ch.setChecked(false);
                    ch.setVisibility(ch.INVISIBLE);
                }
                cancelbtn.setVisibility(cancelbtn.INVISIBLE);
            }
        });

        contacts = (ListView) findViewById(R.id.ListView);
        registerForContextMenu(contacts);
        contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if(choosemode == 0) {
                    OpenItem(position);
                }
                else{
                    CheckBox ch = contacts.getChildAt(position).findViewById(R.id.checkItem);
                    ch.setChecked(true);
                }
            }
        });



//        contacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
//
//                choosemode = 1;
//                cancelbtn.setVisibility(cancelbtn.VISIBLE);
//                CheckBox ch = contacts.getChildAt(position).findViewById(R.id.checkItem);
//                ch.setChecked(true);
//                int count = contacts.getCount();
//                for(int i=0; i<count;i++) { //вывод на экран чекбоксов
//                    //contacts.setItemChecked(i, false);
//                    //contacts.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.white));
//                    ch = contacts.getChildAt(i).findViewById(R.id.checkItem);
//                    ch.setVisibility(ch.VISIBLE);
//                }
//                contacts.getChildAt(position).findViewById(R.id.checkItem).setActivated(true);
//                return true;
//            }
//        });

    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu, menu);
        //menu.add(Menu.NONE, 101, Menu.NONE, "Копировать");
        //menu.add(Menu.NONE, 102, Menu.NONE, "Удалить");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int pos = info.position;
        switch (item.getItemId()){
            case R.id.copy_option:
                //Toast.makeText(this, "copy", Toast.LENGTH_SHORT).show();
                CopyItem(pos);
                return  true;
            case R.id.delete_option:
                //Toast.makeText(this, "del", Toast.LENGTH_SHORT).show();
                //AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
                //pos = info.position;
                //pos = contacts.getCheckedItemPosition();
                CheckBox ch = contacts.getChildAt(pos).findViewById(R.id.checkItem);
                ch.setChecked(true);
                customdialog customdialog = new customdialog();
                customdialog.show(getSupportFragmentManager(), "custom dialog");
                //DeleteContacts();
                return  true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void apply(int res){
        if(res == 1){
            DeleteContacts();
        }
        else{

            for(int i=0; i<ContactsArray.size();i++)
            {
                CheckBox ch = contacts.getChildAt(i).findViewById(R.id.checkItem);
                ch.setChecked(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //MenuInflater inflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == R.id.search){
            Toast.makeText(getApplicationContext(), "Search",Toast.LENGTH_SHORT).show();
        }
        else if(id  == R.id.sortAZ){
            //Toast.makeText(getApplicationContext(), "sort",Toast.LENGTH_SHORT).show();
            if(sort == 0){
                sort = 1;
                FillListView(sort);
            }
            else{
                sort = 0;
                FillListView(sort);
            }
        }
        return true;
    }


    public void OpenItem(int id){
        try {
            Intent intent = new Intent(MainActivity.this, ContactInformationActivity.class);
            intent.putExtra("id", ContactsArray.get(id).id);
            intent.putExtra("name", ContactsArray.get(id).name);
            intent.putExtra("email", ContactsArray.get(id).email);
            intent.putExtra("location", ContactsArray.get(id).location);
            intent.putExtra("phone", ContactsArray.get(id).phone);
            intent.putExtra("profile", ContactsArray.get(id).profile);
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public void OpenSecondActivity(){
        Intent intent = new Intent(this, ContactInformationActivity.class);
        startActivity(intent);
    }

    public void DBcontext()
    {
        //SQLite
//        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("contact.db", MODE_PRIVATE, null);
//        db.execSQL("CREATE TABLE IF NOT EXISTS contacts ("+
//                "id integer primary key autoincrement," +
//                "name varchar(50),"+
//                "email varchar(100),"+
//                "location varchar(100),"+
//                "phone varchar(50),"+
//                "profile varchar(100)"+
//                ");");

        //firebase

    }

    public void FillListView(int sort)
    {
        //SQLite
//        SQLiteDatabase db = getBaseContext().openOrCreateDatabase("contact.db", MODE_PRIVATE, null);
          ContactsArray.clear();
//        Cursor getAllData = db.rawQuery("SELECT * FROM contacts;",null);
          contacts = (ListView) findViewById(R.id.ListView);
//        if(getAllData.moveToFirst()){
//            do{
//                ContactsArray.add(new Contacts(getAllData.getInt(0),
//                        getAllData.getString(1),
//                        getAllData.getString(2),
//                        getAllData.getString(3),
//                        getAllData.getString(4),
//                        getAllData.getString(5)));
//
//            }while(getAllData.moveToNext());
//        }
//        getAllData.close();
//        db.close();
        //firebase
        try {


            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Contacts contact = ds.getValue(Contacts.class);
                        assert contact != null;
                        ContactsArray.add(contact);
                    }
                    adapter = new ListVIewItemAdapter(MainActivity.this, ContactsArray);
                    contacts.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            mydb.addValueEventListener(valueEventListener);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

//        if(sort == 0) {
//            adapter = new ListVIewItemAdapter(MainActivity.this, ContactsArray);
//            contacts.setAdapter(adapter);
//        }
//        else{
//            Collections.sort(ContactsArray, new Comparator<Contacts>() {
//                @Override
//                public int compare(Contacts t1, Contacts t2) {
//                    return t1.name.compareTo(t2.name);
//                }
//            });
//            adapter = new ListVIewItemAdapter(MainActivity.this, ContactsArray);
//            contacts.setAdapter(adapter);
//        }
    }

    public void DeleteContacts() {
        try {
            SQLiteDatabase db = getBaseContext().openOrCreateDatabase("contact.db", MODE_PRIVATE, null);
            //SparseBooleanArray sparseBooleanArray = contacts.getCheckedItemPositions();
            int itemCount = contacts.getCount();
            for (int i = 0; i < itemCount; i++) {
                CheckBox ch = contacts.getChildAt(i).findViewById(R.id.checkItem);
                if (ch.isChecked()) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                    Query query = ref.child(auth.getCurrentUser().getUid()).orderByChild("id").equalTo(ContactsArray.get(i).id);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for(DataSnapshot snapshot1 : snapshot.getChildren()){
                                snapshot1.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    //db.delete("contacts", "id=?", new String[]{String.valueOf(ContactsArray.get(i).id)});
                }
            }

            Toast.makeText(getApplicationContext(),"Успешно удалено", Toast.LENGTH_LONG).show();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Удаление не удалось", Toast.LENGTH_LONG).show();
        }


    }

    public void CopyItem(int pos){
        try {
            SQLiteDatabase db = getBaseContext().openOrCreateDatabase("contact.db", MODE_PRIVATE, null);
            db.execSQL("INSERT INTO contacts (name, email, location, phone, profile) VALUES" +
                    "('" + ContactsArray.get(pos).name + "'," +
                    "'" + ContactsArray.get(pos).email + "'," +
                    "'" + ContactsArray.get(pos).location + "'," +
                    "'" + ContactsArray.get(pos).phone + "'," +
                    "'" + ContactsArray.get(pos).profile + "')");
            FillListView(0);
            Toast.makeText(getApplicationContext(), "Копирование прошло успешно", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Ошибка копирования", Toast.LENGTH_LONG).show();
        }
    }
}