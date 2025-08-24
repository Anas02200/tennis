package org.domain.usecase;

import org.domain.model.Score;

import java.util.List;

public record GameResult(String sequence, List<Score> scores ) {}