package com.proyecto.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

public class ContentViewFlipper extends ViewFlipper {
    public ContentViewFlipper( Context context ) {
       super( context );
    }

    public ContentViewFlipper( Context context, AttributeSet attrs ) {
       super( context, attrs );
    }

    @Override
    protected void onDetachedFromWindow() {
       try {
          super.onDetachedFromWindow();
       }
       catch( Exception e ) {}
    }
 }
