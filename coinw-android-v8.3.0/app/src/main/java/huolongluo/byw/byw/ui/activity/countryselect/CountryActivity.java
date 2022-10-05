package huolongluo.byw.byw.ui.activity.countryselect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import huolongluo.byw.R;



/**
 * 类简要描述
 * <p/>
 * <p>
 * 类详细描述
 * </p>
 *
 * @author duanbokan
 */
public class CountryActivity extends Activity {

    String TAG = "CountryActivity";

    private List<CountrySortModel> mAllCountryList;

    private EditText country_edt_search;

    private ListView country_lv_countryList;

    private ImageView country_iv_clearText;

    private CountrySortAdapter adapter;

    private SideBar sideBar;

    private TextView dialog;

    private CountryComparator pinyinComparator;

    private GetCountryNameSort countryChangeUtil;

    private CharacterParserUtil characterParserUtil;

    public static final int COUNTRY_REQUEST_CODE = 10006;

    public int areaType2 = 0;

    private LinearLayout ll_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.coogame_country);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
           areaType2 = bundle.getInt("area_code_2");
        }

        initView();

        setListener();

        getCountryList();

    }

    /**
     * 初始化界面
     */
    private void initView() {
        country_edt_search = findViewById(R.id.country_et_search);
        country_lv_countryList = findViewById(R.id.country_lv_list);
        country_iv_clearText = findViewById(R.id.country_iv_cleartext);

        dialog = findViewById(R.id.country_dialog);
        sideBar = findViewById(R.id.country_sidebar);
        sideBar.setTextView(dialog);

        ll_back = findViewById(R.id.ll_back);
        ll_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAllCountryList = new ArrayList<CountrySortModel>();
        pinyinComparator = new CountryComparator();
        countryChangeUtil = new GetCountryNameSort();
        characterParserUtil = new CharacterParserUtil();

        // 将联系人进行排序，按照A~Z的顺序
        Collections.sort(mAllCountryList, pinyinComparator);
        adapter = new CountrySortAdapter(this, mAllCountryList);
        country_lv_countryList.setAdapter(adapter);

    }

    /****
     * 添加监听
     */
    private void setListener() {
        country_iv_clearText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                country_edt_search.setText("");
                Collections.sort(mAllCountryList, pinyinComparator);
                adapter.updateListView(mAllCountryList);
            }
        });

        country_edt_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchContent = country_edt_search.getText().toString();
                if (searchContent.equals("")) {
                    country_iv_clearText.setVisibility(View.INVISIBLE);
                } else {
                    country_iv_clearText.setVisibility(View.VISIBLE);
                }

                if (searchContent.length() > 0) {
                    // 按照输入内容进行匹配
                    ArrayList<CountrySortModel> fileterList = (ArrayList<CountrySortModel>) countryChangeUtil
                            .search(searchContent, mAllCountryList);

                    adapter.updateListView(fileterList);
                } else {
                    adapter.updateListView(mAllCountryList);
                }
                country_lv_countryList.setSelection(0);
            }
        });

        // 右侧sideBar监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    country_lv_countryList.setSelection(position);
                }
            }
        });

        country_lv_countryList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {
                String countryName = null;
                String countryNumber = null;
                String searchContent = country_edt_search.getText().toString();

                if (searchContent.length() > 0) {
                    // 按照输入内容进行匹配
                    ArrayList<CountrySortModel> fileterList = (ArrayList<CountrySortModel>) countryChangeUtil
                            .search(searchContent, mAllCountryList);
                    countryName = fileterList.get(position).countryName;
                    countryNumber = fileterList.get(position).countryNumber;

                } else {
                    // 点击后返回
                    countryName = mAllCountryList.get(position).countryName;
                    countryNumber = mAllCountryList.get(position).countryNumber;
                }

                Intent intent = new Intent();
           //     intent.setClass(CountryActivity.this, CameraMainActivity.class);
                intent.putExtra("countryName", countryName);
                intent.putExtra("countryNumber", countryNumber.substring(1));
//                RenZhengInfoActivity.countryName = countryName;

                if(areaType2 == 0){
//                    JPrefence.getInstance(getBaseContext()).setProperty("phone_area",countryNumber.substring(1));
                }else{
//                    JPrefence.getInstance(getBaseContext()).setProperty("phone_area_2",countryNumber.substring(1));
                }

                setResult(RESULT_OK, intent);
                Log.e(TAG, "countryName: + " + countryName + "countryNumber: " + countryNumber);

                // 退出键盘
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

                finish();

            }
        });

    }

    /**
     * 获取国家列表
     */
    private void getCountryList() {

        // 英文
        String[] countryList = getResources().getStringArray(R.array.country_code_list_en);
        // 中文
      //  String[] countryList = getResources().getStringArray(R.array.country_code_list_ch);

        // 默认
//        if("zh-ch".equals(JPrefence.getInstance(this).getProperty("KEY_LANG"))){
            countryList = getResources().getStringArray(R.array.country_code_list_ch);
//        }



        for (int i = 0, length = countryList.length; i < length; i++) {
            String[] country = countryList[i].split("\\*");

            String countryName = country[0];
            String countryNumber = country[1];
            String countrySortKey = characterParserUtil.getSelling(countryName);
            CountrySortModel countrySortModel = new CountrySortModel(countryName, countryNumber,
                    countrySortKey);
            String sortLetter = countryChangeUtil.getSortLetterBySortKey(countrySortKey);
            if (sortLetter == null) {
                sortLetter = countryChangeUtil.getSortLetterBySortKey(countryName);
            }

            countrySortModel.sortLetters = sortLetter;
            mAllCountryList.add(countrySortModel);
        }

        Collections.sort(mAllCountryList, pinyinComparator);
        adapter.updateListView(mAllCountryList);
        Log.e(TAG, "changdu" + mAllCountryList.size());
    }
}
