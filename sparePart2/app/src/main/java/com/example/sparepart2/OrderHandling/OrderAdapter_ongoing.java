package com.example.sparepart2.OrderHandling;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sparepart2.R;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter_ongoing extends RecyclerView.Adapter<OrderAdapter_ongoing.OrderViewHolder> {

    private List<Order> orderList;
    private OnItemClickListener onItemClickListener;
    private List<Order> fullOrderList;

    public OrderAdapter_ongoing(List<Order> orderList) {
        this.orderList = orderList;
        this.fullOrderList = new ArrayList<>(orderList);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_ongoing, parent, false);
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

    public void addOrders(List<Order> newOrders) {
        int startPos = orderList.size();
        orderList.addAll(newOrders);
        fullOrderList.addAll(newOrders);
        notifyItemRangeInserted(startPos, newOrders.size());
    }
    public void updateFullOrders(List<Order> newOrders) {
        this.fullOrderList.clear();
        this.fullOrderList.addAll(newOrders);
    }

    public Filter getFilter() {
        return orderFilter;
    }
    private Filter orderFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Order> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullOrderList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Order order : fullOrderList) {
                    if (order.getSparePart().toLowerCase().contains(filterPattern)) {
                        filteredList.add(order);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            orderList.clear();
            orderList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
    public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView carTypeTextView;
        private TextView sparePartTextView;
        private TextView priceRangeTextView;
        private TextView orderTimeTextView;
        private TextView orderStatusTextView;
        private TextView userPhoneNumberTextView;

        private TextView carYearTextView;
        private TextView carModelTextView;
        private TextView extra_detailsTextView;
        private TextView orderIdTextView;

        public OrderViewHolder(View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.orderIdTextView);
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

            orderIdTextView.setText(order.getOrderId());
            carTypeTextView.setText("Car Type:  "+order.getCarType());
            carModelTextView.setText("Car Model:  "+order.getCarModel());
            carYearTextView.setText("Car Year:  "+order.getCarYear());
            sparePartTextView.setText("Wanted Spare Part:  "+order.getSparePart());
            priceRangeTextView.setText("Price Range:  "+order.getPriceRange());
            orderTimeTextView.setText("Order Date:  "+order.getOrderTime());
            orderStatusTextView.setText("Order Status:  "+order.getOrderStatus());
            userPhoneNumberTextView.setText("User Phone Number:  "+order.getUserPhoneNumber());

            if (order.getOrderStatus().equalsIgnoreCase("canceled")) {
                orderStatusTextView.setTextColor(Color.RED);
            } else if (order.getOrderStatus().equalsIgnoreCase("active")) {
                orderStatusTextView.setTextColor(Color.GREEN);
            } else {
                // Default color for other status
                orderStatusTextView.setTextColor(Color.BLACK);
            }
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
