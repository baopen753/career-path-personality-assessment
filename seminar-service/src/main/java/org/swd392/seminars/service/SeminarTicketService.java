package org.swd392.seminars.service;

import org.swd392.seminars.payload.request.SeminarTicketRequest;
import org.swd392.seminars.payload.response.SeminarTicketResponse;

import java.util.List;

public interface SeminarTicketService {
    SeminarTicketResponse bookTicket(SeminarTicketRequest request);
    void cancelTicket(Integer userProfileId, Integer ticketId);
    List<SeminarTicketResponse> getTicketsBySeminar(Integer seminarId);
    List<SeminarTicketResponse> getTicketsByUser(Integer userProfileId);
    SeminarTicketResponse getTicket(Integer ticketId);
    boolean hasActiveTicket(Integer seminarId, Integer userProfileId);
    long getBookedTicketsCount(Integer seminarId);
}
