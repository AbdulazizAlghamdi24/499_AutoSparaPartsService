package com.example.sparepart2.OrderHandling;

public class Order {
    private String orderId;
    private String CarType;
    private String  SparePart;
    private String PriceRange;
    private String orderTime;
    private String orderStatus;
    private String userPhoneNumber;
    private  String CarYear;

    private String CarModel;

    private String ExtraDetails;

    public Order(String orderId,String CarType, String CarModel, String CarYear, String SparePart,String ExtraDetails,String PriceRange,String orderTime, String orderStatus, String userPhoneNumber) {
        this.orderId = orderId;
        this.CarType = CarType;
        this.SparePart = SparePart;
        this.PriceRange = PriceRange;
        this.orderTime =orderTime;
        this.orderStatus = orderStatus;
         this.userPhoneNumber=userPhoneNumber;
         this.CarYear = CarYear;
         this.CarModel = CarModel;
         this.ExtraDetails = ExtraDetails;
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

    public String getCarYear(){return  CarYear;}

    public String getCarModel(){
        return CarModel;
    }

    public String getExtraDetails(){ return  ExtraDetails;}
}
