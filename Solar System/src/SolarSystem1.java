
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.swing.*;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import static javax.media.opengl.GL.*;  // GL constants
import static javax.media.opengl.GL2.*; // GL2 constants
import static javax.media.opengl.GL2GL3.GL_QUADS;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT1;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHTING;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_POSITION;
 
/**
 * JOGL 2.0 Solar system with accurate distance and size ratios 
 * excluding size of sun/pluto and orbit size of moon
 */
@SuppressWarnings("serial")
public class SolarSystem1 extends GLCanvas implements GLEventListener {
   // Define constants for the top-level container
   private static String TITLE = "Solar System for Virginia, Cheyenne, and Anastatia";  // window's title
   private static final int CANVAS_WIDTH = 1500;  // width of the drawable
   private static final int CANVAS_HEIGHT = 850; // height of the drawable
   private static final int FPS = 60; // animator's target frames per second
   private static int reverse=1;
   private static float earthScale=2.5f;
   private static float earthDistance=30;
   private static float earthOrbitPeriod=1000;
   
   private static double moonRad=50*.00257*earthDistance;
   private double moonXPos=moonRad;
   private double moonYPos=0;
   private double moonZPos=0;
   private double theta=0;
   
   private static double mercuryRad=.387*earthDistance;
   private double mercuryXPos=1*mercuryRad;
   private double mercuryYPos=0;
   private double mercuryZPos=0;
   private double thetaMercury=0;//Math.PI;
   
   private static double venusRad=.723*earthDistance;
   private double venusXPos=1*venusRad;
   private double venusYPos=0;
   private double venusZPos=0;
   private double thetaVenus=0;//Math.PI;
   
   private static double earthRad=earthDistance;
   private double earthXPos=earthRad;
   private double earthYPos=0;
   private double earthZPos=0;
   private double thetaEarth=0;
   
   private static double marsRad=1.52*earthDistance;
   private double marsXPos=marsRad;
   private double marsYPos=0;
   private double marsZPos=0;
   private double thetaMars=0;
   
   private static double jupiterRad=5.2*earthDistance;
   private double jupiterXPos=0;
   private double jupiterYPos=jupiterRad;
   private double jupiterZPos=0;
   private double thetaJupiter=Math.PI/2.4;
   
   private static double saturnRad=9.58*earthDistance;
   private double saturnXPos=0;
   private double saturnYPos=saturnRad;
   private double saturnZPos=0;
   private double thetaSaturn=Math.PI/2.6;
   
   private static double uranusRad=19.20*earthDistance;
   private double uranusXPos=0;
   private double uranusYPos=uranusRad;
   private double uranusZPos=0;
   private double thetaUranus=Math.PI/2.7;
   
   private static double neptuneRad=30.05*earthDistance;
   private double neptuneXPos=0;
   private double neptuneYPos=neptuneRad;
   private double neptuneZPos=0;
   private double thetaNeptune=Math.PI/2.8;
   
   private static double plutoRad=39.24*earthDistance;
   private double plutoXPos=0;
   private double plutoYPos=plutoRad;
   private double plutoZPos=0;
   private double thetaPluto=Math.PI/2.9;
   
   Texture sunTexture=null;
   Texture sunSkyTexture=null;
   Texture mercuryTexture=null;
   Texture venusTexture=null;
   Texture earthTexture=null;
   Texture moonTexture=null;
   Texture skyTexture=null;
   Texture marsTexture=null;
   Texture jupiterTexture=null;
   Texture saturnTexture=null;
   Texture saturnRingTexture=null;
   Texture saturnRing2Texture=null;
   Texture uranusTexture=null;
   Texture uranusRingTexture=null;
   Texture neptuneTexture=null;
   Texture plutoTexture=null;
   Texture backgroundTexture=null;
   private float backgroundTextureTop, backgroundTextureBottom, backgroundTextureLeft, backgroundTextureRight;
   
