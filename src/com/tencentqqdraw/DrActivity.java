package com.tencentqqdraw;

import com.tencentqqdraw.reminder.DraggableFlagView;
import com.tencentqqdraw.reminder.DraggableFlagView.OnDraggableFlagViewListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class DrActivity extends Activity implements OnDraggableFlagViewListener, OnClickListener {
	private DraggableFlagView draggableFlagView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dragpoint_main);
        findViewById(R.id.main_btn).setOnClickListener(this);

        draggableFlagView = (DraggableFlagView) findViewById(R.id.main_dfv);
        draggableFlagView.setOnDraggableFlagViewListener(this);
        draggableFlagView.setText("7");


    }


    @Override
    public void onFlagDismiss(DraggableFlagView view) {
        Toast.makeText(this, "onFlagDismiss", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_btn:
                draggableFlagView.setText("7");
                break;
        }
    }
}
