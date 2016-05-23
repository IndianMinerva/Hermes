package com.im.resources;

import com.im.beans.JVM;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/threads")
@ExposesResourceFor(JVM.class)
public class ThreadsResource {
}
