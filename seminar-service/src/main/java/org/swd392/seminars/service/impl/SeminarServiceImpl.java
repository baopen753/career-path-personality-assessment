package org.swd392.seminars.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.swd392.seminars.payload.request.SeminarRequest;
import org.swd392.seminars.payload.response.SeminarResponse;
import org.swd392.seminars.entity.Seminar;
import org.swd392.seminars.exception.ResourceNotFoundException;
import org.swd392.seminars.exception.UnauthorizedException;
import org.swd392.seminars.repository.SeminarRepository;
import org.swd392.seminars.service.SeminarService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeminarServiceImpl implements SeminarService {

    private final SeminarRepository seminarRepository;

    @Override
    public SeminarResponse createSeminar(Integer eventManagerId, SeminarRequest request) {
        Seminar seminar = new Seminar();
        seminar.setTitle(request.getTitle());
        seminar.setDescription(request.getDescription());
        seminar.setDuration(request.getDuration());
        seminar.setMeetingUrl(request.getMeetingUrl());
        seminar.setFormUrl(request.getFormUrl());
        seminar.setSlot(request.getSlot());
        seminar.setCreatedBy(eventManagerId);
        seminar.setStatus("PENDING");
        seminar.setPrice(request.getPrice());
        seminar.setImageUrl(request.getImageUrl());

        Seminar savedSeminar = seminarRepository.save(seminar);
        return mapToResponse(savedSeminar);
    }

    @Override
    public SeminarResponse updateSeminar(Integer eventManagerId, Integer seminarId, SeminarRequest request) {
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new ResourceNotFoundException("Seminar not found"));

        if (!seminar.getCreatedBy().equals(eventManagerId)) {
            throw new UnauthorizedException("Not authorized to update this seminar");
        }

        if (!"PENDING".equals(seminar.getStatus())) {
            throw new UnauthorizedException("Can only update pending seminars");
        }

        seminar.setTitle(request.getTitle());
        seminar.setDescription(request.getDescription());
        seminar.setDuration(request.getDuration());
        seminar.setMeetingUrl(request.getMeetingUrl());
        seminar.setFormUrl(request.getFormUrl());
        seminar.setSlot(request.getSlot());
        seminar.setPrice(request.getPrice());
        seminar.setImageUrl(request.getImageUrl());

        Seminar updatedSeminar = seminarRepository.save(seminar);
        return mapToResponse(updatedSeminar);
    }

    @Override
    public void deleteSeminar(Integer eventManagerId, Integer seminarId) {
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new ResourceNotFoundException("Seminar not found"));

        if (!seminar.getCreatedBy().equals(eventManagerId)) {
            throw new UnauthorizedException("Not authorized to delete this seminar");
        }

        if (!"PENDING".equals(seminar.getStatus())) {
            throw new UnauthorizedException("Can only delete pending seminars");
        }

        seminarRepository.delete(seminar);
    }

    @Override
    public SeminarResponse getSeminar(Integer seminarId) {
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new ResourceNotFoundException("Seminar not found"));
        return mapToResponse(seminar);
    }

    @Override
    public List<SeminarResponse> getSeminarsByEventManager(Integer eventManagerId) {
        return seminarRepository.findByCreatedBy(eventManagerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeminarResponse> getSeminarsByStatus(String status) {
        return seminarRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SeminarResponse approveSeminar(Integer adminId, Integer seminarId, String status) {
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new ResourceNotFoundException("Seminar not found"));

        if (!"PENDING".equals(seminar.getStatus())) {
            throw new UnauthorizedException("Can only approve/reject pending seminars");
        }

        seminar.setStatus(status); // APPROVED or REJECTED
        Seminar updatedSeminar = seminarRepository.save(seminar);
        return mapToResponse(updatedSeminar);
    }

    @Override
    public SeminarResponse updateVideoLink(Integer eventManagerId, Integer seminarId, String videoUrl) {
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new ResourceNotFoundException("Seminar not found"));

        if (!seminar.getCreatedBy().equals(eventManagerId)) {
            throw new UnauthorizedException("Not authorized to update this seminar");
        }

        if (!"APPROVED".equals(seminar.getStatus())) {
            throw new UnauthorizedException("Can only add video to approved seminars");
        }

        seminar.setVideoUrl(videoUrl);
        seminar.setStatus("COMPLETED");
        return mapToResponse(seminarRepository.save(seminar));
    }

    private SeminarResponse mapToResponse(Seminar seminar) {
        SeminarResponse response = new SeminarResponse();
        response.setId(seminar.getId());
        response.setTitle(seminar.getTitle());
        response.setDescription(seminar.getDescription());
        response.setStatus(seminar.getStatus());
        response.setDuration(seminar.getDuration());
        response.setMeetingUrl(seminar.getMeetingUrl());
        response.setFormUrl(seminar.getFormUrl());
        response.setVideoUrl(seminar.getVideoUrl());
        response.setSlot(seminar.getSlot());
        response.setCreatedBy(seminar.getCreatedBy());
        response.setPrice(seminar.getPrice());
        response.setImageUrl(seminar.getImageUrl());
        return response;
    }
} 