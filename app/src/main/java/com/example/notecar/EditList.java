package com.example.notecar;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class EditList extends AppCompatActivity {

    private EditText personValue, placeValue;
    private TextView displayTime;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edytuj listę");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String time = getIntent().getStringExtra("TIME");
        String person = getIntent().getStringExtra("PERSON");
        String place = getIntent().getStringExtra("PLACE");

        personValue = findViewById(R.id.personValue);
        personValue.setText(person);
        placeValue = findViewById(R.id.placeValue);
        placeValue.setText(place);

        databaseHelper = new DatabaseHelper(this);

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
                if (personTrim.length() == 0 || placeTrim.length() == 0) {
                    showAlertDialogEmpty();
                } else {
                    editData(personTrim, placeTrim);
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

    private void showAlertDialogEmpty() {
        AlertDialog.Builder alert=new AlertDialog.Builder(this);
        alert.setMessage("Wypełnij wszystkie pola!");
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    private void editData(String personTrim, String placeTrim) {
        int idUpdate=getIntent().getIntExtra("ID",0);
        String timeUpdate = displayTime.getText().toString();
        databaseHelper.updateList(idUpdate, timeUpdate, personTrim, placeTrim);

        Toast.makeText(this, "Rekord edytowany pomyślnie", Toast.LENGTH_SHORT).show();
    }
}
