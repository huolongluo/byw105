package huolongluo.byw.reform.login_regist;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import huolongluo.byw.R;
import huolongluo.byw.reform.base.BaseActivity;

/**
 * Created by Administrator on 2019/1/2 0002.
 */
public class AreaCodeChoiceActivity extends BaseActivity {

    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.right_listview)
    ListView right_listview;
    @BindView(R.id.back_iv)
    ImageButton back_iv;
    @BindView(R.id.title_tv)
    TextView title_tv;
    @BindView(R.id.et_content)
    EditText et_content;
    @BindView(R.id.iv_clear_user)
    ImageView iv_clear_user;
    Unbinder unbinder;
    Map<String, Integer> map = new HashMap<>();
    private List<String> stringList = new ArrayList<>();
    private List<String> areaList = new ArrayList<>();
    private List<String> areaListAll = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_areacode_choice);
        unbinder = ButterKnife.bind(this);
        back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        title_tv.setText(R.string.ws3);
        Resources res = getResources();
        String[] areaCode = res.getStringArray(R.array.country_code_list_ch);
        String[] hot_areaCode = res.getStringArray(R.array.hot_country_code_list_ch);
        List<String> hotList = Arrays.asList(hot_areaCode);
        areaList = Arrays.asList(areaCode);
        Collections.sort(areaList);
        areaListAll.add(getString(R.string.ws1));
        areaListAll.addAll(hotList);
        areaListAll.addAll(areaList);
        //TixianAdapter adapter = new TixianAdapter(this, areaList);
        TixianAdapter adapter = new TixianAdapter(this, areaListAll);
        listView.setAdapter(adapter);
        for (int i = 0; i < areaList.size(); i++) {
            String firstLitter = areaList.get(i).substring(0, 1).toLowerCase();
            if (!map.containsKey(firstLitter)) {
                stringList.add(firstLitter);
                map.put(firstLitter, i + 10);
            }
        }
        LattetAdapter lattetAdapter = new LattetAdapter(stringList);
        right_listview.setAdapter(lattetAdapter);
        right_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lattetAdapter != null && lattetAdapter.getCount() > position) {
                    String letter = lattetAdapter.getItem(position);
                    Integer data = map.get(letter);
                    // java.lang.NullPointerException: Attempt to invoke virtual method 'int java.lang.Integer.intValue()' on a null object reference
                    if (data == null) {
                        return;
                    }
                    int positions = data.intValue();
                    if (adapter != null && adapter.getCount() > positions) {
                        listView.setSelection(positions);
                        lattetAdapter.setPosition(position);
                        lattetAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (adapter != null && adapter.getCount() > firstVisibleItem) {
                    String name = adapter.getItem(firstVisibleItem);
                    if (!TextUtils.isEmpty(name)) {
                        String firsetLitter = name.substring(0, 1).toLowerCase();
                        int posotion = stringList.indexOf(firsetLitter);
                        Log.i("weizhi", "posotion==  " + posotion + "    firs = " + firsetLitter);
                        if (lattetAdapter != null) {
                            lattetAdapter.setPosition(posotion);
                            lattetAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String code = adapter.getItem(position);
                if (code.contains("*")) {
                    String[] code1 = code.trim().replace("*", "#").split("#");
                    Intent intent = new Intent();
                    intent.putExtra("areaCode", code1[2]);
                    setResult(102, intent);
                    finish();
                }
            }
        });
        iv_clear_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_content.setText("");
                iv_clear_user.setVisibility(View.GONE);
            }
        });
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!TextUtils.isEmpty(text)) {
                    iv_clear_user.setVisibility(View.VISIBLE);
                    List<String> search = new ArrayList<>();
                    for (String areacode : areaList) {
                        if (areacode.contains(text)) {
                            search.add(areacode);
                        }
                    }
                    adapter.notifityData(search);
                } else {
                    adapter.notifityData(areaListAll);
                }
            }
        });
    }

    private static class TixianAdapter extends BaseAdapter {

        Context context;
        private List<String> mAssetcoinsList = new ArrayList<>();

        public TixianAdapter(Context context, List<String> mAssetcoinsList) {
            this.context = context;
            this.mAssetcoinsList.addAll(mAssetcoinsList);
        }

        public TixianAdapter() {
            super();
        }

        @Override
        public int getItemViewType(int position) {
            if (!mAssetcoinsList.get(position).contains("*")) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        public void notifityData(List<String> mAssetcoinsList) {
            this.mAssetcoinsList.clear();
            this.mAssetcoinsList.addAll(mAssetcoinsList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mAssetcoinsList.size();
        }

        @Override
        public String getItem(int position) {
            if (position < 0 || mAssetcoinsList == null || mAssetcoinsList.isEmpty()) {
                return "";
            }
            return mAssetcoinsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String bean = mAssetcoinsList.get(position);
            if (getItemViewType(position) == 0) {
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.areacode_titleitem, null);
                }
                TextView area_name_tv = convertView.findViewById(R.id.area_name_tv);
                if (bean.length() == 1) {
                    area_name_tv.setText(bean.toUpperCase());
                } else {
                    area_name_tv.setText(bean);
                }
            } else {
                if (convertView == null) {
                    convertView = View.inflate(parent.getContext(), R.layout.areacode_item, null);
                }
                TextView country_tv = convertView.findViewById(R.id.country_tv);
                TextView areaCode_tv = convertView.findViewById(R.id.areaCode_tv);
                if (bean.contains("*")) {
                    String[] s = bean.trim().replace("*", "#").split("#");
                    country_tv.setText(s[1]);
                    areaCode_tv.setText(s[2]);
                } else {
                    country_tv.setText(bean);
                }
            }
            return convertView;
        }
    }

    private class LattetAdapter extends BaseAdapter {

        int position;
        List<String> lettterList = new ArrayList<>();

        public LattetAdapter(List<String> lettterList) {
            this.lettterList.clear();
            this.lettterList.addAll(lettterList);
        }

        public void setAdata(List<String> lettterList) {
            this.lettterList.clear();
            this.lettterList.addAll(lettterList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return lettterList.size();
        }

        @Override
        public String getItem(int position) {
            if (position <= 0 || lettterList == null || lettterList.isEmpty()) {
                return "";
            }
            return lettterList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.tixian_list_item1, null);
            }
            TextView tixian_dot = convertView.findViewById(R.id.textView);
            tixian_dot.setText(lettterList.get(position));
            if (this.position == position) {
                tixian_dot.setBackgroundResource(R.drawable.tixian_dot);
                tixian_dot.setTextColor(Color.WHITE);
            } else {
                tixian_dot.setBackgroundColor(getResources().getColor(R.color.transparent));
                tixian_dot.setTextColor(getResources().getColor(R.color.ff262046));
            }
            return convertView;
        }

        public void setPosition(int posotion) {
            position = posotion;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