   protected static boolean isLighting = true;
   /** The entry main() method to setup the top-level container and animator */
   public static void main(String[] args) {
      // Run the GUI codes in the event-dispatching thread for thread safety
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            // Create the OpenGL rendering canvas
            GLCanvas canvas = new SolarSystem1();
            canvas.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
 
            // Create a animator that drives canvas' display() at the specified FPS.
            final FPSAnimator animator = new FPSAnimator(canvas, FPS, true);
 
            // Create the top-level container
            final JFrame frame = new JFrame(); // Swing's JFrame or AWT's Frame
            frame.setLayout(new BorderLayout());
            JPanel panel1 = new JPanel();
            JPanel buttonPanel = new JPanel();
            JLabel jlSize = new JLabel("Size");
            final JTextField jtfSize = new JTextField("",4);
            JLabel jlDistance = new JLabel("Distance");
            final JTextField jtfDistance = new JTextField("",4);
            JLabel jlPeriod = new JLabel("Orbital Period");
            final JTextField jtfPeriod = new JTextField("",5);
            buttonPanel.setLayout(new FlowLayout());
            frame.add(panel1, BorderLayout.CENTER);
            panel1.add(canvas);
            //frame.getContentPane().add(canvas);
            JButton jbSomething = new JButton("Lighting");
            JButton jbReverse = new JButton("Reverse Direction");
            
            frame.addWindowListener(new WindowAdapter() {
               @Override
               public void windowClosing(WindowEvent e) {
                  // Use a dedicate thread to run the stop() to ensure that the
                  // animator stops before program exits.
                  new Thread() {
                     @Override
                     public void run() {
                        if (animator.isStarted()) animator.stop();
                        System.exit(0);
                     }
                  }.start();
               }
            });
            frame.setTitle(TITLE);
            buttonPanel.add(jbSomething);
            buttonPanel.add(jbReverse);
            buttonPanel.add(jlSize);
            buttonPanel.add(jtfSize);
            buttonPanel.add(jlDistance);
            buttonPanel.add(jtfDistance);
            buttonPanel.add(jlPeriod);
            buttonPanel.add(jtfPeriod);
            frame.add(buttonPanel,BorderLayout.SOUTH);
            jbSomething.addActionListener(new ActionListener(){
            	@Override
            	public void actionPerformed(ActionEvent e){
            		if(isLighting==true){
            			isLighting=false;
            		}else{
            			isLighting=true;
            		}
            	}
            });
            jbReverse.addActionListener(new ActionListener(){
            	@Override
            	public void actionPerformed(ActionEvent e){
            		reverse*=-1;
            	}
            });
            jtfSize.addActionListener(new ActionListener(){
            	@Override
            	public void actionPerformed(ActionEvent e){
            		try{
            			earthScale=(float)Double.parseDouble(jtfSize.getText());
            		}catch(Exception ex){
            			
            		}
        
            	}
            });
            jtfDistance.addActionListener(new ActionListener(){
            	@Override
            	public void actionPerformed(ActionEvent e){
            		try{
            			earthDistance=(float)Double.parseDouble(jtfDistance.getText());
            		}catch(Exception ex){
            			
            		}
            		moonRad=50*.00257*earthDistance;
            		mercuryRad=.387*earthDistance;
            		venusRad=.723*earthDistance;
            		earthRad=earthDistance;
            		marsRad=1.52*earthDistance;
            		jupiterRad=5.2*earthDistance;
            		saturnRad=9.58*earthDistance;
            		uranusRad=19.20*earthDistance;
            		neptuneRad=30.05*earthDistance;
            		plutoRad=39.24*earthDistance;
            	}
            });
            jtfPeriod.addActionListener(new ActionListener(){
            	@Override
            	public void actionPerformed(ActionEvent e){
            		try{
            			earthOrbitPeriod=(float)Double.parseDouble(jtfPeriod.getText());
            		}catch (Exception ex){
            			
            		}
            	}
            });
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            animator.start(); // start the animation loop
         }
      });
   }
 
   // Setup OpenGL Graphics Renderer
 
   private GLU glu;  // for the GL Utility
   private float angleSun = 0;
   private float angleMoon = 0;       // rotational angle in degree for cube
   private float speedSun = 1.25f*reverse;
   private float speedMoon = 1.5f;   // rotational speed for moon
   private float angleMercury = 0.0f;
   private float speedMercury = .5f;
   private float angleVenus = 0.0f;
   private float speedVenus = 1.0f;
   private float angleEarth = 0.0f;
   private float speedEarth = 1.5f;
   private float angleEarthSky = 0.0f;
   private float speedEarthSky= speedEarth+.5f;
   private float angleMars = 0.0f;
   private float speedMars = 1.5f;
   private float angleJupiter = 0.0f;
   private float speedJupiter = 1.5f;
   private float angleSaturn = 0.0f;
   private float speedSaturn = 1.5f;
   private float angleUranus = 0.0f;
   private float speedUranus = 1.5f;
   private float angleNeptune = 0.0f;
   private float speedNeptune = 1.5f;
   private float anglePluto = 0.0f;
   private float speedPluto = 1.5f;
   
   /** Constructor to setup the GUI for this Component */
   public SolarSystem1() {
      this.addGLEventListener(this);
   }
 
   // ------ Implement methods declared in GLEventListener ------
 
   /**
    * Called back immediately after the OpenGL context is initialized. Can be used
    * to perform one-time initialization. Run only once.
    */
   @Override
   public void init(GLAutoDrawable drawable) {
      GL2 gl = drawable.getGL().getGL2();      // get the OpenGL graphics context
      glu = new GLU();                         // get GL Utilities
      gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // set background (clear) color
      gl.glClearDepth(1.0f);      // set clear depth value to farthest
      gl.glEnable(GL_DEPTH_TEST); // enables depth testing
      gl.glDepthFunc(GL_LEQUAL);  // the type of depth test to do
      gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // best perspective correction
      gl.glShadeModel(GL_SMOOTH); // blends colors nicely, and smoothes out lighting
      /*
      if(isLighting==true){
    	float SHINE_ALL_DIRECTIONS = 1;
      	float[] lightPos = {0, 0, 0, SHINE_ALL_DIRECTIONS};
      	float[] lightColorAmbient = {0.8f, 0.8f, 0.8f, 1f};
      	float[] lightColorSpecular = {0.8f, 0.8f, 0.8f, 1f};

      	// Set light parameters.
      	gl.glLightfv(GL_LIGHT1, GL_POSITION, lightPos, 0);
      	gl.glLightfv(GL_LIGHT1, GL_AMBIENT, lightColorAmbient, 0);
      	gl.glLightfv(GL_LIGHT1, GL_SPECULAR, lightColorSpecular,0);

      	// Enable lighting in GL.
      	gl.glEnable(GL_LIGHT1);
      	gl.glEnable(GL_LIGHTING);

      	// Set material properties.
      	float[] rgba = {0.6f, 0.8f, 1f};
      	gl.glMaterialfv(GL_FRONT, GL_AMBIENT, rgba, 0);
      	gl.glMaterialfv(GL_FRONT, GL_SPECULAR, rgba, 0);
      	gl.glMaterialf(GL_FRONT, GL_SHININESS, 0.5f);
      }
      */
      try {
          InputStream stream = getClass().getResourceAsStream("images/sunmap.jpg");
          TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
          sunTexture = TextureIO.newTexture(data);
      }
      catch (IOException exc) {
          exc.printStackTrace();
          System.exit(1);
      }
      try {
          InputStream stream = getClass().getResourceAsStream("images/sunmap2.jpg");
          TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
          sunSkyTexture = TextureIO.newTexture(data);
      }
      catch (IOException exc) {
          exc.printStackTrace();
          System.exit(1);
      }
      try {
          InputStream stream = getClass().getResourceAsStream("images/earthmap1k.jpg");
          TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
          earthTexture = TextureIO.newTexture(data);
      }
      catch (IOException exc) {
          exc.printStackTrace();
          System.exit(1);
      }
      try {
          //InputStream stream = getClass().getResourceAsStream("images/moonmap4k.jpg");
          //TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
          //moonTexture = TextureIO.newTexture(data);
          BufferedImage image = 
                  ImageIO.read(getClass().getClassLoader().getResource("images/moonmap1k.jpg"));

            // Create a OpenGL Texture object
            moonTexture = AWTTextureIO.newTexture(GLProfile.getDefault(), image, false); 
            // Linear filter is more compute-intensive
            // Use linear filter if image is larger than the original texture
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            // Use linear filter if image is smaller than the original texture
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

          
      }
      catch (IOException exc) {
          exc.printStackTrace();
          System.exit(1);
      }
      try {
          InputStream stream = getClass().getResourceAsStream("images/mercurymap.jpg");
          TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
          mercuryTexture = TextureIO.newTexture(data);
      }
      catch (IOException exc) {
          exc.printStackTrace();
          System.exit(1);
      }
      try {
          InputStream stream = getClass().getResourceAsStream("images/venusmap.jpg");
          TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
          venusTexture = TextureIO.newTexture(data);
      }
      catch (IOException exc) {
          exc.printStackTrace();
          System.exit(1);
      }
      try {
          InputStream stream = getClass().getResourceAsStream("images/earthcloudmap.jpg");
          TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
          skyTexture = TextureIO.newTexture(data);
      }
      catch (IOException exc) {
          exc.printStackTrace();
          System.exit(1);
      }
      try {
          InputStream stream = getClass().getResourceAsStream("images/milkywayback.jpg");
          TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
          backgroundTexture = TextureIO.newTexture(data);
      }
      catch (IOException exc) {
          exc.printStackTrace();
          System.exit(1);
      }
      
      try {
          InputStream stream = getClass().getResourceAsStream("images/mars_1k_color.jpg");
          TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
          marsTexture = TextureIO.newTexture(data);
      }
      catch (IOException exc) {
          exc.printStackTrace();
          System.exit(1);
      }
      try {
          InputStream stream = getClass().getResourceAsStream("images/jupiter2_1k.jpg");
          TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
          jupiterTexture = TextureIO.newTexture(data);
      }
      catch (IOException exc) {
          exc.printStackTrace();
          System.exit(1);
      }
      try {
          InputStream stream = getClass().getResourceAsStream("images/saturnmap.jpg");
          TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
          saturnTexture = TextureIO.newTexture(data);
      }
      catch (IOException exc) {
          exc.printStackTrace();
          System.exit(1);
      }
      try {
          InputStream stream = getClass().getResourceAsStream("images/saturnringnocolor.jpg");
          TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
          saturnRingTexture = TextureIO.newTexture(data);
      }
      catch (IOException exc) {
          exc.printStackTrace();
          System.exit(1);
      }
      try {
          InputStream stream = getClass().getResourceAsStream("images/ringnocolor2.jpg");
          TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
          saturnRing2Texture = TextureIO.newTexture(data);
      }
      catch (IOException exc) {
          exc.printStackTrace();
          System.exit(1);
      }
      try {
          InputStream stream = getClass().getResourceAsStream("images/uranusmap.jpg");
          TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
          uranusTexture = TextureIO.newTexture(data);
      }
      catch (IOException exc) {
          exc.printStackTrace();
          System.exit(1);
      }
      try {
          InputStream stream = getClass().getResourceAsStream("images/ringnocolor3.jpg");
          TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
          uranusRingTexture = TextureIO.newTexture(data);
      }
      catch (IOException exc) {
          exc.printStackTrace();
          System.exit(1);
      }
      try {
          InputStream stream = getClass().getResourceAsStream("images/neptunemap.jpg");
          TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
          neptuneTexture = TextureIO.newTexture(data);
      }
      catch (IOException exc) {
          exc.printStackTrace();
          System.exit(1);
      }
      try {
          InputStream stream = getClass().getResourceAsStream("images/plutomap1k.jpg");
          TextureData data = TextureIO.newTextureData(GLProfile.getDefault(), stream, false, "jpg");
          plutoTexture = TextureIO.newTexture(data);
      }
      catch (IOException exc) {
          exc.printStackTrace();
          System.exit(1);
      }
      
      TextureCoords textureCoords;
      textureCoords = backgroundTexture.getImageTexCoords();
      backgroundTextureTop = textureCoords.top();
      backgroundTextureBottom = textureCoords.bottom();
      backgroundTextureLeft = textureCoords.left();
      backgroundTextureRight = textureCoords.right();
   // Use linear filter if image is larger than the original texture
      //gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
      // Use linear filter if image is smaller than the original texture
      //gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
   }
 
   /**
    * Call-back handler for window re-size event. Also called when the drawable is
    * first set to visible.
    */
   @Override
   public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
      GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
 
      if (height == 0) height = 1;   // prevent divide by zero
      float aspect = (float)width / height;
 
      // Set the view port (display area) to cover the entire window
      gl.glViewport(0, 0, width, height);
      
      // Setup perspective projection, with aspect ratio matches viewport
      gl.glMatrixMode(GL_PROJECTION);  // choose projection matrix
      gl.glLoadIdentity();             // reset projection matrix
      glu.gluPerspective(45.0, aspect, 0.1, 2000.0); // fovy, aspect, zNear, zFar
      glu.gluLookAt(0, -50, 10, 0, 0, 0, 0, 0, 1);
      
      // Enable the model-view transform
      gl.glMatrixMode(GL_MODELVIEW);
      gl.glLoadIdentity(); // reset
   }
 
   /**
    * Called back by the animator to perform rendering.
    */
   @Override
   public void display(GLAutoDrawable drawable) {
      GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
      gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear color and depth buffers
 
      
      
      
      
      if(isLighting==true){
    	  gl.glLoadIdentity();
    	  //float SHINE_ALL_DIRECTIONS = 1f;
    	  //float[] lightPos = {0, 0, 0, SHINE_ALL_DIRECTIONS};
    	  float[] lightColorAmbient = {0.3f, 0.3f, 0.3f, 1f};
    	  //float[] lightColorSpecular = {0.8f, 0.8f, 0.8f, 1f};
    	  
    	  float[] lightDiffuseValue = {1.0f, 1.0f, 1.0f, 1.0f};
          // Diffuse light location xyz (in front of the screen).
          float lightDiffusePosition[] = {0.0f, 0.0f, 0.0f, 1.0f};

    	// Set light parameters.
    	//gl.glLightfv(GL_LIGHT1, GL_POSITION, lightPos, 0);
    	gl.glLightfv(GL_LIGHT1, GL_AMBIENT, lightColorAmbient, 0);
    	//gl.glLightfv(GL_LIGHT1, GL_SPECULAR, lightColorSpecular,0);
    	gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, lightDiffuseValue, 0);
        gl.glLightfv(GL_LIGHT1, GL_POSITION, lightDiffusePosition, 0);
    	

    	// Enable lighting in GL.
    	gl.glEnable(GL_LIGHT1);
    	gl.glEnable(GL_LIGHTING);

    	// Set material properties.
    	float[] rgba = {0.6f, 0.8f, 1f};
    	gl.glMaterialfv(GL_FRONT, GL_AMBIENT, rgba, 0);
    	gl.glMaterialfv(GL_FRONT, GL_SPECULAR, rgba, 0);
    	gl.glMaterialf(GL_FRONT, GL_SHININESS, 0.5f);
      }else{
    	gl.glDisable(GL_LIGHT1);
      	gl.glDisable(GL_LIGHTING);
      }

      //Render Sun---
      gl.glLoadIdentity();
      gl.glTranslatef(0.0f,0.0f, 0.0f);
      gl.glRotatef(angleSun, 0.0f, 0.0f, 1.0f);
      
      //gl.glDisable(GL.GL_TEXTURE_2D);
      //gl.glEnable(GL_BLEND);
      gl.glEnable(GL_DEPTH_TEST);
      // Apply texture.
      sunTexture.enable(gl);
      sunTexture.bind(gl);
      
      float[] rgba = {.9f, .9f, .9f,};
    	gl.glMaterialfv(GL_FRONT, GL_AMBIENT, rgba, 0);
    	gl.glMaterialfv(GL_FRONT, GL_SPECULAR, rgba, 0);
    	gl.glMaterialf(GL_FRONT, GL_SHININESS, 0.0f);
        
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE);
      // Draw sphere (possible styles: FILL, LINE, POINT).
      GLUquadric sun = glu.gluNewQuadric();
      glu.gluQuadricTexture(sun, true);
      glu.gluQuadricDrawStyle(sun, GLU.GLU_FILL);
      glu.gluQuadricNormals(sun, GLU.GLU_FLAT);
      glu.gluQuadricOrientation(sun, GLU.GLU_INSIDE);
      final float radiusSun =1f*earthScale;
      final int slicesSun = 64;
      final int stacksSun =64;
      glu.gluSphere(sun, radiusSun, slicesSun, stacksSun);
      glu.gluDeleteQuadric(sun);
      
      
