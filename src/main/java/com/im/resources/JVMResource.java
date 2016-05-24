package com.im.resources;

import com.im.JvmUtils;
import com.im.beans.JVM;
import com.im.services.JVMService;
import com.sun.tools.attach.AttachNotSupportedException;
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
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/jvms")
@ExposesResourceFor(JVM.class)
public class JVMResource {

    @Autowired private JvmUtils jvmUtils;
    @Autowired private JVMService jvmService;
    @Autowired private EntityLinks entityLinks;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    HttpEntity<Resources<List<JVM>>> getAllJVMs() {
        Resources<List<JVM>> resources = new Resources(jvmService.getAllJVMs());
        resources.add(this.entityLinks.linkToCollectionResource(JVM.class));
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/threads", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    HttpEntity<Resources<List<ThreadInfo>>> getThreadInfo(@PathVariable String id) throws AttachNotSupportedException, IOException {
        List<ThreadInfo> threadInfos = new ArrayList<>();
        try {
            VirtualMachine vm = VirtualMachine.attach(id);
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
        }

        Resources<List<ThreadInfo>> resources = new Resources(threadInfos);
        resources.add(this.entityLinks.linkToCollectionResource(JVM.class));
        return new ResponseEntity<>(resources, HttpStatus.OK);
    }
}
