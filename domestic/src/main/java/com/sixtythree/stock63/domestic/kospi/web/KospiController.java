package com.sixtythree.stock63.domestic.kospi.web;

import com.sixtythree.stock63.domestic.kospi.entity.KospiItem;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.sixtythree.stock63.domestic.kospi.service.KospiService;

import java.util.List;

@Tag(name = "kospi", description = "국내주식 코스피")
@Controller
@RequestMapping("/domestic/kospi")
@RequiredArgsConstructor
public class KospiController {

    private final KospiService kospiService;

    @ResponseBody
    @GetMapping("list")
    public ResponseEntity<List<KospiItem>> rankList(
            @RequestParam(name ="period",required = false) int period,
            @RequestParam(name="gradient",required = false) int gradient
            ) {
        return kospiService.stockList(period, gradient);
    }

}
