package org.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.domain.usecase.GameResultDto;
import org.domain.usecase.TennisScoreQuery;
import org.infrastructure.web.request.GameSequenceDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


//todo fix tests

@WebMvcTest(TennisScoreApi.class)
class TennisScoreApiTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private TennisScoreQuery tennisScoreQuery;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testPlayGameEndpoint() throws Exception {
        when(tennisScoreQuery.execute(any())).thenReturn(new GameResultDto(null, null));
        GameSequenceDto request = new GameSequenceDto("XYXYXX");

        mockMvc.perform(post("/api/v1/tennis/play")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sequence").value("XYXYXX"))
                .andExpect(jsonPath("$.scores").isArray())
                .andExpect(jsonPath("$.scores[0]").value("Player X : 15 / Player Y : 0"));
    }

    @Test
    void testPlayGameByPathEndpoint() throws Exception {
        when(tennisScoreQuery.execute(any())).thenReturn(new GameResultDto(null, null));
        mockMvc.perform(get("/api/v1/tennis/play/MNMNMN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sequence").value("MNMNMN"))
                .andExpect(jsonPath("$.scores").isArray());
    }
}