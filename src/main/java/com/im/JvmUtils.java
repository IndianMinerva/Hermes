package com.im;

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.VirtualMachine;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Properties;

@Component
public class JvmUtils {
    public String getJmxUrl(VirtualMachine virtualMachine) throws IOException, AgentLoadException, AgentInitializationException {
        String jmxUrl = readAgentProperty(virtualMachine, "com.sun.management.jmxremote.localConnectorAddress");
        if (jmxUrl == null) {
            loadMangementAgent(virtualMachine);
            jmxUrl = readAgentProperty(virtualMachine, "com.sun.management.jmxremote.localConnectorAddress");
        }
        return jmxUrl;
    }

    private void loadMangementAgent(VirtualMachine virtualMachine) throws IOException, AgentLoadException, AgentInitializationException {
        final String id = virtualMachine.id();
        String agent = null;
        Boolean loaded = false;

        String javaHome = virtualMachine.getSystemProperties().getProperty("java.home");
        agent = javaHome + "/lib/management-agent.jar";
        virtualMachine.loadAgent(agent);
    }

    private String readAgentProperty(VirtualMachine virtualMachine, String propertyName) {
        String propertyValue = null;
        try {
            Properties agentProperties = virtualMachine.getAgentProperties();
            propertyValue = agentProperties.getProperty(propertyName);
        } catch (IOException e) {
        }
        return propertyValue;
    }

}
