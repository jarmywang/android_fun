package com.example.user.myapplication.vr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.myapplication.R;

import cn.wang.vrecyclerview.VRecyclerView;

/**
 * Created by Wang on 2015/12/17.
 */
public class VRFunActivity extends AppCompatActivity {

    VRecyclerView vRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr);

        vRecyclerView = (VRecyclerView) findViewById(R.id.vr);

        RecyclerView.Adapter<VHolder> adapter = new RecyclerView.Adapter<VHolder>() {
            @Override
            public VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(VRFunActivity.this).inflate(R.layout.item_gallery, parent, false);
                VHolder vHolder = new VHolder(view);
//                vHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(v.getContext(), "root_item " + vRecyclerView.getChildAdapterPosition(v), Toast.LENGTH_SHORT).show();
//                    }
//                });
                return vHolder;
            }

            @Override
            public void onBindViewHolder(VHolder holder, final int position) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "root_item " + vRecyclerView.getChildAdapterPosition(v), Toast.LENGTH_SHORT).show();
                    }
                });
                holder.textView.setText("item " + position);
            }

            @Override
            public int getItemCount() {
                return 36;
            }
        };

        vRecyclerView.setAdapter(adapter);
    }


    class VHolder extends RecyclerView.ViewHolder{

        View item;
        TextView textView;

        public VHolder(View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.item);
            textView = (TextView) itemView.findViewById(R.id.id_index_gallery_item_text);
        }
    }

}
