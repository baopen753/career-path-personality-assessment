package org.swd392.seminars.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.swd392.seminars.payload.request.SeminarTicketRequest;
import org.swd392.seminars.payload.response.SeminarTicketResponse;
import org.swd392.seminars.entity.Seminar;
import org.swd392.seminars.entity.SeminarTicket;
import org.swd392.seminars.exception.ResourceNotFoundException;
import org.swd392.seminars.exception.SeminarTicketException;
import org.swd392.seminars.repository.SeminarRepository;
import org.swd392.seminars.repository.SeminarTicketRepository;
import org.swd392.seminars.service.SeminarTicketService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeminarTicketServiceImpl implements SeminarTicketService {

    private final SeminarTicketRepository seminarTicketRepository;
    private final SeminarRepository seminarRepository;

    @Override
    @Transactional
    public SeminarTicketResponse bookTicket(SeminarTicketRequest request) {
        log.info("Starting to book ticket for seminar ID: {}, user ID: {}", request.getSeminarId(), request.getUserProfileId());

        // Validate seminar exists
        Seminar seminar = seminarRepository.findById(request.getSeminarId())
                .orElseThrow(() -> new ResourceNotFoundException("Seminar not found with ID: " + request.getSeminarId()));

        // Validate seminar status
        if (seminar.getStatusApprove() != Seminar.StatusApprove.APPROVED) {
            throw new SeminarTicketException("Cannot book ticket for unapproved seminar");
        }

        if (seminar.getStatus() != Seminar.Status.ONGOING) {
            throw new SeminarTicketException("Cannot book ticket for seminar with status: " + seminar.getStatus() + ". Tickets can only be booked when seminar status is ONGOING");
        }

        // Validate user ID
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        // Check if user already has an active ticket
        if (hasActiveTicket(request.getSeminarId(), request.getUserId())) {
            throw new SeminarTicketException("User already has an active ticket for this seminar");
        }

        // Check if seminar is fully booked
        long bookedTickets = getBookedTicketsCount(request.getSeminarId());
            if (bookedTickets >= seminar.getSlot()) {
            throw new SeminarTicketException("Seminar is fully booked");
        }

        // Validate starting time
        LocalDate startingTime = request.getStartingTime();
        if (startingTime == null) {
            throw new IllegalArgumentException("Starting time cannot be null");
        }
        if (startingTime.isBefore(LocalDate.now())) {
            throw new SeminarTicketException("Starting time cannot be in the past");
        }

        // Create new ticket
        SeminarTicket ticket = new SeminarTicket();
        ticket.setSeminar(seminar);
        ticket.setUserId(request.getUserId());
        ticket.setDescription(request.getDescription());
        ticket.setStartingTime(startingTime.atStartOfDay()); // Convert LocalDate to LocalDateTime
        ticket.setBookingTime(LocalDateTime.now());
        ticket.setStatus(true);


        SeminarTicket savedTicket = seminarTicketRepository.save(ticket);
        log.info("Saving new ticket: {}", ticket);
        return mapToResponse(savedTicket);
    }

    @Override
    @Transactional
    public void cancelTicket(Integer userId, Integer ticketId) {
        log.info("Cancelling ticket ID: {} for user ID: {}", ticketId, userId);
        
        SeminarTicket ticket = seminarTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));

        if (!ticket.getUserId().equals(userId)) {
            throw new SeminarTicketException("Not authorized to cancel this ticket");
        }

        // Check if seminar is still bookable
        Seminar seminar = ticket.getSeminar();
        if (seminar.getStatus() == Seminar.Status.COMPLETED || seminar.getStatus() == Seminar.Status.CANCELLED) {
            throw new SeminarTicketException("Cannot cancel ticket for seminar with status: " + seminar.getStatus());
        }

        ticket.setStatus(false);
        seminarTicketRepository.save(ticket);
        log.info("Successfully cancelled ticket ID: {}", ticketId);
    }

    @Override
    public List<SeminarTicketResponse> getTicketsBySeminar(Integer seminarId) {
        return seminarTicketRepository.findBySeminarId(seminarId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeminarTicketResponse> getTicketsByUser(Integer userId) {
        return seminarTicketRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SeminarTicketResponse getTicket(Integer ticketId) {
        SeminarTicket ticket = seminarTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));
        return mapToResponse(ticket);
    }

    @Override
    public boolean hasActiveTicket(Integer seminarId, Integer userId) {
        return seminarTicketRepository.existsBySeminarIdAndUserId(seminarId, userId);
    }

    @Override
    public long getBookedTicketsCount(Integer seminarId) {
        return seminarTicketRepository.countBySeminarIdAndStatusTrue(seminarId);
    }

    @Override
    @Transactional
    public void deleteBookedTicket(Integer seminarId, Integer userId) {
        SeminarTicket seminarTicket = seminarTicketRepository.findBySeminarIdAndUserId(seminarId,userId)
                        .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + seminarId + " and UserProfileId: " + userId));
        seminarTicketRepository.delete(seminarTicket);
    }

    private SeminarTicketResponse mapToResponse(SeminarTicket ticket) {
        SeminarTicketResponse response = new SeminarTicketResponse();
        response.setId(ticket.getId());
        response.setSeminarId(ticket.getSeminar().getId());
        response.setUserId(ticket.getUserId());
        response.setDescription(ticket.getDescription());
        response.setStartingTime(ticket.getStartingTime());
        response.setBookingTime(ticket.getBookingTime());
        response.setStatus(ticket.isStatus());
        return response;
    }
} 