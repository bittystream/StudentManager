package cn.edu.cqu.studentmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import cn.edu.cqu.studentmanager.util.BlobUtil;
import cn.edu.cqu.studentmanager.DisplayActivity;

import static android.service.controls.ControlsProviderService.TAG;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder> {
    private List<StudentInfo> mInfoList;
    public InfoAdapter(List<StudentInfo> infoList, Context context){
        mInfoList = infoList;
        mContext = context;
    }
    private View parentView = null;
    private TextView clickedItem = null;
    private String clickedStudentId;
    private Context mContext;



    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView studentIdText, studentNameText, studentGenderText, studentNativeText, studentGpaText, studentCollegeText;
        ImageView studentAvatarImage;
        public ViewHolder(View view){
            super(view);
            studentIdText = view.findViewById(R.id.display_id);
            studentNameText = view.findViewById(R.id.display_name);
            studentGenderText = view.findViewById(R.id.display_gender);
            studentNativeText = view.findViewById(R.id.display_native);
            studentGpaText = view.findViewById(R.id.display_gpa);
            studentCollegeText = view.findViewById(R.id.display_college);
            studentAvatarImage = view.findViewById(R.id.display_avatar);
        }
    }

    private int findPositionById(String id){
        List<String> idList = new ArrayList<>();
        for (StudentInfo s: mInfoList){
            idList.add(s.getId());
        }
        return idList.indexOf(id);
    }


    class InfoOnLongClickListener implements View.OnLongClickListener{

        private static final String TAG = "InfoOnLongClickListener";

        @Override
        public boolean onLongClick(View view) {

            clickedItem = (TextView)((ViewGroup)view).getChildAt(2);
            clickedStudentId = clickedItem.getText().toString().substring(3);
            // id ??? primary key, ???????????????infoList?????????id???studentInfo???, ???????????????index????????????
            Log.i(TAG, "onLongClick: "+clickedStudentId);
            // ???????????????????????????????????????????????????????????????
            String [] options = {"????????????","??????"};
            AlertDialog.Builder listDialog = new AlertDialog.Builder(parentView.getContext());
            listDialog.setTitle("?????????");
            DisplayActivity activity = new DisplayActivity();
            listDialog.setItems(options, new DialogOnClickListener());
            listDialog.show();
            return true;
        }
    }

    class DialogOnClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == 0){// ???????????????
                DatabaseHelper databaseHelper = new DatabaseHelper(parentView.getContext(),"student",null,1);
                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                String sql = "select avatar from student where id = \"" + clickedStudentId + "\"";
                Cursor cursor = db.rawQuery(sql,null);
                byte [] avatarBlob = null;
                if (cursor.moveToFirst()){
                    avatarBlob = cursor.getBlob(cursor.getColumnIndex("avatar"));
                }
                Intent intent = new Intent(mContext,ImageActivity.class);
                intent.putExtra("avatar",avatarBlob);
                mContext.startActivity(intent);
            }
            else {
                final AlertDialog.Builder normalDialog = new AlertDialog.Builder(mContext);
                normalDialog.setTitle("???????????????");

                normalDialog.setPositiveButton("??????", new DeleteOnClickListener());
                normalDialog.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                normalDialog.show();
            }
        }
    }

    class DeleteOnClickListener implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            DatabaseHelper databaseHelper = new DatabaseHelper(parentView.getContext(),"student",null,1);
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            String sql = "delete from student where id = \""+clickedStudentId+"\"";
            db.execSQL(sql);
            db.close();
            Log.i(TAG, "onClick: deleted id="+clickedStudentId);
            mInfoList.remove(findPositionById(clickedStudentId));
            notifyDataSetChanged();
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        parentView = parent.getRootView();
        // ??????????????? (Inflate) ?????????
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item,parent,false);
        view.setOnLongClickListener(new InfoOnLongClickListener());
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        StudentInfo studentInfo = mInfoList.get(position);
        holder.studentIdText.setText("?????????"+studentInfo.getId());
        holder.studentNameText.setText("?????????"+studentInfo.getName());
        holder.studentNativeText.setText("?????????"+studentInfo.getNative());
        holder.studentGenderText.setText("?????????"+studentInfo.getGender());
        // ????????????????????????????????????(??????????????????)
        String gpa = String.valueOf(studentInfo.getGpa())+"000";
        holder.studentGpaText.setText("GPA???"+gpa.substring(0,4));
        holder.studentCollegeText.setText("?????????"+studentInfo.getCollege());
        holder.studentAvatarImage.setImageBitmap(BlobUtil.blob2Bitmap(studentInfo.getAvatar()));
    }

    @Override
    public int getItemCount() {
        if (mInfoList==null) return 0;
        return mInfoList.size();
    }
}
