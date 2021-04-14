package cn.edu.cqu.studentmanager;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import cn.edu.cqu.studentmanager.util.BlobUtil;

public class InputActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "InputActivity";
    private Spinner provinceSpinner, citySpinner, collegeSpinner;
    private LinearLayout avaterItem;
    private Button inputSubmitButton;
    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton, femaleRadioButton;
    private EditText nameText, idText, gpaText;
    private ImageView avaterImage;

    private String studentName;
    private String studentGender;
    private String studentId;
    private String studentProvince;
    private String studentCity;
    private String studentCollege;
    private float studentGpa;
    private Bitmap studentAvatarBitmap;
    private byte [] studentAvatar;

    private String [] province = null;
    private String [] city = null;
    private String [] college = null;

    private SQLiteOpenHelper sqLiteOpenHelper = null;
    private SQLiteDatabase db = null;


    // 动态获取相机权限
    private void getCameraPermission(){
        String [] permissions = new String[] {
                Manifest.permission.CAMERA
        };
        ActivityCompat.requestPermissions(InputActivity.this,permissions,100);
    }

    // 动态获取相册访问权限
    private void getAlbumPermission(){
        String [] permissions = new String[] {
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(InputActivity.this,permissions,100);
    }

   private void init(){
        avaterItem = findViewById(R.id.avatar_item);
        inputSubmitButton = findViewById(R.id.input_submit);
        provinceSpinner = findViewById(R.id.student_province);
        citySpinner = findViewById(R.id.student_city);
        collegeSpinner = findViewById(R.id.student_college);
        genderRadioGroup = findViewById(R.id.student_gender);
        maleRadioButton = findViewById(R.id.student_gender_male);
        femaleRadioButton = findViewById(R.id.student_gender_female);
        avaterImage = findViewById(R.id.avatar_image);

        nameText = findViewById(R.id.student_name);
        idText = findViewById(R.id.student_id);
        gpaText = findViewById(R.id.student_gpa);

        genderRadioGroup.setOnCheckedChangeListener(new GenderRadioButtonListener());

        province = getResources().getStringArray(R.array.province);
        city = getResources().getStringArray(R.array.def);
        college = getResources().getStringArray(R.array.college);

        provinceSpinner.setOnItemSelectedListener(new ProvinceSpinnerListener());
        citySpinner.setOnItemSelectedListener(new CitySpinnerListener());
        collegeSpinner.setOnItemSelectedListener(new CollegeSpinnerListener());
   }

   // 显示“上传头像”对话框
   private void avatarDialog(){
       String [] items = {"拍照","从手机相册选择"};
       AlertDialog.Builder listDialog = new AlertDialog.Builder(InputActivity.this);
       listDialog.setTitle("上传头像");
       listDialog.setItems(items,new AvatarOnClickListener());
       listDialog.show();
   }

   class AvatarOnClickListener implements DialogInterface.OnClickListener{
       @Override
       public void onClick(DialogInterface dialogInterface, int i) {
           if (i == 0){
//               Toast.makeText(InputActivity.this,"拍照",Toast.LENGTH_SHORT).show();
               getCameraPermission();
               Intent intent = new Intent();
               intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
               startActivityForResult(intent,1);
           }
           else{
//               Toast.makeText(InputActivity.this,"从手机相册选择",Toast.LENGTH_SHORT).show();
               getAlbumPermission();
               Intent intent = new Intent(Intent.ACTION_PICK,null);
               intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
               startActivityForResult(intent, 2);
           }
       }
   }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        init();
        avaterItem.setOnClickListener(this);
        inputSubmitButton.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 上传头像处理
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                studentAvatarBitmap = (Bitmap)data.getExtras().get("data");
                avaterImage.setImageBitmap(studentAvatarBitmap);
            }
        }
        else if (requestCode == 2){
            if (data != null){
                Uri uri = data.getData();
                avaterImage.setImageURI(uri);
                // 从 ImageView 拿到 Bitmap
                studentAvatarBitmap = ((BitmapDrawable)avaterImage.getDrawable()).getBitmap();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.avatar_item:
//                Toast.makeText(this,"修改头像",Toast.LENGTH_SHORT).show();
                avatarDialog();

                break;
            case R.id.input_submit:
                // 此处处理没有填写完信息就点提交按钮的情况
                if (studentAvatarBitmap == null){
                    Toast.makeText(this,"还未上传头像！",Toast.LENGTH_SHORT).show();
                    break;
                }
                studentAvatar = BlobUtil.bitmap2Blob(studentAvatarBitmap);
                studentId = idText.getText().toString();
                if (studentId == null || studentId.length() < 1){
                    Toast.makeText(this,"学号不能为空！",Toast.LENGTH_SHORT).show();
                    break;
                }
                studentName = nameText.getText().toString();
                if (studentName == null || studentName.length() < 1){
                    Toast.makeText(this,"姓名不能为空！",Toast.LENGTH_SHORT).show();
                    break;
                }
                if (studentGender == null){
                    Toast.makeText(this,"还未选择性别！",Toast.LENGTH_SHORT).show();
                    break;
                }
                if (studentProvince.equals("-省份-")){
                    Toast.makeText(this, "还未选择籍贯！", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (studentCity.equals("-城市-")){
                    Toast.makeText(this, "还未选择籍贯城市！", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (studentCollege.equals("-请选择-")){
                    Toast.makeText(this,"还未选择学院！",Toast.LENGTH_SHORT).show();
                    break;
                }
                String gpa = gpaText.getText().toString();
                if (gpa == null || gpa.length() < 1) {
                    Toast.makeText(this,"成绩不能为空！",Toast.LENGTH_SHORT).show();
                    break;
                }
                studentGpa = Float.parseFloat(gpa);


                // 存入数据库操作
                sqLiteOpenHelper = new DatabaseHelper(this,"student",null,1);
                db = sqLiteOpenHelper.getWritableDatabase();
                ContentValues contentValues = new ContentValues();
                contentValues.put("id",studentId);
                contentValues.put("name",studentName);
                contentValues.put("gender",studentGender);
                contentValues.put("province",studentProvince);
                contentValues.put("city",studentCity);
                contentValues.put("college",studentCollege);
                contentValues.put("gpa",(float)studentGpa);
                contentValues.put("avatar",studentAvatar);

                db.insert("student",null,contentValues);
                db.close();
                Toast.makeText(this,"提交成功",Toast.LENGTH_LONG).show();
                // 信息填写完提交后返回主界面
                finish();
                break;
        }
    }

    class GenderRadioButtonListener implements RadioGroup.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            switch(i){
                case R.id.student_gender_male:
                    studentGender = "男";
                    Log.i(TAG, "onCheckedChanged: Select gender as Male.");
                    break;
                case R.id.student_gender_female:
                    studentGender = "女";
                    Log.i(TAG, "onCheckedChanged: Select gender as Female.");
                    break;
            }
        }
    }
    class CitySpinnerListener implements Spinner.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            studentCity = city[i];
            Log.i(TAG, "onItemSelected: Select city as "+studentCity);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
    class CollegeSpinnerListener implements Spinner.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            studentCollege = college[i];
            Log.i(TAG, "onItemSelected: Select college as "+studentCollege);
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }

    class ProvinceSpinnerListener implements Spinner.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Log.i(TAG, "onItemSelected: ");
            switch (province[i]){
                case "-省份-":
                    city = getResources().getStringArray(R.array.def);
                    break;
                default:
                    // 获取对应省份string-array在arrays.xml中对应的id
                    int n = getResources().getIdentifier(province[i],"array",getPackageName());
                    city = getResources().getStringArray(n);
                    break;
            }
            studentProvince = province[i];
            Log.i(TAG, "onItemSelected: Select province as "+studentProvince);
            ArrayAdapter arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,city);
            citySpinner.setAdapter(arrayAdapter);
        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
        }
    }
}