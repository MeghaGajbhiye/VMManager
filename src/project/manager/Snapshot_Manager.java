package project.manager;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Properties;

import project.Virtual_Host;
import project.Virtual_Machine;
import project.utilities.Constants;
import project.utilities.VM_Utility;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;


public class Snapshot_Manager {
	static Virtual_Host virtualhost;
	//Create snapShots for VMs
	public static boolean createSnapShot(Virtual_Machine vMachine) throws Exception {
		boolean successFlag = false;
		if (vMachine.getIp() == null || !VM_Utility.pingIp(vMachine.getIp())) {
			System.out.println("Error!!! \n Either the virutal machine is invalid  or virtual machine is not reachable..");
			return false;
		}
		removeAllSnapshot(vMachine);
		Task task = vMachine.getVirtualMachine().createSnapshot_Task(
				vMachine.getVmName() + "-Recovery", "For recovery", false,
				false);

		if (task.waitForTask() == Task.SUCCESS) {
			successFlag = true;
			System.out.println("Snapshot taken successfully for virtual Machine in a Host: "+ vMachine.getVmName());
		}
		return successFlag;
	}
	
	//Create snapShot for Hosts
	public static boolean createSnapShot(Virtual_Host virtualHost, Properties vmProperties)throws Exception {
		boolean success = false;
		ServiceInstance serviceInstance = null;
		virtualhost=virtualHost;
		if (vmProperties == null) {
			System.out.println("VMProperties not initialized...");
			return false;
		}
		try {
				serviceInstance = new ServiceInstance(new URL(vmProperties.getProperty(Constants.ADMIN_URL)),
						vmProperties.getProperty(Constants.ADMIN_UID),
						vmProperties.getProperty(Constants.ADMIN_PASSWORD), true);

			VirtualMachine vm = (VirtualMachine) new InventoryNavigator(serviceInstance.getRootFolder())
									.searchManagedEntity("VirtualMachine", (Constants.HOST_MAP.get(virtualHost.getIP())));
									//searchManagedEntity("VirtualMachine", Constants.HOST_MAP.get(vHost.getIP()));
			//System.out.println("HOST IS: "+ vm.getName());
			
			if(VM_Utility.pingIp(virtualHost.getIP())) {
				removeAllSnapshot(vm);
				Task task = vm.createSnapshot_Task(vm.getName() + "-Recovery","For recovery", false, false);
				if (task.waitForTask() == Task.SUCCESS) {
					success = true;
					System.out.println("Snapshot taken successfully for Host: "+ vm.getName());
				}
			}
			
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.out.println(" MalformedURLException");
			//e.printStackTrace();
		} finally {
			if (serviceInstance != null)
				serviceInstance.getServerConnection().logout();
		}
		return success;
	}
	
	
	
	public static void removeAllSnapshot(Virtual_Machine vm) throws Exception {
		Task task = vm.getVirtualMachine().removeAllSnapshots_Task();
		if (task.waitForTask() == Task.SUCCESS) {
			System.out.println("All snapshots Removed for Hosts: " + vm.getVmName());
		}
	}
	
	
	
	public static void removeAllSnapshot(VirtualMachine vm) throws Exception {
		Task task = vm.removeAllSnapshots_Task();
		if (task.waitForTask() == Task.SUCCESS) {
			System.out.println("Removed all snapshots for VMs: " + vm.getName());
		}
	}
	
	//Retrieving From Snapshot
	public static boolean revertToLastSnapShot(Virtual_Machine vm) throws Exception {
		Task task = vm.getVirtualMachine().revertToCurrentSnapshot_Task(null);
		if (task.waitForTask() == Task.SUCCESS) {
			System.out.println("Virtual Machine: " + vm.getVmName()
					+ " successfully reverted to latest snapshot.");
	
/*CHECK THIS AFTER RUNNING*/
			vm.powerOn();
			/* TILL THIS */
			
			return true;
		}
		return false;
	}
	
	//Retrieving VM From SnapShot
	public static boolean retrieveToLastSnapShot(Virtual_Host vHost,Properties vmProperties) throws Exception{
		

			ServiceInstance si=new ServiceInstance((new URL(vmProperties.getProperty(Constants.ADMIN_URL))),
					vmProperties.getProperty(Constants.ADMIN_UID), vmProperties.getProperty(Constants.ADMIN_PASSWORD), true);
			VirtualMachine vm= (VirtualMachine) new InventoryNavigator(
					si.getRootFolder()).searchManagedEntity(
							"VirtualMachine", Constants.HOST_MAP.get(vHost.getIP()));
			Virtual_Machine virtualMachine=new Virtual_Machine (vm);
			
			boolean isRetrieve=revertToLastSnapShot(virtualMachine);
			virtualMachine.powerOn();
			si.getServerConnection().logout();
			vHost.reconnect();		
		
		return isRetrieve;
		
	}
	
	public static boolean revertToLastSnapShot(Virtual_Machine vMachine,HostSystem host){
		System.out.println("Reverting "+vMachine.getVmName()+"to latest snapshot");
		try {
			Task task= vMachine.getVirtualMachine().revertToCurrentSnapshot_Task(host);
			if(task.waitForTask()==task.SUCCESS){
				System.out.println("VirtualMachine: "+ vMachine.getVmName() + "Reverted to latest snapshot");
				return true;
			}
			else{
				System.out.println("Something went wrong.. \n Not able to recover from snapshot...");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
		
	}

}
