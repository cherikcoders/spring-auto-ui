package ir.cherikcoders.springautoui;

import ir.cherikcoders.springautoui.util.detection.model.MethodInputModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping(value = "/method1/{t1}")
    public List<String> test1(@PathVariable Integer t1, @RequestBody String s1){
        return List.of("hi");
    }

    @RequestMapping(value = "/method2/{t1}",method = RequestMethod.GET)
    public String test2(@PathVariable Integer t1, @RequestBody MethodInputModel s1){
        return "hi";
    }


    @RequestMapping(value = "/method3/{t1}",method = RequestMethod.GET)
    public MethodInputModel test3(@PathVariable Integer t1, @RequestBody String s1,@RequestParam(name = "aaa") String bbb){
        return new MethodInputModel();
    }


}
