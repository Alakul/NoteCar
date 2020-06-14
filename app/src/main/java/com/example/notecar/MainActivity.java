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

    private String date;
    private int sortData;

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

        listView=findViewById(R.id.listView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                arrayList= databaseHelper.getAllData(displayDate.getText().toString(), sortData);
                DataTable dataTable=arrayList.get(position);

                Intent intent = new Intent(getBaseContext(), Edit.class);
                intent.putExtra("ID", dataTable.getId());
                intent.putExtra("DATE", dataTable.getDate());
                intent.putExtra("TIME", dataTable.getTime());
                intent.putExtra("PERSON", dataTable.getPerson());
                intent.putExtra("PLACE", dataTable.getPlace());
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

                arrayList= databaseHelper.getAllData(displayDate.getText().toString(), sortData);
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
                if (item.getItemId() == R.id.menuDel) {
                    ArrayList<Integer> selectedItemPositions = adapterData.itemsSelected;
                    int deleted = 0;
                    int sumDel = selectedItemPositions.size();
                    for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                        databaseHelper.deleteData(selectedItemPositions.get(i));
                        deleted++;
                    }
                    onResume();
                    mode.finish();

                    Toast.makeText(getApplicationContext(), "Usunięto " + deleted + " z " + sumDel, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
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
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.parseColor("#2196F3")));
                openItem.setWidth(190);
                openItem.setTitle("Edytuj");
                openItem.setTitleSize(18);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);
            }
        };
        listView.setMenuCreator(creator);

        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                if (index == 0) {
                    arrayList= databaseHelper.getAllData(displayDate.getText().toString(), sortData);
                    DataTable dataTable=arrayList.get(position);

                    Intent intent = new Intent(getBaseContext(), Edit.class);
                    intent.putExtra("ID", dataTable.getId());
                    intent.putExtra("DATE", dataTable.getDate());
                    intent.putExtra("TIME", dataTable.getTime());
                    intent.putExtra("PERSON", dataTable.getPerson());
                    intent.putExtra("PLACE", dataTable.getPlace());
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


        SharedPreferences sharedpreferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        if (sharedpreferences.contains(date)) {
            displayDate.setText(sharedpreferences.getString("date", "")); }
        else {
            Date currentTime = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy",Locale.getDefault());
            String formattedDate = simpleDateFormat.format(currentTime);
            displayDate.setText(formattedDate);
        }
    }

    public void savePreferences(String date){
        SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("date", date);
        editor.putInt("sortData", sortData);
        editor.apply();
    }

    public void getPreferences(){
        SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        date = preferences.getString("date", "");
        sortData = preferences.getInt("sortData", -1);
        displayDate.setText(date);
    }

    @Override
    public void onPause() {
        super.onPause();
        savePreferences(displayDate.getText().toString());
        adapterData.swapItems(databaseHelper.getAllData(displayDate.getText().toString(), sortData));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferences();
        adapterData.swapItems(databaseHelper.getAllData(displayDate.getText().toString(), sortData));
    }

    private void showDatePickerDialog() {
        final Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(year, month, day);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String formattedDate = simpleDateFormat.format(calendar.getTime());

                displayDate.setText(formattedDate);
                savePreferences(formattedDate);
                adapterData.swapItems(databaseHelper.getAllData(displayDate.getText().toString(),sortData));
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
            case R.id.menuList:
                showList();
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
        String date=displayDate.getText().toString();
        Intent intent = new Intent(getBaseContext(), Add.class);
        intent.putExtra("DATE", date);
        startActivity(intent);
    }

    void sort() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] choice = {"Data dodania", "Godzina", "Osoba", "Miejsce"};

        builder.setTitle("Sortuj według").setItems(choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        sortData = 0;
                        break;
                    case 1:
                        sortData = 1;
                        break;
                    case 2:
                        sortData = 2;
                        break;
                    case 3:
                        sortData = 3;
                        break;
                }
                setOrder();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void clear() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Czy na pewno chcesz usunąć wszystkie rekordy ze strony?");
        builder.setCancelable(true);

        builder.setPositiveButton("Tak",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        databaseHelper.deleteAllData(displayDate.getText().toString());
                        adapterData.swapItems(databaseHelper.getAllData(displayDate.getText().toString(), sortData));
                    }
                });

        builder.setNegativeButton("Anuluj",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    void showList() {
        String date=displayDate.getText().toString();
        Intent intent = new Intent(getBaseContext(), List.class);
        intent.putExtra("DATE", date);
        startActivity(intent);
    }

    private void viewData() {
        arrayList= databaseHelper.getAllData(displayDate.getText().toString(), sortData);
        adapterData = new AdapterData(this,arrayList);
        listView.setAdapter(adapterData);
    }

    void setOrder(){
        savePreferences(displayDate.getText().toString());
        adapterData.swapItems(databaseHelper.getAllData(displayDate.getText().toString(), sortData));
    }
}

