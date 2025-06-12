package org.swd392.seminars.service;

import org.swd392.seminars.payload.request.SeminarRequest;
import org.swd392.seminars.payload.response.SeminarResponse;

import java.util.List;

public interface SeminarService {
    SeminarResponse createSeminar(Integer eventManagerId, SeminarRequest request);
    SeminarResponse updateSeminar(Integer eventManagerId, Integer seminarId, SeminarRequest request);
    void deleteSeminar(Integer eventManagerId, Integer seminarId);
    SeminarResponse getSeminar(Integer seminarId);
    List<SeminarResponse> getSeminarsByEventManager(Integer eventManagerId);
    List<SeminarResponse> getSeminarsByStatus(String status);
    SeminarResponse approveSeminar(Integer adminId, Integer seminarId, String status);
    SeminarResponse updateVideoLink(Integer eventManagerId, Integer seminarId, String videoUrl);
}
