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
public class ChildrenAdapter extends BaseAdapter {

    final int GET_BARCODEREQUEST  = 3;
    private ArrayList<ChildObject> childList;
    private LayoutInflater inflater;
    Activity activity;
    Context context;
    String action;
    String role;
    String mode;

    boolean completionMode = false;

//    private AddResidentActivity addResidentActivity;
 //   private EditResidentActivity editResidentActivity;


    public ChildrenAdapter(Context context, String action, String mode)
    {
        childList =new ArrayList<ChildObject>();
        inflater = LayoutInflater.from(context);
        this.action = action;
        this.context = context;
        this.mode = mode;
        setRole(context);


    }

    public ChildrenAdapter(Context context, String action, Activity activity,String mode)
    {
        childList =new ArrayList<ChildObject>();
        inflater = LayoutInflater.from(context);
        this.action = action;
        this.context = context;
        this.activity = activity;
        this.mode = mode;
        setRole(context);
    }

    private void setRole(Context context){
       // JSONObject userObj = UserPrefs.getUserData(context);
       // try {
            role = UserPrefs.getUserPref(context);
       // }
       // catch (JSONException e)
        //{

        //}

    }

    public void  addList(ArrayList<ChildObject> list)
    {
        childList = list;
    }

    public ArrayList<ChildObject>  getList()
    {
        return childList;
    }


    public void  addItemToList(ChildObject child)
    {
        childList.add(child);
        notifyDataSetChanged();
    }



    public  void setForCompletion()
    {

        completionMode = true;

        for(ChildObject childObject : childList){

            if(childObject.getStatus().equals("ALLOCATED"))
                childObject.setStatus("COMPLETED");

        }

        notifyDataSetChanged();
    }

    public void  editItemToList(ChildObject child, int position)
    {
        //childList.remove(position);
        //childList.set(position,child);
            childList.get(position).setName(child.name);
            childList.get(position).setAge(child.age);
            childList.get(position).setGender(child.gender);
            childList.get(position).setKit(child.kit);
            childList.get(position).setStatus(child.status);
            childList.get(position).setReasonForEdit(child.reasonForEdit);
            childList.get(position).setVoucherCode(child.voucherCode);

        notifyDataSetChanged();
    }

    public void  removeItemFromList(int position)
    {
        childList.remove(position);
        notifyDataSetChanged();
    }

    public void  updateItemFromList(int position, Button button)
    {

        ChildObject childObject = childList.get(position);

        if (childObject.getStatus().equals("ALLOCATED") || childObject.getStatus().equals("EDITED")
                || childObject.getStatus().equals("NOT_DISTRIBUTED"))
        {

            childList.get(position).setStatus("COMPLETED");
            button.setText("Uncomplete");
            button.setBackground(button.getResources().getDrawable(R.drawable.custombutton_red));

        }
        else
        {
            childList.get(position).setStatus("ALLOCATED");
            button.setText("Complete");
            button.setBackground(button.getResources().getDrawable(R.drawable.custombutton_green));


        }

        notifyDataSetChanged();

    }



    @Override
    public int getCount() {
        return childList.size();
    }

    @Override
    public ChildObject getItem(int position) {
        return childList.get(position);
    }

    // public String getNameValue() {
    //     return naming;
    //   }


