package com.cryvis.attendancemanager.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cryvis.attendancemanager.Objects.AttendanceRVA;
import com.cryvis.attendancemanager.Objects.Subject;
import com.cryvis.attendancemanager.R;

/**
 * Created by Cryvis on 24-01-2018.
 */

public class AttendanceFragment extends AppCompatActivity {
    public static final String fragmentID = "ATTENDANCEFRAG";
    private RecyclerView recyclerView;
    private AttendanceRVA recyclerViewAdapter;
    private LinearLayout NoSubjectsAdded;
    private RelativeLayout SubjectsAdded;
    private Button AddSubjectsEmptyBtn;
    private Button AddSubjectBtn;
    private TextView TotalAttendancetxt;
    private Dialog mBottomSheetDialog;
    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_attendance);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        NoSubjectsAdded = (LinearLayout) findViewById(R.id.layout_nosubjectadded);
        SubjectsAdded = (RelativeLayout) findViewById(R.id.layout_subjectadded);
        AddSubjectsEmptyBtn = (Button) findViewById(R.id.addsubject_emptylist);
        AddSubjectBtn = (Button) findViewById(R.id.addsubject_filledlist);
        TotalAttendancetxt = (TextView)findViewById(R.id.totalAttendance);
        recyclerViewAdapter = new AttendanceRVA(this);
        recyclerViewAdapter.LoadSubjectList();
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerViewAdapter);
        Refresh();
        AddSubjectsEmptyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddSubjectDialog(view);
            }
        });
        AddSubjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddSubjectDialog(view);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final Subject RemovedSubject = recyclerViewAdapter.Subjects.get(viewHolder.getAdapterPosition());
                final int RemovedIndex = viewHolder.getAdapterPosition();
                recyclerViewAdapter.RemoveSubject(viewHolder.getAdapterPosition());
                if(mBottomSheetDialog == null) {
                        mBottomSheetDialog = new Dialog(AttendanceFragment.this);
                        mBottomSheetDialog.setContentView(R.layout.dialog_askundo); // your custom view.
                        mBottomSheetDialog.setCancelable(true);
                        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
                        mBottomSheetDialog.setTitle("Undo Changes?");
                        Button Undo = (Button) mBottomSheetDialog.findViewById(R.id.btn_Undo);
                        Undo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                recyclerViewAdapter.UndoRemoved(RemovedSubject,RemovedIndex);
                                mBottomSheetDialog.dismiss();
                                mBottomSheetDialog = null;
                            }
                        });
                        mBottomSheetDialog.show();
                        mBottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                mBottomSheetDialog = null;
                            }
                        });
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

    }
    public void ModifiySubject(final Subject subject , final int Index){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.dialogue_attendance,null);
        builder.setView(v);
        final Dialog dialog = builder.create();
        final TextView DialogTitle = v.findViewById(R.id.dialogue_title);
        final EditText SubjectName = v.findViewById(R.id.text_input_SubjectName);
        final EditText MinAttendance = v.findViewById(R.id.text_input_MinAttendance);
        final EditText LecturesAttended = v.findViewById(R.id.text_input_LecturesAttended);
        final EditText LecturesBunked = v.findViewById(R.id.text_input_LecturesBunked);
        final Button AddSubjectButton = v.findViewById(R.id.btn_AddSubject_dialog);
        SubjectName.setText(subject.Name);
        DialogTitle.setText("Modify Subject");
        MinAttendance.setText(String.valueOf(subject.MinAttendance));
        LecturesAttended.setText(String.valueOf(subject.LecturesAttended));
        LecturesBunked.setText(String.valueOf(subject.LecturesBunked));
        AddSubjectButton.setText("Modify Subject");
        AddSubjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subject.Name = SubjectName.getText().toString();
                subject.MinAttendance = Integer.valueOf(MinAttendance.getText().toString());
                subject.setLecturesAttended(Integer.valueOf(LecturesAttended.getText().toString()));
                subject.setLecturesBunked(Integer.valueOf(LecturesBunked.getText().toString()));
                recyclerViewAdapter.Subjects.set(Index,subject);
                recyclerViewAdapter.notifyItemChanged(Index);
                recyclerViewAdapter.SaveSubjectList();
                Refresh();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void AddSubjectDialog(View view)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.dialogue_attendance,null);
        builder.setView(v);
        final Dialog dialog = builder.create();
        final TextView DialogTitle = v.findViewById(R.id.dialogue_title);
        final EditText SubjectName = v.findViewById(R.id.text_input_SubjectName);
        final EditText MinAttendance = v.findViewById(R.id.text_input_MinAttendance);
        final EditText LecturesAttended = v.findViewById(R.id.text_input_LecturesAttended);
        final EditText LecturesBunked = v.findViewById(R.id.text_input_LecturesBunked);
        final Button AddSubjectButton = v.findViewById(R.id.btn_AddSubject_dialog);
        DialogTitle.setText("Add Subject");
        AddSubjectButton.setText("Add Subject");
        AddSubjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(SubjectName.getText().toString().length() <= 0){
                    SubjectName.setError("Enter Subject Name");
                    return;
                }
                if (MinAttendance.getText().toString() == "" || MinAttendance.getText().toString() == null || MinAttendance.getText().toString().length() <= 0 ){
                    MinAttendance.setError("Invalid Input");
                    return;
                }else if(Integer.parseInt(MinAttendance.getText().toString()) <= 0){
                    MinAttendance.setError("Invalid Input");
                    return;
                }
                if(LecturesAttended.getText().toString().length() <= 0){
                    LecturesAttended.setText("0");
                }
                if(LecturesBunked.getText().toString().length() <= 0){
                    LecturesBunked.setText("0");
                }
                Subject subject = new Subject(SubjectName.getText().toString(),Integer.parseInt(LecturesAttended.getText().toString()),Integer.parseInt(LecturesBunked.getText().toString()),Integer.parseInt(MinAttendance.getText().toString()));
                recyclerViewAdapter.AddSubject(subject);
                recyclerViewAdapter.notifyDataSetChanged();
                recyclerViewAdapter.SaveSubjectList();
                Refresh();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void Refresh(){
        if(recyclerViewAdapter.getItemCount() > 0){
            NoSubjectsAdded.setVisibility(View.INVISIBLE);
            SubjectsAdded.setVisibility(View.VISIBLE);
        }else{
            NoSubjectsAdded.setVisibility(View.VISIBLE);
            SubjectsAdded.setVisibility(View.INVISIBLE);
        }

        TotalAttendancetxt.setText("Total Attendance is " + String.valueOf(recyclerViewAdapter.GetTotalPercent()) + "%");
    }
}
