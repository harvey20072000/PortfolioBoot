package ga.workshop.com.controller;


import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ErrorController {

//    public static final Logger LOG = Logger.getLogger(ErrorController.class);

    @RequestMapping(value = "/errorController")
    public ModelAndView handleError(HttpServletRequest request,
            @RequestAttribute("exception") Throwable th) {
        ModelAndView mv = null;
        mv = new ModelAndView("appAjaxBadRequest");
    	mv.addObject("errMsg", th.toString());
    	String details = "";
    	for(StackTraceElement element : th.getStackTrace()){
    		details += element.toString() + "｜";
    	}
    	mv.addObject("errDetail", details);
//        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
//            if (isBusinessException(th)) {
//                mv = new ModelAndView("appAjaxBadRequest");
//                mv.setStatus(BAD_REQUEST);
//            } else {
//                log.error("Internal server error while processing AJAX call.", th);
//                mv = new ModelAndView("appAjaxInternalServerError");
//                mv.setStatus(INTERNAL_SERVER_ERROR);
//            }
//            mv.addObject("message", getUserFriendlyErrorMessage(th).replaceAll("\r?\n", "<br/>"));
//        } else {
//            log.error("Cannot process http request.", th);
//            mv = new ModelAndView("appErrorPage");
//            mv.addObject("exeption", th);
//        }

        return mv;
    }
}
