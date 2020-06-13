package com.example.notecar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Add extends AppCompatActivity {

    private EditText person,place;
    private TextView displayDate;
    private TextView displayTime;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Dodaj dane");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String date = getIntent().getStringExtra("DATE");

        person=findViewById(R.id.personValue);
        place=findViewById(R.id.placeValue);

        databaseHelper =new DatabaseHelper(this);

        displayDate = findViewById(R.id.dateChoiceValue);
        displayDate.setText(date);

        findViewById(R.id.dateChoiceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        displayTime = findViewById(R.id.timeChoiceValue);
        displayTime.setText("00:00");

        findViewById(R.id.timeChoiceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String personTrim = person.getText().toString().trim();
                String placeTrim = place.getText().toString().trim();
                if (personTrim.length()==0 || placeTrim.length()==0) {
                    showAlertDialogEmpty(); }
                else {
                    addData(personTrim, placeTrim);
                }
            }
        });

        findViewById(R.id.addToListButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String personTrim = person.getText().toString().trim();
                String placeTrim = place.getText().toString().trim();
                if (personTrim.length()==0 || placeTrim.length()==0) {
                    showAlertDialogEmpty(); }
                else {
                    addToList(personTrim, placeTrim);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id= menuItem.getItemId();
        if (id==android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                String time=String.format("%02d:%02d", hour, minute);
                displayTime.setText(time);
            }
        }, 0, 0, true);
        timePickerDialog.show();
    }

    private void showDatePickerDialog() {
        Calendar calendar=Calendar.getInstance();
        int year=calendar.get(Calendar.YEAR);
        int month=calendar.get(Calendar.MONTH);
        int day=calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String date=String.format("%02d.%02d.", day , (month+1))+year;
                //SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                //String date=dateFormatter.format(new Date());
                displayDate.setText(date);
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showAlertDialogEmpty() {
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setMessage("Wypełnij wszystkie pola!");
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    private void addData(String personTrim, String placeTrim) {
        String dateT = displayDate.getText().toString();
        String timeT = displayTime.getText().toString();

        if (databaseHelper.insertData(dateT, timeT, personTrim, placeTrim)) {
            person.getText().clear();
            place.getText().clear();
            Toast.makeText(this, "Rekord dodany pomyślnie", Toast.LENGTH_SHORT).show(); }
        else {
            Toast.makeText(this, "Rekord istnieje", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToList(String personTrim, String placeTrim) {
        String timeT = displayTime.getText().toString();

        if (databaseHelper.insertList(timeT, personTrim, placeTrim)) {
            Toast.makeText(this, "Dodano do listy", Toast.LENGTH_SHORT).show(); }
        else {
            Toast.makeText(this, "Rekord istnieje", Toast.LENGTH_SHORT).show();
        }
    }

}