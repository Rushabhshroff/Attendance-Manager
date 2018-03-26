package com.cryvis.attendancemanager.Objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cryvis.attendancemanager.Fragments.AttendanceFragment;
import com.cryvis.attendancemanager.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Cryvis on 24-01-2018.
 */

public class AttendanceRVA extends RecyclerView.Adapter<AttendanceRVA.ARVHolder> {
    public ArrayList<Subject> Subjects = new ArrayList<>();
    AttendanceFragment context = null;
    public AttendanceRVA(Context context){
        this.context = (AttendanceFragment) context;
    }
    public void AddSubject(Subject subject){
        Subjects.add(subject);
    }
    @Override
    public ARVHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View recyclerView = inflater.inflate(R.layout.attendance_prefab,parent,false);
        return new ARVHolder(recyclerView);
    }

    @Override
    public void onBindViewHolder(ARVHolder holder, final int position) {
        holder.SubjectName.setText(Subjects.get(position).Name);
        holder.PercentageAttendance.setText(String.valueOf(Subjects.get(position).GetPercentAttendance()) + "%");
        holder.LecturesBunked.setText("Bunked : " + String.valueOf(Subjects.get(position).GetBunked()));
        holder.LecturesAttended.setText("Attended : "+ String.valueOf(Subjects.get(position).GetAttended()));
        holder.Comment.setText(Subjects.get(position).GetComment());
        holder.AddAttended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Subjects.get(position).AddAttendance(1);
                context.Refresh();
                SaveSubjectList();
                notifyDataSetChanged();
            }
        });
        holder.SubtractAttended.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Subjects.get(position).SubtractAttendance(1);
                context.Refresh();
                SaveSubjectList();
                notifyDataSetChanged();
            }
        });
        holder.AddBuncked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Subjects.get(position).AddBunked(1);
                context.Refresh();
                SaveSubjectList();
                notifyDataSetChanged();
            }
        });
        holder.SubtractBuncked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Subjects.get(position).SubtractBunked(1);
                context.Refresh();
                SaveSubjectList();
                notifyDataSetChanged();
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.ModifiySubject(Subjects.get(position),position);
            }
        });
        if(Subjects.get(position).GetPercentAttendance() != 0) {
            if (Subjects.get(position).GetPercentAttendance() >= Subjects.get(position).MinAttendance) {
                holder.PercentageAttendance.setTextColor(Color.GREEN);
            } else {
                holder.PercentageAttendance.setTextColor(Color.RED);
            }
        }else{
            holder.PercentageAttendance.setTextColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return Subjects.size();
    }
    public int GetTotalPercent(){
        float finalResult = 0;
        float attended = 0;
        float bunked = 0;
        for(int i = 0;i < Subjects.size();i++){
            attended += Subjects.get(i).LecturesAttended;
            bunked += Subjects.get(i).LecturesBunked;
        }
        finalResult = (attended/(attended+bunked))*100;
        return (int)finalResult;
    }
    public void SaveSubjectList(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        SharedPreferences.Editor prefEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(Subjects);
        prefEditor.putString(context.getResources().getString(R.string.SubjectListID),json);
        prefEditor.commit();
    }
    public void LoadSubjectList(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        String json = sharedPreferences.getString(context.getResources().getString(R.string.SubjectListID),"");
        Type type = new TypeToken<ArrayList<Subject>>(){}.getType();
        if(json != "" || json != null) {
            Subjects = gson.fromJson(json, type);
            if(Subjects == null){
                Subjects = new ArrayList<>();
            }
        }
    }
    public void RemoveSubject(int Index){
        Subjects.remove(Index);
        notifyItemRemoved(Index);
        SaveSubjectList();
        context.Refresh();
    }
    public void UndoRemoved(Subject subject , int index){
       Subjects.add(index,subject);
       notifyItemInserted(index);
       SaveSubjectList();
       context.Refresh();
    }
    public class ARVHolder extends RecyclerView.ViewHolder{
        public TextView SubjectName;
        public  TextView PercentageAttendance;
        public TextView LecturesAttended;
        public  TextView LecturesBunked;
        public TextView Comment;
        public Button AddAttended;
        public Button SubtractAttended;
        public Button AddBuncked;
        public Button SubtractBuncked;
        public ARVHolder(View view){
            super(view);
            SubjectName = view.findViewById(R.id.subject_name);
            PercentageAttendance = view.findViewById(R.id.attendance_percent);
            LecturesAttended = view.findViewById(R.id.attended_text);
            LecturesBunked = view.findViewById(R.id.bunked_text);
            AddAttended = view.findViewById(R.id.btn_add_attendance);
            SubtractAttended = view.findViewById(R.id.btn_subtract_attendance);
            AddBuncked = view.findViewById(R.id.btn_add_bunk);
            SubtractBuncked = view.findViewById(R.id.btn_subtract_bunk);
            Comment = view.findViewById(R.id.comment_text);
        }
    }

}
