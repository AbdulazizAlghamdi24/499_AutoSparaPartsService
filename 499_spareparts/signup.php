<?php
// Include the necessary files and establish a database connection
require_once 'config.php';

// Retrieve the request data
$data = json_decode(file_get_contents("php://input"), true);

// Perform data validation and sanitization

// Check if the required fields are valid
if (isset($data['username']) && isset($data['email']) && isset($data['password']) && isset($data['phone_number'])) {
    $username = $data['username'];
    $email = $data['email'];
    $password = $data['password'];
    $phone_number = $data['phone_number'];
} else {
    // Invalid request, missing required fields
    $response = [
        'message' => 'Invalid request. Required fields are missing.'
    ];

    // Set the appropriate headers and encode the response data as JSON
    header('Content-Type: application/json');
    echo json_encode($response);
    exit;
}

// Check if the email is valid
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    $response = [
        'message' => 'Invalid email format.'
    ];

    // Set the appropriate headers and encode the response data as JSON
    header('Content-Type: application/json');
    echo json_encode($response);
    exit;
}

$stmt = $pdo->prepare("SELECT * FROM users WHERE email = ?");

$stmt->execute([$data['email']]);
$user = $stmt->fetch();

if ($user) {
    // User already exists
    $response = [
        'message' => 'User with this email already exists.'
    ];
} else {
    // Insert the new user into the database
    $stmt = $pdo->prepare("INSERT INTO users (username, email, password, phone_number) VALUES (?, ?, ?, ?)");
    $stmt->execute([$data['username'], $data['email'], password_hash($data['password'], PASSWORD_DEFAULT), $data['phone_number']]);

    // Get last inserted ID
    $last_id = $pdo->lastInsertId();

    // Query the inserted user data
    $stmt = $pdo->prepare("SELECT * FROM users WHERE id = ?");
    $stmt->execute([$last_id]);
    $user = $stmt->fetch();

    if ($user) {
        // User registration successful
        $response = [
            'message' => 'User registration successful.',
            'user' => [
                'id' => $user['id'],
                'username' => $user['username'],
                'email' => $user['email'],
                'phone_number' => $user['phone_number']
            ]
        ];
    } else {
        $response = [
            'message' => 'An error occurred while fetching the registered user data.'
        ];
    }
}

// Set the appropriate headers and encode the response data as JSON
header('Content-Type: application/json');
echo json_encode($response);
?>