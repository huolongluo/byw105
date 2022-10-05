package huolongluo.byw.byw.ui.activity.address;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.legend.common.util.StatusBarUtils;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.byw.manager.DialogManager;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.byw.ui.activity.countryselect.GetCountryNameSort;
import huolongluo.byw.byw.ui.activity.walletlist.NewWalletListActivity;
import huolongluo.byw.byw.ui.dialog.EditAddressDialog;
import huolongluo.byw.io.AppConstants;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.GsonUtil;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.RSACipher;
import huolongluo.byw.util.SPUtils;
import huolongluo.byw.util.adapter.LatterAdapter;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.util.tip.SnackBarUtils;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import okhttp3.Request;

/**
 * Created by LS on 2018/7/11.
 * 地址管理页面
 */

public class AdressManageActivity extends SwipeBackActivity {
    private AdressManageAdapter manageAdapter;
    private List<AddressListBean> mData = new ArrayList<>();
    private List<AddressListBean> mData1 = new ArrayList<>();
    private EditAddressDialog dialog = null;
    private GetCountryNameSort countryChangeUtil;
    private AddressComparator pinyinComparator;

    @BindView(R.id.lv_content)
    ListView listView;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refresh_layout;

    @BindView(R.id.no_data_tv)
    RelativeLayout no_data_tv; // 没有数据
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.back_iv)
    ImageView back_iv;
    @BindView(R.id.hide_ll)
    LinearLayout hide_ll;
    @BindView(R.id.hide_iv)
    ImageView hide_iv;

    boolean isAll = true;

    @BindView(R.id.net_error_view)
    FrameLayout net_error_view;

    @BindView(R.id.btn_reLoad)
    Button btn_reLoad;
    @BindView(R.id.right_listview)
    ListView right_listview;

    private LatterAdapter lattetAdapter;
    Map<String, Integer> mapHide = new HashMap<>();
    Map<String, Integer> map = new HashMap<>();
    private List<String> stringListHide = new ArrayList<>();
    private List<String> stringList = new ArrayList<>();
