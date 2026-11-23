package com.varun.link_shortner.controllers;

import com.varun.link_shortner.dto.ShortenRequest;
import com.varun.link_shortner.dto.ShortenResponse;
import com.varun.link_shortner.dto.UpdateRequest;
import com.varun.link_shortner.services.UrlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.nio.channels.ScatteringByteChannel;

@RestController
@RequestMapping("/api/shortner")
@Tag(name = "URL API's")
@RequiredArgsConstructor
public class UrlController {


    private final UrlService us;

    /**
     * Shortens a given String, preferably url into a short reference to it.
     *
     * @param request takes property url:String
     * @return short url
     */
    @Operation(summary = "Shorten url", description = "Returns a short reference to url" , responses =  {
            @ApiResponse(responseCode = "200", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Bad Request" )
    })
    @PostMapping()
    public ResponseEntity<ShortenResponse> shorten(@Valid @RequestBody ShortenRequest request) {
        return ResponseEntity.ok(us.shorten(request.getUrl()));
    }


    @Operation(summary = "Redirects", description = "Retrieves and redirects to the original url content" ,  responses = {
            @ApiResponse(
                    responseCode = "302",
                    description = "Redirect",
                    content = @Content(
                            schema = @Schema(implementation = RedirectView.class),
                            examples = @ExampleObject(value = "{\"url\": \"https://example.com/dashboard\"}")
                    )
            )    ,

            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request",
                    content = @Content(
                            schema = @Schema(implementation = RedirectView.class),
                            examples = @ExampleObject(value = "Short code not found")
                    )
            )
    })
    @GetMapping("/{shortCode}")
    public RedirectView redirect(@PathVariable String shortCode) {

        String redirectString = us.redirect(shortCode);

        return new RedirectView(redirectString);
    }



    @Operation(summary = "Updates Url", description = "updates a url to another based on shortCode." ,  responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successful"
            ) ,

            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request"
            )
    })
    @PatchMapping()
    public ResponseEntity<UpdateRequest> updateUrl(@Valid @RequestBody UpdateRequest req){
        return ResponseEntity.ok().body( us.updateUrl(req));
    }


    @Operation(summary = "Delete Url", description = "Deletes a url based on shortCode." ,  responses = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Successful"
            ) ,

            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request"
            )
    })
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Null> deleteUrl(@PathVariable String shortCode){
        us.deleteUrl(shortCode);
        return ResponseEntity.noContent().build();
    }

}
