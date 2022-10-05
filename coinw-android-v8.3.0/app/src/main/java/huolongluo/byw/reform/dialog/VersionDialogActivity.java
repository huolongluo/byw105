package huolongluo.byw.reform.dialog;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.List;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.ui.activity.UpVersionActivity;

/**
 * Created by dell on 2019/7/17.
 */

public class VersionDialogActivity extends Activity {


String Android_version;
int Force;
String update_instructions;
String Android_downurl;
    private TextView version_name_tv;
    private TextView instructions_tv;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_versiondialog);

        if (getIntent() != null) {
            /*intent.putExtra("Android_version",versionInfo.getAndroid_version());
            intent.putExtra("Force",versionInfo.getForce());
            intent.putExtra("update_instructions",versionInfo.getAdd_update_instructions());
            intent.putExtra("Android_downurl",versionInfo.getAndroid_downurl());*/

            Android_version=getIntent().getStringExtra("Android_version");
            Force=getIntent().getIntExtra("Force",0);
            update_instructions=getIntent().getStringExtra("update_instructions");
            Android_downurl=getIntent().getStringExtra("Android_downurl");

        }
        TextView tv_ok = findViewById(R.id.tv_ok);
        TextView tv_ok_1 = findViewById(R.id.tv_ok_1);
        TextView tv_cancle = findViewById(R.id.tv_cancle);
        version_name_tv = findViewById(R.id.version_name_tv);
        instructions_tv = findViewById(R.id.instrucdddtions_tv);

        // version_name_tv.setText("Android: " +"aaaddaaa");
            version_name_tv.setText("Android: " + Android_version+"");
           instructions_tv.setText(update_instructions + "");
         //   instructions_tv.setText(  "bbb");


            if (Force == 1) {

                tv_cancle.setVisibility(View.GONE);
                tv_ok.setVisibility(View.GONE);
                tv_ok_1.setVisibility(View.VISIBLE);

            } else {

                tv_cancle.setVisibility(View.VISIBLE);
                tv_ok.setVisibility(View.VISIBLE);
                tv_ok_1.setVisibility(View.GONE);
            }


        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDownload();
            }
        });
        tv_ok_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDownload();
            }
        });

        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 finish();

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseApp.UPDATAING = false;
    }

    @Override
    public void onBackPressed() {

        if(Force!=1){
            super.onBackPressed();
        }


    }

    void toDownload(){


        Intent intent = new Intent();
        intent.setData(Uri.parse(Android_downurl));//Url 就是你要打开的网址
        intent.setAction(Intent.ACTION_VIEW);

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
        Log.i("版本升级", "ResolveInfo= size:" + list.size());
        for (ResolveInfo info : list) {
            Log.i("版本升级", "ResolveInfo= :" + info.loadLabel(getPackageManager()).toString());
        }

        if (list.size() > 0) {
            startActivity(intent); //启动浏览器
            ///浏览器存在
        } else {
            ///浏览器不存在
            Bundle bundle = new Bundle();
            bundle.putString("downUrl", Android_downurl);


            intent.putExtra("bundle", bundle);
            Intent intent1=new Intent(this,UpVersionActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent1);
        }
    }
}
