package problem;

import headfirst.command.undo.CeilingFan;
import headfirst.command.undo.CeilingFanHighCommand;
import headfirst.command.undo.CeilingFanMediumCommand;
import headfirst.command.undo.CeilingFanOffCommand;
import headfirst.command.undo.Light;
import headfirst.command.undo.LightOffCommand;
import headfirst.command.undo.LightOnCommand;

public class RemoteLoader {
 
	public static void main(String[] args) {
		RemoteControl remoteControl = new RemoteControl();
 
		Light livingRoomLight = new Light("Living Room");
 
		LightOnCommand livingRoomLightOn = 
				new LightOnCommand(livingRoomLight);
		LightOffCommand livingRoomLightOff = 
				new LightOffCommand(livingRoomLight);
 
		remoteControl.setCommand(0, livingRoomLightOn, livingRoomLightOff);
 
		remoteControl.onButtonWasPushed(0);
		remoteControl.offButtonWasPushed(0);
		System.out.println(remoteControl);
		remoteControl.undoButtonWasPushed();
		remoteControl.offButtonWasPushed(0);
		remoteControl.onButtonWasPushed(0);
		System.out.println(remoteControl);
		remoteControl.undoButtonWasPushed();

		CeilingFan ceilingFan = new CeilingFan("Living Room");
   
		CeilingFanMediumCommand ceilingFanMedium = 
				new CeilingFanMediumCommand(ceilingFan);
		CeilingFanHighCommand ceilingFanHigh = 
				new CeilingFanHighCommand(ceilingFan);
		CeilingFanOffCommand ceilingFanOff = 
				new CeilingFanOffCommand(ceilingFan);
  
		remoteControl.setCommand(0, ceilingFanMedium, ceilingFanOff);
		remoteControl.setCommand(1, ceilingFanHigh, ceilingFanOff);
   
		remoteControl.onButtonWasPushed(0);
		remoteControl.offButtonWasPushed(0);
		System.out.println(remoteControl);
		remoteControl.undoButtonWasPushed();
  
		remoteControl.onButtonWasPushed(1);
		System.out.println(remoteControl);
		remoteControl.undoButtonWasPushed();
	}
}
