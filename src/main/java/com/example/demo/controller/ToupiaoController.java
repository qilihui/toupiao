package com.example.demo.controller;

import com.example.demo.config.R;
import com.example.demo.entity.User;
import com.example.demo.service.ToupiaoService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qilihui
 * @date 2021/6/15 22:05
 */
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class ToupiaoController {

    private ToupiaoService toupiaoService;

    @PostMapping("/getInfo")
    public R getInfo(@RequestParam("rid") Integer rid, @RequestParam("id") Integer id) {
        User user = toupiaoService.getCurrentTicket(rid, id);
        return R.success(user);
    }


}
