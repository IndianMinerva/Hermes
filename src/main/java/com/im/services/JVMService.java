package com.im.services;

import com.im.beans.JVM;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JVMService {

    public List<JVM> getAllJVMs() {
        List<JVM> jvms = new ArrayList<>();
        for (VirtualMachineDescriptor virtualMachine : VirtualMachine.list()) {
            jvms.add(new JVM(virtualMachine.id(), virtualMachine.displayName()));
        }
        return jvms;
    }
}
