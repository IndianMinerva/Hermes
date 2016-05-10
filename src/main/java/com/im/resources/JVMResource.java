package com.im.resources;

import com.im.beans.JVM;
import com.im.services.JVMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("/jvms")
@ExposesResourceFor(JVM.class)
public class JVMResource {

    @Autowired private JVMService jvmService;

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    HttpEntity<Resources<List<JVM>>> getAllJVMs() {
        //resources.add(this.entityLinks.linkToCollectionResource(JVM.class));
        return new ResponseEntity<>(new Resources(jvmService.getAllJVMs()), HttpStatus.OK);
    }
}
