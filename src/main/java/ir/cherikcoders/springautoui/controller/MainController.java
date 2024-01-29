package ir.cherikcoders.springautoui.controller;

import ir.cherikcoders.springautoui.util.annotaions.ExcludeFromUI;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@ExcludeFromUI
public class MainController {

    @GetMapping({"/","/home"})
    public String home(){
        return "home";
    }

}
