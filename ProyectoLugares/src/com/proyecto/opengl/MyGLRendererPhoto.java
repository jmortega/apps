package com.proyecto.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * Clase que permite renderizar el cubo 3d con imágenes que se cargan de los lugares que tengamos registrados
 */
public class MyGLRendererPhoto implements GLSurfaceView.Renderer {
   private PhotoCube cube;   
   private static float angleCube = 0;     // angulo de rotacion
   private static float speedCube = -1.2f; // velocidad de rotacion del cubo
   
   //Posiciones,angulos y velocidad
   
   float angleX = 0;   
   float angleY = 0;   
   float speedX = 0;   
   float speedY = 0;   
   float z = -6.0f;  
   

   // Constructor
   public MyGLRendererPhoto(Context context,String [] lugares) {
      cube = new PhotoCube(context,lugares);    // (NEW)
   }
   
   @Override
   public void onSurfaceCreated(GL10 gl, EGLConfig config) {
      gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
      gl.glClearDepthf(1.0f);            
      gl.glEnable(GL10.GL_DEPTH_TEST);   
      gl.glDepthFunc(GL10.GL_LEQUAL);   
      gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);  
      gl.glShadeModel(GL10.GL_SMOOTH);   
      gl.glDisable(GL10.GL_DITHER);      
    
      //Texturas
      cube.loadTexture(gl);             
      gl.glEnable(GL10.GL_TEXTURE_2D);
   }
  
   @Override
   public void onSurfaceChanged(GL10 gl, int width, int height) {
       gl.glViewport(50, 75, 205, 205);
       gl.glMatrixMode(GL10.GL_PROJECTION);
       gl.glLoadIdentity();
       gl.glOrthof(0, width, 0, height, 0, 1);
       gl.glShadeModel(GL10.GL_FLAT);
       gl.glEnable(GL10.GL_BLEND);
       gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
       gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);
       gl.glEnable(GL10.GL_TEXTURE_2D);
   }
   @Override
   public void onDrawFrame(GL10 gl) {

      gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
  
      // Pintar el cubo
      gl.glLoadIdentity();
      //Colocarlo en pantalla
      gl.glTranslatef(0.0f, 0.0f, -6.0f);
      //rotar
      gl.glRotatef(angleCube, 0.15f, 1.0f, 0.3f); 
      cube.draw(gl);
      
      angleCube += speedCube;
   }
}