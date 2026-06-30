package kr.me.seesaw.controller;

import kr.me.seesaw.core.hierarchy.HierarchicalFactory;
import kr.me.seesaw.response.CodeResponse;
import kr.me.seesaw.service.CodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/codes")
public class CodeApiController {

    private final CodeService codeService;

    @GetMapping
    public ResponseEntity<List<CodeResponse>> getCodes() {
        List<CodeResponse> codes = codeService.getAllCodes();
        return ResponseEntity.ok(HierarchicalFactory.build(codes));
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<CodeResponse>> getCode(@PathVariable("name") String name) {
        List<CodeResponse> codes = codeService.getAllCodesByName(name);
        return ResponseEntity.ok(HierarchicalFactory.build(codes));
    }

}
