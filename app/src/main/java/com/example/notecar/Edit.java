package com.example.notecar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class Edit extends AppCompatActivity {

    private EditText personValue, placeValue;
    private TimePickerDialog.OnTimeSetListener onTimeSetListener;
    private TextView displayDate;
    private TextView displayTime;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edytuj dane");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String date = getIntent().getStringExtra("DATE");
        String time = getIntent().getStringExtra("TIME");
        String person = getIntent().getStringExtra("PERSON");
        String place = getIntent().getStringExtra("PLACE");

        personValue=findViewById(R.id.personValue);
        personValue.setText(person);
        placeValue=findViewById(R.id.placeValue);
        placeValue.setText(place);
        displayDate = findViewById(R.id.dateChoiceValue);
        displayDate.setText(date);

        databaseHelper =new DatabaseHelper(this);

        findViewById(R.id.dateChoiceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        displayTime = findViewById(R.id.timeChoiceValue);
        displayTime.setText(time);

        findViewById(R.id.timeChoiceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        findViewById(R.id.editButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String personTrim = personValue.getText().toString().trim();
                String placeTrim = placeValue.getText().toString().trim();
                if (personTrim.length()==0 || placeTrim.length()==0) {
                    showAlertDialogEmpty(); }
                else {
                    editData(personTrim, placeTrim);
                }
            }
        });

        findViewById(R.id.addToListButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String personTrim = personValue.getText().toString().trim();
                String placeTrim = placeValue.getText().toString().trim();
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

    private void editData(String personTrim, String placeTrim) {
        int idUpdate=getIntent().getIntExtra("ID",0);
        String dateUpdate = displayDate.getText().toString();
        String timeUpdate = displayTime.getText().toString();
        databaseHelper.updateData(idUpdate, dateUpdate, timeUpdate, personTrim, placeTrim);

        Toast.makeText(this, "Rekord edytowany pomyślnie", Toast.LENGTH_SHORT).show();
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
