package org.swd392.seminars.service;

import org.swd392.seminars.payload.request.SeminarTicketRequest;
import org.swd392.seminars.payload.response.SeminarTicketResponse;

import java.util.List;

public interface SeminarTicketService {
    SeminarTicketResponse bookTicket(SeminarTicketRequest request);
    void cancelTicket(Integer userId, Integer ticketId);
    List<SeminarTicketResponse> getTicketsBySeminar(Integer seminarId);
    List<SeminarTicketResponse> getTicketsByUser(Integer userId);
    SeminarTicketResponse getTicket(Integer ticketId);
    boolean hasActiveTicket(Integer seminarId, Integer userId);
    long getBookedTicketsCount(Integer seminarId);
}
