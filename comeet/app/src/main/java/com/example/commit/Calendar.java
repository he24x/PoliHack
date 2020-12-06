package com.example.commit;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class Calendar extends AppCompatActivity {

    private int currentYear = 0;
    private int currentMonth = 0;
    private int currentDay = 0;
    double ex=0;

    private int index = 0;
    private List<String> calendarString;
    private List<String> calendarInt;
    private int[] days;
    private int[] months;
    private int[] years;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        final CalendarView calendarView = findViewById(R.id.calendar);
        calendarString = new ArrayList<>();
        calendarInt = new ArrayList<>();
        final int numberofDays = 2000;
        days = new int[numberofDays];
        months = new int[numberofDays];
        years = new int[numberofDays];
        readInfo();
        final EditText textInput = findViewById(R.id.status);
        final EditText nrInput = findViewById(R.id.editTextNumber);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                currentYear = year;
                currentMonth = month;
                currentDay = dayOfMonth;
                for (int h = 0; h < index; h++) {
                    if (years[h] == currentYear) {
                        for (int i = 0; i < index; i++) {
                            if (days[i] == currentDay) {
                                for (int j = 0; j < index; j++) {
                                    if (months[j] == currentMonth && days[j] == currentDay && years[j] == currentYear) {
                                        textInput.setText(calendarString.get(j));
                                        nrInput.setText(calendarInt.get(j));
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }

                nrInput.setText("");
                textInput.setText("");
            }
        });
        final Button btn = findViewById(R.id.savebtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                days[index] = currentDay;
                months[index] = currentMonth;
                years[index] = currentYear;
                calendarString.add(index, textInput.getText().toString());
                calendarInt.add(index, nrInput.getText().toString());
                index++;
                ex = Integer.parseInt(nrInput.getText().toString());
                nrInput.setText("");
                textInput.setText("");
            }
        });

    }

    private void readInfo() {
        File file = new File(this.getFilesDir(), "saved");
        File daysFile = new File(this.getFilesDir(), "days");
        File monthsFile = new File(this.getFilesDir(), "months");
        File yearsFile = new File(this.getFilesDir(), "years");

        if (!file.exists()) {
            return;
        }
        try {
            FileInputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            FileInputStream isDays = new FileInputStream(daysFile);
            BufferedReader readerDays = new BufferedReader(new InputStreamReader(isDays));

            FileInputStream isMonths = new FileInputStream(monthsFile);
            BufferedReader readerMonths = new BufferedReader(new InputStreamReader(isMonths));

            FileInputStream isYears = new FileInputStream(yearsFile);
            BufferedReader readerYears = new BufferedReader(new InputStreamReader(isYears));

            int i = 0;
            String line = reader.readLine();

            while (line != null) {
                calendarInt.add(line);
                line = reader.readLine();
                days[i] = readerDays.read();
                months[i] = readerMonths.read();
                years[i] = readerYears.read();
                i++;
            }

            index = i;
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileInputStream ais = new FileInputStream(file);
            BufferedReader areader = new BufferedReader(new InputStreamReader(ais));

            FileInputStream aisDays = new FileInputStream(daysFile);
            BufferedReader areaderDays = new BufferedReader(new InputStreamReader(aisDays));

            FileInputStream aisMonths = new FileInputStream(monthsFile);
            BufferedReader areaderMonths = new BufferedReader(new InputStreamReader(aisMonths));

            FileInputStream aisYears = new FileInputStream(yearsFile);
            BufferedReader areaderYears = new BufferedReader(new InputStreamReader(aisYears));

            int i = 0;
            String line = areader.readLine();

            while (line != null) {
                calendarString.add(line);
                line = areader.readLine();
                days[i] = areaderDays.read();
                months[i] = areaderMonths.read();
                years[i] = areaderYears.read();
                i++;
            }

            index = i;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveInfo();
    }

    private void saveInfo() {
        File file = new File(this.getFilesDir(), "saved");
        File daysFile = new File(this.getFilesDir(), "days");
        File monthsFile = new File(this.getFilesDir(), "months");
        File yearsFile = new File(this.getFilesDir(), "years");

        try {
            FileOutputStream fOut = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fOut));

            FileOutputStream fOutDays = new FileOutputStream(daysFile);
            BufferedWriter bwDays = new BufferedWriter(new OutputStreamWriter(fOutDays));

            FileOutputStream fOutMonths = new FileOutputStream(monthsFile);
            BufferedWriter bwMonths = new BufferedWriter(new OutputStreamWriter(fOutMonths));

            FileOutputStream fOutYears = new FileOutputStream(yearsFile);
            BufferedWriter bwYears = new BufferedWriter(new OutputStreamWriter(fOutYears));

            for (int i = 0; i < index; i++) {
                bw.write(calendarString.get(i));
                bw.newLine();
                bwDays.write(days[i]);
                bwMonths.write(months[i]);
                bwYears.write(years[i]);
            }

            bw.close();
            fOut.close();
            bwDays.close();
            fOutDays.close();
            bwMonths.close();
            fOutMonths.close();
            bwYears.close();
            fOutYears.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream tOut = new FileOutputStream(file);
            BufferedWriter tw = new BufferedWriter(new OutputStreamWriter(tOut));

            FileOutputStream tOutDays = new FileOutputStream(daysFile);
            BufferedWriter twDays = new BufferedWriter(new OutputStreamWriter(tOutDays));

            FileOutputStream tOutMonths = new FileOutputStream(monthsFile);
            BufferedWriter twMonths = new BufferedWriter(new OutputStreamWriter(tOutMonths));

            FileOutputStream tOutYears = new FileOutputStream(yearsFile);
            BufferedWriter twYears = new BufferedWriter(new OutputStreamWriter(tOutYears));

            for (int i = 0; i < index; i++) {
                tw.write(calendarInt.get(i));
                tw.newLine();
                twDays.write(days[i]);
                twMonths.write(months[i]);
                twYears.write(years[i]);
            }

            tw.close();
            tOut.close();
            twDays.close();
            tOutDays.close();
            twMonths.close();
            tOutMonths.close();
            twYears.close();
            tOutYears.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}