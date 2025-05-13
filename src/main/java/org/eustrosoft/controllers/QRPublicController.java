package org.eustrosoft.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.eustrosoft.dtos.QRDto;
import org.eustrosoft.services.QRService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Unallowed to use this endpoint"),
        @ApiResponse(responseCode = "500", description = "Error on server")
})
@RestController
@RequestMapping("/v1/api/unsecured/qrs")
@RequiredArgsConstructor
@Tag(name = "Public QR API")
public class QRPublicController {
    private final QRService service;

    @Operation(summary = "Get public card by Q parameter (available for all users, one of the main parts in the system)")
    @GetMapping
    public QRDto findById(@RequestParam("q") String q) throws JsonProcessingException {
        if (StringUtils.isEmpty(q)) {
            return null;
        }
        return service.getByCodePublic(Long.parseLong(q, 16));
    }

//    @GetMapping("/files/all/download")
//    public void downloadAllPublicFilesZip(@RequestParam("q") String q) throws IOException {
//        service.downloadAllPublicFiles(Long.parseLong(q, 16));
//    }
}
