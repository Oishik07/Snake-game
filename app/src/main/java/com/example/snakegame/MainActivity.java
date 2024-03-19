package com.example.snakegame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private TextView scoreTV;
    private String movingPosition="right";  //By default the snake moves to right
    private final List<SnakePoints> snakePointsList = new ArrayList<>();
    private int score=0;
    private static final int pointSize=28;
    private static final int defaultTalePoints=3;
    private static final int snakeColor= Color.CYAN;
    private static final int snakeMovingSpeed=700;
    private int positionX,positionY;
    private Timer timer;
    private Canvas canvas=null;
    private Paint pointColor = null;
    private static int hs=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView=findViewById(R.id.surfaceView);
        scoreTV=findViewById(R.id.scoreTV);

        final AppCompatImageButton topBtn=findViewById(R.id.topBtn);
        final AppCompatImageButton leftBtn=findViewById(R.id.leftBtn);
        final AppCompatImageButton rightBtn=findViewById(R.id.rightBtn);
        final AppCompatImageButton bottomBtn=findViewById(R.id.bottomBtn);

        // adding callback to surfaceview
        surfaceView.getHolder().addCallback(this);

        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!movingPosition.equals("bottom"))
                {
                    movingPosition="top";
                }
            }
        });

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!movingPosition.equals("right"))
                {
                    movingPosition="left";
                }
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!movingPosition.equals("left"))
                {
                    movingPosition="right";
                }
            }
        });

        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!movingPosition.equals("top"))
                {
                    movingPosition="bottom";
                }
            }
        });

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

        this.surfaceHolder=surfaceHolder;
        init();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    private void init()
    {
        snakePointsList.clear();
        scoreTV.setText("0");
        score=0;
        movingPosition="right";
        int startPositionX = (pointSize) * defaultTalePoints;

        for(int i=0;i<defaultTalePoints;i++)
        {
            SnakePoints snakePoints=new SnakePoints(startPositionX,pointSize);
            snakePointsList.add(snakePoints);
            startPositionX=startPositionX-(pointSize*2);
        }

        //add random points
        addPoint();

        //start moving snake
        moveSnake();
    }

    private void addPoint(){

        int surfaceWidth=surfaceView.getWidth()-(pointSize*2);
        int surfaceHeight=surfaceView.getHeight()-(pointSize*2);

        int randomXPosition = new Random().nextInt(surfaceWidth/pointSize);
        int randomYPosition = new Random().nextInt(surfaceHeight/pointSize);

        if((randomXPosition % 2) !=0)
        {
            randomXPosition = randomXPosition + 1;     //we need only even random values
        }

        if((randomYPosition % 2) != 0)
        {
            randomYPosition = randomYPosition + 1;
        }

        positionX = (pointSize * randomXPosition) + pointSize;
        positionY = (pointSize * randomYPosition) + pointSize;
    }

    private void moveSnake(){

        timer=new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                int headPositionX = snakePointsList.get(0).getPositionX();
                int headPositionY = snakePointsList.get(0).getPositionY();

                if(headPositionX==positionX && headPositionY==positionY)
                {
                    growSnake();
                    addPoint();
                }

                switch (movingPosition)
                {
                    case "right":
                        snakePointsList.get(0).setPositionX(headPositionX + (pointSize * 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;

                    case "left":
                        snakePointsList.get(0).setPositionX(headPositionX - (pointSize * 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        break;

                    case "top":
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY - (pointSize*2));
                        break;

                    case "bottom":
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY + (pointSize * 2));
                        break;

                }

                if(checkGameOver(headPositionX,headPositionY))
                {
                    //stop timer / stop moving snake

                    timer.purge();
                    timer.cancel();

                    AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);

                    builder.setMessage("                     Your Score : "+score+"\n                     Highest score : "+hs);
                    builder.setNeutralButton("Close game", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });

                    builder.setTitle("                Game Over !");
                    builder.setCancelable(false);

                    Vibrator vb = (Vibrator)   getSystemService(Context.VIBRATOR_SERVICE);
                    vb.vibrate(71);


                    builder.setPositiveButton("Play again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            init();
                        }
                    });

                    // Timer runs in background , so we need to show dialog on main thread

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            builder.show();
                            if(hs<score)
                            {
                                hs=score;
                                Toast.makeText(MainActivity.this, "Congratulations on achieving the highest score !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else
                {
                    // Lock canvas to draw on it
                    canvas = surfaceHolder.lockCanvas();

                    // clear canvas with white color
                    canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);

                    // Change snake's head position. Other snake points will follow snake's head
                    canvas.drawCircle(snakePointsList.get(0).getPositionX(),snakePointsList.get(0).getPositionY(),pointSize,createPointColor());

                    // Draw random circles on the surface to be eaten by the snake
                    canvas.drawCircle(positionX,positionY,pointSize,createPointColor());

                    for(int i=1;i<snakePointsList.size();i++)
                    {
                        int getTempPositionX = snakePointsList.get(i).getPositionX();
                        int getTempPositionY = snakePointsList.get(i).getPositionY();

                        snakePointsList.get(i).setPositionX(headPositionX);
                        snakePointsList.get(i).setPositionY(headPositionY);
                        canvas.drawCircle(snakePointsList.get(i).getPositionX(),snakePointsList.get(i).getPositionY(),pointSize,createPointColor());

                        headPositionX=getTempPositionX;
                        headPositionY=getTempPositionY;
                    }

                    surfaceHolder.unlockCanvasAndPost(canvas);    // Unlock canvas to draw on surfaceView
                }
            }
        },1000 - snakeMovingSpeed,1000-snakeMovingSpeed);
    }

    private void growSnake()
    {
        SnakePoints snakePoints = new SnakePoints(0,0);

        snakePointsList.add(snakePoints);

        score++;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreTV.setText(String.valueOf(score));
            }
        });
    }

    private boolean checkGameOver(int headPositionX,int headPositionY)
    {
        boolean gameOver = false;

        if(snakePointsList.get(0).getPositionX() < 0 || snakePointsList.get(0).getPositionY() < 0
            || snakePointsList.get(0).getPositionX() >= surfaceView.getWidth() ||
                snakePointsList.get(0).getPositionY() >= surfaceView.getHeight())
        {
            gameOver = true;
        }
        else
        {
            for(int i=1 ; i<snakePointsList.size(); i++)
            {
                if(headPositionX == snakePointsList.get(i).getPositionX() &&
                        headPositionY == snakePointsList.get(i).getPositionY())
                {
                    gameOver=true;
                    break;
                }
            }
        }

        return gameOver;
    }

    private Paint createPointColor()
    {
        if(pointColor==null)
        {
            pointColor=new Paint();
            pointColor.setColor(snakeColor);
            pointColor.setStyle(Paint.Style.FILL);
            pointColor.setAntiAlias(true);              // Smoothness
        }
        return pointColor;
    }
}