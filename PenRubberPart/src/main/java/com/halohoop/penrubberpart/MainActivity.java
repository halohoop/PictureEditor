package com.halohoop.penrubberpart;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.halohoop.penrubberpart.widgets.PenDrawView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private PenDrawView mPdv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPdv = (PenDrawView) findViewById(R.id.pdv);
        String dirPath = "/mnt/sdcard/Pictures/Screenshots";
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        File file = files[files.length-1];
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        mPdv.setBitmap(bitmap);
    }

    public void penMode(View view) {
        mPdv.setMode(PenDrawView.PEN_MODE);
    }

    public void rubberMode(View view) {
        mPdv.setMode(PenDrawView.RUBBER_MODE);
    }

    public void redo(View view) {

    }

    public void undo(View view) {

    }
}
