package com.example.mobileapplication.Model;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

public class GraphNode extends android.support.v7.widget.AppCompatImageView implements View.OnTouchListener {

    private Bitmap bitmap;
    private Canvas c;
    private Data center;
    private NodeClickListener nodeClickListener;
    private final Float SIZE = 40.0f;
    private List<NodeChild> children = new ArrayList();
    private Data touched;
    private List<Node> nodes = new ArrayList();
    //    private Company company;
    private List<Float> radius = new ArrayList();
    private List<Float> length = new ArrayList();
    // private float length = 600;
    private float mCircleX, mCircleY;
    private float mCircleRadius = 50f;



    public interface NodeClickListener {
        void onClickNode(Data data);
    }

    public void setNodeClickListener (NodeClickListener nodeClickListener) {
        this.nodeClickListener = nodeClickListener;
    }

    public GraphNode(Context context){
        super(context);
        init();
    }

    public GraphNode(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GraphNode(Context context, AttributeSet attrs, int defStyleAttr) {
        super (context, attrs);
        init();
    }

    private void init() {this.setOnTouchListener(this);}


    public  Bitmap getBitmap() {
        return bitmap;
    }

    public void setCenter(Data center) {
        this.center = center;
        invalidate();
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Compute the height required to render the view
        // Assume Width will always be MATCH_PARENT.
        int width = MeasureSpec.getSize(widthMeasureSpec)*2;
        int height = MeasureSpec.getSize(heightMeasureSpec)*2; // Since 3000 is bottom of last Rect to be drawn added and 50 for padding.
        setMeasuredDimension(width, height);
    }

    public void addChildren(NodeChild child){
        children.add(child);
        radius.add(0.0f);
        length.add(0.0f);
        startAnimation(radius.size()-1);
    }

    // override and redraw the circle to cover up the line
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int Width = this.getWidth();
        int Height = this.getHeight();
        int halfWidth = Width / 2;
        int halfHeight = Height / 2;

        if(center!=null){
            nodes.add(new Node(center,halfWidth,halfHeight));
            drawData(canvas, halfWidth, halfHeight, mCircleRadius, Color.parseColor("#F9AA33"), center, touched == center);
        }

        for(int i =0; i<children.size(); i++) {
            Node center = getNode(children.get(i).getCenter());
            List<Data> child = children.get(i).getChildren();

            for(int j=0; j<child.size(); j++) {
                float[] xyData = computePosition(child.size(),j,length.get(i), center.getX(), center.getY());
                float x = xyData[0];
                float y = xyData[1];

                nodes.add(new Node(child.get(j),x,y));
                drawData(canvas, x, y, mCircleRadius, Color.parseColor("#fd3c3c"), child.get(j), touched == child.get(j));
            }
        }

    }

    @Override
    public void onDraw(Canvas canvas) {

        int Width = this.getWidth();
        int Height = this.getHeight();
        int halfWidth = Width / 2;
        int halfHeight = Height / 2;

        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(Width, Height, Bitmap.Config.ARGB_8888);
            this.setImageBitmap(bitmap);
            c = new Canvas(bitmap);
            canvas = c;
        }

        canvas.drawColor(Color.parseColor("#303030"));

        nodes.clear();

        if(center!=null){
            nodes.add(new Node(center,halfWidth,halfHeight));
            drawData(canvas, halfWidth, halfHeight, mCircleRadius, Color.parseColor("#F9AA33"), center, touched == center);
        }


        for(int i =0; i<children.size(); i++) {
            Node center = getNode(children.get(i).getCenter());
            List<Data> child = children.get(i).getChildren();

            for(int j=0; j<child.size(); j++) {
                float[] xyData = computePosition(child.size(),j,length.get(i), center.getX(), center.getY());
                float x = xyData[0];
                float y = xyData[1];

                nodes.add(new Node(child.get(j),x,y));
                drawLine(canvas,x,y, center.getX(), center.getY());
                drawData(canvas, x, y, mCircleRadius, Color.parseColor("#fd3c3c"), child.get(j), touched == child.get(j));
            }
        }
    }


    private void drawData(Canvas canvas, float x, float y , float rad, int color, Data data, boolean isTouched) {
        float strokeWidth = 3;
        if(isTouched) {
            rad = rad +15;
            strokeWidth = 7;
        }

        if (data instanceof Company) {
            drawCircle(canvas, x, y, rad, strokeWidth, Color.parseColor("#F9AA33"));
        }
        else {
            drawCircle(canvas, x, y, rad, strokeWidth, Color.parseColor("#fd3c3c"));
        }
        drawText(canvas, x, y,data.getInitials());
    }


    private void drawCircle(Canvas canvas, float x, float y, float radius, float strokeWidth, int color) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#303030"));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, radius, paint);

        Paint stroke = new Paint();
        stroke.setColor(color);
        stroke.setStrokeWidth(strokeWidth);
        stroke.setStyle(Paint.Style.STROKE);

        if (mCircleX == 0f || mCircleY == 0f) {
            mCircleX = getWidth() / 2;
            mCircleY = getHeight() / 2;
        }

//        canvas.drawCircle(mCircleX, mCircleY, mCircleRadius, paint);
        canvas.drawCircle(x, y, radius, stroke);
    }

    private void drawText(Canvas canvas, float x, float y, String text) {
        Rect bound = new Rect();
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        paint.getTextBounds(text, 0, text.length(), bound);
        canvas.drawText(text, x - bound.centerX(), y - bound.centerY() , paint);
    }

    private void drawLine(Canvas canvas, float startX, float startY, float stopX, float stopY) {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(3);
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    private float[] computePosition(final int nrofNode, final int pos, final float radius, final float cx, final float cy){
        float[] result = new float[2];

        Double angle = (Math.PI * 2 / nrofNode * pos) - (Math.PI / 3);
        result[0] = (float) (radius * Math.cos(angle)) + cx;
        result[1] = (float) (radius * Math.sin(angle)) + cy;
        return result;
    }


    private Node getNode(Data data) {
        Node node = null;

        for(int i=0; i <nodes.size(); i++){
            if(nodes.get(i).getData().equals(data)) {
                node = nodes.get(i);
                break;
            }
        }
        return node;
    }

    private boolean isNodeTouched(float x,float y, Node node) {
        float diffX = node.getX() - x;
        float diffY = node.getY() - y;

        if(diffX >= -50 && diffX <= 50 && diffY >= -50 && diffY <= 50) {
            return true;
        }

        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent){
        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            for(Node node: nodes) {
                if(isNodeTouched(motionEvent.getX(),motionEvent.getY(), node)) {
                    touched = node.getData();
                    this.nodeClickListener.onClickNode(touched);
                    invalidate();
                    break;
                }
            }
        }
        return true;
    }

    private void startAnimation(final int i) {
        int vWidth = this.getWidth();
        int vHeight = this.getHeight();
        float mRadius = (float) (Math.min(vWidth, vHeight) / 2 * 0.3);
        if(i>0) mRadius = mRadius / 3;

        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator animator = ValueAnimator.ofFloat(0, mRadius + 20);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                length.set(i, value);
                invalidate();
            }
        });

        ValueAnimator sizeAnimator = ValueAnimator.ofFloat(0, SIZE);
        sizeAnimator.setDuration(500);
        sizeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                radius.set(i, value);
                invalidate();
            }
        });

        animatorSet.play(animator).with(sizeAnimator);
        animatorSet.start();

    }


}