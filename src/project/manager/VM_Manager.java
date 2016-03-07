package project.manager;

import java.net.URL;
import java.util.Properties;

import project.Virtual_Machine;

import project.Virtual_Center;
import project.utilities.VM_Utility;
import project.utilities.Constants;
import project.utilities.VM_Properties;

import com.vmware.vim25.mo.ServiceInstance;

public class VM_Manager {
	private static VM_Manager vm_manager;
	private Properties properties;
	private ServiceInstance sinstance;
	private Virtual_Machine virtual_Machine;
	private VM_Utility vm_Utility;
	private Virtual_Center virtual_Center;
	private static Object mutex = new Object();
	

	private VM_Manager()
	{
		try {
			
			properties=new VM_Properties(Constants.CONFIGURATION_FILE);
			
			/*************** Get an instance of VIM Server*************/
			
			URL url = new URL("https://192.168.200.128/sdk");
			sinstance = new ServiceInstance(url, properties.getProperty(Constants.VIRTUALCENTER_USERNAME),
					properties.getProperty(Constants.VIRTUALCENTER_PASSWORD),true);
			
			/**********************************************************/
			vm_Utility = new VM_Utility(sinstance);
			virtual_Center = new Virtual_Center(sinstance);
		    }
		catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Exception is: "+ e.getMessage());
			e.printStackTrace();
		    }
				
    }
	
    public static VM_Manager VM_Instance()
    {
		
		if(vm_manager==null){
			synchronized (mutex) {
				vm_manager=new VM_Manager();
				
			}
		}
		return vm_manager;
    }
    
    public void Start_VMManager()
    {
    	
    	Thread information = new Thread() 
	     {
			public void run()
			{				
				try 
				{
					while(true)
					{
						vm_Utility.VM_Information();
					    Thread.sleep(Integer.parseInt(properties.getProperty(
					    Constants.STAT_TIME, "10000000000000000")));
					}
				 } 
				 catch (NumberFormatException | InterruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		Thread SnapShot=new Thread()
		{
			public void run()
			{
				try 
				{
					while(true)
					{
						System.out.println("Taking Snapshot");
						Virtual_Center.SnapshotVM();
						Thread.sleep(Long.parseLong(properties.getProperty(
						Constants.SNAPSHOT_TIME, "300000")));
					}
				}
			    catch (NumberFormatException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				catch (InterruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread recoverVM=new Thread()
		{
			public void run()
			{
				try
				{
					while(true)
					{
						virtual_Center.recoverVM();
						Thread.sleep(Long.parseLong(properties.getProperty(
						Constants.RECOVERY_TIME, "20000")));
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
		};
		
		information.start();
		 SnapShot.start();
		recoverVM.start();
    }
    public Properties getProperties() {
		return properties;
	}
	
	public ServiceInstance getServiceInstance(){
		return sinstance;
	}
	
	public Virtual_Machine getvMachine() {
		return virtual_Machine;
	}

	public VM_Utility getVmUtility() {
		return vm_Utility;
	}

	public Virtual_Center getvCenter() {
		return virtual_Center;
	}

}

