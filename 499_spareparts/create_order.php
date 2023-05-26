<?php
require_once 'config.php';

$data = json_decode(file_get_contents("php://input"), true);

// Check if all the required fields are present
if (
    isset($data['user_id']) &&
    isset($data['car_manufacture']) &&
    isset($data['type']) &&
    isset($data['model']) &&
    isset($data['year']) &&
    isset($data['spare_part']) &&
    isset($data['extra_details']) &&
    isset($data['price_range'])
) {
    

    // Retrieve the order data sent from the frontend
    $userID = $data['user_id'];
    $carManufacture = $data['car_manufacture'];
    $type = $data['type'];
    $model = $data['model'];
    $year = $data['year'];
    $sparePart = $data['spare_part'];
    $extraDetails = $data['extra_details'];
    $priceRange = $data['price_range'];

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


    // Sanitize the order data
    $userID = filter_var($userID, FILTER_SANITIZE_NUMBER_INT);
    $carManufacture = filter_var($carManufacture, FILTER_SANITIZE_STRING);
    $type = filter_var($type, FILTER_SANITIZE_STRING);
    $model = filter_var($model, FILTER_SANITIZE_STRING);
    $year = filter_var($year, FILTER_SANITIZE_NUMBER_INT);
    $sparePart = filter_var($sparePart, FILTER_SANITIZE_STRING);
    $extraDetails = filter_var($extraDetails, FILTER_SANITIZE_STRING);
    $priceRange = filter_var($priceRange, FILTER_SANITIZE_STRING);

    // Construct the SQL query
    $sql = "INSERT INTO orders (user_id, car_manufacture, type, model, year, spare_part, extra_details, price_range)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    // Prepare the SQL statement
    $stmt = $pdo->prepare($sql);

    // Bind the order data values to the query parameters
    $stmt->execute([$userID, $carManufacture, $type, $model, $year, $sparePart, $extraDetails, $priceRange]);

    // Check if the order creation was successful
    if ($stmt->rowCount() > 0) {
        // Order creation successful
        $response = [
            'message' => 'Order created successfully.'
        ];
    } else {
        // Order creation failed
        $response = [
            'message' => 'Failed to create the order.'
        ];
    }

// Set the appropriate headers and encode the response data as JSON
header('Content-Type: application/json');
echo json_encode($response);
?>
