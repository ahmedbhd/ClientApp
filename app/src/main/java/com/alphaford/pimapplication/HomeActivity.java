package com.alphaford.pimapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.alphaford.pimapplication.StatsDay.StatsActivity;

public class HomeActivity extends AppCompatActivity {
    ViewFlipper viewFlipper;
    LinearLayout layoutDescription;
    Button showDesc;
    TextView getStarted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        viewFlipper=(ViewFlipper)findViewById(R.id.ViewFlipperKa);
        viewFlipper.setFlipInterval(1000);
        viewFlipper.startFlipping();
        layoutDescription = (LinearLayout) findViewById(R.id.layoutDescription);
        getStarted = (TextView) findViewById(R.id.showCharts);
        SpannableString content = new SpannableString("Get Started !");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        getStarted.setText(content);

        showDesc = (Button) findViewById(R.id.showLayout);
        showDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutDescription.setVisibility(View.VISIBLE);
                RunAnimation();
            }
        });
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, StatsActivity.class);
                startActivity(intent);
            }
        });



    }
    private void RunAnimation()
    {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.text_anim);
        a.reset();

        getStarted.clearAnimation();
        getStarted.startAnimation(a);
    }
}
