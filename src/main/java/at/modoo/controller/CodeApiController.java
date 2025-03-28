package at.modoo.controller;

import at.modoo.core.hierarchy.HierarchicalFactory;
import at.modoo.entity.Code;
import at.modoo.service.CodeService;
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
    public ResponseEntity<List<Code>> getCodes() {
        List<Code> codes = codeService.get();
        return ResponseEntity.ok(HierarchicalFactory.build(codes));
    }

    @GetMapping("/{name}")
    public ResponseEntity<List<Code>> getCode(@PathVariable("name") String name) {
        List<Code> codes = codeService.findByName(name);
        return ResponseEntity.ok(HierarchicalFactory.build(codes));
    }

}
