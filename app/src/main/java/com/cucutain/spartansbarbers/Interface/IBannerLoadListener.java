package com.cucutain.spartansbarbers.Interface;

import com.cucutain.spartansbarbers.Models.Banner;

import java.util.List;

public interface IBannerLoadListener {
    void onBannerLoadSuccess(List<Banner> banners);
    void onBannerLoadFailed(String message);
}
