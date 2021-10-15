package com.unicefwinterizationplatform.winterization_android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tarek on 7/23/15.
 */
public class ItemAdapter extends BaseAdapter {




    private ArrayList<ItemObject> itemList;
    private LayoutInflater inflater;
    Activity activity;
    Context context;

//    private AddResidentActivity addResidentActivity;
    //   private EditResidentActivity editResidentActivity;


    public ItemAdapter(Context context)
    {
        itemList =new ArrayList<ItemObject>();
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void  addList(ArrayList<ItemObject> list)
    {
        itemList = list;
    }

    public ArrayList<ItemObject>  getList()
    {
        return itemList;
    }


    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public ItemObject getItem(int position) {
        return itemList.get(position);
    }

    // public String getNameValue() {
    //     return naming;
    //   }



    public void  editItemToList(ItemObject item, int position)
    {
        //childList.remove(position);
        //childList.set(position,child);
        itemList.get(position).setQuantity(item.getQuantity());
        itemList.get(position).setDelivered(item.getDelivered());
       // itemList.get(position).setDestroyed(item.getDestroyed());
        itemList.get(position).setComment(item.getComment());
        itemList.get(position).setCommentArray(item.getCommentArray());
        if (item.getDelivered() == 0)
            itemList.get(position).setDeliveryStatus("NOT_COMPLETED");
        else if(item.getQuantity().equals(item.getDelivered()))
            itemList.get(position).setDeliveryStatus("COMPLETED");
        else if(!item.getQuantity().equals(item.getDelivered()) && item.getDelivered() !=0)
            itemList.get(position).setDeliveryStatus("PARTIALLY_COMPLETED");

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
                view = inflater.inflate(R.layout.item_item, null);
                view.setTag(new ViewCollectorHolder(view));
            }
        return view;
    }


    private void bind(ItemObject itemMap, View view, final int position) {

        ViewCollectorHolder holder = (ViewCollectorHolder) view.getTag();

        holder.nameView.setText(itemMap.getItemType());
        holder.quantity.setText("Quantity: "+itemMap.getQuantity().toString());
        holder.delivered.setText("Delivered: "+itemMap.getDelivered().toString());
        holder.progBar.setMax(itemMap.getQuantity());
        holder.progBar.setProgress(itemMap.getDelivered());
        holder.remaining.setText("Remaining: "+(itemMap.getQuantity() - itemMap.getDelivered()));
      //  holder.progBar.setSecondaryProgress(itemMap.getDelivered()+itemMap.getDestroyed());

        float fl = ((float)itemMap.getDelivered()/(float)itemMap.getQuantity()) * 100;

        if (fl<=34)
        {
            holder.progBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.progdraw_red));
        }
        else if (fl>34 && fl<=66)
        {
            holder.progBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.progdraw_orange));
        }
        else if (fl>66)
        {
            holder.progBar.setProgressDrawable(context.getResources().getDrawable(R.drawable.progdraw_green));
        }

    }

    static class ViewCollectorHolder {

        // if(UserPrefs.getUserPref(context).equals("Collector"))
        // {}
        //final TextView childNoView;
        final TextView nameView;
        final TextView quantity;
        final TextView delivered;
        final TextView remaining;
        final ProgressBar progBar;




        ViewCollectorHolder(View view) {
            //childNoView = (TextView) view.findViewWithTag("child_no");
            nameView = (TextView) view.findViewWithTag("name");
            remaining = (TextView) view.findViewWithTag("remaining");
            quantity = (TextView) view.findViewWithTag("quantity");
            delivered = (TextView) view.findViewWithTag("delivered");
            progBar = (ProgressBar) view.findViewWithTag("amount_left");
        }
    }






}
