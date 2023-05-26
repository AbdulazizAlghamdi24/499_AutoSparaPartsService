package com.example.sparepart2.OrderHandling;


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
        private TextView orderIdTextView;
        private TextView carTypeTextView;
        private TextView sparePartTextView;
        private TextView priceRangeTextView;
        private TextView orderTimeTextView;
        private TextView orderStatusTextView;
        private TextView userPhoneNumberTextView;

        public OrderViewHolder(View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
            carTypeTextView = itemView.findViewById(R.id.carTypeTextView);
            sparePartTextView = itemView.findViewById(R.id.sparePartTextView);
            priceRangeTextView = itemView.findViewById(R.id.priceRangeTextView);
            orderTimeTextView = itemView.findViewById(R.id.orderTimeTextView);
            orderStatusTextView = itemView.findViewById(R.id.orderStatusTextView);
            userPhoneNumberTextView = itemView.findViewById(R.id.userPhoneNumberTextView);


            itemView.setOnClickListener(this);
        }

        public void bind(Order order) {
            orderIdTextView.setText(order.getOrderId());
            carTypeTextView.setText(order.getCarType());
            sparePartTextView.setText(order.getSparePart());
            priceRangeTextView.setText(order.getPriceRange());
            orderTimeTextView.setText(order.getOrderTime());
            orderStatusTextView.setText(order.getOrderStatus());
            userPhoneNumberTextView.setText(order.getUserPhoneNumber());

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
