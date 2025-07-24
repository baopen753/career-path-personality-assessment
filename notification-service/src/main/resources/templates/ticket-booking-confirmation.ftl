<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ticket Booking Confirmation</title>
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
            background-color: #4CAF50;
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
        .booking-details {
            background-color: white;
            border: 2px solid #4CAF50;
            border-radius: 5px;
            padding: 15px;
            margin: 15px 0;
        }
        .payment-info {
            background-color: #e8f5e8;
            border: 1px solid #4CAF50;
            border-radius: 5px;
            padding: 15px;
            margin: 15px 0;
        }
        .status-completed {
            color: #4CAF50;
            font-weight: bold;
            font-size: 18px;
        }
        .status-pending {
            color: #FF9800;
            font-weight: bold;
            font-size: 18px;
        }
        .status-failed {
            color: #f44336;
            font-weight: bold;
            font-size: 18px;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>🎫 Ticket Booking Confirmation</h1>
    </div>
    <div class="content">
        <p>Dear ${fullName},</p>
        
        <p>Thank you for booking a ticket! Your transaction has been processed successfully.</p>
        
        <div class="booking-details">
            <h3>Booking Details:</h3>
            <ul>
                <li><strong>Customer Name:</strong> ${fullName}</li>
                <li><strong>Email:</strong> ${email}</li>
                <li><strong>Payment Order Code:</strong> ${paymentOrderCode}</li>
                <li><strong>Transaction Status:</strong> <span class="status-${status?lower_case}">${status}</span></li>
                <li><strong>Booking Date:</strong> ${createdAt}</li>
            </ul>
        </div>
        
        <div class="payment-info">
            <h3>Payment Information:</h3>
            <ul>
                <li><strong>Order Code:</strong> ${paymentOrderCode}</li>
                <li><strong>Status:</strong> <span class="status-${status?lower_case}">${status}</span></li>
                <li><strong>Transaction Date:</strong> ${createdAt}</li>
                <#if amount?has_content>
                <li><strong>Amount:</strong> <span style="color: #4CAF50; font-weight: bold; font-size: 16px;">${amount} VND</span></li>
                </#if>
                <#if paymentMethod?has_content>
                <li><strong>Payment Method:</strong> ${paymentMethod}</li>
                </#if>
            </ul>
        </div>
        
        <p><strong>What's Next?</strong></p>
        <ul>
            <li>You will receive a separate email with your seminar ticket details</li>
            <li>Please check your email for the seminar schedule and location</li>
            <li>Arrive 15 minutes before the seminar starts</li>
            <li>Bring a valid ID for verification</li>
        </ul>
        
        <p><strong>Important Notes:</strong></p>
        <ul>
            <li>Keep this confirmation email for your records</li>
            <li>If you have any questions, please contact our support team</li>
            <li>Payment is non-refundable unless the seminar is cancelled</li>
        </ul>
        
        <p>We look forward to seeing you at the seminar!</p>
        
        <p>Best regards,<br>
        Career Path Team</p>
    </div>
</body>
</html> 