package com.example.l_tech.Repozitory;

import com.example.l_tech.Model.Banner;
import com.example.l_tech.R;

import java.util.ArrayList;
import java.util.List;

public class BannerRepozitory {

    public static List<Banner> getBanners() {
        List<Banner> banners = new ArrayList<>();

        // Пример заглушек с изображениями (здесь нужно использовать ваши ресурсы)
        banners.add(new Banner(R.drawable.media));
        banners.add(new Banner(R.drawable.__2_));
        banners.add(new Banner(R.drawable.media));

        return banners;
    }
}