/*
    @BindView(R.id.ll_back)
    LinearLayout back;
   @BindView(R.id.ll_choose)
    LinearLayout ll_add;*/

    /**
     * bind layout resource file
     */
    private SwipeBackLayout mSwipeBackLayout;
    Unbinder unbinder;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置状态栏背景及字体颜色
        StatusBarUtils.setStatusBar(this);
        setContentView(R.layout.activity_address_manger);
        unbinder = ButterKnife.bind(this);


        mSwipeBackLayout = getSwipeBackLayout();

        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.setEdgeSize(200);//

        title_tv.setText(getString(R.string.address_man));
        pinyinComparator = new AddressComparator();
        countryChangeUtil = new GetCountryNameSort();

        getData();
        // 设置下拉进度的主题颜色
        refresh_layout.setColorSchemeResources(R.color.FF1FCEC2, R.color.F74D47, R.color.E39F72);
        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        refresh_layout.setOnRefreshListener(() ->
        {
            // 开始刷新，设置当前为刷新状态
            //swipeRefreshLayout.setRefreshing(true);


            getData();
            // 这个不能写在外边，不然会直接收起来
            //swipeRefreshLayout.setRefreshing(false);
        });

        btn_reLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });

        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        hide_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAll) {
                    isAll = false;
                    hide_iv.setImageResource(R.drawable.ic_address_selected);
                    lattetAdapter.setLettterList(stringListHide);
                    manageAdapter.FreshData(mData1);
                } else {
                    isAll = true;
                    hide_iv.setImageResource(R.drawable.ic_address_normal);
                    lattetAdapter.setLettterList(stringList);
                    manageAdapter.FreshData(mData);
                }
            }
        });
        manageAdapter = new AdressManageAdapter(this, mData, deleteListener, editListener, editListener1);
        listView.setAdapter(manageAdapter);


        listView.setEmptyView(no_data_tv);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AddressListBean bean = manageAdapter.getItem((int) id);
                Intent intent = new Intent(AdressManageActivity.this, NewWalletListActivity.class);

                intent.putExtra("shortName", bean.getShortName());
                intent.putExtra("fromClass", "AdressManageActivity");
                intent.putExtra("id", bean.getId());
                startActivity(intent);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == firstVisibleItem) {
                    return;
                }
                if (manageAdapter != null && manageAdapter.getCount() > firstVisibleItem) {
                    AddressListBean bean = manageAdapter.getItem(firstVisibleItem);
                    String name = bean.getShortName();
                    if (!TextUtils.isEmpty(name)) {
                        String firsetLitter = name.substring(0, 1).toLowerCase();
                        int posotion ;
                        if(!isAll){
                            posotion= stringListHide.indexOf(firsetLitter);
                        }else{
                            posotion= stringList.indexOf(firsetLitter);
                        }
                        if (lattetAdapter != null) {
                            lattetAdapter.setPosition(posotion);
                            lattetAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        lattetAdapter = new LatterAdapter();
        right_listview.setAdapter(lattetAdapter);
        right_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lattetAdapter != null && lattetAdapter.getCount() > position) {
                    String letter = lattetAdapter.getItem(position);
                    int positions;
                    if(!isAll){
                        positions= mapHide.get(letter);
                    }else{
                        positions= map.get(letter);
                    }
                    if (manageAdapter != null && manageAdapter.getCount() > positions) {
                        listView.setSelection(positions);
                        lattetAdapter.setPosition(position);
                        lattetAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }


    AdressManageAdapter.OperationClickListener2 editListener1 = new AdressManageAdapter.OperationClickListener2() {
        @Override
        public void edit1(int position, int i) {
//            getData();
            final EditAddressDialog dialog1 = new EditAddressDialog(AdressManageActivity.this);

            dialog1.setPositiveListener(new EditAddressDialog.DialogPositiveListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick() {
                    //Toast.show(BalanceActivity.this, "确定");
                    String info = dialog1.etInfo.getText().toString();
                    if (info.equals("")) {
                        MToast.show(AdressManageActivity.this, getString(R.string.empty_bz), 2);
//                        dialog1.initDialog("").dismiss();
                    } else {
                        String loginToken = SPUtils.getLoginToken();
                        String id = mData.get(i).getCoinAddress().get(position).getId();
                        Edit(loginToken, id, info);
//                        dialog1.initDialog("").dismiss();
                    }
                }
            });
            dialog1.initDialog("").show();
            dialog = dialog1;
//            getData();
        }
    };

    AdressManageAdapter.OperationClickListener1 editListener = new AdressManageAdapter.OperationClickListener1() {
        @Override
        public void edit(int position, int i) {
//            getData();
            final EditAddressDialog dialog1 = new EditAddressDialog(AdressManageActivity.this);

            dialog1.setPositiveListener(new EditAddressDialog.DialogPositiveListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick() {
                    //Toast.show(BalanceActivity.this, "确定");
                    String info = dialog1.etInfo.getText().toString();
                    if (info.equals("")) {
                        MToast.showButton(AdressManageActivity.this, getString(R.string.empty_bz), 2);
//                        dialog1.initDialog("").dismiss();
                    } else {
                        String loginToken = SPUtils.getLoginToken();
                        String id = mData.get(i).getCoinAddress().get(position).getId();
                        Edit(loginToken, id, info);
//                        dialog1.initDialog("").dismiss();
                    }
                }
            });
            dialog1.initDialog("").show();
            dialog = dialog1;
//            getData();
        }
    };

    AdressManageAdapter.OperationClickListener deleteListener = new AdressManageAdapter.OperationClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void delete(final int position, int i) {
            String loginToken = SPUtils.getLoginToken();
            String id = mData.get(i).getCoinAddress().get(position).getId();
            Delete(loginToken, id);
        }
    };

    //删除提币地址
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void Delete(String loginToken, String id) {
        HashMap<String, String> pamars = new HashMap<>();
        RSACipher rsaCipher = new RSACipher();
        String body = "id=" + URLEncoder.encode(id);
        try {
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            pamars.put("body", body1);
            pamars.put("loginToken", loginToken);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

//        pamars.put("id",id);

        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.DETELE_ADD, pamars, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {

            }

            @Override
            public void requestSuccess(String result) {
                try {
                    JSONObject jsonObject = new org.json.JSONObject(result);
                    boolean result1 = jsonObject.getBoolean("result");
                    int code = jsonObject.getInt("code");
                    String value = jsonObject.getString("value");
                    if (result1) {
                        //   showMessage("删除成功",1);
                        SnackBarUtils.ShowBlue(AdressManageActivity.this, getString(R.string.delect_suc));
                        getData();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //虚拟币提现地址备注修改
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void Edit(String loginToken, String adressId, String remark) {
        HashMap<String, String> pamars = new HashMap<>();
        RSACipher rsaCipher = new RSACipher();
        String body = "adressId=" + URLEncoder.encode(adressId) + "&remark=" + URLEncoder.encode(remark);
        try {
            String body1 = rsaCipher.encrypt(body, AppConstants.KEY.PUBLIC_KEY);
            pamars.put("loginToken", loginToken);
            pamars.put("body", body1);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }


//        pamars.put("adressId",adressId);
//        pamars.put("remark",remark);

        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.EDIT_ADD, pamars, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {

            }

            @Override
            public void requestSuccess(String result) {
                if (dialog != null) {
                    dialog.dialog.dismiss();
                }
                try {
                    JSONObject jsonObject = new org.json.JSONObject(result);
                    boolean result1 = jsonObject.getBoolean("result");
                    int code = jsonObject.getInt("code");
                    String value = jsonObject.getString("value");
                    if (result1) {
                        //   showMessage("修改成功",1);
                        SnackBarUtils.ShowBlue(AdressManageActivity.this, getString(R.string.change_suc));
                        dialog.initDialog("").dismiss();
                        getData();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getData() {

        DialogManager.INSTANCE.showProgressDialog(this, getString(R.string.load));

        Map<String, String> params = new HashMap<>();
        params.put("type", "1");
        params = OkhttpManager.encrypt(params);

        params.put("loginToken", UserInfoManager.getToken());

        OkhttpManager.postAsync(UrlConstants.DOMAIN + UrlConstants.app_GET_ALL_ADD, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                DialogManager.INSTANCE.dismiss();
                if (refresh_layout.isRefreshing()) {
                    refresh_layout.setRefreshing(false);

                }
                SnackBarUtils.ShowRed(AdressManageActivity.this, errorMsg);
                net_error_view.setVisibility(View.VISIBLE);
            }

            @Override
            public void requestSuccess(String result) {
                DialogManager.INSTANCE.dismiss();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (isDestroyed()) {
                        return;
                    }
                }
                if (isFinishing()) {
                    return;
                }
                AddressBean listBean = null;
                net_error_view.setVisibility(View.GONE);


                try {
                    listBean = GsonUtil.json2Obj(result, AddressBean.class);
                    if (listBean.getCode() == 0) {


                        mData.clear();
                        mData1.clear();
                        if (listBean.isResult()) {

                            if (refresh_layout.isRefreshing()) {
                                refresh_layout.setRefreshing(false);
                                SnackBarUtils.ShowBlue(AdressManageActivity.this, getString(R.string.reslas));
                            }
                            mData = listBean.getFvirtualcointype();
                            if (mData.isEmpty() || mData.size() == 0) {
                                listView.setVisibility(View.GONE);

                            } else {
                                stringList.clear();
                                stringListHide.clear();
                                map.clear();
                                mapHide.clear();
                                //根据字母排序
                                for (int i = 0; i < mData.size(); i++) {
                                    AddressListBean bean=mData.get(i);
                                    bean.setSortLetters(countryChangeUtil.getSortLetterBySortKey(bean.getShortName()));
                                }
                                Collections.sort(mData, pinyinComparator);
                                // TODO: 2017/12/29 显示数据
                                for (int i = 0; i < mData.size(); i++) {
                                    AddressListBean bean=mData.get(i);
                                    String firstLitter = bean.getShortName().substring(0, 1).toLowerCase();
                                    if (!map.containsKey(firstLitter)) {
                                        stringList.add(firstLitter);
                                        map.put(firstLitter, i);
                                    }
                                    if (bean.getCoinAddress().size() > 0) {
                                        mData1.add(bean);
                                    }
                                }
                                for (int i = 0; i <mData1.size() ; i++) {
                                    String firstLitter = mData1.get(i).getShortName().substring(0, 1).toLowerCase();
                                    if (!mapHide.containsKey(firstLitter)) {
                                        stringListHide.add(firstLitter);
                                        mapHide.put(firstLitter, i);
                                    }
                                }
                                if (isAll) {
                                    listView.setVisibility(View.VISIBLE);
                                    lattetAdapter.setLettterList(stringList);
                                    manageAdapter.FreshData(mData);
                                } else {
                                    listView.setVisibility(View.VISIBLE);
                                    lattetAdapter.setLettterList(stringListHide);
                                    manageAdapter.FreshData(mData1);
                                }
                            }
                        } else {
                            if (refresh_layout.isRefreshing()) {
                                refresh_layout.setRefreshing(false);
                                SnackBarUtils.ShowRed(AdressManageActivity.this, getString(R.string.refresh_fail));
                            }
                        }
                    } else {
                        MToast.show(AdressManageActivity.this, getString(R.string.linef), 1);
                    }
                } catch (Exception e) {
                    if (refresh_layout.isRefreshing()) {
                        refresh_layout.setRefreshing(false);
                        SnackBarUtils.ShowRed(AdressManageActivity.this, getString(R.string.service_expec));
                    }

                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //   getData();
    }
}
