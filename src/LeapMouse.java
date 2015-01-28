import com.leapmotion.leap.*;
import com.leapmotion.leap.Gesture.State;
import com.leapmotion.leap.Gesture.Type;

import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;

class CustomListener extends Listener {
	
	public static Robot robot;
	
	public void onConnect(Controller controller) {
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
		controller.enableGesture(Gesture.Type.TYPE_SCREEN_TAP);
		controller.enableGesture(Gesture.Type.TYPE_KEY_TAP);
		
		
		controller.setPolicy(Controller.PolicyFlag.POLICY_BACKGROUND_FRAMES);
		controller.setPolicy(Controller.PolicyFlag.POLICY_IMAGES);
		controller.setPolicy(Controller.PolicyFlag.POLICY_OPTIMIZE_HMD);
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
				if (frame.fingers().count() == 4) {
					String[] keys = {
							"VK_ALT", "VK_F4"
					};
					
					sendKeysCombo(keys);
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
					
			} else if (gesture.type() == Type.TYPE_SCREEN_TAP) {
				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
			} else if (gesture.type() == Type.TYPE_SWIPE && gesture.state() == State.STATE_START) {
				String [] keysOSDashBoard = {
						"VK_CONTROL", "VK_LEFT"
				};
				//WINDOWS
				/*robot.keyPress(KeyEvent.VK_WINDOWS);
				robot.keyRelease(KeyEvent.VK_WINDOWS);*/
				
				//OS X
				sendKeysCombo(keysOSDashBoard);
				
				//Show desktop
				SwipeGesture swipe = new SwipeGesture();
				
				Vector direction = swipe.direction();
				
				if (direction.getZ() > 0 && direction.getY() < 0) {
					String[] keysShowDesktop = {
						"VK_CONTROL", "VK_M"
					};
					sendKeysCombo(keysShowDesktop);
				} else if (direction.getX() > 0) {
					String[] keysOpenMyPC = {
						"VK_E", "VK_Q", "VK_U", "VK_I", "VK_P", "VK_O", "VK_ENTER"
					};
					
					robot.keyPress(KeyEvent.VK_WINDOWS);
					try {
						Thread.sleep(50);
					} catch (Exception e) {}
					sendKeysCombo(keysOpenMyPC);
					
				}
				
			} else if (gesture.type() == Type.TYPE_KEY_TAP && gesture.state() == State.STATE_STOP) {
				//KEY TAP event
			}
			
		}
		
		
	}
	
	public static void sendKeysCombo(String keys[]) {
		try {
			
			Class<?> cl = KeyEvent.class;
			
			int [] intKeys = new int [keys.length];
			
			for (int i = 0; i < keys.length; i++) {
				Field field = cl.getDeclaredField(keys[i]);
				intKeys[i] = field.getInt(field);
				robot.keyPress(intKeys[i]);
			}
			
			for (int i = keys.length - 1; i >= 0; i--)
                robot.keyRelease(intKeys[i]);
			
		} 
		catch (Throwable e) {
			System.err.println(e);
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


