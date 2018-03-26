package com.cryvis.attendancemanager.Objects;

import android.text.Html;

/**
 * Created by Cryvis on 24-01-2018.
 */

public class Subject {
    public String Name;
    public int LecturesAttended = 0;
    public int LecturesBunked = 0;
    public int MinAttendance = 0;
    public Subject(String name,int lecturesAttended,int lecturesBunked,int minAttendance){
        Name = name;
        MinAttendance = minAttendance;
        LecturesAttended = lecturesAttended;
        LecturesBunked = lecturesBunked;
    }
    public  void AddAttendance(int Value){
        LecturesAttended += Value;
    }
    public void SubtractAttendance(int Value){
        if(LecturesAttended > 0) {
            LecturesAttended -= Value;
        }
    }
    public  void AddBunked(int Value){
        LecturesBunked += Value;
    }
    public void SubtractBunked(int Value){
        if(LecturesBunked > 0) {
            LecturesBunked -= Value;
        }
    }

    public void setLecturesAttended(int lecturesAttended) {
        LecturesAttended = lecturesAttended;
    }

    public void setLecturesBunked(int lecturesBunked) {
        LecturesBunked = lecturesBunked;
    }

    public  int GetAttended(){return  LecturesAttended;}
    public int GetBunked(){return LecturesBunked;}
    public int GetPercentAttendance(){
        if((LecturesAttended + LecturesBunked)>0){
            int percent = Math.round((float)LecturesAttended / ((float) LecturesBunked + (float) LecturesAttended) * 100);
            return percent;
        }else{
            return 0;
        }
    }

    public String GetComment(){
        if(GetPercentAttendance() >= MinAttendance){
            return Html.fromHtml("Can bunk <font color= '#41f458'>"+String.valueOf(getCanBunk()) + "</font> Lectures").toString();
        }else{
            return Html.fromHtml("Need to attend <font color= '#b20000'>"+String.valueOf(getRequiredAttendance()) + "</font> Lectures").toString();
        }
    }
    public int getCanBunk(){
        if(GetPercentAttendance() > MinAttendance) {
            float PercentDiff = ((100 - (float)MinAttendance) / (float)MinAttendance);
            int canBunk = Math.round((LecturesAttended * PercentDiff - LecturesBunked));
            return canBunk;
        }else {
            return 0;
        }
    }
    public int getRequiredAttendance()
    {
        if(GetPercentAttendance() < MinAttendance){
            int ReqAttendance = Math.round((float)LecturesBunked * ((float)MinAttendance/(100-(float)MinAttendance)) - (float) LecturesAttended);
            return ReqAttendance;
        }
        return 0;
    }
}
