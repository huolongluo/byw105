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
import huolongluo.byw.reform.mine.bean.JsonDetailsBean;
import huolongluo.byw.user.UserInfoManager;
import huolongluo.byw.util.OkhttpManager;
import huolongluo.byw.util.cache.CacheManager;
import huolongluo.byw.util.tip.MToast;
import huolongluo.byw.widget.pulltorefresh.PullToRefreshListView;
import okhttp3.Request;

/**
 * Created by Administrator on 2019/1/22 0022.
 */

public class PyramidFriendsFragment extends BaseFragment {

    private Adapter adapter;

      private LinearLayout tv_no_data;
    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_pyramidfriends;
    }
    PullToRefreshListView listView;

    List<JsonDetailsBean.JsonDetail> list=new ArrayList<>();
    @Override
    protected void onCreatedView(View rootView) {

        listView=rootView.findViewById(R.id.listView);
        tv_no_data=rootView.findViewById(R.id.tv_no_data);

        String result = CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().getAsString("jsonDetails");
        if (!TextUtils.isEmpty(result)) {
            JsonDetailsBean beam =new Gson().fromJson(result,JsonDetailsBean.class);
            list.addAll(beam.getFusers());

        }







        adapter = new Adapter(list);
        listView.setPullRefreshEnable(false);
        listView.setPullLoadEnable(false);
         listView.setAdapter(adapter);



        jsonDetails();
    }


    int currentPage = 1;

    void jsonDetails() {

        Map<String, String> params = new HashMap<>();
        params.put("currentPage", currentPage + "");
        params = OkhttpManager.encrypt(params);
        params.put("loginToken", UserInfoManager.getToken());
        netTags.add(UrlConstants.jsonDetails);
        OkhttpManager.postAsync(UrlConstants.jsonDetails, params, new OkhttpManager.DataCallBack() {
            @Override
            public void requestFailure(Request request, Exception e, String errorMsg) {
                e.printStackTrace();
                MToast.show(getActivity(), getString(R.string.qs41), 2);
            }

            @Override
            public void requestSuccess(String result) {

                listView.stopLoadMore();
                try {
                    JsonDetailsBean beam =new Gson().fromJson(result,JsonDetailsBean.class);

                    if(beam.getCode()==0){

                           if(currentPage==1){
                               list.clear();

                                   CacheManager.getDefault(BaseApp.getSelf().getApplicationContext()).getAcache().put("jsonDetails", result);

                           }




                      list.addAll(beam.getFusers());
                       adapter.notifyDataSetChanged();

                       if(list.size()==0){
                           tv_no_data.setVisibility(View.VISIBLE);
                       }else {
                           tv_no_data.setVisibility(View.GONE);
                       }
                       if(currentPage<beam.getTotalPage()){
                           currentPage++;
                           listView.setPullLoadEnable(true);
                       }else {
                           listView.setPullLoadEnable(false);
                       }
                    }



                }catch (Exception e){
                    e.printStackTrace();
                    MToast.show(getActivity(), getString(R.string.qs40), 2);
                }


            }
        });
    }







    public static class Adapter extends BaseAdapter {
        List<JsonDetailsBean.JsonDetail> list=new ArrayList<>();

        public Adapter(List<JsonDetailsBean.JsonDetail> list) {
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
                convertView=View.inflate(parent.getContext(),R.layout.pyramidfriendfragment_item,null);
            }

            TextView time_tv=convertView.findViewById(R.id.time_tv);
            TextView detail_tv=convertView.findViewById(R.id.detail_tv);
            time_tv.setText(list.get(position).getFid()+"");
            if(list.get(position).getFregisterTime()!=null){
                detail_tv.setText(list.get(position).getFregisterTime().replace(" ","\n"));
            }

            return convertView;
        }
    }
}
