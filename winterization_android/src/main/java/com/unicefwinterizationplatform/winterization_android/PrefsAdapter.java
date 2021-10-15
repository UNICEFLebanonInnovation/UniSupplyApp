package com.unicefwinterizationplatform.winterization_android;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.prefs.PreferenceChangeListener;

/**
 * Created by Tarek on 9/8/15.
 */
public class PrefsAdapter extends BaseAdapter {


    private ArrayList<PrefObject> prefList;
    private LayoutInflater inflater;
    Activity activity;
    Context context;

//    private AddResidentActivity addResidentActivity;
    //   private EditResidentActivity editResidentActivity;


    public PrefsAdapter(Context context)
    {
        prefList =new ArrayList<PrefObject>();
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void  addList(ArrayList<PrefObject> list)
    {
        prefList = list;
        notifyDataSetChanged();
    }



    public ArrayList<PrefObject>  getList()
    {
        return prefList;
    }


    @Override
    public int getCount() {
        return prefList.size();
    }

    @Override
    public PrefObject getItem(int position) {
        return prefList.get(position);
    }

    // public String getNameValue() {
    //     return naming;
    //   }


    public void editItemToList(PrefObject map, int pos)
    {
        prefList.set(pos,map);
        notifyDataSetChanged();

    }


    public  void deleteItemFromList(int pos)
    {
        UserPrefs.removeSettingsFromList(prefList.get(pos).getPrefName(), context);

        prefList.remove(pos);

        notifyDataSetChanged();

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = inflateIfRequired(view, position, parent);
        bind(getItem(position), view, position);
        return view;
    }

    private View inflateIfRequired(View view, int position, ViewGroup parent) {
        if (view == null) {
            view = inflater.inflate(R.layout.pref_item, null);
            view.setTag(new ViewCollectorHolder(view));
        }
        return view;
    }


    private void bind(PrefObject prefMap, View view, final int position) {

        ViewCollectorHolder holder = (ViewCollectorHolder) view.getTag();
        String prefNameStr = prefMap.getPrefName();

        holder.prefName.setText(prefNameStr);

        if (prefMap.getCbName() != null)
        holder.cbName.setText(prefMap.getCbName());


        boolean bool = prefMap.isEnabled();

        if (bool)
        {
            holder.selectedView.setBackgroundResource(R.drawable.custombutton_green);
        }
        else
        {
            holder.selectedView.setBackgroundResource(R.drawable.custombutton_red);
        }


        //  holder.progBar.setSecondaryProgress(itemMap.getDelivered()+itemMap.getDestroyed());

    }

    static class ViewCollectorHolder {

        // if(UserPrefs.getUserPref(context).equals("Collector"))
        // {}
        //final TextView childNoView;
        final TextView prefName;
        final TextView cbName;
        final ImageView selectedView;





        ViewCollectorHolder(View view) {
            //childNoView = (TextView) view.findViewWithTag("child_no");
            prefName = (TextView) view.findViewWithTag("pref_name");
            cbName = (TextView) view.findViewWithTag("cb_name");
            selectedView = (ImageView) view.findViewWithTag("pref_img");

        }
    }

}

