package org.infrastructure.web.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.domain.usecase.GameResult;
import org.domain.usecase.TennisScoreQuery;
import org.infrastructure.web.request.GameSequenceRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


//todo fix tests

@WebMvcTest(controllers = TennisScoreEndpoint.class)
@ContextConfiguration(classes =Config.class)
class TennisScoreEndpointTest {

    public static final String SEQUENCE = "MNMNMN";
    public static final String SEQUENCE1 = "XYXYXX";
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TennisScoreQuery tennisScoreQuery;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPlayGameEndpoint() throws Exception {
        when(tennisScoreQuery.execute(any())).thenReturn(new GameResult(SEQUENCE1, Collections.emptyList()));
        GameSequenceRequest request = new GameSequenceRequest(SEQUENCE1);

        mockMvc.perform(post("/api/v1/tennis/play")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sequence").value(SEQUENCE1))
                .andExpect(jsonPath("$.scores").isArray());
//                .andExpect(jsonPath("$.scores[0]").value("Player X : 15 / Player Y : 0"));
    }

    @Test
    void testPlayGameByPathEndpoint() throws Exception {
        when(tennisScoreQuery.execute(any())).thenReturn(new GameResult(SEQUENCE, Collections.emptyList()));
        mockMvc.perform(get("/api/v1/tennis/play/MNMNMN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sequence").value("MNMNMN"))
                .andExpect(jsonPath("$.scores").isArray());
    }
}