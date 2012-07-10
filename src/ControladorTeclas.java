import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class ControladorTeclas implements KeyListener {

	Ventana ventana;

	ControladorTeclas(Ventana ventana){
		this.ventana=ventana;
	}
	
	
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
        if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) ||
            (keyCode == KeyEvent.VK_END) ||
            ((keyCode == KeyEvent.VK_C) && e.isControlDown()) )ventana.cerrar();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

}
