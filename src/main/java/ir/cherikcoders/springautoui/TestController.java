package ir.cherikcoders.springautoui;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping(value = "/method1/{t1}")
    public String test1(@PathVariable Integer t1, @RequestBody String s1){
        return "hi";
    }

    @RequestMapping(value = "/method2/{t1}",method = RequestMethod.GET)
    public String test2(@PathVariable Integer t1, @RequestBody String s1){
        return "hi";
    }


    @RequestMapping(value = "/method3/{t1}",method = RequestMethod.GET)
    public String test3(@PathVariable Integer t1, @RequestBody String s1,@RequestParam(name = "aaa") String bbb){
        return "hi";
    }


}
