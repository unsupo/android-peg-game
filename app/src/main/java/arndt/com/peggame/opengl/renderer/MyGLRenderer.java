package arndt.com.peggame.opengl.renderer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import arndt.com.peggame.opengl.shapes.Square;
import arndt.com.peggame.opengl.shapes.Triangle;

public class MyGLRenderer implements GLSurfaceView.Renderer{
    private Triangle    mTriangle;
    private Square      mSquare;

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //initialize a triangle
        mTriangle = new Triangle();
        //initialize a square
        mSquare   = new Square();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        mTriangle.draw();
    }

    public static int loadShader(int type, String shaderCode){
        //create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        //or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);
        //add the source code to the shader and compile it
        GLES20.glShaderSource(shader,shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

}
