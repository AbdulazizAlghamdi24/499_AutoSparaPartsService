<?php
// Include the necessary files and establish a database connection
require_once 'config.php';

// Retrieve the request data
$data = json_decode(file_get_contents("php://input"), true);

// Perform data validation and sanitization
if (isset($data['email']) && isset($data['password'])) {
    $email = $data['email'];
    $password = $data['password'];
} else {
    // Invalid request, email or password is missing or not set
    $response = [
        'message' => 'Invalid request. Email or password is missing or not set.'
    ];

    // Set the appropriate headers and encode the response data as JSON
    header('Content-Type: application/json');
    echo json_encode($response);
    exit;
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    $response = [
        'message' => 'Invalid email format.'
    ];
    header('Content-Type: application/json');
    echo json_encode($response);
    exit;
}

// Check if the user exists in the database
$stmt = $pdo->prepare("SELECT * FROM users WHERE email = ?");
if (!$stmt) {
    $response = [
        'message' => 'Database query error.'
    ];
    header('Content-Type: application/json');
    echo json_encode($response);
    exit;
}

// Check if the user exists in the database
$stmt->execute([$data['email']]);
$user = $stmt->fetch();

if ($user && password_verify($data['password'], $user['password'])) {
    // User authentication successful
    $response = [
        'message' => 'Login successful.',
        'user' => [
            'id' => $user['id'],
            'email' => $user['email'],
            'username' => $user['username'],
            'phone_number' => $user['phone_number']
        ]
    ];
} else {
    // User authentication failed
    $response = [
        'message' => 'Invalid email or password.'
    ];
}

// Set the appropriate headers and encode the response data as JSON
header('Content-Type: application/json');
echo json_encode($response);
?>