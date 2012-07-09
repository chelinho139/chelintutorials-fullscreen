import java.awt.DisplayMode;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;
import javax.swing.*;


public class Ventana extends JFrame implements Runnable {

	//Atributos
	private Thread threadprincipal;
	
	private int  ancho;
	private int largo;
	
	private GraphicsDevice graphicsdevice; //Tarjeta de Video
	private Graphics graphics;
	
	private final int PAGE_FLIPPING_NUMBER = 2;
	private BufferStrategy bufferstrategy;
	
	private boolean ejecutando=true;
	
	public Ventana (){
		super("Mi Juego 1.0");
		iniciarPantallaCompleta();
		iniciarThread();
		
	}
	
	private void iniciarThread() {
		if (threadprincipal==null){
			threadprincipal= new Thread(this);
			threadprincipal.start();
		}
		
	}

	public void run() {
		int i = 0;
		while(ejecutando){
			
			System.out.println("debug:"+(i++));
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}	
	
	
	private void iniciarPantallaCompleta() {
		/*Inicia la Ventana en Pantalla Completa*/
		//Obtengo la Tarjeta de Video
		GraphicsEnvironment graphicsenviroment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		graphicsdevice = graphicsenviroment.getDefaultScreenDevice();
		
		setUndecorated(true);//sin bordes
		setResizable(false);
		
		if(!(graphicsdevice.isFullScreenSupported())){
			//si el sistema no admite pantalla completa salir
			System.out.println("No se puede hacer fullscreen");
			System.exit(0);			
		}
		
		graphicsdevice.setFullScreenWindow(this);
		graphicsdevice.setDisplayMode(new DisplayMode(1024,768,32,DisplayMode.REFRESH_RATE_UNKNOWN));
		
		ancho= this.getBounds().width;
		largo= this.getBounds().height;
			
		setBufferStrategy();
	}


	private void setBufferStrategy() {
		createBufferStrategy(PAGE_FLIPPING_NUMBER);
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		bufferstrategy = getBufferStrategy();
		
	}



	public static void main(String[] args) {
		Ventana v1= new Ventana();
	}




}
