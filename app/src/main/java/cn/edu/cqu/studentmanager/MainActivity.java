package cn.edu.cqu.studentmanager;

import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button gotoInputButton = findViewById(R.id.goto_input);
        Button gotoDisplayButton = findViewById(R.id.goto_display);

        gotoInputButton.setOnClickListener(this);
        gotoDisplayButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.goto_input:
                Intent intent0 = new Intent(MainActivity.this, InputActivity.class);
                startActivity(intent0);
                break;
            case R.id.goto_display:
                Intent intent = new Intent(MainActivity.this, DisplayActivity.class);
                startActivity(intent);
                break;
        }
    }
}