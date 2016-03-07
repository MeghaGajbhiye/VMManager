package project.utilities;

import java.io.IOException;
import java.rmi.RemoteException;

import project.manager.VM_Manager;
import project.utilities.Constants;
import com.vmware.vim25.VirtualMachineCapability;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class VM_Utility {
	
	private ServiceInstance si;
	public VM_Utility(ServiceInstance si)
	{
		this.si=si;
	}
	public static boolean pingIp(String ip)
	{
		if(ip==null)
		{
			return false;
		}
	    String cmd="";
	   
	    cmd=Constants.PING_WINDOWS + Constants.SPACE + ip;
    	System.out.println("Ping VM " + cmd);
	    /*
	    if(System.getProperty("os.name").startsWith("windows"))
	    {
	    	cmd=Constants.PING_WINDOWS + Constants.SPACE + ip;
	    	System.out.println("Ping VM " + cmd);
		}
	    else 
	    {
	    	cmd=Constants.PING_MAC + Constants.SPACE + ip;
		
	    }
	    */
	    Process process=null;
		/*for(int attempt=1;attempt<=5; attempt++)*/
			for(int attempt=1; attempt <=Integer.parseInt(VM_Manager.VM_Instance().getProperties()
		.getProperty(Constants.PING_ATTEMPTS, "5")); attempt++)
		{
			System.out.println("Attempting to Ping a VM");
			System.out.println("Attempt "+ attempt);
			System.out.println("Pinging: " + ip);
			try {
				process=Runtime.getRuntime().exec(cmd);
				process.waitFor();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(process.exitValue() == 0){
				System.out.println("Ping to "+ip+"Successfull");
				return true;
			}
			
		}
		
		System.out.println("Ping failed");
		return false;
		
	}
	
	public void VM_Information() 
	
	{
		
		//rootFolder has the datacenter in it.
		Folder rootFolder=si.getRootFolder();
		String fname=rootFolder.getName();
		System.out.println("Root Folder is: "+ fname);
		
		//Array for to hold all the Virtual Machines in Root folder
		ManagedEntity mes[];
		
		try {
			mes=new InventoryNavigator(rootFolder)
				.searchManagedEntities("VirtualMachine");
						
			if(mes == null || mes.length == 0){
				System.out.println("No Vms are added to your datacenter");
				return;
			}
			System.out.println("");
			System.out.println("");
			System.out.println("");
			System.out.println("---------------------------------------------------------------------------------");
			System.out.println("---------------------------------------------------------------------------------");
			
			System.out.println("VIRTUAL MACHINE INFORMATION ");
			System.out.println("---------------------------------------------------------------------------------");
			System.out.println("---------------------------------------------------------------------------------");
			for(int i=0;i<mes.length;i++){
				
				VirtualMachine vm=(VirtualMachine)mes[i];
				System.out.println("VM name is : "+ vm.getName());
				System.out.println("Host is: "+ mes[i].getName());
				
				//System.out.println("Virtual Machine Information");
				System.out.println("Parent to Datacenter is:"+mes[i].getParent().getName());
				System.out.println("Resource Pool: "+vm.getResourcePool());
				System.out.println("Resource Pool Owner: "+ vm.getResourcePool().getOwner());
				System.out.println("Datastores used: "+ vm.getDatastores());
				System.out.println("VM Config:: " + vm.getConfig().name);
				System.out.println("Guest of the vm"+ vm.getGuest().guestFullName);
				System.out.println("Guest IP Address of the vm"+ vm.getGuest().getIpAddress());
				System.out.println("Parent for a vm:"+ vm.getParent().getName());
				System.out.println("VM resouce pool:: " + vm.getResourcePool().getName());
				System.out.println("");
				System.out.println("");
				//System.out.println("Resource pool Parent:: "+ vm.getResourcePool().getParent());
				System.out.println("---------------------------------------------------------------------------------");
				
				System.out.println("VM RUNTIME STATISTICS");
				System.out.println("---------------------------------------------------------------------------------");
					
				//System.out.println("Resource Pool:"+mes[i].getDeclaredAlarmState());
				System.out.println("VM Runtime:: " + vm.getRuntime());
				System.out.println("VM Storage:: " + vm.getStorage().toString());
				System.out.println("");
				System.out.println("");
				System.out.println("");
				
				System.out.println("---------------------------------------------------------------------------------");
				
				System.out.println("Virtual Machines Configuration Information... ");
				System.out.println("---------------------------------------------------------------------------------");
				
				//Virtual Machine Info
				VirtualMachineConfigInfo vminfo = vm.getConfig();
				VirtualMachineCapability vmc = vm.getCapability();
				
				System.out.println("GuestOS:: " + vminfo.getGuestFullName());
				System.out.println("GuestID:: " + vminfo.getGuestId());
				System.out.println("GuestName:: " + vminfo.getName());
				System.out.println("Does it suuport multiple snapshots?: "+ vmc.isMultipleSnapshotsSupported());

				//CPU Stats
				
				VirtualMachineRuntimeInfo vmri = vm.getRuntime();
				System.out.println("");
				System.out.println("");
				System.out.println("---------------------------------------------------------------------------------");
				System.out.println("VM Runtime Statistics");
				System.out.println("---------------------------------------------------------------------------------");
				
				System.out.println(vmri.host);
				System.out.println("Connection State:: "+ vmri.getConnectionState());
				System.out.println("Power State:: " + vmri.getPowerState());
				System.out.println("Max CPU Usage:: " + vmri.getMaxCpuUsage());
				System.out.println("Max Memory Usage:: "+ vmri.getMaxMemoryUsage());
				System.out.println("");
				System.out.println("");
				
				System.out.println("---------------------------------------------------------------------------------");
				System.out.println("---------------------------------------------------------------------------------");
				
				System.out.println("VIRTUAL MACHINE INFORMATION ");
				System.out.println("---------------------------------------------------------------------------------");
				System.out.println("---------------------------------------------------------------------------------");
				
			}
			
			
		} 
		
		catch (RemoteException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
	    }
	
	}
}