//----- Render sun sky----
      
      gl.glLoadIdentity();
      gl.glTranslated(0,0,0);
      gl.glRotatef(0, 0, 1, 0);
      gl.glRotatef(angleSun, 0.0f, 0.0f, 1.0f);
     
      gl.glDisable(GL.GL_TEXTURE_2D);
      // Apply texture.
      sunSkyTexture.enable(gl);
      sunSkyTexture.bind(gl);
      
      float[] rgba4 = {0.1f, 0.1f, 0.1f};
  	gl.glMaterialfv(GL_FRONT, GL_AMBIENT, rgba4, 0);
  	gl.glMaterialfv(GL_FRONT, GL_SPECULAR, rgba4, 0);
  	gl.glMaterialf(GL_FRONT, GL_SHININESS, 0.0f);
      
      gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE);
      gl.glEnable(GL_BLEND);
      //gl.glDisable(GL_DEPTH_TEST);

      // Draw sphere (possible styles: FILL, LINE, POINT).
      GLUquadric sunSky = glu.gluNewQuadric();
      glu.gluQuadricTexture(sunSky, true);
      glu.gluQuadricDrawStyle(sunSky, GLU.GLU_FILL);
      glu.gluQuadricNormals(sunSky, GLU.GLU_FLAT);
      glu.gluQuadricOrientation(sunSky, GLU.GLU_INSIDE);
      final float radiusSunSky =.2f+radiusSun;
      final int slicesSunSky = 64;
      final int stacksSunSky =64;
      glu.gluSphere(sunSky, radiusSunSky, slicesSunSky, stacksSunSky);
      glu.gluDeleteQuadric(sunSky);
      
