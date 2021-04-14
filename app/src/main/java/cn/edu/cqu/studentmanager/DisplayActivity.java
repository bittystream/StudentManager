package cn.edu.cqu.studentmanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import cn.edu.cqu.studentmanager.util.ListUtil;

import static android.service.controls.ControlsProviderService.TAG;

public class DisplayActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "DisplayActivity";
    private DatabaseHelper databaseHelper = null;
    private List<StudentInfo> infoList = null;
    private GridLayoutManager gridLayoutManager = null;
    private boolean [] sortSorts = null;
    private InfoAdapter infoAdapter = null;

    private String [] province = null;
    private String [] city = null;
    private String [] college = null;

    private String filterId, filterName, filterCollege, filterCity, filterProvince;
    private boolean isFiltered = false;

    private Spinner provinceSpinner, citySpinner, collegeSpinner;
    private EditText nameText, idText;
    private Button sortByIdButton, sortByNameButton, sortByGpaButton, filterButton, clearButton, submitButton;
    private RecyclerView recyclerView;
    private View popupWindowView;
    private PopupWindow popupWindow;

    private void init(){
        sortByIdButton = findViewById(R.id.sort_by_id);
        sortByNameButton = findViewById(R.id.sort_by_name);
        sortByGpaButton = findViewById(R.id.sort_by_gpa);
        filterButton = findViewById(R.id.filter_button);
        recyclerView = findViewById(R.id.recycler_view);
        popupWindowView = getLayoutInflater().from(this).inflate(R.layout.filter,null);


        provinceSpinner = popupWindowView.findViewById(R.id.filter_province);
        citySpinner = popupWindowView.findViewById(R.id.filter_city);
        collegeSpinner = popupWindowView.findViewById(R.id.filter_college);
        nameText = popupWindowView.findViewById(R.id.filter_name);
        idText = popupWindowView.findViewById(R.id.filter_id);
        clearButton = popupWindowView.findViewById(R.id.filter_clear_button);
        submitButton = popupWindowView.findViewById(R.id.filter_submit_button);

        province = getResources().getStringArray(R.array.province);
        city = getResources().getStringArray(R.array.def);
        college = getResources().getStringArray(R.array.college);

        sortByIdButton.setOnClickListener(this);
        sortByGpaButton.setOnClickListener(this);
        sortByNameButton.setOnClickListener(this);
        sortByGpaButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        clearButton.setOnClickListener(this);
        filterButton.setOnClickListener(this);
        provinceSpinner.setOnItemSelectedListener(new ProvinceSpinnerListener());
        popupWindow = new PopupWindow(this);


    }

    class ProvinceSpinnerListener implements Spinner.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Log.i(TAG, "onItemSelected: ");
            switch (province[i]) {
                case "-省份-":
                    city = getResources().getStringArray(R.array.def);
                    break;
                default:
                    // 获取对应省份string-array在arrays.xml中对应的id
                    int n = getResources().getIdentifier(province[i], "array", getPackageName());
                    city = getResources().getStringArray(n);
                    break;
            }
            filterProvince = province[i];
            Log.i(TAG, "onItemSelected: Select province as " + filterProvince);
            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, city);
            citySpinner.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }

    private void selectData(boolean hasCondition){
        String displayId, displayName, displayGender, displayNative, displayCollege;
        float displayGpa;
        byte [] displayAvatar;
        databaseHelper = new DatabaseHelper(this,"student",null,1);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        infoList = new ArrayList<StudentInfo>();
        String sql = "select * from student";
        String condition = "", idCondition = "", nameCondition = "", collegeCondition = "", provinceCondition = "", cityCondition = "";
        if (hasCondition){
            List<String> conditions = new ArrayList<>();
            if (filterId != null && filterId.length() > 0){
                idCondition = "id like \""+filterId+"%\"";
                conditions.add(idCondition);
            }
            if (filterName != null && filterName.length() > 0){
                nameCondition = "name like \""+filterName+"%\"";
                conditions.add(nameCondition);
            }
            if (!filterCollege.equals("-请选择-")){
                collegeCondition = "college = \""+filterCollege+"\"";
                conditions.add(collegeCondition);
            }
            if (!filterProvince.equals("-省份-")){
                provinceCondition = "province = \""+filterProvince+"\"";
                conditions.add(provinceCondition);
            }
            if (!filterCity.equals("-城市-")){
                cityCondition = "city = \""+filterCity+"\"";
                conditions.add(cityCondition);
            }
            if (conditions.size() > 0) condition = " where "+String.join(" and ",conditions);
            sql = sql + condition;
        }
        Log.i(TAG, "selectData: sql="+sql);
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.moveToFirst()){
            Log.i(TAG, "onCreate: cursor is not null");
            do {
                displayId = cursor.getString(cursor.getColumnIndex("id"));
                displayName = cursor.getString(cursor.getColumnIndex("name"));
                displayGender = cursor.getString(cursor.getColumnIndex("gender"));
                displayNative = cursor.getString(cursor.getColumnIndex("province"))
                        + cursor.getString(cursor.getColumnIndex("city"));
                displayGpa = cursor.getFloat(cursor.getColumnIndex("gpa"));
                displayAvatar = cursor.getBlob(cursor.getColumnIndex("avatar"));
                displayCollege = cursor.getString(cursor.getColumnIndex("college"));
                StudentInfo info = new StudentInfo(displayName,displayId,displayGender,displayCollege,displayNative,displayGpa,displayAvatar);
                infoList.add(info);
                Log.i(TAG, "onCreate: studentId="+displayId);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        Log.i(TAG, "selectData: total items = "+infoList.size());
        if (hasCondition) infoAdapter.notifyDataSetChanged();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        init();
        selectData(false);
        ListUtil.sortByIdAscending(infoList);
        sortSorts = new boolean[]{true, false, false};
        sortByIdButton.setTextColor(getResources().getColor(R.color.colorPrimary,null));

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sort_by_id:
                if (sortSorts[0]){
                    ListUtil.sortByIdDescending(infoList);
                    sortSorts[0] = false;
                    sortByIdButton.setText("学号↓");
                }
                else {
                    ListUtil.sortByIdAscending(infoList);
                    sortSorts[0] = true;
                    sortSorts[1] = false;
                    sortSorts[2] = false;
                    sortByIdButton.setText("学号↑");
                    sortByIdButton.setTextColor(getResources().getColor(R.color.colorPrimary,null));
                    sortByNameButton.setText("姓名");
                    sortByNameButton.setTextColor(Color.BLACK);
                    sortByGpaButton.setText("成绩");
                    sortByGpaButton.setTextColor(Color.BLACK);
                }
                if(!isFiltered) filterButton.setTextColor(Color.BLACK);
                else filterButton.setTextColor(getResources().getColor(R.color.colorPrimary,null));
                break;
            case R.id.sort_by_name:
                if (sortSorts[1]){
                    ListUtil.sortByNameDescending(infoList);
                    sortSorts[1] = false;
                    sortByNameButton.setText("姓名↓");
                }
                else {
                    ListUtil.sortByNameAscending(infoList);
                    sortSorts[1] = true;
                    sortSorts[0] = false;
                    sortSorts[2] = false;
                    sortByNameButton.setText("姓名↑");
                    sortByNameButton.setTextColor(getResources().getColor(R.color.colorPrimary,null));
                    sortByIdButton.setText("学号");
                    sortByIdButton.setTextColor(Color.BLACK);
                    sortByGpaButton.setText("成绩");
                    sortByGpaButton.setTextColor(Color.BLACK);
                }
                if(!isFiltered) filterButton.setTextColor(Color.BLACK);
                else filterButton.setTextColor(getResources().getColor(R.color.colorPrimary,null));
                break;
            case R.id.sort_by_gpa:
                if (sortSorts[2]){
                    ListUtil.sortByGpaDescending(infoList);
                    sortSorts[2] = false;
                    sortByGpaButton.setText("成绩↓");
                }
                else {
                    ListUtil.sortByGpaAscending(infoList);
                    sortSorts[2] = true;
                    sortSorts[1] = false;
                    sortSorts[0] = false;
                    sortByGpaButton.setText("成绩↑");
                    sortByGpaButton.setTextColor(getResources().getColor(R.color.colorPrimary,null));
                    sortByNameButton.setText("姓名");
                    sortByNameButton.setTextColor(Color.BLACK);
                    sortByIdButton.setText("学号");
                    sortByIdButton.setTextColor(Color.BLACK);
                }
                if(!isFiltered) filterButton.setTextColor(Color.BLACK);
                else filterButton.setTextColor(getResources().getColor(R.color.colorPrimary,null));
                break;
            case R.id.filter_button:
                popupWindow.setContentView(popupWindowView);
                popupWindow.setFocusable(true);
                ColorDrawable dw = new ColorDrawable(Color.WHITE);
                popupWindow.setBackgroundDrawable(dw);
                // 顺序很重要！'show' must be the last step
                popupWindow.showAsDropDown(findViewById(R.id.filter_button));
                Log.i(TAG, "onClick: popup window filter popped!");
                filterButton.setTextColor(getResources().getColor(R.color.colorPrimary,null));
                sortByIdButton.setText("学号");
                sortByIdButton.setTextColor(Color.BLACK);
                sortByNameButton.setText("姓名");
                sortByNameButton.setTextColor(Color.BLACK);
                sortByGpaButton.setText("成绩");
                sortByGpaButton.setTextColor(Color.BLACK);
                break;
            case R.id.filter_clear_button:
                idText.setText("");
                nameText.setText("");
                provinceSpinner.setSelection(0);
                collegeSpinner.setSelection(0);
                break;
            case R.id.filter_submit_button:
                isFiltered = true;
                filterId = idText.getText().toString();
                filterName = nameText.getText().toString();
                filterCollege = collegeSpinner.getSelectedItem().toString();
                filterProvince = provinceSpinner.getSelectedItem().toString();
                filterCity = citySpinner.getSelectedItem().toString();
                selectData(true);
                popupWindow.dismiss();
                if (!isFilterEmpty()) filterButton.setTextColor(getResources().getColor(R.color.colorPrimary,null));
                else {
                    isFiltered = false;
                    Log.i(TAG, "onClick: filter is not empty!");
                    filterButton.setTextColor(Color.BLACK);
                    ListUtil.sortByIdAscending(infoList);
                    sortSorts[0] = true;
                    sortSorts[1] = false;
                    sortSorts[2] = false;
                    sortByIdButton.setText("学号↑");
                    sortByIdButton.setTextColor(getResources().getColor(R.color.colorPrimary,null));
                    sortByNameButton.setText("姓名");
                    sortByNameButton.setTextColor(Color.BLACK);
                    sortByGpaButton.setText("成绩");
                    sortByGpaButton.setTextColor(Color.BLACK);
                }
                break;
        }

        onResume();
    }

    private boolean isFilterEmpty(){
        // 判断筛选区域是不是都是空的，如果没有筛选条件，则要改变按钮颜色！
        boolean idEmpty = filterId == null || filterId.length() < 1,
                nameEmpty = filterName == null || filterName.length() < 1,
                collegeEmpty = filterCollege == null || filterCollege.equals("-请选择-"),
                provinceEmpty = filterProvince == null || filterProvince.equals("-省份-"),
                cityEmpty = filterCity == null || filterCity.equals("-城市-");
        
        return idEmpty || nameEmpty || collegeEmpty || provinceEmpty || cityEmpty;
    }


    @Override
    protected void onResume() {
        super.onResume();

        gridLayoutManager = new GridLayoutManager(this,1,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);

        infoAdapter = new InfoAdapter(infoList,DisplayActivity.this);
        recyclerView.setAdapter(infoAdapter);
    }
}