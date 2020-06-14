package com.example.notecar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.content.DialogInterface;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class List extends AppCompatActivity {

    private TextView displayDate;
    private String displayDateList;
    private DatabaseHelper databaseHelper;
    private ListView listView;
    private AdapterList adapterList;
    private ArrayList<ListTable> arrayList;

    int sortList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Lista");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String date = getIntent().getStringExtra("DATE");
        displayDate = findViewById(R.id.dateChoiceValue);
        displayDate.setText(date);

        listView=findViewById(R.id.listView);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new ListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                int checkedCount = listView.getCheckedItemCount();
                mode.setTitle("Zaznaczono: "+ checkedCount);

                ListTable listTable=arrayList.get(position);
                int idAdd=listTable.getId();

                if (checked) {
                    adapterList.itemsSelected.add(idAdd); }
                else {
                    adapterList.itemsSelected.remove(id);
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_selection_list, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {

                final ArrayList<Integer> selectedItemPositions;
                switch (item.getItemId()) {
                    case R.id.menuDel:
                        selectedItemPositions = adapterList.itemsSelected;

                        AlertDialog.Builder builder = new AlertDialog.Builder(List.this);
                        builder.setMessage("Czy na pewno chcesz usunąć zaznaczone rekordy?");
                        builder.setCancelable(true);

                        builder.setPositiveButton("Tak",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        int deleted = 0;
                                        int sumDel = selectedItemPositions.size();
                                        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                                            databaseHelper.deleteList(selectedItemPositions.get(i));
                                            deleted++;
                                        }
                                        onResume();
                                        mode.finish();

                                        Toast.makeText(getApplicationContext(), "Usunięto "+deleted+" z "+ sumDel, Toast.LENGTH_SHORT).show();
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

                        return true;

                    case R.id.menuPlus:
                        selectedItemPositions = adapterList.itemsSelected;
                        int added = 0;
                        int sumAdd = selectedItemPositions.size();
                        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                            displayDateList=displayDate.getText().toString();

                            if (databaseHelper.insertToData(selectedItemPositions.get(i), displayDateList)){
                                added++;
                            }
                        }
                        mode.finish();

                        Toast.makeText(getApplicationContext(), "Dodano "+added+" z "+ sumAdd, Toast.LENGTH_SHORT).show();
                        return true;

                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                adapterList.itemsSelected.clear();
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
                    arrayList= databaseHelper.getAllList(sortList);
                    ListTable listTable=arrayList.get(position);

                    Intent intent = new Intent(getBaseContext(), EditList.class);
                    intent.putExtra("ID", listTable.getId());
                    intent.putExtra("TIME", listTable.getTime());
                    intent.putExtra("PERSON", listTable.getPerson());
                    intent.putExtra("PLACE", listTable.getPlace());
                    startActivity(intent);
                }
                return false;
            }
        });


        findViewById(R.id.dateChoiceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        databaseHelper=new DatabaseHelper(this);
        arrayList=new ArrayList<ListTable>();
        viewData();

        sortList=0;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuPlus:
                addList();
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

    void addList() {
        Intent intent = new Intent(getBaseContext(), AddList.class);
        startActivity(intent);
    }

    void clear() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Czy na pewno chcesz usunąć wszystkie rekordy?");
        builder.setCancelable(true);

        builder.setPositiveButton("Tak",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        databaseHelper.deleteAllList();
                        adapterList.swapItems(databaseHelper.getAllList(sortList));
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

    public void savePreferences(){
        SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("sortList", sortList);
        editor.apply();
    }

    public void getPreferences(){
        SharedPreferences preferences = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        sortList = preferences.getInt("sortList", -1);
    }

    @Override
    public void onPause() {
        super.onPause();
        savePreferences();
        adapterList.swapItems(databaseHelper.getAllList(sortList));
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferences();
        adapterList.swapItems(databaseHelper.getAllList(sortList));
    }

    private void showDatePickerDialog()
    {
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
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void viewData() {
        arrayList=databaseHelper.getAllList(sortList);
        adapterList = new AdapterList(this,arrayList);
        listView.setAdapter(adapterList);
    }

    void sort() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] choice = {"Data dodania", "Godzina", "Osoba", "Miejsce"};

        builder.setTitle("Sortuj według").setItems(choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        sortList = 0;
                        break;
                    case 1:
                        sortList = 1;
                        break;
                    case 2:
                        sortList = 2;
                        break;
                    case 3:
                        sortList = 3;
                        break;
                }
                setOrder();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    void setOrder(){
        savePreferences();
        adapterList.swapItems(databaseHelper.getAllList(sortList));
    }
}