//----- Render Mercury----
      
      gl.glLoadIdentity();
      gl.glTranslated(mercuryXPos,mercuryYPos, mercuryZPos);
      gl.glRotatef(0, 0, 1, 0);
      gl.glRotatef(angleMercury, 0.0f, 0.0f, 1.0f);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glDisable(GL_BLEND);
      gl.glEnable(GL_DEPTH_TEST);
      // Apply texture.
      mercuryTexture.enable(gl);
      mercuryTexture.bind(gl);

      // Draw sphere (possible styles: FILL, LINE, POINT).
      GLUquadric mercury = glu.gluNewQuadric();
      glu.gluQuadricTexture(mercury, true);
      glu.gluQuadricDrawStyle(mercury, GLU.GLU_FILL);
      glu.gluQuadricNormals(mercury, GLU.GLU_FLAT);
      glu.gluQuadricOrientation(mercury, GLU.GLU_OUTSIDE);
      final float radiusMercury =.383f*earthScale;
      final int slicesMercury = 32;
      final int stacksMercury =32;
      glu.gluSphere(mercury, radiusMercury, slicesMercury, stacksMercury);
      glu.gluDeleteQuadric(mercury);
      
//----- Render Venus----
      
      gl.glLoadIdentity();
      gl.glTranslated(venusXPos,venusYPos, venusZPos);
      gl.glRotatef(-3.63f, 0, 1, 0);
      gl.glRotatef(angleVenus, 0.0f, 0.0f, 1.0f);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glDisable(GL_BLEND);
      gl.glEnable(GL_DEPTH_TEST);
      // Apply texture.
      venusTexture.enable(gl);
      venusTexture.bind(gl);

      // Draw sphere (possible styles: FILL, LINE, POINT).
      GLUquadric venus = glu.gluNewQuadric();
      glu.gluQuadricTexture(venus, true);
      glu.gluQuadricDrawStyle(venus, GLU.GLU_FILL);
      glu.gluQuadricNormals(venus, GLU.GLU_FLAT);
      glu.gluQuadricOrientation(venus, GLU.GLU_OUTSIDE);
      final float radiusVenus =0.815f*earthScale;
      final int slicesVenus = 64;
      final int stacksVenus =64;
      glu.gluSphere(venus, radiusVenus, slicesVenus, stacksVenus);
      glu.gluDeleteQuadric(venus);
      
      //----- Render Earth----
      
      gl.glLoadIdentity();
      gl.glTranslated(earthXPos,earthYPos, earthZPos);
      gl.glRotatef(23.44f, 0, 1, 0);
      gl.glRotatef(angleEarth, 0.0f, 0.0f, 1.0f);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glDisable(GL_BLEND);
      gl.glEnable(GL_DEPTH_TEST);
      // Apply texture.
      earthTexture.enable(gl);
      earthTexture.bind(gl);

      // Draw sphere (possible styles: FILL, LINE, POINT).
      GLUquadric earth = glu.gluNewQuadric();
      glu.gluQuadricTexture(earth, true);
      glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
      glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
      glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);
      final float radius =earthScale;
      final int slices = 64;
      final int stacks =64;
      glu.gluSphere(earth, radius, slices, stacks);
      glu.gluDeleteQuadric(earth);
      
     
 
      // ----- Render the moon -----
      gl.glLoadIdentity();                // reset the current model-view matrix
      gl.glTranslated(moonXPos+earthXPos, moonYPos+earthYPos, moonZPos+earthZPos); // translate right and into the screen
      
      gl.glRotatef(angleMoon, 0.0f, 0.0f, 1.0f); // rotate about the x, y and z-axes
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glDisable(GL_BLEND);
      gl.glEnable(GL_DEPTH_TEST);
      // Apply texture.
      moonTexture.enable(gl);
      moonTexture.bind(gl);
   // Use linear filter if image is larger than the original texture
      //gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
      // Use linear filter if image is smaller than the original texture
      //gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
      // Draw sphere (possible styles: FILL, LINE, POINT).
      GLUquadric moon1 = glu.gluNewQuadric();
      glu.gluQuadricTexture(moon1, true);
      glu.gluQuadricDrawStyle(moon1, GLU.GLU_FILL);
      glu.gluQuadricNormals(moon1, GLU.GLU_FLAT);
      glu.gluQuadricOrientation(moon1, GLU.GLU_OUTSIDE);
      final float radius1 =0.272f*earthScale;
      final int slices1 = 16;
      final int stacks1 =16;
      glu.gluSphere(moon1, radius1, slices1, stacks1);
      glu.gluDeleteQuadric(moon1);
    
      
