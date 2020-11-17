package com.cucutain.spartansbarbers.Interface;

import com.cucutain.spartansbarbers.Database.CartItem;

import java.util.List;

public interface ICartItemLoadListener {
    void onGetAllItemFromCartSuccess(List<CartItem> cartItemList);

}
