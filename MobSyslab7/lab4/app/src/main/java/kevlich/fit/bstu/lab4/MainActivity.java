package kevlich.fit.bstu.lab4;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.view.menu.*;
import androidx.core.view.MenuItemCompat;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
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
    int choosemode = 0, sort = 0, fav = 0;
    ContactsDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbHelper = new ContactsDatabaseHelper(this);

        FillListView(0,0);



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
                CopyItem(pos);
                return  true;
            case R.id.delete_option:

                CheckBox ch = contacts.getChildAt(pos).findViewById(R.id.checkItem);
                ch.setChecked(true);
                customdialog customdialog = new customdialog();
                customdialog.show(getSupportFragmentManager(), "custom dialog");
                return true;
            case R.id.favorites_option:
                //CheckBox ch1 = contacts.getChildAt(pos).findViewById(R.id.checkItem);
                //ch1.setChecked(true);
                String s = ContactsArray.get(pos).favorite;
                if(s.equals("0")) {
                    addTofavorites(ContactsArray.get(pos).id);
                }
                else if(s.equals("1")){
                    delFromFavorites(ContactsArray.get(pos).id);
                }
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
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                searchByName(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (item.getItemId()){
            case R.id.search:
                Toast.makeText(getApplicationContext(), "Search",Toast.LENGTH_SHORT).show();
                break;
            case R.id.sortAZ:
                if(sort == 0){
                    sort = 1;
                    sortbyAZ();
                    //FillListView(sort,0);
                }
                else{
                    sort = 0;
                    FillListView(sort, 0);
                }
                break;
            case R.id.favorites:
                if(fav == 0) {
                    fav = 1;
                    FillListView(0, fav);
                }
                else {
                    fav = 0;
                    FillListView(0, fav);
                }
                break;
        }
        return true;
    }


    public void OpenItem(int id){
        Intent intent = new Intent(this, ContactInformationActivity.class);
        intent.putExtra(dbHelper.KEY_ID,ContactsArray.get(id).id);
        intent.putExtra(dbHelper.KEY_NAME,ContactsArray.get(id).name);
        intent.putExtra(dbHelper.KEY_EMAIL,ContactsArray.get(id).email);
        intent.putExtra(dbHelper.KEY_LOCATION,ContactsArray.get(id).location);
        intent.putExtra(dbHelper.KEY_PHONE,ContactsArray.get(id).phone);
        intent.putExtra(dbHelper.KEY_PROFILE,ContactsArray.get(id).profile);
        intent.putExtra(dbHelper.KEY_LOCATION,ContactsArray.get(id).favorite);
        startActivity(intent);
    }

    public void OpenSecondActivity(){
        Intent intent = new Intent(this, ContactInformationActivity.class);
        startActivity(intent);
    }

    public void FillListView(int sort, int showfavorites)
    {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        if(showfavorites == 0) {
            Cursor cursor = database.query(dbHelper.TABLE_CONTACTS, null, null, null, null, null, null);

            ContactsArray.clear();
            contacts = (ListView) findViewById(R.id.ListView);
            try {
                if (cursor.moveToFirst()) {
                    do {
                        ContactsArray.add(new Contacts(cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_ID)),
                                cursor.getString(cursor.getColumnIndex(dbHelper.KEY_NAME)),
                                cursor.getString(cursor.getColumnIndex(dbHelper.KEY_EMAIL)),
                                cursor.getString(cursor.getColumnIndex(dbHelper.KEY_LOCATION)),
                                cursor.getString(cursor.getColumnIndex(dbHelper.KEY_PHONE)),
                                cursor.getString(cursor.getColumnIndex(dbHelper.KEY_PROFILE)),
                                cursor.getString(cursor.getColumnIndex(dbHelper.KEY_FAVORITE))));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            } catch (Exception e) {

            }
        }
        else if(showfavorites == 1){
            Cursor cursor = database.query(dbHelper.TABLE_CONTACTS, null, "favorite = '1' ", null, null, null, null);

            ContactsArray.clear();
            contacts = (ListView) findViewById(R.id.ListView);
            try {
                if (cursor.moveToFirst()) {
                    do {
                        ContactsArray.add(new Contacts(cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_ID)),
                                cursor.getString(cursor.getColumnIndex(dbHelper.KEY_NAME)),
                                cursor.getString(cursor.getColumnIndex(dbHelper.KEY_EMAIL)),
                                cursor.getString(cursor.getColumnIndex(dbHelper.KEY_LOCATION)),
                                cursor.getString(cursor.getColumnIndex(dbHelper.KEY_PHONE)),
                                cursor.getString(cursor.getColumnIndex(dbHelper.KEY_PROFILE)),
                                cursor.getString(cursor.getColumnIndex(dbHelper.KEY_FAVORITE))));
                    } while (cursor.moveToNext());
                }
                cursor.close();
            } catch (Exception e) {

            }
        }
        if(sort == 0) {
            adapter = new ListVIewItemAdapter(this, ContactsArray);
            contacts.setAdapter(adapter);
        }
        else{
            Collections.sort(ContactsArray, new Comparator<Contacts>() {
                @Override
                public int compare(Contacts t1, Contacts t2) {
                    return t1.name.compareTo(t2.name);
                }
            });
            adapter = new ListVIewItemAdapter(this, ContactsArray);
            contacts.setAdapter(adapter);
        }
    }

    public void DeleteContacts() {
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            int itemCount = contacts.getCount();
            for (int i = 0; i < itemCount; i++) {
                CheckBox ch = contacts.getChildAt(i).findViewById(R.id.checkItem);
                if (ch.isChecked()) {
                    db.delete("contacts", "id=?", new String[]{String.valueOf(ContactsArray.get(i).id)});
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
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(dbHelper.KEY_NAME, ContactsArray.get(pos).name);
            contentValues.put(dbHelper.KEY_EMAIL, ContactsArray.get(pos).email);
            contentValues.put(dbHelper.KEY_LOCATION, ContactsArray.get(pos).location);
            contentValues.put(dbHelper.KEY_PHONE, ContactsArray.get(pos).phone);
            contentValues.put(dbHelper.KEY_PROFILE, ContactsArray.get(pos).profile);
            contentValues.put(dbHelper.KEY_FAVORITE, ContactsArray.get(pos).favorite);
            db.insert(dbHelper.TABLE_CONTACTS, null, contentValues);
            FillListView(0,0);
            Toast.makeText(getApplicationContext(), "Копирование прошло успешно", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Ошибка копирования", Toast.LENGTH_LONG).show();
        }
    }

    //7
    public void addTofavorites(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.KEY_FAVORITE,"1");
        db.update(dbHelper.TABLE_CONTACTS,contentValues,"id="+id, null);
    }

    public void delFromFavorites(int id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(dbHelper.KEY_FAVORITE,"0");
        db.update(dbHelper.TABLE_CONTACTS,contentValues,"id="+id, null);
        FillListView(0,1);
    }

    public void sortbyAZ(){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(dbHelper.TABLE_CONTACTS, null, null, null, null, null, dbHelper.KEY_NAME + " ASC");
        ContactsArray.clear();
        contacts = (ListView) findViewById(R.id.ListView);
        try {
            if (cursor.moveToFirst()) {
                do {
                    ContactsArray.add(new Contacts(cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_ID)),
                            cursor.getString(cursor.getColumnIndex(dbHelper.KEY_NAME)),
                            cursor.getString(cursor.getColumnIndex(dbHelper.KEY_EMAIL)),
                            cursor.getString(cursor.getColumnIndex(dbHelper.KEY_LOCATION)),
                            cursor.getString(cursor.getColumnIndex(dbHelper.KEY_PHONE)),
                            cursor.getString(cursor.getColumnIndex(dbHelper.KEY_PROFILE)),
                            cursor.getString(cursor.getColumnIndex(dbHelper.KEY_FAVORITE))));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {

        }
        adapter = new ListVIewItemAdapter(this, ContactsArray);
        contacts.setAdapter(adapter);
    }

    public void searchByName(String name){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(dbHelper.TABLE_CONTACTS, null, "UPPER(name) like '%" + name + "%'", null, null, null, null);
        ContactsArray.clear();
        contacts = (ListView) findViewById(R.id.ListView);
        try {
            if (cursor.moveToFirst()) {
                do {
                    ContactsArray.add(new Contacts(cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_ID)),
                            cursor.getString(cursor.getColumnIndex(dbHelper.KEY_NAME)),
                            cursor.getString(cursor.getColumnIndex(dbHelper.KEY_EMAIL)),
                            cursor.getString(cursor.getColumnIndex(dbHelper.KEY_LOCATION)),
                            cursor.getString(cursor.getColumnIndex(dbHelper.KEY_PHONE)),
                            cursor.getString(cursor.getColumnIndex(dbHelper.KEY_PROFILE)),
                            cursor.getString(cursor.getColumnIndex(dbHelper.KEY_FAVORITE))));
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {

        }
        adapter = new ListVIewItemAdapter(this, ContactsArray);
        contacts.setAdapter(adapter);
    }
}