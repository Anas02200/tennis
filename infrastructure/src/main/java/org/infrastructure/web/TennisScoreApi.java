package org.infrastructure.web;
//todo add mapper between domain and api // add exception handler

import org.domain.usecase.GameResultDto;
import org.domain.usecase.TennisScoreQuery;
import org.infrastructure.web.request.GameSequenceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tennis")
public class TennisScoreApi {

    private final TennisScoreQuery tennisScoreQuery;

    public TennisScoreApi(TennisScoreQuery tennisScoreQuery) {
        this.tennisScoreQuery = tennisScoreQuery;
    }

    @PostMapping("/play")
    public ResponseEntity<GameResultDto> playGame(@RequestBody GameSequenceDto request) {
        try {
            GameResultDto result = tennisScoreQuery.execute(request.sequence());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/play/{sequence}")
    public ResponseEntity<GameResultDto> playGamePath(@PathVariable("sequence") String sequence) {
        try {
            GameResultDto result = tennisScoreQuery.execute(sequence);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
