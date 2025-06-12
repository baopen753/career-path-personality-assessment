package org.swd392.seminars.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.swd392.seminars.payload.request.SeminarTicketRequest;
import org.swd392.seminars.payload.response.SeminarTicketResponse;
import org.swd392.seminars.entity.Seminar;
import org.swd392.seminars.entity.SeminarTicket;
import org.swd392.seminars.exception.ResourceNotFoundException;
import org.swd392.seminars.exception.UnauthorizedException;
import org.swd392.seminars.repository.SeminarRepository;
import org.swd392.seminars.repository.SeminarTicketRepository;
import org.swd392.seminars.service.SeminarTicketService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeminarTicketServiceImpl implements SeminarTicketService {

    private final SeminarTicketRepository seminarTicketRepository;
    private final SeminarRepository seminarRepository;

    @Override
    public SeminarTicketResponse bookTicket(SeminarTicketRequest request) {
        Seminar seminar = seminarRepository.findById(request.getSeminarId())
                .orElseThrow(() -> new ResourceNotFoundException("Seminar not found"));

        if (hasActiveTicket(request.getSeminarId(), request.getUserProfileId())) {
            throw new UnauthorizedException("User already has an active ticket for this seminar");
        }

        long bookedTickets = getBookedTicketsCount(request.getSeminarId());
        if (bookedTickets >= seminar.getSlot()) {
            throw new UnauthorizedException("Seminar is fully booked");
        }

        SeminarTicket ticket = new SeminarTicket();
        ticket.setSeminar(seminar);
        ticket.setUserProfileId(request.getUserProfileId());
        ticket.setBookingTime(LocalDateTime.now());

        SeminarTicket savedTicket = seminarTicketRepository.save(ticket);
        return mapToResponse(savedTicket);
    }

    @Override
    public void cancelTicket(Integer userProfileId, Integer ticketId) {
        SeminarTicket ticket = seminarTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        if (!ticket.getUserProfileId().equals(userProfileId)) {
            throw new UnauthorizedException("Not authorized to cancel this ticket");
        }

        seminarTicketRepository.delete(ticket);
    }

    @Override
    public List<SeminarTicketResponse> getTicketsBySeminar(Integer seminarId) {
        return seminarTicketRepository.findBySeminarId(seminarId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeminarTicketResponse> getTicketsByUser(Integer userProfileId) {
        return seminarTicketRepository.findByUserProfileId(userProfileId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SeminarTicketResponse getTicket(Integer ticketId) {
        SeminarTicket ticket = seminarTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return mapToResponse(ticket);
    }

    @Override
    public boolean hasActiveTicket(Integer seminarId, Integer userProfileId) {
        return seminarTicketRepository.existsBySeminarIdAndUserProfileId(seminarId, userProfileId);
    }

    @Override
    public long getBookedTicketsCount(Integer seminarId) {
        return seminarTicketRepository.countBySeminarId(seminarId);
    }

    private SeminarTicketResponse mapToResponse(SeminarTicket ticket) {
        SeminarTicketResponse response = new SeminarTicketResponse();
        response.setId(ticket.getId());
        response.setSeminarId(ticket.getSeminar().getId());
        response.setUserProfileId(ticket.getUserProfileId());
        response.setBookingTime(ticket.getBookingTime());
        return response;
    }
} 