    public void setVoucherCodeInList(int position, String voucherCode)
    {
        childList.get(position).setVoucherCode(voucherCode);
        if (voucherCode.length() == 10)
        {
            childList.get(position).setStatus("DISTRIBUTED");
        }
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
            if (mode.equals(Constants.ROLE_KITS_ASSESSOR)) {
                view = inflater.inflate(R.layout.child_item, null);
                view.setTag(new ViewCollectorHolder(view));
            } else if (mode.equals(Constants.ROLE_KITS_DISTRIBUTOR)) {
                view = inflater.inflate(R.layout.kit_item, null);
                view.setTag(new ViewDistributorHolder(view));
            } //else if (role.equals(Constants.ROLE_VOUCHERS_DISTRIBUTOR) || role.equals(Constants.ROLE_VOUCHERS_REDEEMER)) {
      //          view = inflater.inflate(R.layout.voucher_item, null);
        //        view.setTag(new ViewVoucherHolder(view));
            //}
        }
        return view;
    }


    private void bind(final ChildObject childMap, View view, final int position) {


        if(mode.equals(Constants.ROLE_KITS_ASSESSOR)) {
            ViewCollectorHolder holder = (ViewCollectorHolder) view.getTag();
            if (childMap.getGender().toLowerCase().equals("boy"))
            holder.genderView.setImageResource(R.drawable.boy);
            if (childMap.getGender().toLowerCase().equals("girl"))
                holder.genderView.setImageResource(R.drawable.girl);

            holder.ageView.setText(childMap.getAge());
          //  holder.genderView.setText("Gender:" + childMap.getGender());

           if(action.equals("IDLE")){
             holder.parentLayout.removeView(holder.removeButton);
           }
            else {
               holder.removeButton.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {

                       removeChildAlert(position);

                   }
               });
           }
        }
        else if(mode.equals(Constants.ROLE_KITS_DISTRIBUTOR)) {
            final ViewDistributorHolder holder = (ViewDistributorHolder) view.getTag();

            holder.ageView.setText("Age: " + childMap.getAge());
            holder.kitView.setText("Kit: " + childMap.getKit());
            //Log.d("KIT", childMap.getKit().toString());


            if (completionMode)
            {
                holder.statusView.setImageResource(R.drawable.completed);
            }
            else {


                Log.d("STATUS", childMap.getStatus().toString());
                if (childMap.getStatus().equals("ALLOCATED")) {
                    holder.statusView.setImageResource(R.drawable.allocated);
                    holder.completeButton.setText("Complete");
                    holder.completeButton.setBackground(holder.completeButton.getResources().getDrawable(R.drawable.custombutton_green));
                }

                else if (childMap.getStatus().equals("EDITED")) {
                    holder.statusView.setImageResource(R.drawable.edit);
                    holder.completeButton.setText("Complete");
                    holder.completeButton.setBackground(holder.completeButton.getResources().getDrawable(R.drawable.custombutton_green));
                }
                else if (childMap.getStatus().equals("NOT_DISTRIBUTED")) {
                    holder.statusView.setImageResource(R.drawable.delete);
                    holder.completeButton.setText("Complete");
                    holder.completeButton.setBackground(holder.completeButton.getResources().getDrawable(R.drawable.custombutton_green));
                }
                else if (childMap.getStatus().equals("COMPLETED")) {
                    holder.statusView.setImageResource(R.drawable.completed);
                    holder.completeButton.setText("Uncomplete");
                    holder.completeButton.setBackground(holder.completeButton.getResources().getDrawable(R.drawable.custombutton_red));
                }
            }


            holder.completeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Button btn = (Button)view;

                    updateItemFromList(position,btn);

                }
            });

        }

        /*else if(mode.equals(Constants.ROLE_VOUCHERS_DISTRIBUTOR ) || mode.equals(Constants.ROLE_VOUCHERS_REDEEMER )) {
            ViewVoucherHolder holder = (ViewVoucherHolder) view.getTag();

            holder.nameView.setText("Name: " + childMap.getName());
            holder.ageView.setText("Age: " + childMap.getAge());
            holder.genderView.setText("Gender: " + childMap.getGender());
            if (childMap.getVoucherCode() != null && childMap.getVoucherCode().length() == 10 && childMap.getStatus().equals("DISTRIBUTED")) {
                holder.voucherCode.setText("Voucher Code: " + childMap.getVoucherCode());
                holder.voucherStatusView.setImageResource(R.drawable.edit);
            }
            else if(childMap.getVoucherCode() != null && childMap.getVoucherCode().length() == 10 && childMap.getStatus().equals("REDEEMED"))
            {
                holder.voucherCode.setText("Voucher Code: " + childMap.getVoucherCode());
                holder.voucherStatusView.setImageResource(R.drawable.completed);
            }

            else if(childMap.getStatus().equals("NOT_DISTRIBUTED")){
                holder.voucherCode.setText("Voucher Code: voucher not set yet");
                holder.voucherStatusView.setImageResource(R.drawable.initialize2);
            }
            else if(childMap.getStatus().equals("DELETED")){
                holder.voucherStatusView.setImageResource(R.drawable.delete);
            }


              holder.barcodeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent =   new Intent(context, ReaderActivity.class);
                    //ChildObject childObject = childrenAdapter.getList().get(i);
                    intent.setAction("addVoucher_styleTwo");
                    //intent.putExtra("CHILD",childObject);
                    intent.putExtra("position",position);
                    activity.startActivityForResult(intent, GET_BARCODEREQUEST);
                }
            });
        }*/
    }

    static class ViewCollectorHolder {

       // if(UserPrefs.getUserPref(context).equals("Collector"))
       // {}
        //final TextView childNoView;
        final TextView ageView;
        final ImageView genderView;

        final Button removeButton;
        final LinearLayout parentLayout;


        ViewCollectorHolder(View view) {
            //childNoView = (TextView) view.findViewWithTag("child_no");
            ageView = (TextView) view.findViewWithTag("age");
            genderView = (ImageView) view.findViewWithTag("gender");
            removeButton = (Button) view.findViewWithTag("button_removeChild");
            parentLayout = (LinearLayout) view.findViewWithTag("parentTag");
        }
    }

    static class ViewDistributorHolder {

        // if(UserPrefs.getUserPref(context).equals("Collector"))
        // {}

        final TextView kitView;
        final ImageView statusView;
        final Button completeButton;
        final TextView ageView;


        ViewDistributorHolder(View view) {

            kitView = (TextView) view.findViewWithTag("kit");
            ageView = (TextView) view.findViewWithTag("age");
            completeButton = (Button) view.findViewWithTag("complete");
            statusView = (ImageView) view.findViewWithTag("status");


        }
    }

    static class ViewVoucherHolder {

        // if(UserPrefs.getUserPref(context).equals("Collector"))
        // {}
        //final TextView childNoView;

        final ImageView voucherStatusView;
        final TextView nameView;
        final TextView ageView;
        final TextView genderView;
        final TextView voucherCode;
        final Button barcodeButton;

        //final LinearLayout parentLayout;


        ViewVoucherHolder(View view) {
            voucherStatusView = (ImageView) view.findViewWithTag("voucherStatus");
            nameView = (TextView) view.findViewWithTag("name");
            ageView = (TextView) view.findViewWithTag("age");
            genderView = (TextView) view.findViewWithTag("gender");
            voucherCode = (TextView) view.findViewWithTag("voucherCode");
            barcodeButton = (Button) view.findViewWithTag("addVoucherCode");
           // parentLayout = (LinearLayout) view.findViewWithTag("parentTag");
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
