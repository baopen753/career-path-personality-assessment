package org.swd392.seminars.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swd392.seminars.entity.Seminar;
import org.swd392.seminars.exception.ResourceNotFoundException;
import org.swd392.seminars.exception.SeminarTicketException;
import org.swd392.seminars.exception.UnauthorizedException;
import org.swd392.seminars.payload.request.SeminarRequest;
import org.swd392.seminars.payload.response.SeminarResponse;
import org.swd392.seminars.repository.SeminarRepository;
import org.swd392.seminars.service.SeminarService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeminarServiceImpl implements SeminarService {

    private final SeminarRepository seminarRepository;

    @Override
    @Transactional
    public SeminarResponse createSeminar(Integer eventManagerId, SeminarRequest request) {
        log.info("Creating new seminar by event manager ID: {} with request: {}", eventManagerId, request);
        try {
            // Validate required fields
            if (request.getTitle() == null || request.getTitle().trim().isEmpty()) {
                throw new SeminarTicketException("Title is required");
            }
            if (request.getDescription() == null || request.getDescription().trim().isEmpty()) {
                throw new SeminarTicketException("Description is required");
            }
            if (request.getDuration() == null || request.getDuration() <= 0) {
                throw new SeminarTicketException("Duration must be greater than 0");
            }
            if (request.getPrice() == null || request.getPrice() < 0) {
                throw new SeminarTicketException("Price cannot be negative");
            }
            if (request.getSlot() == null || request.getSlot() <= 0) {
                throw new SeminarTicketException("Slot must be greater than 0");
            }

            // Create and save seminar
            Seminar seminar = new Seminar();
            try {
                seminar.setTitle(request.getTitle().trim());
                seminar.setDescription(request.getDescription().trim());
                seminar.setDuration(request.getDuration());
                seminar.setMeetingUrl(request.getMeetingUrl());
                seminar.setFormUrl(request.getFormUrl());
                seminar.setSlot(request.getSlot());
                seminar.setCreateBy(eventManagerId);
                seminar.setPrice(request.getPrice());
                seminar.setImageUrl(request.getImageUrl());
                seminar.setStatus(Seminar.Status.PENDING);
                seminar.setStatusApprove(Seminar.StatusApprove.PENDING);

                log.info("Saving new seminar with title: {}", request.getTitle());
                Seminar savedSeminar = seminarRepository.save(seminar);
                log.info("Successfully created seminar with ID: {}", savedSeminar.getId());

                return mapToResponse(savedSeminar);
            } catch (Exception e) {
                log.error("Error setting seminar fields: {}", e.getMessage(), e);
                throw new SeminarTicketException("Error creating seminar: " + e.getMessage());
            }
        } catch (SeminarTicketException e) {
            log.error("Validation error creating seminar: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating seminar: {}", e.getMessage(), e);
            throw new SeminarTicketException("Failed to create seminar: " + e.getMessage());
        }
    }

    @Override
    public SeminarResponse updateSeminar(Integer eventManagerId, Integer seminarId, SeminarRequest request) {
        log.info("Updating seminar ID: {} by event manager ID: {}", seminarId, eventManagerId);
        
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new ResourceNotFoundException("Seminar not found with ID: " + seminarId));

        if (!seminar.getCreateBy().equals(eventManagerId)) {
            throw new UnauthorizedException("Not authorized to update this seminar");
        }

        if (seminar.getStatusApprove() != Seminar.StatusApprove.PENDING) {
            throw new SeminarTicketException("Can only update pending seminars");
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
        log.info("Successfully updated seminar ID: {}", seminarId);
        return mapToResponse(updatedSeminar);
    }

    @Override
    public void deleteSeminar(Integer eventManagerId, Integer seminarId) {
        log.info("Deleting seminar ID: {} by event manager ID: {}", seminarId, eventManagerId);
        
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new ResourceNotFoundException("Seminar not found with ID: " + seminarId));

        if (!seminar.getCreateBy().equals(eventManagerId)) {
            throw new UnauthorizedException("Not authorized to delete this seminar");
        }

        if (seminar.getStatusApprove() != Seminar.StatusApprove.PENDING) {
            throw new SeminarTicketException("Can only delete pending seminars");
        }

        seminarRepository.delete(seminar);
        log.info("Successfully deleted seminar ID: {}", seminarId);
    }

    @Override
    public SeminarResponse getSeminar(Integer seminarId) {
        log.info("Getting seminar with ID: {}", seminarId);
        
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new ResourceNotFoundException("Seminar not found with ID: " + seminarId));
        return mapToResponse(seminar);
    }

    @Override
    public List<SeminarResponse> getSeminarsByEventManager(Integer eventManagerId) {
        log.info("Getting all seminars for event manager ID: {}", eventManagerId);
        
        return seminarRepository.findByCreateBy(eventManagerId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeminarResponse> getSeminarsByStatus(Seminar.Status status) {
        log.info("Getting all seminars with status: {}", status);
        
        return seminarRepository.findByStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SeminarResponse approveSeminar(Integer adminId, Integer seminarId, Seminar.StatusApprove status) {
        log.info("Admin ID: {} approving seminar ID: {} with status: {}", adminId, seminarId, status);
        
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new ResourceNotFoundException("Seminar not found with ID: " + seminarId));

        if (seminar.getStatusApprove() != Seminar.StatusApprove.PENDING) {
            throw new SeminarTicketException("Can only approve/reject pending seminars");
        }

        seminar.setStatusApprove(status);
        
        if (status == Seminar.StatusApprove.APPROVED) {
            seminar.setStatus(Seminar.Status.PENDING); // Event manager will manage the status after approval
        } else if (status == Seminar.StatusApprove.REJECTED) {
            seminar.setStatus(Seminar.Status.CANCELLED);
        }

        Seminar updatedSeminar = seminarRepository.save(seminar);
        log.info("Successfully updated seminar ID: {} status to: {}", seminarId, status);
        return mapToResponse(updatedSeminar);
    }

    @Override
    public SeminarResponse updateStatus(Integer eventManagerId, Integer seminarId, Seminar.Status status) {
        log.info("Event manager ID: {} updating seminar ID: {} status to: {}", eventManagerId, seminarId, status);
        
        Seminar seminar = seminarRepository.findById(seminarId)
                .orElseThrow(() -> new ResourceNotFoundException("Seminar not found with ID: " + seminarId));

        if (!seminar.getCreateBy().equals(eventManagerId)) {
            throw new UnauthorizedException("Not authorized to update this seminar");
        }

        if (seminar.getStatusApprove() != Seminar.StatusApprove.APPROVED) {
            throw new SeminarTicketException("Can only update status of approved seminars");
        }

        seminar.setStatus(status);
        Seminar updatedSeminar = seminarRepository.save(seminar);
        log.info("Successfully updated seminar ID: {} status to: {}", seminarId, status);
        return mapToResponse(updatedSeminar);
    }

    @Override
    public List<SeminarResponse> getPendingSeminars(Seminar.StatusApprove statusApprove) {
        log.info("Getting seminars with status approve: {}", statusApprove);
        try {
            List<Seminar> seminars = seminarRepository.findByStatusApprove(statusApprove);
            return seminars.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting seminars with status approve {}: {}", statusApprove, e.getMessage());
            throw new SeminarTicketException("Failed to get seminars: " + e.getMessage());
        }
    }

    @Override
    public List<SeminarResponse> getApprovedSeminars() {
        log.info("Getting all approved seminars");
        try {
            List<Seminar> seminars = seminarRepository.findByStatusApprove(Seminar.StatusApprove.APPROVED);
            return seminars.stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting approved seminars: {}", e.getMessage());
            throw new SeminarTicketException("Failed to get approved seminars: " + e.getMessage());
        }
    }

    private SeminarResponse mapToResponse(Seminar seminar) {
        SeminarResponse response = new SeminarResponse();
        response.setId(seminar.getId());
        response.setTitle(seminar.getTitle());
        response.setDescription(seminar.getDescription());
        response.setStatus(seminar.getStatus());
        response.setStatusApprove(seminar.getStatusApprove());
        response.setDuration(seminar.getDuration());
        response.setMeetingUrl(seminar.getMeetingUrl());
        response.setFormUrl(seminar.getFormUrl());
        response.setSlot(seminar.getSlot());
        response.setCreateBy(seminar.getCreateBy());
        response.setPrice(seminar.getPrice());
        response.setImageUrl(seminar.getImageUrl());
        return response;
    }
} 