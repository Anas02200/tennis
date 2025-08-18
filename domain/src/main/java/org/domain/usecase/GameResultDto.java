package org.domain.usecase;

import java.util.List;

public record GameResultDto(String sequence, List<String> scores) {}