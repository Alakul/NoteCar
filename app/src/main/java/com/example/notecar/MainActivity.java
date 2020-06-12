package com.example.notecar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.widget.ListView;
import android.widget.TextView;
import android.view.View;
import android.app.AlertDialog;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

public class MainActivity extends AppCompatActivity{

    //private static final String ORDER_BY_DATA = "orderByData";

    private TextView displayDate;
    private DatabaseHelper databaseHelper;
    private ListView listView;
    private AdapterData adapterData;
    private ArrayList<DataTable> arrayList;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //preferences = getSharedPreferences("preferences", Activity.MODE_PRIVATE);

        listView=findViewById(R.id.listView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                arrayList= databaseHelper.getAllData(displayDate.getText().toString());
                DataTable dataTable=arrayList.get(position);

                int id1=dataTable.getId();
                String date=dataTable.getDate();
                String time=dataTable.getTime();
                String person=dataTable.getPerson();
                String place=dataTable.getPlace();

                Intent intent = new Intent(getBaseContext(), Edit.class);
                intent.putExtra("ID", id1);
                intent.putExtra("DATE", date);
                intent.putExtra("TIME", time);
                intent.putExtra("PERSON", person);
                intent.putExtra("PLACE", place);
                startActivity(intent);

                return false;
            }
        });

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new ListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int checkedCount = listView.getCheckedItemCount();
                mode.setTitle("Zaznaczono: "+ checkedCount);

                arrayList= databaseHelper.getAllData(displayDate.getText().toString());
                DataTable dataTable=arrayList.get(position);
                int idAdd=dataTable.getId();

                if (checked) {
                    adapterData.itemsSelected.add(idAdd); }
                else {
                    adapterData.itemsSelected.remove(id);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_selection, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuDel:
                        ArrayList<Integer> selectedItemPositions = adapterData.itemsSelected;
                        int deleted = 0;
                        int sumDel = selectedItemPositions.size();
                        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                            databaseHelper.deleteData(selectedItemPositions.get(i));
                            deleted++;
                        }
                        onResume();
                        mode.finish();

                        Toast.makeText(getApplicationContext(), "Usunięto "+deleted+" z "+ sumDel, Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapterData.itemsSelected.clear();
            }
        });

        SwipeMenuListView listView= findViewById(R.id.listView);
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.parseColor("#2196F3")));
                // set item width
                openItem.setWidth(190);
                // set item title
                openItem.setTitle("Edytuj");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);
            }
        };
        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (index == 0) {
                    arrayList= databaseHelper.getAllData(displayDate.getText().toString());
                    DataTable dataTable=arrayList.get(position);

                    int id=dataTable.getId();
                    String date=dataTable.getDate();
                    String time=dataTable.getTime();
                    String person=dataTable.getPerson();
                    String place=dataTable.getPlace();

                    Intent intent = new Intent(getBaseContext(), Edit.class);
                    intent.putExtra("ID", id);
                    intent.putExtra("DATE", date);
                    intent.putExtra("TIME", time);
                    intent.putExtra("PERSON", person);
                    intent.putExtra("PLACE", place);
                    startActivity(intent);
                }
                return false;
            }
        });


        displayDate = findViewById(R.id.dateChoiceValue);
        findViewById(R.id.dateChoiceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        databaseHelper =new DatabaseHelper(this);
        arrayList=new ArrayList<DataTable>();
        viewData();



        Calendar dateNow = Calendar.getInstance();
        int year=dateNow.get(Calendar.YEAR);
        int month=dateNow.get(Calendar.MONTH);
        int day=dateNow.get(Calendar.DAY_OF_MONTH);

        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy",Locale.getDefault());
        String formattedDate = simpleDateFormat.format(currentTime);
        displayDate.setText(formattedDate);

        savePreferences(year, month, day);
    }

    public void savePreferences(int year, int month, int day){
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("year", year);
        editor.putInt("month", month);
        editor.putInt("day", day);
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        adapterData.swapItems(databaseHelper.getAllData(displayDate.getText().toString()));
    }

    private void showDatePickerDialog()
    {
        final Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        //preferences = getSharedPreferences("preferences", 0);
        //int year = preferences.getInt("year", 0);
        //int month = preferences.getInt("month", 0);
        //int day= preferences.getInt("day", 0);

        //String[] values=value.split(".");
        //int year=Integer.parseInt(values[0]);
        //int month=Integer.parseInt(values[1]);
        //int day=Integer.parseInt(values[2]);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                //SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                //String formattedDate = simpleDateFormat.format(calendar.getTime());

                String date=String.format("%02d.%02d.", day , (month+1))+year;
                displayDate.setText(date);
                adapterData.swapItems(databaseHelper.getAllData(displayDate.getText().toString()));
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuPlus:
                add();
                break;
            case R.id.menuSort:
                sort();
                break;
            case R.id.menuClear:
                clear();
                break;
            default:
                return false;
        }
        return true;
    }

    void add() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] choice = {"Nowy", "Z listy"};

        builder.setTitle("Dodaj rekord").setItems(choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        addNew();
                        break;
                    case 1:
                        addList();
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void sort() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] choice = {"Data dodania", "Godzina", "Osoba", "Miejsce"};

        builder.setTitle("Sortuj według").setItems(choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        timeOrder();
                        break;
                    case 1:
                        personOrder();
                        break;
                    case 2:
                        placeOrder();
                        break;
                    case 3:
                        idOrder();
                        break;
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void clear() {
        Toast.makeText(this, "Działa", Toast.LENGTH_SHORT).show();
    }

    void addNew() {
        String date=displayDate.getText().toString();
        Intent intent = new Intent(getBaseContext(), Add.class);
        intent.putExtra("DATE", date);
        startActivity(intent);
    }

    void addList() {
        String date=displayDate.getText().toString();
        Intent intent = new Intent(getBaseContext(), List.class);
        intent.putExtra("DATE", date);
        startActivity(intent);
    }

    private void viewData() {
        arrayList= databaseHelper.getAllData(displayDate.getText().toString());

        adapterData = new AdapterData(this,arrayList);
        listView.setAdapter(adapterData);
    }

    void timeOrder() {
        preferences = getSharedPreferences("preferences", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("orderByData", 0);
        editor.apply();


        //SharedPreferences.Editor editor = preferences.edit();
        //editor.putInt(ORDER_BY_DATA, 0);
        //editor.apply();
    }

    void personOrder() {
        preferences = getSharedPreferences("preferences", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("orderByData", 1);
        editor.apply();

        adapterData.swapItems(databaseHelper.getAllData(displayDate.getText().toString()));
        Toast.makeText(this, "Działa", Toast.LENGTH_SHORT).show();
    }

    void placeOrder() {
        preferences = getSharedPreferences("preferences", 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("orderByData", 2);
        editor.apply();
    }

    void idOrder() {
        preferences = getSharedPreferences("preferences",0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("orderByData", 3);
        editor.apply();
    }
}

