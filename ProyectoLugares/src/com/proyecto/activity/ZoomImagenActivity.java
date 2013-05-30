package com.proyecto.activity;



import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.GestureDetector.OnGestureListener;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * Actividad que permite realizar un zoom sobre la imagen y moverse a través de la imagen
 */

public class ZoomImagenActivity extends Activity implements OnGestureListener {
    private int screen_w = 400;
    private int screen_h = 400;
    private int     scrollX     = 0;
    private int     scrollY     = 0;

    private int     imageWidth  = 0;
    private int     imageHeight = 0;

    private MoveImageView   moveImageView;
    private Bitmap          sourceBmp;
    private Bitmap          showBmp;
    private Resources       res;
    private Paint           paint;
    private GestureDetector gestureScanner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        res = getResources();

        DisplayMetrics dm = res.getDisplayMetrics();
        
        screen_w = dm.widthPixels;
        screen_h = dm.heightPixels;

        gestureScanner = new GestureDetector(this);
        paint = new Paint();

        String rutaImagen="";
        
        //Comprobamos en que modo debemos cargar la activity
        Bundle extras = getIntent().getExtras();
        
        if(extras != null) {
            rutaImagen = extras.getString("rutaImagen");
        }

        sourceBmp = BitmapFactory.decodeFile(rutaImagen);

        sourceBmp = Bitmap.createScaledBitmap(sourceBmp, 500, 500,true);
 
        if(sourceBmp!=null){
            imageWidth = sourceBmp.getWidth();
            imageHeight = sourceBmp.getHeight();
            showBmp = Bitmap.createBitmap(sourceBmp);

            moveImageView = new MoveImageView(this);
            setContentView(moveImageView, new ViewGroup.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
            
            Toast.makeText(getApplicationContext(),imageWidth+"X"+imageHeight,Toast.LENGTH_LONG).show();
            
        }else{
            finish();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        return gestureScanner.onTouchEvent(me);
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        try{
        moveImageView.handleScroll(distanceX, distanceY);
        }catch(IllegalArgumentException iare){
            
            Toast.makeText(getApplicationContext(),imageWidth+"X"+imageHeight,Toast.LENGTH_LONG).show();
        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return true;
    }
    
    // Comprimir bitmap
    public Bitmap DecodeUri(Uri selectedImage, ContentResolver contResolver) {

        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(contResolver.openInputStream(selectedImage), null, o);

            // The new size we want to scale to
            final int REQUIRED_SIZE = 140;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(contResolver.openInputStream(selectedImage), null, o2);

        } catch (Exception e) {
            return null;

        }

    }

    class MoveImageView extends View {
        public MoveImageView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(showBmp, 0, 0, paint);
        }

        public void handleScroll(float distX, float distY) throws IllegalArgumentException{
            if ((scrollX <= imageWidth - screen_w) && (scrollX >= 0)) {
                scrollX += distX;
            }

            if ((scrollY <= imageHeight - screen_h) && (scrollY >= 0)) {
                scrollY += distY;
            }

            if (scrollX < 0) scrollX = 0;
            if (scrollY < 0) scrollY = 0;
            if (scrollX > imageWidth - screen_w) scrollX = imageWidth - screen_w;
            if (scrollY > imageHeight - screen_h) scrollY = imageHeight - screen_h;
            
            if ((scrollX <= imageWidth - screen_w) && (scrollY <= imageHeight - screen_h)) {

                    showBmp = Bitmap.createBitmap(sourceBmp, scrollX, scrollY, screen_w, screen_h);
                    invalidate();
                
                
            }
        }
    }
}
