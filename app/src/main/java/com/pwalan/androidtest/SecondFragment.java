package com.pwalan.androidtest;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 第二个页面
 */
public class SecondFragment extends Fragment {

    private List<Map<String, Object>> listItems;

    private String[] names = new String[]{
            "name1", "name2", "name3", "name4", "name5", "name6"
    };

    private String[] times = new String[]{
            "2011", "2012", "2013", "2014", "2015", "2016"
    };

    private int[] pic = new int[]{
            R.drawable.picture0, R.drawable.picture1, R.drawable.picture2, R.drawable.picture3,
            R.drawable.picture4, R.drawable.picture5
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg2, container, false);

        listItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < names.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("name", names[i]);
            listItem.put("time", times[i]);
            listItem.put("pic", pic[i]);
            listItems.add(listItem);
        }

        /*SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), listItems, R.layout.quanzi_item,
                new String[]{"name", "time", "pic"},
                new int[]{R.id.name, R.id.time, R.id.picture});*/

        MyAdapter adapter=new MyAdapter(getActivity());

        ListView list = (ListView) view.findViewById(R.id.fg2list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "You clicked " + names[position], Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    public class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return listItems.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        //****************************************final方法
        //注意原本getView方法中的int position变量是非final的，现在改为final
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            final CheckBox cb_favorite;
            if (convertView == null) {

                holder=new ViewHolder();

                //可以理解为从vlist获取view  之后把view返回给ListView

                convertView = mInflater.inflate(R.layout.quanzi_item, null);
                holder.name = (TextView)convertView.findViewById(R.id.name);
                holder.time = (TextView)convertView.findViewById(R.id.time);
                holder.picture=(ImageView)convertView.findViewById(R.id.picture);
                cb_favorite = (CheckBox)convertView.findViewById(R.id.cb_favorite);
                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
                cb_favorite = (CheckBox)convertView.findViewById(R.id.cb_favorite);
            }

            holder.name.setText((String) listItems.get(position).get("name"));
            holder.time.setText((String) listItems.get(position).get("time"));
            holder.picture.setImageResource((int)listItems.get(position).get("pic"));
            cb_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(cb_favorite.isChecked()){
                        favorite(position);
                    }else{
                        disfavorite(position);
                    }
                }
            });


            return convertView;
        }
    }

    //这里存储的是quanzi_item里的组件
    public final class ViewHolder {
        public RoundImageView head;
        public TextView name;
        public TextView time;
        public ImageView picture;
    }

    //收藏动作
    public void favorite(int position) {
        Toast.makeText(getActivity(),"你收藏了 "+names[position]+" 的 "+times[position],Toast.LENGTH_SHORT).show();
    }

    //取消收藏
    public void disfavorite(int position){
        Toast.makeText(getActivity(),"你取消了收藏 "+names[position]+" 的 "+times[position],Toast.LENGTH_SHORT).show();
    }
}