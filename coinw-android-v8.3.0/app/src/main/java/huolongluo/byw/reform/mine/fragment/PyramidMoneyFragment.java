package huolongluo.byw.reform.mine.fragment;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huolongluo.byw.R;
import huolongluo.byw.byw.base.BaseApp;
import huolongluo.byw.byw.net.UrlConstants;
import huolongluo.byw.reform.base.BaseFragment;
import huolongluo.byw.reform.mine.bean.fintrolinfoBeam;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.cache.CacheManager;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.widget.pulltorefresh.PullToRefreshListView;
import okhttp3.Request;

/**
 * Created by Administrator on 2019/1/22 0022.
 */

public class PyramidMoneyFragment extends BaseFragment {


    PullToRefreshListView listView;
   LinearLayout tv_no_data;
     List<fintrolinfoBeam.FintrolInfo> list=new ArrayList<>();
    private Adapter adapter;

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_pyramidmoney;
    }

    @Override
    protected void onCreatedView(View rootView) {
        listView = rootView.findViewById(R.id.listView);
        tv_no_data = rootView.findViewById(R.id.tv_no_data);



        String result = CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().getAsString("jsonProfit");
        if (!TextUtils.isEmpty(result)) {
            fintrolinfoBeam beam =new Gson().fromJson(result,fintrolinfoBeam.class);

            list.addAll(beam.getFintrolinfo());

        }

        adapter = new Adapter(list);
        listView.setPullRefreshEnable(false);
        listView.setPullLoadEnable(false);
        listView.setAdapter(adapter);
        jsonProfit();
    }

    int currentPage = 1;


    void jsonProfit() {

        Map<String, String> params = new HashMap<>();
        params.put("currentPage", currentPage + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.jsonProfit);
        OkhttpManager.postAsync(UrlConstants.jsonProfit, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                MToast.show(getActivity(), getString(R.string.qs50), 1);
            }

            @Override
            public void requestSuccess(String result) {
                listView.stopLoadMore();

                try {
                    fintrolinfoBeam beam =new Gson().fromJson(result,fintrolinfoBeam.class);

                    if(beam.getCode()==0){

                        if(currentPage==1){
                            list.clear();


                                CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().put("jsonProfit", result);

                        }



                        list.addAll(beam.getFintrolinfo());
                        adapter.notifyDataSetChanged();
                        if(list.size()>0){
                            tv_no_data.setVisibility(View.GONE);
                        }else {
                            tv_no_data.setVisibility(View.VISIBLE);
                        }
                        if(currentPage<beam.getTotalPage()){
                            currentPage++;
                            listView.setPullLoadEnable(true);
                        }else {
                            listView.setPullLoadEnable(false);
                        }
                    }



                }catch (Exception e){
                    MToast.show(getActivity(), getString(R.string.qs51), 1);
                    e.printStackTrace();
                }


            }
        });
    }


    public static class Adapter extends BaseAdapter {
        List<fintrolinfoBeam.FintrolInfo> list=new ArrayList<>();

        public Adapter(List<fintrolinfoBeam.FintrolInfo> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView==null){
                convertView=View.inflate(parent.getContext(),R.layout.pyramidmoneyfragment_item,null);
            }

            TextView time_tv=convertView.findViewById(R.id.time_tv);
            TextView detail_tv=convertView.findViewById(R.id.detail_tv);
            if(list.get(position).getFcreatetime()!=null){
                time_tv.setText(list.get(position).getFcreatetime().replace(" ","\n"));
            }
                     if(list.get(position).getFtitle()!=null){
                         detail_tv.setText(list.get(position).getFtitle());
                     }

            return convertView;
        }
    }
}
