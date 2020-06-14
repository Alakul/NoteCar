package com.example.notecar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddList extends AppCompatActivity {

    private EditText person,place;
    private TextView displayTime;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Dodaj do listy");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        person=findViewById(R.id.personValue);
        place=findViewById(R.id.placeValue);
        databaseHelper =new DatabaseHelper(this);

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

        displayTime = findViewById(R.id.timeChoiceValue);
        displayTime.setText("00:00");

        findViewById(R.id.timeChoiceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
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

    private void showAlertDialogEmpty() {
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setMessage("Wypełnij wszystkie pola!");
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id= menuItem.getItemId();
        if (id==android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    private void addToList(String personTrim, String placeTrim) {
        String timeT = displayTime.getText().toString();

        if (databaseHelper.insertList(timeT, personTrim, placeTrim)) {
            person.getText().clear();
            place.getText().clear();
            Toast.makeText(this, "Dodano do listy", Toast.LENGTH_SHORT).show(); }
        else {
            Toast.makeText(this, "Dane istnieją na liście", Toast.LENGTH_SHORT).show();
        }
    }
}