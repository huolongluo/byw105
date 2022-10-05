package huolongluo.byw.reform.mine.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.legend.common.util.StatusBarUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.tip.DialogUtils;
import huolongluo.byw.util.tip.MToast;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * Created by hy on 2018/12/18 0018.
 */

public class BindHPayActivity extends SwipeBackActivity {

    Unbinder unbinder;

    @BindView(R.id.back_iv)
    ImageButton back_iv;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.bind_bn)
    Button bind_bn;

    private SwipeBackLayout mSwipeBackLayout;

    String uniqueId=null;
    String appId=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏背景及字体颜色
        StatusBarUtils.setStatusBar(this);
        setContentView(R.layout.activity_bind_hpay);
        unbinder = ButterKnife.bind(this);


      //  intent.putExtra("uniqueId", bindHpyBean.getUniqueId());
       // intent.putExtra("appId", bindHpyBean.getAppId());

        uniqueId=getIntent().getStringExtra("uniqueId");
        appId=getIntent().getStringExtra("appId");

        mSwipeBackLayout = getSwipeBackLayout();

        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.setEdgeSize(200);//

        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title_tv.setText(R.string.qs1);
        bind_bn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //startActivity(new Intent(BindHPayActivity.this,BindHPaySuccessActivity.class));

                      checkApp();

            }
        });

    }





    void checkApp(){
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        String packageName=null;
           for(PackageInfo infos:packageInfos){

               Log.i("infos","="+infos.versionCode);

               if(TextUtils.equals(infos.packageName, UrlConstants.hppPackapeNameo)){

                   if(infos.versionCode<UrlConstants.hppVerCode){
                       showInstallDoalog(getString(R.string.qs2), getString(R.string.qs3));
                       return;
                   }

                   packageName=infos.packageName;
               }else if(TextUtils.equals(infos.packageName, UrlConstants.hppPackapeNamet)){
                   packageName=infos.packageName;
                //   startActivity(new Intent(BindHPayActivity.this,BindHPaySuccessActivity.class));

                   if(infos.versionCode<UrlConstants.hppVerCode){
                       showInstallDoalog(getString(R.string.qs4), getString(R.string.qs5));
                       return;
                   }

               }
               if(!TextUtils.isEmpty(packageName)){
                   showOpenDialog(packageName);
                   //   MToast.show(BindHPayActivity.this,"已安装",1);
                   return;
               }
           }

        showInstallDoalog(getString(R.string.nHyperPay), getString(R.string.qs6));

    }
    void showOpenDialog(String pagName){
        DialogUtils.getInstance().showTwoButtonDialog(this, getString(R.string.qs7), getString(R.string.qs8), getString(R.string.qs9));
        DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
            @Override
            public void onLiftClick(AlertDialog dialog, View view) {
                if(dialog!=null){
                    dialog.dismiss();
                }
            }

            @Override
            public void onRightClick(AlertDialog dialog, View view) {
                if(dialog!=null){
                    dialog.dismiss();
                }
                try {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    //前提：知道要跳转应用的包名、类名
                    // ComponentName componentName = new ComponentName("com.legendwd.hcash", "com.legendwd.hcash.main.login.LoginActivity");
                    ComponentName componentName = new ComponentName(pagName, "com.legendwd.hyperpay"+".main.coinw.CoinwBindHPYActivity");
                    intent.setComponent(componentName);
                    // String uniqueId=null;
                    // String appId=null;
                    intent.putExtra("uniqueId", uniqueId);
                    intent.putExtra("appId", appId);
                    intent.putExtra("userName", UserInfoManager.getUserInfo().getLoginName());
                    startActivityForResult(intent,101);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==101){

            if(data!=null){
                boolean status=data.getBooleanExtra("status",false);
                if(status){
                    MToast.show(this, getString(R.string.qs10), 1);

                    Intent intent=new Intent(this,BindHPaySuccessActivity.class);
                    intent.putExtra("appId", appId);
                    startActivity(intent);

                     finish();
                }else {
                    MToast.show(this, getString(R.string.qs11), 1);
                }

            }
        }


    }

    void showInstallDoalog(String text,String rightText){

        DialogUtils.getInstance().showTwoButtonDialog(this, text, getString(R.string.qs12), rightText);
        DialogUtils.getInstance().setOnclickListener(new DialogUtils.onBnClickListener() {
            @Override
            public void onLiftClick(AlertDialog dialog, View view) {
                if(dialog!=null){
                    dialog.dismiss();
                }
            }

            @Override
            public void onRightClick(AlertDialog dialog, View view) {
                if(dialog!=null){
                    dialog.dismiss();
                }
                Intent intent = new Intent();
                intent.setData(Uri.parse(UrlConstants.getDownloadHpp(BindHPayActivity.this)));//Url 就是你要打开的网址
                intent.setAction(Intent.ACTION_VIEW);

                List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);
                Log.i("版本升级", "ResolveInfo= size:" + list.size());
                for (ResolveInfo info : list) {
                    Log.i("版本升级", "ResolveInfo= :" + info.loadLabel(getPackageManager()).toString());
                }

                if (list.size() > 0) {
                    startActivity(intent); //启动浏览器
                    ///浏览器存在
                }
            }
        });
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();



        if(unbinder!=null){
            unbinder.unbind();
        }
    }
}
