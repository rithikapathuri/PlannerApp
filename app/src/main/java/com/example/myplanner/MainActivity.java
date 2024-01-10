package com.example.myplanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView newNote;
    CalendarView calendarView;
    ListView listView;
    TextView currentDate;
    String today;
    String current;
    int currentIndex;
    ArrayList<ArrayList<Note>> notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newNote = findViewById(R.id.imageView_plus);
        calendarView = findViewById(R.id.calendarView);
        listView = findViewById(R.id.listView);
        currentDate = findViewById(R.id.textView_currentDate);

        notes = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        today = calendar.get(Calendar.MONTH)+"/"+calendar.get(Calendar.DAY_OF_MONTH)+"/"+calendar.get(Calendar.YEAR);
        current = today;
        currentDate.setText(current);

        newNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildAlert("", today, "", 0);
            }
        });

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String str = month+"/"+dayOfMonth+"/"+year;
                updateCurrentNotes(str);
            }
        });
    }

    public void buildAlert(String title, String date, String description, int index){
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);

        View customView = getLayoutInflater().inflate(R.layout.new_note, null);
        alertBuilder.setView(customView);

        EditText editTextTitle = customView.findViewById(R.id.editText_title_newNote);
        editTextTitle.setText(title);

        EditText editTextDate = customView.findViewById(R.id.editText_date_newNote);
        editTextDate.setText(date);
        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this);
                datePickerDialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextDate.setText(month+"/"+dayOfMonth+"/"+year);
                    }
                });
                datePickerDialog.show();
            }
        });

        EditText editTextDescription = customView.findViewById(R.id.editText_description_newNote);
        editTextDescription.setText(description);

        if(title.equals("") && date.equals(today) && description.equals("")){
            alertBuilder.setTitle("New Note");
            alertBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    //create note
                    boolean added = false;
                    for(int x=0;x<notes.size();x++){
                        if(notes.get(x).get(0).getDate().equals(editTextDate.getText().toString())){
                            notes.get(x).add(new Note(editTextTitle.getText().toString(), editTextDate.getText().toString(), editTextDescription.getText().toString()));
                            added = true;
                        }
                    }

                    if(!added){
                        notes.add(new ArrayList<>());
                        notes.get(notes.size()-1).add(new Note(editTextTitle.getText().toString(), editTextDate.getText().toString(), editTextDescription.getText().toString()));
                    }

                    if(current.equals(editTextDate.getText().toString())){
                        updateCurrentNotes(current);
                    }

                    //Toast.makeText(MainActivity.this, "NOTE CREATED", Toast.LENGTH_SHORT);
                }
            });
        }
        else{
            alertBuilder.setTitle("Edit Note");
            alertBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();

                    //create note
                    boolean added = false;
                    for(int x=0;x<notes.size();x++){
                        if(notes.get(x).get(0).getDate().equals(editTextDate.getText().toString())){
                            notes.get(x).add(new Note(editTextTitle.getText().toString(), editTextDate.getText().toString(), editTextDescription.getText().toString()));
                            added = true;
                        }
                    }

                    if(!added){
                        notes.add(new ArrayList<>());
                        notes.get(notes.size()-1).add(new Note(editTextTitle.getText().toString(), editTextDate.getText().toString(), editTextDescription.getText().toString()));
                    }

                    notes.get(currentIndex).get(index).setTitle(editTextTitle.getText().toString());
                    notes.get(currentIndex).get(index).setDate(editTextDate.getText().toString());
                    notes.get(currentIndex).get(index).setDescription(editTextDescription.getText().toString());


                    if(current.equals(editTextDate.getText().toString())){
                        updateCurrentNotes(current);
                    }

                    //Toast.makeText(MainActivity.this, "NOTE CREATED", Toast.LENGTH_SHORT);
                }
            });
        }


        alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

                //Toast.makeText(MainActivity.this, "NOTE CANCELED", Toast.LENGTH_SHORT);
            }
        });

        alertBuilder.setTitle("New Note");

        AlertDialog myAlert = alertBuilder.create();
        myAlert.show();
    }

    public void updateCurrentNotes(String date){
        //String note = "";
        current = date;
        currentDate.setText(current);

        ArrayList<Note> notesStr = new ArrayList<>();;
        boolean found = false;
        int x = 0;
        while(!found && x<notes.size()){
            if(notes.get(x).get(0).getDate().equals(date)){
                found = true;
                currentIndex = x;
                notesStr = new ArrayList<>(notes.get(x));
            }
            x++;
        }

        CustomListAdapter adapter = new CustomListAdapter(MainActivity.this, R.layout.list_note, notesStr);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle(notes.get(currentIndex).get(position).getTitle());
                b.setMessage(""+notes.get(currentIndex).get(position).getDate()+"\n\n"+notes.get(currentIndex).get(position).getDescription()+"\n\n");
                b.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        buildAlert(notes.get(currentIndex).get(position).getTitle(), notes.get(currentIndex).get(position).getDate(), notes.get(currentIndex).get(position).getDescription(), position);
                    }
                });
                b.create().show();
            }
        });
    }

    public class Note{
        String title, date, description;
        boolean checked;

        public Note(String title, String date, String description){
            this.title = title;
            this.date = date;
            this.description = description;
            this.checked = false;
        }

        public String getTitle(){
            return title;
        }

        public String getDate(){
            return date;
        }

        public String getDescription(){
            return description;
        }

        public boolean getChecked(){
            return checked;
        }

        public void changeChecked(){
            checked = !checked;
        }

        public void setTitle(String title){
            this.title = title;
        }

        public void setDate(String date){
            this.date = date;
        }

        public void setDescription(String description){
            this.description = description;
        }

        public String toString(){
            return title;
        }
    }

    public class CustomListAdapter extends ArrayAdapter<Note> {

        Context mainContext;
        int xml;
        List<Note> list;

        public CustomListAdapter(@NonNull Context context, int resource, @NonNull List<Note> objects) {
            super(context, resource, objects);
            mainContext = context;
            xml = resource;
            list = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View customView;
            LayoutInflater layoutInflater = (LayoutInflater) mainContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            customView = layoutInflater.inflate(xml, null);
            TextView note = customView.findViewById(R.id.textView_list_note);
            note.setText(list.get(position).getTitle());

            CheckBox checkBox = customView.findViewById(R.id.checkBox_list_note);
            checkBox.setChecked(notes.get(currentIndex).get(position).getChecked());
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    notes.get(currentIndex).get(position).changeChecked();
                    Log.d("HELLO", "Changed - "+notes.get(currentIndex).get(position).getChecked());
                }
            });

            return customView;
        }
    }
}