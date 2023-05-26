<?php

// Check if the request method is not POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    // Invalid request method
    $response = [
        'error' => 'Invalid request method (405). Only POST requests are allowed.'
    ];

    // Set the appropriate headers and encode the response data as JSON
    header('Content-Type: application/json');
    echo json_encode($response);

    // Terminate the script execution
    die();
}

$host = 'localhost';
$dbName = 'spare_parts_app';
$username = 'root';
$password = '';

try {
    $pdo = new PDO("mysql:host=$host;dbname=$dbName", $username, $password);
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    // Check the connection status
    $connectionStatus = $pdo->getAttribute(PDO::ATTR_CONNECTION_STATUS);

    if ($connectionStatus === false) {
        // Connection failed
        echo json_encode("Database connection failed.");
    } else {
        // Connection successful
        echo json_encode("Database connection successful.");
    }
} catch (PDOException $e) {
    // Connection error
    echo json_encode("Database connection error: " . $e->getMessage());
}


?>
