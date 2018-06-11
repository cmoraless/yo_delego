package kiwigroup.yodelego.customui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.Button;

public class CenteredIconButton extends Button {
    public CenteredIconButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CenteredIconButton(Context context) {
        super(context);
    }

    public CenteredIconButton(Context context, AttributeSet attrs, int style) {
        super(context, attrs, style);
    }

    private Drawable mLeftDrawable;

    @Override
    //Overriden to work only with a left drawable.
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable left,
                                                        Drawable top, Drawable right, Drawable bottom) {
        if(left == null) return;
        left.setBounds(0, 0, left.getIntrinsicWidth(), left.getIntrinsicHeight());
        mLeftDrawable = left;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //transform the canvas so we can draw both image and text at center.
        canvas.save();
        canvas.translate(2+mLeftDrawable.getIntrinsicWidth()/2, 0);
        super.onDraw(canvas);
        canvas.restore();
        canvas.save();
        int widthOfText = (int)getPaint().measureText(getText().toString());
        int left = (getWidth()+widthOfText)/2 - mLeftDrawable.getIntrinsicWidth() - 2;
        canvas.translate(left, (getHeight()-mLeftDrawable.getIntrinsicHeight())/2);
        mLeftDrawable.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getMeasuredHeight();
        height = Math.max(height, mLeftDrawable.getIntrinsicHeight() + getPaddingTop() + getPaddingBottom());
        setMeasuredDimension(getMeasuredWidth(), height);
    }
}