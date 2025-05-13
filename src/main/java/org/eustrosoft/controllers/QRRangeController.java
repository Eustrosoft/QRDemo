package org.eustrosoft.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.services.QRRangeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Unallowed to use this endpoint"),
        @ApiResponse(responseCode = "500", description = "Error on server")
})
@RestController
@RequestMapping("/v1/api/secured/ranges")
@RequiredArgsConstructor
@Tag(name = "QR Ranges API")
public class QRRangeController {
    private final QRRangeService service;

    @Operation(summary = "Get all available ranges to create QRs for current user")
    @GetMapping
    public Collection<QRRange> getMyRanges() throws IllegalAccessException {
        return service.getMyRanges();
    }
}
