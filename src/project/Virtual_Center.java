package project;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import project.Virtual_Host;
import com.vmware.vim25.*;

//import project.Host_System;
//import project.Inventory_Navigator;
//import project.Managed_Entity;


import project.Virtual_Machine;
import project.manager.Alarm_Manager;
import com.vmware.vim25.mo.Alarm;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;

public class Virtual_Center {
	
	private ServiceInstance si;
	private static List<Virtual_Host> vHost;
	private Alarm powerOffAlarm;
	
    public Virtual_Center(ServiceInstance serviceInstance)
    {
		
		this.si=serviceInstance;
		Initialize_Virtual_Hosts();
		/*
		try {
			// powerOffAlarm=Alarm_Manager.PowerOffAlarm(serviceInstance);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		*/
    }
    
    private void Initialize_Virtual_Hosts() {
		vHost=new ArrayList<Virtual_Host>();
		
		System.out.println("Starting the retrieval of all the Hosts: ");
		try {
			ManagedEntity mes[]=new InventoryNavigator(si.getRootFolder()).searchManagedEntities("HostSystem");
			for(ManagedEntity entities:mes){
				//System.out.println("Host " + entities.getName() + "Retrieved Successfully");
				vHost.add(new Virtual_Host((HostSystem) entities));
			}
			//System.out.println("All vHosts and VMs retrieved successfully..");
			System.out.println("All VMs retrieved successfully..");
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static boolean SnapshotVM() {
		System.out.println(Thread.currentThread()
				+ "Snapshot creation process started...");
		for (Virtual_Host vHost : vHost)
			if (!vHost.createSnapShot())
				return false;
		return true;
				
	}

	public boolean recoverVM() throws Exception {
		boolean success=true;
		for(Virtual_Host host:vHost){
			if(!host.checkVMsHealth()){
				if (host.ping()) {
					Virtual_Machine vMachine = host.findFaultyVM();
					if (vMachine == null)
						continue;
					if (vMachine.checkPowerOffAlarm(powerOffAlarm)) {
						System.out.println(vMachine.getVmName()
								+ " is powered off.");
						continue;
					} else {
						if (vMachine.recover()) {
							System.out.println("VirtualMachine: "
									+ vMachine.getVmName()
									+ " recovered successfuly..");

						} else
							System.out
									.println("Something went wrong... Not able to recover Virtual Machine: "
											+ vMachine.getVmName());
					}
				} else {
					if (host.recoverVHost())
						System.out.println("VHost \"" + host.getIP()
								+ "\" recovered successfully..");
					else
						System.out
								.println("Something went wrong... Not able to recover VHost \""
										+ host.getIP() + "\"");
				}
				
			}
			
		}
		return success;
		
	}
	
	public boolean checkVHosts() {
		return findFaultyVHost() == null;
	}

	private Virtual_Host findFaultyVHost() {
		for(Virtual_Host host:vHost){
			if(!host.ping()){
				return host;
			}
		}
		
		return null;
		
	}
	public List<Virtual_Host> getVHostList(){
		return vHost;
		
	}
	public boolean createSnapShot() {
		System.out.println(Thread.currentThread()
				+ "Snapshot creation process started...");
		for (Virtual_Host vHost : vHost)
			if (!vHost.createSnapShot())
				return false;
		return true;
	}
}