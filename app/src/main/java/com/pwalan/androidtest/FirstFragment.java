package com.pwalan.androidtest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 第一个页面
 */
public class FirstFragment extends Fragment {

    private String[] names = new String[]{
            "picture1", "picture2", "picture3", "picture4", "picture5","picture6"
    };

    private int[] imageIds = new int[]{
            R.drawable.picture0, R.drawable.picture1, R.drawable.picture2,
            R.drawable.picture3, R.drawable.picture4,R.drawable.picture5
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fg1, container, false);

        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < names.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("picture", imageIds[i]);
            listItem.put("name", names[i]);
            listItems.add(listItem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), listItems, R.layout.simple_item,
                new String[]{"picture", "name"},
                new int[]{R.id.shared_pictures, R.id.picture_name});

        ListView list = (ListView) view.findViewById(R.id.internet_list);
        list.setAdapter(simpleAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "你点击了 " + names[position], Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}