//----- Render sky----
      
      gl.glLoadIdentity();
      gl.glTranslated(earthXPos,earthYPos, earthZPos);
      gl.glRotatef(15, 0, 1, 0);
      gl.glRotatef(angleEarthSky, 0.0f, 0.0f, 1.0f);
     
      gl.glDisable(GL.GL_TEXTURE_2D);
      // Apply texture.
      skyTexture.enable(gl);
      skyTexture.bind(gl);
      
      float[] rgba2 = {0.2f, 0.4f, .8f};
  	gl.glMaterialfv(GL_FRONT, GL_AMBIENT, rgba2, 0);
  	gl.glMaterialfv(GL_FRONT, GL_SPECULAR, rgba2, 0);
  	gl.glMaterialf(GL_FRONT, GL_SHININESS, 0.5f);
      
      gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE);
      gl.glEnable(GL_BLEND);
      //gl.glDisable(GL_DEPTH_TEST);

      // Draw sphere (possible styles: FILL, LINE, POINT).
      GLUquadric earthSky = glu.gluNewQuadric();
      glu.gluQuadricTexture(earthSky, true);
      glu.gluQuadricDrawStyle(earthSky, GLU.GLU_FILL);
      glu.gluQuadricNormals(earthSky, GLU.GLU_FLAT);
      glu.gluQuadricOrientation(earthSky, GLU.GLU_OUTSIDE);
      final float radiusSky =1.1f*earthScale;
      final int slicesSky = 64;
      final int stacksSky =64;
      glu.gluSphere(earthSky, radiusSky, slicesSky, stacksSky);
      glu.gluDeleteQuadric(earthSky);
      
      
      
//----- Render Mars----
      
      gl.glLoadIdentity();
      gl.glTranslated(marsXPos,marsYPos, marsZPos);
      gl.glRotatef(25.19f, 0, 1, 0);
      gl.glRotatef(angleMars, 0.0f, 0.0f, 1.0f);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glDisable(GL_BLEND);
      gl.glEnable(GL_DEPTH_TEST);
      // Apply texture.
      marsTexture.enable(gl);
      marsTexture.bind(gl);

      // Draw sphere (possible styles: FILL, LINE, POINT).
      GLUquadric mars = glu.gluNewQuadric();
      glu.gluQuadricTexture(mars, true);
      glu.gluQuadricDrawStyle(mars, GLU.GLU_FILL);
      glu.gluQuadricNormals(mars, GLU.GLU_FLAT);
      glu.gluQuadricOrientation(mars, GLU.GLU_OUTSIDE);
      final float radiusMars =0.532f*earthScale;
      final int slicesMars = 64;
      final int stacksMars =64;
      glu.gluSphere(mars, radiusMars, slicesMars, stacksMars);
      glu.gluDeleteQuadric(mars);
      
//----- Render Jupiter----
      
      gl.glLoadIdentity();
      gl.glTranslated(jupiterXPos,jupiterYPos, jupiterZPos);
      gl.glRotatef(3.13f, 0, 1, 0);
      gl.glRotatef(angleJupiter, 0.0f, 0.0f, 1.0f);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glDisable(GL_BLEND);
      gl.glEnable(GL_DEPTH_TEST);
      // Apply texture.
      jupiterTexture.enable(gl);
      jupiterTexture.bind(gl);

      // Draw sphere (possible styles: FILL, LINE, POINT).
      GLUquadric jupiter = glu.gluNewQuadric();
      glu.gluQuadricTexture(jupiter, true);
      glu.gluQuadricDrawStyle(jupiter, GLU.GLU_FILL);
      glu.gluQuadricNormals(jupiter, GLU.GLU_FLAT);
      glu.gluQuadricOrientation(jupiter, GLU.GLU_OUTSIDE);
      final float radiusJupiter =11.21f*earthScale;
      final int slicesJupiter = 64;
      final int stacksJupiter =64;
      glu.gluSphere(jupiter, radiusJupiter, slicesJupiter, stacksJupiter);
      glu.gluDeleteQuadric(jupiter);
      
