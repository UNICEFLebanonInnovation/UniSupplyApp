package com.unicefwinterizationplatform.winterization_android;

/**
 * Created by Tarek on 9/21/2014.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.text.style.UpdateLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tarek on 9/18/2014.
 */
public class FamilyAdapter extends BaseAdapter {

    private ArrayList<FamilyObject> familyList;
    private LayoutInflater inflater;
    Activity activity;
    Context context;
    String action;
    String role;

    boolean completionMode = false;

//    private AddResidentActivity addResidentActivity;
    //   private EditResidentActivity editResidentActivity;


    public FamilyAdapter(Context context, String action)
    {
        familyList =new ArrayList<FamilyObject>();
        inflater = LayoutInflater.from(context);
        this.action = action;
        this.context = context;
        setRole(context);


    }


    private void setRole(Context context){

        role = UserPrefs.getUserPref(context);

    }

    public void  addList(ArrayList<FamilyObject> list)
    {
        familyList = list;
    }

    public ArrayList<FamilyObject>  getList()
    {
        return familyList;
    }


    public void  addItemToList(FamilyObject familyObject)
    {
        familyList.add(familyObject);
        notifyDataSetChanged();
    }




    public void  editItemToList(FamilyObject familyObject, int position)
    {
        //childList.remove(position);
        //childList.set(position,child);
        familyList.get(position).setIdType(familyObject.getIdType());
        familyList.get(position).setID(familyObject.getID());

        notifyDataSetChanged();
    }

    public void  removeItemFromList(int position)
    {
        familyList.remove(position);
        notifyDataSetChanged();
    }



    @Override
    public int getCount() {
        return familyList.size();
    }

    @Override
    public FamilyObject getItem(int position) {
        return familyList.get(position);
    }

    // public String getNameValue() {
    //     return naming;
    //   }



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
                view = inflater.inflate(R.layout.family_item, null);
                view.setTag(new ViewHolder(view));
        }
        return view;
    }


    private void bind(final FamilyObject familyObject, View view, final int position) {


            ViewHolder holder = (ViewHolder) view.getTag();


                holder.idView.setText(familyObject.getID());
        holder.idTypeView.setText(familyObject.getIdType());

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        removeChildAlert(position);

                    }
                });
            }





    static class ViewHolder {

        // if(UserPrefs.getUserPref(context).equals("Collector"))
        // {}
        //final TextView childNoView;
        final TextView idView;
        final TextView idTypeView;

        final Button removeButton;
        final LinearLayout parentLayout;


        ViewHolder(View view) {
            //childNoView = (TextView) view.findViewWithTag("child_no");
            idTypeView = (TextView) view.findViewWithTag("id_type");
            idView = (TextView) view.findViewWithTag("id");
            removeButton = (Button) view.findViewWithTag("button_removeFamily");
            parentLayout = (LinearLayout) view.findViewWithTag("parentTag");
        }
    }




    public void removeChildAlert(final int index)
    {
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.alert_checkbarcode, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final TextView barcodeVal = (TextView) promptsView
                .findViewById(R.id.textView_barcodeNumber);

        barcodeVal.setText("Do you want to remove this child from the list?");
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                removeItemFromList(index);

                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


}

