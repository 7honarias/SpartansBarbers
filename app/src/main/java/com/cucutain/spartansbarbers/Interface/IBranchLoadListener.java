package com.cucutain.spartansbarbers.Interface;

import com.cucutain.spartansbarbers.Models.Salon;

import java.util.List;

public interface IBranchLoadListener {
    void onBranchLoadSuccess(List<Salon> salonList);
    void onBranchLoadFailed(String message);
}