//----- Render Saturn----
      
      gl.glLoadIdentity();
      gl.glTranslated(saturnXPos,saturnYPos, saturnZPos);
      gl.glRotatef(26.73f, 1, 1, 0);
      gl.glRotatef(angleSaturn, 0.0f, 0.0f, 1.0f);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glDisable(GL_BLEND);
      gl.glEnable(GL_DEPTH_TEST);
      // Apply texture.
      saturnTexture.enable(gl);
      saturnTexture.bind(gl);

      // Draw sphere (possible styles: FILL, LINE, POINT).
      GLUquadric saturn = glu.gluNewQuadric();
      glu.gluQuadricTexture(saturn, true);
      glu.gluQuadricDrawStyle(saturn, GLU.GLU_FILL);
      glu.gluQuadricNormals(saturn, GLU.GLU_FLAT);
      glu.gluQuadricOrientation(saturn, GLU.GLU_OUTSIDE);
      final float radiusSaturn =9.45f*earthScale;
      final int slicesSaturn = 64;
      final int stacksSaturn =64;
      glu.gluSphere(saturn, radiusSaturn, slicesSaturn, stacksSaturn);
      glu.gluDeleteQuadric(saturn);
      
      //saturns rings
      gl.glLoadIdentity();
      gl.glTranslated(saturnXPos,saturnYPos, saturnZPos);
      gl.glRotatef(26.73f, 1, 1, 0);
      gl.glRotatef(angleSaturn, 0.0f, 0.0f, 1.0f);
      // Apply texture.
      saturnRing2Texture.enable(gl);
      saturnRing2Texture.bind(gl);
      gl.glEnable(GL_BLEND);
      GLUquadric ringSaturn1 = glu.gluNewQuadric();
      glu.gluQuadricTexture(ringSaturn1, true);
      glu.gluQuadricDrawStyle(ringSaturn1, GLU.GLU_FILL);
      glu.gluQuadricNormals(ringSaturn1, GLU.GLU_SMOOTH);
      glu.gluQuadricOrientation(ringSaturn1, GLU.GLU_OUTSIDE);
      glu.gluDisk(ringSaturn1, radiusSaturn*1.9,radiusSaturn*2.5, 100, 1);
      glu.gluDeleteQuadric(ringSaturn1);
      
      gl.glLoadIdentity();
      gl.glTranslated(saturnXPos,saturnYPos, saturnZPos);
      gl.glRotatef(26.73f, 1, 1, 0);
      gl.glRotatef(angleSaturn, 0.0f, 0.0f, 1.0f);
      // Apply texture.
      saturnRingTexture.enable(gl);
      saturnRingTexture.bind(gl);
      gl.glEnable(GL_BLEND);
      GLUquadric ringSaturn2 = glu.gluNewQuadric();
      glu.gluQuadricTexture(ringSaturn2, true);
      glu.gluQuadricDrawStyle(ringSaturn2, GLU.GLU_FILL);
      glu.gluQuadricNormals(ringSaturn2, GLU.GLU_SMOOTH);
      glu.gluQuadricOrientation(ringSaturn2, GLU.GLU_OUTSIDE);
      glu.gluDisk(ringSaturn2, radiusSaturn*1.3,radiusSaturn*1.9, 50, 1);
      glu.gluDeleteQuadric(ringSaturn2);
      
//----- Render Uranus----
      
      gl.glLoadIdentity();
      gl.glTranslated(uranusXPos,uranusYPos, uranusZPos);
      gl.glRotatef(83.33f, 1, 0, 0);
      gl.glRotatef(angleUranus, 0.0f, 0.0f, 1.0f);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glDisable(GL_BLEND);
      gl.glEnable(GL_DEPTH_TEST);
      // Apply texture.
      uranusTexture.enable(gl);
      uranusTexture.bind(gl);

      // Draw sphere (possible styles: FILL, LINE, POINT).
      GLUquadric uranus = glu.gluNewQuadric();
      glu.gluQuadricTexture(uranus, true);
      glu.gluQuadricDrawStyle(uranus, GLU.GLU_FILL);
      glu.gluQuadricNormals(uranus, GLU.GLU_FLAT);
      glu.gluQuadricOrientation(uranus, GLU.GLU_OUTSIDE);
      final float radiusUranus =4.01f*earthScale;
      final int slicesUranus = 64;
      final int stacksUranus =64;
      glu.gluSphere(uranus, radiusUranus, slicesUranus, stacksUranus);
      glu.gluDeleteQuadric(uranus);
      
      //Uranus rings
      gl.glLoadIdentity();
      gl.glTranslated(uranusXPos,uranusYPos, uranusZPos);
      gl.glRotatef(83.33f, 1, 0, 0);
      gl.glRotatef(angleUranus, 0.0f, 0.0f, 1.0f);
      // Apply texture.
      uranusRingTexture.enable(gl);
      uranusRingTexture.bind(gl);
      gl.glEnable(GL_BLEND);
      GLUquadric ringUranus1 = glu.gluNewQuadric();
      glu.gluQuadricTexture(ringUranus1, true);
      glu.gluQuadricDrawStyle(ringUranus1, GLU.GLU_FILL);
      glu.gluQuadricNormals(ringUranus1, GLU.GLU_SMOOTH);
      glu.gluQuadricOrientation(ringUranus1, GLU.GLU_OUTSIDE);
      glu.gluDisk(ringUranus1, radiusUranus*1.3,radiusUranus*1.6, 100, 1);
      glu.gluDeleteQuadric(ringUranus1);
      
