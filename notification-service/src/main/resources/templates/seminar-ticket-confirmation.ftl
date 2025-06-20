<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ticket Confirmation</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
        }
        .header {
            background-color: #2196F3;
            color: white;
            padding: 20px;
            text-align: center;
            border-radius: 5px 5px 0 0;
        }
        .content {
            padding: 20px;
            background-color: #f9f9f9;
            border: 1px solid #ddd;
            border-radius: 0 0 5px 5px;
        }
        .ticket-details {
            background-color: white;
            border: 2px solid #2196F3;
            border-radius: 5px;
            padding: 15px;
            margin: 15px 0;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>🎟️ Ticket Confirmation</h1>
    </div>
    <div class="content">
        <p>Dear ${attendeeName},</p>
        
        <p>Your ticket for "<strong>${seminarName}</strong>" has been confirmed!</p>
        
        <div class="ticket-details">
            <h3>Ticket Details:</h3>
            <ul>
                <li><strong>Ticket ID:</strong> ${ticketId}</li>
                <li><strong>Seminar:</strong> ${seminarName}</li>
                <li><strong>Date:</strong> ${seminarDate}</li>
                <li><strong>Location:</strong> ${seminarLocation}</li>
                <li><strong>Attendee Type:</strong> ${userRole}</li>
                <#if ticketLink?has_content>
                <li><strong>Meeting Link:</strong> <a href="${ticketLink}">Join Here</a></li>
                </#if>
            </ul>
        </div>
        
        <p><strong>Important Notes:</strong></p>
        <ul>
            <li>Please arrive 15 minutes early</li>
            <li>Bring a valid ID for verification</li>
            <li>Check your email for any updates</li>
        </ul>
        
        <p>We look forward to seeing you at the seminar!</p>
        
        <p>Best regards,<br>
        Career Path Team</p>
    </div>
</body>
</html>
