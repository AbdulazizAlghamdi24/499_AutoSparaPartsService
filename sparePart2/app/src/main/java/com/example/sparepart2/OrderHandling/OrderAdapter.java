package com.example.sparepart2.OrderHandling;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparepart2.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;
    private OnItemClickListener onItemClickListener;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView carTypeTextView;
        private TextView sparePartTextView;
        private TextView priceRangeTextView;
        private TextView orderTimeTextView;
        private TextView orderStatusTextView;
        private TextView userPhoneNumberTextView;

        private TextView carYearTextView;
        private TextView carModelTextView;

        public OrderViewHolder(View itemView) {
            super(itemView);
            TextView orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            carTypeTextView = itemView.findViewById(R.id.carTypeTextView);
            carYearTextView = itemView.findViewById(R.id.carYearTextView);
            sparePartTextView = itemView.findViewById(R.id.sparePartTextView);
            priceRangeTextView = itemView.findViewById(R.id.priceRangeTextView);
            orderTimeTextView = itemView.findViewById(R.id.orderTimeTextView);
            orderStatusTextView = itemView.findViewById(R.id.orderStatusTextView);
            userPhoneNumberTextView = itemView.findViewById(R.id.userPhoneNumberTextView);
            carModelTextView = itemView.findViewById(R.id.carModelTextView);


            itemView.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void bind(Order order) {

            carTypeTextView.setText("Car Type:  "+order.getCarType());
            carModelTextView.setText("Car Model:  "+order.getCarModel());
            carYearTextView.setText("Car Year:  "+order.getCarYear());
            sparePartTextView.setText("Wanted Spare Part:  "+order.getSparePart());
            priceRangeTextView.setText("Price Range:  "+order.getPriceRange());
            orderTimeTextView.setText("Order Date:  "+order.getOrderTime());
            orderStatusTextView.setText("Order Status:  "+order.getOrderStatus());
            userPhoneNumberTextView.setText("User Phone Number:  "+order.getUserPhoneNumber());

        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener.onItemClick(orderList.get(position));
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Order order);
    }
}
