package org.infrastructure.web.endpoints;

import org.domain.usecase.TennisScoreQuery;
import org.infrastructure.web.request.GameSequenceRequest;
import org.infrastructure.web.response.GameResultResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tennis")
public class TennisScoreEndpoint {

    private final TennisScoreQuery tennisScoreQuery;

    public TennisScoreEndpoint(TennisScoreQuery tennisScoreQuery) {
        this.tennisScoreQuery = tennisScoreQuery;
    }

    @PostMapping("/play")
    public ResponseEntity<GameResultResponse> playGame(@RequestBody GameSequenceRequest request) {
        try {
            GameResultResponse result = GameResultResponse.of(tennisScoreQuery.execute(request.sequence()));
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/play/{sequence}")
    public ResponseEntity<GameResultResponse> playGamePath(@PathVariable("sequence") String sequence) {
        try {
            GameResultResponse result = GameResultResponse.of(tennisScoreQuery.execute(sequence));
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
