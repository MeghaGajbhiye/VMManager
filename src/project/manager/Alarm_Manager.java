package project.manager;

import com.vmware.vim25.AlarmSetting;
import com.vmware.vim25.AlarmSpec;
import com.vmware.vim25.StateAlarmExpression;
import com.vmware.vim25.StateAlarmOperator;
import com.vmware.vim25.mo.Alarm;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;

public class Alarm_Manager {
	// create alarm on the datacenter, which can be applied to all sub-vms
	public static Alarm PowerOffAlarm(ServiceInstance si) throws Exception {
		ManagedEntity[] dcs = si.getRootFolder().getChildEntity();

		com.vmware.vim25.mo.AlarmManager alarmMgr = si.getAlarmManager();
		AlarmSpec spec = new AlarmSpec();
		StateAlarmExpression expression = newStateAlarmExpression();
		
		spec.setAction(null);
		spec.setExpression(expression);
		spec.setName("Virtual Machine Powered Off");
		spec.setDescription("Monitor VM state and alarm when VM powers off");
		spec.setEnabled(true);
		
		AlarmSetting as = new AlarmSetting();
		as.setReportingFrequency(0); // the alarm is allowed to trigger as often
										// as possible
		as.setToleranceRange(0);

		spec.setSetting(as);
		Alarm[] alarms = alarmMgr.getAlarm(dcs[0]);
		for (Alarm alarm : alarms) {
			if (alarm.getAlarmInfo().getName().equals(spec.getName()))
				alarm.removeAlarm();
		}
		Alarm alarm = alarmMgr.createAlarm(dcs[0], spec);
		System.out.println("PowerOff State Alarm created!");
		Thread.sleep(2000);
		return alarm;
	}
	
	private static StateAlarmExpression newStateAlarmExpression() {
		StateAlarmExpression expression = new StateAlarmExpression();
		expression.setType("VirtualMachine");
		expression.setStatePath("runtime.powerState");
		expression.setOperator(StateAlarmOperator.isEqual);
		expression.setRed("poweredOff");
		return expression;
	}
	}


