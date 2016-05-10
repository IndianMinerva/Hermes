package com.im.resources;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class IndexResource {

    @RequestMapping(method = RequestMethod.GET)
    public String getIndexPage() {
        return "index";
    }
}
