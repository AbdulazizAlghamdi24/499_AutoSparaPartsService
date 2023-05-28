<?php

// Include the necessary files and establish a database connection
require_once 'config.php';

// Retrieve the JSON data from the request body
$data = json_decode(file_get_contents('php://input'), true);


if (isset($data['user_id'])) {

    $user_id = $data['user_id'];

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


// Retrieve the user ID from the JSON data
$user_id = $data['user_id'];

// Perform data validation and sanitization if needed

// Query the database to retrieve the order information for the user
$stmt = $pdo->prepare("SELECT * FROM orders WHERE user_id = ?");
$stmt->execute([$user_id]);
$orders = $stmt->fetchAll(PDO::FETCH_ASSOC);

// Check if any orders were found
if (empty($orders)) {
    // No orders found for the user
    $response = [
        'message' => 'No orders found for the user.',
        'data' => []
    ];
} else {
    // Orders found, include them in the response
    $response = [
        'message' => 'Orders retrieved successfully.',
        'data' => $orders
    ];
}

// Set the appropriate headers and encode the response data as JSON
header('Content-Type: application/json');
echo json_encode($response);

?>