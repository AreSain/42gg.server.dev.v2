package com.gg.server.admin.penalty.controller;

import com.gg.server.admin.penalty.dto.PenaltyListResponseDto;
import com.gg.server.admin.penalty.dto.PenaltyRequestDto;
import com.gg.server.admin.penalty.service.PenaltyService;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/pingpong/admin/")
public class PenaltyController {
    private final PenaltyService penaltyService;

    @PostMapping("users/{intraId}/penalty")
    public ResponseEntity givePenaltyToUser(@PathVariable String intraId, @RequestBody @Valid PenaltyRequestDto requestDto) {
        penaltyService.givePenalty(intraId, requestDto.getPenaltyTime(), requestDto.getReason());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("penalty/users")
    public PenaltyListResponseDto getAllPenaltyUser(String q, @RequestParam @Min(1) Integer page,
                                                    @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        if (q == null)
            return penaltyService.getAllPenaltyUser(page - 1, size);
        return penaltyService.searchPenaltyUser(q, page - 1, size);
    }

    @DeleteMapping("penalty/users/{intraId}")
    public ResponseEntity releasePenaltyUser(@PathVariable @Size(max = 30) String intraId) {
        penaltyService.releasePenaltyUser(intraId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}