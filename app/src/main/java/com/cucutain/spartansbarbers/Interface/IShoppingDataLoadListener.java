package com.cucutain.spartansbarbers.Interface;

import com.cucutain.spartansbarbers.Models.ShoppingItem;

import java.util.List;

public interface IShoppingDataLoadListener {
    void onShoppingDataLoadSuccess(List<ShoppingItem> shoppingItemList);
    void onShoppingDataLoadFailed(String message);

}
