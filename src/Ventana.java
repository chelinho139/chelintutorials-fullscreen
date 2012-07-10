import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import javax.swing.*;


@SuppressWarnings("serial")
public class Ventana extends JFrame implements Runnable {

	private static final int NUMERO_FLIPPING_BUFFERS = 2;
	private static final int TIEMPO_ESPERA = 20;
	private static final int NO_DELAYS_PER_YIELD = 16;

	
	//Atributos
	private boolean ejecutando=true;
	private Thread threadprincipal;
	
	private int  ancho;
	private int largo;
	private GraphicsDevice graphicsdevice; //Tarjeta de Video
	private Graphics graphics;
	private BufferStrategy bufferstrategy;
		
	public Ventana (){
		super("Mi Juego 1.0");
		
		this.addKeyListener(new ControladorTeclas(this));
		
		iniciarPantallaCompleta();
		iniciarThread();
		
	}
	
	private void pintar(Graphics g) {
		/*Pintar su juego*/
		 g.setColor(Color.blue);
		 g.fillRect (0, 0, ancho, largo);
		 
		 Font font = new Font("SansSerif", Font.BOLD, 24);;
		   
		 g.setColor(Color.WHITE);
		 g.setFont(font);
		 g.drawString("FPS: ", 25, 20);
	}

	private void actualizarJuego() {
		/*Actualizar su juego*/
	}	
	
	
	public void run() {
		/*Ciclo Principal: Espera o Adelanta tiempo para mantener FPS constantes*/
		long nuevotiempo= 0L ;
		long viejotiempo= 0L ;
		long diferenciatiempo=0L;
		long exesoacomuladotiempo=0L;
	    int noDelays = 0;
	    long excess = 0L;
	    long faltadormirtiempo=0;
	    
		viejotiempo = System.nanoTime();
	    
		while(ejecutando){
	    
			actualizarJuego();
			actualizarPantalla();
						
			nuevotiempo = System.nanoTime();
			diferenciatiempo = nuevotiempo - viejotiempo;
			faltadormirtiempo = (TIEMPO_ESPERA - diferenciatiempo) - exesoacomuladotiempo;  

			if (faltadormirtiempo > 0) {   // SI FALTA ESPERAR MAS TIEMPO:
				try {
					Thread.sleep(faltadormirtiempo/1000000L);  // nano -> ms
				}
				catch(InterruptedException ex){}
				exesoacomuladotiempo = (System.nanoTime() - nuevotiempo) - faltadormirtiempo;
			}
			else {    //Se tardo mas que el tiempo de espera
				excess -= faltadormirtiempo;  // store excess time value
				exesoacomuladotiempo = 0L;

				if (++noDelays >= NO_DELAYS_PER_YIELD) {
					Thread.yield();   // give another thread a chance to run
					noDelays = 0;
				}
			}
			viejotiempo = System.nanoTime();
		}
		System.exit(0);
	}
			
	private void actualizarPantalla() {
		graphics= bufferstrategy.getDrawGraphics();
				
		pintar(graphics);
		
		graphics.dispose();
		if(!bufferstrategy.contentsLost())bufferstrategy.show();
		else System.out.println("Contento lost en acutalizar pantalla");
		Toolkit.getDefaultToolkit().sync();
	}

	private void iniciarThread() {
		/*Empezar el thread, el metodo run*/
		if (threadprincipal==null){
			threadprincipal= new Thread(this);
			threadprincipal.start();
		}
	}

	private void iniciarPantallaCompleta() {
		/*Inicia la Ventana en Pantalla Completa*/
		
		GraphicsEnvironment graphicsenviroment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		graphicsdevice = graphicsenviroment.getDefaultScreenDevice();//Obtengo la Tarjeta de Video
		
		setUndecorated(true);//sin bordes
		setResizable(false);//sin redimensionar
		setIgnoreRepaint(true);//sin repaint (ya que lo hacemos nostros)
		
		if(!(graphicsdevice.isFullScreenSupported())){
			//si el sistema no admite pantalla completa salir
			System.out.println("No se puede hacer fullscreen");
			System.exit(0);			
		}
		
		graphicsdevice.setFullScreenWindow(this);//Hacer fullscreen
		
		ancho= this.getBounds().width;//Obtener dimensiones del fullscreen
		largo= this.getBounds().height;
			
		setBufferStrategy(); //Page Flipping
	}
	
	
	private void setBufferStrategy() {
		/*Establece la estrategia pageflipping*/
		createBufferStrategy(NUMERO_FLIPPING_BUFFERS);
		try {
			Thread.sleep(500); //Espero 500ms por las dudas.
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		bufferstrategy = getBufferStrategy();
	}

	public void cerrar() {
		ejecutando=false;		
	}

	public static void main(String[] args) {
		Ventana v1= new Ventana();
	}

}
