package gamecodeschool.com;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ImageView gameView;
    Bitmap blankBitmap;
    Canvas canvas;
    Paint paint;
    int numberHorizontalPixels;
    int numberVerticalPixels;
    int blockSize;
    int gridWidth = 20;
    int gridHeight;
    float horizontalTouched = -100;
    float verticalTouched = -100;
    int subHorizontalPosition;
    int subVerticalPosition;
    boolean hit = false;
    int shotsTaken;
    int distanceFromSub;
    boolean debugging = false;
    public int numberOfBooms = 10;
    int positions[] = new int[numberOfBooms*2];
    List<Integer> flagCoordintes = new ArrayList();
    // Initialize all the objects ready for drawing
// We will do this inside the onCreate method


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        // Initialize our size based variables
        // based on the screen resolution

        numberHorizontalPixels = size.x;
        numberVerticalPixels = size.y;
        blockSize = numberHorizontalPixels / gridWidth;
        gridHeight = numberVerticalPixels / blockSize;
        // Initialize all the objects ready for drawing
        blankBitmap = Bitmap.createBitmap(numberHorizontalPixels,
                numberVerticalPixels,
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(blankBitmap);
        gameView = new ImageView(this);
        paint = new Paint();
        setContentView(gameView);
        Log.d("Debugging", "In onCreate");
        newGame();
        draw();
    }
    /*
    Start Code
     */
    void newGame(){
        initializePositions();
        shotsTaken = 0;
        initializePositions();
        Log.d("Debugging", "In new Game");
    }

    void initializePositions(){
        Random random = new Random();
        for(int i=0; i< positions.length; i=i+2){
            positions[i] = random.nextInt(gridWidth);
            positions[i+1] = random.nextInt(gridHeight);
        }
    }
    /*
    Visual Part
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void draw(){
        gameView.setImageBitmap(blankBitmap);
        Random random = new Random();
        canvas.drawColor(Color.argb(255, 255, 255, 255));
        // Change the paint color to black
        paint.setColor(Color.argb(255, 0, 0, 0));
        for(int i = 1; i <=gridWidth; i++) {
            canvas.drawLine(blockSize * i, 0,
                    blockSize * i, numberVerticalPixels - 1,
                    paint);
        }
        for(int i = 1; i <=gridHeight; i++) {
            canvas.drawLine(0, blockSize * i,
                    numberHorizontalPixels - 1, blockSize * i,
                    paint);
        }
        for(int i = 0; i< flagCoordintes.size(); i = i+3) {
            canvas.drawText(String.format("%d", flagCoordintes.get(i+2)), (int) flagCoordintes.get(i) * blockSize, (int) flagCoordintes.get(i+1) * blockSize, (float) (flagCoordintes.get(i) * blockSize) + blockSize, (float) (flagCoordintes.get(i+1) * blockSize) + blockSize, paint);
        }
//        Drawable d = getResources().getDrawable(R.drawable.unnamed, null);
//        d.setBounds((int) flagCoordintes.get(i) * blockSize, (int) flagCoordintes.get(i+1) * blockSize, (int) (flagCoordintes.get(i) * blockSize) + blockSize, (int) (flagCoordintes.get(i+1) * blockSize) + blockSize);
//        d.draw(canvas);
        if(debugging)
        printDebuggingTest();
    }
    /*
    Detectie Tap
     */

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("Debugging", "In onTouchEvent");
        float xTouch;
        float yTouch;
        if((event.getAction() & event.ACTION_MASK) == event.ACTION_UP) {
            xTouch = event.getX();
            yTouch = event.getY();
            takeShot(xTouch, yTouch);
        }

        return true;
    }

    int calculateDistance(){
        distanceFromSub = 1000;
        int currentDistance;
        for(int i = 0 ;i<positions.length; i=i+2) {
            int horizontalGap = (int) horizontalTouched -
                    positions[i];
            int verticalGap = (int) verticalTouched -
                    positions[i+1];
            currentDistance = (int) Math.sqrt(
                    ((horizontalGap * horizontalGap) +
                            (verticalGap * verticalGap)));
            if(currentDistance < distanceFromSub) {
                distanceFromSub = currentDistance;
            }
        }
        return distanceFromSub;
    }
    /*
    Hit or Miss
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void takeShot(float touchX, float touchY){
        Log.d("Debugging", "In takeShot");
        shotsTaken ++;
        horizontalTouched = (int)touchX/ blockSize;
        verticalTouched = (int)touchY/ blockSize;
        for(int i = 0; i<positions.length; i=i+2) {
            hit = horizontalTouched == positions[i]
                    && verticalTouched == positions[i+1];
            if(hit)
                break;
        }

        if(hit) {
            flagCoordintes.clear();
            boom();
        }
            else {
             flagCoordintes.add(new Integer((int) horizontalTouched));
             flagCoordintes.add(new Integer((int) verticalTouched));
             flagCoordintes.add(calculateDistance());
            draw();
        }
    }

    void boom(){
        gameView.setImageBitmap(blankBitmap);
        canvas.drawColor(Color.argb(255, 255, 0, 0));
        paint.setColor(Color.argb(255, 255, 255, 255));
        paint.setTextSize(blockSize*3);
        canvas.drawText("BOOM!", blockSize * 4,
                blockSize * 2, paint);
        paint.setTextSize(blockSize);
        canvas.drawText("Take a shot to start again",
                blockSize,
                blockSize * 6, paint);
        newGame();
    }
    /*
    Debugging
     */
    void printDebuggingTest() {
        paint.setTextSize(blockSize);
        if (debugging) {
            canvas.drawText("numberHorizontalPixels = "
                            + numberHorizontalPixels,
                    50, blockSize * 3, paint);
            canvas.drawText("numberVerticalPixels = "
                            + numberVerticalPixels,
                    50, blockSize * 4, paint);
            canvas.drawText("blockSize = " + blockSize,
                    50, blockSize * 5, paint);
            canvas.drawText("gridWidth = " + gridWidth,
                    50, blockSize * 6, paint);
            canvas.drawText("gridHeight = " + gridHeight,
                    50, blockSize * 7, paint);
            canvas.drawText("horizontalTouched = " +
                            horizontalTouched, 50,
                    blockSize * 8, paint);
            canvas.drawText("verticalTouched = " +
                            verticalTouched, 50,
                    blockSize * 9, paint);
            canvas.drawText("subHorizontalPosition = " +
                            subHorizontalPosition, 50,
                    blockSize * 10, paint);
            canvas.drawText("subVerticalPosition = " +
                            subVerticalPosition, 50,
                    blockSize * 11, paint);
            canvas.drawText("hit = " + hit,
                    50, blockSize * 12, paint);
            canvas.drawText("shotsTaken = " +
                            shotsTaken,
                    50, blockSize * 13, paint);
            canvas.drawText("debugging = " + debugging,
                    50, blockSize * 14, paint);
       }
    }
}