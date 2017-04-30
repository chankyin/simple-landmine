package io.github.chankyin.simplelandmine;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.ScaleGestureDetector;
import android.widget.LinearLayout;
import android.widget.TextView;

public class EntryActivity extends AppCompatActivity{
    private TextView testView;
    private float scaleFactor;
    private int px, py;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        testView = new TextView(this);
        testView.setText("1. Test view for testing line 1\n2. Test view for testing line 2\n3. Test view for testing line 3");
        ScaleGestureDetector scaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener(){
            @Override
            public boolean onScale(ScaleGestureDetector detector){
                scaleFactor = detector.getScaleFactor();
                testView.setScaleX(scaleFactor);
                testView.setScaleY(scaleFactor);
                return true;
            }
        });
        GestureDetector detector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){

        });
        testView.setOnTouchListener((v, event) -> {
            boolean a = scaleDetector.onTouchEvent(event);
            boolean b = detector.onTouchEvent(event);
            return a || b;
        });
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            testView.setOnGenericMotionListener((v, event) -> detector.onGenericMotionEvent(event));
        }
        ((LinearLayout) findViewById(R.id.activity_entry))
                .addView(testView);
    }
}
