package com.proyecto.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;

import com.proyecto.activity.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
/*
 * Cubo de 6 caras donde cargamos imágenes y texturas
 */
public class PhotoCube {
   private FloatBuffer vertexBuffer; 
   private FloatBuffer texBuffer;     
  
   private int numFaces = 6;
   private int[] imageFileIDs = {
      R.drawable.textura,
      R.drawable.textura,
      R.drawable.textura,
      R.drawable.textura,
      R.drawable.textura,
      R.drawable.textura
   };
   private int[] textureIDs = new int[numFaces];
   private Bitmap[] bitmap = new Bitmap[numFaces];
   private float cubeHalfSize = 1.2f;
        
   // Constructor
   public PhotoCube(Context context,String[] lugares) {

      ByteBuffer vbb = ByteBuffer.allocateDirect(12 * 4 * numFaces);
      vbb.order(ByteOrder.nativeOrder());
      vertexBuffer = vbb.asFloatBuffer();
  
      // Obtenemos bitmaps
      for (int face = 0; face < numFaces; face++) {
         try{
             try{
                 
             if((lugares!=null) && (lugares.length>0)){
                 if((lugares[face]!=null) && (!lugares[face].equals(""))){
                     bitmap[face] = BitmapFactory.decodeFile(lugares[face]);
                     bitmap[face] = Bitmap.createScaledBitmap(bitmap[face], 500, 250,true);
                     bitmap[face] = Bitmap.createBitmap(bitmap[face]);
                 }else{
                     bitmap[face] = BitmapFactory.decodeStream(
                         context.getResources().openRawResource(imageFileIDs[face]));
                 }
             }else{
                 bitmap[face] = BitmapFactory.decodeStream(
                         context.getResources().openRawResource(imageFileIDs[face]));
             }
         
             } catch (Exception e) {
                 e.printStackTrace();
             }
         
          } catch (OutOfMemoryError e) {
              System.gc();

          }
      
         int imgWidth = bitmap[face].getWidth();
         int imgHeight = bitmap[face].getHeight();
         float faceWidth = 2.0f;
         float faceHeight = 2.0f;

         if (imgWidth > imgHeight) {
            faceHeight = faceHeight * imgHeight / imgWidth; 
         } else {
            faceWidth = faceWidth * imgWidth / imgHeight;
         }
         float faceLeft = -faceWidth / 2;
         float faceRight = -faceLeft;
         float faceTop = faceHeight / 2;
         float faceBottom = -faceTop;
         
         // Definimos vértices
         float[] vertices = {
            faceLeft,  faceBottom, 0.0f, 
            faceRight, faceBottom, 0.0f,
            faceLeft,  faceTop,    0.0f,
            faceRight, faceTop,    0.0f,
         };
         vertexBuffer.put(vertices); 
      }
      vertexBuffer.position(0);
  
      float[] texCoords = {
         0.0f, 1.0f,
         1.0f, 1.0f,
         0.0f, 0.0f,
         1.0f, 0.0f 
      };
      ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4 * numFaces);
      tbb.order(ByteOrder.nativeOrder());
      texBuffer = tbb.asFloatBuffer();
      for (int face = 0; face < numFaces; face++) {
         texBuffer.put(texCoords);
      }
      texBuffer.position(0); 
   }
   
   // Pintamos la figura
   public void draw(GL10 gl) {
      gl.glFrontFace(GL10.GL_CCW);
      
      gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
      gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
      gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
      gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer);
  
      // delante
      gl.glPushMatrix();
      gl.glTranslatef(0f, 0f, cubeHalfSize);
  
      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[0]);
      
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
      gl.glPopMatrix();
  
      // izquierda
      gl.glPushMatrix();
      gl.glRotatef(270.0f, 0f, 1f, 0f);
      gl.glTranslatef(0f, 0f, cubeHalfSize);
  
      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[1]);
      
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
      gl.glPopMatrix();
  
      // atras
      gl.glPushMatrix();
      gl.glRotatef(180.0f, 0f, 1f, 0f);
      gl.glTranslatef(0f, 0f, cubeHalfSize);
     
      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[2]);
      
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
      gl.glPopMatrix();
  
      // derecha
      gl.glPushMatrix();
      gl.glRotatef(90.0f, 0f, 1f, 0f);
      gl.glTranslatef(0f, 0f, cubeHalfSize);
    
      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[3]);
      
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);
      gl.glPopMatrix();
  
      // arriba
      gl.glPushMatrix();
      gl.glRotatef(270.0f, 1f, 0f, 0f);
      gl.glTranslatef(0f, 0f, cubeHalfSize);
    
      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[4]);
      
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
      gl.glPopMatrix();
  
      // abajo
      gl.glPushMatrix();
      gl.glRotatef(90.0f, 1f, 0f, 0f);
      gl.glTranslatef(0f, 0f, cubeHalfSize);
      
      gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[5]);
      
      gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);
      gl.glPopMatrix();
   
      gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
      gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
   }
  
   // Cargamos texturas
   public void loadTexture(GL10 gl) {
      gl.glGenTextures(6, textureIDs, 0);
  
      for (int face = 0; face < numFaces; face++) {
          
         gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[face]);

         GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap[face], 0);
         bitmap[face].recycle();
      }
   }
}
