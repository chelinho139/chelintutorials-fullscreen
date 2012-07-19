import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.Vector;

import javax.swing.*;


@SuppressWarnings("serial")
public class Ventana extends JFrame implements Runnable {

	private static final int NUMERO_FLIPPING_BUFFERS = 2;
	private static final int FPS_DESEADO = 60; //de 60 para abajo anda bien
	private static final int TIEMPO_ESPERA = (int) (1/(FPS_DESEADO/1000.0));
	private static final int NUMERO_PROMEDIAR = 15;
	
	//Atributos
	private boolean ejecutando=true;
	private Thread threadprincipal;
	
	
	//Calculo de FPS
	private Vector<Double> ultimosFPS = new Vector<Double>() ;
	private double FPS_ult_promedio=0;
	
	//Objs Para Pantalla Completa
	private int  ancho;
	private int largo;
	private GraphicsDevice graphicsdevice; //Tarjeta de Video
	private Graphics graphics;
	private BufferStrategy bufferstrategy;
		
	
	/*Constructor de la ventana*/
	public Ventana (){
		super("Mi Juego 1.0");
		
		this.addKeyListener(new ControladorTeclas(this));
		
		iniciarPantallaCompleta();
		iniciarThread();
		
	}
	
	
	/*Pintar su juego*/
	private void renderizar_pantalla(Graphics g) {
		
		 g.setColor(Color.blue);
		 g.fillRect (0, 0, ancho, largo);
		 
		 Font font = new Font("SansSerif", Font.BOLD, 24);;
		   
		 g.setColor(Color.WHITE);
		 g.setFont(font);
		 g.drawString("FPS: "+FPS_ult_promedio, 25, 20);
	}

	private void actualizarJuego() {
		/*Actualizar su juego*/
	}	
	
	
	public void run() {
		/*Ciclo Principal: Espera tiempo para mantener FPS constantes*/
		long nuevotiempo= 0L ;
		long viejotiempo= 0L ;
		long diferenciatiempo=0L;
	    long faltodormirtiempo=0L;
		viejotiempo = System.nanoTime();
	    
		while(ejecutando){
	    
			actualizarJuego();
			actualizarPantalla();
						
			nuevotiempo = System.nanoTime();
			diferenciatiempo = (nuevotiempo - viejotiempo)/1000000L;// nano -> ms
			faltodormirtiempo = (TIEMPO_ESPERA - diferenciatiempo); 
			
			if (faltodormirtiempo > 0) {   // SI FALTA ESPERAR MAS TIEMPO:
				try {Thread.sleep(faltodormirtiempo);}
				catch(InterruptedException ex){}
			}
			
			else {    //Se tardo mas que el tiempo de espera dormir un poquito
				try {Thread.sleep(5);} 
				catch (InterruptedException e) {e.printStackTrace();}
			}
			
			calcular_fps_promedio(viejotiempo);
			viejotiempo = System.nanoTime();
			
		
		
		} //fin del while ejecutando
		System.exit(0);
		
	}//fin de run
	

	public void cerrar() {
		ejecutando=false;		
	}
	
	public void calcular_fps_promedio(long viejotiempo){
		double fps_actual =calcular_fps_actual(System.nanoTime(),viejotiempo);
		ultimosFPS.add(fps_actual);
		
		if (ultimosFPS.size()>NUMERO_PROMEDIAR){
			calcularpromedio();
			ultimosFPS.clear();		}
		
		
	}
	
	public void calcularpromedio(){
		double suma=0;
		for(int i=0;i<ultimosFPS.size(); i++ )suma+=ultimosFPS.elementAt(i);
		
		FPS_ult_promedio=suma/ultimosFPS.size();
	}
	
	public double calcular_fps_actual(long tiempoactual, long tiempoinicial){
		double FPS_actual;
		double diftiempo= (tiempoactual-tiempoinicial)/1000000000.0;
		FPS_actual= (double)1.00/diftiempo;
		return FPS_actual;
		
	}
	
	

	/***************************************************************
	 * Funciones para utilizar pantalla completa* */
	
	private void actualizarPantalla() {
		graphics= bufferstrategy.getDrawGraphics();
				
		renderizar_pantalla(graphics);
		
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
	
	/** FIN DE Funciones pantalla completa*
	 **************************************************************  */
	
	

	public static void main(String[] args) {
		Ventana v1= new Ventana();
	}

}
