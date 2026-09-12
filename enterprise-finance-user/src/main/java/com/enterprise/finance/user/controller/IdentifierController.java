package com.enterprise.finance.user.controller;

import com.enterprise.finance.user.api.dto.IdentifierDTO;
import com.enterprise.finance.user.api.dto.IdentifierPageRequest;
import com.enterprise.finance.user.api.dto.IdentifierSaveRequest;
import com.enterprise.finance.common.result.PageResult;
import com.enterprise.finance.common.result.R;
import com.enterprise.finance.user.service.IdentifierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/identifier")
@RequiredArgsConstructor
public class IdentifierController {

    private final IdentifierService identifierService;

    @PostMapping
    public R<IdentifierDTO> create(@Valid @RequestBody IdentifierSaveRequest request) {
        return R.ok(identifierService.create(request));
    }

    @PutMapping("/{id}")
    public R<IdentifierDTO> update(@PathVariable Long id, @Valid @RequestBody IdentifierSaveRequest request) {
        return R.ok(identifierService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        identifierService.delete(id);
        return R.ok(null);
    }

    @GetMapping("/{id}")
    public R<IdentifierDTO> getById(@PathVariable Long id) {
        return R.ok(identifierService.getById(id));
    }

    @GetMapping("/page")
    public R<PageResult<IdentifierDTO>> page(IdentifierPageRequest request) {
        return R.ok(identifierService.page(request));
    }
}