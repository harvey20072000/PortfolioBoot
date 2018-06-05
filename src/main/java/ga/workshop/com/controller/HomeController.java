/*
* @(#)SmartCloudController.java
*
* Copyright (c) 2016 GEOSAT. All rights reserved.
*
* Description : �����
*
* Modify History:
*  v1.00, 2016/06/28
*	1) First release
*/

package ga.workshop.com.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ga.workshop.com.util.Const;

@Slf4j
@Controller
public class HomeController {
	
	@RequestMapping(value = "/")
	@ResponseBody
    public String index() {
        return "Hello, Portfolio Boot !\n "+Const.FILE_ROOT_PATH;
    }
	
	@RequestMapping(value = "/analyzer")
    public String analyzer() {
        return "analyzer";
    }
	
	@RequestMapping(value = "/tracker")
    public String tracker() {
        return "tracker";
    }
	
}
