<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Welcome to Career Path System</title>
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
            background-color: #FF9800;
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
        .account-details {
            background-color: white;
            border: 2px solid #FF9800;
            border-radius: 5px;
            padding: 15px;
            margin: 15px 0;
        }
        .cta-button {
            background-color: #FF9800;
            color: white;
            padding: 12px 25px;
            text-decoration: none;
            border-radius: 5px;
            display: inline-block;
            margin: 15px 0;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>🎓 Welcome to Career Path System!</h1>
    </div>
    <div class="content">
        <p>Dear ${username},</p>
        
        <p>Congratulations! Your account has been successfully created on our Career Path System.</p>
        
        <div class="account-details">
            <h3>Account Details:</h3>
            <ul>
                <li><strong>Username:</strong> ${username}</li>
                <li><strong>Account Type:</strong> ${accountType}</li>
                <li><strong>Registration Date:</strong> ${registrationDate}</li>
            </ul>
        </div>
        
        <p>You can now access our platform and explore various features:</p>
        <ul>
            <li>Browse career guidance seminars</li>
            <li>Take career assessment quizzes</li>
            <li>Book tickets for events</li>
            <li>Connect with mentors and peers</li>
        </ul>
        
        <a href="${loginLink}" class="cta-button">Login to Your Account</a>
        
        <p><strong>Getting Started:</strong></p>
        <ol>
            <li>Complete your profile information</li>
            <li>Explore available seminars</li>
            <li>Book your first seminar ticket</li>
            <li>Start your career journey!</li>
        </ol>
        
        <p>If you have any questions, feel free to contact our support team.</p>
        
        <p>Welcome aboard and best of luck with your career journey!</p>
        
        <p>Best regards,<br>
        Career Path Team</p>
    </div>
</body>
</html>
