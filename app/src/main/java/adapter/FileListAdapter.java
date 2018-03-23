package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qiang.collect.R;

import java.io.File;
import java.util.List;

/**
 * Created by Hero on 2017/1/17.
 */

public class FileListAdapter extends BaseAdapter {

    private List<File> mData ;
    private boolean[] mChecked ;
    private Context mContext ;

    public FileListAdapter(Context context, List<File> data, boolean[] checked){
        this.mContext = context ;
        this.mData = data ;
        this.mChecked = checked ;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null ;
        if(convertView == null){
            viewHolder = new ViewHolder() ;
            convertView = LayoutInflater.from(mContext).inflate(R.layout.file_list_item, parent, false) ;
            viewHolder.mFileNameView = (TextView) convertView.findViewById(R.id.list_item_file_name);
            viewHolder.mFileTimeView = (TextView) convertView.findViewById(R.id.list_item_file_time) ;
            viewHolder.mCheckBtn = (ImageView) convertView.findViewById(R.id.multi_check_btn) ;
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try{
            if(mData.get(position).getName()!=null){
                String[] split = mData.get(position).getName().split("_") ;
                if(split[0]!=null){
                    viewHolder.mFileNameView.setText(split[0]);
                }else{
                    viewHolder.mFileNameView.setText("未命名");
                }

                if(split[1].length()<5){
                    if(split[1].length()==0){
                        viewHolder.mFileTimeView.setText("未知");
                    }else{
                        viewHolder.mFileTimeView.setText(split[1]);
                    }
                }else{
                    viewHolder.mFileTimeView.setText(split[1].substring(0, split[1].length() - 4));
                }
            }else{
                viewHolder.mFileNameView.setText("未命名");
                viewHolder.mFileTimeView.setText("未知");
            }
        }catch (Exception e){
            Toast.makeText(mContext, "系统繁忙！", Toast.LENGTH_SHORT).show() ;
        }


        if(mChecked[position]){
            viewHolder.mCheckBtn.setImageResource(R.drawable.img_selected);
        }else{
            viewHolder.mCheckBtn.setImageResource(R.drawable.img_unselected);
        }
        final ViewHolder finalViewHolder = viewHolder;
        viewHolder.mCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChecked[position] == true){
                    mChecked[position] = false ;
                    finalViewHolder.mCheckBtn.setImageResource(R.drawable.img_unselected);
                }else{
                    mChecked[position] = true ;
                    finalViewHolder.mCheckBtn.setImageResource(R.drawable.img_selected);
                }
            }
        });
        return convertView;
    }

    class ViewHolder{
        private TextView mFileNameView ;
        private TextView mFileTimeView ;
        private ImageView mCheckBtn ;
    }
}
