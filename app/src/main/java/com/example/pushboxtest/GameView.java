package com.example.pushboxtest;

import java.util.ArrayList;
import java.util.Random;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class GameView extends View {
    public PushBoxMain gameMain = null;
    public static int width = 0;
    public static int height = 0;

    public static int xoff = 25;
    public static int yoff = 50;

    public final int WALL = 1;
    public final int GOAL = 2;
    public final int ROAD = 3;
    public final int BOX = 4;
    public final int BOX_AT_GOAL = 5;
    public final int WORKER = 6;
    public final int BACK = 7;
    public final int UP = 8;
    public final int DOWN = 9;
    public final int LEFT = 10;
    public final int RIGHT = 11;
    public final int MUSIC = 12;
    public final int TREASURE = 13;
    public final int FUDAI = 14;

    public Bitmap pic[] = null;
    private int[][] map = null;
    private int[][] tem = null;
    private int gate = 0;
    private int manRow = 0;
    private int manColumn = 0;
    public int widthPicSize = 30;
    public int heightPicSize = 30;
    public int row = 0;
    public int column = 0;
    private int nowRound = 0;
    private final int AddParameter = 10;

    String question[] = {
            "坤宁宫每天杀猪是真的吗",
            "紫禁城狮子逗弄小狮子的是雄狮子还是雌狮子",
            "紫禁城是哪个皇帝建的",
            "故宫里的大铜缸是做什么用的",
            "题写乾清宫的正大光明匾的是谁",
            "紫禁城里有路灯吗"
    };
    String ansA[] = {
        "真的",
            "雄狮子",
            "朱棣",
            "防火",
            "康熙皇帝玄烨",
            "有"
    };
    String ansB[] = {
            "假的",
            "雌狮子",
            "朱元璋",
            "装饰",
            "顺治皇帝福临",
            "没有"
    };
    int trueAns[] = {
            0,
            1,
            0,
            0,
            1,
            1
    };

    int nowAns;
    int askYouAQuestionIdx;
    void askYouAQuestion() {
        if(nowRound > trueAns.length) {
            return ;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(gameMain);
        builder.setTitle("答题时间");

        askYouAQuestionIdx = nowRound - 1;
        nowAns = 0;
        builder.setMessage(question[askYouAQuestionIdx]);
        builder.setPositiveButton(ansA[askYouAQuestionIdx], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                AlertDialog.Builder nowBuilder = new AlertDialog.Builder(gameMain);
                if(nowAns == trueAns[askYouAQuestionIdx]) {
                    nowBuilder.setMessage("恭喜你获得神秘礼包");
                } else {
                    nowBuilder.setMessage("很遗憾，功力尚需修炼");
                }
                nowBuilder.show();
            }
        });

        builder.setNegativeButton(ansB[askYouAQuestionIdx], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                nowAns = 1;
                AlertDialog.Builder nowBuilder = new AlertDialog.Builder(gameMain);
                if(nowAns == trueAns[askYouAQuestionIdx]) {
                    nowBuilder.setMessage("恭喜你获得神秘礼包");
                } else {
                    nowBuilder.setMessage("很遗憾，功力尚需修炼");
                }
                nowBuilder.show();
            }
        });

        builder.create();
        builder.show();

        fuDaiX = -1;
        fuDaiY = -1;
    }

    boolean showTimer = false;
    private CountDownTimer timer = new CountDownTimer(5000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            showTimer = true;
            PushBoxMain.textView.setText((millisUntilFinished / 1000) + "秒后宝藏图标即将消失");
        }

        @Override
        public void onFinish() {
            showTimer = false;
            PushBoxMain.textView.setEnabled(true);
            //PushBoxMain.textView.setText("游戏开始");

            row = map.length;
            column = map[0].length;

            for(int i = 0; i < row; i++) {
                for(int j = 0; j < column; j++) {
                    canSee[i][j] = false;
                }
            }
            invalidate();
        }
    };

    //MediaPlayer m, m1;

    //内部类
    class CurrentMap {
        int[][] curMap;

        public CurrentMap(int[][] myMap) {
            int r = myMap.length;
            int c = myMap[0].length;
            int[][] temp = new int[r][c];
            for (int i = 0; i < r; i++)
                for (int j = 0; j < c; j++)
                    temp[i][j] = myMap[i][j];
            this.curMap = temp;
        }

        public int[][] getMap() {
            return curMap;
        }
    }

    private ArrayList<CurrentMap> list = new ArrayList<CurrentMap>();
    boolean flag[][] = new boolean[20][20];
    boolean vis[][] = new boolean[20][20];
    boolean canSee[][] = new boolean[20][20];
    int totTreasure = 5;

    class LocationInformation {
        int x, y;
        LocationInformation() {}
        LocationInformation(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    LocationInformation locationRecord[] = new LocationInformation[totTreasure + 1];

    public boolean nextPermutation(int[] nums, int begin, int end) {
        for (int i = end - 2; i >= begin; i--)
            if (nums[i] < nums[i + 1])
                for (int j = end - 1; j > i; j--)
                    if (nums[i] < nums[j]) {
                        swap(nums, i, j);
                        reverse(nums, i + 1, end - 1);
                        return true;
                    }
        return false;
    }

    private void swap(int[] nums, int left, int right) {
        int temp = nums[left];
        nums[left] = nums[right];
        nums[right] = temp;
    }

    private void reverse(int[] nums, int left, int right) {
        while (left < right)
            swap(nums, left++, right--);
    }

    int mxStep;
    boolean judgeTimeOut;

    private CountDownTimer timer2 = new CountDownTimer(200000, 200) {
        @Override
        public void onTick(long millisUntilFinished) {
            if(mxStep >= 0) {
                if(!showTimer)
                    PushBoxMain.textView.setText(String.valueOf(millisUntilFinished / 1000) + "秒" + " " + String.valueOf(mxStep) + "步");
            }
            //PushBoxMain.textView2.setText();
        }

        @Override
        public void onFinish() {
            //通过设置步数，使得超过时限后游戏结束
            mxStep = -1;
            judgeTimeOut = true;
        }
    };

    private void initCanSee() {
        int row = map.length;
        int column = map[0].length;
        timer.start();
        for(int i = 0; i < row; i++) {
            for(int j = 0; j < column; j++) {
                canSee[i][j] = true;
            }
        }
    }

    boolean havAFuDai() {
        Random random = new Random();
        return random.nextInt(2) < 1;
    }

    int fuDaiX, fuDaiY;
    private void initMyNewMap() {
        showTimer = false;

        initCanSee();
        judgeTimeOut = false;
        timer2.start();

        row = map.length;
        column = map[0].length;

        fuDaiX = -1;
        fuDaiY = -1;

        for(int i = 0; i < row; i++) {
            for(int j = 0; j < column; j++) {
                flag[i][j] = vis[i][j] = false;
            }
        }

        int totRow = row - 2;
        int totColumn = column - 2;
        for(int i = 0; i < row; i++) {
            for(int j = 0; j < column; j++) {
                if(i == 0 || j == 0 || i == row - 1 || j == column - 1) {
                    map[i][j] = 1;
                } else {
                    map[i][j] = 3;
                }
            }
        }

        totTreasure = nowRound + 3;
        Random rd = new Random();
        for(int i = 0; i < totTreasure; i++) {
            int idx = rd.nextInt(totRow * totColumn);
            while(flag[idx / totColumn + 1][idx % totColumn + 1]) {
                idx = rd.nextInt(totRow * totColumn);
            }
            flag[idx / totColumn + 1][idx % totColumn + 1] = true;
            //map[idx / totColumn + 1][idx % totColumn + 1] = TREASURE;
            locationRecord[i] = new LocationInformation(idx / totColumn + 1, idx % totColumn + 1);
            locationRecord[i].x = idx / totColumn + 1;
            locationRecord[i].y = idx % totColumn + 1;
        }

        while(true) {
            int idx = rd.nextInt(totRow * totColumn);
            if (!flag[idx / totColumn + 1][idx % totColumn + 1]) {
                map[idx / totColumn + 1][idx % totColumn + 1] = 6;
                locationRecord[totTreasure] = new LocationInformation(idx / totColumn + 1, idx % totColumn + 1);
                locationRecord[totTreasure].x = idx / totColumn + 1;
                locationRecord[totTreasure].y = idx % totColumn + 1;
                break;
            }
        }

        if(havAFuDai()) {
            int idx = rd.nextInt(totRow * totColumn);
            while(flag[idx / totColumn + 1][idx % totColumn + 1]) {
                idx = rd.nextInt(totRow * totColumn);
            }
            fuDaiX = idx / totColumn + 1;
            fuDaiY = idx % totColumn + 1;
        }

        int a[] = new int[totTreasure + 1];
        for(int i = 0; i <= totTreasure; i++) {
            a[i] = i;
        }
        mxStep = (1 << 30);
        do {
            int nowStep = 0;
            for(int i = 0; i < totTreasure; i++) {
                nowStep += Math.abs(locationRecord[a[i]].x - locationRecord[a[i + 1]].x)
                        + Math.abs(locationRecord[a[i]].y - locationRecord[a[i + 1]].y);
            }
            mxStep = Math.min(nowStep, mxStep);
        }while(nextPermutation(a, 0, totTreasure));

        nowRound++;
        mxStep += AddParameter / nowRound;

        //PushBoxMain.textView2.setEnabled(true);
        //PushBoxMain.textView2.setText(String.valueOf(mxStep));
        //PushBoxMain.textView.setText("aabbccdd");
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub

        gameMain = (PushBoxMain) context;
        WindowManager win = gameMain.getWindowManager();

        width = win.getDefaultDisplay().getWidth();
        height = win.getDefaultDisplay().getHeight();

        Log.d("width", String.valueOf(width));
        Log.d("count", String.valueOf(MapList.getCount()));

        intMap();
        intPic();
        initSounds();
    }

    public void initSounds() {//初始化声音的方法
        /*初始化MediaPlayer对象*/

        //m = MediaPlayer.create(this.getContext(), R.raw.bgm);
        //m.start();
        //m1 = MediaPlayer.create(this.getContext(), R.raw.dingdong );
    }

    int mxTimeCanSee = 0;
    private void intMap() {
        mxTimeCanSee = 0;

        // TODO Auto-generated method stub
        map = MapList.getMap(gate);

        //init my new map
        initMyNewMap();
        // tem=MapList.getMap(gate);
        getMapDetail();
        getManPosition();
    }

    private void getManPosition() {
        // TODO Auto-generated method stub
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == WORKER) {
                    manRow = i;
                    manColumn = j;
                    break;
                }
            }
    }

	/*
	 * public int[][] getMap(int grade) { return MapList.getMap(grade); }
	 */

    private void getMapDetail() {
        // TODO Auto-generated method stub
        row = map.length;
        column = map[0].length;

        xoff = width / (column + 2);
        yoff = height / (row + 2);

        widthPicSize = (int) Math.floor((width - 2 * xoff) / column);
        heightPicSize = (int)Math.floor((height - 6 * yoff) / row);

        tem = MapList.getMap(gate);
    }

    private void intPic() {
        // TODO Auto-generated method stub
        pic = new Bitmap[20];
        loadPic(WALL, this.getResources().getDrawable(R.drawable.wall));
        loadPic(GOAL, this.getResources().getDrawable(R.drawable.goal));
        loadPic(ROAD, this.getResources().getDrawable(R.drawable.road));
        loadPic(BOX, this.getResources().getDrawable(R.drawable.box));
        loadPic(BOX_AT_GOAL, this.getResources().getDrawable(R.drawable.box_at_goal));
        loadPic(WORKER, this.getResources().getDrawable(R.drawable.man));

        loadPic(BACK, this.getResources().getDrawable(R.drawable.back));
        loadPic(UP, this.getResources().getDrawable(R.drawable.up));
        loadPic(DOWN, this.getResources().getDrawable(R.drawable.down));
        loadPic(LEFT, this.getResources().getDrawable(R.drawable.left));
        loadPic(RIGHT, this.getResources().getDrawable(R.drawable.right));
        loadPic(MUSIC, this.getResources().getDrawable(R.drawable.music));
        loadPic(TREASURE, this.getResources().getDrawable(R.drawable.treasure));
        loadPic(FUDAI, this.getResources().getDrawable(R.drawable.fudai));
    }

    private void loadPic(int KEY, Drawable dw) {
        // TODO Auto-generated method stub
        Bitmap bm = Bitmap.createBitmap(widthPicSize, heightPicSize,
                Bitmap.Config.ARGB_8888);
        dw.setBounds(0, 0, widthPicSize, heightPicSize);
        Canvas cs = new Canvas(bm);
        dw.draw(cs);
        pic[KEY] = bm;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        Paint paint = new Paint();
        paint.setTextSize(20f);
        paint.setColor(Color.WHITE);
        canvas.drawText("第" + String.valueOf(nowRound) + "关", 2 * width / 5, yoff / 2, paint);
        for (int i = 0; i < row; i++)
            for (int j = 0; j < column; j++) {
                if(map[i][j] == WORKER) {
                    canvas.drawBitmap(pic[WORKER], xoff + j * widthPicSize, yoff + i * heightPicSize, paint);
                }
                else if(i == fuDaiX && j == fuDaiY && canSee[i][j]) {
                    canvas.drawBitmap(pic[FUDAI], xoff + j * widthPicSize, yoff + i * heightPicSize, paint);
                }
                else if (map[i][j] > 0) {
                    if(!flag[i][j]) {
                        canvas.drawBitmap(pic[map[i][j]], xoff + j * widthPicSize, yoff + i * heightPicSize, paint);
                    }
                    else {
                        if(canSee[i][j]) {
                            canvas.drawBitmap(pic[13], xoff + j * widthPicSize, yoff + i * heightPicSize, paint);
                        } else if(!vis[i][j]) {
                            canvas.drawBitmap(pic[map[i][j]], xoff + j * widthPicSize, yoff + i * heightPicSize, paint);
                        } else if(map[i][j] != WORKER){
                            canvas.drawBitmap(pic[GOAL], xoff + j * widthPicSize, yoff + i * heightPicSize, paint);
                        } else {
                            canvas.drawBitmap(pic[WORKER], xoff + j * widthPicSize, yoff + i * heightPicSize, paint);
                        }
                    }
                }
            }

        canvas.drawBitmap(pic[BACK], xoff + widthPicSize, 2 * yoff + row * heightPicSize, paint);
        canvas.drawBitmap(pic[UP], xoff + 3 * widthPicSize, 2 * yoff + row * heightPicSize, paint);
        canvas.drawBitmap(pic[DOWN], xoff + 5 * widthPicSize, 2 * yoff + row * heightPicSize, paint);
        canvas.drawBitmap(pic[LEFT], xoff + 7 * widthPicSize, 2 * yoff + row * heightPicSize, paint);
        canvas.drawBitmap(pic[RIGHT], xoff + 9 * widthPicSize, 2 * yoff + row * heightPicSize, paint);
        canvas.drawBitmap(pic[MUSIC], xoff + 11 * widthPicSize, 2 * yoff + row * heightPicSize, paint);
        super.onDraw(canvas);
    }

    private int roadOrGoal() {
        // TODO Auto-generated method stub
        int result = ROAD;
        if (tem[manRow][manColumn] == GOAL)
            result = GOAL;
        return result;
    }

    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(gameIsFinished()) {
            nextGate(true);
            return true;
        } else if(isStepOutOfRange()) {
            nextGate(false);
            return true;
        }

        for(int i = 0; i < row; i++) {
            for(int j = 0; j < column; j++) {
                if(canSee[i][j]) {
                    return true;
                }
            }
        }

        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            moveDown();

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            moveUp();

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            moveLeft();

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            moveRight();

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            backMap();
        }

        if (gameIsFinished()) {
            nextGate(true);
        } else if(isStepOutOfRange()) {
            nextGate(false);
        }
        invalidate();

        --mxStep;
        //PushBoxMain.mxStepTextView.setText(--mxStep);

        return true;
    }*/

    private float x = 0;
    private float y = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(gameIsFinished()) {
            nextGate(true);
            return super.onTouchEvent(event);
        } else if(isStepOutOfRange()) {
            nextGate(false);
            return super.onTouchEvent(event);
        }

        for(int i = 0; i < row; i++) {
            for(int j = 0; j < column; j++) {
                if(canSee[i][j]) {
                    return super.onTouchEvent(event);
                }
            }
        }

        // TODO Auto-generated method stub
        x = event.getX();
        y = event.getY();

        //在图标点触
        if (y > 2 * yoff + row * heightPicSize && y < 2 * yoff + row * heightPicSize + heightPicSize) {
            if (x > xoff + widthPicSize && x < xoff + 2 * widthPicSize) {
                backMap();
            } else if (x > xoff + 3 * widthPicSize && x < xoff + 4 * widthPicSize) {
                moveUp();
                mxStep--;
            } else if (x > xoff + 5 * widthPicSize && x < xoff + 6 * widthPicSize) {
                moveDown();
                mxStep--;
            } else if (x > xoff + 7 * widthPicSize && x < xoff + 8 * widthPicSize) {
                moveLeft();
                mxStep--;
            } else if (x > xoff + 9 * widthPicSize && x < xoff + 10 * widthPicSize) {
                moveRight();
                mxStep--;
            } else if (x > xoff + 11 * widthPicSize && x < xoff + 12 * widthPicSize) {
                //if (!m.isPlaying()) {
                //    m.start();//播放声音
                //} else
                //    m.pause();
                if(mxTimeCanSee++ < 3)
                    initCanSee();
            }
        } else {
            //在图中点触
            if (x > xoff + manColumn * widthPicSize && x < xoff + manColumn * widthPicSize + widthPicSize) {
                if (y < yoff + manRow * heightPicSize)
                    moveUp();

                else if (y > yoff + manRow * heightPicSize + heightPicSize)
                    moveDown();

                --mxStep;
            } else if (y > yoff + manRow * heightPicSize && y < yoff + manRow * heightPicSize + heightPicSize) {
                if (x < xoff + manColumn * widthPicSize)
                    moveLeft();

                if (x > xoff + manColumn * widthPicSize + widthPicSize)
                    moveRight();

                --mxStep;
            }
        }
        if (gameIsFinished()) {
            nextGate(true);
        } else if(isStepOutOfRange()) {
            nextGate(false);
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    private void backMap() {
        if (list.size() > 0) {
            CurrentMap priMap = list.get(list.size() - 1);
            map = priMap.getMap();
            getManPosition();
            list.remove(list.size() - 1);
            mxStep++;
        } else
            Toast.makeText(this.getContext(), "You can't back the game!", Toast.LENGTH_LONG).show();
    }

    private void storeMap(int[][] maps) {
        CurrentMap curMap = new CurrentMap(maps);
        list.add(curMap);
        if (list.size() > 10)
            list.remove(0);
    }

    private boolean gameIsFinished() {
        // TODO Auto-generated method stub
        /*boolean gameFinsh = true;
        for (int i = 0; i < row; i++)
            for (int j = 0; j < column; j++)
                if (map[i][j] == BOX)
                    gameFinsh = false;

        return gameFinsh;*/

        for(int i = 0; i < row; i++) {
            for(int j = 0; j < column; j++) {
                if(flag[i][j] == true && !vis[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isStepOutOfRange() {
        return mxStep < 0;
    }

    private void nextGate(boolean isAllFound) {
        // TODO Auto-generated method stub
        if (MapList.getCount() == gate + 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(gameMain);
            if(isAllFound)builder.setTitle("恭喜你通关了");
            else  {
                nowRound--;
                if(judgeTimeOut) builder.setTitle("很遗憾，您已超时");
                else builder.setTitle("很遗憾，您已超过最大的步数");
            }
            builder.setMessage("开始新一关的游戏还是退出?");
            builder.setPositiveButton("新一关", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    gate = 0;
                    intMap();
                    invalidate();
                }

            });
            builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    gameMain.finish();//结束游戏
                    //m.stop(); //关闭背景音乐
                }

            });
            builder.create();
            builder.show();
        } else {
            gate++;
            //m1.start();//闯关音乐欢呼声响起
            intMap();
        }
    }

    /*String msgOfCulturalRelic[] = {
            "《清明上河图》，画卷生动记录了中国十二世纪北宋汴京的城市面貌和当时社会各阶层人民的生活状况。是汴京当年繁荣的见证，也是北宋城市经济情况的写照。",
            "《伯远帖》，王羲之家族唯一实名真迹。是乾隆帝的爱物。",
            "《游春图》，这件隋代珍品《游春图》代表了中国早期山水画的面貌。从艺术成就上，这幅画是中国山水画中“金碧山水”画法的先声。",
            "《文苑图》，五代真迹名品。少唐画气息，最明显的是衣纹线条颤动曲折，像极了五代周文矩所创的“战笔描”。",
            "《五牛图》，中国已知的最早纸本绘画。",
            "《写生蛱蝶图》，这幅作品体现了宋代花卉作品的新风格",
            "《十咏图》，这是唯一一幅张先的绘画作品。在这幅山水人物画中，建筑楼阁有花草树木掩映，小亭栏杆曲折相应，环境幽雅，气象恢宏。",
            "乾隆款金瓯永固杯，为故宫宫廷文物的代表作。根据清“内务府活计档”记载，乾隆皇帝对此杯的制作十分重视，不仅调用内库黄金、珍珠、宝石等珍贵材料，而且精工细作，曾多次修改，直至皇帝满意为止。",
            "张成造款雕漆云纹盘，为故宫漆器的代表作品。此盘为研究元代剔犀工艺提供了可靠而精美的实例。",
            "青玉云龙纹炉，为故宫玉器的代表作品。",
            "掐丝珐琅缠枝莲纹象耳炉，此器釉质莹润，有的部分釉质呈玻璃般的透明状，珐琅色泽浑厚谐调，富丽典雅，是一件高水平的元代掐丝珐琅作品。",
            "《梅鹊图》，为故宫织绣的代表作品。"
    };*/

    private void moveDown() {
        // TODO Auto-generated method stub
        if (map[manRow + 1][manColumn] == BOX || map[manRow + 1][manColumn] == BOX_AT_GOAL) {
            if (map[manRow + 2][manColumn] == GOAL || map[manRow + 2][manColumn] == ROAD) {
                storeMap(map);
                map[manRow + 2][manColumn] = map[manRow + 2][manColumn] == GOAL ? BOX_AT_GOAL
                        : BOX;
                map[manRow + 1][manColumn] = WORKER;
                map[manRow][manColumn] = roadOrGoal();
                manRow++;

                vis[manRow][manColumn] = true;
            }
        } else {
            if (map[manRow + 1][manColumn] == ROAD || map[manRow + 1][manColumn] == GOAL) {
                storeMap(map);
                map[manRow + 1][manColumn] = WORKER;
                map[manRow][manColumn] = roadOrGoal();
                manRow++;

                vis[manRow][manColumn] = true;
            }
        }
        /*if(flag[manRow][manColumn]) {
            AlertDialog.Builder builder = new AlertDialog.Builder(gameMain);
            Random now = new Random();
            builder.setMessage(msgOfCulturalRelic[now.nextInt(msgOfCulturalRelic.length)]);
            builder.show();
        }*/

        if(manRow == fuDaiX && manColumn == fuDaiY) {
            askYouAQuestion();
        }
    }

    private void moveUp() {
        // TODO Auto-generated method stub
        if (map[manRow - 1][manColumn] == BOX || map[manRow - 1][manColumn] == BOX_AT_GOAL) {
            if (map[manRow - 2][manColumn] == GOAL || map[manRow - 2][manColumn] == ROAD) {
                storeMap(map);
                map[manRow - 2][manColumn] = map[manRow - 2][manColumn] == GOAL ? BOX_AT_GOAL : BOX;
                map[manRow - 1][manColumn] = WORKER;
                map[manRow][manColumn] = roadOrGoal();
                manRow--;

                vis[manRow][manColumn] = true;
            }
        } else {
            if (map[manRow - 1][manColumn] == ROAD || map[manRow - 1][manColumn] == GOAL) {
                storeMap(map);
                map[manRow - 1][manColumn] = WORKER;
                map[manRow][manColumn] = roadOrGoal();
                manRow--;

                vis[manRow][manColumn] = true;
            }
        }
        /*if(flag[manRow][manColumn]) {
            AlertDialog.Builder builder = new AlertDialog.Builder(gameMain);
            Random now = new Random();
            builder.setMessage(msgOfCulturalRelic[now.nextInt(msgOfCulturalRelic.length)]);
            builder.show();
        }*/

        if(manRow == fuDaiX && manColumn == fuDaiY) {
            askYouAQuestion();
        }
    }

    private void moveLeft() {
        // TODO Auto-generated method stub
        if (map[manRow][manColumn - 1] == BOX || map[manRow][manColumn - 1] == BOX_AT_GOAL) {
            if (map[manRow][manColumn - 2] == GOAL || map[manRow][manColumn - 2] == ROAD) {
                storeMap(map);
                map[manRow][manColumn - 2] = map[manRow][manColumn - 2] == GOAL ? BOX_AT_GOAL : BOX;
                map[manRow][manColumn - 1] = WORKER;
                map[manRow][manColumn] = roadOrGoal();
                manColumn--;

                vis[manRow][manColumn] = true;
            }
        } else {
            if (map[manRow][manColumn - 1] == ROAD || map[manRow][manColumn - 1] == GOAL) {
                storeMap(map);
                map[manRow][manColumn - 1] = WORKER;
                map[manRow][manColumn] = roadOrGoal();
                manColumn--;

                vis[manRow][manColumn] = true;
            }
        }
        /*if(flag[manRow][manColumn]) {
            AlertDialog.Builder builder = new AlertDialog.Builder(gameMain);
            Random now = new Random();
            builder.setMessage(msgOfCulturalRelic[now.nextInt(msgOfCulturalRelic.length)]);
            builder.show();
        }*/

        if(manRow == fuDaiX && manColumn == fuDaiY) {
            askYouAQuestion();
        }
    }

    private void moveRight() {
        // TODO Auto-generated method stub
        if (map[manRow][manColumn + 1] == BOX || map[manRow][manColumn + 1] == BOX_AT_GOAL) {
            if (map[manRow][manColumn + 2] == GOAL || map[manRow][manColumn + 2] == ROAD) {
                storeMap(map);
                map[manRow][manColumn + 2] = map[manRow][manColumn + 2] == GOAL ? BOX_AT_GOAL : BOX;
                map[manRow][manColumn + 1] = WORKER;
                map[manRow][manColumn] = roadOrGoal();
                manColumn++;

                vis[manRow][manColumn] = true;
            }
        } else {
            if (map[manRow][manColumn + 1] == ROAD || map[manRow][manColumn + 1] == GOAL) {
                storeMap(map);
                map[manRow][manColumn + 1] = WORKER;
                map[manRow][manColumn] = roadOrGoal();
                manColumn++;

                vis[manRow][manColumn] = true;
            }
        }
        /*if(flag[manRow][manColumn]) {
            AlertDialog.Builder builder = new AlertDialog.Builder(gameMain);
            Random now = new Random();
            builder.setMessage(msgOfCulturalRelic[now.nextInt(msgOfCulturalRelic.length)]);
            builder.show();
        }*/

        if(manRow == fuDaiX && manColumn == fuDaiY) {
            askYouAQuestion();
        }
    }
}
