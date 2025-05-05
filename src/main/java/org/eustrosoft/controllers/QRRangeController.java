package org.eustrosoft.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.eustrosoft.entitites.QRRange;
import org.eustrosoft.services.QRRangeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@ApiResponses(value = {
        @ApiResponse(code = 200, message = "Success"),
        @ApiResponse(code = 401, message = "Unauthorized"),
        @ApiResponse(code = 403, message = "Unallowed to use this endpoint"),
        @ApiResponse(code = 500, message = "Error on server")
})
@RestController
@RequestMapping("/v1/api/secured/ranges")
@RequiredArgsConstructor
public class QRRangeController {
    private final QRRangeService service;

    @ApiOperation(value = "Get all available ranges to create QRs for current user")
    @GetMapping
    public Collection<QRRange> getMyRanges() throws IllegalAccessException {
        return service.getMyRanges();
    }
}
