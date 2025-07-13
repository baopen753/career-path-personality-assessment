<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Seminar Status Update</title>
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
        .approved { color: #4CAF50; font-weight: bold; }
        .rejected { color: #f44336; font-weight: bold; }
    </style>
</head>
<body>
    <div class="header">
        <h1>Seminar Status Update</h1>
    </div>
    <div class="content">
        <p>Dear ${eventManagerName},</p>
        
        <p>Your seminar "<strong>${seminarName}</strong>" has been <span class="${status?lower_case}">${status}</span>.</p>
        
        <div class="seminar-details">
            <h3>Seminar Details:</h3>
            <ul>
                <li><strong>Name:</strong> ${seminarName}</li>
                <li><strong>Status:</strong> ${status}</li>
                <li><strong>Start Time:</strong> ${startingTime}</li>
                <li><strong>End Time:</strong> ${endingTime}</li>
                <li><strong>Location:</strong> ${seminarLocation}</li>
                <#if seminarLink?has_content>
                <li><strong>Meeting Link:</strong> <a href="${seminarLink}">${seminarLink}</a></li>
                </#if>
            </ul>
        </div>
        
        <#if rejectionReason?has_content>
        <div class="rejection-reason">
            <h3>Rejection Reason:</h3>
            <p>${rejectionReason}</p>
        </div>
        </#if>
        
        <p>Best regards,<br>
        Career Path Team</p>
    </div>
</body>
</html>
