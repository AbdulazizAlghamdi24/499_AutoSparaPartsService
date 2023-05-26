package com.example.sparepart2.OrderHandling;

public class Order {
    private String orderId;
    private String CarType;
    private String  SparePart;
    private String PriceRange;
    private String orderTime;
    private String orderStatus;
    private String userPhoneNumber;

    public Order(String orderId,String CarType, String SparePart,String PriceRange,String orderTime, String orderStatus, String userPhoneNumber) {
        this.orderId = orderId;
        this.CarType = CarType;
        this.SparePart = SparePart;
        this.PriceRange = PriceRange;
        this.orderTime =orderTime;
        this.orderStatus = orderStatus;
         this.userPhoneNumber=userPhoneNumber;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCarType() {
        return CarType;
    }

    public String getSparePart() {
        return SparePart;
    }

    public String getPriceRange() {
        return PriceRange;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getUserPhoneNumber(){return  userPhoneNumber;}
}
