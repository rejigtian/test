package com.example.test.gl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.example.test.R;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glGetUniformLocation;


/**
 * @author : rejig
 * date: 2020-08-07
 * 基于纹理贴图显示bitmap
 */

class TextureRenderer implements GLSurfaceView.Renderer{

    private Bitmap bmp,fall;
    private int mProgram;
    private int mTexSamplerHandle;
    private int mTexCoordHandle;
    private int mPosCoordHandle;
    private int mMatrixHandle;
    private FloatBuffer mTexVertices;
    private FloatBuffer mPosVertices;
    protected ShortBuffer mVertexIndexBuffer;
    private int[] mTextures = new int[2];
    private int[] mTextures2 = new int[2];
    private Context context;
    private int level;
    private float viewHeight,viewWidth;
    private List<Coordinate> coordinates=new ArrayList<>();

    float radiow,radioh;

    public TextureRenderer(Context c,int level) {
        bmp = BitmapFactory.decodeResource(c.getResources(), R.drawable.rain_red_packet);
        fall = BitmapFactory.decodeResource(c.getResources(), R.drawable.rain_fall);
        context=c;
        this.level=level;
    }

    public List<Coordinate> getRectFList() {
        return coordinates;
    }

    public void setRectFList(List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    //纹理
    float[] TEX_VERTICES = { 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f };
    protected final short[] VERTEX_INDEX = { 0, 1, 2, 1, 2, 3 };
    //顶点
    float[] POS_VERTICES = {-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f };
    float[] s=new float[16];
    //着色器程序
    String VERTEX_SHADER_CODE =
            "attribute vec4 a_position;\n" +
                    "attribute vec2 a_texcoord;\n" +
                    "varying vec2 v_texcoord;\n" +
                    "uniform mat4 uMVPMatrix;\n"+
                    "void main() {\n" +
                    "  gl_Position = uMVPMatrix * a_position;\n" +
                    "  v_texcoord = a_texcoord;\n" +
                    "}\n";
    String FRAGMENT_SHADER_CODE =
            "precision lowp float;\n" +
                    "uniform sampler2D tex_sampler;\n" +
                    "varying vec2 v_texcoord;\n" +
                    "uniform float v_alpha;\n"+
                    "void main() {\n" +
                    "  gl_FragColor = texture2D(tex_sampler, v_texcoord);\n" +
                    "}\n";
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //纹理坐标      屏幕右上角为原点(左下，右下，左上，右上)
        mTexVertices = ByteBuffer.allocateDirect(TEX_VERTICES.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mTexVertices.put(TEX_VERTICES).position(0);
        //顶点坐标    屏幕中心点为原点(左下，右下，左上，右上)
        mPosVertices = ByteBuffer.allocateDirect(POS_VERTICES.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPosVertices.put(POS_VERTICES).position(0);
        mVertexIndexBuffer = ByteBuffer.allocateDirect(VERTEX_INDEX.length * 2)
                .order(ByteOrder.nativeOrder())
                .asShortBuffer()
                .put(VERTEX_INDEX);
        mVertexIndexBuffer.position(0);
        mProgram = GLShaderToolbox.createProgram(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);
        mTexSamplerHandle = glGetUniformLocation(mProgram,"tex_sampler");
        mTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_texcoord");
        mPosCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_position");
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //创建纹理并将图片贴入纹理
        GLES20.glGenTextures(1, mTextures , 0);
        GLES20.glBindTexture(GL_TEXTURE_2D, mTextures[0]);
        GLUtils.texImage2D(GL_TEXTURE_2D, level, bmp, 0);
        GLShaderToolbox.initTextureNeedParams();
        GLES20.glGenTextures(1, mTextures , 1);
        GLES20.glBindTexture(GL_TEXTURE_2D, mTextures[1]);
        GLUtils.texImage2D(GL_TEXTURE_2D, level, fall, 0);
        GLShaderToolbox.initTextureNeedParams();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        viewHeight=height;
        viewWidth=width;
    }

    @Override
    public void onDrawFrame(GL10 gl) {

    //调整AspectRatio 保证landscape和portrait的时候显示比例相同，图片不会被拉伸
        if (mPosVertices != null) {
            reSize(bmp);
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            //启用混合模式
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

            GLShaderToolbox.checkGlError("glUseProgram");

            GLES20.glUseProgram(mProgram);

            //将纹理坐标传递给着色器程序并使能属性数组
            GLES20.glVertexAttribPointer(mTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mTexVertices);
            GLES20.glEnableVertexAttribArray(mTexCoordHandle);
            //将顶点坐标传递给着色器程序并使能属性数组
            GLES20.glVertexAttribPointer(mPosCoordHandle, 2, GLES20.GL_FLOAT, false, 0, mPosVertices);
            GLES20.glEnableVertexAttribArray(mPosCoordHandle);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GL_TEXTURE_2D, mTextures[0]);
            GLES20.glUniform1i(mTexSamplerHandle, 0);
//            GLES20.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            for (int i=0;i<coordinates.size();i++) {
                Matrix.setIdentityM(s,0);
                Matrix.translateM(s,0,(coordinates.get(i).getX()/viewWidth*2),-coordinates.get(i).getY()/viewHeight*2,0);
                GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false,s,0 );
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                        GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
            }
            reSize(fall);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GL_TEXTURE_2D, mTextures[1]);
            GLES20.glUniform1i(mTexSamplerHandle, 0);
            for (int i=0;i<coordinates.size();i++) {
                Matrix.setIdentityM(s,0);
                Matrix.translateM(s,0,(coordinates.get(i).getX()/viewWidth*2),-coordinates.get(i).getY()/viewHeight*2+radioh*2,0);
                GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false,s,0 );
                GLES20.glDrawElements(GLES20.GL_TRIANGLES, VERTEX_INDEX.length,
                        GLES20.GL_UNSIGNED_SHORT, mVertexIndexBuffer);
            }
        }
    }

    private void reSize(Bitmap bitmap) {
        radiow=((float) bitmap.getWidth())/viewWidth;
        radioh=((float) bitmap.getHeight())/viewHeight;
        float x0=-1f,y0=1f,x1=x0+2*radiow,y1=y0-2*radioh;
        float[] postion={x0,y1,x1,y1,x0,y0,x1,y0};
        mPosVertices.put(postion).position(0);
    }
}
class GLShaderToolbox {

    public static int loadShader(int shaderType, String source) {
        int shader = GLES20.glCreateShader(shaderType);
        if (shader != 0) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) {
                String info = GLES20.glGetShaderInfoLog(shader);
                GLES20.glDeleteShader(shader);
                shader = 0;
                throw new RuntimeException("Could not compile shader " +
                        shaderType + ":" + info);
            }
        }
        return shader;
    }

    public static int createProgram(String vertexSourceCode, String fragmentSourceCode) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexSourceCode);
        if (vertexShader == 0) return 0;
        int pixelShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentSourceCode);
        if (pixelShader == 0) return 0;
        int program = GLES20.glCreateProgram();
        if (program != 0) {
            GLES20.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            GLES20.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            GLES20.glLinkProgram(program);
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] != GLES20.GL_TRUE) {
                String info = GLES20.glGetProgramInfoLog(program);
                GLES20.glDeleteProgram(program);
                program = 0;
                throw new RuntimeException("Could not link program: " + info);
            }
        }
        return program;
    }

    public static void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    public static void initTextureNeedParams() {
        GLES20.glTexParameteri(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
    }
}
