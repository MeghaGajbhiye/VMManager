package project;

import project.manager.VM_Manager;

public class Main {

	public static void main(String[] args) {
			
		VM_Manager manager = VM_Manager.VM_Instance();
		manager.Start_VMManager();
	}

}
