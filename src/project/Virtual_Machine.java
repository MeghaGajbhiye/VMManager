package project;

import java.rmi.RemoteException;

import project.Virtual_Host;
import project.manager.Snapshot_Manager;
import project.utilities.VM_Utility;
import com.vmware.vim25.AlarmState;
import com.vmware.vim25.VirtualMachineMovePriority;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.Alarm;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class Virtual_Machine {
	//Keeping information
	private String vmName;
	private ServiceInstance si;
	private String ip;
	private VirtualMachine virtualMachine;

	public Virtual_Machine(VirtualMachine vm) {
		//storing virtual machine information 
		virtualMachine=vm;

	}
	
	public Virtual_Machine(String vmName, ServiceInstance si){
		//storing name and si instance
		super();
		this.vmName=vmName;
		this.si = si;
		updateVirtualMachineDetails();
		updateIp();
		
	}

	private synchronized void updateIp() {
		ip = virtualMachine.getGuest().getIpAddress();
		System.out.println("IP OF THE GUEST IS: "+ ip);
	}

	private void updateVirtualMachineDetails() {
		//If vm name is null or si is null, it takes the data from the rootfolder for that particular 
		if(vmName==null || si==null){
			System.out.println("Error! VM Name is null or si is null");
			return;
		}
		
		
		try {
				if(virtualMachine==null){
				virtualMachine = (VirtualMachine) new InventoryNavigator(
						si.getRootFolder()).searchManagedEntity(
						"VirtualMachine", vmName);
				}
		} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		
	}
	
	public String vmState() {
		if (virtualMachine == null){
			return null;
		}
		return virtualMachine.getRuntime().getPowerState().name();

	}
	
	public boolean migrate(Virtual_Host newhost){
		
		boolean succes=false;
		try {
			Task task=virtualMachine.migrateVM_Task((ResourcePool) newhost
					.getHost().getParent(), newhost.getHost(),
					VirtualMachineMovePriority.highPriority,
					VirtualMachinePowerState.poweredOn);
			
			if(task.waitForTask()==task.SUCCESS){
				if (virtualMachine.getRuntime().getPowerState() != VirtualMachinePowerState.poweredOn)
						powerOn();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return succes;
		
	}

	public boolean createSnapShot() {
		try {
			Snapshot_Manager.createSnapShot(this);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public VirtualMachine getVirtualMachine() {
		return virtualMachine;
	}

	public String getVmName() {
		if (vmName == null){
			vmName = virtualMachine.getName();
		}
		return vmName;

	}

	public String getIp() {
		if (ip == null){
			updateIp();
		}
		return ip;
	}
	
	public void setVmName(String vmName) {
		this.vmName = vmName;
	}
	public ServiceInstance getServiceInstance() {
		return si;
	}

	public void setServiceInstance(ServiceInstance serviceInstance) {
		this.si = serviceInstance;
	}
	
	public void setVirtualMachine(VirtualMachine virtualMachine) {
		this.virtualMachine = virtualMachine;
	}


	public synchronized void powerOn() {
		
		try {
			Task task=virtualMachine.powerOnVM_Task(null);
			System.out.println(virtualMachine.getName() + " is powering on...");

			if (task.waitForTask() == Task.SUCCESS)
				Thread.sleep(5000);
			System.out.println(virtualMachine.getName() + " is running now.");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public synchronized boolean ping() {
		return VM_Utility.pingIp(getIp());
	}

	public boolean checkPowerOffAlarm(Alarm alarm) {
		AlarmState[] aStates = virtualMachine.getTriggeredAlarmState();
		if (aStates == null)
			return false;
		for (AlarmState aState : aStates) {
			if (alarm.getMOR().getVal().equals(aState.getAlarm().getVal()))
				return true;
		}
		return false;
	}

	public boolean recover() throws Exception {
		return Snapshot_Manager.revertToLastSnapShot(this);
	}
}