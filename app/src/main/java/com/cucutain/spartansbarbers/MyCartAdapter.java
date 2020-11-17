package com.cucutain.spartansbarbers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cucutain.spartansbarbers.Database.CartDatabase;
import com.cucutain.spartansbarbers.Database.CartItem;
import com.cucutain.spartansbarbers.Database.DatabaseUtils;
import com.cucutain.spartansbarbers.Interface.ICartItemUpdateListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {


    Context context;
    List<CartItem> cartItemList;
    CartDatabase cartDatabase;
    ICartItemUpdateListener iCartItemUpdateListener;

    public MyCartAdapter(Context context, List<CartItem> cartItemList, ICartItemUpdateListener iCartItemUpdateListener) {
        this.context = context;
        this.cartItemList = cartItemList;
        this.iCartItemUpdateListener = iCartItemUpdateListener;
        this.cartDatabase = CartDatabase.getInstance(context);
    }


    interface IImageButtonListener{
        void onImageButtonClick(View view, int pos, boolean isDecrease);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(
                R.layout.layout_cart_item,parent,false
        );
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Picasso.get().load(cartItemList.get(i).getProductImage()).into(holder.img_product);
        holder.txt_cart_name.setText(new StringBuilder(cartItemList.get(i).getProductName()));
        holder.txt_cart_price.setText(new StringBuilder("$ ").append(cartItemList.get(i).getProductPrice()));
        holder.txt_quantity.setText(new StringBuilder(String.valueOf(cartItemList.get(i).getProductQuantity())));


        holder.setListener(new IImageButtonListener() {
            @Override
            public void onImageButtonClick(View view, int pos, boolean isDecrease) {
                if (isDecrease)
                {
                    if (cartItemList.get(pos).getProductQuantity() >0)
                    {
                        cartItemList.get(pos)
                                .setProductQuantity(cartItemList.get(pos).getProductQuantity()-1);

                        DatabaseUtils.updateCart(cartDatabase,cartItemList.get(pos));
                    }

                }else
                {
                    if (cartItemList.get(pos).getProductQuantity() <99)
                    {
                        cartItemList.get(pos)
                                .setProductQuantity(cartItemList.get(pos).getProductQuantity()+1);

                        DatabaseUtils.updateCart(cartDatabase,cartItemList.get(pos));
                    }
                }

                holder.txt_quantity.setText(new StringBuilder(String.valueOf(cartItemList.get(pos).getProductQuantity())));
                iCartItemUpdateListener.onCartItemUpdateSuccess();


            }
        });

    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_cart_name, txt_cart_price, txt_quantity;
        ImageView img_decrease, img_increase, img_product;

        IImageButtonListener listener;

        public void setListener(IImageButtonListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_cart_name = itemView.findViewById(R.id.txt_cart_name);
            txt_cart_price = itemView.findViewById(R.id.txt_cart_price);
            txt_quantity = itemView.findViewById(R.id.txt_cart_quantity);
            img_decrease = itemView.findViewById(R.id.img_decraese);
            img_increase = itemView.findViewById(R.id.img_increase);
            img_product = itemView.findViewById(R.id.cart_img);


            img_decrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onImageButtonClick(view,getAdapterPosition(),true);



                }
            });

            img_increase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onImageButtonClick(view,getAdapterPosition(),false);
                }
            });
        }
    }
}
