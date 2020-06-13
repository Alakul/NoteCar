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

import java.util.ArrayList;
import java.util.Calendar;

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
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

                ArrayList<Integer> selectedItemPositions;
                switch (item.getItemId()) {
                    case R.id.menuDel:
                        selectedItemPositions = adapterList.itemsSelected;
                        int deleted = 0;
                        int sumDel = selectedItemPositions.size();
                        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
                            databaseHelper.deleteList(selectedItemPositions.get(i));
                            deleted++;
                        }
                        onResume();
                        mode.finish();

                        Toast.makeText(getApplicationContext(), "Usunięto "+deleted+" z "+ sumDel, Toast.LENGTH_SHORT).show();
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

    public void savePreferences(int sortList){
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
        savePreferences(sortList);
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
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                //SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                String date=String.format("%02d.%02d.", day , (month+1))+year;
                displayDate.setText(date);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void viewData() {
        arrayList=databaseHelper.getAllList(sortList);
        adapterList = new AdapterList(this,arrayList);
        listView.setAdapter(adapterList);
    }
}