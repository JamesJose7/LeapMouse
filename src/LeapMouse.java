import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.Type;

import java.awt.Dimension;
import java.awt.Robot;

class CustomListener extends Listener {
	
	public Robot robot;
	
	public void onConnect(Controller controller) {
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
		controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
	}
	
	public void onFrame(Controller controller) {
		try { 
			robot = new Robot(); 
		} catch (Exception e) {}
		
		Frame frame = controller.frame();
		InteractionBox box = frame.interactionBox();
		for (Finger finger : frame.fingers()) {
			if (finger.type() == Finger.Type.TYPE_INDEX) {
				Vector fingerPos = finger.stabilizedTipPosition();
				Vector boxFingerPos = box.normalizePoint(fingerPos);
				Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
				robot.mouseMove((int) (screen.width * boxFingerPos.getX()), (int) (screen.height - boxFingerPos.getY() * screen.height));
			}
		}
		
		for (Gesture gesture : frame.gestures()) {
			if (gesture.type() == Type.TYPE_CIRCLE) {
				CircleGesture circle = new CircleGesture(gesture);
				if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI/4) {
					robot.mouseWheel(1);
					try {
						Thread.sleep(50);
					} catch (Exception e) {}
				} else {
					robot.mouseWheel(-1);
					try {
						Thread.sleep(50);
					} catch (Exception e) {}
				}
					
			}
		}
	}
}

public class LeapMouse {

	public static void main(String[] args) {
		CustomListener listener = new CustomListener();
		Controller controller = new Controller();
		controller.addListener(listener);
		
		try {
			System.in.read();
		} catch (Exception e) {
			
		}
		
		controller.removeListener(listener);
	}
}


