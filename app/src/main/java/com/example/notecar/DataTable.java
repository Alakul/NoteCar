package com.example.notecar;

import java.sql.Date;
import java.sql.Time;

public class DataTable {

    private int id;
    private String date;
    private String time;
    private String person;
    private String place;


    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id=id;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date=date;
    }

    public String getTime()
    {
        return  time;
    }

    public void setTime(String time)
    {
        this.time=time;
    }

    public String getPerson()
    {
        return  person;
    }

    public void setPerson(String person)
    {
        this.person=person;
    }

    public String getPlace()
    {
        return  place;
    }

    public void setPlace(String place)
    {
        this.place=place;
    }

}
