<?php
require_once 'config.php';

// Retrieve the request data
$data = json_decode(file_get_contents("php://input"), true);

// Retrieve the page number from the request data
$page = $data['page'] ?? 1;

// Calculate the offset based on the page number
$offset = ($page - 1) * 9;

// Construct the SQL query with pagination and join with users table
$sql = "SELECT o.id, o.user_id, o.type, o.spare_part, o.price_range, o.created_at, o.status, u.phone_number
        FROM orders AS o
        JOIN users AS u ON o.user_id = u.id
        WHERE o.status = 'active'
        LIMIT 9 OFFSET $offset";

// Execute the SQL query
$stmt = $pdo->query($sql);
$ongoingOrders = $stmt->fetchAll(PDO::FETCH_ASSOC);

// Get the total number of ongoing orders
$totalOrders = $pdo->query("SELECT COUNT(*) FROM orders WHERE status = 'active'")->fetchColumn();

// Check if any ongoing orders were found
if (!empty($ongoingOrders)) {
    // Ongoing orders found
    // Construct the response data
    $response = [
        'page' => $page,
        'total_orders' => $totalOrders,
        'ongoing_orders' => $ongoingOrders
    ];
} else {
    // No ongoing orders found
    $response = [
        'message' => 'No ongoing orders found.'
    ];
}

// Set the appropriate headers and encode the response data as JSON
header('Content-Type: application/json');
echo json_encode($response);
?>
