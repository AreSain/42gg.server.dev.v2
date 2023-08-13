package com.gg.server.domain.item.controller;

import com.gg.server.domain.item.dto.ItemStoreListResponseDto;
import com.gg.server.domain.item.dto.PurchaseItemRequestDto;
import com.gg.server.domain.item.service.ItemService;
import com.gg.server.domain.user.dto.UserDto;
import com.gg.server.global.utils.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/store")
    public ItemStoreListResponseDto getAllItems() {
        return itemService.getAllItems();
    }

    @PostMapping("/purchases/{itemId}")
    public ResponseEntity<Void> purchaseItem(@PathVariable Long itemId,
                                             @Parameter(hidden = true) @Login UserDto userDto,
                                             @RequestBody PurchaseItemRequestDto requestDto) {
        itemService.purchaseItem(itemId, userDto, requestDto);
        return ResponseEntity.ok().build();
    }
}
//    @PostMapping("/purchases/{itemId}")
//    public ResponseEntity<Void> purchaseItem(@PathVariable Long itemId,
//                                             @Parameter(hidden = true) @Login UserDto userDto,
//                                             @RequestBody PurchaseItemRequestDto requestDto) {
//        itemService.purchaseItem(itemId, userDto, requestDto);
//        return ResponseEntity.ok().build();
//    }
//