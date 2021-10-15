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

        import org.w3c.dom.Text;

        import java.util.ArrayList;
        import java.util.Map;

/**
 * Created by Tarek on 7/23/15.
 */
public class CommentAdapter extends BaseAdapter {




    private ArrayList<Map<String,Object>> commentList;
    private LayoutInflater inflater;
    Activity activity;
    Context context;

//    private AddResidentActivity addResidentActivity;
    //   private EditResidentActivity editResidentActivity;


    public CommentAdapter(Context context)
    {
        commentList =new ArrayList<Map<String,Object>>();
        inflater = LayoutInflater.from(context);
        this.context = context;


    }



    public void  addList(ArrayList<Map<String,Object>> list)
    {
        commentList = list;
    }


    public void  addComment(Map<String,Object> comment)
    {
        commentList.add(comment);
        notifyDataSetChanged();
    }

    public ArrayList<Map<String,Object>>  getList()
    {
        return commentList;
    }



    @Override
    public int getCount() {
        return commentList.size();
    }

    @Override
    public Map<String,Object> getItem(int i) {
        return commentList.get(i);
    }



    // public String getNameValue() {
    //     return naming;
    //   }



    public void  editCommentToList(Map<String,Object> item, int position)
    {
        //childList.remove(position);
        //childList.set(position,child);
        commentList.set(position,item);



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
            view = inflater.inflate(R.layout.comment_item, null);
            view.setTag(new ViewCollectorHolder(view));
        }
        return view;
    }


    private void bind(Map<String,Object> comment, View view, final int position) {

        ViewCollectorHolder holder = (ViewCollectorHolder) view.getTag();
        Map<String,String> userProfile = UserPrefs.getUserProfile(context);



        holder.comment_index.setText( (position+1)+" - ");
        holder.comment.setText(comment.get("comment") +"");
        holder.comment_date.setText("By "+comment.get("user")+" on "+comment.get("date")+"");

    }

    static class ViewCollectorHolder {

        // if(UserPrefs.getUserPref(context).equals("Collector"))
        // {}
        //final TextView childNoView;
        final TextView comment_index;
        final TextView comment;
        final TextView comment_date;




        ViewCollectorHolder(View view) {
            //childNoView = (TextView) view.findViewWithTag("child_no");
            comment_index = (TextView) view.findViewWithTag("comment_index");
            comment = (TextView) view.findViewWithTag("comment");
            comment_date = (TextView) view.findViewWithTag("comment_date");
        }
    }






}