//----- Render Neptune----
      
      gl.glLoadIdentity();
      gl.glTranslated(neptuneXPos,neptuneYPos, neptuneZPos);
      gl.glRotatef(28.32f, 0, 1, 0);
      gl.glRotatef(angleNeptune, 0.0f, 0.0f, 1.0f);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glDisable(GL_BLEND);
      gl.glEnable(GL_DEPTH_TEST);
      // Apply texture.
      neptuneTexture.enable(gl);
      neptuneTexture.bind(gl);

      // Draw sphere (possible styles: FILL, LINE, POINT).
      GLUquadric neptune = glu.gluNewQuadric();
      glu.gluQuadricTexture(neptune, true);
      glu.gluQuadricDrawStyle(neptune, GLU.GLU_FILL);
      glu.gluQuadricNormals(neptune, GLU.GLU_FLAT);
      glu.gluQuadricOrientation(neptune, GLU.GLU_OUTSIDE);
      final float radiusNeptune =3.88f*earthScale;
      final int slicesNeptune = 64;
      final int stacksNeptune =64;
      glu.gluSphere(neptune, radiusNeptune, slicesNeptune, stacksNeptune);
      glu.gluDeleteQuadric(neptune);
      
//----- Render Pluto----
      
      gl.glLoadIdentity();
      gl.glTranslated(plutoXPos,plutoYPos, plutoZPos);
      gl.glRotatef(78.0f, 0, 1, 0);
      gl.glRotatef(anglePluto, 0.0f, 0.0f, 1.0f);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glDisable(GL_BLEND);
      gl.glEnable(GL_DEPTH_TEST);
      // Apply texture.
      plutoTexture.enable(gl);
      plutoTexture.bind(gl);

      // Draw sphere (possible styles: FILL, LINE, POINT).
      GLUquadric pluto = glu.gluNewQuadric();
      glu.gluQuadricTexture(pluto, true);
      glu.gluQuadricDrawStyle(pluto, GLU.GLU_FILL);
      glu.gluQuadricNormals(pluto, GLU.GLU_FLAT);
      glu.gluQuadricOrientation(pluto, GLU.GLU_OUTSIDE);
      final float radiusPluto =10*.187f*earthScale;
      final int slicesPluto = 16;
      final int stacksPluto =16;
      glu.gluSphere(pluto, radiusPluto, slicesPluto, stacksPluto);
      glu.gluDeleteQuadric(pluto);

      
      //Render background
      
      gl.glLoadIdentity();
      gl.glTranslatef(0.0f,1200.0f, -200.0f);
      gl.glRotatef(-110f, 1.0f, 0.0f, 0.0f);
      
      backgroundTexture.enable(gl);
      backgroundTexture.bind(gl);
      float[] rgba3 = {0.1f, 0.1f, .1f};
    	gl.glMaterialfv(GL_FRONT, GL_AMBIENT, rgba3, 0);
    	gl.glMaterialfv(GL_FRONT, GL_SPECULAR, rgba3, 0);
    	gl.glMaterialf(GL_FRONT, GL_SHININESS, 0.5f);
      gl.glBegin(GL_QUADS); // of the color cube

      // Front Face
      gl.glNormal3f(0.0f, 0.0f, -1.0f);
      gl.glTexCoord2f(backgroundTextureLeft, backgroundTextureBottom);
      gl.glVertex3f(-1000f, -689.5f, 0f); // bottom-left of the texture and quad
      gl.glTexCoord2f(backgroundTextureRight, backgroundTextureBottom);
      gl.glVertex3f(1000f, -689.5f, 0f);  // bottom-right of the texture and quad
      gl.glTexCoord2f(backgroundTextureRight, backgroundTextureTop);
      gl.glVertex3f(1000f, 689.5f, 0.0f);   // top-right of the texture and quad
      gl.glTexCoord2f(backgroundTextureLeft, backgroundTextureTop);
      gl.glVertex3f(-1000f, 689.5f, 0.0f);  // top-left of the texture and quad
      gl.glEnd();

      // Update the rotational angle after each refresh.
      angleSun+=speedSun*reverse;
      angleMoon += speedMoon*reverse;
      angleMercury +=speedMercury*reverse;
      angleVenus+=speedVenus*reverse;
      angleEarth +=speedEarth*reverse;
      angleEarthSky+=speedEarthSky*reverse;
      angleMars +=speedMars*reverse;
      angleJupiter +=speedJupiter*reverse;
      angleSaturn +=speedSaturn*reverse;
      angleUranus +=speedUranus*reverse;
      angleNeptune +=speedNeptune*reverse;
      anglePluto +=speedPluto*reverse;
      
      thetaMercury += Math.PI/(0.241*earthOrbitPeriod)*reverse;
      if (thetaMercury==2*Math.PI){thetaMercury=0;}
      mercuryXPos=mercuryRad*Math.cos(thetaMercury);
      mercuryYPos=mercuryRad*Math.sin(thetaMercury);
      
      thetaVenus += Math.PI/(0.615*earthOrbitPeriod)*reverse;
      if (thetaVenus==2*Math.PI){thetaVenus=0;}
      venusXPos=venusRad*Math.cos(thetaVenus);
      venusYPos=venusRad*Math.sin(thetaVenus);
      
      thetaEarth += Math.PI/earthOrbitPeriod*reverse;
      if (thetaEarth==2*Math.PI){thetaEarth=0;}
      earthXPos=earthRad*Math.cos(thetaEarth);
      earthYPos=earthRad*Math.sin(thetaEarth);
      
      thetaMars += Math.PI/(1.88*earthOrbitPeriod*reverse);
      if (thetaMars==2*Math.PI){thetaMars=0;}
      marsXPos=marsRad*Math.cos(thetaMars);
      marsYPos=marsRad*Math.sin(thetaMars);
      
      thetaJupiter += Math.PI/(11.9*earthOrbitPeriod)*reverse;
      if (thetaJupiter==2*Math.PI){thetaJupiter=0;}
      jupiterXPos=jupiterRad*Math.cos(thetaJupiter);
      jupiterYPos=jupiterRad*Math.sin(thetaJupiter);
      
      thetaSaturn += Math.PI/(29.4*earthOrbitPeriod)*reverse;
      if (thetaSaturn==2*Math.PI){thetaSaturn=0;}
      saturnXPos=saturnRad*Math.cos(thetaSaturn);
      saturnYPos=saturnRad*Math.sin(thetaSaturn);
      
      thetaUranus += Math.PI/(83.7*earthOrbitPeriod*reverse);
      if (thetaUranus==2*Math.PI){thetaUranus=0;}
      uranusXPos=uranusRad*Math.cos(thetaUranus);
      uranusYPos=uranusRad*Math.sin(thetaUranus);
      
      thetaNeptune += Math.PI/(163.7*earthOrbitPeriod)*reverse;
      if (thetaNeptune==2*Math.PI){thetaNeptune=0;}
      neptuneXPos=neptuneRad*Math.cos(thetaNeptune);
      neptuneYPos=neptuneRad*Math.sin(thetaNeptune);
      
      thetaPluto += Math.PI/(248.0*earthOrbitPeriod*reverse);
      if (thetaPluto==2*Math.PI){thetaPluto=0;}
      plutoXPos=plutoRad*Math.cos(thetaPluto);
      plutoYPos=plutoRad*Math.sin(thetaPluto);
      
      theta += Math.PI/(0.0748*earthOrbitPeriod)*reverse;
      
      if (theta==2*Math.PI){theta=0;}
      moonXPos=moonRad*Math.cos(theta);
      moonYPos=moonRad*Math.sin(theta);
      //render rings
      gl.glLoadIdentity();
      gl.glTranslatef(0.0f,0.0f, 0.0f);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glColor3f(0.3f, 0.2f, 0.1f);
      gl.glDisable(GL_LIGHTING);
      GLUquadric ringMercury = glu.gluNewQuadric();
      glu.gluQuadricDrawStyle(ringMercury, GLU.GLU_LINE);
      glu.gluDisk(ringMercury, mercuryRad, mercuryRad, 100, 1);
      glu.gluDeleteQuadric(ringMercury);
      
      gl.glLoadIdentity();
      gl.glTranslatef(0.0f,0.0f, 0.0f);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glColor3f(0.3f, 0.3f, 0.1f);
      gl.glDisable(GL_LIGHTING);
      GLUquadric ringVenus = glu.gluNewQuadric();
      glu.gluQuadricDrawStyle(ringVenus, GLU.GLU_LINE);
      glu.gluDisk(ringVenus, venusRad, venusRad, 200, 1);
      glu.gluDeleteQuadric(ringVenus);
      
      gl.glLoadIdentity();
      gl.glTranslatef(0.0f,0.0f, 0.0f);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glColor3f(0.1f, 0.3f, 0.3f);
      gl.glDisable(GL_LIGHTING);
      GLUquadric ringEarth = glu.gluNewQuadric();
      glu.gluQuadricDrawStyle(ringEarth, GLU.GLU_LINE);
      glu.gluDisk(ringEarth, earthRad, earthRad, 200, 1);
      glu.gluDeleteQuadric(ringEarth);
      
      gl.glLoadIdentity();
      gl.glTranslatef(0.0f,0.0f, 0.0f);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glColor3f(0.3f, 0.1f, 0.1f);
      gl.glDisable(GL_LIGHTING);
      GLUquadric ringMars = glu.gluNewQuadric();
      glu.gluQuadricDrawStyle(ringMars, GLU.GLU_LINE);
      glu.gluDisk(ringMars, marsRad, marsRad, 200, 1);
      glu.gluDeleteQuadric(ringMars);
      
      gl.glLoadIdentity();
      gl.glTranslatef(0.0f,0.0f, 0.0f);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glColor3f(0.3f, 0.2f, 0.2f);
      gl.glDisable(GL_LIGHTING);
      GLUquadric ringJupiter = glu.gluNewQuadric();
      glu.gluQuadricDrawStyle(ringJupiter, GLU.GLU_LINE);
      glu.gluDisk(ringJupiter, jupiterRad, jupiterRad, 200, 1);
      glu.gluDeleteQuadric(ringJupiter);
      
      gl.glLoadIdentity();
      gl.glTranslatef(0.0f,0.0f, 0.0f);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glColor3f(0.3f, 0.3f, 0.2f);
      gl.glDisable(GL_LIGHTING);
      GLUquadric ringSaturn = glu.gluNewQuadric();
      glu.gluQuadricDrawStyle(ringSaturn, GLU.GLU_LINE);
      glu.gluDisk(ringSaturn, saturnRad, saturnRad, 200, 1);
      glu.gluDeleteQuadric(ringSaturn);
      
      gl.glLoadIdentity();
      gl.glTranslatef(0.0f,0.0f, 0.0f);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glColor3f(0.1f, 0.2f, 0.3f);
      gl.glDisable(GL_LIGHTING);
      GLUquadric ringUranus = glu.gluNewQuadric();
      glu.gluQuadricDrawStyle(ringUranus, GLU.GLU_LINE);
      glu.gluDisk(ringUranus, uranusRad,uranusRad, 200, 1);
      glu.gluDeleteQuadric(ringUranus);
      
      gl.glLoadIdentity();
      gl.glTranslatef(0.0f,0.0f, 0.0f);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glColor3f(0.1f, 0.1f, 0.3f);
      gl.glDisable(GL_LIGHTING);
      GLUquadric ringNeptune = glu.gluNewQuadric();
      glu.gluQuadricDrawStyle(ringNeptune, GLU.GLU_LINE);
      glu.gluDisk(ringNeptune, neptuneRad, neptuneRad, 200, 1);
      glu.gluDeleteQuadric(ringNeptune);
      
      gl.glLoadIdentity();
      gl.glTranslatef(0.0f,0.0f, 0.0f);
      //gl.glRotatef(-15,0,1,0);
      
      gl.glDisable(GL.GL_TEXTURE_2D);
      gl.glColor3f(0.3f, 0.3f, 0.3f);
      gl.glDisable(GL_LIGHTING);
      GLUquadric ringPluto = glu.gluNewQuadric();
      glu.gluQuadricDrawStyle(ringPluto, GLU.GLU_LINE);
      glu.gluDisk(ringPluto, plutoRad, plutoRad, 200, 1);
      glu.gluDeleteQuadric(ringPluto);
      
      
      
      
   }
 
   /**
    * Called back before the OpenGL context is destroyed. Release resource such as buffers.
    */
   @Override
   public void dispose(GLAutoDrawable drawable) { }
}
