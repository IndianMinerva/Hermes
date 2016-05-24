package com.im.resources;

import com.im.JvmUtils;
import com.im.beans.JVM;
import com.im.services.JVMService;
import com.sun.tools.attach.VirtualMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping("/jvms")
@ExposesResourceFor(JVM.class)
public class JVMResource {

    @Autowired private JvmUtils jvmUtils;
    @Autowired private JVMService jvmService;
    @Autowired private EntityLinks entityLinks;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    HttpEntity<Resources<List<JVM>>> getAllJVMs() throws IOException {
        return prepareResourcesAndReturn(new Resources(jvmService.getAllJVMs()), null);
    }

    @RequestMapping(value = "/{id}/thread", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    HttpEntity<Resources<List<ThreadInfo>>> getThreadInfo(@PathVariable String id) throws IOException {
        VirtualMachine vm = null;
        List<ThreadInfo> threadInfos = new ArrayList<>();
        try {
            vm = VirtualMachine.attach(id);
            String jmxUrl = jvmUtils.getJmxUrl(vm);

            MBeanServerConnection mbsc = JMXConnectorFactory.connect(new JMXServiceURL(jmxUrl)).getMBeanServerConnection();
            ObjectName mbean = (ObjectName) mbsc.queryNames(new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME), null).toArray()[0];
            ThreadMXBean threadBean = ManagementFactory.newPlatformMXBeanProxy(mbsc, mbean.toString(), ThreadMXBean.class);

            for (long threadId : threadBean.getAllThreadIds()) {
                threadInfos.add(threadBean.getThreadInfo(threadId));
            }
        } catch (MalformedObjectNameException mone) {
            throw new IOException(mone);
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            vm.detach();
        }
        return prepareResourcesAndReturn(new Resources(threadInfos), id);
    }


    @RequestMapping(value = "/{id}/memory", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    HttpEntity<Resources<MemoryUsage>> getMemoryInfo(@PathVariable String id) throws IOException {
        VirtualMachine vm = null;
        List<MemoryUsage> memoryUsages = new ArrayList<>();

        try {
            vm = VirtualMachine.attach(id);
            String jmxUrl = jvmUtils.getJmxUrl(vm);

            MBeanServerConnection mbsc = JMXConnectorFactory.connect(new JMXServiceURL(jmxUrl)).getMBeanServerConnection();
            ObjectName mbean = (ObjectName) mbsc.queryNames(new ObjectName(ManagementFactory.MEMORY_MXBEAN_NAME), null).toArray()[0];
            MemoryMXBean memoryMXBean = ManagementFactory.newPlatformMXBeanProxy(mbsc, mbean.toString(), MemoryMXBean.class);
            memoryUsages.add(memoryMXBean.getHeapMemoryUsage());
            memoryUsages.add(memoryMXBean.getNonHeapMemoryUsage());
            vm.detach();
        } catch (MalformedObjectNameException mone) {
            throw new IOException(mone);
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            vm.detach();
        }
        return prepareResourcesAndReturn(new Resources(memoryUsages), id);
    }

    private <T> HttpEntity<Resources<List<T>>> prepareResourcesAndReturn(Resources<List<T>> resources, String id) throws IOException {
        String idValue = StringUtils.isEmpty(id) ? "id" : id;
        resources.add(linkTo(methodOn(JVMResource.class).getAllJVMs()).withRel("jvms"));
        resources.add(linkTo(methodOn(JVMResource.class).getThreadInfo(idValue)).withRel("threads"));
        resources.add(linkTo(methodOn(JVMResource.class).getMemoryInfo(idValue)).withRel("memory"));
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }
}
