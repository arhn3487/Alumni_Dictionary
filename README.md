<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MIST Alumni Portal README</title>
    <style>
        body {
            font-family: 'Arial', sans-serif;
            margin: 0;
            padding: 20px;
            background: linear-gradient(to bottom right, #6a11cb, #2575fc);
            color: white;
        }
        h1, h2, h3 {
            text-align: center;
            color: #FFD700;
        }
        .section {
            background: rgba(0, 0, 0, 0.6);
            padding: 20px;
            margin: 20px 0;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.2);
        }
        .section-title {
            background: linear-gradient(to left, #ff512f, #dd2476);
            color: white;
            padding: 12px;
            border-radius: 8px;
            text-align: center;
            font-size: 24px;
        }
        ul {
            padding-left: 20px;
        }
        li {
            font-size: 18px;
            margin-bottom: 10px;
            padding: 10px;
            background: linear-gradient(to right, #FF8C00, #FF6347);
            border-radius: 5px;
            box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
            transition: transform 0.3s ease;
        }
        li:hover {
            transform: scale(1.05);
        }
        code {
            background-color: #333;
            color: #f8f8f2;
            padding: 5px 10px;
            border-radius: 5px;
            font-family: 'Courier New', Courier, monospace;
        }
        .highlight {
            color: #FF4500;
            font-weight: bold;
        }
        .contributors a {
            color: #FFD700;
            text-decoration: none;
            font-weight: bold;
        }
        .contributors a:hover {
            text-decoration: underline;
        }
        .footer {
            text-align: center;
            font-size: 14px;
            color: #f1f1f1;
            margin-top: 30px;
            padding-top: 10px;
            background-color: #333;
        }
    </style>
</head>
<body>

    <h1>MIST Alumni Portal</h1>
    <h2 class="section-title">🌟 Overview</h2>
    <div class="section">
        <p>The MIST Alumni Portal is a comprehensive platform designed to manage alumni information, provide digital ID cards, and facilitate job postings and events.</p>
    </div>

    <h2 class="section-title">👥 Features by User Role</h2>

    <div class="section">
        <h3>🧑‍🎓 Student</h3>
        <ul>
            <li>✅ Dashboard</li>
            <li>✅ ID Card (Downloadable as PNG)</li>
            <li>✅ Event (View-only in table format)</li>
            <li>✅ Job Board (View-only in table format)</li>
            <li>✅ Alumni List</li>
            <li>✅ Access to the MIST website</li>
        </ul>
    </div>

    <div class="section">
        <h3>🎓 Alumni</h3>
        <ul>
            <li>✅ Dashboard</li>
            <li>✅ ID Card</li>
            <li>✅ Event (View-only)</li>
            <li>✅ Job Board (Can post jobs with circular link, company, salary, posted date, and description)</li>
            <li>✅ Alumni List (View-only)</li>
            <li>✅ Access to the MIST website</li>
        </ul>
    </div>

    <div class="section">
        <h3>🛠️ Admin</h3>
        <ul>
            <li>✅ Dashboard</li>
            <li>✅ Event (Can post events)</li>
            <li>✅ Job Board (Can post jobs with circular link, company, salary, posted date, and description)</li>
            <li>✅ Alumni List (Sortable by batch & department, displaying email, name, student ID, workplace, graduation year)</li>
            <li>✅ Create ID option</li>
            <li>✅ Broadcast Feature (Ability to send emails to a particular batch and session)</li>
            <li>✅ Access to the MIST website</li>
        </ul>
    </div>

    <h2 class="section-title">🔐 Security & Authentication</h2>
    <div class="section">
        <p>Password security is crucial for this platform. All passwords are stored in <span class="highlight">SHA-256</span> format with salt. If users forget their passwords, an OTP will be sent to their email for resetting the password.</p>
    </div>

    <h2 class="section-title">💻 Technology Stack</h2>
    <div class="section">
        <h3>Frontend</h3>
        <p>ReactJS, TailwindCSS</p>
        <h3>Backend</h3>
        <p>Node.js, Express.js, MongoDB</p>
        <h3>Authentication</h3>
        <p>JWT-based authentication system</p>
    </div>

    <h2 class="section-title">🚀 Setup & Installation</h2>
    <div class="section">
        <h3>To Get Started:</h3>
        <ul>
            <li>Clone the repository:  
                <code>git clone &lt;repo-url&gt;</code></li>
            <li>Install dependencies:  
                <code>npm install</code></li>
            <li>Start the development server:  
                <code>npm start</code></li>
        </ul>
    </div>

    <h2 class="section-title">👨‍💻 Contributors</h2>
    <div class="section contributors">
        <p><strong>Arafat Hasan</strong> - <a href="https://github.com/arhn3487" target="_blank">GitHub</a></p>
        <p><strong>Sakib</strong> - <a href="https://github.com/sakib-sm-shakib" target="_blank">GitHub</a></p>
        <p><strong>Isbat</strong> - <a href="https://github.com/ihsamin01" target="_blank">GitHub</a></p>
    </div>

    <div class="footer">
        <p>Developed with ❤️ by MIST Alumni Portal Team</p>
    </div>

</body>
</html>
