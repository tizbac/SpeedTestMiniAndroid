package com.bacocco.speedtestmini;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;


public class SpeedGauge extends View {
	private Paint tickspaint;
	private Paint indicatorpaint;
	private static final int TICKS = 100;
	private static final float RADSxTICK = (float)Math.PI/(float)TICKS;
	private static final int BIGGERTICKEVERY = TICKS/10;
	
	double curvalue = 0.0;
	double endvalue = 100.0;
	
	public void setEndValue(double endvalue)
	{
		this.endvalue = endvalue;
		invalidate();
		
	}
	public void setValue(double curvalue)
	{
		this.curvalue = curvalue;
		invalidate();
	}
	private void init()
	{
		tickspaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		tickspaint.setColor(0xff000000);
		tickspaint.setStrokeWidth(2.5f);
		tickspaint.setTextSize(50.0f);
		indicatorpaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		indicatorpaint.setColor(0xffff0000);
		indicatorpaint.setStrokeWidth(10.0f);
		indicatorpaint.setStyle(Style.FILL);
	}
	public SpeedGauge(Context context) {
		super(context);
		init();
	}

	public SpeedGauge(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SpeedGauge(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	    int widthSize = MeasureSpec.getSize(widthMeasureSpec);
	    int heightMode = MeasureSpec.getMode(heightMeasureSpec);
	    int heightSize = MeasureSpec.getSize(heightMeasureSpec);
	    int w;
	    int h;
	    if ( widthMode == MeasureSpec.EXACTLY )
	    {
	    	w = widthSize;
	    	h = widthSize/2;
	    }else if (widthMode == MeasureSpec.AT_MOST) 
	    {
	    	w = widthSize;
	    	h = widthSize/2;
	    }else{
	    	w = 500;
	    	h = 250;
	    }
	    setMeasuredDimension(w, h);
	}
	
	@Override
	public void draw(Canvas canvas) {
		float outerradius = getWidth()/2;
		float innerradius = outerradius-10.0f;
		float innerradius2 = outerradius-20.0f;
		float innerradius3 = outerradius-40.0f;
		float halfWidth = getWidth()/2;
		float h = getHeight();
		int k = 0;
		for ( int i = 0; i <= TICKS; i++, k++ )
		{
			float cos = (float) Math.cos(i*RADSxTICK);
			float sin = (float) Math.sin(i*RADSxTICK);
			
			if ( k == BIGGERTICKEVERY )
			{
				canvas.drawLine(cos*innerradius2+halfWidth, h-sin*innerradius2, cos*outerradius+halfWidth, h-sin*outerradius, tickspaint);
				k = 0;
				
				canvas.drawText(""+(TICKS-i),cos*innerradius3+halfWidth-(TICKS-i)*.5f, h-sin*innerradius3+40,tickspaint);
			}
			else
				canvas.drawLine(cos*innerradius+halfWidth, h-sin*innerradius, cos*outerradius+halfWidth, h-sin*outerradius, tickspaint);
		}
		float prop = (float) (curvalue/endvalue);
		float cos = (float) Math.cos(Math.PI-Math.PI*prop);
		float  sin = (float) Math.sin(Math.PI-Math.PI*prop);
		canvas.drawLine(halfWidth, h, halfWidth+cos*innerradius3, h-sin*innerradius3, indicatorpaint);
		
		float cos2 = (float) Math.cos(Math.PI-Math.PI*prop+Math.PI/60.0);
		float  sin2 = (float) Math.sin(Math.PI-Math.PI*prop+Math.PI/60.0);
		float cos3 = (float) Math.cos(Math.PI-Math.PI*prop-Math.PI/60.0);
		float  sin3 = (float) Math.sin(Math.PI-Math.PI*prop-Math.PI/60.0);
		
		Path p = new Path();
		p.reset();
		p.moveTo(halfWidth+cos2*innerradius3, h-sin2*innerradius3);
		p.lineTo(halfWidth+cos3*innerradius3, h-sin3*innerradius3);
		p.lineTo(halfWidth+cos*innerradius2, h-sin*innerradius2);
		p.lineTo(halfWidth+cos2*innerradius3, h-sin2*innerradius3);
		canvas.drawPath(p, indicatorpaint);
	}
	
	
}
