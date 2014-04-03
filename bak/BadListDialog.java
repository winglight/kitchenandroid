package com.syt.health.kitchen.fragment;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.syt.health.kitchen.R;

public class BadListDialog extends Dialog  {
	
	
    private Context context;
    private List<String> list;

    public BadListDialog(Context context, List<String> list) {
        super(context);
        this.context = context;
        this.list = list;
    }

    protected void onStart() {
        super.onStart();
        
        ListView listView = new ListView(context);
        
        setContentView(listView);
        
        getWindow().setFlags(4, 4);
        setTitle(context.getResources().getString(R.string.meal_bads));
        
        listView.setAdapter(new ArrayAdapter<String>(this.context, android.R.layout.simple_list_item_1, list));
    }
    

}

