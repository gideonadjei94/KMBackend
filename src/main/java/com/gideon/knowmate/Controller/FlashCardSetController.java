package com.gideon.knowmate.Controller;


import com.gideon.knowmate.Dto.FlashCardSetDto;
import com.gideon.knowmate.Enum.SubjectDomain;
import com.gideon.knowmate.Requests.CreateFlashCardSetRequest;
import com.gideon.knowmate.Requests.UpdateFlashCardSetRequest;
import com.gideon.knowmate.Response.ApiResponse;
import com.gideon.knowmate.Service.FlashCardService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/flashcard-sets")
@SecurityRequirement(name = "bearerAuth")
public class FlashCardSetController {

    private final FlashCardService service;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addFlashCardSet(@RequestBody CreateFlashCardSetRequest request){
        String setId = service.createFlashCardSet(request);
        return ResponseEntity
                .status(CREATED)
                .body(new ApiResponse("FlashCard set Created Successfully", setId));
    }


    @GetMapping("/popular-flashcard-sets")
    public ResponseEntity<ApiResponse> getPopularFlashCards(){
        List<FlashCardSetDto> response = service.getPopularFlashCards();
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Success", response));
    }


    @GetMapping("/get-flashcard-sets-by-subject")
    public ResponseEntity<ApiResponse> getFlashCardsBySubject(@RequestParam("subject") SubjectDomain subject){
        List<FlashCardSetDto> response = service.getFlashCardsBySubject(subject);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Success", response));
    }


    @GetMapping("/get-flashcard-sets-by-course")
    public ResponseEntity<ApiResponse> getFlashCardsByCourse(@RequestParam("course") String course){
        List<FlashCardSetDto> response = service.getFlashCardByCourse(course);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Success", response));
    }


    @GetMapping("/get-user-flashcard-sets")
    public ResponseEntity<ApiResponse> getUserFlashCards(@RequestParam("user") String userId){
        List<FlashCardSetDto> response = service.getUserFlashCards(userId);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Success", response));
    }


    @GetMapping("/get-flashcard-set-by-id")
    public ResponseEntity<ApiResponse> getFlashCardById(@RequestParam("flashcardId") String cardId){
        FlashCardSetDto response = service.getFlashCardSet(cardId);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Success", response));
    }


    @PutMapping("/update/set/{setId}")
    public ResponseEntity<ApiResponse> updateFlashCardSet(@RequestBody UpdateFlashCardSetRequest request, @PathVariable String setId){
        FlashCardSetDto response = service.updateFlashCardSet(setId, request);
        return ResponseEntity
                .status(CREATED)
                .body(new ApiResponse("Flashcard Updated Successfully", response));
    }


    @DeleteMapping("/delete-set")
    public ResponseEntity<ApiResponse> deleteFlashCardSet(@RequestParam("setId") String setId){
        service.deleteFlashCardSet(setId);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse("Flashcard Deleted Successfully", null));
    }

}
