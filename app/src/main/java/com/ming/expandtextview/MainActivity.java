package com.ming.expandtextview;

import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mBtnExpand;
    private Button mBtnStrink;
    private ExpandTextView mExpandTextview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnExpand = (Button) findViewById(R.id.expand);
        mBtnStrink = (Button) findViewById(R.id.strink);
        mExpandTextview = (ExpandTextView) findViewById(R.id.expandtextview);
        mExpandTextview.setTextSize(18);
        mExpandTextview.setText("科技本身蕴含着一种碾压一切的力量，而这种力量会导致我们加速奔向某种设定好的结局。" +
                "这篇文章尝试预测未来最可能的5种结局：黄金时代，虚拟世界，冷平衡，生化人，大寂灭。");
        mBtnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandTextview.expand();
            }
        });
        mBtnStrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandTextview.shrink();
            }
        });

        mExpandTextview.post(new Runnable() {
            @Override
            public void run() {
                if (mExpandTextview.isExpandable()) {
                    mBtnExpand.setVisibility(View.VISIBLE);
                    mBtnStrink.setVisibility(View.VISIBLE);
                } else {
                    mBtnExpand.setVisibility(View.GONE);
                    mBtnStrink.setVisibility(View.GONE);
                }
            }
        });

    }
}
