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
        .seminar-details {
            background-color: white;
            border: 2px solid #4CAF50;
            border-radius: 5px;
            padding: 15px;
            margin: 15px 0;
        }
        .status-approved {
            color: #4CAF50;
            font-weight: bold;
            font-size: 18px;
        }
        .status-rejected {
            color: #f44336;
            font-weight: bold;
            font-size: 18px;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>Seminar Status Update</h1>
    </div>
    <div class="content">
        <p>Dear ${managerFullName},</p>
        <#if statusApprove == "APPROVED">
            <p>Your seminar <strong>"${seminarTitle}"</strong> has been <span class="status-approved">APPROVED</span> by the admin.</p>
        <#else>
            <p>Your seminar <strong>"${seminarTitle}"</strong> has been <span class="status-rejected">REJECTED</span> by the admin.</p>
        </#if>
        <div class="seminar-details">
            <h3>Seminar Details:</h3>
            <ul>
                <li><strong>Seminar Title:</strong> ${seminarTitle}</li>
                <li><strong>Status:</strong> <span class="status-approved"><#if statusApprove == "APPROVED">${statusApprove}<#else><span class="status-rejected">${statusApprove}</span></#if></span></li>
                <li><strong>Approval Time:</strong> ${approvedAt}</li>
            </ul>
        </div>
        <p>Thank you for your contribution to the Career Path System.</p>
        <p>Best regards,<br>Career Path System Team</p>
    </div>
</body>
</html>
